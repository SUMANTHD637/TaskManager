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

// ======================== UI MODEL ========================
data class Task(
    val id: Int,
    val title: String,
    val type: String,
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

// ======================== DATE HELPERS ========================
data class DayInfo(
    val dayName: String,
    val dayNum: Int,
    val month: Int,
    val year: Int,
    val isToday: Boolean
)

fun getWeekDays(): List<DayInfo> {
    val calendar = Calendar.getInstance()
    val todayDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
    val daysFromMonday = (todayDayOfWeek - Calendar.MONDAY + 7) % 7
    calendar.add(Calendar.DAY_OF_YEAR, -daysFromMonday)

    val today = Calendar.getInstance()
    val dayNames = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")

    return (0..6).map { i ->
        val isToday = calendar.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)
                && calendar.get(Calendar.YEAR) == today.get(Calendar.YEAR)
        val info = DayInfo(
            dayName = dayNames[i],
            dayNum = calendar.get(Calendar.DAY_OF_MONTH),
            month = calendar.get(Calendar.MONTH) + 1,
            year = calendar.get(Calendar.YEAR),
            isToday = isToday
        )
        calendar.add(Calendar.DAY_OF_YEAR, 1)
        info
    }
}

fun getTodayIndex(): Int {
    val calendar = Calendar.getInstance()
    val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
    return (dayOfWeek - Calendar.MONDAY + 7) % 7
}

fun getMonthName(month: Int): String {
    return listOf(
        "January", "February", "March", "April", "May", "June",
        "July", "August", "September", "October", "November", "December"
    )[month - 1]
}

// ======================== MAIN SCREEN ========================
@Composable
fun TaskScreen(viewModel: TaskViewModel) {
    var showAddTaskDialog by remember { mutableStateOf(false) }
    val weekDays = remember { getWeekDays() }
    var selectedIndex by remember { mutableStateOf(getTodayIndex()) }
    val selectedDay = weekDays[selectedIndex]

    val incompleteTasks by viewModel.incompleteTasks.collectAsState()
    val completedTasks by viewModel.completedTasks.collectAsState()
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
            TopBar(selectedDay)
            Spacer(modifier = Modifier.height(16.dp))
            DateRow(weekDays = weekDays, selectedIndex = selectedIndex, onDateSelect = { selectedIndex = it })
            Spacer(modifier = Modifier.height(16.dp))
            TaskList(uiTasks) { taskId, isCompleted -> viewModel.markTaskComplete(taskId, isCompleted) }
        }
    }

    if (showAddTaskDialog) {
        AddTaskDialog(
            onDismiss = { showAddTaskDialog = false },
            onAddTask = { title, isHabit ->
                viewModel.addTask(title = title, isHabit = isHabit, habitFrequency = if (isHabit) "daily" else "")
                showAddTaskDialog = false
            }
        )
    }
}

// ======================== TOP BAR ========================
@Composable
fun TopBar(selectedDay: DayInfo) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Default.Menu, contentDescription = null, tint = Color.White)
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = if (selectedDay.isToday) "Today" else "${selectedDay.dayName}, ${selectedDay.dayNum}",
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "${getMonthName(selectedDay.month)} ${selectedDay.year}",
                color = Color.Gray,
                fontSize = 13.sp
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        Icon(Icons.Default.Search, contentDescription = null, tint = Color.White)
    }
}

// ======================== DATE ROW ========================
@Composable
fun DateRow(weekDays: List<DayInfo>, selectedIndex: Int, onDateSelect: (Int) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        weekDays.forEachIndexed { index, day ->
            val isSelected = index == selectedIndex
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        when {
                            isSelected -> Color(0xFFE91E63)
                            day.isToday -> Color(0xFF3A3A3A)
                            else -> Color.DarkGray
                        }
                    )
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = rememberRipple(),
                        onClick = { onDateSelect(index) }
                    )
                    .padding(vertical = 10.dp, horizontal = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = day.dayName, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                    Text(
                        text = day.dayNum.toString(),
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = if (day.isToday) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }
    }
}

// ======================== TASK LIST ========================
@Composable
fun TaskList(tasks: List<Task>, onTaskComplete: (Int, Boolean) -> Unit) {
    if (tasks.isEmpty()) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = "No tasks yet 📋", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Tap + to create one", color = Color.Gray, fontSize = 14.sp)
        }
    } else {
        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(tasks) { task ->
                TaskItem(task = task, onComplete = { onTaskComplete(task.id, it) })
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
        Box(
            modifier = Modifier
                .size(50.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(task.color),
            contentAlignment = Alignment.Center
        ) {
            Text(text = if (task.type == "Habit") "🔄" else "✓", fontSize = 24.sp)
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = task.title, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Medium)
            Text(text = task.type, color = Color.Gray, fontSize = 12.sp)
        }
        Checkbox(
            checked = task.isCompleted,
            onCheckedChange = { onComplete(it) },
            modifier = Modifier.size(20.dp)
        )
    }
}

// ======================== ADD TASK DIALOG ========================
@Composable
fun AddTaskDialog(onDismiss: () -> Unit, onAddTask: (String, Boolean) -> Unit) {
    var title by remember { mutableStateOf("") }
    var isHabit by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .background(Color(0xFF1F1F1F), RoundedCornerShape(16.dp)),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1F1F1F))
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(text = "Add New Task", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Task Title", color = Color.Gray) },
                    modifier = Modifier.fillMaxWidth().background(Color(0xFF2A2A2A), RoundedCornerShape(8.dp)),
                    textStyle = LocalTextStyle.current.copy(color = Color.White),
                    singleLine = true
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF2A2A2A), RoundedCornerShape(8.dp))
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(checked = isHabit, onCheckedChange = { isHabit = it })
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("This is a Habit 🔄", color = Color.White)
                }

                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f).height(44.dp)) {
                        Text("Cancel", color = Color.White)
                    }
                    Button(
                        onClick = { if (title.isNotBlank()) onAddTask(title, isHabit) },
                        modifier = Modifier.weight(1f).height(44.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE91E63)),
                        enabled = title.isNotBlank()
                    ) {
                        Text("Add Task", color = Color.White)
                    }
                }
            }
        }
    }
}
