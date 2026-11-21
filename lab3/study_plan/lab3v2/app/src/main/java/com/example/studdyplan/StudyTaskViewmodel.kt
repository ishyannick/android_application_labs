package com.example.studdyplan.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.studdyplan.data.local.AppDatabase
import com.example.studdyplan.data.local.StudyTaskEntity
import com.example.studdyplan.data.repository.StudyTaskRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class StudyTaskViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: StudyTaskRepository

    // Observable StateFlow for Compose UI
    val allTasks: StateFlow<List<StudyTaskEntity>>

    init {
        val dao = AppDatabase.getDatabase(application).studyTaskDao()
        repository = StudyTaskRepository(dao)

        allTasks = repository.allTasks.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    }

    fun insert(task: StudyTaskEntity) = viewModelScope.launch {
        repository.insertTask(task)
    }

    fun update(task: StudyTaskEntity) = viewModelScope.launch {
        repository.updateTask(task)
    }

    fun delete(task: StudyTaskEntity) = viewModelScope.launch {
        repository.deleteTask(task)
    }
}