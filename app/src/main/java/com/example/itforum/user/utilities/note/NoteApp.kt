package com.example.itforum.utilities.note


import android.app.Application
import android.content.Context
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.livedata.observeAsState
import com.example.itforum.user.utilities.note.NoteEntity
import com.example.itforum.user.utilities.note.viewmodel.NoteViewModel
import com.example.itforum.user.utilities.note.viewmodel.NoteViewModelFactory
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun NotesApp(
    onBackToHome: () -> Unit,
) {

    val context = LocalContext.current.applicationContext as Application
    val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    val userId = sharedPreferences.getString("userId", "") ?: ""

    val viewModel: NoteViewModel = viewModel(
        factory = NoteViewModelFactory(context)
    )

    val notes by viewModel.notes.observeAsState(initial = emptyList())
    var editingNote by remember { mutableStateOf<NoteEntity?>(null) }

    if (editingNote != null) {
        NoteEditScreen(
            initialTitle = editingNote?.title ?: "",
            initialContent = editingNote?.content ?: "",
            userId = userId, // ✅ Truyền userId vào NoteEditScreen
            onSave = { updatedNote ->  // ✅ Đây là NoteEntity
                val noteToSave = updatedNote.copy(
                    id = editingNote!!.id  // giữ lại ID nếu đang edit
                )

                if (noteToSave.id == 0) {
                    viewModel.addNote(noteToSave)
                } else {
                    viewModel.updateNote(noteToSave)
                }

                editingNote = null
            },
            onCancel = {
                editingNote = null
            }
        )
    } else {
        NotesListScreen(
            viewModel = viewModel,
            notes = notes,
            onDelete = { viewModel.deleteNote(it) },
            onBackToHome = onBackToHome
        )
    }
}



