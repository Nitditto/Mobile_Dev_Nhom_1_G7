package com.nhom1.studyschedule.data.repository

/** What a successful write in [ScheduleRepository] did; passed to its onScheduleChanged hook. */
sealed class ScheduleEvent {
    data object Created : ScheduleEvent()
    data object Updated : ScheduleEvent()

    /** [from] = previous ScheduleStatus value; the item passed along holds the new one. */
    data class StatusChanged(val from: String) : ScheduleEvent()

    data object Deleted : ScheduleEvent()
}
