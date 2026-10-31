package com.nhom1.studyschedule.data.repository

import com.nhom1.studyschedule.R
import com.nhom1.studyschedule.data.AppDatabase
import com.nhom1.studyschedule.data.entity.ScheduleStatus
import com.nhom1.studyschedule.data.entity.StudySchedule
import com.nhom1.studyschedule.data.repository.BookingResult.Field
import com.nhom1.studyschedule.util.DateTimeUtils
import java.time.LocalTime

/**
 * Every write to StudySchedule goes through here so validation (and later notifications)
 * live in one place.
 */
class ScheduleRepository(db: AppDatabase) {
    private val dao = db.studyScheduleDao()

    suspend fun getById(id: Long): StudySchedule? = dao.getById(id)

    /** New bookings always start as PENDING. */
    suspend fun book(schedule: StudySchedule): BookingResult {
        val pending = schedule.copy(id = 0, status = ScheduleStatus.PENDING)
        return validate(pending) ?: run {
            dao.insert(pending)
            BookingResult.Success
        }
    }

    suspend fun update(schedule: StudySchedule): BookingResult =
        validate(schedule) ?: run {
            dao.update(schedule)
            BookingResult.Success
        }

    private suspend fun validate(s: StudySchedule): BookingResult? {
        if (s.subjectId == 0L) return BookingResult.Invalid(Field.SUBJECT, R.string.error_booking_subject)
        if (s.location.isBlank()) return BookingResult.Invalid(Field.LOCATION, R.string.error_required)
        if (!DateTimeUtils.isEndAfterStart(s.startTime, s.endTime)) {
            return BookingResult.Invalid(Field.END_TIME, R.string.error_booking_time_order)
        }

        val today = DateTimeUtils.today()
        if (s.studyDate < today) {
            return BookingResult.Invalid(Field.DATE, R.string.error_booking_past_date)
        }
        if (s.studyDate == today && DateTimeUtils.parseTime(s.startTime) < LocalTime.now()) {
            return BookingResult.Invalid(Field.START_TIME, R.string.error_booking_past_time)
        }

        // A cancelled session does not block anything, so it cannot conflict either.
        if (s.status != ScheduleStatus.CANCELLED) {
            dao.findConflict(s.studentId, s.studyDate, s.startTime, s.endTime, excludeId = s.id)
                ?.let { return BookingResult.Conflict(it) }
        }
        return null
    }
}
