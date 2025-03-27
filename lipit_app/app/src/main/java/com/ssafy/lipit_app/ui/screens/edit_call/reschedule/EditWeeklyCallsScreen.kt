package com.ssafy.lipit_app.ui.screens.edit_call.reschedule

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun EditWeeklyCallsScreen(
    state: EditWeeklyCallsState,
    onIntent: (EditWeeklyCallsIntent) -> Unit
) {
    Column(
        modifier = Modifier
            .padding(top = 60.dp, start = 20.dp, end = 20.dp)
            .background(Color(0xFFFDF8FF))
            .height(
                750.dp
            )
            .fillMaxHeight(0.7f)
    ) {
        // 제목

        // 상단 - 선택 보이스 출력
        // 하단 - Call 스케줄표
    }
}

@Preview(showBackground = true)
@Composable
fun EditWeeklyCallsPreview() {
    // 테스트용 스케줄 정보
    val SampleSchedules = listOf(
        CallSchedule(
            callScheduleId = 1L,
            memberId = 101L,
            scheduleDay = "월",
            scheduledTime = "08:00",
            topicCategory = "자유주제"
        ),
        CallSchedule(
            callScheduleId = 2L,
            memberId = 101L,
            scheduleDay = "화",
            scheduledTime = "09:30",
            topicCategory = "음식"
        ),
        CallSchedule(
            callScheduleId = 3L,
            memberId = 101L,
            scheduleDay = "수",
            scheduledTime = "11:00",
            topicCategory = "여행"
        ),
        CallSchedule(
            callScheduleId = 4L,
            memberId = 101L,
            scheduleDay = "목",
            scheduledTime = "14:00",
            topicCategory = "영화"
        ),
        CallSchedule(
            callScheduleId = 5L,
            memberId = 101L,
            scheduleDay = "금",
            scheduledTime = "16:30",
            topicCategory = "스포츠"
        )
    )

    EditWeeklyCallsScreen(
        state = EditWeeklyCallsState(
            VoiceName = "Harry Potter",
            VoiceImageUrl = "https://file.notion.so/f/f/87d6e907-21b3-47d8-98dc-55005c285cce/7a38e4c0-9789-42d0-b8a0-2e3d8c421433/image.png?table=block&id=1c0fd4f4-17d0-80ed-9fa9-caa1056dc3f9&spaceId=87d6e907-21b3-47d8-98dc-55005c285cce&expirationTimestamp=1742824800000&signature=3tw9F7cAaX__HcAYxwEFal6KBsvDg2Gt0kd7VnZ4LcY&downloadName=image.png",

            callSchedules = SampleSchedules
        ),
        onIntent = {}
    )
}
