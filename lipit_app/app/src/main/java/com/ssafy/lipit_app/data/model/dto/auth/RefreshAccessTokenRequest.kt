package com.ssafy.lipit_app.data.model.dto.auth

data class RefreshAccessTokenRequest(
    val memberId: Long,
    val refreshToken: String
)