package com.nhom1.studyschedule.ui.common

import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.nhom1.studyschedule.StudyScheduleApp

val Fragment.app: StudyScheduleApp
    get() = requireActivity().application as StudyScheduleApp

/**
 * `private val viewModel by appViewModels { SubjectViewModel(it) }`
 */
inline fun <reified VM : ViewModel> Fragment.appViewModels(
    crossinline create: (StudyScheduleApp) -> VM
) = viewModels<VM> {
    viewModelFactory { initializer { create(app) } }
}

val ComponentActivity.app: StudyScheduleApp
    get() = application as StudyScheduleApp

/** Activity version of [Fragment.appViewModels]. */
inline fun <reified VM : ViewModel> ComponentActivity.appViewModels(
    crossinline create: (StudyScheduleApp) -> VM
) = viewModels<VM> {
    viewModelFactory { initializer { create(app) } }
}
