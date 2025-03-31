package com.ssafy.lipit_app.ui.screens.auth.components

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ssafy.lipit_app.ui.screens.auth.Login.LoginState

@Composable
fun CustomFilledButton(text: String, context: Context, state: LoginState, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .height(50.dp)
            .fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFFA37BBD), // 원하는 배경색
            contentColor = Color.White // 텍스트 색상
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(text = text, fontWeight = FontWeight.Bold, color = Color.White)

        Toast.makeText(
            context,
            "입력된 아이디: ${state.id}\n비밀번호: ${state.pw}",
            Toast.LENGTH_SHORT
        ).show()

        // 로그인 성공 이벤트
        LaunchedEffect(state.isLoginSuccess) {
            if (state.isLoginSuccess) {
                // main 으로 이동 및 로그인 성공 토스트 출력
                Toast.makeText(context, "로그인 성공!", Toast.LENGTH_SHORT).show()
            }
        }

        LaunchedEffect(state.errorMessage) {
            state.errorMessage?.let {
                // 로그로 에러 메시지 출력
            }
        }
    }
}

@Composable
fun CustomOutlinedButton(text: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier
            .height(50.dp)
            .fillMaxWidth(),
        border = BorderStroke(1.dp, Color(0xFFD09FE6)),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = Color.White
        )
    ) {
        Text(text = text, color = Color.White, fontWeight = FontWeight.Bold)
    }
}
