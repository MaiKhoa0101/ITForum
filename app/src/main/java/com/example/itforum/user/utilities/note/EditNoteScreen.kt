package com.example.itforum.utilities.note
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.MaterialTheme
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
fun NoteEditScreen(
    initialTitle: String = "",
    initialContent: String = "",
    onSave: (String, String) -> Unit,
    onCancel: () -> Unit
) {
    var title by remember { mutableStateOf(initialTitle) }
    var content by remember { mutableStateOf(initialContent) }
    Column(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp) // 👈 chỉnh chiều cao tại đây
                .background(MaterialTheme.colorScheme.primaryContainer)
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.CenterStart
        ){
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 12.dp, top = 12.dp), // dịch các phần tử xuống
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
            TextButton(onClick = { onSave(title, content) }) {
                Text("Lưu", fontSize = 20.sp)
            }
        }

        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Nhập tiêu đề",fontSize = 18.sp) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            OutlinedTextField(
                value = content,
                onValueChange = { content = it },
                label = { Text("Nhập nội dung",fontSize = 18.sp) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .heightIn(min = 200.dp, max = 550.dp), // hoặc bỏ height cố định
                maxLines = Int.MAX_VALUE
            )
        }
    }

}


//@Composable
//fun NoteEditScreen(
//    onSave: (String, String) -> Unit,
//    onCancel: () -> Unit,
//    initialTitle: String = "",
//    initialContent: String = ""
//) {
//    var title by remember { mutableStateOf(initialTitle) }
//    var content by remember { mutableStateOf(initialContent) }
//
//    Column(modifier = Modifier.fillMaxSize()) {
//        TopAppBar(
//            modifier = Modifier.height(100.dp),
//            title = {
//                Row(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(top = 15.dp),
//                    verticalAlignment = Alignment.CenterVertically,
//                    horizontalArrangement = Arrangement.Center
//                ) {
//                    Image(
//                        painter = painterResource(id = R.drawable.note),
//                        contentDescription = "note",
//                        modifier = Modifier
//                            .size(50.dp)
//                            .padding(end = 8.dp)
//                    )
//                    Text(
//                        "Ghi chú",
//                        fontSize = 30.sp,
//                        color = Color.White,
//                        modifier = Modifier.padding(bottom = 4.dp)
//                    )
//                }
//            },
//            backgroundColor = Color(0xFF03A9F4)
//        )
//
//        Column {
//            Row(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(horizontal = 16.dp, vertical = 8.dp),
//                horizontalArrangement = Arrangement.End
//            ) {
//                TextButton(onClick = onCancel) {
//                    Text("Hủy", color = Color.Black, fontSize = 18.sp)
//                }
//                Spacer(modifier = Modifier.width(8.dp))
//                TextButton(onClick = {
//                    onSave(title, content)
//                }) {
//                    Text("Lưu", color = Color.Black, fontSize = 18.sp)
//                }
//            }
//
//            Column(modifier = Modifier.padding(16.dp)) {
//                OutlinedTextField(
//                    value = title,
//                    onValueChange = { title = it },
//                    label = { Text("Nhập tiêu đề") },
//                    modifier = Modifier.fillMaxWidth()
//                )
//                Spacer(modifier = Modifier.height(16.dp))
//                OutlinedTextField(
//                    value = content,
//                    onValueChange = { content = it },
//                    label = { Text("Nhập nội dung") },
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .height(500.dp),
//                    maxLines = Int.MAX_VALUE
//                )
//            }
//        }
//    }
//}

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

