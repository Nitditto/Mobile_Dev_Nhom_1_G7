package com.nhom1.studyschedule.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.nhom1.studyschedule.StudyScheduleApp
import com.nhom1.studyschedule.data.SessionManager
import com.nhom1.studyschedule.data.entity.Role
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/** Alarms do not survive a reboot, so re-arm them for the logged-in student. */
class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) return
        val session = (context.applicationContext as StudyScheduleApp).session
        if (session.role != Role.STUDENT || session.studentId == SessionManager.NO_ID) return

        val pending = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                ReminderScheduler.rescheduleAll(context, session.studentId)
            } finally {
                pending.finish()
            }
        }
    }
}
