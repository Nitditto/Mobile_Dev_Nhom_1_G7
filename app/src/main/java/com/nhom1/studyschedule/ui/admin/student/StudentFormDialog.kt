package com.nhom1.studyschedule.ui.admin.student

import android.util.Patterns
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.nhom1.studyschedule.R
import com.nhom1.studyschedule.data.repository.StudentRepository.SaveResult
import com.nhom1.studyschedule.databinding.DialogStudentBinding
import kotlinx.coroutines.launch

/** Raw, validated form values. [password] is empty when editing and the admin keeps the old one. */
data class StudentForm(
    val code: String,
    val name: String,
    val email: String,
    val phone: String,
    val username: String,
    val password: String
)

/**
 * Add (current == null) or edit dialog; on edit [current] pre-fills every field but the password.
 * The dialog stays open while [onSave] reports a duplicate code or username.
 */
class StudentFormDialog(
    private val fragment: Fragment,
    private val current: StudentForm?,
    private val onSave: suspend (StudentForm) -> SaveResult
) {
    private val binding = DialogStudentBinding.inflate(fragment.layoutInflater)
    private val isEdit = current != null

    fun show() {
        current?.let {
            binding.etCode.setText(it.code)
            binding.etName.setText(it.name)
            binding.etEmail.setText(it.email)
            binding.etPhone.setText(it.phone)
            binding.etUsername.setText(it.username)
            binding.tilPassword.hint = fragment.getString(R.string.hint_password_keep)
        }

        val dialog = MaterialAlertDialogBuilder(fragment.requireContext())
            .setTitle(if (isEdit) R.string.student_edit else R.string.student_add)
            .setView(binding.root)
            .setNegativeButton(R.string.action_cancel, null)
            .setPositiveButton(R.string.action_save, null)
            .show()

        // Set after show() so invalid input does not close the dialog.
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            val input = validate() ?: return@setOnClickListener
            fragment.viewLifecycleOwner.lifecycleScope.launch {
                when (onSave(input)) {
                    SaveResult.SUCCESS -> dialog.dismiss()
                    SaveResult.CODE_TAKEN -> binding.tilCode.error =
                        fragment.getString(R.string.error_student_code_taken)
                    SaveResult.USERNAME_TAKEN -> binding.tilUsername.error =
                        fragment.getString(R.string.error_username_taken)
                }
            }
        }
    }

    private fun validate(): StudentForm? {
        fun text(view: android.widget.EditText) = view.text?.toString().orEmpty().trim()
        val code = text(binding.etCode).uppercase()
        val name = text(binding.etName)
        val email = text(binding.etEmail)
        val phone = text(binding.etPhone)
        val username = text(binding.etUsername)
        val password = binding.etPassword.text?.toString().orEmpty()
        val required = fragment.getString(R.string.error_required)

        binding.tilCode.error = if (code.isEmpty()) required else null
        binding.tilName.error = if (name.isEmpty()) required else null
        binding.tilEmail.error = when {
            email.isEmpty() -> required
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() ->
                fragment.getString(R.string.error_email_invalid)
            else -> null
        }
        binding.tilPhone.error = when {
            phone.isEmpty() -> required
            !PHONE_REGEX.matches(phone) -> fragment.getString(R.string.error_phone_invalid)
            else -> null
        }
        binding.tilUsername.error = if (username.isEmpty()) required else null
        binding.tilPassword.error = when {
            password.isEmpty() && !isEdit -> required
            password.isNotEmpty() && password.length < MIN_PASSWORD ->
                fragment.getString(R.string.error_password_short)
            else -> null
        }

        val tils = listOf(
            binding.tilCode, binding.tilName, binding.tilEmail,
            binding.tilPhone, binding.tilUsername, binding.tilPassword
        )
        if (tils.any { it.error != null }) return null
        return StudentForm(code, name, email, phone, username, password)
    }

    private companion object {
        val PHONE_REGEX = Regex("^0\\d{9}$")
        const val MIN_PASSWORD = 6
    }
}
