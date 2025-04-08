package com.ssafy.lipit_app.ui.screens.call.alarm

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.Person
import androidx.core.graphics.drawable.IconCompat
import com.ssafy.lipit_app.MainActivity
import com.ssafy.lipit_app.R

object CallNotificationHelper {
    private const val CALL_CHANNEL_ID = "call_channel"
    const val CALL_NOTIFICATION_ID = 1001
    private const val MISSED_CALL_NOTIFICATION_ID = 1002

    // 진동 패턴 정의: 0ms 대기, 500ms 진동, 500ms 대기, 500ms 진동 (반복)
    private val VIBRATION_PATTERN = longArrayOf(0, 500, 500, 500)

    /**
     * 전화 알림 채널 생성
     */
    fun createCallNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CALL_CHANNEL_ID,
                "전화 알림",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "수신 전화 알림을 표시합니다"
                setShowBadge(true)
                enableVibration(true)
                vibrationPattern = VIBRATION_PATTERN
            }

            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    /**
     * 진동 시작
     */
    @SuppressLint("ServiceCast")
    fun startVibration(context: Context) {
        // SDK 버전에 따라 Vibrator 획득 방법이 다름
        val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }

        // 진동 지원 여부 확인
        if (!vibrator.hasVibrator()) {
            Log.d("CallNotificationHelper", "기기가 진동을 지원하지 않습니다")
            return
        }

        // API 레벨에 따라 다른 방식으로 진동 실행
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Android 8.0 이상에서는 VibrationEffect 사용
            val vibrationEffect = VibrationEffect.createWaveform(VIBRATION_PATTERN, 1) // 두 번째 파라미터 -1: 반복 안함, 0 이상: 해당 인덱스부터 반복
            vibrator.vibrate(vibrationEffect)
        } else {
            // Android 8.0 미만에서는 deprecated API 사용
            @Suppress("DEPRECATION")
            vibrator.vibrate(VIBRATION_PATTERN, 1) // 두 번째 파라미터 -1: 반복 안함, 0 이상: 해당 인덱스부터 반복
        }
    }

    /**
     * 진동 중지
     */
    @SuppressLint("ServiceCast")
    fun stopVibration(context: Context) {
        val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }

        vibrator.cancel()
    }

    /**
     * CallStyle 알림 표시
     */
    fun showCallNotification(
        context: Context,
        callerName: String,
        callerPhotoUri: Uri? = null,
        declineIntent: PendingIntent? = null
    ) {
        // 수락
        val acceptIntent = PendingIntent.getBroadcast(
            context,
            0,
            Intent(context, CallActionReceiver::class.java).apply {
                action = CallActionReceiver.ACTION_ACCEPT_CALL
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // 거절
        val finalDeclineIntent = declineIntent ?: PendingIntent.getBroadcast(
            context,
            1,
            Intent(context, CallActionReceiver::class.java).apply {
                action = CallActionReceiver.ACTION_DECLINE_CALL
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // 알림을 탭했을 때 열릴 액티비티
        val contentIntent = PendingIntent.getActivity(
            context,
            2,
            Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                putExtra("NAVIGATION_DESTINATION", "inComingCall")
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // 발신자 Person 객체 생성
        val caller = Person.Builder()
            .setName(callerName)
            .apply {
                if (callerPhotoUri != null) {
                    setIcon(IconCompat.createWithContentUri(callerPhotoUri))
                } else {
                    setIcon(
                        IconCompat.createWithResource(
                            context,
                            R.drawable.ic_launcher_foreground
                        )
                    )
                }
            }
            .build()

        // 알림 생성
        val builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            NotificationCompat.Builder(context, CALL_CHANNEL_ID)
                .setSmallIcon(R.drawable.notification_icon)
                .setContentTitle("Lip It")
                .setContentText("${callerName}님으로부터 전화가 왔습니다")
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setCategory(NotificationCompat.CATEGORY_CALL)
                .setFullScreenIntent(contentIntent, true)
                .setStyle(
                    NotificationCompat.CallStyle.forIncomingCall(
                        caller,
                        finalDeclineIntent,
                        acceptIntent
                    )
                )
                .setAutoCancel(true)
        } else {
            // Android 11 이하에서는 일반 알림 사용
            NotificationCompat.Builder(context, CALL_CHANNEL_ID)
                .setSmallIcon(R.drawable.notification_icon)
                .setContentTitle("Lip It")
                .setContentText("${callerName}님으로부터 전화가 왔습니다")
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setCategory(NotificationCompat.CATEGORY_CALL)
                .setFullScreenIntent(contentIntent, true)
                .addAction(
                    R.drawable.incoming_call_decline,
                    "거절",
                    finalDeclineIntent
                )
                .addAction(
                    R.drawable.incoming_call_accept,
                    "수락",
                    acceptIntent
                )
                .setAutoCancel(true)
        }

        startVibration(context)

        // 알림 표시
        val notificationManager = NotificationManagerCompat.from(context)
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED || Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU
        ) {
            notificationManager.notify(CALL_NOTIFICATION_ID, builder.build())
        }
    }

    /**
     * 전화 알림 취소
     */
    fun cancelCallNotification(context: Context) {
        stopVibration(context)

        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.cancel(CALL_NOTIFICATION_ID)
    }

    /**
     * 알림이 아직 활성 상태인지 확인
     */
    fun isNotificationActive(context: Context, notificationId: Int): Boolean {
        val notificationManager = NotificationManagerCompat.from(context)
        val activeNotifications = notificationManager.activeNotifications
        return activeNotifications.any { it.id == notificationId }
    }

    /**
     * 부재중 전화 알림 표시
     */
    fun showMissedCallNotification(context: Context, callerName: String, retryCount: Int) {
        // 알림을 탭했을 때 열릴 액티비티
        val contentIntent = PendingIntent.getActivity(
            context,
            2,
            Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                putExtra("NAVIGATION_DESTINATION", "missedCalls")
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val retryString =
            if (retryCount == 0) "${callerName}님으로부터 부재중 전화" else "${callerName}님으로부터 부재중 전화(${retryCount + 1})"

        // 알림 생성
        val builder = NotificationCompat.Builder(context, CALL_CHANNEL_ID)
            .setSmallIcon(R.drawable.notification_icon)
            .setContentTitle("부재중 전화")
            .setContentText(retryString)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_CALL)
            .setContentIntent(contentIntent)
            .setAutoCancel(true)

        // 알림 표시
        val notificationManager = NotificationManagerCompat.from(context)
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED || Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU
        ) {
            notificationManager.notify(MISSED_CALL_NOTIFICATION_ID, builder.build())
        }
    }

}