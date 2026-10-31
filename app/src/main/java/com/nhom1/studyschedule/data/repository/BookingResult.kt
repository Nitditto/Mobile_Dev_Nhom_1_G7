package com.nhom1.studyschedule.data.repository

import androidx.annotation.StringRes
import com.nhom1.studyschedule.data.entity.ScheduleItem

sealed interface BookingResult {
    data object Success : BookingResult

    /** Overlaps [with], a non-cancelled session of the same student on the same date. */
    data class Conflict(val with: ScheduleItem) : BookingResult

    /** Input rule broken; [field] tells the form which input to mark. */
    data class Invalid(val field: Field, @StringRes val reason: Int) : BookingResult

    enum class Field { SUBJECT, DATE, START_TIME, END_TIME, LOCATION }
}
