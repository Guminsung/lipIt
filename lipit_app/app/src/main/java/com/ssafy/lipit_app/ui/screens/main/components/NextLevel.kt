package com.ssafy.lipit_app.ui.screens.main.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// 레벨업 보드 파트
@Composable
fun NextLevel(sentenceCnt: Int, wordCnt: Int, attendanceCnt: Int, attendanceTotal: Int) {
    // 임시 규칙
    // 레벨 1~5 (기준: 출석일, 통화 누적 시간, 통화 횟수)

    // 통화 누적 시간 : 30분 / 90분 / 180분 / 300분 / 그 이상
    // 출석일 : 7일 / 14일 / 21일 / 28일 / 35일 이상
    // 통화 횟수: 6회 / 18회 / 36회 / 60회 / 90회 이상


    val sentencePercent = (sentenceCnt * 100 / 100).coerceAtMost(100)
    val wordPercent = (wordCnt * 100 / 200).coerceAtMost(100)
    val attendancePercent = if (attendanceTotal > 0) (attendanceCnt * 100 / attendanceTotal) else 0

    // 퍼센트 달성률에 따른 컬러 설정
    val sentenceColor = if (sentencePercent >= 50) Color(0xFFD09FE6) else Color(0xFF6D6D6F)
    val wordColor = if (wordPercent >= 50) Color(0xFFD09FE6) else Color(0xFF6D6D6F)
    val attendanceColor = if (attendancePercent == 100) Color(0xFFD09FE6) else Color(0xFF6D6D6F)

    Box(
        modifier = Modifier
            .padding(top = 23.dp, bottom = 15.dp)
    ) {
        // 제목
        Text(
            text = "Next Level",
            style = TextStyle(
                fontSize = 23.sp,
                lineHeight = 45.sp,
                fontWeight = FontWeight(700),
                color = Color(0xFF000000)
            )
        )

    }
}