package com.ssafy.lipit_app.ui.screens.call.oncall

import android.graphics.drawable.Icon

sealed interface VoiceCallIntent {
    //자막 이벤트
    data class SubtitleOn(val icon: Icon): VoiceCallIntent
    data class SubtitleOff(val icon: Icon): VoiceCallIntent
    
    //번역 이벤트
    data class TranslationOn(val icon: Icon): VoiceCallIntent
    data class TranslationOff(val icon: Icon): VoiceCallIntent

}