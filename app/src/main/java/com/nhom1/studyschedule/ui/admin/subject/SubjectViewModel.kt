package com.nhom1.studyschedule.ui.admin.subject

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nhom1.studyschedule.StudyScheduleApp
import com.nhom1.studyschedule.data.entity.Subject
import com.nhom1.studyschedule.notification.ReminderScheduler
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SubjectViewModel(private val app: StudyScheduleApp) : ViewModel() {
    private val dao = app.database.subjectDao()
    private val query = MutableStateFlow("")

    @OptIn(ExperimentalCoroutinesApi::class)
    val subjects: StateFlow<List<Subject>> = query
        .flatMapLatest { dao.search(it.trim()) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun setQuery(value: String) {
        query.value = value
    }

    /** Inserts or updates. Returns false when the subject code is already used. */
    suspend fun save(subject: Subject): Boolean {
        if (dao.countByCode(subject.subjectCode, subject.id) > 0) return false
        if (subject.id == 0L) dao.insert(subject) else dao.update(subject)
        return true
    }

    /**
     * Also deletes every study schedule of this subject (FK cascade). The cascade does not
     * touch AlarmManager, so read the ids first and cancel their reminders afterwards.
     */
    fun delete(subject: Subject) {
        viewModelScope.launch {
            val scheduleIds = app.database.studyScheduleDao().getIdsBySubject(subject.id)
            dao.delete(subject)
            scheduleIds.forEach { ReminderScheduler.cancel(app, it) }
        }
    }
}
