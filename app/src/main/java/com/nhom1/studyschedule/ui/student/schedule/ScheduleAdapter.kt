package com.nhom1.studyschedule.ui.student.schedule

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.nhom1.studyschedule.R
import com.nhom1.studyschedule.data.entity.ScheduleItem
import com.nhom1.studyschedule.databinding.ItemScheduleBinding
import com.nhom1.studyschedule.util.DateTimeUtils
import com.nhom1.studyschedule.util.StatusUi

class ScheduleAdapter(
    private val onClick: (ScheduleItem) -> Unit,
    private val onDelete: (ScheduleItem) -> Unit
) : ListAdapter<ScheduleItem, ScheduleAdapter.Holder>(Diff) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = Holder(
        ItemScheduleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: Holder, position: Int) = holder.bind(getItem(position))

    inner class Holder(private val binding: ItemScheduleBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ScheduleItem) {
            bindSchedule(binding, item)
            binding.root.setOnClickListener { onClick(item) }
            binding.btnDelete.setOnClickListener { onDelete(item) }
        }
    }

    private object Diff : DiffUtil.ItemCallback<ScheduleItem>() {
        override fun areItemsTheSame(old: ScheduleItem, new: ScheduleItem) = old.id == new.id
        override fun areContentsTheSame(old: ScheduleItem, new: ScheduleItem) = old == new
    }
}

/** Fills [item_schedule.xml]; shared with the calendar screens. */
fun bindSchedule(binding: ItemScheduleBinding, item: ScheduleItem) {
    val context = binding.root.context
    binding.tvSubject.text =
        context.getString(R.string.schedule_subject, item.subjectName, item.subjectCode)
    binding.tvDateTime.text = context.getString(
        R.string.schedule_date_time,
        DateTimeUtils.displayDate(item.studyDate),
        item.startTime,
        item.endTime
    )
    binding.tvLocation.text = context.getString(R.string.schedule_location, item.location)
    binding.tvStatus.text = StatusUi.label(context, item.status)
}
