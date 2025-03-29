package com.ssafy.lipit_app.ui.screens.edit_call.add_voice

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AddVoiceViewModel : ViewModel() {
    private val _state = MutableStateFlow(AddVoiceState())
    val state: StateFlow<AddVoiceState> = _state

    private var timerJob: Job? = null

    fun startRecording() {
        if (_state.value.isRecording) return

        _state.update { it.copy(isRecording = true, secondsRemaining = 30) }

        timerJob = viewModelScope.launch {
            while (_state.value.secondsRemaining > 0) {
                delay(1000L)
                _state.update { current ->
                    current.copy(secondsRemaining = current.secondsRemaining - 1)
                }
            }
        }
    }

    fun stopRecording() {
        timerJob?.cancel()
        _state.update { it.copy(isRecording = false) }
    }

    fun nextSentence() {
        val current = _state.value
        if (current.currentSentenceIndex < current.sentenceList.lastIndex) {
            _state.update { it.copy(currentSentenceIndex = it.currentSentenceIndex + 1) }
        } else {
            // todo: 마지막 문장까지 끝났으면 목소리 생성으로 넘어가기
            _state.update { it.copy(isRecording = false) }
        }
    }


    fun onIntent(intent: AddVoiceIntent) {
        fun onIntent(intent: AddVoiceIntent) {
            when (intent) {
                is AddVoiceIntent.StartRecording -> startRecording()
                is AddVoiceIntent.StopRecording -> stopRecording()
                is AddVoiceIntent.NextSentence -> nextSentence()
            }
        }
    }
}