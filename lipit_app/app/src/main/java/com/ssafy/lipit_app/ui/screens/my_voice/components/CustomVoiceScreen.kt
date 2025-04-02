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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import coil.compose.rememberAsyncImagePainter
import com.ssafy.lipit_app.R
import com.ssafy.lipit_app.data.model.response_dto.myvoice.CustomResponse

@Composable
fun CustomVoiceScreen(
    customVoices: List<CustomResponse> = emptyList(),
    onVoiceChange: (Long) -> Unit
) {

    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
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

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 커스텀 음성 목록 표시
                items(customVoices) { voice ->
                    CustomColumn(
                        voiceId = voice.voiceId,
                        imageUrl = voice.customImageUrl,
                        voiceName = voice.voiceName,
                        onVoiceChange = { onVoiceChange(voice.voiceId) }
                    )

                    Spacer(modifier = Modifier.height(15.dp))
                }
            }

        }

    }

}

@Composable
fun CustomColumn(
    voiceId: Long,
    imageUrl: String,
    voiceName: String,
    onVoiceChange: (Long) -> Unit
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


            if (imageUrl.isNotEmpty()) {
                Image(
                    painter = rememberAsyncImagePainter(model = imageUrl),
                    contentDescription = "3D Avatar",
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color.Gray),
                    contentScale = ContentScale.Crop
                )

            } else {
                Image(
                    painter = painterResource(id = R.drawable.ic_launcher_foreground), // 프로필 이미지
                    contentDescription = "Profile Image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color.Gray)
                )
            }

            Text(
                text = voiceName,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )
        }

        Button(
            onClick = { onVoiceChange(voiceId) },
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
