package com.ssafy.lipit_app.ui.screens.main

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class MainViewModel : ViewModel() {
    private val _state = MutableStateFlow(MainState())
    val state: StateFlow<MainState> = _state

    fun onIntent(intent:MainIntent){
        when(intent){
            is MainIntent.OnDaySelected ->{
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

            // [Weekly Calls] BottomSheet 이벤트
            is MainIntent.OnSettingsClicked -> {
                _state.update { it.copy(isSettingsSheetVisible = true) }
            }
            is MainIntent.OnCloseSettingsSheet -> {
                _state.update { it.copy(isSettingsSheetVisible = false) }
            }

            else -> {

            }
        }
    }
}