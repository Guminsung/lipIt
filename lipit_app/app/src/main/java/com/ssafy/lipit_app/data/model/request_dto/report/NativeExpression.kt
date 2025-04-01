package com.ssafy.lipit_app.data.model.request_dto.report

data class NativeExpression(
    val nativeExpressionId: Int,
    val mySentence: String,
    val aISentence: String,
    val keyword: String,
    val keywordKorean: String
)