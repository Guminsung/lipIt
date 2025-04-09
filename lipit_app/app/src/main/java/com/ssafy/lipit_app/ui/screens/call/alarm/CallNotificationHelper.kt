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
    private var vibrator: Vibrator? = null
    private var applicationContext: Context? = null
    private var isVibrating = false

    /**
     * 전화 알림 채널 생성
     */
    fun createCallNotificationChannel(context: Context) {

        if (applicationContext == null) {
            applicationContext = context.applicationContext
        }


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

        if (isVibrating) {
            Log.d("Vibration", "이미 진동 중입니다")
            return
        }
        try {
            // Vibrator 가져오기 (기존 객체 재사용 또는 새로 생성)
            vibrator = vibrator ?: getVibrator(context)

            Log.d("Vibration", "진동 시작: ${vibrator.hashCode()}")

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val vibrationEffect = VibrationEffect.createWaveform(VIBRATION_PATTERN, 0) // 인덱스 0부터 반복
                vibrator?.vibrate(vibrationEffect)
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(VIBRATION_PATTERN, 0) // 인덱스 0부터 반복
            }

            isVibrating = true
        } catch (e: Exception) {
            Log.e("Vibration", "진동 시작 중 오류: ${e.message}")
        }
    }

    /**
     * 진동 중지
     */
    @SuppressLint("ServiceCast")
    fun stopVibration(context: Context) {
        if (!isVibrating) {
            Log.d("Vibration", "진동 중이 아닙니다")
            return
        }
        try {

            val vibratorToUse = vibrator ?: getVibrator(context)

            Log.d("Vibration", "진동 중지: ${vibratorToUse?.hashCode()}")

            vibratorToUse?.cancel()

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val stopEffect = VibrationEffect.createOneShot(1, 0) // 1ms, 강도 0 (실질적으로 중지)
                vibratorToUse?.vibrate(stopEffect)
            }

            isVibrating = false

        } catch (e: Exception) {
            Log.e("Vibration", "진동 중지 중 오류: ${e.message}")
        }
    }

    /**
     * Vibrator 서비스 가져오기
     */
    private fun getVibrator(context: Context): Vibrator? {
        try {
            // 저장된 애플리케이션 컨텍스트 사용
            val ctx = applicationContext ?: context.applicationContext
            applicationContext = ctx

            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = ctx.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vibratorManager?.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                ctx.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            }
        } catch (e: Exception) {
            Log.e("Vibration", "Vibrator 서비스 가져오기 실패: ${e.message}")
            return null
        }
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
                            R.drawable.img_add_image
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

        val ctx = applicationContext ?: context.applicationContext
        stopVibration(ctx)

        val notificationManager = NotificationManagerCompat.from(ctx)
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