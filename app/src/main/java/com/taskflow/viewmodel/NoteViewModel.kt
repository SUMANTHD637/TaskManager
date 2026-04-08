package com.taskflow.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.taskflow.data.entities.NoteEntity
import com.taskflow.data.repository.NoteRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class NoteViewModel(private val repository: NoteRepository) : ViewModel() {

    private val _allNotes = MutableStateFlow<List<NoteEntity>>(emptyList())
    val allNotes: StateFlow<List<NoteEntity>> = _allNotes.asStateFlow()

    private val _pinnedNotes = MutableStateFlow<List<NoteEntity>>(emptyList())
    val pinnedNotes: StateFlow<List<NoteEntity>> = _pinnedNotes.asStateFlow()

    private val _searchResults = MutableStateFlow<List<NoteEntity>>(emptyList())
    val searchResults: StateFlow<List<NoteEntity>> = _searchResults.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        loadAllNotes()
        loadPinnedNotes()
    }

    private fun loadAllNotes() {
        viewModelScope.launch {
            repository.getAllNotes().collect { notes ->
                _allNotes.value = notes
            }
        }
    }

    private fun loadPinnedNotes() {
        viewModelScope.launch {
            repository.getPinnedNotes().collect { notes ->
                _pinnedNotes.value = notes
            }
        }
    }

    fun addNote(title: String, content: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val newNote = NoteEntity(
                    title = title,
                    content = content
                )
                repository.insertNote(newNote)
                _errorMessage.value = null
            } catch (e: Exception) {
                _errorMessage.value = "Failed to add note: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateNote(note: NoteEntity) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val updatedNote = note.copy(updatedAt = System.currentTimeMillis())
                repository.updateNote(updatedNote)
                _errorMessage.value = null
            } catch (e: Exception) {
                _errorMessage.value = "Failed to update note: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteNote(note: NoteEntity) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                repository.deleteNote(note)
                _errorMessage.value = null
            } catch (e: Exception) {
                _errorMessage.value = "Failed to delete note: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun togglePinNote(noteId: Int, isPinned: Boolean) {
        viewModelScope.launch {
            try {
                repository.togglePinNote(noteId, isPinned)
                _errorMessage.value = null
            } catch (e: Exception) {
                _errorMessage.value = "Failed to update note: ${e.message}"
            }
        }
    }

    fun searchNotes(query: String) {
        viewModelScope.launch {
            repository.searchNotes(query).collect { results ->
                _searchResults.value = results
            }
        }
    }

    fun clearErrorMessage() {
        _errorMessage.value = null
    }
}

