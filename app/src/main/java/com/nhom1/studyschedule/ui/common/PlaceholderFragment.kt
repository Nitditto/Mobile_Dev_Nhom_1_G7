package com.nhom1.studyschedule.ui.common

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment

/** Temporary screen for a feature that has not been built yet. Replace it in each home activity. */
class PlaceholderFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = TextView(requireContext()).apply {
        text = requireArguments().getString(ARG_TITLE)
        gravity = Gravity.CENTER
        textSize = 18f
    }

    companion object {
        private const val ARG_TITLE = "title"

        fun newInstance(title: String) = PlaceholderFragment().apply {
            arguments = Bundle().apply { putString(ARG_TITLE, title) }
        }
    }
}
