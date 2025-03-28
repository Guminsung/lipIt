package com.ssafy.lipit_app.ui.screens.main

import android.util.Log
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.ssafy.lipit_app.R
import com.ssafy.lipit_app.ui.screens.edit_call.weekly_calls.WeeklyCallsScreen
import com.ssafy.lipit_app.ui.screens.edit_call.weekly_calls.WeeklyCallsViewModel
import kotlinx.coroutines.launch

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
            .padding(horizontal = 20.dp, vertical = 60.dp)
    ) {
        UserInfoSection(state.userName) // 상단의 유저 이름, 등급 부분
        TodaysSentence(state.sentenceOriginal, state.sentenceTranslated) // 오늘의 문장
        WeeklyCallsSection(
            selectedDay = selectedDay, //state의 selectedDay -> screen 안에서 정의한 selectedDay로 변경
            callItems = state.callItems,
            onIntent = {
                if(it is MainIntent.OnDaySelected){
                    selectedDay = it.day
                }
            }
        )
        //todo: 레벨업, Call Log 버튼, 전화 걸기 버튼 부분 추가


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

// 오늘의 문장
@Composable
fun TodaysSentence(sentenceOriginal: String, sentenceTranslated: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 9.dp),
    ) {
        // 배경
        Image(
            painter = painterResource(id = R.drawable.main_todays_sentence_background),
            contentDescription = "오늘의 명언 배경",
            contentScale = ContentScale.FillWidth,
            modifier = Modifier
                .fillMaxWidth()
                .height(110.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(110.dp)
        ) {
            // 오늘의 문장 원문 + 번역 텍스트
            Column(
                modifier = Modifier
                    .weight(1f)
                    .align(Alignment.CenterVertically)
                    .padding(top = 21.dp, start = 27.dp, bottom = 21.dp, end = 6.dp)
            ) {
                Text(
                    text = sentenceOriginal,
                    style = TextStyle(
                        fontSize = 15.sp,
                        lineHeight = 20.sp,
                        fontWeight = FontWeight(400),
                        color = Color(0xFFFFFFFF),
                    )
                )

                Spacer(modifier = Modifier.height(7.dp))

                Text(
                    text = "$sentenceTranslated  ✦˚",
                    style = TextStyle(
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
                        fontWeight = FontWeight(700),
                        color = Color(0xFFFFFFFF),
                    )
                )
            }

            Image(
                painterResource(id = R.drawable.main_todays_sentance_img),
                contentDescription = "오늘의 문장 이미지",
                modifier = Modifier
                    .height(120.dp)
                    .width(80.dp)
                    .align(Alignment.Top)
                    .padding(start = 0.dp, top = 12.dp, bottom = 20.dp, end = 15.dp)
            )
        }

    }
}

// 주간 전화 일정 한 눈에 보기
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeeklyCallsSection(
    selectedDay: String,
    callItems: List<CallItem>,
    onIntent: (MainIntent) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = false
    )

    // 처음엔 중간 상태로 출력되도록 설정함
    LaunchedEffect(Unit) {
        sheetState.partialExpand()
    }

    val scope = rememberCoroutineScope()

    var showSheet by remember{ mutableStateOf(false) }


    if(showSheet){
        ModalBottomSheet(
            onDismissRequest = { showSheet = false },
            sheetState = sheetState
        ) {
            val viewModel = androidx.lifecycle.viewmodel.compose.viewModel<WeeklyCallsViewModel>()
            val state by viewModel.state.collectAsState()

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFFDF8FF))
            ){
                WeeklyCallsScreen( state = state,
                    onIntent = {viewModel.onIntent(it)})
            }
        }
    }

    // sheet를 여는 이벤트 동작을 수행할 ui
    Column(
        modifier = Modifier
            .padding(top = 25.dp)
    ) {
        // 제목 + 버튼 영역
        Row(
            Modifier.padding(bottom = 14.dp)
        ) {
            Text(
                text = "Weekly Calls",
                style = TextStyle(
                    fontSize = 25.sp,
                    lineHeight = 50.sp,
                    fontWeight = FontWeight(700),
                    color = Color(0xFF000000),
                )
            )

            // 편집 버튼
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Button(
                    onClick = {
                        //전화 일정 편집 화면으로 넘어감
                        showSheet = true
                        scope.launch {
                            sheetState.show()
                        }
                    },
                    Modifier
                        .width(50.dp)
                        .height(25.dp)
                    ,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFA37BBD)
                    ),
                    contentPadding = PaddingValues(0.dp) // 내부 여백 (기본 여백 제거해서 텍스트에 맞춰서 재설정)
                ) {
                    Text(
                        text = "편집",
                        style = TextStyle(
                            fontSize = 12.sp,
                            lineHeight = 15.sp,
                            fontWeight = FontWeight(590),
                            color = Color(0xFFFFFFFF),
                            textAlign = TextAlign.Center
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

        }
    }

    // 전화 일정 출력 영역
    // 요일 선택 커스텀 탭
    //        DaySelector(
    //            onDaySelected = { day ->
    //                onIntent(MainIntent.OnDaySelected(day))
    //                Log.d("selectedDay", "selectedDay: $day")
    //             },
    //            selectedDay
    //        )

    // 스케줄 카드뷰
    //dailyCallSchedule(callItems)
}

// 요일별 call 카드뷰
@Composable
fun dailyCallSchedule(callItems: List<CallItem>) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 2.dp)
    ) {
        // 배경 박스
        Image(
            painter = painterResource(id = R.drawable.main_weekly_calls_background),
            contentDescription = "요일별 Calls 스케줄 카드",
            contentScale = ContentScale.FillWidth,
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
        )

        // 내용
        Row(
            modifier = Modifier
                .fillMaxWidth()
                //todo: 현재는 임시로 패딩을 통해 위치 지정 했는데 시간 남으면
                //todo: 박스랑 상대적인 위치를 고려해서 중앙 배치 수정하기!
                .padding(top = 24.dp, start = 22.dp, end = 20.dp)
        ) {
            // url을 통해 이미지 받아오기
            AsyncImage(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape),
                model = callItems[0].imageUrl, //임시
                contentDescription = "voice 프로필 사진"
            )

            Log.d("ImageCheck", "URL: ${callItems.getOrNull(0)?.imageUrl}")


            Column(
                modifier = Modifier
                    .padding(start = 15.dp, end = 105.dp)
                    .align(Alignment.CenterVertically)

            ) {
                // 보이스 이름
                Text(
                    text = callItems[0].name,
                    style = TextStyle(
                        fontSize = 16.sp,
                        lineHeight = 15.sp,
                        fontWeight = FontWeight(590),
                        color = Color(0xFF000000))
                )

                // 대화 주제 (토픽)
                Text(
                    text = callItems[0].topic,
                    style = TextStyle(
                        fontSize = 15.sp,
                        lineHeight = 15.sp,
                        fontWeight = FontWeight(400),
                        color = Color(0xFF5F5F61),

                        )
                )
            }

            // 정해진 call 시간
            Box(
                modifier = Modifier
                    .align(Alignment.Bottom)
                    .padding(bottom = 7.dp)
            ){
                Text(
                    text = "At " + callItems[0].time,
                    style = TextStyle(
                        fontSize = 12.sp,
                        lineHeight = 15.sp,
                        fontWeight = FontWeight(400),
                        color = Color(0xFF5F5F61),
                        //textAlign = TextAlign.End
                    )
                )
            }


        }
    }
}

// 커스텀 탭 레이아웃
@Composable
fun DaySelector(
    onDaySelected: (String) -> Unit,
    selectedDay: String
) {
    val days = listOf("월", "화", "수", "목", "금", "토", "일")
    val selectedIndex = days.indexOf(selectedDay)

    // 애니메이션 효과 추가
    // 슬라이딩 형식으로 배경 이동
    val itemWidth = 41.dp // 박스 가로 길이
    val animatedOffsetX by animateDpAsState(
        targetValue = (selectedIndex * 48).dp,
        label="offsetX"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(36.dp)
            .background(
                color = Color(0xB2F3E7F9),
                shape = RoundedCornerShape(size = 20.dp)
            )
            .padding(horizontal = 3.dp)
    ) {
        days.forEach { day ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .height(26.dp)
                    .padding(horizontal = 4.dp)
                    .background(
                        if (day == selectedDay) Color(0xFFA37BBD) else Color(0xB2F3E7F9),
                        shape = RoundedCornerShape(size = 50.dp)
                    )
                    .clickable {
                        onDaySelected(day)
                    }
                    .align(Alignment.CenterVertically),
                Alignment.Center
            ){
                Text(
                    text = day,
                    fontWeight = if(day == selectedDay) FontWeight(600) else FontWeight(400),
                    color = if(day == selectedDay) Color.White else Color.Black,
                    textAlign = TextAlign.Center
                )
            }
        }

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
                CallItem(id = 1, name = "Harry Potter", topic = "자유주제", time = "08:00", imageUrl = "https://file.notion.so/f/f/87d6e907-21b3-47d8-98dc-55005c285cce/7a38e4c0-9789-42d0-b8a0-2e3d8c421433/image.png?table=block&id=1c0fd4f4-17d0-80ed-9fa9-caa1056dc3f9&spaceId=87d6e907-21b3-47d8-98dc-55005c285cce&expirationTimestamp=1742824800000&signature=3tw9F7cAaX__HcAYxwEFal6KBsvDg2Gt0kd7VnZ4LcY&downloadName=image.png", "월")
            ),
            sentenceProgress = 90,
            wordProgress = 50,
            attendanceCount = 20,
            attendanceTotal = 20,
            sentenceOriginal = "With your talent and hard work, sky’s the limit!",
            sentenceTranslated = "너의 재능과 노력이라면, 한계란 없지!",
        ),
        onIntent = { }
    )
}


