package com.ssafy.lipit_app.data.model.response.report

data class Script(
    val isAI: Boolean, // true면 진한 보라색, false면 연한 보라색
    val timestamp: String,
    val content: String = "",
    val contentKorean: String = ""
)