package com.nhom1.studyschedule.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.nhom1.studyschedule.data.entity.Subject
import kotlinx.coroutines.flow.Flow

@Dao
interface SubjectDao {
    @Insert
    suspend fun insert(subject: Subject): Long

    @Update
    suspend fun update(subject: Subject)

    @Delete
    suspend fun delete(subject: Subject)

    @Query("SELECT * FROM Subject ORDER BY subjectName")
    fun observeAll(): Flow<List<Subject>>

    @Query(
        "SELECT * FROM Subject WHERE subjectCode LIKE '%' || :query || '%' " +
            "OR subjectName LIKE '%' || :query || '%' " +
            "OR description LIKE '%' || :query || '%' ORDER BY subjectName"
    )
    fun search(query: String): Flow<List<Subject>>

    @Query("SELECT * FROM Subject ORDER BY subjectName")
    suspend fun getAll(): List<Subject>

    @Query("SELECT * FROM Subject WHERE id = :id")
    suspend fun getById(id: Long): Subject?
}
