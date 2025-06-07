package com.example.itforum.utilities.note

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.itforum.R
import com.example.itforum.user.utilities.note.NoteEntity
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun NoteEditScreen(
    initialTitle: String = "",
    initialContent: String = "",
    userId: String,
    noteId: Int = 0,
    onSave: (NoteEntity) -> Unit,
    onCancel: () -> Unit
) {
    var title by remember { mutableStateOf(initialTitle) }
    var content by remember { mutableStateOf(initialContent) }

    Column(modifier = Modifier.fillMaxSize()) {

        // Top bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .background(MaterialTheme.colorScheme.primaryContainer)
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 12.dp, top = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Spacer(modifier = Modifier.width(6.dp))
                Icon(
                    painter = painterResource(id = R.drawable.note),
                    contentDescription = "Note Icon",
                    tint = Color.White,
                    modifier = Modifier.size(38.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Ghi chú",
                    color = Color.White,
                    fontSize = 28.sp
                )
            }
        }

        // Buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(onClick = onCancel) {
                Text("Hủy", fontSize = 20.sp)
            }
            Spacer(modifier = Modifier.width(8.dp))
            TextButton(onClick = {
                val date = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
                val newNote = NoteEntity(
                    id = noteId,
                    userId = userId,
                    title = title,
                    content = content,
                    date = date
                )
                onSave(newNote)
            }) {
                Text("Lưu", fontSize = 20.sp)
            }
        }

        // Title input
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Nhập tiêu đề", fontSize = 18.sp) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )

        // Content input
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            OutlinedTextField(
                value = content,
                onValueChange = { content = it },
                label = { Text("Nhập nội dung", fontSize = 18.sp) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .heightIn(min = 200.dp, max = 550.dp),
                maxLines = Int.MAX_VALUE
            )
        }
    }
}
