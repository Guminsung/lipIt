package com.ssafy.lipit_app.ui.screens.edit_call.weekly_calls

import androidx.lifecycle.ViewModel
import com.ssafy.lipit_app.ui.screens.edit_call.reschedule.WeeklyCallsUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class WeeklyCallsViewModel : ViewModel() {
    private val _state = MutableStateFlow(WeeklyCallsUiState())
    val state: StateFlow<WeeklyCallsUiState> = _state


    fun onIntent(intent: WeeklyCallsIntent) {
        when (intent) {
            is WeeklyCallsIntent.OnDaySelected -> {
                // 요일 선택 처리
            }

            is WeeklyCallsIntent.OnCallSelected -> {
                // 개별 call 선택 처리
            }

            is WeeklyCallsIntent.OnEditSchedule -> {
                // 바텀시트 내부에서 edit 모드로 전환 트리거 (MainScreen의 isEditMode 전환을 호출해야 함)
            }

            is WeeklyCallsIntent.OnChangeVoice -> {

            }

            is WeeklyCallsIntent.SelectFreeMode -> {
                _state.update {
                    it.copy(
                        editState = it.editState.copy(
                            isFreeModeSelected = intent.isSelected,
                            isCategoryModeSelected = !intent.isSelected
                        )
                    )
                }
            }

            is WeeklyCallsIntent.SelectCategory -> {
                _state.update {
                    it.copy(editState = it.editState.copy(
                        selectedCategory = intent.category
                    ))
                }
            }
        }
    }
}