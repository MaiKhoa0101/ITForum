package com.example.itforum.user.utilities.note

import kotlinx.coroutines.flow.Flow


//class NoteRepository(private val noteDao: NoteDao) {
//
//    fun getAllNotes() = noteDao.getAllNotes()
//
//    suspend fun insert(note: NoteEntity) = noteDao.insert(note)
//
//    suspend fun update(note: NoteEntity) = noteDao.update(note)
//
//    suspend fun delete(note: NoteEntity) = noteDao.delete(note)
//}
class NoteRepository(private val noteDao: NoteDao) {

    fun getAllNotes(userId: String): Flow<List<NoteEntity>> {
        return noteDao.getAllNotes(userId)
    }

    suspend fun insert(note: NoteEntity) {
        noteDao.insert(note)
    }

    suspend fun update(note: NoteEntity) {
        noteDao.update(note)
    }

    suspend fun delete(note: NoteEntity) {
        noteDao.delete(note)
    }
}
