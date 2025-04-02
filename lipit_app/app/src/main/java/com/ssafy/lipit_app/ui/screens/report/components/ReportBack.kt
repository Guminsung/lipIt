package com.ssafy.lipit_app.ui.screens.report.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.ssafy.lipit_app.R
import com.ssafy.lipit_app.data.model.response_dto.report.ReportListResponse

// 카드 뒷면
@Composable
fun ReportBack(
    report: ReportListResponse,
    isVisible: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f) // 가로:세로 = 1:1 비율 설정
            .border(
                BorderStroke(1.dp, color = Color.White),
                shape = RoundedCornerShape(25.dp)
            )
            .background(Color.Transparent, shape = RoundedCornerShape(25.dp))
            .paint(
                painter = painterResource(id = R.drawable.bg_report_back),
                contentScale = ContentScale.FillBounds
            ),
//            .padding(horizontal = 30.dp, vertical = 23.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        if (report.celebVideoUrl != null) {
            VideoPlayer(
                videoUrl = report.celebVideoUrl,
                isLooping = true,
                isVisible = isVisible,
                modifier = Modifier.fillMaxWidth()
            )
        } else {
            Image(
                painter = painterResource(id = R.drawable.avatar_3d),
                contentDescription = "3D Avatar",
                modifier = Modifier
                    .fillMaxSize(),
                contentScale = ContentScale.Fit
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

    }
}