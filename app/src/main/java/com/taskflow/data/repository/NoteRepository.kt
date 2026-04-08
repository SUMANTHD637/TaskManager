package com.taskflow.data.repository

import com.taskflow.data.dao.NoteDao
import com.taskflow.data.entities.NoteEntity
import kotlinx.coroutines.flow.Flow

class NoteRepository(private val noteDao: NoteDao) {

    suspend fun insertNote(note: NoteEntity): Long {
        return noteDao.insertNote(note)
    }

    suspend fun updateNote(note: NoteEntity) {
        noteDao.updateNote(note)
    }

    suspend fun deleteNote(note: NoteEntity) {
        noteDao.deleteNote(note)
    }

    suspend fun deleteNoteById(noteId: Int) {
        noteDao.deleteNoteById(noteId)
    }

    suspend fun getNoteById(noteId: Int): NoteEntity? {
        return noteDao.getNoteById(noteId)
    }

    fun getAllNotes(): Flow<List<NoteEntity>> {
        return noteDao.getAllNotes()
    }

    fun getPinnedNotes(): Flow<List<NoteEntity>> {
        return noteDao.getPinnedNotes()
    }

    fun searchNotes(query: String): Flow<List<NoteEntity>> {
        return noteDao.searchNotes(query)
    }

    suspend fun togglePinNote(noteId: Int, isPinned: Boolean) {
        val note = noteDao.getNoteById(noteId)
        note?.let {
            val updatedNote = it.copy(
                isPinned = isPinned,
                updatedAt = System.currentTimeMillis()
            )
            noteDao.updateNote(updatedNote)
        }
    }
}

