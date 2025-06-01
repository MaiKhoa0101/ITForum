package com.example.itforum.user.utilities.note

import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity(tableName = "notes")
data class NoteEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val content: String,
    val date: String
)
