package com.ssafy.lipit_app.ui.screens.report

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

val purpleDark = Color(0xFF603981)
val purpleLight = Color(0xFFE7D1F4)

@Composable
fun NativeSpeakerContent() {

    Box(
        modifier = Modifier
            .fillMaxWidth()
    ) {

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            val dummyItems = List(3) { index -> index }
            items(dummyItems) { _ ->
                NativeContent()
            }
        }

    }

}


@Composable
fun NativeContent() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                BorderStroke(1.dp, color = Color.White),
                shape = RoundedCornerShape(25.dp)
            )
            .background(Color.Transparent, shape = RoundedCornerShape(25.dp))
//            .padding(horizontal = 30.dp, vertical = 23.dp)
    ) {
        // 제목과 번역
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    purpleLight,
                    shape = RoundedCornerShape(topStart = 25.dp, topEnd = 25.dp)
                )
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            Text(
                text = "incorporate AI features",
                color = purpleDark,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 24.sp
            )

            Text(
                "AI 기능을 포함하다.",
                color = purpleDark,
                fontWeight = FontWeight.Light,
                fontSize = 15.sp,
                lineHeight = 24.sp
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 대화 내용 요약
        Column(
            modifier = Modifier
                .padding(start = 30.dp, end = 30.dp, bottom = 23.dp)
        ) {
            Text(
                "나의 문장",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                "I want to include AI features like hearing. I want to include AI features like hearing.",
                color = Color.White,
                fontSize = 14.sp,
                lineHeight = 24.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            // AI 피드백 요약
            Text(
                "AI 추천 문장",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                "I want to incorporate AI features such as auditory capabilities.",
                color = Color.White,
                fontSize = 14.sp,
                lineHeight = 24.sp
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
fun NativeContentPreview() {
    NativeContent()
}