package com.nhom1.studyschedule.ui.student.calendar

import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.nhom1.studyschedule.R
import com.nhom1.studyschedule.databinding.FragmentCalendarBinding
import com.nhom1.studyschedule.ui.common.appViewModels
import com.nhom1.studyschedule.util.DateTimeUtils
import kotlinx.coroutines.launch

/** Student tab "Lịch": day / week / month views of the student's own sessions. */
class CalendarFragment : Fragment(R.layout.fragment_calendar) {
    private val viewModel by appViewModels {
        CalendarViewModel(it.database.studyScheduleDao(), it.session.studentId)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val binding = FragmentCalendarBinding.bind(view)
        val dayAdapter = DayGroupAdapter()
        val monthAdapter = MonthGridAdapter(onDayClick = viewModel::openDay)

        binding.rvDays.adapter = dayAdapter
        binding.rvMonth.adapter = monthAdapter
        // The default add/change animation resets alpha to 1 and would undo the faded
        // out-of-month cells; the grid is replaced wholesale on navigation anyway.
        binding.rvMonth.itemAnimator = null
        addWeekdayLabels(binding.weekdayRow)

        binding.toggleMode.addOnButtonCheckedListener { _, buttonId, isChecked ->
            if (isChecked) viewModel.setMode(modeOf(buttonId))
        }
        binding.btnPrev.setOnClickListener { viewModel.shift(-1) }
        binding.btnNext.setOnClickListener { viewModel.shift(1) }
        binding.btnToday.setOnClickListener { viewModel.goToday() }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.ui.collect { ui ->
                    if (ui != null) render(binding, ui, dayAdapter, monthAdapter)
                }
            }
        }
    }

    private fun render(
        binding: FragmentCalendarBinding,
        ui: CalendarUi,
        dayAdapter: DayGroupAdapter,
        monthAdapter: MonthGridAdapter
    ) {
        // Keeps the toggle in sync when the mode changes from code (tap a month cell, rotation).
        val buttonId = buttonOf(ui.mode)
        if (binding.toggleMode.checkedButtonId != buttonId) binding.toggleMode.check(buttonId)

        binding.tvTitle.text = titleOf(ui)

        val isMonth = ui.mode == CalendarMode.MONTH
        binding.rvMonth.isVisible = isMonth
        binding.weekdayRow.isVisible = isMonth
        binding.rvDays.isVisible = !isMonth
        dayAdapter.submitList(ui.rows)
        monthAdapter.submitList(ui.cells)

        binding.tvEmpty.isVisible = !isMonth && ui.rows.isEmpty()
        binding.tvEmpty.setText(
            if (ui.mode == CalendarMode.DAY) R.string.calendar_empty_day else R.string.calendar_empty_week
        )
    }

    private fun titleOf(ui: CalendarUi): String = when (ui.mode) {
        CalendarMode.DAY -> requireContext().dayTitle(ui.selectedDate)
        CalendarMode.WEEK -> getString(
            R.string.calendar_title_week,
            DateTimeUtils.displayDate(ui.fromDate),
            DateTimeUtils.displayDate(ui.toDate)
        )
        CalendarMode.MONTH -> {
            val date = DateTimeUtils.parseDate(ui.selectedDate)
            getString(R.string.calendar_title_month, date.monthValue, date.year)
        }
    }

    private fun addWeekdayLabels(row: LinearLayout) {
        row.removeAllViews()
        resources.getStringArray(R.array.calendar_weekdays_short).forEach { label ->
            row.addView(
                TextView(requireContext()).apply {
                    text = label
                    gravity = Gravity.CENTER
                    setTextAppearance(com.google.android.material.R.style.TextAppearance_Material3_LabelMedium)
                },
                LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            )
        }
    }

    private fun modeOf(buttonId: Int) = when (buttonId) {
        R.id.btnModeDay -> CalendarMode.DAY
        R.id.btnModeMonth -> CalendarMode.MONTH
        else -> CalendarMode.WEEK
    }

    private fun buttonOf(mode: CalendarMode) = when (mode) {
        CalendarMode.DAY -> R.id.btnModeDay
        CalendarMode.WEEK -> R.id.btnModeWeek
        CalendarMode.MONTH -> R.id.btnModeMonth
    }
}
