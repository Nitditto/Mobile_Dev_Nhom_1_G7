package com.nhom1.studyschedule.ui.student.booking

import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.nhom1.studyschedule.R
import com.nhom1.studyschedule.data.entity.Subject
import com.nhom1.studyschedule.databinding.ActivityBookingBinding
import com.nhom1.studyschedule.ui.common.appViewModels
import com.nhom1.studyschedule.util.DateTimeUtils
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class BookingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBookingBinding
    private val viewModel by appViewModels { BookingViewModel(it.database.subjectDao()) }

    private var subjects: List<Subject> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.setNavigationOnClickListener { finish() }
        binding.actvSubject.setOnItemClickListener { _, _, position, _ ->
            viewModel.setSubject(subjects[position].id)
            binding.tilSubject.error = null
        }
        binding.etDate.setOnClickListener { pickDate() }
        binding.etStartTime.setOnClickListener { pickTime(isStart = true) }
        binding.etEndTime.setOnClickListener { pickTime(isStart = false) }
        binding.etLocation.doAfterTextChanged { viewModel.setLocation(it?.toString().orEmpty()) }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                combine(viewModel.subjects, viewModel.form, ::Pair).collect { (list, form) ->
                    render(list, form)
                }
            }
        }
    }

    private fun render(list: List<Subject>, form: BookingForm) {
        if (list != subjects) {
            subjects = list
            binding.actvSubject.setAdapter(
                ArrayAdapter(this, android.R.layout.simple_list_item_1, list.map(::subjectLabel))
            )
        }
        val selected = list.firstOrNull { it.id == form.subjectId }
        // filter = false keeps the dropdown showing every subject after setText.
        binding.actvSubject.setText(selected?.let(::subjectLabel).orEmpty(), false)

        binding.etDate.setText(form.date?.let(DateTimeUtils::displayDate).orEmpty())
        binding.etStartTime.setText(form.startTime.orEmpty())
        binding.etEndTime.setText(form.endTime.orEmpty())
        if (binding.etLocation.text?.toString() != form.location) {
            binding.etLocation.setText(form.location)
        }
    }

    private fun subjectLabel(subject: Subject) = "${subject.subjectCode} – ${subject.subjectName}"

    private fun pickDate() {
        val today = DateTimeUtils.toPickerMillis(DateTimeUtils.today())
        val current = viewModel.form.value.date?.let(DateTimeUtils::toPickerMillis) ?: today
        val picker = MaterialDatePicker.Builder.datePicker()
            .setTitleText(R.string.hint_booking_date)
            .setSelection(current)
            .setCalendarConstraints(
                // from(today) instead of now(): picker dates are UTC midnight, now() would hide today.
                CalendarConstraints.Builder()
                    .setValidator(DateValidatorPointForward.from(today))
                    .build()
            )
            .build()
        picker.addOnPositiveButtonClickListener { millis ->
            viewModel.setDate(DateTimeUtils.fromPickerMillis(millis))
            binding.tilDate.error = null
        }
        picker.show(supportFragmentManager, "date")
    }

    private fun pickTime(isStart: Boolean) {
        val form = viewModel.form.value
        val current = (if (isStart) form.startTime else form.endTime)
            ?.let(DateTimeUtils::parseTime)
        val picker = MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_24H)
            .setTitleText(if (isStart) R.string.hint_booking_start else R.string.hint_booking_end)
            .setHour(current?.hour ?: if (isStart) 7 else 9)
            .setMinute(current?.minute ?: 0)
            .build()
        picker.addOnPositiveButtonClickListener {
            val time = DateTimeUtils.formatTime(picker.hour, picker.minute)
            if (isStart) {
                viewModel.setStartTime(time)
                binding.tilStartTime.error = null
            } else {
                viewModel.setEndTime(time)
                binding.tilEndTime.error = null
            }
        }
        picker.show(supportFragmentManager, if (isStart) "start" else "end")
    }
}
