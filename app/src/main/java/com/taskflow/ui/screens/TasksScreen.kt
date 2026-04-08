package com.taskflow.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.taskflow.data.entities.TaskEntity
import com.taskflow.ui.components.TaskItem
import com.taskflow.viewmodel.TaskViewModel

@Composable
fun TasksScreen(
    viewModel: TaskViewModel,
    onAddTaskClick: () -> Unit,
    onTaskClick: (TaskEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    val incompleteTasks by viewModel.incompleteTasks.collectAsState()
    val completedTasks by viewModel.completedTasks.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddTaskClick,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Task")
            }
        }
    ) { innerPadding ->
        if (errorMessage != null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = errorMessage ?: "Unknown error",
                    color = MaterialTheme.colorScheme.error
                )
            }
        } else if (incompleteTasks.isEmpty() && completedTasks.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "No tasks yet",
                    style = MaterialTheme.typography.headlineSmall
                )
                Text(
                    text = "Tap + to create a new task",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    if (incompleteTasks.isNotEmpty()) {
                        Text(
                            text = "Incomplete",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }

                items(incompleteTasks) { task ->
                    TaskItem(
                        task = task,
                        onTaskClick = onTaskClick,
                        onDeleteClick = { viewModel.deleteTask(it) },
                        onCompleteChange = { viewModel.markTaskComplete(task.id, it) }
                    )
                }

                if (completedTasks.isNotEmpty()) {
                    item { Spacer(modifier = Modifier.height(16.dp)) }
                    item {
                        Text(
                            text = "Completed",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                    items(completedTasks) { task ->
                        TaskItem(
                            task = task,
                            onTaskClick = onTaskClick,
                            onDeleteClick = { viewModel.deleteTask(it) },
                            onCompleteChange = { viewModel.markTaskComplete(task.id, it) }
                        )
                    }
                }
            }
        }
    }
}

