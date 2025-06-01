


package com.example.itforum.utilities.note

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.itforum.R
import com.example.itforum.user.utilities.note.NoteEntity
import com.example.itforum.user.utilities.note.NoteViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun NotesListScreen(
    viewModel: NoteViewModel,
    notes: List<NoteEntity>,
    onDelete: (NoteEntity) -> Unit,
    onBackToHome: () -> Unit
) {
    var editingNote by remember { mutableStateOf<NoteEntity?>(null) }
    var isEditing by remember { mutableStateOf(false) }

    if (isEditing && editingNote != null) {
        NoteEditScreen(
            initialTitle = editingNote!!.title,
            initialContent = editingNote!!.content,
            onSave = { title, content ->
                val date = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
                val updatedNote = editingNote!!.copy(title = title, content = content, date = date)
                if (updatedNote.id == 0) {
                    viewModel.addNote(updatedNote)
                } else {
                    viewModel.updateNote(updatedNote)
                }
                isEditing = false
            },
            onCancel = {
                isEditing = false
            }
        )
    } else {
        Scaffold(
            topBar = { NotesTopBar(onBackToHome) },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = {
                        val date = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
                        editingNote = NoteEntity(0, "", "", date)
                        isEditing = true
                    },
                    backgroundColor = Color(0xFF00AEFF)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Thêm",modifier = Modifier.size(45.dp))
                }
            }
        ) { innerPadding ->
            Box(modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
            ) {
                if (notes.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Chưa có ghi chú nào.")
                    }
                } else {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(notes) { note ->
                            var expanded by remember { mutableStateOf(false) }

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 8.dp),
                                elevation = 6.dp,
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(verticalAlignment = Alignment.Top) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(note.title, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                                            Spacer(modifier = Modifier.height(6.dp))
                                            Text(note.content, maxLines = 2, overflow = TextOverflow.Ellipsis)
                                            Spacer(modifier = Modifier.height(6.dp))
                                            Text(note.date, fontSize = 12.sp, color = Color.Gray)
                                        }

                                        // 🔽 Nút ba chấm và menu
                                        Box {
                                            IconButton(onClick = { expanded = true }) {
                                                Icon(Icons.Default.MoreVert, contentDescription = "Tùy chọn")
                                            }

                                            DropdownMenu(
                                                expanded = expanded,
                                                onDismissRequest = { expanded = false }
                                            ) {
                                                DropdownMenuItem(onClick = {
                                                    expanded = false
                                                    editingNote = note
                                                    isEditing = true
                                                }) {
                                                    Text("Chỉnh sửa")
                                                }
                                                DropdownMenuItem(onClick = {
                                                    expanded = false
                                                    onDelete(note)
                                                }) {
                                                    Text("Xoá")
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

//                        items(notes) { note ->
//                            Card(
//                                modifier = Modifier
//                                    .fillMaxWidth()
//                                    .padding(horizontal = 16.dp, vertical = 8.dp)
//                                    .clickable {
//                                        editingNote = note
//                                        isEditing = true
//                                    },
//                                elevation = 6.dp,
//                                shape = RoundedCornerShape(12.dp)
//                            ) {
//                                Column(modifier = Modifier.padding(16.dp)) {
//                                    Text(note.title, fontWeight = FontWeight.Bold, fontSize = 20.sp)
//                                    Spacer(modifier = Modifier.height(6.dp))
//                                    Text(note.content, maxLines = 2, overflow = TextOverflow.Ellipsis)
//                                    Spacer(modifier = Modifier.height(6.dp))
//                                    Text(note.date, fontSize = 12.sp, color = Color.Gray)
//                                }
//                            }
//                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NotesTopBar(onBackToHome: () -> Unit) {
    Surface(
        color = MaterialTheme.colorScheme.primaryContainer,
        elevation = 4.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .padding(horizontal = 16.dp,),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 8.dp, top = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBackToHome, modifier = Modifier.offset(x = (-12).dp)) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }
                Icon(
                    painter = painterResource(id = R.drawable.note),
                    contentDescription = "Note Icon",
                    tint = Color.White,
                    modifier = Modifier.size(38.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))

//                Icon(
//                    painter = painterResource(id = R.drawable.back_white),
//                    contentDescription = "Note Icon",
//                    tint = Color.White
//                )

                Spacer(modifier = Modifier.width(6.dp))

                Text(
                    text = "Ghi chú",
                    fontSize = 28.sp,
                    color = Color.White,

                )
            }
        }
    }
}
