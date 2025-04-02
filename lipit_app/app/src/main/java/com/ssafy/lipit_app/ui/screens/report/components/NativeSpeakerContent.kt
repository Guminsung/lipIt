package com.ssafy.lipit_app.ui.screens.report.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ssafy.lipit_app.data.model.request_dto.report.NativeExpression

val purpleDark = Color(0xFF603981)
val purpleLight = Color(0xFFE7D1F4)


@Composable
fun NativeSpeakerContent(nativeExpressions: List<NativeExpression>) {

    /**
     *     "nativeExpressions": [
     *       {
     *         "nativeExpressionId": 1,
     *         "mySentence": "How are you?",
     *         "AISentence": "How have you been?",
     *         "keyword": "greeting",
     *         "keywordKorean": "인사"
     *       }
     *     ]
     */

    // 더미 데이터
    val nativeExpressions = listOf(
        NativeExpression(
            nativeExpressionId = 1,
            mySentence = "I want to include AI features like hearing. I want to include AI features like hearing.",
            aISentence = "I want to incorporate AI features such as auditory capabilities.",
            keyword = "incorporate AI features",
            keywordKorean = "AI 기능을 포함하다"
        ),
        // Add more sample items if needed
        NativeExpression(
            nativeExpressionId = 2,
            mySentence = "Nice to meet you.",
            aISentence = "Pleasure to make your acquaintance.",
            keyword = "greeting",
            keywordKorean = "인사"
        ),
        NativeExpression(
            nativeExpressionId = 3,
            mySentence = "What's your name?",
            aISentence = "May I know what you're called?",
            keyword = "introduction",
            keywordKorean = "소개"
        )
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
    ) {

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(nativeExpressions) { expressions ->
                NativeContent(expressions)
            }
        }
    }
}


@Composable
fun NativeContent(expression: NativeExpression) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                BorderStroke(1.dp, color = Color.White),
                shape = RoundedCornerShape(25.dp)
            )
            .background(Color.Transparent, shape = RoundedCornerShape(25.dp))
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
            Text(  // 키워드
                text = expression.keyword,
                color = purpleDark,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 24.sp
            )

            Text(
                text = expression.keywordKorean,
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
                .padding(start = 20.dp, end = 20.dp, bottom = 23.dp)
        ) {
            Text(
                text = "나의 문장",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = expression.mySentence,
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
                text = expression.aISentence,
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

    // 더미 데이터
    val nativeExpressions = listOf(
        NativeExpression(
            nativeExpressionId = 1,
            mySentence = "How are you?",
            aISentence = "How have you been?",
            keyword = "greeting",
            keywordKorean = "인사"
        ),
        // Add more sample items if needed
        NativeExpression(
            nativeExpressionId = 2,
            mySentence = "Nice to meet you.",
            aISentence = "Pleasure to make your acquaintance.",
            keyword = "greeting",
            keywordKorean = "인사"
        ),
        NativeExpression(
            nativeExpressionId = 3,
            mySentence = "What's your name?",
            aISentence = "May I know what you're called?",
            keyword = "introduction",
            keywordKorean = "소개"
        )
    )

    for (express in nativeExpressions) {
        NativeContent(express)
    }

}