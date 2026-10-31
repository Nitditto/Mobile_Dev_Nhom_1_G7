package com.nhom1.studyschedule.data.repository

import com.nhom1.studyschedule.R
import com.nhom1.studyschedule.StudyScheduleApp
import com.nhom1.studyschedule.data.entity.ScheduleItem
import com.nhom1.studyschedule.data.entity.ScheduleStatus
import com.nhom1.studyschedule.data.entity.StudySchedule
import com.nhom1.studyschedule.data.repository.BookingResult.Field
import com.nhom1.studyschedule.notification.ChangeNotifier
import com.nhom1.studyschedule.notification.ReminderScheduler
import com.nhom1.studyschedule.util.DateTimeUtils
import java.time.LocalTime

/**
 * Every write to StudySchedule goes through here so validation and side effects
 * (reminders, change notifications) live in one place: each successful write ends
 * with [onScheduleChanged]. Takes the app (not just the database) because AlarmManager
 * and notifications need a Context.
 */
class ScheduleRepository(private val app: StudyScheduleApp) {
    private val dao = app.database.studyScheduleDao()

    suspend fun getById(id: Long): StudySchedule? = dao.getById(id)

    /** New bookings always start as PENDING. */
    suspend fun book(schedule: StudySchedule): BookingResult {
        val pending = schedule.copy(id = 0, status = ScheduleStatus.PENDING)
        validate(pending)?.let { return it }
        val id = dao.insert(pending)
        notifyChanged(ScheduleEvent.Created, id)
        return BookingResult.Success
    }

    suspend fun update(schedule: StudySchedule): BookingResult {
        validate(schedule)?.let { return it }
        dao.update(schedule)
        notifyChanged(ScheduleEvent.Updated, schedule.id)
        return BookingResult.Success
    }

    /**
     * Pending / Confirmed / Cancelled. Only the status changes, so date/time rules are not
     * re-checked (a past session can still be marked). Leaving CANCELLED re-checks conflicts,
     * because another session may have taken that slot meanwhile.
     */
    suspend fun changeStatus(id: Long, newStatus: String): BookingResult {
        val current = dao.getById(id)
            ?: return BookingResult.Invalid(Field.SUBJECT, R.string.error_booking_missing)
        if (current.status == newStatus) return BookingResult.Success

        if (current.status == ScheduleStatus.CANCELLED) {
            dao.findConflict(
                current.studentId, current.studyDate, current.startTime, current.endTime,
                excludeId = current.id
            )?.let { return BookingResult.Conflict(it) }
        }
        dao.updateStatus(id, newStatus)
        notifyChanged(ScheduleEvent.StatusChanged(from = current.status), id)
        return BookingResult.Success
    }

    suspend fun delete(id: Long) {
        // Read before deleting: the hook still needs the subject/time of the removed session.
        val item = dao.getItemById(id) ?: return
        dao.deleteById(id)
        onScheduleChanged(ScheduleEvent.Deleted, item)
    }

    private suspend fun notifyChanged(event: ScheduleEvent, id: Long) {
        dao.getItemById(id)?.let { onScheduleChanged(event, it) }
    }

    /**
     * Single hook after every successful write; [item] is the state after the write
     * (before it, for Deleted).
     */
    private fun onScheduleChanged(event: ScheduleEvent, item: ScheduleItem) {
        when (event) {
            ScheduleEvent.Created, ScheduleEvent.Updated -> ReminderScheduler.schedule(app, item)
            is ScheduleEvent.StatusChanged ->
                if (item.status == ScheduleStatus.CANCELLED) {
                    ReminderScheduler.cancel(app, item.id)
                } else {
                    ReminderScheduler.schedule(app, item)
                }
            ScheduleEvent.Deleted -> ReminderScheduler.cancel(app, item.id)
        }
        ChangeNotifier.notify(app, event, item)
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
