package com.ssafy.lipit_app.domain.repository

import android.util.Log
import com.ssafy.lipit_app.data.model.request_dto.auth.LoginRequest
import com.ssafy.lipit_app.data.model.request_dto.auth.LogoutRequest
import com.ssafy.lipit_app.data.model.request_dto.auth.RefreshAccessTokenRequest
import com.ssafy.lipit_app.data.model.request_dto.auth.SignUpRequest
import com.ssafy.lipit_app.data.model.response_dto.auth.LevelResponse
import com.ssafy.lipit_app.data.model.response_dto.auth.LoginResponse
import com.ssafy.lipit_app.data.model.response_dto.auth.LogoutResponse
import com.ssafy.lipit_app.data.model.response_dto.auth.RefreshAccessTokenResponse
import com.ssafy.lipit_app.data.model.response_dto.auth.SignupResponse
import com.ssafy.lipit_app.data.model.response_dto.schedule.ScheduleResponse
import com.ssafy.lipit_app.data.remote.RetrofitUtil
import handleResponse

class ScheduleRepository {

    // 일주일 스케줄 조회
    suspend fun getWeeklyCallsSchedule(memberId: Long): Result<List<ScheduleResponse>> {
        return try {
            val response = RetrofitUtil.scheduleService.getScheduleList(memberId)

            if (response.isSuccessful) {
                val body = response.body()?.data ?: emptyList()
                Result.success(body)
            } else {
                val error = response.errorBody()?.string()
                Result.failure(Exception("API 실패: $error"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteSchedule(callScheduleId:Long, memberId: Long): Result<Unit> {
        return try {
            val response = RetrofitUtil.scheduleService.deleteSchedule(callScheduleId, memberId)
            if (response.isSuccessful) Result.success(Unit)
            else Result.failure(Exception("삭제 실패"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


}