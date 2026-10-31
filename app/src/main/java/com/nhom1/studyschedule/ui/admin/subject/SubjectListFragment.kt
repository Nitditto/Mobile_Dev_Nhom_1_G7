package com.nhom1.studyschedule.ui.admin.subject

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.nhom1.studyschedule.R
import com.nhom1.studyschedule.data.entity.Subject
import com.nhom1.studyschedule.databinding.FragmentListBinding
import com.nhom1.studyschedule.ui.common.appViewModels
import com.nhom1.studyschedule.ui.common.confirmDelete
import kotlinx.coroutines.launch

class SubjectListFragment : Fragment(R.layout.fragment_list) {
    private val viewModel by appViewModels { SubjectViewModel(it.database.subjectDao()) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val binding = FragmentListBinding.bind(view)
        val adapter = SubjectAdapter(onClick = ::openForm, onDelete = ::confirmDeleteSubject)

        binding.recyclerView.adapter = adapter
        binding.fabAdd.setOnClickListener { openForm(null) }
        binding.tilSearch.hint = getString(R.string.subject_search_hint)
        binding.etSearch.doAfterTextChanged { viewModel.setQuery(it?.toString().orEmpty()) }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.subjects.collect {
                    adapter.submitList(it)
                    binding.tvEmpty.isVisible = it.isEmpty()
                }
            }
        }
    }

    private fun openForm(subject: Subject?) {
        SubjectFormDialog(this, subject, viewModel::save).show()
    }

    private fun confirmDeleteSubject(subject: Subject) {
        confirmDelete(getString(R.string.subject_delete_message, subject.subjectName)) {
            viewModel.delete(subject)
        }
    }
}
