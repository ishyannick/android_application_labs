package com.example.studyplanner.repository

import com.example.studyplanner.data.local.StudyTaskDao
import com.example.studyplanner.data.local.StudyTaskEntity
import com.example.studyplanner.data.local.TaskStatus
import com.example.studyplanner.data.remote.FirestoreDataSource
import kotlinx.coroutines.flow.Flow

class StudyTaskRepository(
    private val dao: StudyTaskDao,
    private val remote: FirestoreDataSource?
) {
    val tasks: Flow<List<StudyTaskEntity>> = dao.observeAll()

    fun task(id: String): Flow<StudyTaskEntity?> = dao.observeById(id)

    suspend fun save(task: StudyTaskEntity) {
        dao.insert(task)
        // Local data is saved even when Firebase is missing or offline.
        remote?.let { runCatching { it.upload(task) } }
    }

    suspend fun delete(task: StudyTaskEntity) {
        dao.delete(task)
        remote?.let { runCatching { it.delete(task.id) } }
    }

    suspend fun markNextStatus(task: StudyTaskEntity) {
        val next = when (task.status) {
            TaskStatus.PLANNED -> TaskStatus.DONE
            TaskStatus.DONE -> TaskStatus.CANCELLED
            else -> TaskStatus.PLANNED
        }
        save(task.copy(status = next, updatedAt = System.currentTimeMillis()))
    }

    suspend fun sync(): Result<Int> = runCatching {
        val cloud = remote ?: error(
            "Firebase is not configured. Add app/google-services.json and rebuild."
        )
        val local = dao.getAllOnce()
        val remoteTasks = cloud.fetchAll()

        // Simple last-updated-wins conflict rule, using the shared string ID.
        val winners = (local + remoteTasks)
            .groupBy { it.id }
            .map { (_, versions) -> versions.maxBy { it.updatedAt } }

        dao.insertAll(winners)
        winners.forEach { cloud.upload(it) }
        winners.size
    }
}
