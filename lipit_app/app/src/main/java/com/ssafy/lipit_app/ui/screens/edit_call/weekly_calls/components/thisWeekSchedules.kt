package com.ssafy.lipit_app.ui.screens.edit_call.weekly_calls.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ssafy.lipit_app.R
import com.ssafy.lipit_app.ui.screens.edit_call.reschedule.EditCallScreen
import com.ssafy.lipit_app.ui.screens.edit_call.reschedule.EditCallViewModel
import com.ssafy.lipit_app.ui.screens.edit_call.weekly_calls.CallSchedule
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThisWeekSchedules(dayList: List<String>, callSchedules: List<CallSchedule>) {
    val today = LocalDate.now()
    val dayOfWeek: DayOfWeek = today.dayOfWeek
    val koreanDayName: String = dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.KOREAN)

    // 각 스케줄 수정 바텀 시트 관련
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true // 스케줄 관련 바텀 시트는 크기만큼 올라오도록 함
    )

    val scope = rememberCoroutineScope()
    var showSheet by remember{ mutableStateOf(false) }

    if(showSheet){
        ModalBottomSheet(
            onDismissRequest = { showSheet = false },
            sheetState = sheetState
        ) {
            val viewModel = androidx.lifecycle.viewmodel.compose.viewModel<EditCallViewModel>()
            val state by viewModel.state.collectAsState()

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFFDF8FF))
            ){
                EditCallScreen( state = state,
                    onIntent = {viewModel.onIntent(it)})
            }
        }
    }


    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(start = 12.dp, bottom = 70.dp, top = 10.dp),
        verticalArrangement = Arrangement.SpaceBetween // 아이템 간격 일정하게 조정
    ) {

        dayList.zip(callSchedules).forEach { (day, schedule) ->
            val isToday = day == koreanDayName
            var showDeletePopup by remember { mutableStateOf(false) } // 삭제 팝업

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(57.dp)
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onLongPress = {
                                showDeletePopup = true // 롱프레스 시 팝업 띄우기
                            },
                            onTap = {
                                // 일반 클릭 시 상세로 넘어감
                                //전화 개별 상세 편집 화면으로 넘어감
                                scope.launch {
                                    // todo: 기존 스케줄 목록 바텀 시트 닫음
                                    if(sheetState.isVisible){
                                        sheetState.hide()
                                    }
                                    showSheet = false

                                    kotlinx.coroutines.delay(200)

                                    showSheet = true
                                    scope.launch {
                                        sheetState.show()
                                    }
                                }
                            }
                        )
                    }
                    .border(
                        width = if (isToday) 1.5.dp else 1.dp,
                        color = if (isToday) Color(0xB28055A6) else Color(0xFFF3E7F9),
                        shape = RoundedCornerShape(size = 15.dp)
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 시간 텍스트
                val time = schedule.scheduledTime.substringBeforeLast(":")
                val divideTime = if (time.substringBeforeLast(":") < 12.toString()) "AM" else "PM"

                Text(
                    modifier = Modifier
                        .padding(horizontal = 20.dp, vertical = 19.dp),
                    text = time + " " + divideTime,
                    style = androidx.compose.ui.text.TextStyle(
                        fontSize = 18.sp,
                        lineHeight = 15.sp,
                        fontWeight = FontWeight(400),
                        color = Color(0xFF5F5F61)
                    )

                )

                Spacer(modifier = Modifier.weight(1f))

                // 토픽 텍스트 with 배경
                Box(
                    modifier = Modifier
                        .height(25.dp)
                        .background(
                            color = Color(0xFFF3E7F9),
                            shape = RoundedCornerShape(size = 15.dp)
                        ),
                    contentAlignment = Alignment.Center
                ){
                    Text(
                        text=schedule.topicCategory,
                        style = androidx.compose.ui.text.TextStyle(
                            fontSize = 12.sp,
                            lineHeight = 15.sp,
                            fontWeight = FontWeight(400),
                            color = Color(0xFF5F5F61),
                            textAlign = TextAlign.Center,
                        ),
                        modifier = Modifier
                            .padding(horizontal = 15.dp)

                    )
                }

                Spacer(modifier = Modifier.width(5.dp))

                // todo: 드래그앤드롭 구현
                // 이동 버튼

                Icon(painterResource(id = R.drawable.menu_icon),
                    contentDescription = "이동버튼",
                    modifier = Modifier
                        .width(20.dp)
                        .height(20.dp),
                    tint = Color(0xFF8055A6))

                Spacer(modifier = Modifier.width(12.dp))


                // 팝업 커스텀
                if (showDeletePopup) {
                    DeleteCallDialog(
                        onDismiss = { showDeletePopup = false },
                        onConfirm = {
                            // 삭제 처리
                            showDeletePopup = false
                        }
                    )
                }

            }
        }
    }
}

