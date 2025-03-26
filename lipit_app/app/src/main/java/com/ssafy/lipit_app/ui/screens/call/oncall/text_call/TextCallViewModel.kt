package com.ssafy.lipit_app.ui.screens.call.oncall.text_call

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class TextCallViewModel : ViewModel() {
    private val _state = MutableStateFlow(TextCallState())
    val state: StateFlow<TextCallState> = _state

    fun onIntent(intent: TextCallIntent){
        when(intent){
            // 사용자 액션 정의
            else -> {}
        }
    }
}