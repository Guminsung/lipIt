package com.ssafy.lipit_app.ui.screens.main.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ssafy.lipit_app.ui.screens.main.BadgeWithText

// 레벨업 보드 파트
@Composable
fun NextLevel(sentenceCnt: Int, wordCnt: Int, attendanceCnt: Int, attendanceTotal: Int) {
    // 임시 규칙1 : 달성률 50% 이상이면 보라색 원, 50% 아래면 회색 원
    // 임시 규칙2 : 2레벨로 가기 위해 필요한 조건
    // - 필요 문장 : 100개
    // - 필요 단어 : 200개
    // => 생성 가능한 셀럽 보이스 개수에 따라 규칙 정하기

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
            text = "Level Up",
            style = TextStyle(
                fontSize = 23.sp,
                lineHeight = 45.sp,
                fontWeight = FontWeight(700),
                color = Color(0xFF000000)
            )
        )

    }

    // 내용
    Box(
        Modifier
            // 커스텀 그림자 생성
            .graphicsLayer {
                shadowElevation = 0f
                shape = RoundedCornerShape(15.dp)
                clip = false
            }
            .drawBehind {
                drawIntoCanvas { canvas ->
                    val paint = Paint().apply {
                        color = Color(0x14000000)
                        asFrameworkPaint().maskFilter =
                            android.graphics.BlurMaskFilter(
                                30f,
                                android.graphics.BlurMaskFilter.Blur.OUTER
                            )
                    }

                    //그림자 살짝 위로 이동시킴
                    canvas.drawRoundRect(
                        left = 0f,
                        top = 15f,
                        right = size.width,
                        bottom = size.height,
                        radiusX = 30f,
                        radiusY = 30f,
                        paint = paint
                    )
                }
            }
            .fillMaxWidth()
            .height(150.dp)
            .background(
                color = Color(0xFFFFFFFF),
                shape = RoundedCornerShape(size = 15.dp)
            )
            .padding(vertical = 17.dp, horizontal = 20.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.Start,
            modifier = Modifier.fillMaxSize()
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // 필요 문항 수
                BadgeWithText(
                    circleText = "$sentencePercent %",
                    label = "필요 문장 수",
                    value = "${100 - sentenceCnt}개",
                    color = sentenceColor
                )

                Spacer(modifier = Modifier.width(25.dp))

                // 필요 단어 수
                BadgeWithText(
                    circleText = "$wordPercent %",
                    label = "필요 단어 수",
                    value = "${200 - wordCnt}",
                    color = wordColor
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 출석률
            BadgeWithText(
                circleText = if (attendancePercent == 100) "MAX" else "$attendancePercent %",
                label = "출석률",
                value = "$attendanceCnt / $attendanceTotal",
                color = attendanceColor
            )
        }

    }

}