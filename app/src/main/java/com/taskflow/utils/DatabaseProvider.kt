package com.taskflow.utils

import android.content.Context
import androidx.room.Room
import com.taskflow.data.database.TaskFlowDatabase
import com.taskflow.data.repository.CategoryRepository
import com.taskflow.data.repository.NoteRepository
import com.taskflow.data.repository.TaskRepository

object DatabaseProvider {
    private var database: TaskFlowDatabase? = null

    fun getDatabase(context: Context): TaskFlowDatabase {
        return database ?: synchronized(this) {
            val instance = Room.databaseBuilder(
                context.applicationContext,
                TaskFlowDatabase::class.java,
                "taskflow_database"
            )
            .fallbackToDestructiveMigration()
            .build()
            database = instance
            instance
        }
    }

    fun getTaskRepository(context: Context): TaskRepository {
        return TaskRepository(getDatabase(context).taskDao())
    }

    fun getNoteRepository(context: Context): NoteRepository {
        return NoteRepository(getDatabase(context).noteDao())
    }

    fun getCategoryRepository(context: Context): CategoryRepository {
        return CategoryRepository(getDatabase(context).categoryDao())
    }
}

