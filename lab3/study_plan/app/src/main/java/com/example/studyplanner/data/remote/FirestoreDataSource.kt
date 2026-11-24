package com.example.studyplanner.data.remote

import com.example.studyplanner.data.local.StudyTaskEntity
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlinx.coroutines.suspendCancellableCoroutine

class FirestoreDataSource(private val firestore: FirebaseFirestore) {
    private val tasks = firestore.collection("study_tasks")

    suspend fun fetchAll(): List<StudyTaskEntity> {
        val snapshot = tasks.get().awaitResult()
        return snapshot.documents.mapNotNull { document ->
            document.toObject(StudyTaskDocument::class.java)
                ?.copy(id = document.id)
                ?.toEntity()
        }
    }

    suspend fun upload(task: StudyTaskEntity) {
        tasks.document(task.id).set(task.toDocument()).awaitResult()
    }

    suspend fun delete(id: String) {
        tasks.document(id).delete().awaitResult()
    }
}

private suspend fun <T> Task<T>.awaitResult(): T =
    suspendCancellableCoroutine { continuation ->
        addOnSuccessListener { value ->
            if (continuation.isActive) continuation.resume(value)
        }
        addOnFailureListener { error ->
            if (continuation.isActive) continuation.resumeWithException(error)
        }
    }
