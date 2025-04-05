package com.ssafy.lipit_app.ui.screens.call.oncall.text_call

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.ssafy.lipit_app.R
import com.ssafy.lipit_app.data.model.ChatMessageText
import com.ssafy.lipit_app.ui.screens.call.oncall.ModeChangeButton
import com.ssafy.lipit_app.ui.screens.call.oncall.text_call.components.TextCallFooter
import com.ssafy.lipit_app.ui.screens.call.oncall.text_call.components.TextCallHeader
import com.ssafy.lipit_app.ui.screens.call.oncall.text_call.components.Translate.TextCallWithTranslate
import com.ssafy.lipit_app.ui.screens.call.oncall.text_call.components.Translate.TextCallwithOriginalOnly
import com.ssafy.lipit_app.ui.screens.call.oncall.voice_call.VoiceCallViewModel

@Composable
fun TextCallScreen(
    viewModel: TextCallViewModel,
    onIntent: (TextCallIntent) -> Unit,
    navController: NavController,
    onModeToggle: () -> Unit,
    voiceCallViewModel: VoiceCallViewModel
) {

    val state = viewModel.state.collectAsState().value
    Log.d("TextCall", "📦 메시지 수: ${state.messages.size}")
    
    val voiceCallState by voiceCallViewModel.state.collectAsState() // time 동기화를 위해 가져옴

    LaunchedEffect(Unit) {
        val textMessages = voiceCallViewModel.convertToTextMessages()
        viewModel.setInitialMessages(textMessages)
        Log.d("TextCallScreen", "이전 대화 불러와서 TextViewModel에 설정 완료")


    }

    LaunchedEffect(voiceCallViewModel.aiMessage) {
        if (voiceCallViewModel.aiMessage.isNotBlank()) {
            Log.d("TextCallScreen", "📥 AI 메시지 수신: ${voiceCallViewModel.aiMessage}")

            val newMessage = ChatMessageText(
                text = voiceCallViewModel.aiMessage,
                translatedText = voiceCallViewModel.aiMessageKor,
                isFromUser = false
            )

            // 1. TextCallViewModel에 메시지 추가
            viewModel.addMessage(newMessage, voiceCallViewModel)

            // 2. VoiceCallViewModel에도 동기화
            voiceCallViewModel.addAiMessage(
                ai = newMessage.text,
                kor = newMessage.translatedText
            )

            voiceCallViewModel.clearAiMessage()
        }
    }


    Log.d("TextCall", "🧾 메시지 렌더링 시작 - 총 ${state.messages.size}개")
    state.messages.forEachIndexed { i, m ->
        Log.d("TextCall", "🔸 [$i] ${if (m.isFromUser) "USER" else "AI"}: ${m.text}")
    }

    Box(
        modifier = Modifier
            .fillMaxWidth(),
        contentAlignment = Alignment.TopCenter
    ) {
        // 배경
        Image(
            painterResource(
                id = R.drawable.incoming_call_background
            ),
            contentDescription = "배경",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
        )

        Column(
            modifier = Modifier
                .padding(top = 55.dp, start = 20.dp, end = 20.dp, bottom = 40.dp)
                .fillMaxSize()
        ) {
            // 모드 변경
            ModeChangeButton(
                currentMode = state.currentMode,
                onToggle = {
                    // 모드 전환: 텍스트 → 보이스로 바꾸는 시점이면 chatMessages 동기화
                    voiceCallViewModel.syncFromTextMessages(viewModel.getMessages())

                    onModeToggle()
                }
            )

            // 헤더 (VoiceName, 남은 시간, 끊기 버튼)
            TextCallHeader(
                voiceName = voiceCallState.voiceName,
                leftTime = voiceCallState.leftTime
            )

            // 대화 내역(채팅 ver.)
            Box(
                modifier = Modifier.weight(1f)
            ) {
                TextVersionCall(state, onIntent)
            }

            Spacer(modifier = Modifier.height(18.dp))

            // 하단 영역 (텍스트 입력 공간, 번역 여부 및 텍스트 보내기 버튼)
            TextCallFooter(state.inputText, state.showTranslation, onIntent = onIntent)
        }
    }
}

@Composable
fun TextVersionCall(state: TextCallState, onIntent: (TextCallIntent) -> Unit) {
    // 번역 여부에 따라 UI 달라짐
    when {
        state.showTranslation -> TextCallWithTranslate(state)
        else -> TextCallwithOriginalOnly(state)
    }
}


//@Preview(showBackground = true)
//@Composable
//fun TextCallScreenPreview() {
//// 테스트용 chat 리스트 -> 기능 구현 시 삭제하기!
//    val sampleChatMessages = listOf(
//        ChatMessage(
//            text = "Hey! Long time no see! How have you been?",
//            translatedText = "오! 오랜만이야! 잘 지냈어?",
//            isFromUser = false
//        ),
//        ChatMessage(
//            text = "Yeah! I’ve been good. I recently started reading a new book.",
//            translatedText = "응! 잘 지냈어. 최근에 새 책 읽기 시작했어.",
//            isFromUser = true
//        ),
//        ChatMessage(
//            text = "Sounds interesting! What book is it?",
//            translatedText = "재미있겠다! 무슨 책이야?",
//            isFromUser = false
//        ),
//        ChatMessage(
//            text = "It’s called 'The Night Circus'. The story is magical!",
//            translatedText = "『나이트 서커스』라는 책이야. 정말 마법 같은 이야기야!",
//            isFromUser = true
//        ),
//        ChatMessage(
//            text = "Nice! I’ll check it out later.",
//            translatedText = "좋아! 나중에 꼭 읽어볼게.",
//            isFromUser = false
//        )
//    )
//
//    TextCallScreen(
//        state = TextCallState(
//            voiceName = "Harry Potter",
//            leftTime = "04:50",
//            currentMode = "Text",
//            messages = sampleChatMessages,
//            inputText = "",
//            showTranslation = true
//        ),
//        onIntent = {}
//    )
//}