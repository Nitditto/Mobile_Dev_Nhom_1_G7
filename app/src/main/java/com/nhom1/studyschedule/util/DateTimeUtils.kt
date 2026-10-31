package com.nhom1.studyschedule.util

import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters

/**
 * Single place to convert between DB strings and java.time.
 * Storage format: date "yyyy-MM-dd", time "HH:mm". Display format: date "dd/MM/yyyy".
 */
object DateTimeUtils {
    private val DATE: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    private val TIME: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")
    private val DISPLAY_DATE: DateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    fun parseDate(value: String): LocalDate = LocalDate.parse(value, DATE)
    fun formatDate(date: LocalDate): String = date.format(DATE)

    fun parseTime(value: String): LocalTime = LocalTime.parse(value, TIME)
    fun formatTime(time: LocalTime): String = time.format(TIME)
    fun formatTime(hour: Int, minute: Int): String = formatTime(LocalTime.of(hour, minute))

    fun displayDate(value: String): String = parseDate(value).format(DISPLAY_DATE)

    fun today(): String = formatDate(LocalDate.now())

    fun isEndAfterStart(start: String, end: String): Boolean =
        parseTime(end).isAfter(parseTime(start))

    /** Monday..Sunday of the week containing [date], inclusive. */
    fun weekRange(date: String): Pair<String, String> {
        val monday = parseDate(date).with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
        return formatDate(monday) to formatDate(monday.plusDays(6))
    }

    /** First..last day of the month containing [date], inclusive. */
    fun monthRange(date: String): Pair<String, String> {
        val d = parseDate(date)
        return formatDate(d.withDayOfMonth(1)) to formatDate(d.with(TemporalAdjusters.lastDayOfMonth()))
    }

    /** MaterialDatePicker returns UTC midnight millis. */
    fun fromPickerMillis(millis: Long): String =
        formatDate(Instant.ofEpochMilli(millis).atZone(ZoneOffset.UTC).toLocalDate())

    fun toPickerMillis(date: String): Long =
        parseDate(date).atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
}
