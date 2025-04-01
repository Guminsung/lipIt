package com.ssafy.lipit_app.ui.screens.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ssafy.lipit_app.R
import com.ssafy.lipit_app.ui.screens.edit_call.weekly_calls.CallSchedule
import com.ssafy.lipit_app.ui.screens.edit_call.weekly_calls.WeeklyCallsIntent
import com.ssafy.lipit_app.ui.screens.edit_call.weekly_calls.WeeklyCallsScreen
import com.ssafy.lipit_app.ui.screens.edit_call.weekly_calls.WeeklyCallsState
import com.ssafy.lipit_app.ui.screens.main.components.NextLevel
import com.ssafy.lipit_app.ui.screens.main.components.ReportAndVoiceBtn
import com.ssafy.lipit_app.ui.screens.main.components.TodaysSentence
import com.ssafy.lipit_app.ui.screens.main.components.WeeklyCallsSection
import kotlinx.coroutines.launch


@Composable
fun MainScreen(
    state: MainState,
    onIntent: (MainIntent) -> Unit
) {
    // [Weekly Calls] Bottom Sheet
    val bottomSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        skipHalfExpanded = true
    )

    androidx.compose.runtime.LaunchedEffect(state.isSettingsSheetVisible) {
        if (state.isSettingsSheetVisible) {
            bottomSheetState.show()
        } else {
            bottomSheetState.hide()
        }
    }
    // BottomSheet 닫힘 감지 코드 : 다른영역을 터치해서 닫았을 때에도 감지하고 변수값을 변경시켜줘야함
    androidx.compose.runtime.LaunchedEffect(bottomSheetState.isVisible) {
        if (!bottomSheetState.isVisible && state.isSettingsSheetVisible) {
            onIntent(MainIntent.OnCloseSettingsSheet)
        }
    }

    ModalBottomSheetLayout(
        sheetState = bottomSheetState,
        sheetBackgroundColor = Color.Transparent,
        sheetContent = {
            Surface( // ← 여기서부터 라운드 처리!
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                color = Color(0xFFFDF8FF), // 원래 배경색 (원하면 바꿔도 됨)
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.9f)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(vertical = 10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(vertical = 8.dp)
                            .size(width = 40.dp, height = 4.dp)
                            .background(Color.LightGray, RoundedCornerShape(2.dp))
                    )

                    // WeeklyCallsScreen
                    WeeklyCallsScreen(
                        state = state.weeklyCallsState,
                        onIntent = { intent ->
                            if (intent is WeeklyCallsIntent.OnEditSchedule) {
                                println("Edit 눌림!")
                            }
                        }
                    )
                }
            }
        }
    ) {
        // 기존 MainScreen UI
        var selectedDay by remember { mutableStateOf(state.selectedDay) }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFFDF8FF))
                .padding(start = 20.dp, end = 20.dp, top = 40.dp)
        ) {
            UserInfoSection(state.userName)
            TodaysSentence(state.sentenceOriginal, state.sentenceTranslated)

            WeeklyCallsSection(
                selectedDay = selectedDay,
                callItems = state.callItems,
                onIntent = {
                    if (it is MainIntent.OnDaySelected) {
                        selectedDay = it.day
                    } else {
                        onIntent(it) // 나머지 이벤트 넘기기 (예: OnSettingsClicked)
                    }
                }
            )

            Spacer(modifier = Modifier.height(12.dp))
            ReportAndVoiceBtn(onIntent)
            NextLevel(
                state.sentenceCnt,
                state.wordCnt,
                state.attendanceCnt,
                state.attendanceTotal
            )

            Spacer(modifier = Modifier.height(20.dp))

            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                CallButton(onIntent)
            }
        }
    }
}


// 전화 걸기 버튼
@Composable
fun CallButton(onIntent: (MainIntent) -> Unit) {
    Image(
        painterResource(id = R.drawable.main_call_icon),
        contentDescription = "전화 걸기",
        modifier = Modifier
            .size(70.dp)
            .clickable { // 화면 이동
                onIntent(MainIntent.NavigateToCallScreen)
            }
    )
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


