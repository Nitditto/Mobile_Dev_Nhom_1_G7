package com.nhom1.studyschedule.ui.student.schedule

import android.os.Bundle
import android.view.View
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
import kotlinx.coroutines.launch

/** Student tab "Lịch học": own schedules, searchable by subject name/code or location. */
class ScheduleListFragment : Fragment(R.layout.fragment_list) {
    private val viewModel by appViewModels {
        ScheduleViewModel(it.database.studyScheduleDao(), it.session.studentId)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val binding = FragmentListBinding.bind(view)
        val adapter = ScheduleAdapter(onClick = ::onItemClick)

        binding.recyclerView.adapter = adapter
        binding.fabAdd.isVisible = false
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

    private fun onItemClick(item: ScheduleItem) = Unit
}
