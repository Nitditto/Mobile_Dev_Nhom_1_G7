package com.nhom1.studyschedule.data.repository

import androidx.room.withTransaction
import com.nhom1.studyschedule.data.AppDatabase
import com.nhom1.studyschedule.data.entity.Account
import com.nhom1.studyschedule.data.entity.Role
import com.nhom1.studyschedule.data.entity.Student
import com.nhom1.studyschedule.util.PasswordHasher
import kotlinx.coroutines.flow.Flow

/**
 * A student always comes with a STUDENT account, so every write here touches both tables
 * inside one transaction.
 */
class StudentRepository(private val db: AppDatabase) {
    private val accountDao = db.accountDao()
    private val studentDao = db.studentDao()

    enum class SaveResult { SUCCESS, CODE_TAKEN, USERNAME_TAKEN }

    fun search(query: String): Flow<List<Student>> = studentDao.search(query)

    suspend fun getUsername(student: Student): String? =
        accountDao.getById(student.accountId)?.username

    suspend fun create(
        code: String,
        name: String,
        email: String,
        phone: String,
        username: String,
        password: String
    ): SaveResult = db.withTransaction {
        if (studentDao.countByCode(code) > 0) return@withTransaction SaveResult.CODE_TAKEN
        if (accountDao.countByUsername(username) > 0) {
            return@withTransaction SaveResult.USERNAME_TAKEN
        }
        val accountId = accountDao.insert(
            Account(username = username, password = PasswordHasher.hash(password), role = Role.STUDENT)
        )
        studentDao.insert(
            Student(
                studentCode = code,
                fullname = name,
                email = email,
                phone = phone,
                accountId = accountId
            )
        )
        SaveResult.SUCCESS
    }

    /** A null or blank [newPassword] keeps the current password. */
    suspend fun update(student: Student, username: String, newPassword: String?): SaveResult =
        db.withTransaction {
            if (studentDao.countByCode(student.studentCode, student.id) > 0) {
                return@withTransaction SaveResult.CODE_TAKEN
            }
            if (accountDao.countByUsername(username, student.accountId) > 0) {
                return@withTransaction SaveResult.USERNAME_TAKEN
            }
            val account = accountDao.getById(student.accountId)
            if (account != null) {
                accountDao.update(
                    account.copy(
                        username = username,
                        password = if (newPassword.isNullOrBlank()) account.password
                        else PasswordHasher.hash(newPassword)
                    )
                )
            }
            studentDao.update(student)
            SaveResult.SUCCESS
        }

    /** Deleting the account cascades to the Student row and all of its study schedules. */
    suspend fun delete(student: Student) {
        accountDao.deleteById(student.accountId)
    }
}
