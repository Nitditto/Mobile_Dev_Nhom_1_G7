package com.nhom1.studyschedule.ui.student.booking

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nhom1.studyschedule.R
import com.nhom1.studyschedule.data.dao.SubjectDao
import com.nhom1.studyschedule.data.entity.StudySchedule
import com.nhom1.studyschedule.data.entity.Subject
import com.nhom1.studyschedule.data.repository.BookingResult
import com.nhom1.studyschedule.data.repository.BookingResult.Field
import com.nhom1.studyschedule.data.repository.ScheduleRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

/** Form values; null date/time = not picked yet. Formats follow DateTimeUtils. */
data class BookingForm(
    val subjectId: Long = 0,
    val date: String? = null,
    val startTime: String? = null,
    val endTime: String? = null,
    val location: String = ""
)

class BookingViewModel(
    subjectDao: SubjectDao,
    private val repository: ScheduleRepository,
    private val studentId: Long
) : ViewModel() {

    val subjects: StateFlow<List<Subject>> = subjectDao.observeAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val _form = MutableStateFlow(BookingForm())
    val form: StateFlow<BookingForm> = _form.asStateFlow()

    fun setSubject(id: Long) = _form.update { it.copy(subjectId = id) }
    fun setDate(date: String) = _form.update { it.copy(date = date) }
    fun setStartTime(time: String) = _form.update { it.copy(startTime = time) }
    fun setEndTime(time: String) = _form.update { it.copy(endTime = time) }
    fun setLocation(location: String) = _form.update { it.copy(location = location) }

    suspend fun save(): BookingResult {
        val f = _form.value
        val date = f.date
            ?: return BookingResult.Invalid(Field.DATE, R.string.error_booking_pick_date)
        val start = f.startTime
            ?: return BookingResult.Invalid(Field.START_TIME, R.string.error_booking_pick_time)
        val end = f.endTime
            ?: return BookingResult.Invalid(Field.END_TIME, R.string.error_booking_pick_time)

        return repository.book(
            StudySchedule(
                studentId = studentId,
                subjectId = f.subjectId,
                studyDate = date,
                startTime = start,
                endTime = end,
                location = f.location.trim()
            )
        )
    }
}
