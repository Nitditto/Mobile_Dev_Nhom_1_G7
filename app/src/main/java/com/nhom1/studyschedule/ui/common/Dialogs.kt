package com.nhom1.studyschedule.ui.common

import android.content.Context
import androidx.fragment.app.Fragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.nhom1.studyschedule.R
import com.nhom1.studyschedule.data.entity.ScheduleItem
import com.nhom1.studyschedule.util.DateTimeUtils

fun Fragment.confirmDelete(message: String, onConfirm: () -> Unit) {
    MaterialAlertDialogBuilder(requireContext())
        .setTitle(R.string.confirm_delete_title)
        .setMessage(message)
        .setNegativeButton(R.string.action_cancel, null)
        .setPositiveButton(R.string.action_delete) { _, _ -> onConfirm() }
        .show()
}

/** BookingResult.Conflict: tells the student which session already holds the slot. */
fun Context.showScheduleConflict(other: ScheduleItem) {
    MaterialAlertDialogBuilder(this)
        .setTitle(R.string.booking_conflict_title)
        .setMessage(
            getString(
                R.string.booking_conflict_message,
                other.subjectName,
                DateTimeUtils.displayDate(other.studyDate),
                other.startTime,
                other.endTime
            )
        )
        .setPositiveButton(android.R.string.ok, null)
        .show()
}
