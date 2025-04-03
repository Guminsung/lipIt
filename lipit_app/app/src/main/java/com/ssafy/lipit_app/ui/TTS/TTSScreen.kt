import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ssafy.lipit_app.data.model.ChatMessage
import com.ssafy.lipit_app.ui.screens.call.oncall.TTSViewModel

@Composable
fun TTSScreen(modifier: Modifier = Modifier, viewModel: TTSViewModel = viewModel()) {
    val context = LocalContext.current
    val textState = remember { mutableStateOf("") }
    val chatMessages = remember { mutableStateListOf<ChatMessage>() }

    // ExoPlayer 초기화
    LaunchedEffect(Unit) {
        viewModel.initializePlayer(context)
    }

    // AI 메시지 도착 시 대화에 추가
    LaunchedEffect(viewModel.aiMessage) {
        if (viewModel.aiMessage.isNotBlank()) {
            chatMessages.add(
                ChatMessage(
                    type = "ai",
                    message = viewModel.aiMessage,
                    messageKor = viewModel.aiMessageKor
                )
            )
            viewModel.clearAiMessage()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(text = viewModel.connectionStatusText)

        // 대화 내용 출력
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            items(chatMessages) { msg ->
                Column(modifier = Modifier.padding(vertical = 4.dp)) {
                    Text(
                        text = if (msg.type == "user") "🙋 You:" else "🤖 AI:",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray
                    )
                    Text(text = msg.message, style = MaterialTheme.typography.bodyLarge)
                    msg.messageKor?.let {
                        Text(
                            text = "🇰🇷 $it",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray
                        )
                    }
                }
            }
        }

        // 대화 종료 메시지
        if (viewModel.isCallEnded) {
            Text(
                text = "🔚 대화가 종료되었습니다.",
                color = Color.Red,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        // 입력창
        TextField(
            value = textState.value,
            onValueChange = { textState.value = it },
            label = { Text("메시지 입력") },
            modifier = Modifier.fillMaxWidth(),
            enabled = !viewModel.isCallEnded
        )

        // 버튼 영역
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            /** 대화 시작 버튼 */
            Button(
                onClick = {
                    viewModel.sendStartCall(memberId = 6, topic = null)
                    chatMessages.clear()
                },
                enabled = !viewModel.isWaitingResponse && viewModel.callId == null,
                modifier = Modifier.weight(1f)
            ) {
                Text("대화 시작")
            }

            /** 메시지 전송 버튼 */
            Button(
                onClick = {
                    chatMessages.add(ChatMessage("user", textState.value))
                    viewModel.sendText(textState.value)
                    textState.value = ""
                },
                enabled = !viewModel.isWaitingResponse && viewModel.callId != null && !viewModel.isCallEnded,
                modifier = Modifier.weight(1f)
            ) {
                if (viewModel.isWaitingResponse) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                }
                Text("전송")
            }

            /** 대화 종료 버튼 */
            Button(
                onClick = {
                    viewModel.sendEndCall()
                },
                enabled = !viewModel.isWaitingResponse && viewModel.callId != null && !viewModel.isCallEnded,
                modifier = Modifier.weight(1f)
            ) {
                Text("대화 종료")
            }
        }

        /** 대화 종료 후 초기화 버튼 */
        if (viewModel.isCallEnded) {
            Button(
                onClick = {
                    viewModel.resetCall()
                    chatMessages.clear()
                    textState.value = ""
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            ) {
                Text("새로운 대화 시작하기")
            }
        }
    }
}
