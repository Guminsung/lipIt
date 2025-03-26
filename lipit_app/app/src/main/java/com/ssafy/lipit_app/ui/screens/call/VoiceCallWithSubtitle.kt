package com.ssafy.lipit_app.ui.screens.call

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp

// Voice 버전 - 자막 O
@Composable
fun CallWithSubtitle(
    state: OnCallState,
    onIntent:(OnCallIntent) -> Unit
) {
    // 1-1-1. 번역 ver
    turnOnTranslate(state)

    // 1-1-2. no 번역 ver
}

@Composable
fun turnOnTranslate(state: OnCallState) {
    Column(

    ) {
        Text(
            text = state.AIMessageOriginal,
            style = TextStyle(
                fontSize = 20.sp,
                lineHeight = 30.sp,
                fontWeight = FontWeight(590),
                color = Color(0xFFFDF8FF),

                textAlign = TextAlign.Center,
            )
        )
    }
}
