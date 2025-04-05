package com.ssafy.lipit_app.ui.screens.call.oncall.text_call

import androidx.lifecycle.ViewModel
import com.ssafy.lipit_app.data.model.ChatMessageText
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class TextCallViewModel : ViewModel() {
    private val _state = MutableStateFlow(TextCallState())
    val state: StateFlow<TextCallState> = _state

    fun toggleMode() {
        _state.update { it.copy(currentMode = if (it.currentMode == "Text") "Voice" else "Text") }

    }


    fun onIntent(intent: TextCallIntent) {
        when (intent) {
            is TextCallIntent.ToggleTranslation -> {
                _state.value = _state.value.copy(
                    showTranslation = !_state.value.showTranslation
                )
            }
        }
    }

    fun setInitialMessages(initialMessages: List<ChatMessageText>) {
        _state.update { it.copy(messages = initialMessages) }
    }

}