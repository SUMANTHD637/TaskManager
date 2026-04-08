package com.taskflow

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.remember
import androidx.room.Room
import androidx.room.*
import com.taskflow.data.database.TaskFlowDatabase
import com.taskflow.data.repository.NoteRepository
import com.taskflow.data.repository.TaskRepository
import com.taskflow.ui.TaskFlowApp
import com.taskflow.ui.theme.TaskFlowTheme
import com.taskflow.viewmodel.NoteViewModelFactory
import com.taskflow.viewmodel.TaskViewModelFactory

class MainActivity : ComponentActivity() {

    private lateinit var database: TaskFlowDatabase
    private lateinit var taskRepository: TaskRepository
    private lateinit var noteRepository: NoteRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Room Database
        database = Room.databaseBuilder(
            applicationContext,
            TaskFlowDatabase::class.java,
            "taskflow_database"
        ).build()

        // Initialize Repositories
        taskRepository = TaskRepository(database.taskDao())
        noteRepository = NoteRepository(database.noteDao())

        setContent {
            TaskFlowTheme {
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

