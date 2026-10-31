package com.nhom1.studyschedule.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.nhom1.studyschedule.R
import com.nhom1.studyschedule.util.NotificationHelper

/** Fired by [ReminderScheduler] shortly before a session starts. */
class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val id = intent.getLongExtra(EXTRA_ID, 0)
        val subject = intent.getStringExtra(EXTRA_SUBJECT) ?: return
        val start = intent.getStringExtra(EXTRA_START).orEmpty()
        val location = intent.getStringExtra(EXTRA_LOCATION).orEmpty()

        NotificationHelper.show(
            context,
            id.toInt(),
            context.getString(R.string.reminder_title),
            context.getString(R.string.reminder_text, subject, start, location)
        )
    }

    companion object {
        const val EXTRA_ID = "schedule_id"
        const val EXTRA_SUBJECT = "subject"
        const val EXTRA_START = "start"
        const val EXTRA_LOCATION = "location"
    }
}
