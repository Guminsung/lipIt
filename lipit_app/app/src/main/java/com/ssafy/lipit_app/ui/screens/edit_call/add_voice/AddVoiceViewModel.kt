package com.ssafy.lipit_app.ui.screens.edit_call.add_voice

import android.content.Context
import android.media.MediaRecorder
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.lipit_app.domain.repository.CustomVoiceRepository
import com.ssafy.lipit_app.util.SharedPreferenceUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject


class AddVoiceViewModel @Inject constructor(context: Context) : ViewModel() {
    private val TAG = "AddVoiceViewModel"
    private val _state = MutableStateFlow(AddVoiceState())
    val state: StateFlow<AddVoiceState> = _state

    private var recorder: MediaRecorder? = null
    private var currentFilePath: String? = null
    private lateinit var appContext: Context

    private val audioFiles = mutableListOf<String>()

    // Repository 인스턴스 생성
    private val voiceRepository = CustomVoiceRepository()

    // Context 설정 메서드 (Activity나 Fragment에서 호출)
    fun setContext(context: Context) {
        appContext = context.applicationContext
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

                Log.d(TAG, "녹음 시작됨: $currentFilePath")
            } catch (e: Exception) {
                Log.e(TAG, "녹음 시작 실패: ${e.message}")
                _state.update { it.copy(
                    recordingStatus = RecordingStatus.WAITING,
                    errorMessage = "녹음을 시작할 수 없습니다: ${e.message}"
                )}
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

                // 녹음 중지
                recorder?.apply {
                    stop()
                    reset()
                    release()
                }
                recorder = null

                // 녹음 파일이 생성되었으면 OpenAI Whisper API로 분석 시작
                currentFilePath?.let { filePath ->
                    Log.d(TAG, "녹음된 파일 경로: $filePath")

                    // 녹음 파일 분석 및 정확도 평가
                    processAudioWithWhisper(filePath)

                } ?: run {
                    Log.e(TAG, "녹음 파일 경로가 없음")
                    _state.update { it.copy(
                        recordingStatus = RecordingStatus.WAITING,
                        errorMessage = "녹음 파일을 생성할 수 없습니다."
                    )}
                }
            } catch (e: Exception) {
                Log.e(TAG, "녹음 중지 실패: ${e.message}")
                _state.update { it.copy(
                    recordingStatus = RecordingStatus.WAITING,
                    errorMessage = "녹음을 중지할 수 없습니다: ${e.message}"
                )}
            }
        }
    }

    // OpenAI Whisper API를 사용한 음성 분석 메서드
    private fun processAudioWithWhisper(filePath: String) {
        viewModelScope.launch {
            try {
                val audioFile = File(filePath)

                // OpenAI Whisper API 호출
                val result = voiceRepository.transcribeAudio(audioFile)

                if (result.isSuccess) {
                    // 인식된 텍스트 가져오기
                    val response = result.getOrThrow()
                    val recognizedText = response.text.trim()

                    Log.d(TAG, "인식된 텍스트: $recognizedText")

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

                    // 정확도가 충분하면 오디오 파일 목록에 추가
                    if (accuracy >= 0.8f) {
                        audioFiles.add(filePath)
                    }
                } else {
                    // API 호출 실패
                    val exception = result.exceptionOrNull()
                    Log.e(TAG, "Whisper API 호출 실패: ${exception?.message}")
                    _state.update { it.copy(
                        recordingStatus = RecordingStatus.FAILED,
                        errorMessage = "음성 인식에 실패했습니다: ${exception?.message}"
                    )}
                }

            } catch (e: Exception) {
                Log.e(TAG, "Whisper API 분석 실패: ${e.message}", e)
                _state.update { it.copy(
                    recordingStatus = RecordingStatus.FAILED,
                    errorMessage = "음성 인식에 실패했습니다: ${e.message}"
                )}
            }
        }
    }

    // 새로운 오디오 파일 경로 생성
    private fun createNewAudioFilePath(): String {
        val fileName = "voice_recording_${System.currentTimeMillis()}.mp3"
        val dir = File(appContext.getExternalFilesDir(null), "voice_recordings")
        if (!dir.exists()) {
            dir.mkdirs()
        }
        return File(dir, fileName).absolutePath
    }

    // 미디어 레코더 설정
    @RequiresApi(Build.VERSION_CODES.S)
    private fun setupMediaRecorder(filePath: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            recorder = MediaRecorder(appContext)
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

//    fun submitVoice() {
//        val current = _state.value
//        if (current.voiceName.isBlank()) {
//            _state.update { it.copy(errorMessage = "음성 이름을 입력해주세요.") }
//            return
//        }
//
//        viewModelScope.launch {
//            try {
//                _state.update { it.copy(isUploading = true, errorMessage = null) }
//
//                // 1. 오디오 파일 병합
//                val mergedFile = withContext(Dispatchers.IO) {
//                    voiceRepository.mergeAudioFiles(audioFiles, current.voiceName)
//                }
//
//                // 2. S3에 음성 파일 업로드
//                val s3Result = voiceRepository.uploadVoiceToS3(mergedFile, current.voiceName)
//
//                if (s3Result.isSuccess) {
//                    val s3Response = s3Result.getOrThrow()
//                    val voiceUrl = s3Response.cdnUrl
//
//                    // 3. 백엔드 서버에 음성 정보 저장
////                    memberId: Long,
////                    voiceName: String,
////                    audioUrl: String,
////                    imageUrl: String = ""
//                    val memberId = SharedPreferenceUtils.getMemberId()
//                    val saveResult = voiceRepository.saveCustomVoice(memberId, current.voiceName, voiceUrl, "")
//
//                    if (saveResult.isSuccess) {
//                        // 모든 과정 성공
//                        _state.update { it.copy(
//                            isUploading = false,
//                            uploadSuccess = true,
//                            errorMessage = null
//                        )}
//                    } else {
//                        // 백엔드 서버 저장 실패
//                        val exception = saveResult.exceptionOrNull()
//                        throw Exception("음성 정보 저장 실패: ${exception?.message}")
//                    }
//                } else {
//                    // S3 업로드 실패
//                    val exception = s3Result.exceptionOrNull()
//                    throw Exception("S3 업로드 실패: ${exception?.message}")
//                }
//            } catch (e: Exception) {
//                Log.e(TAG, "음성 업로드 실패: ${e.message}", e)
//                _state.update {
//                    it.copy(
//                        isUploading = false,
//                        errorMessage = "음성 업로드 중 오류가 발생했습니다: ${e.message}"
//                    )
//                }
//            }
//        }
//    }

    fun submitVoice() {
        val current = _state.value
        val memberId = SharedPreferenceUtils.getMemberId()

        viewModelScope.launch {
            try {
                _state.update { it.copy(isUploading = true, errorMessage = null) }

                // 1. 오디오 병합
                val mergedFile = withContext(Dispatchers.IO) {
                    voiceRepository.mergeAudioFiles(audioFiles, current.voiceName)
                }

                val timestamp = System.currentTimeMillis()
                val audioFileName = "voice-audio/${memberId}_$timestamp.mp3"
                val imageFileName = "voice-image/${memberId}_$timestamp.png" // 또는 jpg

                // 2. presign URL 요청 (1: 이미지, 2: 음성)
                val audioPresign = voiceRepository.getPresignedUrl(audioFileName).getOrThrow()
                val imagePresign = voiceRepository.getPresignedUrl(imageFileName).getOrThrow()

                val audioUrl = audioPresign.url.toString()
                val imageUrl = imagePresign.url.toString()

                // 3. 실제 업로드
                voiceRepository.uploadToPresignedUrl(mergedFile, audioUrl).getOrThrow()

                // TODO 사용자가 직접 추가한 이미지파일로 넘겨야 함
//                val imageFile = File(...) // 이미지 경로 필요
//                voiceRepository.uploadToPresignedUrl(imageFile, imageUrl).getOrThrow()

                // 4. Spring 서버에 저장 요청
                val saveResult = voiceRepository.saveCustomVoice(
                    memberId = memberId,
                    voiceName = current.voiceName,
                    audioUrl = audioPresign.cdnUrl,
                    imageUrl = imagePresign.cdnUrl
                )

                if (saveResult.isSuccess) {
                    _state.update { it.copy(uploadSuccess = true, isUploading = false) }
                } else {
                    throw Exception("DB 저장 실패: ${saveResult.exceptionOrNull()?.message}")
                }

            } catch (e: Exception) {
                Log.e(TAG, "submitVoice 실패: ${e.message}", e)
                _state.update { it.copy(isUploading = false, errorMessage = e.message) }
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

        Log.d(TAG, "정제된 원본 단어들: $originalWords")
        Log.d(TAG, "정제된 인식 단어들: $recognizedWords")

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
                    Log.d(TAG, "정확히 일치: $origWord = $recWord")
                    break
                }

                // 그렇지 않으면 레벤슈타인 거리 기반 유사도 계산
                val distance = levenshteinDistance(origWord, recWord)
                val maxLength = maxOf(origWord.length, recWord.length)
                val similarity = if (maxLength > 0) (maxLength - distance).toDouble() / maxLength else 0.0

                // 유사도가 0.6 이상이면 부분 매칭으로 간주 (더 관대한 매칭)
                if (similarity >= 0.6 && similarity > bestScore) {
                    bestScore = similarity
                    Log.d(TAG, "부분 일치: $origWord ≈ $recWord (유사도: ${similarity * 100}%)")
                }
            }

            matchScore += bestScore
        }

        val finalAccuracy = (matchScore / totalWords).toFloat()
        Log.d(TAG, "총 매칭 점수: $matchScore/$totalWords, 최종 정확도: ${finalAccuracy * 100}%")

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
    }
}