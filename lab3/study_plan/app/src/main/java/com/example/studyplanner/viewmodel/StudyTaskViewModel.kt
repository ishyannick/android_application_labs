package com.example.studyplanner.viewmodel

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.studyplanner.data.local.StudyTaskEntity
import com.example.studyplanner.repository.StudyTaskRepository
import com.example.studyplanner.util.SyncNotifier
import java.util.UUID
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class SyncStatus { IDLE, SYNCING, SUCCESS, ERROR }

data class PlannerUiState(
    val syncStatus: SyncStatus = SyncStatus.IDLE,
    val message: String? = null,
    val filter: String = "ALL",
    val isSaving: Boolean = false
)

class StudyTaskViewModel(
    application: Application,
    private val repository: StudyTaskRepository
) : AndroidViewModel(application) {
    private val filter = MutableStateFlow("ALL")
    private val internalState = MutableStateFlow(PlannerUiState())

    val uiState: StateFlow<PlannerUiState> = combine(internalState, filter) { state, value ->
        state.copy(filter = value)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), PlannerUiState())

    val tasks: StateFlow<List<StudyTaskEntity>> = combine(repository.tasks, filter) { items, value ->
        if (value == "ALL") items else items.filter { it.status == value }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun task(id: String): Flow<StudyTaskEntity?> = repository.task(id)

    fun setFilter(value: String) {
        filter.value = value
    }

    fun save(
        id: String?,
        title: String,
        course: String,
        dateTime: String,
        location: String,
        status: String,
        notes: String,
        onSaved: () -> Unit
    ) {
        if (internalState.value.isSaving) return
        viewModelScope.launch {
            internalState.value = internalState.value.copy(isSaving = true, message = null)
            runCatching {
                repository.save(
                    StudyTaskEntity(
                        id = id ?: UUID.randomUUID().toString(),
                        title = title.trim(),
                        course = course.trim(),
                        dateTime = dateTime.trim(),
                        location = location.trim(),
                        status = status,
                        notes = notes.trim(),
                        updatedAt = System.currentTimeMillis()
                    )
                )
            }.onSuccess {
                internalState.value = internalState.value.copy(isSaving = false)
                onSaved()
            }.onFailure { error ->
                internalState.value = internalState.value.copy(
                    isSaving = false,
                    message = error.message ?: "Could not save the item"
                )
            }
        }
    }

    fun delete(task: StudyTaskEntity, onDeleted: () -> Unit = {}) {
        viewModelScope.launch {
            runCatching { repository.delete(task) }
                .onSuccess { onDeleted() }
                .onFailure { error -> showError(error.message ?: "Could not delete the item") }
        }
    }

    fun markNextStatus(task: StudyTaskEntity) {
        viewModelScope.launch {
            runCatching { repository.markNextStatus(task) }
                .onFailure { error -> showError(error.message ?: "Could not change status") }
        }
    }

    fun sync() {
        if (internalState.value.syncStatus == SyncStatus.SYNCING) return
        if (!hasInternet()) {
            showError("No internet connection. Local Room data is still available.")
            return
        }
        viewModelScope.launch {
            internalState.value = internalState.value.copy(
                syncStatus = SyncStatus.SYNCING,
                message = "Syncing with Firestore..."
            )
            repository.sync()
                .onSuccess { count ->
                    internalState.value = internalState.value.copy(
                        syncStatus = SyncStatus.SUCCESS,
                        message = "Sync complete: $count item(s)"
                    )
                    SyncNotifier.show(getApplication(), count)
                }
                .onFailure { error -> showError(error.message ?: "Firestore sync failed") }
        }
    }

    fun clearMessage() {
        internalState.value = internalState.value.copy(message = null, syncStatus = SyncStatus.IDLE)
    }

    private fun showError(message: String) {
        internalState.value = internalState.value.copy(
            syncStatus = SyncStatus.ERROR,
            message = message,
            isSaving = false
        )
    }

    private fun hasInternet(): Boolean {
        val manager = getApplication<Application>()
            .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = manager.activeNetwork ?: return false
        val capabilities = manager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    class Factory(
        private val application: Application,
        private val repository: StudyTaskRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return StudyTaskViewModel(application, repository) as T
        }
    }
}
