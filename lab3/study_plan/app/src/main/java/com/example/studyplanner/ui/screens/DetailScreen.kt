package com.example.studyplanner.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
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
import com.example.studyplanner.viewmodel.StudyTaskViewModel
import java.text.DateFormat
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    taskId: String,
    viewModel: StudyTaskViewModel,
    onBack: () -> Unit,
    onEdit: () -> Unit
) {
    val task by viewModel.task(taskId).collectAsStateWithLifecycle(initialValue = null)
    var confirmDelete by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Item details") },
                navigationIcon = { TextButton(onClick = onBack) { Text("Back") } }
            )
        }
    ) { padding ->
        val item = task
        if (item == null) {
            Text("Loading item...", Modifier.padding(padding).padding(24.dp))
        } else {
            Column(
                modifier = Modifier
                    .padding(padding)
                    .padding(16.dp)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(item.title, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                        DetailLine("Course", item.course)
                        DetailLine("Date", item.dateTime)
                        DetailLine("Location/link", item.location.ifBlank { "Not provided" })
                        DetailLine("Status", item.status)
                        DetailLine("Notes", item.notes.ifBlank { "No notes" })
                        DetailLine(
                            "Updated",
                            DateFormat.getDateTimeInstance().format(Date(item.updatedAt))
                        )
                        DetailLine("Shared ID", item.id)
                    }
                }
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Button(onClick = onEdit) { Text("Edit") }
                    Button(onClick = { viewModel.markNextStatus(item) }) { Text("Change status") }
                    TextButton(onClick = { confirmDelete = true }) { Text("Delete") }
                }
            }
        }
    }

    if (confirmDelete) {
        AlertDialog(
            onDismissRequest = { confirmDelete = false },
            title = { Text("Delete item?") },
            text = { Text("This removes the item locally and remotely when Firebase is available.") },
            confirmButton = {
                TextButton(onClick = {
                    task?.let { viewModel.delete(it, onBack) }
                    confirmDelete = false
                }) { Text("Delete") }
            },
            dismissButton = {
                TextButton(onClick = { confirmDelete = false }) { Text("Cancel") }
            }
        )
    }
}

@Composable
private fun DetailLine(label: String, value: String) {
    Column {
        Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
        Text(value)
    }
}
