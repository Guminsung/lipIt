package com.ssafy.lipit_app.ui.screens.report

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
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ssafy.lipit_app.R

@Composable
fun SummaryContent() {

    // 카드 앞면 (뒤집혔을 때 숨김)
    Box(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        ReportContent()
    }

}

@Composable
fun ReportContent() {

    Column(
        modifier = Modifier
            .fillMaxWidth()
//            .aspectRatio(1f)
            .border(
                BorderStroke(1.dp, color = Color.White),
                shape = RoundedCornerShape(25.dp)
            )
            .background(Color.Transparent, shape = RoundedCornerShape(25.dp))
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
                    text = "2025년 03월 20일",
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )


            }

            Text(
                "착신 통화 4분 20초",
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
                Text(text = "100개", fontSize = 14.sp)
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
                Text(text = "15개", fontSize = 14.sp)
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
                "사용자는 오픽 시험을 준비하며, 다양한 주제에 대한 연습을 원하고, 롤플레이와 피드백을 요청하였다.",
                color = Color.White,
                fontSize = 14.sp,
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
                "AI는 사용자의 발음이나 문법 실수를 지적하며, 예를 들어 \"I go to park\"를 \"I go to the park\"로 수정하도록 제안합니다.",
                color = Color.White,
                fontSize = 14.sp,
            )
        }

    }

}


@Composable
@Preview(showBackground = true)
fun SummaryPreview() {
    SummaryContent()
}