package com.nhom1.studyschedule.ui.admin.student

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.nhom1.studyschedule.R
import com.nhom1.studyschedule.data.entity.Student
import com.nhom1.studyschedule.data.repository.StudentRepository
import com.nhom1.studyschedule.databinding.FragmentListBinding
import com.nhom1.studyschedule.ui.common.appViewModels
import kotlinx.coroutines.launch

class StudentListFragment : Fragment(R.layout.fragment_list) {
    private val viewModel by appViewModels { StudentViewModel(StudentRepository(it.database)) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val binding = FragmentListBinding.bind(view)
        val adapter = StudentAdapter(onClick = ::openForm, onDelete = {})

        binding.recyclerView.adapter = adapter
        binding.fabAdd.setOnClickListener { openForm(null) }
        binding.tilSearch.hint = getString(R.string.student_search_hint)
        binding.etSearch.doAfterTextChanged { viewModel.setQuery(it?.toString().orEmpty()) }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.students.collect {
                    adapter.submitList(it)
                    binding.tvEmpty.isVisible = it.isEmpty()
                }
            }
        }
    }

    private fun openForm(student: Student?) {
        viewLifecycleOwner.lifecycleScope.launch {
            val form = student?.let { viewModel.toForm(it) }
            StudentFormDialog(this@StudentListFragment, form) { viewModel.save(student, it) }.show()
        }
    }
}
