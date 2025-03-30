package com.ssafy.lipit_app.ui.screens.main.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Call log 버튼 (reports & my voices 버튼들 영역)
@Composable
fun ReportAndVoiceBtn() {
    // 타이틀
    Text(
        text = "Call Log+",
        style = TextStyle(
            fontSize = 23.sp,
            lineHeight = 50.sp,
            fontWeight = FontWeight(700),
            color = Color(0xFF000000)
        ),
        modifier = Modifier
            .padding(top = 20.dp)
    )

    //버튼1 - report
    Button(onClick = {
        /*TODO*/
    }) {

    }

    //버튼2 - My Voices
    Button(onClick = {
        /*TODO*/
    }) {

    }
}