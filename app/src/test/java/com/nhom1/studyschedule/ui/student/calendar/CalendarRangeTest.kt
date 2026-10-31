package com.nhom1.studyschedule.ui.student.calendar

import com.nhom1.studyschedule.ui.student.calendar.CalendarViewModel.Companion.rangeOf
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.ChronoUnit

class CalendarRangeTest {
    @Test
    fun day_isSingleDate() {
        assertEquals("2026-10-31" to "2026-10-31", rangeOf(CalendarMode.DAY, "2026-10-31"))
    }

    @Test
    fun week_startsMondayEndsSunday() {
        // 2026-10-31 is a Saturday.
        assertEquals("2026-10-26" to "2026-11-01", rangeOf(CalendarMode.WEEK, "2026-10-31"))
    }

    @Test
    fun week_onSunday_staysInSameWeek() {
        assertEquals("2026-10-26" to "2026-11-01", rangeOf(CalendarMode.WEEK, "2026-11-01"))
    }

    @Test
    fun month_coversWholeGrid() {
        // October 2026: the 1st is a Thursday, the 31st a Saturday.
        assertEquals("2026-09-28" to "2026-11-01", rangeOf(CalendarMode.MONTH, "2026-10-15"))
    }

    @Test
    fun month_startingOnMonday_hasNoLeadingDays() {
        // June 2026 starts on a Monday and ends on a Tuesday.
        assertEquals("2026-06-01" to "2026-07-05", rangeOf(CalendarMode.MONTH, "2026-06-20"))
    }

    @Test
    fun month_leapFebruary() {
        // February 2028 has 29 days; the 1st and the 29th are Tuesdays.
        assertEquals("2028-01-31" to "2028-03-05", rangeOf(CalendarMode.MONTH, "2028-02-29"))
    }

    @Test
    fun month_gridIsWholeWeeks_forEveryMonth() {
        for (month in 1..12) {
            val (from, to) = rangeOf(CalendarMode.MONTH, "2026-%02d-10".format(month))
            val first = LocalDate.parse(from)
            val last = LocalDate.parse(to)
            val days = ChronoUnit.DAYS.between(first, last) + 1
            assertEquals("month $month starts on Monday", DayOfWeek.MONDAY, first.dayOfWeek)
            assertEquals("month $month ends on Sunday", DayOfWeek.SUNDAY, last.dayOfWeek)
            assertTrue("month $month has 5 or 6 rows", days == 35L || days == 42L)
        }
    }
}
