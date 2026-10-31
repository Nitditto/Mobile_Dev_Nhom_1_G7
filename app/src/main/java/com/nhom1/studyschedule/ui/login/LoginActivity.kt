package com.nhom1.studyschedule.ui.login

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.nhom1.studyschedule.R
import com.nhom1.studyschedule.StudyScheduleApp
import com.nhom1.studyschedule.data.SessionManager
import com.nhom1.studyschedule.data.entity.Role
import com.nhom1.studyschedule.databinding.ActivityLoginBinding
import com.nhom1.studyschedule.notification.ReminderScheduler
import com.nhom1.studyschedule.ui.admin.AdminHomeActivity
import com.nhom1.studyschedule.ui.student.StudentHomeActivity
import com.nhom1.studyschedule.util.PasswordHasher
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val app get() = application as StudyScheduleApp

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (app.session.isLoggedIn) {
            openHome(app.session.role)
            return
        }

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnLogin.setOnClickListener { attemptLogin() }
    }

    private fun attemptLogin() {
        val username = binding.etUsername.text?.toString().orEmpty().trim()
        val password = binding.etPassword.text?.toString().orEmpty()
        if (username.isEmpty() || password.isEmpty()) {
            binding.tilPassword.error = getString(R.string.error_login_empty)
            return
        }
        binding.tilPassword.error = null

        lifecycleScope.launch {
            val db = app.database
            val account = db.accountDao().login(username, PasswordHasher.hash(password))
            if (account == null) {
                binding.tilPassword.error = getString(R.string.error_login_invalid)
                return@launch
            }
            val studentId = if (account.role == Role.STUDENT) {
                db.studentDao().getByAccountId(account.id)?.id ?: SessionManager.NO_ID
            } else {
                SessionManager.NO_ID
            }
            app.session.save(account.id, account.username, account.role, studentId)
            if (studentId != SessionManager.NO_ID) {
                ReminderScheduler.rescheduleAll(this@LoginActivity, studentId)
            }
            openHome(account.role)
        }
    }

    private fun openHome(role: String) {
        val target = if (role == Role.ADMIN) AdminHomeActivity::class.java
        else StudentHomeActivity::class.java
        startActivity(Intent(this, target))
        finish()
    }
}
