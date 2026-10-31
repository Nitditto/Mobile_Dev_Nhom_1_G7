package com.nhom1.studyschedule.ui.student.schedule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nhom1.studyschedule.data.dao.StudyScheduleDao
import com.nhom1.studyschedule.data.entity.ScheduleItem
import com.nhom1.studyschedule.data.repository.BookingResult
import com.nhom1.studyschedule.data.repository.ScheduleRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/** Personal schedules of the logged-in student; [studentId] comes from the session. */
class ScheduleViewModel(
    private val dao: StudyScheduleDao,
    private val repository: ScheduleRepository,
    private val studentId: Long
) : ViewModel() {
    private val query = MutableStateFlow("")

    /** null = all statuses, otherwise a ScheduleStatus value. */
    private val _statusFilter = MutableStateFlow<String?>(null)
    val statusFilter: StateFlow<String?> = _statusFilter.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val schedules: StateFlow<List<ScheduleItem>> = combine(query, _statusFilter, ::Pair)
        .flatMapLatest { (q, status) -> dao.search(studentId, q.trim(), status) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun setQuery(value: String) {
        query.value = value
    }

    fun setStatusFilter(status: String?) {
        _statusFilter.value = status
    }

    /** Conflict is returned when restoring a cancelled session whose slot is taken. */
    suspend fun changeStatus(item: ScheduleItem, newStatus: String): BookingResult =
        repository.changeStatus(item.id, newStatus)

    fun delete(item: ScheduleItem) {
        viewModelScope.launch { dao.deleteById(item.id) }
    }
}
