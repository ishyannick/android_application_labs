package com.example.studdyplan.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "study_tasks")
data class StudyTaskEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val course: String,
    val dateTime: String,
    val location: String,
    val status: String, // e.g., "PLANNED", "DONE", "CANCELLED"
    val notes: String,
    val updatedAt: Long = System.currentTimeMillis()
)