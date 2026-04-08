package com.taskflow.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.taskflow.data.entities.TaskEntity
import com.taskflow.viewmodel.TaskViewModel
import java.util.*

// ✅ ENHANCED DATA CLASS - Map TaskEntity to UI Model
data class Task(
    val id: Int,
    val title: String,
    val type: String, // "Habit" or "Task"
    val color: Color,
    val isCompleted: Boolean = false
) {
    companion object {
        fun fromEntity(entity: TaskEntity): Task {
            return Task(
                id = entity.id,
                title = entity.title,
                type = if (entity.isHabit) "Habit" else "Task",
                color = if (entity.isHabit) Color(0xFFFFA500) else Color(0xFF9C27B0),
                isCompleted = entity.isCompleted
            )
        }
    }
}

// ======================== MAIN SCREEN ========================
@Composable
fun TaskScreen(viewModel: TaskViewModel) {
    var showAddTaskDialog by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf(getTodayIndex()) }

    // Observe tasks from ViewModel
    val incompleteTasks by viewModel.incompleteTasks.collectAsState()
    val completedTasks by viewModel.completedTasks.collectAsState()

    // Convert TaskEntity to UI Task
    val uiTasks = (incompleteTasks + completedTasks).map { Task.fromEntity(it) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddTaskDialog = true },
                containerColor = Color(0xFFE91E63)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Task", tint = Color.White)
            }
        },

        containerColor = Color.Black
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            TopBar()
            Spacer(modifier = Modifier.height(16.dp))

            DateRow(selectedDate) { selectedDate = it }
            Spacer(modifier = Modifier.height(16.dp))

            TaskList(uiTasks) { taskId, isCompleted ->
                viewModel.markTaskComplete(taskId, isCompleted)
            }
        }
    }

    // ✅ ADD TASK DIALOG
    if (showAddTaskDialog) {
        AddTaskDialog(
            onDismiss = { showAddTaskDialog = false },
            onAddTask = { title, isHabit ->
                viewModel.addTask(
                    title = title,
                    isHabit = isHabit,
                    habitFrequency = if (isHabit) "daily" else ""
                )
                showAddTaskDialog = false
            }
        )
    }
}

// ======================== TOP BAR ========================
@Composable
fun TopBar() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Default.Menu, contentDescription = null, tint = Color.White)
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = "Today",
            color = Color.White,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.weight(1f))
        Icon(Icons.Default.Search, contentDescription = null, tint = Color.White)
    }
}

// ======================== DATE ROW ========================
@Composable
fun DateRow(selectedIndex: Int, onDateSelect: (Int) -> Unit) {
    val days = listOf("Sat\n4", "Sun\n5", "Mon\n6", "Tue\n7", "Wed\n8")

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        days.forEachIndexed { index, day ->
            val isSelected = index == selectedIndex

            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        if (isSelected) Color(0xFFE91E63) else Color.DarkGray
                    )
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = rememberRipple(),
                        onClick = { onDateSelect(index) }
                    )
                    .padding(12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(text = day, color = Color.White, fontSize = 12.sp)
            }
        }
    }
}

// ======================== TASK LIST ========================
@Composable
fun TaskList(
    tasks: List<Task>,
    onTaskComplete: (Int, Boolean) -> Unit
) {
    if (tasks.isEmpty()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "No tasks yet 📋",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Tap + to create one",
                color = Color.Gray,
                fontSize = 14.sp
            )
        }
    } else {
        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(tasks) { task ->
                TaskItem(
                    task = task,
                    onComplete = { onTaskComplete(task.id, it) }
                )
            }
        }
    }
}

// ======================== TASK ITEM ========================
@Composable
fun TaskItem(task: Task, onComplete: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF1F1F1F), RoundedCornerShape(12.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left icon box
        Box(
            modifier = Modifier
                .size(50.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(task.color),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (task.type == "Habit") "🔄" else "✓",
                fontSize = 24.sp
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = task.title,
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = task.type,
                color = Color.Gray,
                fontSize = 12.sp
            )
        }

        // Right checkbox
        Checkbox(
            checked = task.isCompleted,
            onCheckedChange = { onComplete(it) },
            modifier = Modifier.size(20.dp)
        )
    }
}

// ======================== BOTTOM BAR ========================


// ======================== ADD TASK DIALOG ========================
@Composable
fun AddTaskDialog(
    onDismiss: () -> Unit,
    onAddTask: (String, Boolean) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var isHabit by remember { mutableStateOf(false) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .background(Color(0xFF1F1F1F), RoundedCornerShape(16.dp)),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF1F1F1F)
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header
                Text(
                    text = "Add New Task",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                // Title Input
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Task Title", color = Color.Gray) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF2A2A2A), RoundedCornerShape(8.dp)),
                    textStyle = LocalTextStyle.current.copy(
                        color = Color.White
                    ),
                    singleLine = true
                )

                // Habit Checkbox
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF2A2A2A), RoundedCornerShape(8.dp))
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = isHabit,
                        onCheckedChange = { isHabit = it }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("This is a Habit 🔄", color = Color.White)
                }

                // Buttons
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Cancel Button
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                    ) {
                        Text("Cancel", color = Color.White)
                    }

                    // Add Button
                    Button(
                        onClick = {
                            if (title.isNotBlank()) {
                                onAddTask(title, isHabit)
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFE91E63)
                        ),
                        enabled = title.isNotBlank()
                    ) {
                        Text("Add Task", color = Color.White)
                    }
                }
            }
        }
    }
}

// ======================== HELPER FUNCTIONS ========================
fun getTodayIndex(): Int {
    val calendar = Calendar.getInstance()
    return (calendar.get(Calendar.DAY_OF_WEEK) - 1) % 7
}






