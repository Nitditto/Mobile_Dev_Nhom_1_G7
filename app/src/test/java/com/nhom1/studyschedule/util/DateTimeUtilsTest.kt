package com.nhom1.studyschedule.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class DateTimeUtilsTest {
    @Test
    fun isEndAfterStart_laterEnd_true() {
        assertTrue(DateTimeUtils.isEndAfterStart("07:00", "09:30"))
    }

    @Test
    fun isEndAfterStart_sameTime_false() {
        assertFalse(DateTimeUtils.isEndAfterStart("07:00", "07:00"))
    }

    @Test
    fun isEndAfterStart_earlierEnd_false() {
        assertFalse(DateTimeUtils.isEndAfterStart("13:00", "12:59"))
    }

    @Test
    fun weekRange_midWeek_mondayToSunday() {
        // 2026-10-28 is a Wednesday.
        assertEquals("2026-10-26" to "2026-11-01", DateTimeUtils.weekRange("2026-10-28"))
    }

    @Test
    fun weekRange_onMonday_startsSameDay() {
        assertEquals("2026-10-26" to "2026-11-01", DateTimeUtils.weekRange("2026-10-26"))
    }

    @Test
    fun weekRange_onSunday_endsSameDay() {
        assertEquals("2026-10-26" to "2026-11-01", DateTimeUtils.weekRange("2026-11-01"))
    }

    @Test
    fun weekRange_acrossYear() {
        // 2026-12-31 is a Thursday.
        assertEquals("2026-12-28" to "2027-01-03", DateTimeUtils.weekRange("2026-12-31"))
    }

    @Test
    fun monthRange_31DayMonth() {
        assertEquals("2026-10-01" to "2026-10-31", DateTimeUtils.monthRange("2026-10-15"))
    }

    @Test
    fun monthRange_februaryLeapYear() {
        assertEquals("2028-02-01" to "2028-02-29", DateTimeUtils.monthRange("2028-02-10"))
    }

    @Test
    fun monthRange_februaryCommonYear() {
        assertEquals("2026-02-01" to "2026-02-28", DateTimeUtils.monthRange("2026-02-10"))
    }

    @Test
    fun pickerMillis_roundTrip() {
        val date = "2026-10-31"
        assertEquals(date, DateTimeUtils.fromPickerMillis(DateTimeUtils.toPickerMillis(date)))
    }
}
