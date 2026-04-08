package com.taskflow.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.taskflow.data.entities.TaskEntity
import com.taskflow.data.repository.TaskRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TaskViewModel(private val repository: TaskRepository) : ViewModel() {

    private val _allTasks = MutableStateFlow<List<TaskEntity>>(emptyList())
    val allTasks: StateFlow<List<TaskEntity>> = _allTasks.asStateFlow()

    private val _incompleteTasks = MutableStateFlow<List<TaskEntity>>(emptyList())
    val incompleteTasks: StateFlow<List<TaskEntity>> = _incompleteTasks.asStateFlow()

    private val _completedTasks = MutableStateFlow<List<TaskEntity>>(emptyList())
    val completedTasks: StateFlow<List<TaskEntity>> = _completedTasks.asStateFlow()

    private val _habits = MutableStateFlow<List<TaskEntity>>(emptyList())
    val habits: StateFlow<List<TaskEntity>> = _habits.asStateFlow()

    private val _searchResults = MutableStateFlow<List<TaskEntity>>(emptyList())
    val searchResults: StateFlow<List<TaskEntity>> = _searchResults.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        loadAllTasks()
        loadIncompleteTasks()
        loadCompletedTasks()
        loadHabits()
    }

    private fun loadAllTasks() {
        viewModelScope.launch {
            repository.getAllTasks().collect { tasks ->
                _allTasks.value = tasks
            }
        }
    }

    private fun loadIncompleteTasks() {
        viewModelScope.launch {
            repository.getIncompleteTasks().collect { tasks ->
                _incompleteTasks.value = tasks
            }
        }
    }

    private fun loadCompletedTasks() {
        viewModelScope.launch {
            repository.getCompletedTasks().collect { tasks ->
                _completedTasks.value = tasks
            }
        }
    }

    private fun loadHabits() {
        viewModelScope.launch {
            repository.getHabits().collect { habits ->
                _habits.value = habits
            }
        }
    }

    fun addTask(title: String, description: String = "", categoryId: Int = 1, isHabit: Boolean = false, habitFrequency: String = "") {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val newTask = TaskEntity(
                    title = title,
                    description = description,
                    categoryId = categoryId,
                    isHabit = isHabit,
                    habitFrequency = habitFrequency
                )
                repository.insertTask(newTask)
                _errorMessage.value = null
            } catch (e: Exception) {
                _errorMessage.value = "Failed to add task: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateTask(task: TaskEntity) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val updatedTask = task.copy(updatedAt = System.currentTimeMillis())
                repository.updateTask(updatedTask)
                _errorMessage.value = null
            } catch (e: Exception) {
                _errorMessage.value = "Failed to update task: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteTask(task: TaskEntity) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                repository.deleteTask(task)
                _errorMessage.value = null
            } catch (e: Exception) {
                _errorMessage.value = "Failed to delete task: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun markTaskComplete(taskId: Int, isComplete: Boolean) {
        viewModelScope.launch {
            try {
                repository.markTaskComplete(taskId, isComplete)
                _errorMessage.value = null
            } catch (e: Exception) {
                _errorMessage.value = "Failed to update task: ${e.message}"
            }
        }
    }

    fun searchTasks(query: String) {
        viewModelScope.launch {
            repository.searchTasks(query).collect { results ->
                _searchResults.value = results
            }
        }
    }

    fun clearErrorMessage() {
        _errorMessage.value = null
    }
}

