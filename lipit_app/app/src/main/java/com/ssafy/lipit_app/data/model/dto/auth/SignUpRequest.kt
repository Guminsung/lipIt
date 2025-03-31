package com.ssafy.lipit_app.data.model.dto.auth

data class SignUpRequest(
    val email: String,
    val password1: String,
    val password2: String,
    val username: String,
    val gender: String? = null
)