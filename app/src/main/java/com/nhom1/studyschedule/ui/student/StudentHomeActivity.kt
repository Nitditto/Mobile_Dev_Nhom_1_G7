package com.nhom1.studyschedule.ui.student

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.nhom1.studyschedule.R
import com.nhom1.studyschedule.ui.common.BaseHomeActivity
import com.nhom1.studyschedule.ui.student.calendar.CalendarFragment
import com.nhom1.studyschedule.ui.student.schedule.ScheduleListFragment

class StudentHomeActivity : BaseHomeActivity() {
    override val bottomMenuRes = R.menu.menu_student_bottom
    override val defaultTabId = R.id.nav_schedules

    // Denying is fine: NotificationHelper silently skips when the permission is missing.
    private val notificationPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) requestNotificationPermission()
    }

    override fun createFragment(tabId: Int): Fragment = when (tabId) {
        R.id.nav_calendar -> CalendarFragment()
        else -> ScheduleListFragment()
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return
        val permission = Manifest.permission.POST_NOTIFICATIONS
        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            notificationPermission.launch(permission)
        }
    }
}
