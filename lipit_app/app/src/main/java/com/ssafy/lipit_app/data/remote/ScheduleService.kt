package com.ssafy.lipit_app.data.remote

import com.ssafy.lipit_app.data.model.request_dto.schedule.ScheduleRequest
import com.ssafy.lipit_app.data.model.response_dto.BaseResponse
import com.ssafy.lipit_app.data.model.response_dto.schedule.ScheduleResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET

interface ScheduleService {
    // 일정 삭제
    
    // 전체 일정 조회
    @GET("schedule/")
    suspend fun schedule(@Body request: ScheduleRequest): Response<BaseResponse<ScheduleResponse>>
    
    // 부재중 개수 조회
    
    // 오늘의 일정 조회
    
    // 일정 수정
    
    // 전화 수신 거절
    
    // 일정 생성
}