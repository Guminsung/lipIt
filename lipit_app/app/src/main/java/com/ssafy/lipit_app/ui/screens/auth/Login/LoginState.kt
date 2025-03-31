package com.ssafy.lipit_app.ui.screens.auth.Login

data class LoginState(
    val id: String = "",
    val pw: String = "",

    val isLoginClicked: Boolean = false,
    val isLoginSuccess: Boolean = false,
    val errorMessage: String? = null
)