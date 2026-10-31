package com.nhom1.studyschedule.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = Account::class,
            parentColumns = ["id"],
            childColumns = ["accountId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["studentCode"], unique = true),
        Index(value = ["accountId"], unique = true)
    ]
)
data class Student(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val studentCode: String,
    val fullname: String,
    val email: String,
    val phone: String,
    val accountId: Long
)
