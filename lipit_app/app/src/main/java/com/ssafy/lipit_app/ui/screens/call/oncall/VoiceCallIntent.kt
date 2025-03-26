package com.ssafy.lipit_app.ui.screens.call.oncall

sealed interface VoiceCallIntent {
    //자막 이벤트
    data class SubtitleOn(val showSubtitle: Boolean): VoiceCallIntent
    data class SubtitleOff(val showSubtitle: Boolean): VoiceCallIntent
    
    //번역 이벤트
    data class TranslationOn(val showTranslation: Boolean): VoiceCallIntent
    data class TranslationOff(val showTranslation: Boolean): VoiceCallIntent

}