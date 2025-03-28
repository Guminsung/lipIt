package com.ssafy.lipit_app.data.model.response.report

data class NativeExpression(
    val nativeExpressionId: Int,
    val mySentence: String,
    val aISentence: String,
    val keyword: String,
    val keywordKorean: String
)