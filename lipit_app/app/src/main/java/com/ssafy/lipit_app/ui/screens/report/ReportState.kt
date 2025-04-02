package com.ssafy.lipit_app.ui.screens.report

import com.ssafy.lipit_app.data.model.request_dto.report.NativeExpression
import com.ssafy.lipit_app.data.model.request_dto.report.ReportScript
import com.ssafy.lipit_app.data.model.response_dto.report.ReportListResponse

data class ReportState(
    // 전체 리포트 목록
    val totalReportList: List<ReportListResponse> = emptyList(),
    // 리포트 스크립트 내용
    val reportScript: List<ReportScript> = emptyList(),
    // 원어민 표현
    val nativeExpression: List<NativeExpression> = emptyList(),


    // 로딩 상태
    val isLoading: Boolean = false,
    // 에러 메시지
    val error: String? = null

)
