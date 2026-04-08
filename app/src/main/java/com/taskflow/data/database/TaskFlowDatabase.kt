package com.taskflow.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.taskflow.data.dao.CategoryDao
import com.taskflow.data.dao.NoteDao
import com.taskflow.data.dao.TaskDao
import com.taskflow.data.entities.CategoryEntity
import com.taskflow.data.entities.NoteEntity
import com.taskflow.data.entities.TaskEntity

@Database(
    entities = [TaskEntity::class, NoteEntity::class, CategoryEntity::class],
    version = 1,
    exportSchema = false
)
abstract class TaskFlowDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
    abstract fun noteDao(): NoteDao
    abstract fun categoryDao(): CategoryDao
}

