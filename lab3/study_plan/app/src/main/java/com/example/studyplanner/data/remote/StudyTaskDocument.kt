package com.example.studyplanner.data.remote

import com.example.studyplanner.data.local.StudyTaskEntity

// Default values are required so Firestore can create this object.
data class StudyTaskDocument(
    val id: String = "",
    val title: String = "",
    val course: String = "",
    val dateTime: String = "",
    val location: String = "",
    val status: String = "PLANNED",
    val notes: String = "",
    val updatedAt: Long = 0L
)

fun StudyTaskEntity.toDocument() = StudyTaskDocument(
    id, title, course, dateTime, location, status, notes, updatedAt
)

fun StudyTaskDocument.toEntity() = StudyTaskEntity(
    id, title, course, dateTime, location, status, notes, updatedAt
)
