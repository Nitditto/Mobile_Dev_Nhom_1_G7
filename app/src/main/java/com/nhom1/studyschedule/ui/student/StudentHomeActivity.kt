package com.nhom1.studyschedule.ui.student

import androidx.fragment.app.Fragment
import com.nhom1.studyschedule.R
import com.nhom1.studyschedule.ui.common.BaseHomeActivity
import com.nhom1.studyschedule.ui.student.calendar.CalendarFragment
import com.nhom1.studyschedule.ui.student.schedule.ScheduleListFragment

class StudentHomeActivity : BaseHomeActivity() {
    override val bottomMenuRes = R.menu.menu_student_bottom
    override val defaultTabId = R.id.nav_schedules

    override fun createFragment(tabId: Int): Fragment = when (tabId) {
        R.id.nav_calendar -> CalendarFragment()
        else -> ScheduleListFragment()
    }
}
