package com.example.itforum.utilities.note


import android.app.Application
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.livedata.observeAsState
import com.example.itforum.user.utilities.note.NoteEntity
import com.example.itforum.user.utilities.note.NoteViewModel
import com.example.itforum.user.utilities.note.NoteViewModelFactory
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun NotesApp(
    onBackToHome: () -> Unit,
) {
    val context = LocalContext.current.applicationContext as Application
    val viewModel: NoteViewModel = viewModel(
        factory = NoteViewModelFactory(context)
    )

    // Lấy danh sách ghi chú từ ViewModel
    val notes by viewModel.notes.observeAsState(initial = emptyList())

    var editingNote by remember { mutableStateOf<NoteEntity?>(null) }

    if (editingNote != null) {
        NoteEditScreen(
            initialTitle = editingNote?.title ?: "",
            initialContent = editingNote?.content ?: "",
            onSave = { title, content ->
                val date = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
                val current = editingNote!!
                val updatedNote = current.copy(
                    title = title,
                    content = content,
                    date = date
                )

                if (current.id == 0) {
                    viewModel.addNote(updatedNote) // Thêm mới
                } else {
                    viewModel.updateNote(updatedNote) // Cập nhật
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
}}




//@Composable
//fun NotesApp(onBackToHome: () -> Unit) {
//    val notes = remember { mutableStateListOf<Note>() }
//
//    // Biến lưu note hiện đang chỉnh sửa, null = không chỉnh sửa note nào (chỉ tạo mới)
//    var editingNoteIndex by remember { mutableStateOf<Int?>(null) }
//    var isEditing by remember { mutableStateOf(false) }
//
//    if (isEditing) {
//        // Nếu editingNoteIndex == null => tạo mới, ngược lại chỉnh sửa note đó
//        val noteToEdit = editingNoteIndex?.let { notes[it] }
//
//        NoteEditScreen(
//            initialTitle = noteToEdit?.title ?: "",
//            initialContent = noteToEdit?.content ?: "",
//            onSave = { title, content ->
//                val date = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
//
//                if (editingNoteIndex == null) {
//                    // Thêm mới note
//                    notes.add(Note(title, content, date))
//                } else {
//                    // Cập nhật note hiện tại
//                    notes[editingNoteIndex!!] = Note(title, content, date)
//                }
//                isEditing = false
//                editingNoteIndex = null
//            },
//            onCancel = {
//                isEditing = false
//                editingNoteIndex = null
//            }
//        )
//    } else {
//        NotesListScreen(
//            notes = notes,
//            onAddNote = {
//                editingNoteIndex = null
//                isEditing = true
//            },
//            onEdit = { note ->
//                // Tìm index note trong list để chỉnh sửa
//                val index = notes.indexOf(note)
//                if (index != -1) {
//                    editingNoteIndex = index
//                    isEditing = true
//                }
//            },
//            onDelete = { note ->
//                notes.remove(note)
//            },
//            onBackToHome = onBackToHome
//        )
//    }
//}
