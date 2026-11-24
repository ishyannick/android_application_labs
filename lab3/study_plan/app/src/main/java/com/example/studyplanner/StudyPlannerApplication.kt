package com.example.studyplanner

import android.app.Application
import com.example.studyplanner.data.local.StudyPlannerDatabase
import com.example.studyplanner.data.remote.FirestoreDataSource
import com.example.studyplanner.repository.StudyTaskRepository
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore

class StudyPlannerApplication : Application() {
    val repository: StudyTaskRepository by lazy {
        val dao = StudyPlannerDatabase.getInstance(this).studyTaskDao()
        val firebaseReady = FirebaseApp.initializeApp(this) != null ||
            FirebaseApp.getApps(this).isNotEmpty()
        val remote = if (firebaseReady) {
            FirestoreDataSource(FirebaseFirestore.getInstance())
        } else {
            null
        }
        StudyTaskRepository(dao, remote)
    }
}
