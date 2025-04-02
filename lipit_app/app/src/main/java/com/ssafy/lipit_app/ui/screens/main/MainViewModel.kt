package com.ssafy.lipit_app.ui.screens.main

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.lipit_app.data.model.request_dto.auth.LogoutRequest
import com.ssafy.lipit_app.ui.screens.main.components.DailySentenceManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MainViewModel(private val context: Context) : ViewModel() {
    private val _state = MutableStateFlow(MainState())
    val state: StateFlow<MainState> = _state

    init {
        loadDailySentence()
    }

    fun loadDailySentence() {
        val sentenceOriginal = DailySentenceManager.getOriginal().ifBlank {
            "With your talent and hard work, sky’s the limit!"
        }
        val sentenceTranslated = DailySentenceManager.getTranslated().ifBlank {
            "너의 재능과 노력이라면, 한계란 없지!"
        }

        _state.value = _state.value.copy(
            sentenceOriginal = sentenceOriginal,
            sentenceTranslated = sentenceTranslated
        )
    }

    fun onIntent(intent: MainIntent) {
        when (intent) {
            is MainIntent.OnDaySelected -> {
                _state.update {
                    it.copy(selectedDay = intent.day)
                }
            }

            //todo: call 기능 구현 시 수정할 것
            is MainIntent.OnCallClick -> TODO()

            // 로그아웃
            is MainIntent.OnLogoutClicked -> {
                logout(context)
            }

            is MainIntent.OnLogoutHandled -> {
                _state.value = _state.value.copy(isLogoutSuccess = false)
            }

            // NavGraph에서 처리
            is MainIntent.NavigateToReports,
            is MainIntent.NavigateToMyVoices,
            is MainIntent.NavigateToCallScreen -> {
                // 네비게이션 관련 상태 업데이트
            }
        }
    }

    private fun logout(context: Context) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLogoutClicked = true)

            // SecureDataStore에서 refreshToken 가져오기
            val refreshToken = com.ssafy.lipit_app.base.SecureDataStore
                .getInstance(context)
                .getRefreshToken()

            Log.d("auth", "logout: refreshToken - $refreshToken")

            // 로그아웃 요청 생성하기
            val request = LogoutRequest(refreshToken.toString())

            // 서버에 로그아웃 요청하기
            val result = com.ssafy.lipit_app.domain.repository.AuthRepository().logout(request)

            if (result.isSuccess) {
                // 성공 시 사용자 정보 삭제
                com.ssafy.lipit_app.base.SecureDataStore.getInstance(context).clearUserInfo()

                _state.value = _state.value.copy(isLogoutSuccess = true)
            } else{
                _state.value = _state.value.copy(isLogoutSuccess = true)

            }
        }
    }
}