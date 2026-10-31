package com.nhom1.studyschedule.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.nhom1.studyschedule.data.entity.Account

@Dao
interface AccountDao {
    @Insert
    suspend fun insert(account: Account): Long

    @Update
    suspend fun update(account: Account)

    /** Deleting an account also deletes its Student row (FK cascade). */
    @Query("DELETE FROM Account WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("SELECT * FROM Account WHERE username = :username AND password = :passwordHash LIMIT 1")
    suspend fun login(username: String, passwordHash: String): Account?

    @Query("SELECT COUNT(*) FROM Account WHERE username = :username")
    suspend fun countByUsername(username: String): Int
}
