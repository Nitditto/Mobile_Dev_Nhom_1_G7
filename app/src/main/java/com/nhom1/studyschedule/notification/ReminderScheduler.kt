package com.nhom1.studyschedule.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.nhom1.studyschedule.StudyScheduleApp
import com.nhom1.studyschedule.data.entity.ScheduleItem
import com.nhom1.studyschedule.data.entity.ScheduleStatus
import com.nhom1.studyschedule.util.DateTimeUtils
import java.time.LocalDateTime
import java.time.ZoneId

/**
 * One inexact alarm per session, [LEAD_MINUTES] before it starts. The schedule id is the
 * PendingIntent request code, so scheduling again replaces the alarm and [cancel] hits it.
 */
object ReminderScheduler {
    const val LEAD_MINUTES = 30L

    fun schedule(context: Context, item: ScheduleItem) {
        if (item.status == ScheduleStatus.CANCELLED) {
            cancel(context, item.id)
            return
        }
        val start = LocalDateTime.of(
            DateTimeUtils.parseDate(item.studyDate),
            DateTimeUtils.parseTime(item.startTime)
        ).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val now = System.currentTimeMillis()
        if (start <= now) return

        // Booked less than LEAD_MINUTES ahead: remind right away instead of never.
        val triggerAt = maxOf(start - LEAD_MINUTES * 60_000, now)
        alarmManager(context).setAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP, triggerAt, pendingIntent(context, item.id, item)
        )
    }

    fun cancel(context: Context, scheduleId: Long) {
        alarmManager(context).cancel(pendingIntent(context, scheduleId, null))
    }

    /** Re-arms every upcoming session of the student (alarms are lost on reboot). */
    suspend fun rescheduleAll(context: Context, studentId: Long) {
        val app = context.applicationContext as StudyScheduleApp
        app.database.studyScheduleDao()
            .getUpcoming(studentId, DateTimeUtils.today())
            .forEach { schedule(app, it) }
    }

    private fun alarmManager(context: Context) =
        context.getSystemService(AlarmManager::class.java)

    private fun pendingIntent(context: Context, scheduleId: Long, item: ScheduleItem?): PendingIntent {
        val intent = Intent(context, ReminderReceiver::class.java).putExtra(ReminderReceiver.EXTRA_ID, scheduleId)
        if (item != null) {
            intent.putExtra(ReminderReceiver.EXTRA_SUBJECT, item.subjectName)
                .putExtra(ReminderReceiver.EXTRA_START, item.startTime)
                .putExtra(ReminderReceiver.EXTRA_LOCATION, item.location)
        }
        return PendingIntent.getBroadcast(
            context,
            scheduleId.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
}
