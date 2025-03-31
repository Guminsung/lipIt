package com.ssafy.lipit_app.ui.screens.myvoice.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ssafy.lipit_app.R

@Composable
fun CustomVoiceScreen() {

    /**
     * memerId             사용자 id
     * memberVoiceId       보유 음성 ID
     * voiceId             커스텀 음성 ID
     * voiceName           음성 이름
     * type                음성 타입(항상 CUSTOM)
     * imageUrl            음성 파일 URL
     * audioUrl            활성화 여부
     */

    // UI 그리는데 필요한 데이터 : 이미지, 음성 이름
    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        for (cnt in 1..3) {
            CustomColumn(imageUrl = "", voiceName = "SSAFY")
            Spacer(modifier = Modifier.height(15.dp))
        }

        Spacer(modifier = Modifier.height(30.dp))

        FloatingActionButton(
            onClick = { /*TODO*/ },
            modifier = Modifier.padding(bottom = 16.dp),
            backgroundColor = Color(0xffA37BBD),
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add Custom Voice",
                tint = Color(0xff603981),
                modifier = Modifier.size(30.dp)
            )
        }

    }

}

@Composable
fun CustomColumn(imageUrl: String, voiceName: String) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Color.White,
                shape = RoundedCornerShape(20.dp)
            )
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_launcher_foreground), // 프로필 이미지
                contentDescription = "Profile Image",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(Color.Gray)
            )

            Text(
                text = voiceName,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )
        }

        Button(
            onClick = { /*TODO*/ },
            colors = ButtonDefaults.buttonColors(Color.Transparent),
            border = BorderStroke(
                width = 1.dp,
                color = Color(0xffA37BBD)
            ),
            shape = RoundedCornerShape(20.dp),
            elevation = ButtonDefaults.elevation(0.dp),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        ) {
            Text(
                "변경", fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = Color(0xffA37BBD)
            )
        }

    }
}

@Preview(showBackground = true)
@Composable
fun CustomVoicePreview() {
    CustomVoiceScreen()
}