package com.example.studyplanner.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.studyplanner.data.local.TaskStatus
import com.example.studyplanner.viewmodel.StudyTaskViewModel
import kotlinx.coroutines.flow.flowOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditScreen(
    taskId: String,
    viewModel: StudyTaskViewModel,
    onBack: () -> Unit
) {
    val isNew = taskId == "new"
    val taskFlow = remember(taskId) {
        if (isNew) flowOf(null) else viewModel.task(taskId)
    }
    val existing by taskFlow.collectAsStateWithLifecycle(initialValue = null)
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var title by remember { mutableStateOf("") }
    var course by remember { mutableStateOf("") }
    var dateTime by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var status by remember { mutableStateOf(TaskStatus.PLANNED) }
    var notes by remember { mutableStateOf("") }
    var formError by remember { mutableStateOf<String?>(null) }
    var loadedId by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(existing?.id) {
        existing?.takeIf { loadedId != it.id }?.let { task ->
            title = task.title
            course = task.course
            dateTime = task.dateTime
            location = task.location
            status = task.status
            notes = task.notes
            loadedId = task.id
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isNew) "Add study item" else "Edit study item") },
                navigationIcon = { TextButton(onClick = onBack) { Text("Back") } }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            formError?.let {
                Text(it, color = MaterialTheme.colorScheme.error)
            }
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Title *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            OutlinedTextField(
                value = course,
                onValueChange = { course = it },
                label = { Text("Course / subject *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            OutlinedTextField(
                value = dateTime,
                onValueChange = { dateTime = it },
                label = { Text("Date or date-time *") },
                supportingText = { Text("Simple example: 2026-11-30 14:00") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            OutlinedTextField(
                value = location,
                onValueChange = { location = it },
                label = { Text("Location or meeting link") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Text("Status: $status")
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TaskStatus.values.forEach { value ->
                    TextButton(onClick = { status = value }) { Text(value) }
                }
            }
            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Notes") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )
            uiState.message?.let {
                Text(it, color = MaterialTheme.colorScheme.error)
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(
                    enabled = !uiState.isSaving && (isNew || existing != null),
                    onClick = {
                        if (title.isBlank() || course.isBlank() || dateTime.isBlank()) {
                            formError = "Title, course, and date are required."
                        } else {
                            formError = null
                            viewModel.save(
                                id = existing?.id,
                                title = title,
                                course = course,
                                dateTime = dateTime,
                                location = location,
                                status = status,
                                notes = notes,
                                onSaved = onBack
                            )
                        }
                    }
                ) { Text(if (uiState.isSaving) "Saving..." else "Save") }
                TextButton(onClick = onBack, enabled = !uiState.isSaving) { Text("Cancel") }
            }
        }
    }
}
