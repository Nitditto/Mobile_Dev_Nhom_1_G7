package com.nhom1.studyschedule.ui.student.calendar

import android.content.res.ColorStateList
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.color.MaterialColors
import com.nhom1.studyschedule.R
import com.nhom1.studyschedule.databinding.ItemCalendarDayBinding
import com.nhom1.studyschedule.util.DateTimeUtils
import androidx.appcompat.R as AppCompatR
import com.google.android.material.R as MaterialR

/** Month view, used with GridLayoutManager(7): Monday-first, cells outside the month are faded. */
class MonthGridAdapter(
    private val onDayClick: (String) -> Unit
) : ListAdapter<MonthCell, MonthGridAdapter.Holder>(Diff) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = Holder(
        ItemCalendarDayBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: Holder, position: Int) = holder.bind(getItem(position))

    inner class Holder(private val binding: ItemCalendarDayBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(cell: MonthCell) {
            val root = binding.root
            val context = root.context

            binding.tvDay.text = cell.dayOfMonth.toString()
            if (cell.isToday) {
                binding.tvDay.setBackgroundResource(R.drawable.bg_circle)
                binding.tvDay.backgroundTintList =
                    ColorStateList.valueOf(MaterialColors.getColor(root, AppCompatR.attr.colorPrimary))
                binding.tvDay.setTextColor(MaterialColors.getColor(root, MaterialR.attr.colorOnPrimary))
                binding.tvDay.setTypeface(null, Typeface.BOLD)
            } else {
                binding.tvDay.background = null
                binding.tvDay.setTextColor(MaterialColors.getColor(root, MaterialR.attr.colorOnSurface))
                binding.tvDay.setTypeface(null, Typeface.NORMAL)
            }

            // INVISIBLE (not GONE) keeps every cell the same height.
            binding.tvCount.visibility = if (cell.activeCount > 0) View.VISIBLE else View.INVISIBLE
            binding.tvCount.text = cell.activeCount.toString()
            binding.tvCount.backgroundTintList = ColorStateList.valueOf(
                MaterialColors.getColor(root, MaterialR.attr.colorPrimaryContainer)
            )
            binding.tvCount.setTextColor(
                MaterialColors.getColor(root, MaterialR.attr.colorOnPrimaryContainer)
            )

            root.alpha = if (cell.inMonth) 1f else OUTSIDE_MONTH_ALPHA
            root.contentDescription = context.getString(
                R.string.calendar_cell_description,
                DateTimeUtils.displayDate(cell.date),
                cell.activeCount
            )
            root.setOnClickListener { onDayClick(cell.date) }
        }
    }

    private object Diff : DiffUtil.ItemCallback<MonthCell>() {
        override fun areItemsTheSame(old: MonthCell, new: MonthCell) = old.date == new.date
        override fun areContentsTheSame(old: MonthCell, new: MonthCell) = old == new
    }

    private companion object {
        const val OUTSIDE_MONTH_ALPHA = 0.38f
    }
}
