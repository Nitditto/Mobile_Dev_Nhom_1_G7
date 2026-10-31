package com.nhom1.studyschedule.util

import android.content.Context
import android.content.res.ColorStateList
import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import com.google.android.material.chip.Chip
import com.nhom1.studyschedule.R
import com.nhom1.studyschedule.data.entity.ScheduleStatus

/** Display helpers for [ScheduleStatus] values stored in the database. */
object StatusUi {
    /** Alpha of the chip fill, so the status color stays readable in light and dark themes. */
    private const val CHIP_FILL_ALPHA = 0x29

    @StringRes
    fun labelRes(status: String): Int = when (status) {
        ScheduleStatus.CONFIRMED -> R.string.status_confirmed
        ScheduleStatus.CANCELLED -> R.string.status_cancelled
        else -> R.string.status_pending
    }

    /** Pending = orange, Confirmed = green, Cancelled = grey. */
    @ColorRes
    fun colorRes(status: String): Int = when (status) {
        ScheduleStatus.CONFIRMED -> R.color.status_confirmed
        ScheduleStatus.CANCELLED -> R.color.status_cancelled
        else -> R.color.status_pending
    }

    fun label(context: Context, status: String): String = context.getString(labelRes(status))

    fun color(context: Context, status: String): Int =
        ContextCompat.getColor(context, colorRes(status))

    /** Shows [status] on [chip]: label, colored text and a tinted fill. */
    fun bindChip(chip: Chip, status: String) {
        val color = color(chip.context, status)
        chip.text = label(chip.context, status)
        chip.setTextColor(color)
        chip.chipBackgroundColor =
            ColorStateList.valueOf(ColorUtils.setAlphaComponent(color, CHIP_FILL_ALPHA))
        chip.chipStrokeWidth = 0f
    }
}
