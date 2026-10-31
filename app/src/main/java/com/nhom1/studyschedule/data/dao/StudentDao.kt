package com.nhom1.studyschedule.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.nhom1.studyschedule.data.entity.Student
import kotlinx.coroutines.flow.Flow

@Dao
interface StudentDao {
    @Insert
    suspend fun insert(student: Student): Long

    @Update
    suspend fun update(student: Student)

    @Delete
    suspend fun delete(student: Student)

    @Query("SELECT * FROM Student ORDER BY fullname")
    fun observeAll(): Flow<List<Student>>

    @Query(
        "SELECT * FROM Student WHERE studentCode LIKE '%' || :query || '%' " +
            "OR fullname LIKE '%' || :query || '%' " +
            "OR email LIKE '%' || :query || '%' " +
            "OR phone LIKE '%' || :query || '%' ORDER BY fullname"
    )
    fun search(query: String): Flow<List<Student>>

    @Query("SELECT * FROM Student WHERE id = :id")
    suspend fun getById(id: Long): Student?

    @Query("SELECT * FROM Student WHERE accountId = :accountId LIMIT 1")
    suspend fun getByAccountId(accountId: Long): Student?

    @Query("SELECT COUNT(*) FROM Student WHERE studentCode = :code")
    suspend fun countByCode(code: String): Int
}
