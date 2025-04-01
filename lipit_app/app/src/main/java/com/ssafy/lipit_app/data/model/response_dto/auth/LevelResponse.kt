package com.ssafy.lipit_app.data.model.response_dto.auth

data class LevelResponse(
    val level: Int,
    val badgeIcon: String,
    val totalCalDurationPercentage: Int,
    val totalReportCountPercentage: Int
)