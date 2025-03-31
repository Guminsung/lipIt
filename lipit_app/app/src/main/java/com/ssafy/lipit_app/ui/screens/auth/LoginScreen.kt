package com.ssafy.lipit_app.ui.screens.auth

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material3.IconButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ssafy.lipit_app.R
import androidx.compose.runtime.*


@Composable
fun LoginScreen(
    onSuccess: () -> Unit
) {
    val context = LocalContext.current

    // ID
    var id by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White) // 배경은 나중에 이미지로
    ) {
        // 배경 이미지
        Image(
            painter = painterResource(id = R.drawable.bg_login),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween, // 위–아래 간격 자동 분배
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 상단 여백
            Spacer(modifier = Modifier.height(44.dp))

            // 1. Title
            Image(
                painter = painterResource(id = R.drawable.img_title),
                contentDescription = "타이틀 로고",
                modifier = Modifier
                    .width(325.dp)
                    .wrapContentHeight(),
                contentScale = ContentScale.FillWidth
            )

            // 2. 3D 로고 영여
            Image(
                painter = painterResource(id = R.drawable.img_3d_crop),
                contentDescription = "타이틀 로고",
                modifier = Modifier
                    .fillMaxWidth() // ✅ 어떤 화면에서도 가로 꽉 채우기
                    .wrapContentHeight(),
                contentScale = ContentScale.FillWidth
            )

            // Input: ID, PWD
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                // Input: ID
                OutlinedTextField(
                    value = id,
                    onValueChange = { id = it },
                    placeholder = { Text("ID", color = Color(0xFFE2C7FF)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Color(0xFFE2C7FF),
                        unfocusedBorderColor = Color(0xFFE2C7FF),
                        textColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Input: PW
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    placeholder = { Text("PW", color = Color(0xFFE2C7FF)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Color(0xFFE2C7FF),
                        unfocusedBorderColor = Color(0xFFE2C7FF),
                        textColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                            Icon(
                                painter = painterResource(
                                    id = if (isPasswordVisible)
                                        R.drawable.ic_pw_closed2
                                    else
                                        R.drawable.ic_pw_show
                                ),
                                contentDescription = if (isPasswordVisible) "비밀번호 숨기기" else "비밀번호 보기",
                                tint = Color(0xFFE2C7FF)
                            )
                        }
                    }
                )

                Spacer(modifier = Modifier.height(50.dp))

                // 로그인 버튼
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CustomFilledButton(text = "LOGIN") {
                        Toast.makeText(
                            context,
                            "입력된 아이디: ${id}\n비밀번호: ${password}",
                            Toast.LENGTH_SHORT
                        ).show()

                        // TODO: 로그인 성공 이벤트
                        onSuccess()
                    }
                }


                Spacer(modifier = Modifier.height(94.dp))
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    LoginScreen( onSuccess = {})
}
