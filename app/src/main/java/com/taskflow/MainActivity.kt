package com.taskflow

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.remember
import com.taskflow.data.repository.NoteRepository
import com.taskflow.data.repository.TaskRepository
import com.taskflow.ui.TaskFlowApp
import com.taskflow.ui.theme.TaskFlowTheme
import com.taskflow.utils.DatabaseProvider
import com.taskflow.viewmodel.NoteViewModelFactory
import com.taskflow.viewmodel.TaskViewModelFactory

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            TaskFlowTheme {
                // Get repositories from DatabaseProvider (lazy initialized)
                val taskRepository = DatabaseProvider.getTaskRepository(this@MainActivity)
                val noteRepository = DatabaseProvider.getNoteRepository(this@MainActivity)

                // Create ViewModels using factories
                val taskViewModel = remember {
                    TaskViewModelFactory(taskRepository).create(
                        com.taskflow.viewmodel.TaskViewModel::class.java
                    )
                }

                val noteViewModel = remember {
                    NoteViewModelFactory(noteRepository).create(
                        com.taskflow.viewmodel.NoteViewModel::class.java
                    )
                }

                TaskFlowApp(
                    taskViewModel = taskViewModel,
                    noteViewModel = noteViewModel
                )
            }
        }
    }
}

