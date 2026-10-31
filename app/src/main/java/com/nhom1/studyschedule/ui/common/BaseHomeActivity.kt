package com.nhom1.studyschedule.ui.common

import android.content.Intent
import android.os.Bundle
import androidx.annotation.MenuRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.nhom1.studyschedule.R
import com.nhom1.studyschedule.StudyScheduleApp
import com.nhom1.studyschedule.databinding.ActivityHomeBinding
import com.nhom1.studyschedule.ui.login.LoginActivity

/**
 * Shared shell for both roles: toolbar with logout + bottom navigation + fragment container.
 * Subclasses only declare their menu and which fragment each tab shows.
 */
abstract class BaseHomeActivity : AppCompatActivity() {
    protected lateinit var binding: ActivityHomeBinding
        private set

    @get:MenuRes
    protected abstract val bottomMenuRes: Int
    protected abstract val defaultTabId: Int
    protected abstract fun createFragment(tabId: Int): Fragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.title = getString(R.string.app_name)
        binding.toolbar.subtitle = (application as StudyScheduleApp).session.username
        binding.toolbar.inflateMenu(R.menu.menu_home_toolbar)
        binding.toolbar.setOnMenuItemClickListener {
            if (it.itemId == R.id.action_logout) logout()
            true
        }

        binding.bottomNav.inflateMenu(bottomMenuRes)
        binding.bottomNav.setOnItemSelectedListener {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, createFragment(it.itemId))
                .commit()
            true
        }
        if (savedInstanceState == null) binding.bottomNav.selectedItemId = defaultTabId
    }

    private fun logout() {
        (application as StudyScheduleApp).session.clear()
        startActivity(
            Intent(this, LoginActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        )
    }
}
