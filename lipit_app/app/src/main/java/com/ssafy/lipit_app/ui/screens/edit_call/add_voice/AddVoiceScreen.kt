package com.ssafy.lipit_app.ui.screens.edit_call.add_voice

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ssafy.lipit_app.R

@SuppressLint("DefaultLocale")
@Composable
fun AddVoiceScreen(
    state: AddVoiceState,
    onIntent: (AddVoiceIntent) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFDF8FF)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.weight(0.5f))

        // 제목
        Text(
            text = "Create Custom Voice",
            style = TextStyle(
                fontSize = 30.sp,
                lineHeight = 30.sp,
                fontWeight = FontWeight(700),
                color = Color(0xFF222124),
                textAlign = TextAlign.Center,
            )
        )

        Spacer(modifier = Modifier.height(5.dp))

        // 안내 문구
        Text(
            text = "조용한 환경에서 녹음해 주세요.",
            style = TextStyle(
                fontSize = 15.sp,
                lineHeight = 30.sp,
                fontWeight = FontWeight(400),
                color = Color(0xFF8A8A8A),
                textAlign = TextAlign.Center,
            )
        )

        Spacer(modifier = Modifier.weight(0.3f))

        // 남은 녹음 시간
        Text(
            text = String.format(
                "%02d:%02d",
                state.secondsRemaining / 60,
                state.secondsRemaining % 60
            ),
            style = TextStyle(
                fontSize = 40.sp,
                lineHeight = 40.sp,
                fontWeight = FontWeight(700),
                color = Color(0xFFE94F52),
            )
        )

        Spacer(modifier = Modifier.height(20.dp))

        // 녹음 문장
        Text(
            text = state.sentenceList.getOrNull(state.currentSentenceIndex) ?: "문장을 불러오는 중 ..",
            style = TextStyle(
                fontSize = 20.sp,
                lineHeight = 30.sp,
                fontWeight = FontWeight(400),
                color = Color(0xFF222124),
                textAlign = TextAlign.Center,
            )
        )

        Spacer(modifier = Modifier.height(10.dp))

        // 문장 진행률
        Text(
            text = "${state.currentSentenceIndex + 1} / ${state.sentenceList.size}",
            style = TextStyle(
                fontSize = 14.sp,
                color = Color.Gray,
                fontWeight = FontWeight.Light
            )
        )

        Spacer(modifier = Modifier.weight(0.3f))

        // 마이크 그림
        Image(
            painterResource(id = R.drawable.add_voice_mic_img),
            contentDescription = "마이크",
            modifier = Modifier
                .width(180.dp)
                .height(381.dp)
        )

        Spacer(modifier = Modifier.weight(0.4f))

        // 재생&정지 버튼
        Image(
            painterResource(
                id = if (state.isRecording) R.drawable.add_voice_stop_icon else R.drawable.add_voice_start_icon
            ),
            contentDescription = "재생/정지 버튼",
            modifier = Modifier
                .size(70.dp)
                .clickable {
                    if (state.isRecording) {
                        onIntent(AddVoiceIntent.StopRecording)
                        onIntent(AddVoiceIntent.NextSentence)
                    } else {
                        onIntent(AddVoiceIntent.StartRecording)
                    }
                }
        )

        Spacer(modifier = Modifier.weight(0.5f))
    }
}

@Preview(showBackground = true)
@Composable
fun AddVoiceScreenPreview() {
    val sampleSentences = listOf(
        "Hi, I'm glad to meet you today.",
        "Can you hear me clearly?",
        "Let's get started with our conversation.",
        "The weather is really nice today, isn't it?",
        "I enjoy reading books and watching movies.",
        "What are your hobbies?",
        "Please repeat after me, slowly and clearly.",
        "This is how I usually talk every day.",
        "You can speak naturally and confidently.",
        "Thank you for listening to my voice!"
    )


    AddVoiceScreen(
        state = AddVoiceState(
            secondsRemaining = 30,
            isRecording = false,
            currentSentenceIndex = 0,
            sentenceList = sampleSentences
        ),
        onIntent = {}
    )
}