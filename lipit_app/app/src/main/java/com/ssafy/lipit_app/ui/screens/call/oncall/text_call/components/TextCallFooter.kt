package com.ssafy.lipit_app.ui.screens.call.oncall.text_call.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// 하단 영역 (텍스트 입력 공간, 번역 여부 및 텍스트 보내기 버튼)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TextCallFooter(inputText: String, showTranslation: Boolean) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(9.dp),
        modifier = Modifier
            .fillMaxWidth()
    ) {
        // 텍스트 입력 공간
        var text by remember { mutableStateOf(inputText) }

        TextField(
            value = text, //inputText: 사용자가 작성 중인 텍스트(state에 있음)
            onValueChange = { text = it },
            modifier = Modifier
                .fillMaxWidth(),
            shape = RoundedCornerShape(15.dp),
            placeholder = {
                Text(
                    text = "텍스트를 입력해 주세요.",
                    style = TextStyle(
                        fontSize = 15.sp,
                        lineHeight = 15.sp,
                        fontWeight = FontWeight(400),
                        color = Color(0xB25F5F61),
                        textAlign = TextAlign.Center,
                    ),
//                    modifier = Modifier
//                        .padding(vertical = .dp)
                )
            },
            colors = TextFieldDefaults.textFieldColors(
                containerColor = Color(0xFFFDF8FF),
                focusedIndicatorColor = Color.Transparent, // 테두리 제거
                unfocusedIndicatorColor = Color.Transparent
            )
        )

        // 우측 버튼 (번역 + 보내기)

        // 번역 버튼으로 변신
        // 보내기 버튼으로 변신
    }
}