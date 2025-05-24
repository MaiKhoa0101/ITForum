package com.example.itforum.utilities.note

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.itforum.R
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.navigation.NavController

@Composable
fun NotesListScreen(

    notes: List<Note>,
    onAddNote: () -> Unit,
    onEdit: (Note) -> Unit,    // thêm hàm xử lý chỉnh sửa
    onDelete: (Note) -> Unit,
    onBackToHome: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column {
            TopAppBar(
                modifier = Modifier.height(100.dp),
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 15.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.note),
                            contentDescription = "note",
                            modifier = Modifier
                                .size(50.dp)
                                .padding(end = 8.dp)
                        )
                        Text(
                            "Ghi chú",
                            fontSize = 30.sp,
                            color = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = {  onBackToHome()}) {
                        Image(
                            painter = painterResource(id = R.drawable.back_white),
                            contentDescription = "back",
                            modifier = Modifier
                                .size(40.dp)
                                .padding(start = 4.dp, top = 15.dp, end = 8.dp, bottom = 2.dp)
                        )
                    }
                },
                backgroundColor = Color(0xFF03A9F4)
            )

            LazyColumn {
                items(notes) { note ->
                    var expanded by remember { mutableStateOf(false) } // trạng thái menu dropdown

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(2.dp)
                            .background(Color(0xFFF0F0F0))
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(
                            modifier = Modifier
                                .weight(1f) // chiếm phần còn lại của hàng, trừ chỗ nút 3 chấm
                                .padding(end = 8.dp) // cách lề phải một chút
                            ){
                                Text(note.title, fontWeight = FontWeight.Bold, fontSize=24.sp, color = MaterialTheme.colorScheme.onBackground)
                                Text(
                                    note.content,
                                    color = MaterialTheme.colorScheme.onBackground,
                                    maxLines = 3,
                                    overflow = TextOverflow.Ellipsis,
                                    fontSize = 18.sp,
                                    modifier = Modifier
                                        .fillMaxWidth() // Giúp phần end padding có hiệu lực
                                        .padding(top = 4.dp)
                                )

                                Text(note.date, color = MaterialTheme.colorScheme.onBackground, fontSize=13.sp)
                            }
                            Box {
                                IconButton(onClick = { expanded = true }) {
                                    Icon(Icons.Default.MoreVert, contentDescription = "Menu")
                                }
                                DropdownMenu(
                                    expanded = expanded,
                                    onDismissRequest = { expanded = false }
                                ) {
                                    DropdownMenuItem(onClick = {
                                        expanded = false
                                        onEdit(note)
                                    }) {
                                        Text("Chỉnh sửa")
                                    }
                                    DropdownMenuItem(onClick = {
                                        expanded = false
                                        onDelete(note)
                                    }) {
                                        Text("Xóa")
                                    }
                                }
                            }
                        }
                    }
                }
            }

        }

        FloatingActionButton(
            onClick = onAddNote,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
                .size(60.dp),

            backgroundColor = Color.White,
            contentColor = Color.Black
        ) {
            Image(
                painter = painterResource(id = R.drawable.them),
                contentDescription = "them",
                modifier = Modifier.size(50.dp)
            )
        }
    }
}
