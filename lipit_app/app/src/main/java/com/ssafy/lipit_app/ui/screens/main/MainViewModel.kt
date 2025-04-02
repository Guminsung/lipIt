package com.ssafy.lipit_app.ui.screens.main

import androidx.lifecycle.ViewModel
import com.ssafy.lipit_app.ui.screens.main.components.DailySentenceManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class MainViewModel : ViewModel() {
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

            // NavGraph에서 처리
            is MainIntent.NavigateToReports,
            is MainIntent.NavigateToMyVoices,
            is MainIntent.NavigateToCallScreen -> {
                // 네비게이션 관련 상태 업데이트
            }
        }
    }
}