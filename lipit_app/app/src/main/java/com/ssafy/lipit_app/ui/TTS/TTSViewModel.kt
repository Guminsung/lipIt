package com.ssafy.lipit_app.ui.TTS

import android.content.Context
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.ssafy.lipit_app.util.WebSocketHeartbeat
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.net.URI
import java.nio.ByteBuffer
import java.util.ArrayDeque

class TTSViewModel : ViewModel() {
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

        exoPlayer?.setMediaItem(MediaItem.fromUri(Uri.fromFile(next)))
        exoPlayer?.prepare()
        exoPlayer?.play()
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

        Log.d("WebSocket", "📤 전송 JSON: ${json.toString(2)}")
        ws?.send(json.toString())
    }

    /**
     * 대화 시작 요청
     * memberId: 사용자 ID
     * topic: 주제(SPORTS, ...)
     */
    fun sendStartCall(memberId: Long, topic: String?) {
        if (!isConnected) {
            Log.d("WebSocket", "🕐 연결 안됨. 대화 시작 대기열에 저장됨")
            pendingCallId = null // callId는 아직 없음
            pendingText = null   // 메시지 아님, start 요청이니까
            // 👉 대기열에 저장
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
}
