package com.ssafy.lipit_app.data.remote

import com.ssafy.lipit_app.data.model.request_dto.myvoice.VoiceRequest
import com.ssafy.lipit_app.data.model.response_dto.BaseResponse
import com.ssafy.lipit_app.data.model.response_dto.myvoice.CelabResponse
import com.ssafy.lipit_app.data.model.response_dto.myvoice.CustomResponse
import com.ssafy.lipit_app.data.model.response_dto.myvoice.VoiceResponse
import com.ssafy.lipit_app.data.model.response_dto.myvoice.VoiceTakeResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface MyVoiceService {

    // 셀럽 음성 목록 조회
    @GET("voices/celeb")
    suspend fun getCelebVoices(
        @Query("memberId") memberId: Long
    ): Response<BaseResponse<List<CelabResponse>>>


    // 커스텀 음성 목록 조회
    @GET("voices/custom")
    suspend fun getCustomVoices(
        @Query("memberId") memberId: Long
    ): Response<BaseResponse<List<CustomResponse>>>


    // 선택한 음성 조회
    @GET("members/{memberId}/voice")
    suspend fun getVoice(
        @Query("memberId") memberId: Long
    ): Response<BaseResponse<CustomResponse>>


    // 기본 음성 선택
    @PATCH("members/{memberId}/voice")
    suspend fun takeVoice(
        @Path("memberId") memberId: Long,
        @Query("voiceId") voiceId: Long
    ): Response<BaseResponse<VoiceTakeResponse>>


    // 커스텀 녹음 저장
    @POST("voices/recording")
    suspend fun saveCustomVoice(
        @Body voiceRequest: VoiceRequest
    ): Response<BaseResponse<VoiceResponse>>

}