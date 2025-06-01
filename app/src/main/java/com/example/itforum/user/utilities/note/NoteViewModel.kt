package com.example.itforum.user.utilities.note

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class NoteViewModel(application: Application) : AndroidViewModel(application) {

    private val noteDao = NoteDatabase.getDatabase(application).noteDao()
    private val repository = NoteRepository(noteDao)

    val notes: LiveData<List<NoteEntity>> = repository.getAllNotes().asLiveData()

    fun addNote(note: NoteEntity) {
        viewModelScope.launch {
            repository.insert(note)
        }
    }

    fun updateNote(note: NoteEntity) {
        viewModelScope.launch {
            repository.update(note)
        }
    }

    fun deleteNote(note: NoteEntity) {
        viewModelScope.launch {
            repository.delete(note)
        }
    }
}

