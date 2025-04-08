package com.ssafy.lipit_app.ui.screens.call.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.ssafy.lipit_app.MainActivity
import java.time.LocalDateTime

class CallActionReceiver : BroadcastReceiver() {
    companion object {
        const val ACTION_ACCEPT_CALL = "com.ssafy.lipit_app.ACTION_ACCEPT_CALL"
        const val ACTION_DECLINE_CALL = "com.ssafy.lipit_app.ACTION_DECLINE_CALL"
        const val ACTION_MISSED_CALL = "com.ssafy.lipit_app.ACTION_MISSED_CALL"
        const val MAX_RETRY_COUNT = 2   // 전화 재시도 횟수
        const val RETRY_INTERVAL_MINUTES = 5  // 5분마다 전화시도
        const val EXTRA_RETRY_COUNT = "RETRY_COUNT"
        const val MISSED_CALL_TIMEOUT_SECONDS = 15  // 30초 후 부재중 처리
    }

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            ACTION_ACCEPT_CALL -> {
                Log.d("CallReceiver", "전화 수락됨")
                // 전화 수락 로직 => VoiceCall 화면으로 이동
                val acceptIntent = Intent(context, MainActivity::class.java).apply {
                    flags =
                        Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    putExtra("NAVIGATION_DESTINATION", "onVoiceCall")
                }
                context.startActivity(acceptIntent)

                // 알림 취소
                CallNotificationHelper.cancelCallNotification(context)
            }

            ACTION_DECLINE_CALL -> {
                Log.d("CallReceiver", "전화 거절됨")

                // 현재 부재중 횟수 확인
                val currentRetryCount = intent.getIntExtra(EXTRA_RETRY_COUNT, 0)
                val callerName = intent.getStringExtra("CALLER_NAME") ?: "SARANG"
                val alarmId = intent.getIntExtra("ALARM_ID", 0)

                if (currentRetryCount < MAX_RETRY_COUNT) {

                    val nextAlarmId = alarmId + 1000 + (currentRetryCount + 1)

                    // 5분 후
                    val nextAlarmTime =
                        LocalDateTime.now().plusMinutes(RETRY_INTERVAL_MINUTES.toLong())

                    // 새 알림 설정
                    val alarmScheduler = AlarmScheduler(context)
                    alarmScheduler.scheduleCallAlarm(
                        time = nextAlarmTime,
                        callerName = callerName,
                        alarmId = nextAlarmId,
                        retryCount = currentRetryCount + 1
                    )

                    Log.d(
                        "CallReceiver",
                        "재시도 알람 설정됨: ${currentRetryCount + 1}번째 재시도, 시간: $nextAlarmTime"
                    )
                } else {
                    Log.d("CallReceiver", "최대 재시도 횟수에 도달했습니다")
                }

                // 알림 취소
                CallNotificationHelper.cancelCallNotification(context)
            }

            ACTION_MISSED_CALL -> {
                Log.d("CallReceiver", "전화 부재중 처리됨")

                // 현재 재시도 횟수 확인
                val currentRetryCount = intent.getIntExtra(EXTRA_RETRY_COUNT, 0)
                val callerName = intent.getStringExtra("CALLER_NAME") ?: "Sarang"
                val alarmId = intent.getIntExtra("ALARM_ID", 0)

                if (currentRetryCount < MAX_RETRY_COUNT) {
                    // 다음 알림 id 계산
                    val nextAlarmId = alarmId + 1000 + (currentRetryCount + 1)

                    // 다음 알림 시간
                    val nextAlarmTime =
                        LocalDateTime.now().plusMinutes(RETRY_INTERVAL_MINUTES.toLong())

                    // 새로운 알림 설정
                    val alarmScheduler = AlarmScheduler(context)
                    alarmScheduler.scheduleCallAlarm(
                        time = nextAlarmTime,
                        callerName = callerName,
                        alarmId = nextAlarmId,
                        retryCount = currentRetryCount + 1
                    )

                    Log.d("CallerReceiver", "부재중 통화 시도 알림 설정됨 : ${currentRetryCount + 1}")
                } else {
                    Log.d("CallReceiver", "최대 재시도 횟수에 도달했습니다")
                }

                CallNotificationHelper.stopVibration(context)
            }
        }
    }

}