package com.example.itforum.admin.components


import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
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
    filterOptions: Map<String,List<String>> = mapOf(),
    modifier: Modifier,
    addComposed: @Composable () -> Unit = {},
    filterField: (String, String, Any) -> Any = { it1, it2, it3-> it3 },
    searchTable: @Composable (String) -> Any,
    table: @Composable (Any) -> Unit = {}
) {
    var searchText by remember { mutableStateOf("") }
    val dateDialogStateStart = rememberMaterialDialogState()
    val dateDialogStateEnd = rememberMaterialDialogState()
    var startDate by remember { mutableStateOf<LocalDate?>(null) }
    var endDate by remember { mutableStateOf<LocalDate?>(null) }
    var showDateFilters by remember { mutableStateOf(false) }

    var selectedFilterField by remember { mutableStateOf("Chọn cột") }
    var expandedFilterField by remember { mutableStateOf(false) }

    var selectedFilterValue by remember { mutableStateOf("Tất cả") }
    var expandedFilterValue by remember { mutableStateOf(false) }
    var dataFiltered: Any
    LaunchedEffect(selectedFilterField) {
        selectedFilterValue = "Tất cả"
    }
    Column(modifier = modifier) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = title,
                modifier = Modifier.padding(start = 20.dp, top = 20.dp),
                color = Color.Black,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(14.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                TextField(
                    value = searchText,
                    onValueChange = { searchText = it },
                    modifier = Modifier.width(270.dp),
                    placeholder = {
                        Row {
                            Icon(Icons.Default.Search, contentDescription = "search")
                            Text("Tìm kiếm")
                        }
                    }
                )

                Spacer(modifier = Modifier.width(10.dp))
                addComposed()
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
            Row(
                modifier = Modifier.fillMaxWidth(),
            ) {
                Box(
                    modifier = Modifier.size(width = 150.dp, height = 50.dp).background(Color(0xFF7BD88F)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "Tổng số: $itemCount")
                }
                Spacer(modifier = Modifier.weight(1f))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Text(
                        text = "Lọc: ",
                        modifier = Modifier.padding(5.dp),
                        fontWeight = FontWeight.Bold,
                    )
                    Box {
                        Button(
                            onClick = { expandedFilterField = true },
                            shape = RoundedCornerShape(0.dp), // Bo góc viền
                            contentPadding = PaddingValues(5.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Transparent,
                                contentColor = Color.White
                            ),
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(
                                    text = "$selectedFilterField",
                                )
                                Icon(
                                    imageVector = Icons.Default.ExpandMore,
                                    contentDescription = "",
                                    modifier = Modifier.size(15.dp),
                                )
                            }

                        }
                        DropdownMenu(expanded = expandedFilterField, onDismissRequest = { expandedFilterField = false }) {
                            DropdownMenuItem(text = { Text("Chọn cột") }, onClick = {
                                selectedFilterField = "Chọn cột"
                                expandedFilterField = false

                            })
                            filterOptions.keys.forEach { option ->
                                DropdownMenuItem(text = { Text(option) }, onClick = {
                                    selectedFilterField = option
                                    expandedFilterField = false
                                })
                            }
                        }
                    }
                    Box {
                        Button(
                            onClick = { expandedFilterValue = true },
                            shape = RoundedCornerShape(0.dp), // Bo góc viền
                            contentPadding = PaddingValues(0.dp),
                            modifier = Modifier.padding(end = 5.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Transparent,
                                contentColor = Color.White
                            ),
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(
                                    text = "$selectedFilterValue",
                                )
                                Icon(
                                    imageVector = Icons.Default.ExpandMore,
                                    contentDescription = "",
                                    modifier = Modifier.size(15.dp),
                                )
                            }

                        }
                        DropdownMenu(expanded = expandedFilterValue, onDismissRequest = { expandedFilterValue = false }) {
                            DropdownMenuItem(text = { Text("Tất cả") }, onClick = {
                                selectedFilterValue = "Tất cả"
                                expandedFilterValue = false

                            })
                            filterOptions[selectedFilterField]?.forEach { option ->
                                DropdownMenuItem(text = { Text(option) }, onClick = {
                                    selectedFilterValue = option
                                    expandedFilterValue = false
                                })
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            dataFiltered = searchTable(searchText)
            dataFiltered = filterField(selectedFilterField, selectedFilterValue, dataFiltered)
            table(dataFiltered)
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
