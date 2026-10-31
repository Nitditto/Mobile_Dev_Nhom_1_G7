package com.nhom1.studyschedule.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

object ScheduleStatus {
    const val PENDING = "PENDING"
    const val CONFIRMED = "CONFIRMED"
    const val CANCELLED = "CANCELLED"
}

/**
 * Dates and times are stored as text so they sort and compare lexicographically in SQL:
 * [studyDate] = "yyyy-MM-dd", [startTime]/[endTime] = "HH:mm" (24h).
 */
@Entity(
    foreignKeys = [
        ForeignKey(
            entity = Student::class,
            parentColumns = ["id"],
            childColumns = ["studentId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Subject::class,
            parentColumns = ["id"],
            childColumns = ["subjectId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["studentId", "studyDate"]),
        Index(value = ["subjectId"])
    ]
)
data class StudySchedule(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val studentId: Long,
    val subjectId: Long,
    val studyDate: String,
    val startTime: String,
    val endTime: String,
    val location: String,
    val status: String = ScheduleStatus.PENDING
)
