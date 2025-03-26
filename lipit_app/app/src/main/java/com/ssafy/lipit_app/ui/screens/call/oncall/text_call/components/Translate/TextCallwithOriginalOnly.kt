package com.ssafy.lipit_app.ui.screens.call.oncall.text_call.components.Translate

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ssafy.lipit_app.ui.screens.call.oncall.text_call.TextCallState

@Composable
fun TextCallwithOriginalOnly(state: TextCallState) {
    val chatMessages = state.messages

    // 일단 리스트로 구현했으나 백 연동 시 다시 고려
    LazyColumn(
    modifier = Modifier
    .fillMaxWidth()
    .padding(top = 24.dp),
    verticalArrangement = Arrangement.spacedBy(17.dp)
    ) {
        items(chatMessages.size) { index ->
            val message = chatMessages[index]

            Column(
                modifier = Modifier
                    .fillMaxWidth(),

                // 누가 보낸 메시지인지에 따라 가로 위치 변경
                horizontalAlignment = if (message.isFromUser) Alignment.End else Alignment.Start
            ) {
                Box(
                    modifier = Modifier
                        .background(
                            color = if (message.isFromUser) Color(0x99C494D9) else Color(
                                0x66000000
                            ),
                            shape = RoundedCornerShape(size = 15.dp)
                        )
                        .padding(17.dp)
                        .widthIn(max = 260.dp) // 최대 가로 길이 제한
                ) {
                    Column {
                        Text(
                            text = message.text,
                            style = TextStyle(
                                fontSize = 20.sp,
                                lineHeight = 30.sp,
                                fontWeight = FontWeight(590),
                                color = Color(0xFFFDF8FF)
                            )
                        )
                    }

                }
            }

        }
    }
}