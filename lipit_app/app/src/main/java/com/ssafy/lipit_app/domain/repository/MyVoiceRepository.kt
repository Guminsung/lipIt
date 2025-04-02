package com.ssafy.lipit_app.domain.repository

import android.util.Log
import com.ssafy.lipit_app.data.model.request_dto.myvoice.VoiceRequest
import com.ssafy.lipit_app.data.model.response_dto.myvoice.CelabResponse
import com.ssafy.lipit_app.data.model.response_dto.myvoice.CustomResponse
import com.ssafy.lipit_app.data.model.response_dto.myvoice.VoiceResponse
import com.ssafy.lipit_app.data.model.response_dto.myvoice.VoiceTakeResponse
import com.ssafy.lipit_app.data.remote.RetrofitUtil
import handleResponse

class MyVoiceRepository {

    // 셀럽 음성 목록 조회
    suspend fun getCelebVoices(memberId: Long)
            : Result<List<CelabResponse>> {
        return try {
            val response = RetrofitUtil.myVoiceService.getCelebVoices(memberId)
            if (response.isSuccessful) {
                Log.d("MyVoiceRepository", "셀럽 음성 목록 조회: ${response.body()}")
            }
            handleResponse(response)
        } catch (e: Exception) {
            Log.e("MyVoiceRepository", "예외 발생: ${e.message}", e)
            Result.failure(e)
        }
    }

    // 커스텀 음성 목록 조회
    suspend fun getCustomVoices(memberId: Long)
            : Result<List<CustomResponse>> {
        return try {
            val response = RetrofitUtil.myVoiceService.getCustomVoices(memberId)
            if (response.isSuccessful) {
                Log.d("MyVoiceRepository", "커스텀 음성 목록 조회: ${response.body()}")
            }
            handleResponse(response)
        } catch (e: Exception) {
            Log.e("MyVoiceRepository", "예외 발생: ${e.message}", e)
            Result.failure(e)
        }
    }

    // 선택한 음성 조회
    suspend fun getVoice(memberId: Long)
            : Result<List<CustomResponse>> {
        return try {
            val response = RetrofitUtil.myVoiceService.getVoice(memberId)
            if (response.isSuccessful) {
                Log.d("MyVoiceRepository", "선택한 음성 조회: ${response.body()}")
            }
            handleResponse(response)
        } catch (e: Exception) {
            Log.e("MyVoiceRepository", "예외 발생: ${e.message}", e)
            Result.failure(e)
        }
    }

    // 기본 음성 선택
    suspend fun changeVoice(memberId: Long, voiceId: Long)
            : Result<VoiceTakeResponse> {
        return try {
            val response = RetrofitUtil.myVoiceService.changeVoice(memberId, voiceId)
            if (response.isSuccessful) {
                Log.d("MyVoiceRepository", "기본 음성 선택: ${response.body()}")
            }
            handleResponse(response)
        } catch (e: Exception) {
            Log.e("MyVoiceRepository", "예외 발생: ${e.message}", e)
            Result.failure(e)
        }
    }


    // 커스텀 녹음 저장
    suspend fun saveCustomVoice(voiceRequest: VoiceRequest)
            : Result<VoiceResponse> {
        return try {
            val response = RetrofitUtil.myVoiceService.saveCustomVoice(voiceRequest)
            if (response.isSuccessful) {
                Log.d("MyVoiceRepository", "커스텀 음성 저장 성공: ${response.body()}")
                Result.success(response)
            }
            handleResponse(response)
        } catch (e: Exception) {
            Log.e("MyVoiceRepository", "예외 발생: ${e.message}", e)
            Result.failure(e)
        }
    }
}
