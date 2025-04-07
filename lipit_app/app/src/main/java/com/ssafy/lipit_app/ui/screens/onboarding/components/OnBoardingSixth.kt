package com.ssafy.lipit_app.ui.screens.onboarding.components

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.ssafy.lipit_app.R

@Composable
fun OnBoardingSixth(onNext: () -> Unit) {

    val context = LocalContext.current

    // 권한 상태 관리
    var notificationPermissionGranted by remember {
        mutableStateOf(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            } else {
                true // Android 13 미만에서는 알림 권한이 필요하지 않음
            }
        )
    }

    var microphonePermissionGranted by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    var storagePermissionGranted by remember {
        mutableStateOf(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.READ_MEDIA_IMAGES
                ) == PackageManager.PERMISSION_GRANTED
            } else {
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
            }
        )
    }


    // 개별 권한 요청 런처들
    val requestNotificationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        notificationPermissionGranted = isGranted
        if (isGranted) {
            Toast.makeText(context, "알림 권한이 허용되었습니다", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "알림 기능 사용을 위해 권한이 필요합니다", Toast.LENGTH_SHORT).show()
        }
    }

    val requestMicrophonePermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        microphonePermissionGranted = isGranted
        if (isGranted) {
            Toast.makeText(context, "마이크 권한이 허용되었습니다", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "통화 및 음성 인식을 위해 마이크 권한이 필요합니다", Toast.LENGTH_SHORT).show()
        }
    }

    val requestStoragePermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        storagePermissionGranted = isGranted
        if (isGranted) {
            Toast.makeText(context, "저장소 권한이 허용되었습니다", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "사진 등록을 위해 저장소 권한이 필요합니다", Toast.LENGTH_SHORT).show()
        }
    }

    // 다중 권한 요청 런처
    val multiplePermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        permissions.entries.forEach { (permission, isGranted) ->
            when (permission) {
                Manifest.permission.RECORD_AUDIO -> {
                    microphonePermissionGranted = isGranted
                    if (isGranted) {
                        Log.d("Granted", "마이크 권한이 허용되었습니다")
                    }
                }

                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.READ_MEDIA_IMAGES -> {
                    storagePermissionGranted = isGranted
                    if (isGranted) {
                        Log.d("Granted", "저장소 권한이 허용되었습니다")
                    }
                }

                Manifest.permission.POST_NOTIFICATIONS -> {
                    notificationPermissionGranted = isGranted
                    if (isGranted) {
                        Log.d("Granted", "알림 권한이 허용되었습니다")
                    }
                }
            }
        }

        // 권한 거부 메시지
        if (!microphonePermissionGranted || !storagePermissionGranted ||
            (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !notificationPermissionGranted)
        ) {
            Toast.makeText(context, "필수 권한을 허용하지 않으면 서비스 이용이 제한됩니다", Toast.LENGTH_LONG).show()
        }
    }

    // 권한 체크 및 요청
    LaunchedEffect(Unit) {
        val permissionsToRequest = mutableListOf<String>()

        // 마이크 권한 확인
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissionsToRequest.add(Manifest.permission.RECORD_AUDIO)
        } else {
            microphonePermissionGranted = true
        }

        // 저장소 권한 확인 (Android 버전에 따라 다른 권한 사용)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.READ_MEDIA_IMAGES
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                permissionsToRequest.add(Manifest.permission.READ_MEDIA_IMAGES)
            } else {
                storagePermissionGranted = true
            }
        } else {
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                permissionsToRequest.add(Manifest.permission.READ_EXTERNAL_STORAGE)
            } else {
                storagePermissionGranted = true
            }
        }

        // 알림 권한 확인 (Android 13 이상에서만)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                permissionsToRequest.add(Manifest.permission.POST_NOTIFICATIONS)
            } else {
                notificationPermissionGranted = true
            }
        } else {
            // Android 13 미만에서는 알림 권한이 필요하지 않음
            notificationPermissionGranted = true
        }

        // 필요한 권한 요청
        if (permissionsToRequest.isNotEmpty()) {
            multiplePermissionLauncher.launch(permissionsToRequest.toTypedArray())
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .paint(
                painter = painterResource(id = R.drawable.bg_onboarding),
                contentScale = ContentScale.FillBounds
            )
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .windowInsetsPadding(WindowInsets.ime)
            .imePadding()
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 30.dp)
                .windowInsetsPadding(WindowInsets.safeDrawing),
            horizontalAlignment = Alignment.Start
        ) {
            Spacer(modifier = Modifier.height(120.dp))


            Text(
                text = "앱 사용을 위해\n접근 권한을 허용해주세요",
                color = Color.White,
                fontSize = 25.sp,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Start,
                lineHeight = 40.sp
            )

            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = "필수 권한",
                color = Color.White.copy(0.7f),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(10.dp))

            // 권한 상태 표시
            CustomText(
                image = R.drawable.ic_bell,
                title = "알림",
                content = "통화 수신 및 부재중 알림 발송",
                isGranted = notificationPermissionGranted,
                onRequestPermission = {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        requestNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                }
            )

            CustomText(
                image = R.drawable.ic_camera,
                title = "갤러리",
                content = "커스텀 보이스 사진 등록",
                isGranted = storagePermissionGranted,
                onRequestPermission = {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        requestStoragePermissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
                    } else {
                        requestStoragePermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                    }
                }
            )

            CustomText(
                image = R.drawable.ic_microphone,
                title = "마이크",
                content = "통화 및 음성 인식",
                isGranted = microphonePermissionGranted,
                onRequestPermission = {
                    requestMicrophonePermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                }
            )

            Spacer(modifier = Modifier.height(50.dp))

            Text(
                text = "필수 권한의 경우 허용하지 않으면 주요 기능 사용이\n" +
                        "불가능하여 서비스 이용이 제한됩니다.",
                color = Color(0xffC494D9),
                fontSize = 15.sp,
                fontWeight = FontWeight.Light,
                lineHeight = 30.sp
            )
        }


        // 하단 버튼 - 박스 맨 아래에 배치
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .align(Alignment.BottomCenter)
                .background(Color(0xff603981))
                .clickable(onClick = onNext),
            contentAlignment = Alignment.TopCenter
        ) {
            Text(
                text = "다음",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 25.dp)
            )
        }
    }
}

@Composable
fun CustomText(
    image: Int,
    title: String,
    content: String,
    isGranted: Boolean = false,
    onRequestPermission: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp)
            .clickable(onClick = onRequestPermission),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Image(
            painter = painterResource(id = image),
            contentDescription = null,
            modifier = Modifier.size(25.dp)
        )

        Text(
            text = title,
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = content,
            color = Color(0xffC494D9),
            fontSize = 15.sp
        )

        Spacer(modifier = Modifier.weight(1f))

        // 권한 상태 표시
        Text(
            text = if (isGranted) "허용됨" else "허용 필요",
            color = if (isGranted) Color.Green else Color(0xFFFFAA00),
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Preview(showBackground = true)
@Composable
fun BoardingSixthPreview() {
    OnBoardingSixth {
    }
}