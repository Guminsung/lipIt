package com.ssafy.lipit_app.ui.screens.auth.Login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.lipit_app.data.model.dto.auth.LoginRequest
import com.ssafy.lipit_app.domain.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {
    private val _state = MutableStateFlow(LoginState())
    val state: StateFlow<LoginState> = _state

    private val authRepository by lazy { AuthRepository() }

    fun onIntent(intent: LoginIntent) {
        when (intent) {
            // id 변경
            is LoginIntent.onIdChanged -> {
                _state.value = _state.value.copy(id = intent.id)
            }

            // pw 변경
            is LoginIntent.onPwChanged -> {
                _state.value = _state.value.copy(pw = intent.pw)
            }

            // login 버튼 눌렸을 경우
            is LoginIntent.OnLoginClicked -> {
                login()
            }
        }

    }

    private fun login() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoginClicked = true)

            val request = LoginRequest(
                email = _state.value.id,
                password = _state.value.pw
            )

            val result = authRepository.login(request)

            if (result.isSuccess) {
                // 로그인 성공 상태 반영
                _state.value = _state.value.copy(isLoginSuccess = true)

            } else {
                // 로그인 실패 상태 반영
                _state.value = _state.value.copy(errorMessage = result.exceptionOrNull()?.message)
            }
        }
    }
}