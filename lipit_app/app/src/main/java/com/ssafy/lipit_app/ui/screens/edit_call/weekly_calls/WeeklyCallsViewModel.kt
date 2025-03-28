package com.ssafy.lipit_app.ui.screens.edit_call.weekly_calls

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class WeeklyCallsViewModel : ViewModel() {
    private val _state = MutableStateFlow(WeeklyCallsState())
    val state: StateFlow<WeeklyCallsState> = _state

    fun onIntent(intent: WeeklyCallsIntent){
        when(intent){
            //
        }
    }
}