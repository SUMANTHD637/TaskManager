package com.taskflow.data.repository

import com.taskflow.data.dao.TaskDao
import com.taskflow.data.entities.TaskEntity
import kotlinx.coroutines.flow.Flow

class TaskRepository(private val taskDao: TaskDao) {

    suspend fun insertTask(task: TaskEntity): Long {
        return taskDao.insertTask(task)
    }

    suspend fun updateTask(task: TaskEntity) {
        taskDao.updateTask(task)
    }

    suspend fun deleteTask(task: TaskEntity) {
        taskDao.deleteTask(task)
    }

    suspend fun deleteTaskById(taskId: Int) {
        taskDao.deleteTaskById(taskId)
    }

    suspend fun getTaskById(taskId: Int): TaskEntity? {
        return taskDao.getTaskById(taskId)
    }

    fun getAllTasks(): Flow<List<TaskEntity>> {
        return taskDao.getAllTasks()
    }

    fun getIncompleteTasks(): Flow<List<TaskEntity>> {
        return taskDao.getIncompleteTasks()
    }

    fun getCompletedTasks(): Flow<List<TaskEntity>> {
        return taskDao.getCompletedTasks()
    }

    fun getHabits(): Flow<List<TaskEntity>> {
        return taskDao.getHabits()
    }

    fun getTasksByCategory(categoryId: Int): Flow<List<TaskEntity>> {
        return taskDao.getTasksByCategory(categoryId)
    }

    fun searchTasks(query: String): Flow<List<TaskEntity>> {
        return taskDao.searchTasks(query)
    }

    suspend fun markTaskComplete(taskId: Int, isComplete: Boolean) {
        val task = taskDao.getTaskById(taskId)
        task?.let {
            val updatedTask = it.copy(
                isCompleted = isComplete,
                streakCount = if (isComplete && it.isHabit) it.streakCount + 1 else it.streakCount,
                lastCompletedDate = if (isComplete) System.currentTimeMillis() else it.lastCompletedDate,
                updatedAt = System.currentTimeMillis()
            )
            taskDao.updateTask(updatedTask)
        }
    }
}

