package com.ssafy.lipit_app.ui.screens.call.oncall.text_call

import android.util.Log
import androidx.lifecycle.ViewModel
import com.ssafy.lipit_app.data.model.ChatMessageText
import com.ssafy.lipit_app.ui.screens.call.oncall.voice_call.VoiceCallViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class TextCallViewModel : ViewModel() {
    private val _state = MutableStateFlow(TextCallState())
    val state: StateFlow<TextCallState> = _state

    fun toggleMode() {
        _state.update { it.copy(currentMode = if (it.currentMode == "Text") "Voice" else "Text") }

    }

    fun addMessage(message: ChatMessageText, voiceViewModel: VoiceCallViewModel? = null) {
        Log.d("TextCall", "📥 ViewModel에 메시지 추가됨: $message")

        _state.update { current ->
            current.copy(messages = current.messages + message)
        }

        // VoiceCallViewModel에도 즉시 동기화
        voiceViewModel?.addAiMessage(message.text, message.translatedText)
    }

    fun getMessages(): List<ChatMessageText> {
        return state.value.messages
    }


    fun onIntent(intent: TextCallIntent, onSendToServer: (String) -> Unit = {}) {
        when (intent) {
            // 번역 켜고 끄기
            is TextCallIntent.ToggleTranslation -> {
                _state.update { it.copy(showTranslation = intent.show) }
            }

            // 사용자 입력 반영
            is TextCallIntent.UpdateInputText -> {
                _state.update { it.copy(inputText = intent.text) }
            }

            // 입력된 메시지 보내기
            is TextCallIntent.SendMessage -> {
                val currentText = _state.value.inputText
                if (currentText.isNotBlank()) {
                    val newMessage = ChatMessageText(
                        text = currentText,
                        isFromUser = true
                    )

                    Log.d("TextCallVM", "🧍‍♂️ 사용자 메시지 추가됨: $newMessage")

                    _state.update {
                        it.copy(
                            messages = it.messages + newMessage,
                            inputText = "" // 전송 후 텍스트 초기화
                        )
                    }

                    onSendToServer(currentText)

                }
            }

        }
    }

    fun setInitialMessages(initialMessages: List<ChatMessageText>) {
        _state.update { it.copy(messages = initialMessages) }
    }

    // 사용자 텍스트 입력 관련
    fun onTextInputChanged(newInput: String) {
        _state.update { it.copy(inputText = newInput) }
    }

}