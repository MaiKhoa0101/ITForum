package com.example.itforum.admin.components


import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import java.time.LocalDate

@Composable
fun AdminScreenLayout(
    title: String,
    itemCount: Int,
    content: @Composable (String, LocalDate?, LocalDate?) -> Unit
) {
    var searchText by remember { mutableStateOf("") }
    val dateDialogStateStart = rememberMaterialDialogState()
    val dateDialogStateEnd = rememberMaterialDialogState()
    var startDate by remember { mutableStateOf<LocalDate?>(null) }
    var endDate by remember { mutableStateOf<LocalDate?>(null) }
    var showDateFilters by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth().background(Color.White)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(69.dp)
                .background(Color(0xFF00AEFF))
        ) {
            Row(
                modifier = Modifier.fillMaxSize().padding(start = 25.dp, end = 25.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Icon(Icons.Default.Menu, contentDescription = "Menu")
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "xin chao user", color = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(Icons.Default.AccountCircle, contentDescription = "User")
                }
            }
        }
        Spacer(modifier = Modifier.height(17.dp))
        Column(modifier = Modifier.padding(horizontal = 25.dp)) {
            Text(text = title, fontSize = 20.sp, color = Color.Black)
            Spacer(modifier = Modifier.height(14.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                TextField(
                    value = searchText,
                    onValueChange = { searchText = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Tìm kiếm") }
                )
                IconButton(onClick = { showDateFilters = !showDateFilters }) {
                    Icon(Icons.Default.Search, contentDescription = "search", tint = Color.Black)
                }
                Spacer(modifier = Modifier.width(16.dp))
                Button(onClick = { showDateFilters = !showDateFilters }) {
                    Text("Lọc")
                }
            }

            if (showDateFilters) {
                Spacer(modifier = Modifier.height(10.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFE3F2FD))
                        .padding(12.dp)
                        .border(1.dp, Color(0xFF90CAF9), shape = RoundedCornerShape(8.dp))
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 8.dp)) {
                        Text("ngày bắt đầu:", color = Color(0xFF0D47A1), modifier = Modifier.width(130.dp))
                        Button(onClick = { dateDialogStateStart.show() }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF90CAF9))) {
                            Text(startDate?.toString() ?: "Chọn", color = Color.White)
                        }
                    }
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 8.dp)) {
                        Text("ngày kết thúc:", color = Color(0xFF0D47A1), modifier = Modifier.width(130.dp))
                        Button(onClick = { dateDialogStateEnd.show() }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF90CAF9))) {
                            Text(endDate?.toString() ?: "Chọn", color = Color.White)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
            Box(
                modifier = Modifier.size(width = 200.dp, height = 50.dp).background(Color(0xFF7BD88F)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "Tổng số: $itemCount")
            }
            Spacer(modifier = Modifier.height(16.dp))

            content(searchText, startDate, endDate)
        }

        Spacer(modifier = Modifier.height(20.dp))
        Box(
            modifier = Modifier.fillMaxWidth().height(60.dp).background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            Text("© 2025 IT Forum Admin", color = Color.Gray, fontSize = 14.sp)
        }
    }

    MaterialDialog(dialogState = dateDialogStateStart, buttons = {
        positiveButton("OK")
        negativeButton("Hủy")
    }) {
        datepicker(initialDate = startDate ?: LocalDate.now(), title = "ngày bắt đầu") {
            startDate = it
        }
    }

    MaterialDialog(dialogState = dateDialogStateEnd, buttons = {
        positiveButton("OK")
        negativeButton("Hủy")
    }) {
        datepicker(initialDate = endDate ?: LocalDate.now(), title = "ngày kết thúc") {
            endDate = it
        }
    }
}
