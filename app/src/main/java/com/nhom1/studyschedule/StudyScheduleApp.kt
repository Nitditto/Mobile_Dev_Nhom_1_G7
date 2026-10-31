package com.nhom1.studyschedule

import android.app.Application
import com.nhom1.studyschedule.data.AppDatabase
import com.nhom1.studyschedule.data.SessionManager
import com.nhom1.studyschedule.util.NotificationHelper

class StudyScheduleApp : Application() {
    val database: AppDatabase by lazy { AppDatabase.get(this) }
    val session: SessionManager by lazy { SessionManager(this) }

    override fun onCreate() {
        super.onCreate()
        NotificationHelper.createChannel(this)
    }
}
