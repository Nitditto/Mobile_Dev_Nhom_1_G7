package com.nhom1.studyschedule.data

import android.content.Context
import androidx.core.content.edit

/** Stores the logged-in account in SharedPreferences. */
class SessionManager(context: Context) {
    private val prefs = context.applicationContext
        .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    val isLoggedIn: Boolean get() = accountId != NO_ID
    val accountId: Long get() = prefs.getLong(KEY_ACCOUNT_ID, NO_ID)
    /** Only meaningful for the STUDENT role; [NO_ID] for admins. */
    val studentId: Long get() = prefs.getLong(KEY_STUDENT_ID, NO_ID)
    val username: String get() = prefs.getString(KEY_USERNAME, "").orEmpty()
    val role: String get() = prefs.getString(KEY_ROLE, "").orEmpty()

    fun save(accountId: Long, username: String, role: String, studentId: Long = NO_ID) {
        prefs.edit {
            putLong(KEY_ACCOUNT_ID, accountId)
            putString(KEY_USERNAME, username)
            putString(KEY_ROLE, role)
            putLong(KEY_STUDENT_ID, studentId)
        }
    }

    fun clear() = prefs.edit { clear() }

    companion object {
        const val NO_ID = -1L
        private const val PREFS_NAME = "session"
        private const val KEY_ACCOUNT_ID = "account_id"
        private const val KEY_STUDENT_ID = "student_id"
        private const val KEY_USERNAME = "username"
        private const val KEY_ROLE = "role"
    }
}
