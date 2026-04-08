package com.taskflow.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.taskflow.ui.screens.AddNoteScreen
import com.taskflow.ui.screens.AddTaskScreen
import com.taskflow.ui.screens.NotesScreen
import com.taskflow.ui.screens.TasksScreen
import com.taskflow.viewmodel.NoteViewModel
import com.taskflow.viewmodel.TaskViewModel

sealed class NavigationItem(val route: String, val label: String) {
    data object Tasks : NavigationItem("tasks", "Tasks")
    data object Notes : NavigationItem("notes", "Notes")
    data object AddTask : NavigationItem("add_task", "Add Task")
    data object AddNote : NavigationItem("add_note", "Add Note")
}

@Composable
fun TaskFlowApp(
    taskViewModel: TaskViewModel,
    noteViewModel: NoteViewModel,
    navController: NavHostController = rememberNavController()
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showBottomBar = currentRoute in listOf(NavigationItem.Tasks.route, NavigationItem.Notes.route)

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Check, contentDescription = "Tasks") },
                        label = { Text("Tasks") },
                        selected = currentRoute == NavigationItem.Tasks.route,
                        onClick = {
                            navController.navigate(NavigationItem.Tasks.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Edit, contentDescription = "Notes") },
                        label = { Text("Notes") },
                        selected = currentRoute == NavigationItem.Notes.route,
                        onClick = {
                            navController.navigate(NavigationItem.Notes.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = NavigationItem.Tasks.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(NavigationItem.Tasks.route) {
                TasksScreen(
                    viewModel = taskViewModel,
                    onAddTaskClick = { navController.navigate(NavigationItem.AddTask.route) },
                    onTaskClick = { }
                )
            }

            composable(NavigationItem.Notes.route) {
                NotesScreen(
                    viewModel = noteViewModel,
                    onAddNoteClick = { navController.navigate(NavigationItem.AddNote.route) },
                    onNoteClick = { }
                )
            }

            composable(NavigationItem.AddTask.route) {
                AddTaskScreen(
                    viewModel = taskViewModel,
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable(NavigationItem.AddNote.route) {
                AddNoteScreen(
                    viewModel = noteViewModel,
                    onBackClick = { navController.popBackStack() }
                )
            }
        }
    }
}

