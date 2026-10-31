package com.nhom1.studyschedule.notification

import android.content.Context
import com.nhom1.studyschedule.R
import com.nhom1.studyschedule.data.entity.ScheduleItem
import com.nhom1.studyschedule.data.entity.ScheduleStatus
import com.nhom1.studyschedule.data.repository.ScheduleEvent
import com.nhom1.studyschedule.util.DateTimeUtils
import com.nhom1.studyschedule.util.NotificationHelper
import com.nhom1.studyschedule.util.StatusUi

/** "Schedule changed" notification, shown after each successful write in ScheduleRepository. */
object ChangeNotifier {
    fun notify(context: Context, event: ScheduleEvent, item: ScheduleItem) {
        val session = context.getString(
            R.string.change_session,
            item.subjectName,
            DateTimeUtils.displayDate(item.studyDate),
            item.startTime,
            item.endTime,
            item.location
        )
        val text = when (event) {
            ScheduleEvent.Created -> context.getString(R.string.change_created, session)
            ScheduleEvent.Updated -> context.getString(R.string.change_updated, session)
            ScheduleEvent.Deleted -> context.getString(R.string.change_deleted, session)
            is ScheduleEvent.StatusChanged ->
                if (item.status == ScheduleStatus.CANCELLED) {
                    context.getString(R.string.change_cancelled, session)
                } else {
                    context.getString(
                        R.string.change_status, session, StatusUi.label(context, item.status)
                    )
                }
        }
        val title = context.getString(R.string.change_title)
        NotificationHelper.show(context, notificationId(item.id), title, text)
    }

    /**
     * Reminders use the schedule id itself as notification id; negate it so a change
     * notification never replaces a reminder. Ids start at 1, so they never meet at 0.
     */
    private fun notificationId(scheduleId: Long): Int = -scheduleId.toInt()
}
