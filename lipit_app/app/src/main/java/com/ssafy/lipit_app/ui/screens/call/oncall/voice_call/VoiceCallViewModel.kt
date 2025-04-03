package com.ssafy.lipit_app.ui.screens.call.oncall.voice_call

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class VoiceCallViewModel : ViewModel() {
    private val _state = MutableStateFlow(VoiceCallState())
    val state: StateFlow<VoiceCallState> = _state

    // 남은 시간 카운트 관련
    private var timerJob: Job? = null

    fun onIntent(intent: VoiceCallIntent) {
        when (intent) {
            is VoiceCallIntent.SubtitleOn -> { // 자막 O, 번역 X
                _state.update {
                    it.copy(showSubtitle = true, showTranslation = false)
                }
            }

            is VoiceCallIntent.SubtitleOff -> { // 자막 X, 번역 X
                _state.update {
                    it.copy(showSubtitle = false, showTranslation = false)
                }
            }

            is VoiceCallIntent.TranslationOff -> { // 자막 O, 번역 X
                _state.update {
                    it.copy(showSubtitle = true, showTranslation = false)
                }
            }

            is VoiceCallIntent.TranslationOn -> { // 자막 O, 번역 O
                _state.update {
                    it.copy(showSubtitle = true, showTranslation = true)
                }
            }

            // 타이머 종료 후
            is VoiceCallIntent.timerIsOver ->{
                _state.update {
                    it.copy(isLoading = true)
                }

                viewModelScope.launch {
                    delay(2000L) // 리포트 생성 대기 시간
                    _state.update{
                        it.copy(isFinished = true)
                    }
                }
            }
        }
    }

    // 남은 시간 카운트
    @SuppressLint("DefaultLocale")
    fun startCountdown(initialSeconds: Int = 300) {
        timerJob?.cancel() // 기존에 타이머가 있다면 정지시킴

        timerJob = viewModelScope.launch {
            var remaining = initialSeconds
            while (remaining >= 0) {
                val minutes = remaining / 60
                val seconds = remaining % 60
                val timeString = String.format("%02d:%02d", minutes, seconds)

                _state.update { it.copy(leftTime = timeString) }

                delay(1000L) // 1초 기다리고 text에 반영
                remaining--

                // 5분이 종료되면 로딩 화면 출력(리포트 생성 중.. or 리포트 생성 실패!) 후 Main으로 돌아가기
                if(remaining == 0){
                    onIntent(VoiceCallIntent.timerIsOver)
                }
            }
        }
    }

    fun stopCountdown() {
        timerJob?.cancel()
    }


}