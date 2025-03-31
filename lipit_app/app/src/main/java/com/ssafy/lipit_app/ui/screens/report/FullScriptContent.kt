package com.ssafy.lipit_app.ui.screens.report

import androidx.compose.foundation.background
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ssafy.lipit_app.data.model.dto.report.TotalScript


@Composable
fun FullScriptContent() {

    LazyColumn(
        modifier = Modifier
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 메시지 데이터를 정의하거나 가져옵니다
        val messages = listOf(
            TotalScript(
                content = "Hey! Long time no see! How have you been? Tell me something fun.",
                contentKor = "오! 오랜만이야! 잘 지냈어? 재밌는 이야기 하나 해줘!",
                isAI = true,
                timestamp = "2025년 12월 20일"
            ),
            TotalScript(
                content = "Hey! Yeah, it's been a while! I've been doing great. Oh, guess what? I finally went on that trip I told you about!",
                contentKor = "",
                isAI = false,
                timestamp = "2025년 12월 20일"
            ),
            TotalScript(
                content = "Wicked! Was it as exciting as a Quidditch match? Or did you run into any mischievous magical creatures?",
                contentKor = "멋지다! 퀴디치 경기만큼 신났어? 아니면 장난꾸러기 마법 생물이라도 만났어?",
                isAI = true,
                timestamp = "2025년 12월 20일"
            ),
            TotalScript(
                content = "Hey! Yeah, it's been a while!",
                contentKor = "",
                isAI = false,
                timestamp = "2025년 12월 20일"
            )
        )

        items(messages) { message ->
            ChatBubble(totalScript = message)
        }
    }
}

@Composable
fun ChatBubble(totalScript: TotalScript) {
    Column(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalAlignment = if (totalScript.isAI) Alignment.Start else Alignment.End
    ) {
        Box(
            modifier = Modifier
                .background(
                    color = if (totalScript.isAI)
                        Color(0xFF483D56) // 진한 보라색
                    else
                        Color(0xFFB19CD9), // 연한 보라색
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(fraction = 0.8f)
            ) {
                Text(
                    text = totalScript.content,
                    color = Color.White,
                    fontSize = 16.sp,
                    lineHeight = 24.sp
                )

                if (totalScript.contentKor.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = totalScript.contentKor,
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 14.sp,
                        lineHeight = 20.sp
                    )
                }
            }
        }
    }
}


@Composable
@Preview(showBackground = true)
fun FullScriptPreview() {
    FullScriptContent()
}