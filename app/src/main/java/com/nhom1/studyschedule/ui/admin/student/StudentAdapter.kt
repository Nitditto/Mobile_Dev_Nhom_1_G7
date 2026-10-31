package com.nhom1.studyschedule.ui.admin.student

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.nhom1.studyschedule.R
import com.nhom1.studyschedule.data.entity.Student
import com.nhom1.studyschedule.databinding.ItemStudentBinding

class StudentAdapter(
    private val onClick: (Student) -> Unit,
    private val onDelete: (Student) -> Unit
) : ListAdapter<Student, StudentAdapter.Holder>(Diff) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = Holder(
        ItemStudentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: Holder, position: Int) = holder.bind(getItem(position))

    inner class Holder(private val binding: ItemStudentBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(student: Student) {
            val context = binding.root.context
            binding.tvName.text = student.fullname
            binding.tvCode.text = student.studentCode
            binding.tvContact.text =
                context.getString(R.string.student_contact, student.email, student.phone)
            binding.root.setOnClickListener { onClick(student) }
            binding.btnDelete.setOnClickListener { onDelete(student) }
        }
    }

    private object Diff : DiffUtil.ItemCallback<Student>() {
        override fun areItemsTheSame(old: Student, new: Student) = old.id == new.id
        override fun areContentsTheSame(old: Student, new: Student) = old == new
    }
}
