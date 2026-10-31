package com.nhom1.studyschedule.ui.admin.subject

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nhom1.studyschedule.data.dao.SubjectDao
import com.nhom1.studyschedule.data.entity.Subject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn

class SubjectViewModel(private val dao: SubjectDao) : ViewModel() {
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
}
