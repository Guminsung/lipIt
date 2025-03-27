package com.ssafy.lipit_app.ui.screens.login

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ssafy.lipit_app.R
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Email

// 회워가입 메인 화면 구성
@Composable
fun SignupScreen() {
    val context = LocalContext.current

    // InputData
    var id by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordConfirm by remember { mutableStateOf("") }
    var selectedGender by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) } // 성별 드롭다운 메뉴 활/비활성화

    var isPasswordVisible_1 by remember { mutableStateOf(false) }
    var isPasswordVisible_2 by remember { mutableStateOf(false) }

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
            Spacer(modifier = Modifier.height(44.dp))

            // Title
            Image(
                painter = painterResource(id = R.drawable.img_title),
                contentDescription = "타이틀 로고",
                modifier = Modifier
                    .width(325.dp)
                    .wrapContentHeight(),
                contentScale = ContentScale.FillWidth
            )

            // 입력 폼
            Column(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp)
            ) {
                Spacer(modifier = Modifier.height(10.dp))

                // 1. Input: ID
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

                // 2. Input: PW
                SpacerHeight(18)
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
                    visualTransformation = if (isPasswordVisible_1) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        androidx.compose.material3.IconButton(onClick = {
                            isPasswordVisible_1 = !isPasswordVisible_1
                        }) {
                            Icon(
                                painter = painterResource(
                                    id = if (isPasswordVisible_1)
                                        R.drawable.ic_pw_closed2
                                    else
                                        R.drawable.ic_pw_show
                                ),
                                contentDescription = if (isPasswordVisible_1) "비밀번호 숨기기" else "비밀번호 보기",
                                tint = Color(0xFFE2C7FF)
                            )
                        }
                    }
                )

                // 3. PW 확인용
                SpacerHeight(18)
                OutlinedTextField(
                    value = passwordConfirm,
                    onValueChange = { passwordConfirm = it },
                    placeholder = { Text("Re-enter PW", color = Color(0xFFE2C7FF)) },
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
                    visualTransformation = if (isPasswordVisible_2) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        androidx.compose.material3.IconButton(onClick = {
                            isPasswordVisible_2 = !isPasswordVisible_2
                        }) {
                            Icon(
                                painter = painterResource(
                                    id = if (isPasswordVisible_2)
                                        R.drawable.ic_pw_closed2
                                    else
                                        R.drawable.ic_pw_show
                                ),
                                contentDescription = if (isPasswordVisible_2) "비밀번호 숨기기" else "비밀번호 보기",
                                tint = Color(0xFFE2C7FF)
                            )
                        }
                    }
                )

                // 4. 영문이름(별명, name) 영역
                SpacerHeight(18)
                OutlinedTextField(
                    value = name,
                    onValueChange = { // 알파벳만 허용 (대소문자 모두)

                        if (it.matches(Regex("^[a-zA-Z]*$"))) {
                            name = it
                        }
                    },
                    placeholder = { Text("English Name", color = Color(0xFFE2C7FF)) },
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

                // 5. 성별: 드뢉 다운
                SpacerHeight(18)
                Box(
                    modifier = Modifier.fillMaxWidth()
                        .clickable { expanded = true },
                ) {
                    OutlinedTextField(
                        value = selectedGender,
                        onValueChange = {},
                        readOnly = true,
                        enabled = false,
                        placeholder = { Text("Gender", color = Color(0xFFE2C7FF)) },
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = null,
                                tint = Color(0xFFE2C7FF)
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth(),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            disabledBorderColor = Color(0xFFE2C7FF),
                            disabledTextColor = Color.White,
                            disabledPlaceholderColor = Color(0xFFE2C7FF),
                        ),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )

                    // DropDown 메뉴 활성화
                    if (expanded) {
                        BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                            val maxWidth = this.maxWidth

                            DropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false },
                                modifier = Modifier
                                    .width(maxWidth) // ✅ 직접 너비 지정!
                                    .padding(horizontal = 20.dp)
                                    .background(Color.Transparent)
                            ) {
                                GenderSelectDialog(
                                    modifier = Modifier.fillMaxWidth(),
                                    onSelect = {
                                        selectedGender = it
                                    },
                                    onDismiss = {
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                // 6. 회원가입 버튼 JOIN
                SpacerHeight(28)
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // TODO: 회원가입 이벤트 정의
                    CustomFilledButton(text = "JOIN") {
                        val errorMessage = validateSignupInput(id, password, passwordConfirm, name, selectedGender)

                        if (errorMessage != null) {
                            Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "${id} ${password} ${passwordConfirm} ${name} ${selectedGender}", Toast.LENGTH_SHORT).show()
                            // TODO: 회원가입 진행 API 실행
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }

                SpacerHeight(94)
            }
        }
    }
}

// Input 유효성 검사 함수
fun validateSignupInput(
    id: String,
    password: String,
    passwordConfirm: String,
    name: String,
    selectedGender: String
): String? {
    return when {
        id.isBlank() -> "아이디를 입력해주세요." // TODO: 서버에서 중복 체크 추가 필요
        password.isBlank() -> "비밀번호를 입력해주세요."
        passwordConfirm.isBlank() -> "비밀번호 확인을 입력해주세요."
        password != passwordConfirm -> "비밀번호가 일치하지 않아요."
        name.isBlank() -> "영문 이름을 입력해주세요."
        selectedGender.isBlank() -> "성별을 선택해주세요."
        else -> null // 문제 없음
    }
}



@Preview(showBackground = true)
@Composable
fun SignupScreenPreview(){
    SignupScreen()
}
