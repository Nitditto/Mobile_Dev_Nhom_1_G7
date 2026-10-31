package com.nhom1.studyschedule.data.entity

/** Read-only join of [StudySchedule] with its [Subject], for list and calendar screens. */
data class ScheduleItem(
    val id: Long,
    val studentId: Long,
    val subjectId: Long,
    val studyDate: String,
    val startTime: String,
    val endTime: String,
    val location: String,
    val status: String,
    val subjectCode: String,
    val subjectName: String
)
