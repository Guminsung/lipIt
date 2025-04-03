package com.ssafy.lipit_app.ui.screens.report.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import com.ssafy.lipit_app.data.model.response_dto.report.ReportListResponse
import com.ssafy.lipit_app.ui.screens.report.ReportIntent

@Composable
fun Report(report: ReportListResponse,  onReportItemClick: (Long) -> Unit) {

    // 카드가 뒤집혔는지 상태 저장
    var isFlipped by remember { mutableStateOf(false) }

    // 회전 애니메이션 값 계산
    val rotation by animateFloatAsState(
        targetValue = if (isFlipped) 180f else 0f,
        animationSpec = tween(durationMillis = 500),
        label = "rotationAnimation"
    )

    val isFrontVisible = rotation <= 90f
    val isBackVisible = rotation > 90f

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { isFlipped = !isFlipped }
    ) {

        // 카드 앞면 (뒤집혔을 때 숨김)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .graphicsLayer {
                    rotationY = rotation
                    cameraDistance = 12f * density
                    alpha = if (isFrontVisible) 1f else 0f
                }
        ) {
            ReportFront(
                report = report,
                onReportItemClick = onReportItemClick
            )
        }

        // 카드 뒷면 (앞면이 보일 때 숨김)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .graphicsLayer {
                    rotationY = rotation - 180f
                    cameraDistance = 12f * density
                    alpha = if (isBackVisible) 1f else 0f
                }
        ) {
            ReportBack(report, isVisible = isBackVisible)
        }
    }
}