package com.ssafy.lipit_app.ui.screens.report

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.*
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ssafy.lipit_app.R
import com.ssafy.lipit_app.data.model.dto.report.ReportSummary
import com.ssafy.lipit_app.util.CommonUtils.formatDate
import com.ssafy.lipit_app.util.CommonUtils.formatSeconds

@Composable
fun ReportScreen(
    onReportItemClick: (Long) -> Unit,
    onBackClick: () -> Unit
) {

    // 더미 데이터
    val communicationSummaryText = "사용자는 오픽 시험을 준비하며, 다양한 주제에 대한 연습을 원하고, 롤플레이와 피드백을 요청하였다."
    val feedbackSummaryText =
        "AI는 사용자의 발음이나 문법 실수를 지적하며, 예를 들어 \"I go to park\"를 \"I go to the park\"로 수정하도록 제안합니다."
    val createdAt = "2025-03-15"

    val reports = listOf(
        ReportSummary(
            reportId = 101,
            callDuration = 280,
            celebVideoUrl = "https://dlxayir1dj7sa.cloudfront.net/celeb-video/trump.mp4",
            wordCount = 100,
            sentenceCount = 15,
            communicationSummary = communicationSummaryText,
            feedbackSummary = feedbackSummaryText,
            createdAt = createdAt
        ),
        ReportSummary(
            reportId = 101,
            callDuration = 280,
            celebVideoUrl = "http://example.com/video.mp4",
            wordCount = 100,
            sentenceCount = 15,
            communicationSummary = communicationSummaryText,
            feedbackSummary = feedbackSummaryText,
            createdAt = createdAt
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color.Transparent, Color.Transparent)
                ),
                shape = RectangleShape
            )
            .paint(
                painter = painterResource(id = R.drawable.bg_myvoice),
                contentScale = ContentScale.FillBounds
            )
            .padding(top = 24.dp, start = 24.dp, end = 24.dp)
    ) {
        Text(
            text = "Reports",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 30.sp,
            modifier = Modifier.padding(top = 46.dp)
        )

        Spacer(modifier = Modifier.height(26.dp))

        // 리포트 내용
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            items(reports) { report ->
                Report(report, { onReportItemClick(report.reportId) })
            }
        }
    }
}

@Composable
fun Report(report: ReportSummary, onReportItemClick: (Long) -> Unit) {

    // 카드가 뒤집혔는지 상태 저장
    var isFlipped by remember { mutableStateOf(false) }

    // 회전 애니메이션 값 계산
    val rotation by animateFloatAsState(
        targetValue = if (isFlipped) 180f else 0f,
        animationSpec = tween(durationMillis = 500),
        label = "rotationAnimation"
    )

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
                    alpha = if (rotation > 90f) 0f else 1f
                }
        ) {
            ReportFront(report, onReportItemClick)
        }

        // 카드 뒷면 (앞면이 보일 때 숨김)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .graphicsLayer {
                    rotationY = rotation - 180f
                    cameraDistance = 12f * density
                    alpha = if (rotation < 90f) 0f else 1f
                }
        ) {
            ReportBack(report)
        }
    }
}


// 카드 뒷면
@Composable
fun ReportBack(report: ReportSummary) {
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

        // mp4 영상
        VideoPlayer(
            videoUrl = report.celebVideoUrl,
            isLooping = true,
            modifier = Modifier
                .fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

    }
}

// 카드 앞면
@Composable
fun ReportFront(
    report: ReportSummary,
    onReportItemClick: (Long) -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                BorderStroke(1.dp, color = Color.White),
                shape = RoundedCornerShape(25.dp)
            )
            .background(Color.Transparent, shape = RoundedCornerShape(25.dp))
            .paint(
                painter = painterResource(id = R.drawable.bg_report),
                contentScale = ContentScale.FillBounds
            )
            .padding(horizontal = 30.dp, vertical = 23.dp)
    ) {

        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom  // 여기에 baseline 정렬 추가
            ) {
                Text(
                    text = formatDate(report.createdAt),
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )

                Row(
                    verticalAlignment = Alignment.Bottom,
                    modifier = Modifier.clickable {
                        // TODO: ReportDetail로 이동
                        onReportItemClick(report.reportId)
                    }
                ) {
                    Text(
                        "상세보기 ",
                        color = Color.White.copy(0.3f),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Image(
                        painter = painterResource(id = R.drawable.btn_report_detail),
                        contentDescription = null,
                        modifier = Modifier
                            .size(18.dp)
                    )
                }

            }

            Text(
                "착신 통화 ${formatSeconds(report.callDuration)}",
                color = Color.White,
                fontSize = 15.sp,
            )
        }

        Spacer(modifier = Modifier.height(10.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xffFDF8FF), shape = RoundedCornerShape(18.dp))
                .padding(horizontal = 32.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "${report.wordCount}개", fontSize = 14.sp)
                Text(
                    text = "말한 단어 수",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }

            VerticalDivider(
                modifier = Modifier
                    .height(40.dp)
                    .width(2.dp),
                color = Color(0xffD3D3D3)
            )


            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "${report.sentenceCount}개", fontSize = 14.sp)
                Text(
                    text = "말한 문장 수",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 요약 영역
        Column {
            Text(
                "대화 내용 요약",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = report.communicationSummary,
                color = Color.White,
                fontSize = 14.sp,
                lineHeight = 24.sp
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Column {
            Text(
                "AI 피드백 요약",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = report.feedbackSummary,
                color = Color.White,
                fontSize = 14.sp,
                lineHeight = 24.sp
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
fun ReportScreenPreview() {
    ReportScreen(onReportItemClick = {}, onBackClick = {})
}