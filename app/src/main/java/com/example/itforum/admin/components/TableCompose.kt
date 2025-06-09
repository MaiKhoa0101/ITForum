package com.example.itforum.admin.components

import android.content.SharedPreferences
import androidx.compose.foundation.background
import androidx.compose.material3.Icon
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.itforum.user.post.IconWithText
import com.example.itforum.user.post.icontext
import com.example.itforum.user.profile.viewmodel.UserViewModel

interface TableRowConvertible {
    fun toTableRow(): List<String>
}

@Composable
fun TableData(
    headers: List<String>,
    rows: List<List<String>>,
    menuOptions: List<icontext>,
    sharedPreferences: SharedPreferences,
    onClickOption: (String) -> Unit = {}
) {
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    val maxTableHeight = screenHeight * 0.6f
    var expandedIndex by remember { mutableStateOf(-1) }
    var rowState: Int? = null
    var rowUser: Int? = null
    var userViewModel: UserViewModel = viewModel(factory = viewModelFactory {
        initializer { UserViewModel(sharedPreferences) }
    })
    Box(modifier = Modifier
        .fillMaxWidth()
        .border(1.dp, Color.Gray)
        .heightIn(max = maxTableHeight)) {
        val horizontalScrollState = rememberScrollState()

        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            item {
                Row(modifier = Modifier
                    .background(Color(0xFF2B544F))
                    .horizontalScroll(horizontalScrollState)) {
                    headers.forEachIndexed {i, header ->
                        Box(modifier = Modifier
                            .width(120.dp)
                            .padding(8.dp)) {
                            Text(text = header, color = Color.White, modifier = Modifier
                                .padding(4.dp)
                                .widthIn(min = 50.dp, max = 100.dp),textAlign = TextAlign.Center)
                            if(header == "Trạng thái") rowState = i
                            if(header == "Người dùng") rowUser = i
                        }
                    }
                }
            }
            itemsIndexed(rows) { index, row ->
                val backgroundColor = if (index % 2 == 0) Color.White else Color(0xFFD9D9D9)
                Row(modifier = Modifier
                    .horizontalScroll(horizontalScrollState)
                    .background(backgroundColor), verticalAlignment = Alignment.CenterVertically) {
                    repeat(row.size) { i ->
                        Box(modifier = Modifier
                            .width(120.dp)
                            .background(backgroundColor)) {
                            var color: Color = Color.Black
                            var text = row[i]
                            if(i == rowState) {
                                when(row[i]){
                                    "pending" -> {
                                        color = Color(0xFFFA8C25)
                                        text = "Chưa xử lý"
                                    }
                                    "approved" -> {
                                        color = Color(0xFF00BB00)
                                        text = "Đã xử lý"
                                    }
                                    "rejected" -> {
                                        color = Color(0xFFFF0004)
                                        text = "Từ chối"
                                    }
                                }
                            }
                            else if (i == rowUser){
                                LaunchedEffect(Unit) {
                                    userViewModel.getUser(row[i])
                                }
                                val user by userViewModel.user.collectAsState()
                                text = user?.name ?: "Đang tải..."
                            }

                            Text(
                                text = text,
                                color = color,
                                modifier = Modifier
                                    .padding(8.dp)
                                    .widthIn(min = 50.dp, max = 100.dp),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                    Box(modifier = Modifier
                        .width(100.dp)
                        .background(backgroundColor),contentAlignment = Alignment.Center) {
                        Box(modifier = Modifier
                            .size(32.dp, 24.dp)
                            .background(Color(0xFFB1D05D), shape = RoundedCornerShape(6.dp))
                            .border(1.dp, Color.Black, shape = RoundedCornerShape(6.dp)), contentAlignment = Alignment.Center) {
                            IconButton(onClick = { expandedIndex = index }) {
                                Icon(Icons.Default.MoreHoriz, "Tùy chỉnh", tint = Color.Black)
                            }
                            DropdownMenu(expanded = expandedIndex == index, onDismissRequest = { expandedIndex = -1 }, modifier = Modifier.width(170.dp)) {
                                menuOptions.forEach(){item->
                                    DropdownMenuItem(
                                        text = {
                                            IconWithText("https://photo.znews.vn/w660/Uploaded/mdf_eioxrd/2021_07_06/2.jpg",item.text,25.dp,
                                                TextStyle(fontSize = 14.sp),
                                                modifier = Modifier.padding(horizontal = 0.dp, vertical = 0.dp)
                                            )
                                       },
                                        onClick = {
                                            expandedIndex = -1
                                            val accountId = row[0]
                                            item.onClick(accountId)
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
                Divider(thickness = 1.dp, color = Color.Gray)
            }
        }
    }
}

fun convertToTableRows(items: List<TableRowConvertible>): List<List<String>> {
    return items.map { it.toTableRow() }
}
