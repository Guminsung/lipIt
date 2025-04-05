package com.ssafy.lipit_app.domain.repository

import android.util.Log
import com.ssafy.lipit_app.BuildConfig
import com.ssafy.lipit_app.base.ApplicationClass.Companion.client
import com.ssafy.lipit_app.data.model.request_dto.custom_voice.S3UploadRequest
import com.ssafy.lipit_app.data.model.request_dto.custom_voice.SaveCustomVoiceRequest
import com.ssafy.lipit_app.data.model.response_dto.custom_vocie.S3UploadResponse
import com.ssafy.lipit_app.data.model.response_dto.custom_vocie.SaveCustomVoiceResponse
import com.ssafy.lipit_app.data.model.response_dto.custom_vocie.WhisperResponse
import com.ssafy.lipit_app.data.remote.RetrofitUtil
import handleResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Request
import java.io.File

class CustomVoiceRepository {
    private val TAG = "VoiceRepository"

    // OpenAI Whisper API를 사용하여 음성 파일을 텍스트로 변환
    suspend fun transcribeAudio(audioFile: File): Result<WhisperResponse> {
        return try {
            Log.d(TAG, "음성 파일을 텍스트로 변환 시작: ${audioFile.name}")

            // 파일을 MultipartBody.Part로 변환
            val requestFile = audioFile.asRequestBody("audio/mpeg".toMediaTypeOrNull())
            val safeFileName = audioFile.name.replace("[^a-zA-Z0-9._-]".toRegex(), "_")
            val filePart = MultipartBody.Part.createFormData("file", safeFileName, requestFile)

            // Whisper API 파라미터들 (모두 @Part 로 전달)
            val modelPart = "whisper-1".toRequestBody("text/plain".toMediaType())
            val languagePart = "en".toRequestBody("text/plain".toMediaType())
            val responseFormatPart = "json".toRequestBody("text/plain".toMediaType())

            // OpenAI API 키
            val apiKey = "Bearer ${BuildConfig.OPEN_AI_KEY}"

            // OpenAI Whisper API 호출
            val response = RetrofitUtil.whisperService.transcribeAudio(
                file = filePart,
                model = modelPart,
                language = languagePart,
                responseFormat = responseFormatPart,
                authorization = apiKey
            )

            Log.d("API_HEADER", "Authorization header = $apiKey")


            if (response.isSuccessful) {
                Log.d(TAG, "음성 텍스트 변환 성공: ${response.body()?.text}")
                Result.success(response.body()!!)
            } else {
                val errorBody = response.errorBody()?.string() ?: "Unknown error"
                Log.e(TAG, "음성 텍스트 변환 실패: $errorBody")
                Result.failure(Exception("음성 텍스트 변환 실패: $errorBody"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "음성 텍스트 변환 중 예외 발생: ${e.message}", e)
            Result.failure(e)
        }
    }


    // 백엔드 서버에 커스텀 음성 정보 저장
    suspend fun saveCustomVoice(
        memberId: Long,
        voiceName: String,
        audioUrl: String,
        imageUrl: String = ""
    ): Result<SaveCustomVoiceResponse> {
        return try {
            val request = SaveCustomVoiceRequest(
                voiceName = voiceName,
                audioUrl = audioUrl,
                imageUrl = imageUrl
            )
            val response = RetrofitUtil.myVoiceService.saveCustomVoice(memberId, request)
            handleResponse(response)
        } catch (e: Exception) {
            Log.e(TAG, "커스텀 음성 저장 중 예외 발생: ${e.message}", e)
            Result.failure(e)
        }
    }


    // TODO : 음성파일 하나로 압축하는 과정 필요
    suspend fun mergeAudioFiles(audioFiles: List<String>, outputFileName: String): File {
        // 이 부분은 실제로 FFmpeg 등의 라이브러리를 이용해야 합니다
        // 여기서는 간단하게 첫 번째 파일만 반환하는 예시로 작성합니다
        Log.d(TAG, "음성 파일 병합 시작 (${audioFiles.size}개 파일)")

        // 실제 구현이 필요한 부분입니다!
        // 예시로 첫 번째 파일을 사용
        val firstFile = File(audioFiles.first())
        val outputFile = File(firstFile.parent, "$outputFileName.mp3")

        // 여기서 파일 병합 로직 구현 필요
        firstFile.copyTo(outputFile, overwrite = true)

        Log.d(TAG, "음성 파일 병합 완료: ${outputFile.absolutePath}")
        return outputFile
    }

    // S3 에 저장하기 위한 주소를 얻어오는 과정
    suspend fun getPresignedUrl(fileName: String): Result<S3UploadResponse> {
        return try {
            val requestBody = mapOf("fileName" to fileName) // 🔥 Body로 JSON 전달
            val response = RetrofitUtil.presignService.getPresignedUrl(S3UploadRequest(fileName))

            if (response.isSuccessful) {
                val body = response.body()
                if (body?.data != null) {
                    Result.success(body.data)
                } else {
                    Result.failure(Exception("Presign 응답 성공했지만 data가 없습니다: ${body?.message ?: "없음"}"))
                }
            } else {
                Result.failure(Exception("Presign 요청 실패: HTTP ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    // 실제 S#에 저장하는 과정
    suspend fun uploadToPresignedUrl(file: File, url: String): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val contentType = when {
                    file.name.endsWith(".mp3") -> "audio/mpeg"
                    file.name.endsWith(".wav") -> "audio/wav"
                    file.name.endsWith(".png") -> "image/png"
                    file.name.endsWith(".jpg", true) || file.name.endsWith(".jpeg", true) -> "image/jpeg"
                    else -> "application/octet-stream"
                }

                val requestBody = file.asRequestBody(contentType.toMediaTypeOrNull())
                val request = Request.Builder()
                    .url(url)
                    .put(requestBody)
                    .header("content-type", contentType)
                    .build()

                client.newCall(request).execute().use { response ->
                    if (response.isSuccessful) {
                        Result.success(Unit)
                    } else {
                        Result.failure(Exception("S3 업로드 실패: ${response.code} - ${response.message}"))
                    }
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

}