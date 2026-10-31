package com.nhom1.studyschedule.ui.admin

import androidx.fragment.app.Fragment
import com.nhom1.studyschedule.R
import com.nhom1.studyschedule.ui.admin.subject.SubjectListFragment
import com.nhom1.studyschedule.ui.common.BaseHomeActivity
import com.nhom1.studyschedule.ui.common.PlaceholderFragment

class AdminHomeActivity : BaseHomeActivity() {
    override val bottomMenuRes = R.menu.menu_admin_bottom
    override val defaultTabId = R.id.nav_students

    // TODO(Việt): replace the Students placeholder with StudentListFragment.
    override fun createFragment(tabId: Int): Fragment = when (tabId) {
        R.id.nav_subjects -> SubjectListFragment()
        else -> PlaceholderFragment.newInstance(getString(R.string.nav_students))
    }
}
