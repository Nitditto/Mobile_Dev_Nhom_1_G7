package com.nhom1.studyschedule.ui.admin

import androidx.fragment.app.Fragment
import com.nhom1.studyschedule.R
import com.nhom1.studyschedule.ui.common.BaseHomeActivity
import com.nhom1.studyschedule.ui.common.PlaceholderFragment

class AdminHomeActivity : BaseHomeActivity() {
    override val bottomMenuRes = R.menu.menu_admin_bottom
    override val defaultTabId = R.id.nav_students

    // TODO: replace placeholders with StudentListFragment / SubjectListFragment.
    override fun createFragment(tabId: Int): Fragment = when (tabId) {
        R.id.nav_students -> PlaceholderFragment.newInstance(getString(R.string.nav_students))
        else -> PlaceholderFragment.newInstance(getString(R.string.nav_subjects))
    }
}
