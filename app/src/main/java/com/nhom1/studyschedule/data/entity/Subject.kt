package com.nhom1.studyschedule.data.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(indices = [Index(value = ["subjectCode"], unique = true)])
data class Subject(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val subjectCode: String,
    val subjectName: String,
    val description: String,
    val credits: Int
)
