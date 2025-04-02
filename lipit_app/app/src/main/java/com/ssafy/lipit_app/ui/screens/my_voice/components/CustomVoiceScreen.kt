package com.ssafy.lipit_app.ui.screens.my_voice.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import com.ssafy.lipit_app.data.model.response_dto.myvoice.CustomResponse

@Composable
fun CustomVoiceScreen(
    customVoices: List<CustomResponse> = emptyList(),
    onVoiceSelected: (String, String) -> Unit = { _, _ -> }
) {

    // UI 그리는데 필요한 데이터 : 이미지, 음성 이름
    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // 커스텀 음성이 없는 경우
        if (customVoices.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "No Custom voices",
                    color = Color.White,
                    fontSize = 16.sp
                )

                Spacer(modifier = Modifier.height(70.dp))
            }
        } else {
            // 커스텀 음성 목록 표시
            customVoices.forEach { voice ->
                CustomColumn(
                    imageUrl = voice.customImageUrl,
                    voiceName = voice.voiceName,
                    onVoiceSelected = { onVoiceSelected(voice.voiceName, voice.customImageUrl) }
                )
            }
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
fun CustomColumn(
    imageUrl: String,
    voiceName: String,
    onVoiceSelected: () -> Unit
) {

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
            onClick = { onVoiceSelected() },
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