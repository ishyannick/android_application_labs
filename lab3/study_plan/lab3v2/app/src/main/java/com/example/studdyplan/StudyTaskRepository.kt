package com.example.studdyplan.data.repository

import com.example.studdyplan.data.local.StudyTaskDao
import com.example.studdyplan.data.local.StudyTaskEntity
import kotlinx.coroutines.flow.Flow

class StudyTaskRepository(private val dao: StudyTaskDao) {

    // Expose the observable list of tasks from Room
    val allTasks: Flow<List<StudyTaskEntity>> = dao.getAllTasks()

    suspend fun getTaskById(id: String): StudyTaskEntity? {
        return dao.getTaskById(id)
    }

    suspend fun insertTask(task: StudyTaskEntity) {
        dao.insertTask(task)
    }

    suspend fun updateTask(task: StudyTaskEntity) {
        dao.updateTask(task)
    }

    suspend fun deleteTask(task: StudyTaskEntity) {
        dao.deleteTask(task)
    }
}