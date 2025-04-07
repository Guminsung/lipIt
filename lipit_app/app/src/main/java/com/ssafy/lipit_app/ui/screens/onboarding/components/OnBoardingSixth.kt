package com.ssafy.lipit_app.ui.screens.onboarding.components

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
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
    var showPermissionToast by remember { mutableStateOf(false) }

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

    // 모든 권한이 허용되었는지 확인
    fun areAllPermissionsGranted(): Boolean {
        // 실시간으로 권한 상태 갱신
        microphonePermissionGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED

        storagePermissionGranted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
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

        notificationPermissionGranted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true // Android 13 미만에서는 알림 권한이 필요하지 않음
        }

        Log.d(
            "Permissions",
            "마이크: $microphonePermissionGranted, 저장소: $storagePermissionGranted, 알림: $notificationPermissionGranted"
        )

        return microphonePermissionGranted && storagePermissionGranted &&
                (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU || notificationPermissionGranted)
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
        var updatedAny = false

        permissions.entries.forEach { (permission, isGranted) ->
            when (permission) {
                Manifest.permission.RECORD_AUDIO -> {
                    microphonePermissionGranted = isGranted
                    updatedAny = true
                    Log.d("Permissions", "마이크 권한 업데이트: $isGranted")
                }

                Manifest.permission.READ_EXTERNAL_STORAGE -> {
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
                        storagePermissionGranted = isGranted
                        updatedAny = true
                        Log.d("Permissions", "저장소 권한(READ_EXTERNAL_STORAGE) 업데이트: $isGranted")
                    }
                }

                Manifest.permission.READ_MEDIA_IMAGES -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        storagePermissionGranted = isGranted
                        updatedAny = true
                        Log.d("Permissions", "저장소 권한(READ_MEDIA_IMAGES) 업데이트: $isGranted")
                    }
                }

                Manifest.permission.POST_NOTIFICATIONS -> {
                    notificationPermissionGranted = isGranted
                    updatedAny = true
                    Log.d("Permissions", "알림 권한 업데이트: $isGranted")
                }
            }
        }

        if (updatedAny) {
            Log.d(
                "Permissions",
                "최종 권한 상태 - 마이크: $microphonePermissionGranted, 저장소: $storagePermissionGranted, 알림: $notificationPermissionGranted"
            )
        }
    }


    fun openAppSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", context.packageName, null)
        }
        context.startActivity(intent)
    }

    // 모든 필요한 권한 요청 함수
    fun requestMissingPermissions() {
        // 실시간 권한 상태 확인
        val hasAudioPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED

        val hasStoragePermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
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

        val hasNotificationPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }

        // 빈 목록으로 시작해 필요한 권한 추가
        val permissionsToRequest = mutableListOf<String>()

        // 누락된 권한 추가
        if (!hasAudioPermission) {
            permissionsToRequest.add(Manifest.permission.RECORD_AUDIO)
        }

        if (!hasStoragePermission) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                permissionsToRequest.add(Manifest.permission.READ_MEDIA_IMAGES)
            } else {
                permissionsToRequest.add(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !hasNotificationPermission) {
            permissionsToRequest.add(Manifest.permission.POST_NOTIFICATIONS)
        }

        // 한 번에 모든 필요한 권한 요청
        if (permissionsToRequest.isNotEmpty()) {
            Log.d("Permissions", "요청할 권한: $permissionsToRequest")
            multiplePermissionLauncher.launch(permissionsToRequest.toTypedArray())
        } else {
            // 특별한 케이스: 알림 권한이 거부된 경우 설정 화면으로 안내
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !hasNotificationPermission) {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.fromParts("package", context.packageName, null)
                }
                Toast.makeText(context, "설정 화면에서 알림 권한을 활성화해주세요", Toast.LENGTH_LONG).show()
                context.startActivity(intent)
            }
        }
    }

    // 모든 필요한 권한 요청 함수
    fun requestAllPermissions() {
        val permissionsToRequest = mutableListOf<String>()

        // 마이크 권한 확인
        if (!microphonePermissionGranted) {
            permissionsToRequest.add(Manifest.permission.RECORD_AUDIO)
        }

        // 저장소 권한 확인
        if (!storagePermissionGranted) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                permissionsToRequest.add(Manifest.permission.READ_MEDIA_IMAGES)
            } else {
                permissionsToRequest.add(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }

        // 알림 권한 확인
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !notificationPermissionGranted) {
            permissionsToRequest.add(Manifest.permission.POST_NOTIFICATIONS)
        }

        if (permissionsToRequest.isNotEmpty()) {
            Log.d("Permissions", "한 번에 요청할 권한: $permissionsToRequest")
            multiplePermissionLauncher.launch(permissionsToRequest.toTypedArray())
        } else {
            Log.d("Permissions", "요청할 권한 없음, 모든 권한 이미 허용됨")
        }
    }

    // 권한 체크 및 요청
    LaunchedEffect(Unit) {
        requestAllPermissions()
    }

    // 토스트 메시지 표시 효과
    LaunchedEffect(showPermissionToast) {
        if (showPermissionToast) {
            Toast.makeText(context, "필수 권한을 모두 허용해야 진행할 수 있습니다", Toast.LENGTH_LONG).show()
            showPermissionToast = false
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
                .clickable(onClick = {
                    Log.d("OnBoardingSixth", "다음 버튼 클릭됨, 권한 상태 확인 중")

                    // 모든 권한이 허용되었는지 확인
                    val allGranted = areAllPermissionsGranted()
                    Log.d("OnBoardingSixth", "모든 권한 허용 여부: $allGranted")

                    if (allGranted) {
                        // 모든 권한이 허용되었으면 다음으로 진행
                        Log.d("OnBoardingSixth", "모든 권한 허용됨, 다음 화면으로 이동")
                        onNext()
                    } else {
                        // 권한이 없으면 권한 요청 다이얼로그 표시 후 토스트 메시지
                        Log.d("OnBoardingSixth", "일부 권한 누락, 권한 요청 다이얼로그 표시")
                        requestMissingPermissions()
                        openAppSettings()
                        showPermissionToast = true
                    }
                }),
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