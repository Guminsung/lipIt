package com.ssafy.lipit_app.data.model.dto.auth

data class TokenRequest(
    val memberId: Long,
    val refreshToken: String
)