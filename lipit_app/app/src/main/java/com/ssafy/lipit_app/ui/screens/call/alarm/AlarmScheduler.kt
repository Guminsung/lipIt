package com.ssafy.lipit_app.ui.screens.call.alarm
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.temporal.TemporalAdjusters
import java.util.Calendar
/**
 * 예약된 시간에 통화 알림을 표시하기 위한 스케줄러
 */
class AlarmScheduler(private val context: Context) {

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    /**
     * 지정된 시간에 통화 알림을 예약합니다
     *
     * @param time 알림이 표시될 시간
     * @param callerName 발신자 이름
     * @param alarmId 고유 알람 ID (여러 알람 구분용)
     */
    fun scheduleCallAlarm(time: LocalDateTime, callerName: String, alarmId: Int = 0, retryCount: Int) {
        // 알람이 울릴 때 실행될 인텐트 준비
        val intent = Intent(context, ScheduledCallReceiver::class.java).apply {
            putExtra("CALLER_NAME", callerName)
            putExtra("ALARM_ID", alarmId)
            putExtra(CallActionReceiver.EXTRA_RETRY_COUNT, retryCount)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            alarmId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // 시간 설정
        val triggerTimeMillis = time
            .atZone(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()

        // 알람 설정 (Android 버전에 따라 다른 메서드 사용)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Android 6.0(M) 이상에서는 Doze 모드에서도 작동하도록 설정
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerTimeMillis,
                pendingIntent
            )
        } else {
            // Android 6.0 미만에서는 setExact 사용
            alarmManager.setExact(
                AlarmManager.RTC_WAKEUP,
                triggerTimeMillis,
                pendingIntent
            )
        }

        Log.d("AlarmScheduler", "통화 알림 예약됨: $callerName, 시간: $time, ID: $alarmId, 재시도: $retryCount")
    }

    /**
     * 지정된 ID의 알람을 취소합니다
     */
    fun cancelAlarm(alarmId: Int) {
        val intent = Intent(context, ScheduledCallReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            alarmId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.cancel(pendingIntent)
        pendingIntent.cancel()

        Log.d("AlarmScheduler", "알람 취소됨: ID $alarmId")
    }
}