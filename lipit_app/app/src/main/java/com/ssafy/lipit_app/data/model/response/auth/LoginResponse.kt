package com.ssafy.lipit_app.data.model.response.auth

data class LoginResponse(
    val memberId: Long,
    val email: String,
    val username: String,
    val accessToken: String,
    val refreshToken: String
)