package com.example.itforum.utilities.note

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
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
import androidx.compose.material3.Card

@Composable
fun NotesListScreen(
    viewModel: NoteViewModel,
    notes: List<NoteEntity>,
    onDelete: (NoteEntity) -> Unit,
    onBackToHome: () -> Unit
) {
    var editingNote by remember { mutableStateOf<NoteEntity?>(null) }
    var isEditing by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    val userId = sharedPreferences.getString("userId", "") ?: ""

    if (isEditing && editingNote != null) {
        NoteEditScreen(
            initialTitle = editingNote!!.title,
            initialContent = editingNote!!.content,
            userId = userId,
            noteId = editingNote!!.id,
            onSave = { note ->
                if (note.id == 0) {
                    viewModel.addNote(note)
                } else {
                    viewModel.updateNote(note)
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
                        editingNote = NoteEntity(0, userId, "", "", date)
                        isEditing = true
                    },
                    backgroundColor = Color(0xFF00AEFF)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Thêm", modifier = Modifier.size(45.dp))
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(MaterialTheme.colorScheme.background)
            ) {
                if (notes.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Chưa có ghi chú nào.",color = MaterialTheme.colorScheme.onSecondaryContainer)
                    }
                } else {
                    LazyColumn(modifier = Modifier.fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)) {
                        items(notes) { note ->
                            var expanded by remember { mutableStateOf(false) }
                            val openEdit = {
                                editingNote = note
                                isEditing = true
                            }


                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                                    .clickable { openEdit() },
                                shape = RoundedCornerShape(12.dp),

                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                                )
                            )

                            {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(verticalAlignment = Alignment.Top) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(note.title, fontWeight = FontWeight.Bold, fontSize = 20.sp,color = MaterialTheme.colorScheme.onBackground)
                                            Spacer(modifier = Modifier.height(6.dp))
                                            Text(note.content, maxLines = 2, overflow = TextOverflow.Ellipsis, fontSize = 16.sp,color = MaterialTheme.colorScheme.onBackground)
                                            Spacer(modifier = Modifier.height(6.dp))
                                            Text(note.date, fontSize = 12.sp,color = MaterialTheme.colorScheme.onBackground)
                                        }

                                        Box {
                                            IconButton(onClick = { expanded = true }) {
                                                Icon(Icons.Default.MoreVert, contentDescription = "Tùy chọn")
                                            }

                                            DropdownMenu(
                                                expanded = expanded,
                                                onDismissRequest = { expanded = false },
                                                modifier = Modifier.background(MaterialTheme.colorScheme.background)
                                            ) {
                                                DropdownMenuItem(onClick = {
                                                    expanded = false
                                                    editingNote = note
                                                    isEditing = true
                                                }) {
                                                    Text("Chỉnh sửa", color = MaterialTheme.colorScheme.onBackground)
                                                }
                                                DropdownMenuItem(onClick = {
                                                    expanded = false
                                                    onDelete(note)
                                                }) {
                                                    Text("Xoá", color = MaterialTheme.colorScheme.onBackground)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
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
                .padding(horizontal = 16.dp),
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


