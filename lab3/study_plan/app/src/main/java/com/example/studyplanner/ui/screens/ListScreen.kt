package com.example.studyplanner.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.weight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.studyplanner.data.local.StudyTaskEntity
import com.example.studyplanner.data.local.TaskStatus
import com.example.studyplanner.viewmodel.StudyTaskViewModel
import com.example.studyplanner.viewmodel.SyncStatus

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListScreen(
    viewModel: StudyTaskViewModel,
    onAdd: () -> Unit,
    onEdit: (String) -> Unit,
    onOpen: (String) -> Unit
) {
    val tasks by viewModel.tasks.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var filterMenu by remember { mutableStateOf(false) }
    var pendingDelete by remember { mutableStateOf<StudyTaskEntity?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Study Group Planner") },
                actions = {
                    IconButton(
                        onClick = viewModel::sync,
                        enabled = uiState.syncStatus != SyncStatus.SYNCING
                    ) {
                        Icon(Icons.Default.CloudSync, contentDescription = "Sync with Firestore")
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onAdd,
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("Add item") }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(horizontal = 16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (uiState.syncStatus == SyncStatus.SYNCING) {
                LinearProgressIndicator(Modifier.fillMaxWidth())
            }
            uiState.message?.let { message ->
                Card(Modifier.fillMaxWidth()) {
                    Row(
                        Modifier.padding(12.dp).fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(message, modifier = Modifier.weight(1f))
                        TextButton(onClick = viewModel::clearMessage) { Text("Dismiss") }
                    }
                }
            }

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("${tasks.size} task(s)", style = MaterialTheme.typography.titleMedium)
                Column {
                    Button(onClick = { filterMenu = true }) {
                        Text("Filter: ${uiState.filter}")
                    }
                    DropdownMenu(expanded = filterMenu, onDismissRequest = { filterMenu = false }) {
                        (listOf("ALL") + TaskStatus.values).forEach { value ->
                            DropdownMenuItem(
                                text = { Text(value) },
                                onClick = {
                                    viewModel.setFilter(value)
                                    filterMenu = false
                                }
                            )
                        }
                    }
                }
            }

            if (tasks.isEmpty()) {
                Text(
                    "No study tasks or events yet. Tap Add item to create one.",
                    modifier = Modifier.padding(top = 40.dp),
                    style = MaterialTheme.typography.bodyLarge
                )
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(tasks, key = { it.id }) { task ->
                        TaskCard(
                            task = task,
                            onOpen = { onOpen(task.id) },
                            onEdit = { onEdit(task.id) },
                            onStatus = { viewModel.markNextStatus(task) },
                            onDelete = { pendingDelete = task }
                        )
                    }
                }
            }
        }
    }

    pendingDelete?.let { task ->
        AlertDialog(
            onDismissRequest = { pendingDelete = null },
            title = { Text("Delete item?") },
            text = { Text("Delete '${task.title}' from the local database and Firestore when available?") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.delete(task)
                    pendingDelete = null
                }) { Text("Delete") }
            },
            dismissButton = {
                TextButton(onClick = { pendingDelete = null }) { Text("Cancel") }
            }
        )
    }
}

@Composable
private fun TaskCard(
    task: StudyTaskEntity,
    onOpen: () -> Unit,
    onEdit: () -> Unit,
    onStatus: () -> Unit,
    onDelete: () -> Unit
) {
    Card(onClick = onOpen, modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(5.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(task.title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                Text(task.status, color = MaterialTheme.colorScheme.primary)
            }
            Text("${task.course} | ${task.dateTime}")
            if (task.location.isNotBlank()) Text("Location/link: ${task.location}")
            if (task.notes.isNotBlank()) Text(task.notes, maxLines = 2)
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                IconButton(onClick = onStatus) {
                    Icon(Icons.Default.Refresh, contentDescription = "Change status")
                }
                Spacer(Modifier.width(4.dp))
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit")
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete")
                }
            }
        }
    }
}
