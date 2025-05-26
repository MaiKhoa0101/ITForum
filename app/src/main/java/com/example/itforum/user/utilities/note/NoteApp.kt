package com.example.itforum.utilities.note

import androidx.compose.runtime.*
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class Note(val title: String, val content: String, val date: String)

@Composable
fun NotesApp(onBackToHome: () -> Unit) {
    val notes = remember { mutableStateListOf<Note>() }

    // Biến lưu note hiện đang chỉnh sửa, null = không chỉnh sửa note nào (chỉ tạo mới)
    var editingNoteIndex by remember { mutableStateOf<Int?>(null) }
    var isEditing by remember { mutableStateOf(false) }

    if (isEditing) {
        // Nếu editingNoteIndex == null => tạo mới, ngược lại chỉnh sửa note đó
        val noteToEdit = editingNoteIndex?.let { notes[it] }

        NoteEditScreen(
            initialTitle = noteToEdit?.title ?: "",
            initialContent = noteToEdit?.content ?: "",
            onSave = { title, content ->
                val date = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())

                if (editingNoteIndex == null) {
                    // Thêm mới note
                    notes.add(Note(title, content, date))
                } else {
                    // Cập nhật note hiện tại
                    notes[editingNoteIndex!!] = Note(title, content, date)
                }
                isEditing = false
                editingNoteIndex = null
            },
            onCancel = {
                isEditing = false
                editingNoteIndex = null
            }
        )
    } else {
        NotesListScreen(
            notes = notes,
            onAddNote = {
                editingNoteIndex = null
                isEditing = true
            },
            onEdit = { note ->
                // Tìm index note trong list để chỉnh sửa
                val index = notes.indexOf(note)
                if (index != -1) {
                    editingNoteIndex = index
                    isEditing = true
                }
            },
            onDelete = { note ->
                notes.remove(note)
            },
            onBackToHome = onBackToHome
        )
    }
}
