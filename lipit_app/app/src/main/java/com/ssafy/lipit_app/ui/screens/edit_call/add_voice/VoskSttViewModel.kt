//package com.ssafy.lipit_app.ui.screens.edit_call.add_voice
//
//import android.content.Context
//import android.util.Log
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.StateFlow
//import kotlinx.coroutines.flow.update
//import kotlinx.coroutines.launch
//import kotlinx.coroutines.withContext
//import org.json.JSONObject
//import java.io.File
//import java.io.FileInputStream
//import org.vosk.Model
//import org.vosk.Recognizer
//import org.vosk.android.SpeechService
//
//class VoskSttViewModel(private val context: Context) : ViewModel() {
//    private val _state = MutableStateFlow(VoskSttState())
//    val state: StateFlow<VoskSttState> = _state
//
//    private var model: Model? = null
//    private var recognizer: Recognizer? = null
//    private val audioFiles = mutableListOf<String>()
//
//    init {
//        initVoskModel()
//    }
//
//    private fun initVoskModel() {
//        viewModelScope.launch(Dispatchers.IO) {
//            try {
//                val modelPath = copyModelToInternalStorage()
//                model = Model(modelPath)
//                recognizer = Recognizer(model, 16000f)
//
//                withContext(Dispatchers.Main) {
//                    _state.update { it.copy(isModelReady = true) }
//                }
//            } catch (e: Exception) {
//                Log.e("VOSK_DEBUG", "모델 초기화 실패: ${e.message}")
//                withContext(Dispatchers.Main) {
//                    _state.update { it.copy(
//                        errorMessage = "음성 모델 로딩 실패: ${e.message}",
//                        isModelReady = false
//                    )}
//                }
//            }
//        }
//    }
//
//    private fun copyModelToInternalStorage(): String {
//        val modelName = "vosk-model-small-en-us-0.15"
//        val modelDir = File(context.filesDir, modelName)
//
//        if (!modelDir.exists()) {
//            modelDir.mkdirs()
//            // assets에서 모델 파일 복사 로직 구현 필요
//        }
//
//        return modelDir.absolutePath
//    }
//
//    fun startRecording() {
//        viewModelScope.launch {
//            _state.update {
//                it.copy(
//                    recordingStatus = RecordingStatus.RECORDING,
//                    recognizedText = "",
//                    accuracy = 0f,
//                    errorMessage = null
//                )
//            }
//            // 실제 녹음 로직 구현
//        }
//    }
//
//    fun stopRecording(audioFilePath: String) {
//        viewModelScope.launch {
//            _state.update {
//                it.copy(recordingStatus = RecordingStatus.ANALYZING)
//            }
//
//            recognizeSpeechWithVosk(audioFilePath)
//        }
//    }
//
//    private fun recognizeSpeechWithVosk(audioFilePath: String) {
//        viewModelScope.launch(Dispatchers.IO) {
//            try {
//                val audioFile = File(audioFilePath)
//                val audioInputStream = FileInputStream(audioFile)
//
//                val buffer = ByteArray(4096)
//                var bytesRead: Int
//                var result = ""
//
//                while (audioInputStream.read(buffer).also { bytesRead = it } >= 0) {
//                    if (recognizer?.acceptWaveForm(buffer, bytesRead) == true) {
//                        val partialResult = recognizer?.result
//                        Log.d("VOSK_DEBUG", "부분 결과: $partialResult")
//                    }
//                }
//
//                // 최종 결과
//                val finalResult = recognizer?.finalResult
//                result = extractRecognizedText(finalResult)
//
//                withContext(Dispatchers.Main) {
//                    val currentSentence = _state.value.sentenceList[_state.value.currentSentenceIndex]
//                    val accuracy = calculateAccuracy(currentSentence, result)
//
//                    _state.update {
//                        it.copy(
//                            recordingStatus = if (accuracy >= 0.8f) RecordingStatus.COMPLETED else RecordingStatus.FAILED,
//                            recognizedText = result,
//                            accuracy = accuracy,
//                            errorMessage = if (accuracy < 0.8f) "정확도가 낮습니다. 다시 녹음해주세요." else null
//                        )
//                    }
//
//                    // 성공적인 녹음 파일 추가
//                    if (accuracy >= 0.8f) {
//                        audioFiles.add(audioFilePath)
//                    }
//                }
//            } catch (e: Exception) {
//                Log.e("VOSK_DEBUG", "음성 인식 실패: ${e.message}")
//                withContext(Dispatchers.Main) {
//                    _state.update {
//                        it.copy(
//                            recordingStatus = RecordingStatus.FAILED,
//                            errorMessage = "음성 인식 중 오류 발생: ${e.message}"
//                        )
//                    }
//                }
//            }
//        }
//    }
//
//    private fun extractRecognizedText(jsonResult: String?): String {
//        return try {
//            val jsonObject = JSONObject(jsonResult ?: "{}")
//            jsonObject.optString("text", "")
//        } catch (e: Exception) {
//            ""
//        }
//    }
//
//    private fun calculateAccuracy(original: String, recognized: String): Float {
//        // 기존 정확도 계산 로직 그대로 사용
//        // 필요에 따라 Vosk에 최적화된 로직으로 수정 가능
//        val cleanOriginal = original.lowercase().replace("[^a-z0-9\\s']".toRegex(), " ")
//        val cleanRecognized = recognized.lowercase().replace("[^a-z0-9\\s']".toRegex(), " ")
//
//        val originalWords = cleanOriginal.replace("\\s+".toRegex(), " ").trim().split(" ")
//        val recognizedWords = cleanRecognized.replace("\\s+".toRegex(), " ").trim().split(" ")
//
//        var matchScore = 0.0
//        val totalWords = originalWords.size
//
//        if (totalWords == 0) return 0f
//
//        for (origWord in originalWords) {
//            if (origWord.isBlank()) continue
//
//            var bestScore = 0.0
//            for (recWord in recognizedWords) {
//                if (recWord.isBlank()) continue
//
//                if (origWord == recWord) {
//                    bestScore = 1.0
//                    break
//                }
//
//                val distance = levenshteinDistance(origWord, recWord)
//                val maxLength = maxOf(origWord.length, recWord.length)
//                val similarity = if (maxLength > 0) (maxLength - distance).toDouble() / maxLength else 0.0
//
//                if (similarity >= 0.6 && similarity > bestScore) {
//                    bestScore = similarity
//                }
//            }
//
//            matchScore += bestScore
//        }
//
//        return (matchScore / totalWords).toFloat()
//    }
//
//    private fun levenshteinDistance(s1: String, s2: String): Int {
//        // 기존 레벤슈타인 거리 계산 로직 그대로 사용
//        val m = s1.length
//        val n = s2.length
//        val dp = Array(m + 1) { IntArray(n + 1) }
//
//        for (i in 0..m) dp[i][0] = i
//        for (j in 0..n) dp[0][j] = j
//
//        for (i in 1..m) {
//            for (j in 1..n) {
//                dp[i][j] = minOf(
//                    dp[i - 1][j] + 1,
//                    dp[i][j - 1] + 1,
//                    dp[i - 1][j - 1] + if (s1[i - 1] == s2[j - 1]) 0 else 1
//                )
//            }
//        }
//
//        return dp[m][n]
//    }
//
//    fun nextSentence() {
//        val current = _state.value
//
//        if (current.accuracy < 0.8f) {
//            _state.update {
//                it.copy(errorMessage = "정확도가 낮습니다. 다시 녹음해주세요.")
//            }
//            return
//        }
//
//        if (current.currentSentenceIndex < current.sentenceList.lastIndex) {
//            _state.update {
//                it.copy(
//                    currentSentenceIndex = it.currentSentenceIndex + 1,
//                    recordingStatus = RecordingStatus.WAITING,
//                    recognizedText = "",
//                    accuracy = 0f,
//                    errorMessage = null
//                )
//            }
//        } else {
//            _state.update {
//                it.copy(
//                    isAllSentencesRecorded = true,
//                    recordingStatus = RecordingStatus.WAITING
//                )
//            }
//        }
//    }
//
//    override fun onCleared() {
//        super.onCleared()
//        recognizer?.close()
//        model?.close()
//    }
//}
//
//// 상태 데이터 클래스
//data class VoskSttState(
//    val sentenceList: List<String> = emptyList(),
//    val currentSentenceIndex: Int = 0,
//    val recordingStatus: RecordingStatus = RecordingStatus.WAITING,
//    val recognizedText: String = "",
//    val accuracy: Float = 0f,
//    val errorMessage: String? = null,
//    val isAllSentencesRecorded: Boolean = false,
//    val isModelReady: Boolean = false
//)
//
//// 기존 RecordingStatus enum 그대로 사용
////enum class RecordingStatus {
////    WAITING, RECORDING, ANALYZING, COMPLETED, FAILED
////}