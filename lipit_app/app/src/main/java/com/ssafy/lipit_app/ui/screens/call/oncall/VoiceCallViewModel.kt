package com.ssafy.lipit_app.ui.screens.call.oncall

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class VoiceCallViewModel : ViewModel() {
    private val _state = MutableStateFlow(VoiceCallState())
    val state: StateFlow<VoiceCallState> = _state

    fun onIntent(intent:VoiceCallIntent){
        when(intent){
            is VoiceCallIntent.SubtitleOn ->{ // 자막 O, 번역 X
                _state.update {
                    it.copy(showTranslation = true, showSubtitle = false)
                }
            }

            is VoiceCallIntent.SubtitleOff -> { // 자막 X, 번역 X
                _state.update {
                    it.copy(showTranslation = false, showSubtitle = false)
                }
            }
            
            is VoiceCallIntent.TranslationOff -> { // 자막 O, 번역 X
                _state.update {
                    it.copy(showTranslation = true, showSubtitle = false)
                }
            }
            
            is VoiceCallIntent.TranslationOn -> { // 자막 O, 번역 O
                _state.update {
                    it.copy(showTranslation = true, showSubtitle = true)
                }
            }
        }
    }
}