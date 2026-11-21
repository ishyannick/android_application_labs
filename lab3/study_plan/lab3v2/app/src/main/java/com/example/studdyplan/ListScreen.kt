package com.example.studdyplan.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.studdyplan.data.local.StudyTaskEntity
import com.example.studdyplan.ui.viewmodel.StudyTaskViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListScreen(
    viewModel: StudyTaskViewModel,
    onNavigateToAddEdit: (String?) -> Unit
) {
    val tasks by viewModel.allTasks.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Study Planner") },
                actions = {
                    IconButton(onClick = { /* TODO: Trigger Firestore Sync later */ }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Sync with Firestore")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { onNavigateToAddEdit(null) }) {
                Icon(Icons.Default.Add, contentDescription = "Add Task")
            }
        }
    ) { innerPadding ->
        if (tasks.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text("No study tasks yet. Tap + to add one!", style = MaterialTheme.typography.bodyLarge)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(tasks) { task ->
                    TaskRowItem(task = task, onClick = { onNavigateToAddEdit(task.id) })
                }
            }
        }
    }
}

@Composable
fun TaskRowItem(task: StudyTaskEntity, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = task.title, style = MaterialTheme.typography.titleMedium)
                Text(text = task.status, style = MaterialTheme.typography.labelMedium)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Course: ${task.course}", style = MaterialTheme.typography.bodyMedium)
            Text(text = "Date: ${task.dateTime}", style = MaterialTheme.typography.bodySmall)
        }
    }
}