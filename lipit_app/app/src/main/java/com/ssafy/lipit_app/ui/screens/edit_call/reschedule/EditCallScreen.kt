package com.ssafy.lipit_app.ui.screens.edit_call.reschedule

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times
import androidx.xr.compose.testing.toDp
import com.ssafy.lipit_app.R

@Composable
fun EditCallScreen(
    state: EditCallState,
    onIntent: (EditCallIntent) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFFDF8FF))
            .padding(top = 44.dp, start = 20.dp, end = 20.dp),
    ) {
        // 제목
        Text(
            text = "Reschedule Call",
            style = TextStyle(
                fontSize = 25.sp,
                lineHeight = 30.sp,
                fontWeight = FontWeight(700),
                color = Color(0xFF222124),
            )
        )

        Spacer(modifier = Modifier.height(20.dp))

        // time picker
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            WheelTimePicker()
        }

        Spacer(modifier = Modifier.height(30.dp))

        // topic(대화 주제) 타이틀
        Text(
            text = "Topic",
            style = TextStyle(
                fontSize = 25.sp,
                lineHeight = 30.sp,
                fontWeight = FontWeight(700),
                color = Color(0xFF222124),

                )
        )

        Spacer(modifier = Modifier.height(10.dp))

        // 자유주제-카테고리 선택 모달
        var selectedIndex by remember { mutableStateOf(0) }

        CustomSegmentedButtons(
            selectedIndex = selectedIndex,
            onSelected = { index ->
                selectedIndex = index // 업뎃

                if (index == 1) { // 카테고리 선택
                    // 상태 변경
                    state.isFreeModeSelected = false
                    state.isCategoryModeSelected = true

                    //todo: 선택 되면 카테고리 선택 활성화

                } else { // 자유주제 선택
                    // 상태 변경
                    state.isFreeModeSelected = true
                    state.isCategoryModeSelected = false

                    //todo: 카테고리 선택 비활성화
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 카테고리 선택 드롭다운
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(55.dp)
                .border(
                    width = 1.dp,
                    color = Color(0xFFE6E6E6),
                    shape = RoundedCornerShape(size = 15.dp)
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "카테고리 선택",
                style = TextStyle(
                    fontSize = 16.sp,
                    lineHeight = 30.sp,
                    fontWeight = FontWeight(400),
                    color = Color(0xFF7D7D7D),
                ),
                modifier = Modifier
                    .padding(start = 18.dp)
            )

            // 드롭다운 띄울 화살표 아이콘
            Box(
                modifier = Modifier
                    .padding(end = 18.dp)
            ) {
                Icon(
                    painterResource(id = R.drawable.edit_call_dropdown_icon),
                    contentDescription = "드롭다운",
                    modifier = Modifier
                        .width(14.dp)
                        .height(7.dp),
                    tint = Color(0xFFD3D3D3)
                )
            }

        }

        Spacer(modifier = Modifier.height(70.dp))

        //변경-삭제 버튼들
        EditCallButtons()

        Spacer(modifier = Modifier.height(20.dp))

    }
}

@Composable
fun EditCallButtons() {
    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        // 변경하기
        Button(
            onClick = { /*TODO: 변경 기능*/ },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFDDD3E2),
            ),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .height(48.dp),
            shape = RoundedCornerShape(8.dp),
            content = {
                Text(
                    text = "변경하기",
                    style = TextStyle(
                        fontSize = 17.sp,
                        lineHeight = 15.sp,
                        fontWeight = FontWeight(510),
                        color = Color(0xFF68606E),
                        textAlign = TextAlign.Center,
                    )
                )
            }

        )

        Spacer(modifier = Modifier.width(8.dp))

        // 삭제하기
        Button(
            onClick = { /*TODO: 삭제 기능*/ },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFA37BBD),
            ),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .height(48.dp),
            shape = RoundedCornerShape(8.dp),
            content = {
                Text(
                    text = "삭제하기",
                    style = TextStyle(
                        fontSize = 17.sp,
                        lineHeight = 15.sp,
                        fontWeight = FontWeight(510),
                        color = Color(0xFFFDF8FF),
                        textAlign = TextAlign.Center,
                    )
                )
            }

        )
    }

    Spacer(modifier = Modifier.height(20.dp))
}

@Composable
fun WheelTimePicker() {
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        WheelColumn(items = (1..24).map { it.toString().padStart(2, '0') })
        WheelColumn(items = (0..59).map { it.toString().padStart(2, '0') })

    }
}

@Composable
fun WheelColumn(items: List<String>) {
    //todo: 선택된 중간 영역에 배경 추가
    //todo: 선택된 시간 중앙에 위치 맞추기

    val listState = rememberLazyListState(initialFirstVisibleItemIndex = 3)
    LazyColumn(
        state = listState,
        modifier = Modifier
            .height(150.dp)
            .width(60.dp),
        verticalArrangement = Arrangement.Center
    ) {
        items(items.size) { index ->
            val item = items[index]
            Text(
                text = item,
                fontSize = if (index == listState.firstVisibleItemIndex + 2) 20.sp else 17.sp,
                modifier = Modifier
                    .height(30.dp)
                    .wrapContentHeight(Alignment.CenterVertically)
                    .fillMaxWidth(),
                textAlign = TextAlign.Center,
                color = if (index == listState.firstVisibleItemIndex + 2) Color.Black else Color.Gray
            )
        }
    }
}


// 대화 주제 선택
@Composable
fun CustomSegmentedButtons(
    selectedIndex: Int,
    onSelected: (Int) -> Unit
) {
    val items = listOf("자유주제", "카테고리")

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF3F3F3), shape = RoundedCornerShape(20.dp))
            .padding(4.dp)
            .height(40.dp)
    ) {
        val totalWidth = constraints.maxWidth.toDp() // 전체 너비
        val buttonWidth = totalWidth / items.size    // 항목 개수로 나누기
        val indicatorOffset by animateDpAsState(
            targetValue = selectedIndex * buttonWidth,
            label = "SegmentSlide"
        )

        // 움직이는 흰색 배경
        Box(
            modifier = Modifier
                .offset(x = indicatorOffset)
                .width(buttonWidth)
                .fillMaxHeight()
                .background(Color(0xFFFDF8FF), shape = RoundedCornerShape(15.dp))
        )

        Row {
            items.forEachIndexed { index, text ->
                Box(
                    modifier = Modifier
                        .width(buttonWidth)
                        .fillMaxHeight()
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            onSelected(index)

                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = text,
                        color = if (index == selectedIndex) Color(0xFF1D1D1F) else Color(0xFF5F5F61),
                        style = TextStyle(
                            fontSize = 16.sp,
                            fontWeight = FontWeight(400),
                        )
                    )
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun EditWCallsPreview() {
    EditCallScreen(
        state = EditCallState(
            isFreeModeSelected = false,
            isCategoryModeSelected = true,
            selectedCategory = "스포츠"
        ),
        onIntent = {}
    )

}