package com.nhom1.studyschedule.ui.admin

import androidx.fragment.app.Fragment
import com.nhom1.studyschedule.R
import com.nhom1.studyschedule.ui.admin.student.StudentListFragment
import com.nhom1.studyschedule.ui.admin.subject.SubjectListFragment
import com.nhom1.studyschedule.ui.common.BaseHomeActivity

class AdminHomeActivity : BaseHomeActivity() {
    override val bottomMenuRes = R.menu.menu_admin_bottom
    override val defaultTabId = R.id.nav_students

    override fun createFragment(tabId: Int): Fragment = when (tabId) {
        R.id.nav_subjects -> SubjectListFragment()
        else -> StudentListFragment()
    }
}
