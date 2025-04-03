package com.ssafy.lipit_app.ui.screens.edit_call.reschedule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.lipit_app.domain.repository.ScheduleRepository
import com.ssafy.lipit_app.ui.screens.edit_call.weekly_calls.CallSchedule
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class EditCallViewModel : ViewModel(){
    private val _state = MutableStateFlow(EditCallState())
    val state: StateFlow<EditCallState> = _state

    private val scheduleRepository by lazy { ScheduleRepository() }


    fun onIntent(intent: EditCallIntent, onSuccess: () -> Unit = {}) {
        when(intent){
            is EditCallIntent.SelectCategory -> TODO()
            is EditCallIntent.SelectFreeMode -> TODO()

            is EditCallIntent.CreateSchedule -> {
                createSchedule(intent.schedule, onSuccess)
            }
            is EditCallIntent.UpdateSchedule -> {
                updateSchedule(intent.schedule, onSuccess)
            }
        }
    }

    private fun createSchedule(schedule: CallSchedule, onSuccess: () -> Unit) {
        viewModelScope.launch {
            val memberId = schedule.memberId
            val day = schedule.scheduleDay
            val time = schedule.scheduledTime
            val topic = schedule.topicCategory

            val result = scheduleRepository.createSchedule(
                memberId = memberId,
                scheduleDay = day,
                scheduledTime = time,
                topicCategory = topic
            )

            if (result.isSuccess) {
                println("✅ 일정 추가 성공")
                // 성공 시 필요한 콜백이나 이벤트 전파 필요 시 여기서 처리
                onSuccess()
            } else {
                println("❌ 일정 추가 실패: ${result.exceptionOrNull()?.message}")
            }
        }
    }

    private fun updateSchedule(schedule: CallSchedule, onSuccess: () -> Unit) {
        viewModelScope.launch {
            val memberId = schedule.memberId
            val scheduleId = schedule.callScheduleId
            val day = schedule.scheduleDay
            val time = schedule.scheduledTime
            val topic = schedule.topicCategory

            val result = scheduleRepository.updateSchedule(
                callScheduleId = scheduleId,
                memberId = memberId,
                scheduleDay = day,
                scheduledTime = time,
                topicCategory = topic
            )

            if (result.isSuccess) {
                println("✅ 일정 수정 성공")
                onSuccess()
            } else {
                println("❌ 일정 수정 실패: ${result.exceptionOrNull()?.message}")
            }
        }
    }

}