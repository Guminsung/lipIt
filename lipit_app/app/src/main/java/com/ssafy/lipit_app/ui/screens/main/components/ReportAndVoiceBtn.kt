package com.ssafy.lipit_app.ui.screens.main.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ssafy.lipit_app.R

// Call log 버튼 (reports & my voices 버튼들 영역)
@Composable
fun ReportAndVoiceBtn() {
    Row(
        modifier = Modifier
            .height(80.dp)
            .fillMaxWidth()
    ) {
        // reports 버튼
        Column(
            modifier = Modifier
                .background(color = Color(0xB2F3E7F9), shape = RoundedCornerShape(size = 15.dp))
                .fillMaxHeight()
                .weight(1f)
                .align(Alignment.CenterVertically)
                .offset(y = (-3).dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // 아이콘
            Image(
                painterResource(id = R.drawable.main_reports_icon),
                contentDescription = "리포트",
                modifier = Modifier
                    .width(50.dp)
                    .height(50.dp)
            )

            // 텍스트
            Text(
                text = "Reports",
                style = TextStyle(
                    fontSize = 15.sp,
                    lineHeight = 30.sp,
                    fontWeight = FontWeight(500),
                    color = Color(0xFF3D3D3D),
                    textAlign = TextAlign.Center,
                ),
                modifier = Modifier
                    .offset(y = (-5).dp),
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        // My Voices 버튼
        Column(
            modifier = Modifier
                .background(color = Color(0xB2F3E7F9), shape = RoundedCornerShape(size = 15.dp))
                .fillMaxHeight()
                .offset(y = (-3).dp)
                .weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // 아이콘
            Image(
                painterResource(id = R.drawable.main_my_voices_icon),
                contentDescription = "리포트",
                modifier = Modifier
                    .width(50.dp)
                    .height(50.dp)
            )

            // 텍스트
            Text(
                text = "My Voices",
                style = TextStyle(
                    fontSize = 15.sp,
                    lineHeight = 30.sp,
                    fontWeight = FontWeight(500),
                    color = Color(0xFF3D3D3D),
                    textAlign = TextAlign.Center,
                ),
                modifier = Modifier
                    .offset(y = (-5).dp),
                )
        }
    }
}
