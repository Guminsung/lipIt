package com.ssafy.lipit_app.ui.screens.auth.Login

sealed class LoginIntent {
    data class onIdChanged(val id: String) : LoginIntent()
    data class onPwChanged(val pw: String) : LoginIntent()

    object OnLoginClicked : LoginIntent()
}