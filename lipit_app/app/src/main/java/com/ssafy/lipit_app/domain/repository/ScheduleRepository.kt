package com.ssafy.lipit_app.domain.repository

import com.ssafy.lipit_app.data.model.request_dto.schedule.ScheduleCreateRequest
import com.ssafy.lipit_app.data.model.response_dto.schedule.ScheduleResponse
import com.ssafy.lipit_app.data.remote.RetrofitUtil

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

    // 스케줄 추가
    suspend fun createSchedule(
        memberId: Long,
        request: ScheduleCreateRequest
    ): Result<Unit> {
        return try {
//            Log.d("TAG", "createSchedule: ****************** ${request}")
            val response = RetrofitUtil.scheduleService.createSchedule(memberId, request)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                val error = response.errorBody()?.string()
                Result.failure(Exception("일정 생성 실패: $error"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // 수정
    suspend fun updateSchedule(
        callScheduleId: Long,
        memberId: Long,
        request: ScheduleCreateRequest
    ): Result<Unit> {
        return try {
            val response = RetrofitUtil.scheduleService.updateSchedule(
                callScheduleId = callScheduleId,
                memberId = memberId,
                request = request
            )

            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                val error = response.errorBody()?.string()
                Result.failure(Exception("일정 수정 실패: $error"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

}