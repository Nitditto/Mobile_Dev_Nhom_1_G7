package com.nhom1.studyschedule.util

import android.content.Context
import androidx.annotation.StringRes
import com.nhom1.studyschedule.R
import com.nhom1.studyschedule.data.entity.ScheduleStatus

/** Display helpers for [ScheduleStatus] values stored in the database. */
object StatusUi {
    @StringRes
    fun labelRes(status: String): Int = when (status) {
        ScheduleStatus.CONFIRMED -> R.string.status_confirmed
        ScheduleStatus.CANCELLED -> R.string.status_cancelled
        else -> R.string.status_pending
    }

    fun label(context: Context, status: String): String = context.getString(labelRes(status))
}
