package com.nhom1.studyschedule.ui.student.schedule

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.nhom1.studyschedule.R
import com.nhom1.studyschedule.data.entity.ScheduleItem
import com.nhom1.studyschedule.data.entity.ScheduleStatus
import com.nhom1.studyschedule.databinding.ItemScheduleBinding
import com.nhom1.studyschedule.util.DateTimeUtils
import com.nhom1.studyschedule.util.StatusUi

class ScheduleAdapter(
    private val onClick: (ScheduleItem) -> Unit,
    private val onDelete: (ScheduleItem) -> Unit,
    /** (item, new ScheduleStatus value) */
    private val onStatusChange: (ScheduleItem, String) -> Unit
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
            binding.root.setOnLongClickListener {
                showStatusMenu(binding.btnMore, item)
                true
            }
            binding.btnMore.setOnClickListener { showStatusMenu(it, item) }
            binding.btnDelete.setOnClickListener { onDelete(item) }
        }
    }

    /** Pending / Confirmed / Cancelled actions, without the one matching the current status. */
    private fun showStatusMenu(anchor: View, item: ScheduleItem) {
        val popup = PopupMenu(anchor.context, anchor)
        popup.inflate(R.menu.menu_schedule_status)
        popup.menu.findItem(R.id.action_status_confirm).isVisible =
            item.status != ScheduleStatus.CONFIRMED
        popup.menu.findItem(R.id.action_status_cancel).isVisible =
            item.status != ScheduleStatus.CANCELLED
        popup.menu.findItem(R.id.action_status_pending).isVisible =
            item.status != ScheduleStatus.PENDING
        popup.setOnMenuItemClickListener { menuItem ->
            val status = when (menuItem.itemId) {
                R.id.action_status_confirm -> ScheduleStatus.CONFIRMED
                R.id.action_status_cancel -> ScheduleStatus.CANCELLED
                R.id.action_status_pending -> ScheduleStatus.PENDING
                else -> return@setOnMenuItemClickListener false
            }
            onStatusChange(item, status)
            true
        }
        popup.show()
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
    StatusUi.bindChip(binding.chipStatus, item.status)
}
