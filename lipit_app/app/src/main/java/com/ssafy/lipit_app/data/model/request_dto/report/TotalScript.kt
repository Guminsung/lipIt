package com.ssafy.lipit_app.data.model.request_dto.report

data class TotalScript(
    val isAI: Boolean, // true면 진한 보라색, false면 연한 보라색
    val timestamp: String,
    val content: String = "",
    val contentKor: String = ""
)