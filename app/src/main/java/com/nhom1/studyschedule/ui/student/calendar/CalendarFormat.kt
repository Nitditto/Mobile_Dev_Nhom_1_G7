package com.nhom1.studyschedule.ui.student.calendar

import android.content.Context
import com.nhom1.studyschedule.R
import com.nhom1.studyschedule.util.DateTimeUtils

/** Vietnamese weekday name ("Thứ Hai" .. "Chủ Nhật") of a "yyyy-MM-dd" date. */
fun Context.weekdayName(date: String): String =
    resources.getStringArray(R.array.calendar_weekdays)[DateTimeUtils.parseDate(date).dayOfWeek.value - 1]

/** "Thứ Hai, 03/11/2026" */
fun Context.dayTitle(date: String): String =
    getString(R.string.calendar_title_day, weekdayName(date), DateTimeUtils.displayDate(date))
