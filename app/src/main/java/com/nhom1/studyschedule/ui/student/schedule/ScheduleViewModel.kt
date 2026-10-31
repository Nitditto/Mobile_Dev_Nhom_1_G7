package com.nhom1.studyschedule.ui.student.schedule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nhom1.studyschedule.data.dao.StudyScheduleDao
import com.nhom1.studyschedule.data.entity.ScheduleItem
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/** Personal schedules of the logged-in student; [studentId] comes from the session. */
class ScheduleViewModel(
    private val dao: StudyScheduleDao,
    private val studentId: Long
) : ViewModel() {
    private val query = MutableStateFlow("")

    @OptIn(ExperimentalCoroutinesApi::class)
    val schedules: StateFlow<List<ScheduleItem>> = query
        .flatMapLatest { dao.search(studentId, it.trim()) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun setQuery(value: String) {
        query.value = value
    }

    fun delete(item: ScheduleItem) {
        viewModelScope.launch { dao.deleteById(item.id) }
    }
}
