package com.nhom1.studyschedule.ui.admin.subject

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.nhom1.studyschedule.R
import com.nhom1.studyschedule.data.entity.Subject
import com.nhom1.studyschedule.databinding.ItemSubjectBinding

class SubjectAdapter(
    private val onClick: (Subject) -> Unit,
    private val onDelete: (Subject) -> Unit
): ListAdapter<Subject, SubjectAdapter.Holder>(Diff) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = Holder(
        ItemSubjectBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: Holder, position: Int) = holder.bind(getItem(position))

    inner class Holder(private val binding: ItemSubjectBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(subject: Subject) {
            val context = binding.root.context
            binding.tvName.text = subject.subjectName
            binding.tvMeta.text =
                context.getString(R.string.subject_meta, subject.subjectCode, subject.credits)
            binding.tvDescription.text = subject.description
            binding.tvDescription.isVisible = subject.description.isNotBlank()
            binding.root.setOnClickListener { onClick(subject) }
            binding.btnDelete.setOnClickListener { onDelete(subject) }
        }
    }

    private object Diff : DiffUtil.ItemCallback<Subject>() {
        override fun areItemsTheSame(old: Subject, new: Subject) = old.id == new.id
        override fun areContentsTheSame(old: Subject, new: Subject) = old == new
    }
}
