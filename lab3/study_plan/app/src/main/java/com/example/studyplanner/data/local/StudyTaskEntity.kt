package com.example.studyplanner.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "study_tasks")
data class StudyTaskEntity(
    @PrimaryKey val id: String,
    val title: String,
    val course: String,
    val dateTime: String,
    val location: String,
    val status: String,
    val notes: String,
    val updatedAt: Long
)

object TaskStatus {
    const val PLANNED = "PLANNED"
    const val DONE = "DONE"
    const val CANCELLED = "CANCELLED"

    val values = listOf(PLANNED, DONE, CANCELLED)
}
