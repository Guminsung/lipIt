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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.ssafy.lipit_app.data.model.ChatMessage
import com.ssafy.lipit_app.data.model.ChatMessageText
import com.ssafy.lipit_app.domain.repository.MyVoiceRepository
import com.ssafy.lipit_app.ui.screens.call.oncall.text_call.TextCallViewModel
import com.ssafy.lipit_app.util.SharedPreferenceUtils
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
    val _state = MutableStateFlow(VoiceCallState())
    val state: StateFlow<VoiceCallState> = _state
    var currentMode by mutableStateOf("Voice") // or "Text"
    val chatMessages = mutableStateListOf<ChatMessage>()


    // 모드 변경 관련
    fun toggleMode() {
        _state.update { current ->
            current.copy(
                currentMode = if (current.currentMode == "Text") "Voice" else "Text"
            )
        }
    }


    fun convertToTextMessages(): List<ChatMessageText> {
        return chatMessages.map { msg ->
            ChatMessageText(
                text = msg.message,
                translatedText = msg.messageKor ?: "",
                isFromUser = msg.type == "user"
            )
        }
    }

    fun addAiMessage(ai: String, kor: String) {
        chatMessages.add(
            ChatMessage(type = "ai", message = ai, messageKor = kor)
        )
    }

    fun addUserMessage(text: String) {
        chatMessages.add(
            ChatMessage(type = "user", message = text)
        )
    }


    // 남은 시간 카운트 관련
    private var timerJob: Job? = null
    val connectionError = mutableStateOf(false) // 통화 시 서버 연결 안되었을 때 활용

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

            is VoiceCallIntent.UpdateTranslation -> {
                _state.update {
                    it.copy(AIMessageTranslate = intent.translatedMessage)
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


    private val voiceRepository by lazy { MyVoiceRepository() }

    fun loadVoiceName(memberId: Long) {
        Log.d("VoiceCallViewModel", "🟢 loadVoiceName() 호출됨")
        Log.d("VoiceCallViewModel", "📢 voiceRepository 인스턴스: $voiceRepository")

        viewModelScope.launch {
            Log.d("VoiceCallViewModel", "🟢 코루틴 시작")

            val result = voiceRepository.getVoiceName(memberId)

            result.onSuccess { name ->
                Log.d("VoiceCallViewModel", "✅ 이름 받아옴: $name")
                _state.update { it.copy(voiceName = name) }
            }.onFailure {
                Log.e("VoiceCallViewModel", "❌ 이름 로드 실패: ${it.message}")
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

    fun initPlayerIfNeeded(context: Context) {
        if (exoPlayer == null) {
            initializePlayer(context)
        }
    }

    /** ExoPlayer 초기화 */
    /** ExoPlayer 초기화 */
    fun initializePlayer(context: Context) {
        if (exoPlayer == null) {
            Log.d("ExoPlayer", "🎬 ViewModel에서 초기화 시작")

            exoPlayer = ExoPlayer.Builder(context).build().also {
                it.addListener(object : Player.Listener {
                    override fun onPlaybackStateChanged(playbackState: Int) {
                        if (playbackState == Player.STATE_ENDED) {
                            playNextFromQueue()
                        }
                    }
                })
            }

            Log.d("ExoPlayer", "✅ ExoPlayer 초기화 완료")
        } else {
            Log.d("ExoPlayer", "✅ ExoPlayer 이미 초기화되어 있음")
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
                    onWebSocketOpened()

                    isConnected = true
                    isConnecting = false
                    connectionStatusText = "✅ 연결됨"

                    // 하트비트 시작
                    heartbeat = WebSocketHeartbeat(this)
                    heartbeat?.start()

                    pendingStartJson?.let {
                        Log.d("WebSocket", "📤 대기 중이던 startCall 전송")

                        if (ws?.isOpen == true) {
                            ws?.send(it)
                            pendingStartJson = null
                        } else {
                            Log.w("WebSocket", "❗️ws는 연결되었지만 아직 open 상태가 아님, 잠시 후 재시도")
                            // 재시도 로직 또는 일정 시간 후 재전송 로직 추가
                            mainHandler.postDelayed({
                                if (ws?.isOpen == true) {
                                    ws?.send(it)
                                    pendingStartJson = null
                                    Log.d("WebSocket", "📤 재시도 후 startCall 전송 성공")
                                } else {
                                    Log.e("WebSocket", "❌ 재시도에도 ws가 아직 열리지 않음")
                                }
                            }, 300) // 300ms 후 재시도
                        }
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
                if (!connectionError.value) {
                    connectionError.value = true
                } // 연결 실패 알림용

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
                if (!connectionError.value) {
                    connectionError.value = true
                } // 연결 실패 알림용

                mainHandler.post {
                    isConnected = false
                    isConnecting = false
                    isWaitingResponse = false
                    connectionStatusText = "❌ 오류 발생"

                    // 하트비트 정지
                    heartbeat?.stop()
                    heartbeat = null

                    Log.e("WebSocket", "❌ 오류: ${ex?.message}")
                    // todo: No address associated with hostname일 경우 서버 종료를 의미하니 추가 ui요소 만들기
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
        Log.d("ExoPlayer", "✅ enqueueAndPlay() 실행됨")

        val tempFile = File.createTempFile("tts_", ".wav")
        FileOutputStream(tempFile).use { out ->
            val bytes = ByteArray(buffer.remaining())
            buffer.get(bytes)
            out.write(bytes)
        }

        Log.d("ExoPlayer", "📥 오디오 파일 저장 완료: ${tempFile.absolutePath}, size=${tempFile.length()}")

        audioQueue.add(tempFile)

        if (exoPlayer?.isPlaying != true && exoPlayer?.playbackState != ExoPlayer.STATE_BUFFERING) {
            Log.d("ExoPlayer", "▶️ playNextFromQueue() 호출 조건 만족")
            playNextFromQueue()
        } else {
            Log.d("ExoPlayer", "⏸️ 재생 중이거나 버퍼링 상태로 대기 중")
        }
    }


    /** 큐에서 다음 오디오 재생 */
    private fun playNextFromQueue() {
        val next = audioQueue.poll() ?: run {
            Log.d("ExoPlayer", "❌ 큐 비어있음 - 재생 안함")
            isWaitingResponse = false
            return
        }

        Log.d("ExoPlayer", "🎧 재생 시도 - 파일: ${next.absolutePath}, size=${next.length()}")

        try {
            exoPlayer?.setMediaItem(MediaItem.fromUri(Uri.fromFile(next)))
            exoPlayer?.prepare()
            exoPlayer?.play()
            Log.d("ExoPlayer", "▶️ 재생 시작됨")
        } catch (e: Exception) {
            Log.e("ExoPlayer", "❌ 재생 실패: ${e.message}")
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
            Log.d("VoiceCall", "🕐 연결 안됨. 대화 시작 대기열에 저장됨.")
            pendingCallId = null
            pendingText = null
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

        try {
            if (ws?.isOpen == true) {
                ws?.send(json.toString())
            } else {
                Log.e("VoiceCall", "❌ WebSocket 연결은 있지만 아직 open 상태가 아님")
                connectionError.value = true
            }
        } catch (e: Exception) {
            Log.e("VoiceCall", "❌ sendStartCall 실패: ${e.message}", e)
            connectionError.value = true
        }
    }


    /**
     * 대화 종료 요청
     */
    fun sendEndCall() {
        stopSpeechToText()  // 음성 인식 종료
        stopCountdown()  // 타이머 종료
        releasePlayer()  // 플레이어 해제
        audioQueue.clear() //  남은 오디오 큐 비우기

        if (ws == null || !isConnected) {
            Log.w("WebSocket", "❌ WebSocket 연결 안 되어 있음 - 종료 메시지 전송 생략")
            return
        }

        val json = JSONObject().apply {
            put("action", "end")
        }
        try {
            ws?.send(json.toString())
            ws?.close()                 // WebSocket 강제 종료
            isConnected = false
            isConnecting = false
        } catch (e: Exception) {
            Log.e("WebSocket", "❌ 종료 메시지 전송 실패: ${e.message}", e)
        }
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
        audioQueue.clear() // 통화 연속 시도 시 이전 기록 비우기
    }

    private fun onWebSocketOpened() {
        isConnected = true
        isConnecting = false
        connectionStatusText = "✅ 연결됨"

        heartbeat = WebSocketHeartbeat(ws!!)
        heartbeat?.start()

        // 연결 후 바로 통화 시작 요청
        val memberId = SharedPreferenceUtils.getMemberId()
        sendStartCall(memberId = memberId, topic = null)
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
    var fullSpeechBuffer = StringBuilder()

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
            override fun onPartialResults(partialResults: Bundle?) {
                val partial =
                    partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                        ?.firstOrNull()
                if (!partial.isNullOrBlank()) {
                    Log.d("STT", "✍️ Partial: $partial")
                    latestSpeechResult = partial
                    fullSpeechBuffer = StringBuilder(partial) // 덮어쓰기 (or append 해도 됨)
                }
            }

            override fun onResults(results: Bundle?) {
                val result =
                    results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.firstOrNull()
                if (!result.isNullOrBlank()) {
                    Log.d("STT", "✅ 최종 결과: $result")
                    fullSpeechBuffer = StringBuilder(result)

                    onResult(result)
                }

                stopSpeechToText()

                // 자동으로 다시 듣기
//                Handler(Looper.getMainLooper()).postDelayed({
//                    startSpeechToText(context, onResult)
//                }, 500)
            }

            override fun onEndOfSpeech() {
                Log.d("STT", "🚫 onEndOfSpeech → 무시 (사용자 버튼으로 종료)")
            }

            override fun onError(error: Int) {
                Log.e("STT", "❌ 인식 오류: $error")

                when (error) {
                    SpeechRecognizer.ERROR_SPEECH_TIMEOUT,
                    SpeechRecognizer.ERROR_NO_MATCH -> {
                        //restartSpeechToText(context, onResult)
                        stopSpeechToText()
                        showNoInputMessage()
                    }
                    else -> {
                        stopSpeechToText()
                    }
                }
            }

            override fun onReadyForSpeech(params: Bundle?) {}
            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}
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
        if (!isListening) return
        isListening = false

        Log.d("STT", "🛑 STT 수동 종료")
        speechRecognizer?.stopListening()
        speechRecognizer?.cancel()
        speechRecognizer?.destroy()
        speechRecognizer = null
    }


    fun sendUserSpeech(text: String, textCallViewModel: TextCallViewModel? = null) {
        // 이미 마지막 메시지가 동일하면 추가 X - 중복 방지
        if (chatMessages.lastOrNull()?.message == text && chatMessages.lastOrNull()?.type == "user") {
            Log.d("VoiceCall", "⚠️ 중복 유저 메시지 감지 - 전송 생략: $text")
            return
        }

        sendText(text) // 기존 웹소켓 전송 함수 재활용

        chatMessages.add(ChatMessage(type = "user", message = text))

        // 조건: 현재 모드가 텍스트일 때만 텍스트 쪽에도 추가
        if (state.value.currentMode == "Text") {
            textCallViewModel?.addMessage(
                ChatMessageText(
                    text = text,
                    translatedText = "",
                    isFromUser = true
                )
            )
        }
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


    // 텍스트 -> 보이스로 돌아올때 대화 싱크 맞추기
    fun syncFromTextMessages(messages: List<ChatMessageText>) {
        chatMessages.clear()
        chatMessages.addAll(messages.map {
            ChatMessage(
                type = if (it.isFromUser) "user" else "ai",
                message = it.text,
                messageKor = it.translatedText
            )
        })
    }

    fun getLastAiMessage(): ChatMessageText? {
        return chatMessages
            .lastOrNull { it.type == "ai" }
            ?.let {
                ChatMessageText(
                    text = it.message,
                    translatedText = it.messageKor ?: "",
                    isFromUser = false
                )
            }
    }


}