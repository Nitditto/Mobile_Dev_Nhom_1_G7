package com.nhom1.studyschedule.ui.common

import androidx.fragment.app.Fragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.nhom1.studyschedule.R

fun Fragment.confirmDelete(message: String, onConfirm: () -> Unit) {
    MaterialAlertDialogBuilder(requireContext())
        .setTitle(R.string.confirm_delete_title)
        .setMessage(message)
        .setNegativeButton(R.string.action_cancel, null)
        .setPositiveButton(R.string.action_delete) { _, _ -> onConfirm() }
        .show()
}
