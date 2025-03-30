package com.ssafy.lipit_app.ui.screens.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ssafy.lipit_app.R
import com.ssafy.lipit_app.ui.screens.main.components.NextLevel
import com.ssafy.lipit_app.ui.screens.main.components.ReportAndVoiceBtn
import com.ssafy.lipit_app.ui.screens.main.components.TodaysSentence
import com.ssafy.lipit_app.ui.screens.main.components.WeeklyCallsSection


@Composable
fun MainScreen(
    state: MainState,
    onIntent: (MainIntent) -> Unit
) {
    //val state by viewModel.state.collectAsState()
    var selectedDay by remember { mutableStateOf(state.selectedDay) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFDF8FF))
            .padding(start = 20.dp, end = 20.dp, top = 40.dp)
    ) {
        UserInfoSection(state.userName) // 상단의 유저 이름, 등급 부분
        TodaysSentence(state.sentenceOriginal, state.sentenceTranslated) // 오늘의 문장

        WeeklyCallsSection(
            selectedDay = selectedDay, //state의 selectedDay -> screen 안에서 정의한 selectedDay로 변경
            callItems = state.callItems,
            onIntent = {
                if (it is MainIntent.OnDaySelected) {
                    selectedDay = it.day
                }
            }
        )

        // 레벨업 파트
        NextLevel(state.sentenceCnt, state.wordCnt, state.attendanceCnt, state.attendanceTotal)

        // 리포트 & 마이 보이스로 넘어가는 버튼들
        ReportAndVoiceBtn()

        // 전화 걸기 버튼
        CallButton()

    }

}



// 전화 걸기 버튼
@Composable
fun CallButton() {
    Button(onClick = { /*TODO*/ }) {

    }
}


// 사용자 정보 (이름 & 등급)
@Composable
fun UserInfoSection(userName: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        //  사용자 이름
        Text(
            text = "Hello, $userName",
            style = androidx.compose.ui.text.TextStyle(
                fontSize = 20.sp,
                lineHeight = 50.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF000000),
            )
        )

        Spacer(modifier = Modifier.width(8.dp))

        // 사용자 등급
        Image(
            painter = painterResource(id = R.drawable.user_level_2),
            contentDescription = "사용자 등급",
            modifier = Modifier.size(26.dp)
        )
    }
}


@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    MainScreen(
        state = MainState(
            userName = "Sarah",
            selectedDay = "월",
            callItems = listOf(
                CallItem(
                    id = 1,
                    name = "Harry Potter",
                    topic = "자유주제",
                    time = "08:00",
                    imageUrl = "https://file.notion.so/f/f/87d6e907-21b3-47d8-98dc-55005c285cce/7a38e4c0-9789-42d0-b8a0-2e3d8c421433/image.png?table=block&id=1c0fd4f4-17d0-80ed-9fa9-caa1056dc3f9&spaceId=87d6e907-21b3-47d8-98dc-55005c285cce&expirationTimestamp=1742824800000&signature=3tw9F7cAaX__HcAYxwEFal6KBsvDg2Gt0kd7VnZ4LcY&downloadName=image.png",
                    "월"
                )
            ),
            sentenceCnt = 50,
            wordCnt = 10,
            attendanceCnt = 20,
            attendanceTotal = 20,
            sentenceOriginal = "With your talent and hard work, sky’s the limit!",
            sentenceTranslated = "너의 재능과 노력이라면, 한계란 없지!",
        ),
        onIntent = { }
    )
}


