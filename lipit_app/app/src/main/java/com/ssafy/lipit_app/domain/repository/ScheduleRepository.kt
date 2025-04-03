package com.ssafy.lipit_app.domain.repository

import android.content.Context
import android.util.Log
import com.ssafy.lipit_app.data.model.response_dto.schedule.ScheduleResponse
import com.ssafy.lipit_app.data.remote.RetrofitUtil
import com.ssafy.lipit_app.data.remote.ScheduleService

class ScheduleRepository(
    private val context: Context,
    private val scheduleService: ScheduleService

) {
    suspend fun getAllSchedules(memberId: Long): Result<List<ScheduleResponse>> {
        return try {
            //val token = SecureDataStore.getInstance(context).getAccessToken()

            //val response = scheduleService.schedule(memberId, "Bearer $token")
            val response = RetrofitUtil.scheduleService.getScheduleList(memberId)

            if (response.isSuccessful) {
                Result.success(response.body()?.data ?: emptyList())
            } else {
                val errorMsg = response.errorBody()?.string()
                Log.e("schedule", "서버 응답 에러: $errorMsg")
                Result.failure(Exception("스케줄 조회 실패: ${response.body()?.message ?: errorMsg}"))
            }

        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}