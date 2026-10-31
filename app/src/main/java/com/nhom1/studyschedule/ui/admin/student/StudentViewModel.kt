package com.nhom1.studyschedule.ui.admin.student

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nhom1.studyschedule.data.entity.Student
import com.nhom1.studyschedule.data.repository.StudentRepository
import com.nhom1.studyschedule.data.repository.StudentRepository.SaveResult
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn

class StudentViewModel(private val repository: StudentRepository) : ViewModel() {
    private val query = MutableStateFlow("")

    @OptIn(ExperimentalCoroutinesApi::class)
    val students: StateFlow<List<Student>> = query
        .flatMapLatest { repository.search(it.trim()) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun setQuery(value: String) {
        query.value = value
    }

    suspend fun toForm(student: Student) = StudentForm(
        code = student.studentCode,
        name = student.fullname,
        email = student.email,
        phone = student.phone,
        username = repository.getUsername(student).orEmpty(),
        password = ""
    )

    /** Creates when [existing] is null, otherwise updates it (empty password = keep current). */
    suspend fun save(existing: Student?, form: StudentForm): SaveResult =
        if (existing == null) {
            repository.create(
                form.code, form.name, form.email, form.phone, form.username, form.password
            )
        } else {
            repository.update(
                existing.copy(
                    studentCode = form.code,
                    fullname = form.name,
                    email = form.email,
                    phone = form.phone
                ),
                form.username,
                form.password.ifEmpty { null }
            )
        }
}
