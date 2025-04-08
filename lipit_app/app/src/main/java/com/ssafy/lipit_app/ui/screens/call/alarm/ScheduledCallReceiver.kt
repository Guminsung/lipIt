package com.ssafy.lipit_app.ui.screens.call.alarm

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Looper
import android.util.Log
import android.os.Handler
import android.telecom.Call

/**
 * 예약된 시간에 호출되어 통화 알림을 표시하는 BroadcastReceiver
 */
private const val TAG = "ScheduledCallReceiver"

class ScheduledCallReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        Log.d(TAG, "예약된 통화 알림 시간이 되었습니다")

        // 인텐트에서 발신자 정보 추출
        val callerName = intent.getStringExtra("CALLER_NAME") ?: "SARANG"
        val alarmId = intent.getIntExtra("ALARM_ID", 0)
        val retryCount = intent.getIntExtra(CallActionReceiver.EXTRA_RETRY_COUNT, 0)

        Log.d(TAG, "발신자: $callerName, 알람 ID: $alarmId, 재시도: $retryCount")

        // 통화 알림 표시
        try {
            // 재시도 카운트가 있으면 CallNotificationHelper에 전달
            val notificationIntent = Intent(context, CallActionReceiver::class.java).apply {
                action = CallActionReceiver.ACTION_DECLINE_CALL
                putExtra(CallActionReceiver.EXTRA_RETRY_COUNT, retryCount)
                putExtra("CALLER_NAME", callerName)
                putExtra("ALARM_ID", alarmId)
            }

            val declineIntent = PendingIntent.getBroadcast(
                context,
                1,
                notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            // 재시도 정보가 포함된 알림 표시
            CallNotificationHelper.showCallNotification(
                context = context,
//                callerName = if (retryCount > 0) "$callerName (재시도 $retryCount/3)" else callerName,
                callerName = callerName,
                declineIntent = declineIntent
            )

            // 타임 아웃 핸들러
            val handler = Handler(Looper.getMainLooper())
            handler.postDelayed({
                // 알림이 아직 활성 상태인지
                if (CallNotificationHelper.isNotificationActive(
                        context,
                        CallNotificationHelper.CALL_NOTIFICATION_ID
                    )
                ) {
                    // 부재중 처리
                    val missCallIntent = Intent(context, CallActionReceiver::class.java).apply {
                        action = CallActionReceiver.ACTION_MISSED_CALL
                        putExtra("CALLER_NAME", callerName)
                        putExtra("ALARM_ID", alarmId)
                        putExtra(CallActionReceiver.EXTRA_RETRY_COUNT, retryCount)
                    }

                    context.sendBroadcast(missCallIntent)

                    // 알림 취소
                    CallNotificationHelper.cancelCallNotification(context)

                    // 부재중 알림 표시
                    CallNotificationHelper.showMissedCallNotification(context, callerName, retryCount)
                    Log.d(TAG, "통화 응답 없음 - 부재중 처리")
                }
            }, CallActionReceiver.MISSED_CALL_TIMEOUT_SECONDS * 1000L)

            Log.d(TAG, "통화 알림 표시 성공")
        } catch (e: Exception) {
            CallNotificationHelper.stopVibration(context)
            Log.e(TAG, "통화 알림 표시 실패: ${e.message}", e)
        }
    }
}

private fun Any.postDelayed(function: () -> Unit) {

}
