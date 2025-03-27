package com.ssafy.lipit_app.ui.screens.call.oncall.text_call.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ssafy.lipit_app.R
import com.ssafy.lipit_app.ui.screens.call.oncall.text_call.TextCallIntent

// 하단 영역 (텍스트 입력 공간, 번역 여부 및 텍스트 보내기 버튼)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TextCallFooter(inputText: String, showTranslation: Boolean, onIntent: (TextCallIntent) -> Unit
) {
    val isKeyboardVisible = isKeyboardOpen()

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
                .fillMaxWidth()
                .height(51.dp)
                .focusable(true)
                .weight(1f),
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

                    )
            },
            colors = TextFieldDefaults.textFieldColors(
                containerColor = Color(0xFFFDF8FF),
                focusedIndicatorColor = Color.Transparent, // 테두리 제거
                unfocusedIndicatorColor = Color.Transparent
            )
        )

        // 우측 버튼 (번역 + 보내기)
        Box(
            modifier = Modifier
                .size(51.dp),
            contentAlignment = Alignment.Center
        ) {
            if (isKeyboardVisible) { // 키보드 열려있다면
                // 보내기 버튼
                Icon(
                    painterResource(id = R.drawable.textcall_send_icon),
                    contentDescription = "보내기"
                )
            } else {
                // 번역 버튼
                Box(
                    modifier = Modifier
                        .size(51.dp)
                        .clip(RoundedCornerShape(50.dp))
                        .background(Color(0x1AFDF8FF)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        painterResource(
                            id = if (showTranslation) R.drawable.oncall_on_translate_icon
                            else R.drawable.oncall_off_translate_icon
                        ),
                        contentDescription = "번역버튼",
                        tint = Color(0xFFFDF8FF),
                        modifier = Modifier.size(25.dp)
                            .clickable {
                                onIntent(TextCallIntent.ToggleTranslation)
                            },
                       // contentAlignment = Alignment.Center

                    )
                }

            }
        }

    }
}

@Composable
fun isKeyboardOpen(): Boolean {
    val ime = androidx.compose.foundation.layout.WindowInsets.ime
    return ime.getBottom(androidx.compose.ui.platform.LocalDensity.current) > 0
}
