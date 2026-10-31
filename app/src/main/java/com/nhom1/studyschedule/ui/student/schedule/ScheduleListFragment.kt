package com.nhom1.studyschedule.ui.student.schedule

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.nhom1.studyschedule.R
import com.nhom1.studyschedule.data.entity.ScheduleItem
import com.nhom1.studyschedule.databinding.FragmentListBinding
import com.nhom1.studyschedule.ui.common.appViewModels
import com.nhom1.studyschedule.ui.common.confirmDelete
import com.nhom1.studyschedule.util.DateTimeUtils
import kotlinx.coroutines.launch

/** Student tab "Lịch học": own schedules, searchable by subject name/code or location. */
class ScheduleListFragment : Fragment(R.layout.fragment_list) {
    private val viewModel by appViewModels {
        ScheduleViewModel(it.database.studyScheduleDao(), it.session.studentId)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val binding = FragmentListBinding.bind(view)
        val adapter = ScheduleAdapter(onClick = ::onItemClick, onDelete = ::confirmDeleteSchedule)

        binding.recyclerView.adapter = adapter
        binding.fabAdd.setOnClickListener { onAddClick() }
        binding.tilSearch.hint = getString(R.string.schedule_search_hint)
        binding.tvEmpty.text = getString(R.string.schedule_empty)
        binding.etSearch.doAfterTextChanged { viewModel.setQuery(it?.toString().orEmpty()) }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.schedules.collect {
                    adapter.submitList(it)
                    binding.tvEmpty.isVisible = it.isEmpty()
                }
            }
        }
    }

    /** FAB: book a new session. */
    private fun onAddClick() {
        // TODO(Trường): open BookingActivity in create mode.
        Toast.makeText(requireContext(), R.string.booking_coming_soon, Toast.LENGTH_SHORT).show()
    }

    /** Item tap: edit the session; pass item.id as BookingActivity.EXTRA_SCHEDULE_ID. */
    private fun onItemClick(item: ScheduleItem) {
        // TODO(Trường): open BookingActivity in edit mode with item.id.
        Toast.makeText(requireContext(), R.string.booking_coming_soon, Toast.LENGTH_SHORT).show()
    }

    private fun confirmDeleteSchedule(item: ScheduleItem) {
        val message = getString(
            R.string.schedule_delete_message,
            item.subjectName,
            DateTimeUtils.displayDate(item.studyDate),
            item.startTime
        )
        confirmDelete(message) { viewModel.delete(item) }
    }
}
