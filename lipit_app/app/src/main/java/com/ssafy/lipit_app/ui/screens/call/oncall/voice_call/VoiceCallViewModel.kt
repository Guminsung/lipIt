package com.ssafy.lipit_app.ui.screens.call.oncall.voice_call

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.ssafy.lipit_app.util.WebSocketHeartbeat
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.net.URI
import java.nio.ByteBuffer
import java.util.ArrayDeque

class VoiceCallViewModel : ViewModel() {
    private val _state = MutableStateFlow(VoiceCallState())
    val state: StateFlow<VoiceCallState> = _state

    // 남은 시간 카운트 관련
    private var timerJob: Job? = null

    fun onIntent(intent: VoiceCallIntent) {
        when (intent) {
            is VoiceCallIntent.UpdateSubtitle -> {
                _state.update {
                    it.copy(AIMessageOriginal = intent.message)
                }
            }
            
            is VoiceCallIntent.SubtitleOn -> { // 자막 O, 번역 X
                _state.update {
                    it.copy(showSubtitle = true, showTranslation = false)
                }
            }

            is VoiceCallIntent.SubtitleOff -> { // 자막 X, 번역 X
                _state.update {
                    it.copy(showSubtitle = false, showTranslation = false)
                }
            }

            is VoiceCallIntent.TranslationOff -> { // 자막 O, 번역 X
                _state.update {
                    it.copy(showSubtitle = true, showTranslation = false)
                }
            }

            is VoiceCallIntent.TranslationOn -> { // 자막 O, 번역 O
                _state.update {
                    it.copy(showSubtitle = true, showTranslation = true)
                }
            }

            // 타이머 종료 후
            is VoiceCallIntent.timerIsOver -> {
                _state.update {
                    it.copy(isLoading = true)
                }

                viewModelScope.launch {
                    delay(2000L) // 리포트 생성 대기 시간
                    _state.update {
                        it.copy(isFinished = true)
                    }
                }
            }

        }
    }

    // ===================================================================

    // websocket 관련 코드

    // 상태 관리 변수들
    var isConnected by mutableStateOf(false)
    var isConnecting by mutableStateOf(false)
    var isWaitingResponse by mutableStateOf(false)
    var connectionStatusText by mutableStateOf("연결되지 않음")
    var isCallEnded by mutableStateOf(false)

    // AI 응답 메시지
    var aiMessage by mutableStateOf("")
    var aiMessageKor by mutableStateOf("")

    // 현재 전화 ID
    var callId: Long? = null

    // WebSocket 인스턴스 및 대기열
    private var ws: WebSocketClient? = null
    private var pendingStartJson: String? = null    // 연결 전 startCall을 저장(대기 중)
    private var pendingText: String? = null         // 연결 전 message를 저장
    private var pendingCallId: Long? = null
    private val mainHandler = Handler(Looper.getMainLooper())

    // 오디오 재생 관련
    private var exoPlayer: ExoPlayer? = null
    private val audioQueue: ArrayDeque<File> = ArrayDeque()

    // 하트비트 관리
    private var heartbeat: WebSocketHeartbeat? = null

    init {
        connectWebSocket()
    }

    // 남은 시간 카운트
    @SuppressLint("DefaultLocale")
    fun startCountdown(initialSeconds: Int = 300) {
        timerJob?.cancel() // 기존에 타이머가 있다면 정지시킴

        timerJob = viewModelScope.launch {
            var remaining = initialSeconds
            while (remaining >= 0) {
                val minutes = remaining / 60
                val seconds = remaining % 60
                val timeString = String.format("%02d:%02d", minutes, seconds)

                _state.update { it.copy(leftTime = timeString) }

                delay(1000L) // 1초 기다리고 text에 반영
                remaining--

                // 5분이 종료되면 로딩 화면 출력(리포트 생성 중.. or 리포트 생성 실패!) 후 Main으로 돌아가기
                if (remaining == 0) {
                    onIntent(VoiceCallIntent.timerIsOver)
                }
            }
        }
    }

    fun stopCountdown() {
        timerJob?.cancel()
    }

    // 웹 소켓 채팅 관련
    /** ExoPlayer 초기화 */
    /** ExoPlayer 초기화 */
    fun initializePlayer(context: Context) {
        if (exoPlayer == null) {
            exoPlayer = ExoPlayer.Builder(context).build()
            exoPlayer?.addListener(object : Player.Listener {
                override fun onPlaybackStateChanged(playbackState: Int) {
                    if (playbackState == Player.STATE_ENDED) {
                        playNextFromQueue()
                    }
                }
            })
        }
    }

    /** ExoPlayer 종료 */
    fun releasePlayer() {
        exoPlayer?.release()
        exoPlayer = null
    }

    /** WebSocket 연결 */
    fun connectWebSocket() {
        if (isConnected || isConnecting) return

        isConnecting = true
        connectionStatusText = "연결 중..."
        Log.d("WebSocket", "🚀 connectWebSocket() 실행됨")

        // WebSocket 서버 주소
        ws = object : WebSocketClient(URI("wss://j12d102.p.ssafy.io/fastapi/ws/android")) {

            /** 연결 성공 시 */
            override fun onOpen(handshakedata: ServerHandshake?) {
                mainHandler.post {
                    isConnected = true
                    isConnecting = false
                    connectionStatusText = "✅ 연결됨"

                    // 하트비트 시작
                    heartbeat = WebSocketHeartbeat(this)
                    heartbeat?.start()

                    pendingStartJson?.let {
                        Log.d("WebSocket", "📤 대기 중이던 startCall 전송")
                        ws?.send(it)
                        pendingStartJson = null
                    }

                    if (pendingText != null && pendingCallId != null) {
                        sendText(pendingText!!)
                        pendingText = null
                    }
                }
            }

            /** 텍스트 메시지 수신 */
            override fun onMessage(message: String?) {
                Log.d("WebSocket", "📨 onMessage() 호출됨: $message")
                message?.let {
                    // 하트비트 응답이면 무시
                    if (it.equals("PONG", ignoreCase = true)) {
                        Log.d("WebSocket", "💓 서버로부터 PONG 수신")
                        return
                    }

                    try {
                        val json = JSONObject(it)
                        val type = json.getString("type")
                        val data = json.getJSONObject("data")

                        when (type) {
                            "text" -> {
                                aiMessage = data.getString("aiMessage")
                                aiMessageKor = data.getString("aiMessageKor")
                                if (data.has("callId")) {
                                    callId = data.getLong("callId")
                                }
                                isWaitingResponse = true
                            }

                            "end" -> {
                                val reportCreated = data.optBoolean("reportCreated", false)
                                Log.d("WebSocket", "🔚 통화 종료 - report=$reportCreated")

                                if (data.has("aiMessage")) {
                                    aiMessage = data.getString("aiMessage")
                                }
                                if (data.has("aiMessageKor")) {
                                    aiMessageKor = data.getString("aiMessageKor")
                                }

                                isWaitingResponse = false
                                isCallEnded = true
                            }

                            else -> {}
                        }
                    } catch (e: Exception) {
                        Log.e("WebSocket", "❌ 메시지 파싱 오류: ${e.message}")
                    }
                }
            }

            /** 바이너리(오디오) 수신 */
            override fun onMessage(bytes: ByteBuffer?) {
                bytes?.let {
                    Log.d("WebSocket", "📥 음성 수신됨 (${bytes.remaining()} bytes)")
                    mainHandler.post {
                        enqueueAndPlay(bytes)
                    }
                }
            }

            /** 연결 종료 */
            override fun onClose(code: Int, reason: String?, remote: Boolean) {
                Log.d("WebSocket", "🔌 onClose: code=$code, reason=$reason")
                mainHandler.post {
                    isConnected = false
                    isWaitingResponse = false
                    connectionStatusText = "❌ 연결 종료 ($code)"

                    // 하트비트 정지
                    heartbeat?.stop()
                    heartbeat = null

                    reconnectWithDelay()
                }
            }

            /** 오류 발생 */
            override fun onError(ex: Exception?) {
                Log.e("WebSocket", "🔥 onError: ${ex?.message}", ex)
                mainHandler.post {
                    isConnected = false
                    isConnecting = false
                    isWaitingResponse = false
                    connectionStatusText = "❌ 오류 발생"

                    // 하트비트 정지
                    heartbeat?.stop()
                    heartbeat = null

                    Log.e("WebSocket", "❌ 오류: ${ex?.message}")
                    reconnectWithDelay()
                }
            }
        }
        ws?.connect()
    }

    /** 연결 재시도 딜레이 */
    private fun reconnectWithDelay(delayMillis: Long = 2000) {
        if (!isConnecting && !isConnected) {
            mainHandler.postDelayed({ connectWebSocket() }, delayMillis)
        }
    }

    /** 수신된 오디오 저장 후 재생 큐에 추가 */
    private fun enqueueAndPlay(buffer: ByteBuffer) {
        val tempFile = File.createTempFile("tts_", ".wav")
        FileOutputStream(tempFile).use { out ->
            val bytes = ByteArray(buffer.remaining())
            buffer.get(bytes)
            out.write(bytes)
        }

        audioQueue.add(tempFile)
        isWaitingResponse = true

        if (exoPlayer?.isPlaying != true && exoPlayer?.playbackState != ExoPlayer.STATE_BUFFERING) {
            playNextFromQueue()
        }
    }

    /** 큐에서 다음 오디오 재생 */
    private fun playNextFromQueue() {
        val next = audioQueue.poll() ?: run {
            isWaitingResponse = false
            return
        }

        try {
            exoPlayer?.setMediaItem(MediaItem.fromUri(Uri.fromFile(next)))
            exoPlayer?.prepare()
            exoPlayer?.play()
        } catch (e: Exception) {
            Log.e("websocket", "❌ exoPlayer 재생 실패: ${e.message}")
        }

    }

    override fun onCleared() {
        super.onCleared()
        releasePlayer()
    }

    /**
     * 사용자 대화 메시지 전송
     * userMessage: 사용자 메시지(STT or 타이핑)
     */
    fun sendText(userMessage: String) {
        Log.d("WebSocket", "📤 sendText 호출됨 (connected=$isConnected)")

        if (callId == null) {
            pendingText = userMessage
            Log.d("WebSocket", "🕐 callId 없음, 대기열에 저장됨: $userMessage")
            return
        }

        if (!isConnected) {
            pendingCallId = callId
            pendingText = userMessage
            Log.d("WebSocket", "🕐 연결 중, 대기열에 저장됨")
            if (!isConnecting) connectWebSocket()
            return
        }

        isWaitingResponse = true

        val json = JSONObject().apply {
            put("action", "message")
            put("data", JSONObject().apply {
                put("callId", callId)
                put("userMessage", userMessage)
            })
        }

        Log.d("VoiceCall", "📤 전송 JSON: ${json.toString(2)}")
        ws?.send(json.toString())
    }

    /**
     * 대화 시작 요청
     * memberId: 사용자 ID
     * topic: 주제(SPORTS, ...)
     */
    fun sendStartCall(memberId: Long, topic: String?) {
        Log.d("VoiceCall", "📤 sendStartCall 호출됨!")

        if (!isConnected) {
            Log.d("VoiceCall", "🕐 연결 안됨. 대화 시작 대기열에 저장됨")
            pendingCallId = null // callId는 아직 없음
            pendingText = null   // 메시지 아님, start 요청이니까
            // 대기열에 저장
            val json = JSONObject().apply {
                put("action", "start")
                put("data", JSONObject().apply {
                    put("memberId", memberId)
                    put("topic", topic)
                })
            }
            pendingStartJson = json.toString()
            if (!isConnecting) connectWebSocket()
            return
        }

        val json = JSONObject().apply {
            put("action", "start")
            put("data", JSONObject().apply {
                put("memberId", memberId)
                put("topic", topic)
            })
        }

        callId = null
        isCallEnded = false
        ws?.send(json.toString())
    }

    /**
     * 대화 종료 요청
     */
    fun sendEndCall() {
        val json = JSONObject().apply {
            put("action", "end")
        }
        ws?.send(json.toString())
    }

    /**
     * 초기화 함수
     */
    fun clearAiMessage() {
        aiMessage = ""
        aiMessageKor = ""
    }

    fun resetCall() {
        callId = null
        isCallEnded = false
    }


    // ===================================================================

    // STT 관련 함수

    // 음성 인식 서비스에 대한 액세스를 제공
    // 보내기 버튼으로 녹음을 멈추기 위해서 전역으로 수정
    private var appContext: Context? = null
    private var speechRecognizer: SpeechRecognizer? = null
    private var isListening = false
    var latestSpeechResult by mutableStateOf("")
    val systemMessage = mutableStateOf<String?>(null)

    fun setContext(context: Context) {
        appContext = context.applicationContext
    }

    // AI 음성 종료 후 음성 인식 자동 시작
    fun onAiVoiceEnded() {
        Log.d("VoiceCallScreen", "AI 음성 끝남, 자동으로 음성 인식 시작!")
        if (!isListening) {  // 음성 인식이 이미 시작되지 않았다면
            isListening = true
            Log.d("VoiceCallScreen", "음성 인식 시작됨!")
        }
    }

    fun startSpeechToText(context: Context, onResult: (String) -> Unit) {
        if (isListening) {
            Log.d("STT", "이미 음성 인식 중!")
            return  // 이미 음성 인식 중이면 다시 시작하지 않음
        }

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US")
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
        }

        speechRecognizer?.setRecognitionListener(object : RecognitionListener {
            override fun onResults(results: Bundle?) {
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                val result = matches?.firstOrNull()

                if (!result.isNullOrBlank()) {
                    Log.d("STT", "✅ 결과 있음: $result")
                    onResult(result)
                    stopSpeechToText()
                } else {
                    Log.w("STT", "⚠️ 인식된 결과 없음, 다시 STT 시작")
                    startSpeechToText(context, onResult) // 다시 STT 시작
                }
            }

            override fun onEndOfSpeech() {
                Log.d("STT", "🚫 onEndOfSpeech → 무시 (사용자 버튼으로 종료)")
            }

            override fun onError(error: Int) {
                Log.e("STT", "❌ 인식 오류: $error")

                if (error == SpeechRecognizer.ERROR_SPEECH_TIMEOUT) {
                    // restartSpeechToText(context, onResult)
                    showNoInputMessage()

                } else {
                    stopSpeechToText() // 다른 오류는 그냥 종료
                }
            }

            override fun onReadyForSpeech(params: Bundle?) {}
            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onPartialResults(partialResults: Bundle?) {}
            override fun onEvent(eventType: Int, params: Bundle?) {}
        })

        isListening = true
        speechRecognizer?.startListening(intent)
        Log.d("STT", "🎤 STT 시작됨")

    }

    fun restartSpeechToText(context: Context, onResult: (String) -> Unit) {
        stopSpeechToText()
        Handler(Looper.getMainLooper()).postDelayed({
            startSpeechToText(context, onResult)
        }, 500) // 0.5초 딜레이 두고 재시작
    }


    fun stopSpeechToText() {
        isListening = false // 안해주면 계속 듣고 있다고 판단함

        Log.d("STT", "🛑 STT 수동 종료")
        speechRecognizer?.stopListening()
        speechRecognizer?.cancel()
        speechRecognizer?.destroy()
        speechRecognizer = null
    }


    fun sendUserSpeech(text: String) {
        sendText(text) // 기존 웹소켓 전송 함수 재활용
    }

    fun clearLatestSpeechResult() {
        latestSpeechResult = ""
    }


    fun showNoInputMessage() {
//        if (systemMessage.value == null) {
//            systemMessage.value = "음성이 감지되지 않았어요. 대신 AI가 다시 물어봐 달라고 했어요."
//            sendUserSpeech("It’s a bit quiet. Could you repeat that for me?")
//        }
        sendUserSpeech("It’s a bit quiet. Could you repeat that for me?")

    }

    fun clearSystemMessage() {
        systemMessage.value = null
    }


}