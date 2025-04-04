package com.ssafy.lipit_app.ui.screens.edit_call.add_voice

import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Build
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import java.util.Locale

class AddVoiceViewModel(private val context: Context) : ViewModel() {
    private val _state = MutableStateFlow(AddVoiceState())
    val state: StateFlow<AddVoiceState> = _state

    private var recorder: MediaRecorder? = null
    private var recognizer: SpeechRecognizer? = null
    private var currentFilePath: String? = null

    private val audioFiles = mutableListOf<String>()

    init {
        initSpeechRecognizer()
    }

    private fun initSpeechRecognizer() {
        // STT 초기화 코드
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun startRecording() {
        if (_state.value.recordingStatus == RecordingStatus.RECORDING) return

        viewModelScope.launch {
            try {
                // 녹음 시작 전에 상태 업데이트 - 오류 메시지와 이전 인식 결과 초기화
                _state.update { it.copy(
                    recordingStatus = RecordingStatus.RECORDING,
                    recognizedText = "",
                    accuracy = 0f,
                    errorMessage = null  // 오류 메시지 초기화
                )}

                // 녹음 파일 경로 설정
                currentFilePath = createNewAudioFilePath()

                // 미디어 레코더 설정
                setupMediaRecorder(currentFilePath!!)

                // 녹음 시작
                recorder?.start()

                Log.e("STT_DEBUG", "녹음 시작됨: $currentFilePath")
            } catch (e: Exception) {
                Log.e("STT_DEBUG", "녹음 시작 실패: ${e.message}")
                _state.update { it.copy(errorMessage = "녹음을 시작할 수 없습니다: ${e.message}") }
            }
        }
    }

    private fun stopRecording() {
        if (_state.value.recordingStatus != RecordingStatus.RECORDING) return

        viewModelScope.launch {
            try {
                // 녹음 완료 대신 분석 중 상태로 변경
                _state.update { it.copy(
                    recordingStatus = RecordingStatus.ANALYZING,  // 새로운 상태: 분석 중
                    errorMessage = null
                )}

                // 지연 시간을 둔 후 실제 녹음 중지
                delay(800) // 마지막 단어를 처리할 시간 확보

                // 녹음 중지
                recorder?.apply {
                    stop()
                    reset()
                    release()
                }
                recorder = null

                // 녹음 파일이 생성되었으면 STT 시작
                currentFilePath?.let { filePath ->
                    Log.e("STT_DEBUG", "녹음된 파일 경로: $filePath")

                    // 이 부분에서 실제 음성 인식 처리
                    recognizeSpeechFromFile(filePath)

                    // 오디오 파일 목록에 추가
                    audioFiles.add(filePath)

                } ?: run {
                    Log.e("STT_DEBUG", "녹음 파일 경로가 없음")
                    _state.update { it.copy(
                        recordingStatus = RecordingStatus.WAITING,
                        errorMessage = "녹음 파일을 생성할 수 없습니다."
                    )}
                }
            } catch (e: Exception) {
                Log.e("STT_DEBUG", "녹음 중지 실패: ${e.message}")
                _state.update { it.copy(
                    recordingStatus = RecordingStatus.WAITING,
                    errorMessage = "녹음을 중지할 수 없습니다: ${e.message}"
                )}
            }
        }
    }

    // 녹음된 파일에서 STT 인식 (실제 음성 파일 분석 구현)
    private fun recognizeSpeechFromFile(filePath: String) {
        viewModelScope.launch {
            try {
                Log.e("STT_DEBUG", "파일에서 STT 인식 시작: $filePath")

                // 오디오 파일을 실제로 분석하는 코드
                recognizer = SpeechRecognizer.createSpeechRecognizer(context)
                recognizer?.setRecognitionListener(object : RecognitionListener {
                    override fun onReadyForSpeech(params: Bundle) {
                        Log.e("STT_DEBUG", "onReadyForSpeech 호출됨")
                    }

                    override fun onBeginningOfSpeech() {
                        Log.e("STT_DEBUG", "onBeginningOfSpeech 호출됨")
                    }

                    override fun onRmsChanged(rmsdB: Float) {}

                    override fun onBufferReceived(buffer: ByteArray) {}

                    override fun onEndOfSpeech() {
                        Log.e("STT_DEBUG", "onEndOfSpeech 호출됨")
                    }

                    override fun onResults(results: Bundle) {
                        Log.e("STT_DEBUG", "onResults 호출됨")
                        val matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)

                        if (!matches.isNullOrEmpty()) {
                            val recognizedText = matches[0]
                            Log.e("STT_DEBUG", "실제 인식된 텍스트: $recognizedText")

                            // 원본 문장과 정확도 계산
                            val currentSentence = _state.value.sentenceList[_state.value.currentSentenceIndex]
                            val accuracy = calculateAccuracy(currentSentence, recognizedText)

                            // 분석 완료 후 상태 업데이트 - 정확도에 따라 COMPLETED 또는 FAILED로 설정
                            _state.update { it.copy(
                                recordingStatus = if (accuracy >= 0.8f) RecordingStatus.COMPLETED else RecordingStatus.FAILED,
                                recognizedText = recognizedText,
                                accuracy = accuracy,
                                errorMessage = if (accuracy < 0.8f) "정확도가 낮습니다. 다시 녹음해주세요." else null
                            )}
                        } else {
                            Log.e("STT_DEBUG", "인식된 텍스트 없음")
                            _state.update { it.copy(
                                recordingStatus = RecordingStatus.FAILED,
                                errorMessage = "음성을 인식할 수 없습니다. 다시 시도해주세요."
                            )}
                        }

                        // 인식 완료 후 리소스 해제
                        recognizer?.destroy()
                        recognizer = null
                    }

                    override fun onPartialResults(partialResults: Bundle) {
                        val matches = partialResults.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                        if (!matches.isNullOrEmpty()) {
                            Log.e("STT_DEBUG", "부분 인식 텍스트: ${matches[0]}")
                        }
                    }

                    override fun onError(error: Int) {
                        val errorMessage = when (error) {
                            SpeechRecognizer.ERROR_AUDIO -> "오디오 에러"
                            SpeechRecognizer.ERROR_CLIENT -> "클라이언트 에러"
                            SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "권한 없음"
                            SpeechRecognizer.ERROR_NETWORK -> "네트워크 에러"
                            SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "네트워크 타임아웃"
                            SpeechRecognizer.ERROR_NO_MATCH -> "일치하는 결과 없음"
                            SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "인식기 사용 중"
                            SpeechRecognizer.ERROR_SERVER -> "서버 에러"
                            SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "음성 입력 없음"
                            else -> "알 수 없는 에러"
                        }
                        Log.e("STT_DEBUG", "STT 오류 발생: $errorMessage (코드: $error)")

                        _state.update { it.copy(
                            recordingStatus = RecordingStatus.FAILED,
                            errorMessage = "음성 인식 오류: $errorMessage"
                        )}

                        // 오류 발생 시 리소스 해제
                        recognizer?.destroy()
                        recognizer = null
                    }

                    override fun onEvent(eventType: Int, params: Bundle) {}
                })

                // STT 설정
                val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                    putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                    putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.ENGLISH.toString())
                    putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, Locale.ENGLISH.toString())
                    putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3)
                }

                // 녹음된 파일을 사용해 직접 오디오를 재생하며 STT 인식 시작
                val mediaPlayer = MediaPlayer()
                mediaPlayer.setDataSource(filePath)
                mediaPlayer.prepare()

                // 오디오 재생 시작하면서 음성 인식 시작
                mediaPlayer.setOnPreparedListener {
                    recognizer?.startListening(intent)
                    mediaPlayer.start()
                }

                // 오디오 재생 완료 후 처리
                mediaPlayer.setOnCompletionListener {
                    it.release()
                }
            } catch (e: Exception) {
                Log.e("STT_DEBUG", "파일에서 STT 인식 실패: ${e.message}")
                e.printStackTrace()
                _state.update { it.copy(
                    recordingStatus = RecordingStatus.FAILED,
                    errorMessage = "음성 인식에 실패했습니다: ${e.message}"
                )}

                // 오류 발생 시 리소스 해제
                recognizer?.destroy()
                recognizer = null
            }
        }
    }

    // 새로운 오디오 파일 경로 생성
    private fun createNewAudioFilePath(): String {
        val fileName = "voice_recording_${System.currentTimeMillis()}.mp3"
        val dir = File(context.getExternalFilesDir(null), "voice_recordings")
        if (!dir.exists()) {
            dir.mkdirs()
        }
        return File(dir, fileName).absolutePath
    }

    // 미디어 레코더 설정
    @RequiresApi(Build.VERSION_CODES.S)
    private fun setupMediaRecorder(filePath: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            recorder = MediaRecorder(context)
        } else {
            recorder = MediaRecorder()
        }

        recorder?.apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setAudioChannels(1)
            setAudioSamplingRate(44100)
            setAudioEncodingBitRate(128000)
            setOutputFile(filePath)
            prepare()
        }
    }

    // STT 인식 중지
    private fun stopSpeechRecognition() {
        recognizer?.stopListening()
        recognizer?.cancel()
        recognizer?.destroy()
        recognizer = null
    }

    private fun nextSentence() {
        val current = _state.value

        // 정확도가 80% 미만이면 다음으로 넘어갈 수 없음
        if (current.accuracy < 0.8f) {
            _state.update { it.copy(errorMessage = "정확도가 낮습니다. 다시 녹음해주세요.") }
            return
        }

        if (current.currentSentenceIndex < current.sentenceList.lastIndex) {
            _state.update { it.copy(
                currentSentenceIndex = it.currentSentenceIndex + 1,
                recordingStatus = RecordingStatus.WAITING,
                recognizedText = "",
                accuracy = 0f,
                errorMessage = null
            )}
        } else {
            // 모든 문장 녹음 완료
            _state.update { it.copy(
                isAllSentencesRecorded = true,
                recordingStatus = RecordingStatus.WAITING
            )}
        }
    }

    fun setVoiceName(name: String) {
        _state.update { it.copy(voiceName = name) }
    }

    fun submitVoice() {
        val current = _state.value
        if (current.voiceName.isBlank()) {
            _state.update { it.copy(errorMessage = "음성 이름을 입력해주세요.") }
            return
        }

        viewModelScope.launch {
            try {
                _state.update { it.copy(isUploading = true, errorMessage = null) }

                // 오디오 파일 병합
                val mergedFilePath = mergeAudioFiles(audioFiles)

                // S3 업로드
                val s3Url = uploadToS3(mergedFilePath, current.voiceName)

                // 백엔드 서버에 URL 전송
                saveVoiceToServer(current.voiceName, s3Url)

                _state.update { it.copy(
                    isUploading = false,
                    uploadSuccess = true
                )}
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isUploading = false,
                        errorMessage = "음성 업로드 중 오류가 발생했습니다: ${e.message}"
                    )
                }
            }
        }
    }

    // 개선된 STT 정확도 계산 함수
    private fun calculateAccuracy(original: String, recognized: String): Float {
        // 특수 문자 제거 및 소문자 변환
        val cleanOriginal = original.lowercase().replace("[^a-z0-9\\s']".toRegex(), " ")
        val cleanRecognized = recognized.lowercase().replace("[^a-z0-9\\s']".toRegex(), " ")

        // 연속된 공백을 단일 공백으로 대체하고 단어 분리
        val originalWords = cleanOriginal.replace("\\s+".toRegex(), " ").trim().split(" ")
        val recognizedWords = cleanRecognized.replace("\\s+".toRegex(), " ").trim().split(" ")

        Log.e("STT_DEBUG", "정제된 원본 단어들: $originalWords")
        Log.e("STT_DEBUG", "정제된 인식 단어들: $recognizedWords")

        // 레벤슈타인 거리를 사용한 단어 유사도 계산
        var matchScore = 0.0
        val totalWords = originalWords.size

        if (totalWords == 0) return 0f

        // 각 원본 단어에 대해 최적의 매칭 단어 찾기
        for (origWord in originalWords) {
            if (origWord.isBlank()) continue

            var bestScore = 0.0
            for (recWord in recognizedWords) {
                if (recWord.isBlank()) continue

                // 단어가 정확히 일치하면 1점
                if (origWord == recWord) {
                    bestScore = 1.0
                    Log.e("STT_DEBUG", "정확히 일치: $origWord = $recWord")
                    break
                }

                // 그렇지 않으면 레벤슈타인 거리 기반 유사도 계산
                val distance = levenshteinDistance(origWord, recWord)
                val maxLength = maxOf(origWord.length, recWord.length)
                val similarity = if (maxLength > 0) (maxLength - distance).toDouble() / maxLength else 0.0

                // 유사도가 0.6 이상이면 부분 매칭으로 간주 (더 관대한 매칭)
                if (similarity >= 0.6 && similarity > bestScore) {
                    bestScore = similarity
                    Log.e("STT_DEBUG", "부분 일치: $origWord ≈ $recWord (유사도: ${similarity * 100}%)")
                }
            }

            matchScore += bestScore
        }

        val finalAccuracy = (matchScore / totalWords).toFloat()
        Log.e("STT_DEBUG", "총 매칭 점수: $matchScore/$totalWords, 최종 정확도: ${finalAccuracy * 100}%")

        return finalAccuracy
    }

    // 레벤슈타인 거리 계산 함수 (단어 간 편집 거리 계산)
    private fun levenshteinDistance(s1: String, s2: String): Int {
        val m = s1.length
        val n = s2.length
        val dp = Array(m + 1) { IntArray(n + 1) }

        for (i in 0..m) {
            dp[i][0] = i
        }

        for (j in 0..n) {
            dp[0][j] = j
        }

        for (i in 1..m) {
            for (j in 1..n) {
                dp[i][j] = minOf(
                    dp[i - 1][j] + 1,
                    dp[i][j - 1] + 1,
                    dp[i - 1][j - 1] + if (s1[i - 1] == s2[j - 1]) 0 else 1
                )
            }
        }

        return dp[m][n]
    }

    // 오디오 파일 병합 함수
    private suspend fun mergeAudioFiles(filePaths: List<String>): String {
        // 실제 구현은 외부 라이브러리 사용 필요
        return "merged_audio_path.mp3" // 더미 값
    }

    // S3 업로드 함수
    private suspend fun uploadToS3(filePath: String, fileName: String): String {
        // AWS SDK를 사용하여 S3 업로드 구현
        delay(2000) // 업로드 시간 시뮬레이션
        return "https://s3.example.com/voices/$fileName.mp3" // 더미 URL
    }

    // 백엔드 서버에 음성 정보 저장
    private suspend fun saveVoiceToServer(name: String, url: String) {
        // Retrofit 등을 사용하여 서버 API 호출
        delay(1000) // API 호출 시간 시뮬레이션
    }

    // Firebase ML Kit를 사용한 처리 함수 - 주석 처리
    /*
    private fun processAudioFileWithFirebase(filePath: String) {
        viewModelScope.launch {
            try {
                Log.e("STT_DEBUG", "Firebase ML Kit로 오디오 파일 처리 시작: $filePath")

                // 여기서는 임의의 테스트 텍스트를 사용 (실제 구현에서는 파이어베이스 호출로 대체)
                // 사용자가 실제로 말한 내용을 인식하도록 구현
                val testRecognizedText = "i love you" // 실제 사용자가 말한 내용

                Log.e("STT_DEBUG", "인식된 텍스트: $testRecognizedText")

                // 원본 문장과 정확도 계산
                val currentSentence = _state.value.sentenceList[_state.value.currentSentenceIndex]
                val accuracy = calculateAccuracy(currentSentence, testRecognizedText)

                _state.update { it.copy(
                    recognizedText = testRecognizedText,
                    accuracy = accuracy
                )}

                // 오디오 파일을 저장 (나중에 모든 오디오 파일 병합 위해)
                if (accuracy >= 0.8f) {
                    audioFiles.add(filePath)
                    Log.e("STT_DEBUG", "음성 파일 저장됨 (정확도: ${accuracy * 100}%)")
                }

            } catch (e: Exception) {
                Log.e("STT_DEBUG", "오디오 파일 처리 실패: ${e.message}")
                e.printStackTrace()
                _state.update { it.copy(errorMessage = "음성 처리에 실패했습니다: ${e.message}") }
            }
        }
    }
    */

    @RequiresApi(Build.VERSION_CODES.S)
    fun onIntent(intent: AddVoiceIntent) {
        when (intent) {
            is AddVoiceIntent.StartRecording -> startRecording()
            is AddVoiceIntent.StopRecording -> stopRecording()
            is AddVoiceIntent.NextSentence -> nextSentence()
            is AddVoiceIntent.SetVoiceName -> setVoiceName(intent.name)
            is AddVoiceIntent.SubmitVoice -> submitVoice()
            is AddVoiceIntent.NavigateToMain -> {} // NavGraph에서 처리
        }
    }

    override fun onCleared() {
        super.onCleared()
        recorder?.release()
        recorder = null
        recognizer?.destroy()
        recognizer = null
    }
}