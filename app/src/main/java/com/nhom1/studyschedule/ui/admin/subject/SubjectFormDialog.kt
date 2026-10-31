package com.nhom1.studyschedule.ui.admin.subject

import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.nhom1.studyschedule.R
import com.nhom1.studyschedule.data.entity.Subject
import com.nhom1.studyschedule.databinding.DialogSubjectBinding
import kotlinx.coroutines.launch

/**
 * Add (subject == null) or edit dialog. [onSave] returns false when the code is a duplicate,
 * in which case the dialog stays open and shows the error.
 */
class SubjectFormDialog(
    private val fragment: Fragment,
    private val subject: Subject?,
    private val onSave: suspend (Subject) -> Boolean
) {
    private val binding = DialogSubjectBinding.inflate(fragment.layoutInflater)

    fun show() {
        subject?.let {
            binding.etCode.setText(it.subjectCode)
            binding.etName.setText(it.subjectName)
            binding.etDescription.setText(it.description)
            binding.etCredits.setText(it.credits.toString())
        }

        val dialog = MaterialAlertDialogBuilder(fragment.requireContext())
            .setTitle(if (subject == null) R.string.subject_add else R.string.subject_edit)
            .setView(binding.root)
            .setNegativeButton(R.string.action_cancel, null)
            .setPositiveButton(R.string.action_save, null)
            .show()

        // Set after show() so invalid input does not close the dialog.
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            val input = validate() ?: return@setOnClickListener
            fragment.viewLifecycleOwner.lifecycleScope.launch {
                if (onSave(input)) {
                    dialog.dismiss()
                } else {
                    binding.tilCode.error = fragment.getString(R.string.error_subject_code_taken)
                }
            }
        }
    }

    private fun validate(): Subject? {
        val code = binding.etCode.text?.toString().orEmpty().trim().uppercase()
        val name = binding.etName.text?.toString().orEmpty().trim()
        val description = binding.etDescription.text?.toString().orEmpty().trim()
        val credits = binding.etCredits.text?.toString()?.trim()?.toIntOrNull()
        val required = fragment.getString(R.string.error_required)

        binding.tilCode.error = if (code.isEmpty()) required else null
        binding.tilName.error = if (name.isEmpty()) required else null
        binding.tilCredits.error = if (credits == null || credits !in 1..10) {
            fragment.getString(R.string.error_subject_credits)
        } else {
            null
        }
        if (code.isEmpty() || name.isEmpty() || credits == null || credits !in 1..10) return null

        return Subject(
            id = subject?.id ?: 0,
            subjectCode = code,
            subjectName = name,
            description = description,
            credits = credits
        )
    }
}
