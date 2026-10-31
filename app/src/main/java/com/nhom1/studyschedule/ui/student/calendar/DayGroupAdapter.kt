package com.nhom1.studyschedule.ui.student.calendar

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.nhom1.studyschedule.R
import com.nhom1.studyschedule.databinding.ItemDayHeaderBinding
import com.nhom1.studyschedule.databinding.ItemScheduleBinding
import com.nhom1.studyschedule.ui.student.schedule.bindSchedule
import com.nhom1.studyschedule.util.DateTimeUtils

/** Day/week view: date headers interleaved with that date's sessions (reuses item_schedule). */
class DayGroupAdapter : ListAdapter<DayRow, RecyclerView.ViewHolder>(Diff) {

    override fun getItemViewType(position: Int) = when (getItem(position)) {
        is DayRow.Header -> TYPE_HEADER
        is DayRow.Session -> TYPE_SESSION
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == TYPE_HEADER) {
            HeaderHolder(ItemDayHeaderBinding.inflate(inflater, parent, false))
        } else {
            SessionHolder(ItemScheduleBinding.inflate(inflater, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val row = getItem(position)) {
            is DayRow.Header -> (holder as HeaderHolder).bind(row)
            is DayRow.Session -> (holder as SessionHolder).bind(row)
        }
    }

    class HeaderHolder(private val binding: ItemDayHeaderBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(row: DayRow.Header) {
            val context = binding.root.context
            val res = if (row.isToday) R.string.calendar_day_header_today else R.string.calendar_day_header
            binding.tvDate.text =
                context.getString(res, context.weekdayName(row.date), DateTimeUtils.displayDate(row.date))
            binding.tvCount.text = context.getString(R.string.calendar_session_count, row.activeCount)
        }
    }

    class SessionHolder(private val binding: ItemScheduleBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(row: DayRow.Session) {
            bindSchedule(binding, row.item)
            binding.btnDelete.isVisible = false
        }
    }

    private object Diff : DiffUtil.ItemCallback<DayRow>() {
        override fun areItemsTheSame(old: DayRow, new: DayRow) = when {
            old is DayRow.Header && new is DayRow.Header -> old.date == new.date
            old is DayRow.Session && new is DayRow.Session -> old.item.id == new.item.id
            else -> false
        }

        override fun areContentsTheSame(old: DayRow, new: DayRow) = old == new
    }

    private companion object {
        const val TYPE_HEADER = 0
        const val TYPE_SESSION = 1
    }
}
