package com.ssafy.lipit_app.ui.screens.report

sealed class ReportIntent {

    object LoadReportList: ReportIntent()  // 전체 리포트 목록

    data class ReportItemClicked(val reportId: Long) : ReportIntent() // 리포트 클릭
    data class LoadReportScript(val reportId: Long) : ReportIntent() // 리포트 스크립트 목록
    data class LoadNativeExpression(val reportId: Long): ReportIntent() // 원어민 표현

    data class NavigateToReportDetail(val reportId: Long) : ReportIntent()
}