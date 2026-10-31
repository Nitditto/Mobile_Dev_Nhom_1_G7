package com.nhom1.studyschedule.ui.student.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nhom1.studyschedule.data.dao.StudyScheduleDao
import com.nhom1.studyschedule.data.entity.ScheduleItem
import com.nhom1.studyschedule.data.entity.ScheduleStatus
import com.nhom1.studyschedule.util.DateTimeUtils
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

enum class CalendarMode { DAY, WEEK, MONTH }

/** One row of the day/week list: a date header followed by that date's sessions. */
sealed interface DayRow {
    data class Header(val date: String, val activeCount: Int, val isToday: Boolean) : DayRow
    data class Session(val item: ScheduleItem) : DayRow
}

/** One cell of the month grid. [activeCount] ignores cancelled sessions. */
data class MonthCell(
    val date: String,
    val dayOfMonth: Int,
    val inMonth: Boolean,
    val isToday: Boolean,
    val activeCount: Int
)

/** Everything the calendar screen draws; [rows] is used by DAY/WEEK, [cells] by MONTH. */
data class CalendarUi(
    val mode: CalendarMode,
    val selectedDate: String,
    val fromDate: String,
    val toDate: String,
    val rows: List<DayRow>,
    val cells: List<MonthCell>
)

/**
 * State = [CalendarMode] + selected date ("yyyy-MM-dd"). The visible date range is derived from
 * both and observed from Room, so bookings/cancellations elsewhere refresh the calendar.
 */
class CalendarViewModel(
    private val dao: StudyScheduleDao,
    private val studentId: Long
) : ViewModel() {
    private val mode = MutableStateFlow(CalendarMode.WEEK)
    private val selectedDate = MutableStateFlow(DateTimeUtils.today())

    @OptIn(ExperimentalCoroutinesApi::class)
    val ui: StateFlow<CalendarUi?> = combine(mode, selectedDate, ::Pair)
        .flatMapLatest { (m, date) ->
            val (from, to) = rangeOf(m, date)
            dao.observeBetween(studentId, from, to).map { buildUi(m, date, from, to, it) }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    fun setMode(value: CalendarMode) {
        mode.value = value
    }

    /** Moves one day/week/month back ([step] = -1) or forward ([step] = 1). */
    fun shift(step: Long) {
        val date = DateTimeUtils.parseDate(selectedDate.value)
        val moved = when (mode.value) {
            CalendarMode.DAY -> date.plusDays(step)
            CalendarMode.WEEK -> date.plusWeeks(step)
            CalendarMode.MONTH -> date.plusMonths(step)
        }
        selectedDate.value = DateTimeUtils.formatDate(moved)
    }

    fun goToday() {
        selectedDate.value = DateTimeUtils.today()
    }

    /** Tapping a month cell opens the day view of that date. */
    fun openDay(date: String) {
        selectedDate.value = date
        mode.value = CalendarMode.DAY
    }

    private fun buildUi(
        m: CalendarMode,
        date: String,
        from: String,
        to: String,
        items: List<ScheduleItem>
    ): CalendarUi {
        val today = DateTimeUtils.today()
        val byDate = items.groupBy { it.studyDate }
        return when (m) {
            CalendarMode.MONTH -> CalendarUi(m, date, from, to, emptyList(), buildCells(date, from, to, byDate, today))
            else -> CalendarUi(m, date, from, to, buildRows(byDate, today), emptyList())
        }
    }

    private fun buildRows(byDate: Map<String, List<ScheduleItem>>, today: String): List<DayRow> =
        byDate.flatMap { (date, sessions) ->
            listOf(DayRow.Header(date, sessions.activeCount(), date == today)) +
                sessions.map { DayRow.Session(it) }
        }

    private fun buildCells(
        selected: String,
        from: String,
        to: String,
        byDate: Map<String, List<ScheduleItem>>,
        today: String
    ): List<MonthCell> {
        val month = DateTimeUtils.parseDate(selected)
        val last = DateTimeUtils.parseDate(to)
        return generateSequence(DateTimeUtils.parseDate(from)) { it.plusDays(1) }
            .takeWhile { !it.isAfter(last) }
            .map { day ->
                val key = DateTimeUtils.formatDate(day)
                MonthCell(
                    date = key,
                    dayOfMonth = day.dayOfMonth,
                    inMonth = day.month == month.month && day.year == month.year,
                    isToday = key == today,
                    activeCount = byDate[key].orEmpty().activeCount()
                )
            }
            .toList()
    }

    private fun List<ScheduleItem>.activeCount() = count { it.status != ScheduleStatus.CANCELLED }

    companion object {
        /**
         * DAY = (d, d); WEEK = Monday..Sunday; MONTH = the full 5-6 week grid (Monday before the
         * 1st .. Sunday after the last day) so faded leading/trailing cells show real counts.
         */
        fun rangeOf(mode: CalendarMode, date: String): Pair<String, String> = when (mode) {
            CalendarMode.DAY -> date to date
            CalendarMode.WEEK -> DateTimeUtils.weekRange(date)
            CalendarMode.MONTH -> {
                val (first, last) = DateTimeUtils.monthRange(date)
                DateTimeUtils.weekRange(first).first to DateTimeUtils.weekRange(last).second
            }
        }
    }
}
