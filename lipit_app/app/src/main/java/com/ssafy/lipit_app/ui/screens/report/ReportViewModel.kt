package com.ssafy.lipit_app.ui.screens.report

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.lipit_app.domain.repository.ReportRepository
import com.ssafy.lipit_app.util.SharedPreferenceUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val TAG = "ReportViewModel"

class ReportViewModel : ViewModel() {

    private val _state = MutableStateFlow(ReportState())
    val state: StateFlow<ReportState> = _state.asStateFlow()

    private val reportRepository = ReportRepository()

    // 멤버 ID 가져오기
    private val memberId: Long by lazy {
        SharedPreferenceUtils.getMemberId()
    }

    init {
        loadReportList()
    }

    fun onIntent(intent: ReportIntent) {
        when (intent) {
            is ReportIntent.LoadReportList -> loadReportList()
            is ReportIntent.LoadReportScript -> loadReportScript(intent.reportId)
            is ReportIntent.LoadNativeExpression -> loadNativeExpression(intent.reportId)
            is ReportIntent.ReportItemClicked -> {}
            is ReportIntent.NavigateToReportDetail -> {}
        }
    }

    // 전체 리포트
    private fun loadReportList() {
        viewModelScope.launch {
            try {
                _state.update { it.copy(isLoading = true, error = null) }

                val totalReport = reportRepository.getReportList(memberId)
                Log.d(TAG, "loadReportList: $memberId $totalReport")

                totalReport.onSuccess { data ->
                    _state.update { currentState ->
                        currentState.copy(
                            totalReportList = data,
                            isLoading = false,
                            error = null
                        )
                    }
                }.onFailure { error ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = error.message ?: "리포트 전체 목록을 불러올 수 없습니다."
                        )
                    }
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "리포트 전체 목록을 불러오는 중 오류가 발생했습니다."
                    )
                }
            }
        }
    }

    // 스크립트 불러오기
    private fun loadReportScript(reportId: Long) {
        viewModelScope.launch {
            try {
                _state.update { it.copy(isLoading = true, error = null) }

                val result = reportRepository.getReportScript(reportId)
                result.onSuccess { scripts ->
                    _state.update { currentState ->
                        currentState.copy(
                            reportScript = scripts,
                            isLoading = false,
                            error = null
                        )
                    }
                }.onFailure { error ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = error.message ?: "스크립트 목록을 불러오던 중 오류 발생"
                        )
                    }
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "스크립트 목록을 불러오던 중 오류 발생"
                    )
                }
            }
        }
    }

    // 원어민 표현
    private fun loadNativeExpression(reportId: Long) {

        viewModelScope.launch {
            try {
                _state.update { it.copy(isLoading = true, error = null) }

                val result = reportRepository.getNativeExpressions(reportId)
                result.onSuccess { natives ->
                    _state.update { currentState ->
                        currentState.copy(
                            nativeExpression = natives,
                            isLoading = false,
                            error = null
                        )
                    }
                }.onFailure { error ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = error.message ?: "원어민 표현을 불러오던 중 오류 발생"
                        )
                    }
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "원어민 표현을 불러오던 중 오류 발생"
                    )
                }
            }
        }

    }

}