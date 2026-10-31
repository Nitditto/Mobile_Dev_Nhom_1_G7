package com.nhom1.studyschedule.data.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

object Role {
    const val ADMIN = "ADMIN"
    const val STUDENT = "STUDENT"
}

@Entity(indices = [Index(value = ["username"], unique = true)])
data class Account(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val username: String,
    /** SHA-256 hex of the raw password, see [com.nhom1.studyschedule.util.PasswordHasher]. */
    val password: String,
    val role: String
)
