package com.ssafy.lipit_app.ui.screens.edit_call.weekly_calls

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.lipit_app.ui.screens.edit_call.reschedule.WeeklyCallsUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

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


//    private fun deleteScheduleById(callScheduleId: Long) {
//        // TODO : 로그아웃 기능 구현 되면 수정
////        val memberId = SharedPreferenceUtils.getMemberId()
//        val memberId: Long = 1
//
//        viewModelScope.launch {
//            try {
//                val response = scheduleRepository.deleteSchedule(callScheduleId, memberId)
//                if (response.isSuccess) {
//                    // 삭제 성공했으면 현재 리스트에서 제거
//                    _state.update { current ->
//                        val updatedList = current.weeklyCallsState.callSchedules.filterNot {
//                            it.callScheduleId == callScheduleId
//                        }
//                        current.copy(
//                            weeklyCallsState = current.weeklyCallsState.copy(
//                                callSchedules = updatedList
//                            )
//                        )
//                    }
//                } else {
//                    Log.e("Schedule", "삭제 실패")
//                }
//            } catch (e: Exception) {
//                Log.e("Schedule", "예외 발생: ${e.message}")
//            }
//        }
//    }
}