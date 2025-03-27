package com.ssafy.lipit_app.ui.screens.edit_call.reschedule

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class EditWeeklyCallsViewModel : ViewModel() {
    private val _state = MutableStateFlow(EditWeeklyCallsState())
    val state: StateFlow<EditWeeklyCallsState> = _state

    fun onIntent(intent:EditWeeklyCallsIntent){
        when(intent){
            //
        }
    }
}