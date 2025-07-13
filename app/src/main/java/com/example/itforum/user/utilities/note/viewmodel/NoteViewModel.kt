package com.example.itforum.user.utilities.note.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.itforum.user.utilities.note.NoteDatabase
import com.example.itforum.user.utilities.note.NoteEntity
import com.example.itforum.user.utilities.note.NoteRepository
import kotlinx.coroutines.launch

class NoteViewModel(application: Application) : AndroidViewModel(application) {

    private val noteDao = NoteDatabase.Companion.getDatabase(application).noteDao()
    private val repository = NoteRepository(noteDao)

//    val notes: LiveData<List<NoteEntity>> = repository.getAllNotes().asLiveData()
    // Lấy SharedPreferences
    private val sharedPreferences = application.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    // Lấy userId từ SharedPreferences
    private val userId: String? = sharedPreferences.getString("userId", null)

    // Tạo notes LiveData lọc theo userId
    val notes: LiveData<List<NoteEntity>> =
        if (userId != null) repository.getAllNotes(userId).asLiveData()
        else MutableLiveData(emptyList())
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