package com.ssafy.lipit_app.ui.screens.report.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ssafy.lipit_app.R
import com.ssafy.lipit_app.data.model.response_dto.report.ReportListResponse
import com.ssafy.lipit_app.util.CommonUtils.formatDate
import com.ssafy.lipit_app.util.CommonUtils.formatSeconds

// 카드 앞면
@Composable
fun ReportFront(
    report: ReportListResponse,
    onReportItemClick: (Long) -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
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
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = formatDate(report.createdAt),
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )

                Box(
                    modifier = Modifier
                        .border(
                            1.dp,
                            color = Color.White.copy(0.7f),
                            shape = RoundedCornerShape(20.dp)
                        )
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.White.copy(0.1f), shape = RoundedCornerShape(20.dp))
                        .clickable {
                            onReportItemClick(report.reportId)
                        }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "상세보기 ",
                            color = Color.White.copy(0.6f),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Light
                        )

                        Image(
                            painter = painterResource(id = R.drawable.btn_report_detail),
                            contentDescription = null,
                            modifier = Modifier
                                .size(16.dp)
                        )
                    }
                }

            }

            Text(
                "착신 통화 ${formatSeconds(report.callDuration)}",
                color = Color.White.copy(0.8f),
                fontSize = 15.sp,
                fontWeight = FontWeight.Light
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
                Text(
                    text = "${report.wordCount}개",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Light
                )
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
                Text(
                    text = "${report.sentenceCount}개",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Light
                )
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
                color = Color.White.copy(0.8f),
                fontWeight = FontWeight.Light,
                fontSize = 15.sp,
                lineHeight = 24.sp
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Column {
            Text(
                "AI 피드백 요약",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = report.feedbackSummary,
                color = Color.White.copy(0.8f),
                fontWeight = FontWeight.Light,
                fontSize = 15.sp,
                lineHeight = 24.sp
            )
        }
    }
}