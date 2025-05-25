package com.example.itforum.utilities.note
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.itforum.R
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

@Composable
fun NoteEditScreen(onSave: (String, String) -> Unit,
                   onCancel: () -> Unit,
                   initialTitle: String = "",
                   initialContent: String = "") {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize()) {
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
                        color = Color.White,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                }
            },

            backgroundColor = Color(0xFF03A9F4)

        )
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onCancel) {
                    Text("Hủy", color = Color.Black, fontSize = 18.sp)
                }
                Spacer(modifier = Modifier.width(8.dp))
                TextButton(onClick = {
                    onSave(title, content)
                }) {
                    Text("Lưu", color = Color.Black, fontSize = 18.sp)
                }
            }
            Column(modifier = Modifier.padding(16.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Nhập tiêu đề") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("Nhập nội dung") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(500.dp),
                    maxLines = Int.MAX_VALUE
                )
            }
        }
//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(horizontal = 16.dp, vertical = 8.dp),
//            horizontalArrangement = Arrangement.End
//        ) {
//            TextButton(onClick = { /* Hủy */ }) {
//                Text("Hủy", color = Color.Black, fontSize = 18.sp)
//            }
//            Spacer(modifier = Modifier.width(8.dp))
//            TextButton(onClick = { /* Lưu */ }) {
//                Text("Lưu", color = Color.Black, fontSize = 18.sp)
//            }
//        }
//        Column(modifier = Modifier.padding(16.dp)) {
//
//            OutlinedTextField(
//                value = "",
//                onValueChange = {},
//                label = { Text("Nhập tiêu đề") },
//                modifier = Modifier.fillMaxWidth()
//            )
//            Spacer(modifier = Modifier.height(16.dp))
//            OutlinedTextField(
//                value = "",
//                onValueChange = {},
//                label = { Text("Nhập nội dung") },
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .height(500.dp),
//                maxLines = Int.MAX_VALUE
//            )
//        }
    }
}
