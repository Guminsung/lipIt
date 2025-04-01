package com.ssafy.lipit_app.ui.screens.auth.Login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.lipit_app.data.model.request_dto.auth.LoginRequest
import com.ssafy.lipit_app.domain.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.HttpException

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

            is LoginIntent.OnisPasswordVisibleChanged -> {
                val current = _state.value.isPasswordVisible
                _state.value = _state.value.copy(isPasswordVisible = !current)

            }

            is LoginIntent.OnLoginHandled -> {
                _state.value = _state.value.copy(isLoginSuccess = false, errorMessage = null)
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
                _state.value = _state.value.copy(isLoginSuccess = true)

//                val loginData = result.getOrNull() // BaseResponse<LoginResponse>의 data
//                val accessToken = loginData?.accessToken
//                Log.d("LoginViewModel", "받은 accessToken: $accessToken")
//                TokenManager.saveAccessToken(accessToken ?: "")

            } else {
                val exception = result.exceptionOrNull()
                val errorMsg = if (exception is HttpException) {
                    val errorBody = exception.response()?.errorBody()?.string()
                    extractErrorMessages(errorBody)
                } else {
                    exception?.message ?: "알 수 없는 오류가 발생했어요."
                }
                _state.value = _state.value.copy(errorMessage = errorMsg)
            }

        }

    }

    fun extractErrorMessages(errorBody: String?): String {
        if (errorBody.isNullOrEmpty()) return "알 수 없는 오류가 발생했어요."

        return try {
            val json = JSONObject(errorBody)
            val errorsArray = json.optJSONArray("errors")
            if (errorsArray != null && errorsArray.length() > 0) {
                // errors 안에 있는 모든 메시지를 줄바꿈으로 연결
                buildString {
                    for (i in 0 until errorsArray.length()) {
                        val obj = errorsArray.getJSONObject(i)
                        appendLine(obj.optString("message"))
                    }
                }.trim()
            } else {
                json.optString("message", "오류가 발생했습니다.")
            }
        } catch (e: Exception) {
            "에러 메시지를 파싱하는 데 실패했어요."
        }
    }
}