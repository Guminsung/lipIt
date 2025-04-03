package com.ssafy.lipit_app.ui.screens.edit_call.reschedule

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.lipit_app.data.model.request_dto.schedule.ScheduleCreateRequest
import com.ssafy.lipit_app.data.model.response_dto.schedule.TopicCategory
import com.ssafy.lipit_app.domain.repository.ScheduleRepository
import com.ssafy.lipit_app.ui.screens.edit_call.weekly_calls.CallSchedule
import com.ssafy.lipit_app.util.SharedPreferenceUtils
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

            // EditCallScreen.kt 에서 수정 이벤트가 발생하면 실행 된다.
            is EditCallIntent.UpdateSchedule -> {
                Log.d("TAG", "EditCallViewModel Change: EditCallITnetn ${intent.schedule}")
                updateSchedule(intent.schedule, onSuccess)
            }
        }
    }

    private fun createSchedule(schedule: CallSchedule, onSuccess: () -> Unit) {
        viewModelScope.launch {
//            val memberId = schedule.memberId
            val memberId = SharedPreferenceUtils.getMemberId()
            val day = schedule.scheduleDay
            val time = schedule.scheduledTime
            val topic = schedule.topicCategory

            // topicCategory가 null이거나 "자유주제"인 경우 카테고리 없이 요청
            val request = if (topic.isNullOrBlank() || topic == "자유주제") {
                ScheduleCreateRequest(
                    scheduledDay = day,
                    scheduledTime = time
                )
            } else {
                // 카테고리 이름을 영어로 변환하여 요청
                val englishTopic = TopicCategory.fromKorean(topic)?.name
                ScheduleCreateRequest(
                    scheduledDay = day,
                    scheduledTime = time,
                    topicCategory = englishTopic
                )
            }

            val result = scheduleRepository.createSchedule(memberId, request)
            if (result.isSuccess) {
                setScheduleAlarm(schedule)
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

            val topic = schedule.topicCategory
            // "자유주제"면 null, 그 외엔 영어 enum으로 변환
            val englishTopic = if (topic.isNullOrBlank() || topic == "자유주제") {
                null
            } else {
                TopicCategory.fromKorean(topic)?.name // "스포츠" → "SPORTS"
            }


            val request = ScheduleCreateRequest(
                scheduledDay = schedule.scheduleDay,
                scheduledTime = schedule.scheduledTime,
                topicCategory = englishTopic
            )

            Log.d("TAG", "updateSchedule: Send Server Data ${request}")


            // 알람(스케줄) 내역 업데이트 API 실행
            val result = scheduleRepository.updateSchedule(
                callScheduleId = schedule.callScheduleId,
                memberId = schedule.memberId,
                request = request
            )

            if (result.isSuccess) {
                Log.d("TAG", "updateSchedule: Step 1")
                setScheduleAlarm(schedule)
                onSuccess()
            } else {
                println("❌ 일정 수정 실패: ${result.exceptionOrNull()?.message}")
            }
        }
    }


    /**
     *
     * TODO AlarmManager : Add, Update == Set
     * AlarmManager 수정/추가 작업 진행할 부분
     *
     */
    private fun setScheduleAlarm(schedule: CallSchedule) {
        // schedule : 스케쥴 내역 (요일, 시간, 카테고리) => 해당 데이터를 기반으로 알람 설정 진행
        Log.d("TAG", "updateSchedule: Step 3 Here Alarm Setting")

        Log.d("Alarm", "Alarm _---------- 알람 정보 업데이트: $schedule")
    }

}