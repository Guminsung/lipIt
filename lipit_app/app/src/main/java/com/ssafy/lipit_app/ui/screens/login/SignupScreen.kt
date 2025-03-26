package com.ssafy.lipit_app.ui.screens.login

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ExposedDropdownMenuBox
import androidx.compose.material.ExposedDropdownMenuDefaults
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ssafy.lipit_app.R

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun GenderDropdown(selectedGender: String, onGenderSelected: (String) -> Unit) {
    val options = listOf("Male", "Female")
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
    ) {
        OutlinedTextField(
            value = selectedGender,
            onValueChange = {},
            readOnly = true,
            placeholder = {
//                Text(text = "Gender", color = Color.White)
                          },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(
                    expanded = expanded,
                    onIconClick = { expanded = !expanded } // 클릭 시 열기/닫기 토글
                )
            },
            modifier = Modifier
//                .menuAnchor()
                .fillMaxWidth()
                .height(56.dp),
            label = { Text("Gender") },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Color(0xFFE2C7FF),
                unfocusedBorderColor = Color(0xFFE2C7FF),
                textColor = Color.White
            ),
            shape = RoundedCornerShape(12.dp),
            singleLine = true
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { gender ->
                DropdownMenuItem(
                    onClick = {
                        onGenderSelected(gender)
                        expanded = false
                    }
                ) {
                    Column {
                        Text(text = gender, color = Color.White)
                        Text(
                            text = if (gender == "Male") "남성" else "여성",
                            color = Color.Gray
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun SignupScreen() {
    val context = LocalContext.current
    var selectedGender by remember { mutableStateOf("") }


    // ID
    var id by remember { mutableStateOf("") }

    // PW
    var password by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White) // 배경은 나중에 이미지로
    ) {
        // 배경 이미지
        Image(
            painter = painterResource(id = R.drawable.bg_myvoice),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            verticalArrangement = Arrangement.SpaceBetween, // 위–아래 간격 자동 분배
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(60.dp)) // 상단 여백
            // 타이틀 이미지
            Image(
                painter = painterResource(id = R.drawable.ic_launcher_background),
                contentDescription = "타이틀 로고",
                modifier = Modifier.size(200.dp)
            )

            // 하단
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                // 왼쪽 정렬 텍스트


                Spacer(modifier = Modifier.height(10.dp))

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

                Spacer(modifier = Modifier.height(10.dp))

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
                        val icon = if (isPasswordVisible)
                            painterResource(id = R.drawable.ic_visibility_off)
                        else
                            painterResource(id = R.drawable.ic_visibility)

                        IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                            Icon(painter = icon, contentDescription = null, tint = Color(0xFFE2C7FF))
                        }
                    }
                )

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
                        val icon = if (isPasswordVisible)
                            painterResource(id = R.drawable.ic_visibility_off)
                        else
                            painterResource(id = R.drawable.ic_visibility)

                        IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                            Icon(painter = icon, contentDescription = null, tint = Color(0xFFE2C7FF))
                        }
                    }
                )

                OutlinedTextField(
                    value = id,
                    onValueChange = { id = it },
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

                GenderDropdown(selectedGender = selectedGender) {
                    selectedGender = it
                }

                Spacer(modifier = Modifier.height(20.dp))

                // 가운데 정렬 버튼 + 텍스트
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CustomFilledButton(text = "JOIN") {
                        // 시작 액션
                    }

                    Spacer(modifier = Modifier.height(12.dp))


                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SignupScreenPreview(){
    SignupScreen()
}
