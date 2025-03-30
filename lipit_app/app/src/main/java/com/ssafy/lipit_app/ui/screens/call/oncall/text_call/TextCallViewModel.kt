package com.ssafy.lipit_app.ui.screens.call.oncall.text_call

import androidx.lifecycle.ViewModel
import com.ssafy.lipit_app.ui.screens.call.oncall.text_call.components.ChatMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class TextCallViewModel : ViewModel() {
    val sampleChatMessages = listOf(
        ChatMessage(
            text = "Hey! Long time no see! How have you been?",
            translatedText = "오! 오랜만이야! 잘 지냈어?",
            isFromUser = false
        ),
        ChatMessage(
            text = "Yeah! I’ve been good. I recently started reading a new book.",
            translatedText = "응! 잘 지냈어. 최근에 새 책 읽기 시작했어.",
            isFromUser = true
        ),
        ChatMessage(
            text = "Sounds interesting! What book is it?",
            translatedText = "재미있겠다! 무슨 책이야?",
            isFromUser = false
        ),
        ChatMessage(
            text = "It’s called 'The Night Circus'. The story is magical!",
            translatedText = "『나이트 서커스』라는 책이야. 정말 마법 같은 이야기야!",
            isFromUser = true
        ),
        ChatMessage(
            text = "Nice! I’ll check it out later.",
            translatedText = "좋아! 나중에 꼭 읽어볼게.",
            isFromUser = false
        )
    )


    private val _state = MutableStateFlow(
        TextCallState(
            voiceName = "Harry Potter",
            leftTime = "04:50",
            currentMode = "Text",
            messages = sampleChatMessages,
            inputText = "",
            showTranslation = true
        )
    )

    val state: StateFlow<TextCallState> = _state

    fun onIntent(intent: TextCallIntent) {
        when (intent) {
            is TextCallIntent.ToggleTranslation -> {
                _state.value = _state.value.copy(
                    showTranslation = !_state.value.showTranslation
                )
            }
        }
    }
}