package com.ssafy.lipit_app.ui.screens.main

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.lipit_app.data.model.response_dto.schedule.TopicCategory
import com.ssafy.lipit_app.domain.repository.ScheduleRepository
import com.ssafy.lipit_app.ui.screens.edit_call.weekly_calls.CallSchedule
import com.ssafy.lipit_app.ui.screens.edit_call.weekly_calls.WeeklyCallsState
import com.ssafy.lipit_app.util.sortSchedulesByDay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    private val _state = MutableStateFlow(MainState())
    val state: StateFlow<MainState> = _state

    private val scheduleRepository by lazy { ScheduleRepository() }

    fun onIntent(intent:MainIntent){
        when(intent){
            is MainIntent.OnDaySelected ->{
                _state.update {
                    it.copy(selectedDay = intent.day)
                }
            }

            //todo: call 기능 구현 시 수정할 것
            is MainIntent.OnCallClick -> TODO()

            // NavGraph에서 처리
            is MainIntent.NavigateToReports,
            is MainIntent.NavigateToMyVoices,
            is MainIntent.NavigateToCallScreen -> {
                // 네비게이션 관련 상태 업데이트
            }

            // BottomSheet: 상태관리
            is MainIntent.OnSettingsClicked -> {
                getWeeklyCallsSchedule()
            }
            is MainIntent.OnCloseSettingsSheet -> {
                _state.update { it.copy(isSettingsSheetVisible = false) }
            }
            is MainIntent.ResetBottomSheetContent -> {
                _state.update {
                    it.copy(bottomSheetContent = BottomSheetContent.WEEKLY_CALLS)
                }
            }

            // BottomSheet: 종류 변경
            is MainIntent.ShowWeeklyCallsScreen -> {
                _state.update { it.copy(bottomSheetContent = BottomSheetContent.WEEKLY_CALLS) }
            }
            // 스케줄 변경할 아이템 선택
            is MainIntent.SelectSchedule -> {
                _state.update {
                    it.copy(selectedSchedule = intent.schedule)
                }
            }
            is MainIntent.ShowRescheduleScreen -> {
                _state.update {
                    it.copy(
                        bottomSheetContent = BottomSheetContent.RESCHEDULE_CALL,
                        selectedSchedule = intent.schedule
                    )
                }
            }
            is MainIntent.ShowMyVoicesScreen -> {
                _state.update { it.copy(bottomSheetContent = BottomSheetContent.MY_VOICES) }

            }

            is MainIntent.DeleteSchedule -> {
                deleteScheduleAndReload(intent.scheduleId)
            }

            is MainIntent.ScheduleChanged -> {
                getWeeklyCallsSchedule()
            }

            else -> {

            }
        }
    }

    // [Weekly Calls: BottomSheet] 요일별 스케쥴 리스트
    private fun getWeeklyCallsSchedule() {
        // TODO : 로그아웃 기능 구현 되면 수정
//        val memberId = SharedPreferenceUtils.getMemberId()
        val memberId: Long = 1

        viewModelScope.launch {
            try {
                val response = scheduleRepository.getWeeklyCallsSchedule(memberId = memberId)

                response.onSuccess { scheduleList ->
                    // 1. ScheduleResponse → CallSchedule로 변환
                    val convertedSchedules = scheduleList.map { schedule ->
                        CallSchedule(
                            callScheduleId = schedule.callScheduleId,
                            memberId = memberId,
                            scheduleDay = schedule.scheduledDay,
                            scheduledTime = schedule.scheduledTime,
                            topicCategory = TopicCategory.fromEnglish(schedule.topicCategory)?.koreanName ?: "기타"
                        )
                    }

                    // 2. 요일 정렬 유틸 적용
                    val sortedSchedules = sortSchedulesByDay(convertedSchedules)

                    // 3. 상태 업데이트
                    _state.update {
                        it.copy(
                            isSettingsSheetVisible = true,
                            weeklyCallsState = WeeklyCallsState(
                                VoiceName = "Harry Potter2", // TODO: 서버 연동 시 교체
                                VoiceImageUrl = "...",
                                callSchedules = sortedSchedules
                            )
                        )
                    }
                }.onFailure {
                    println("스케줄 조회 실패: ${it.message}")
                }

            } catch (e: Exception) {
                println("예외 발생: ${e.message}")
            }
        }
    }

    private fun deleteScheduleAndReload(scheduleId: Long) {
//        val memberId = SharedPreferenceUtils.getMemberId()
        val memberId = 1L

        viewModelScope.launch {
            val result = scheduleRepository.deleteSchedule(
                callScheduleId = scheduleId.toLong(),
                memberId = memberId
            )

            if (result.isSuccess) {
                getWeeklyCallsSchedule() // 삭제 후 다시 조회
            } else {
                Log.e("MainViewModel", "삭제 실패: ${result.exceptionOrNull()?.message}")
            }
        }
    }



}