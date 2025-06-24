package com.example.itforum.admin.adminNotification

import android.content.SharedPreferences
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavHostController
import androidx.room.Room
import com.example.itforum.user.complaint.AddImage
import com.example.itforum.user.complaint.SuccessDialog
import com.example.itforum.user.complaint.TitleChild
import com.example.itforum.user.effect.model.UiState
import com.example.itforum.user.modelData.request.NewsRequest
import com.example.itforum.user.news.NewsDatabase
import com.example.itforum.user.news.viewmodel.NewsViewModel
import com.example.itforum.user.news.viewmodel.NewsViewModelFactory
import com.example.itforum.user.post.IconWithText
import com.example.itforum.user.post.TopPost
import com.example.itforum.user.post.WritePost
import com.example.itforum.user.profile.viewmodel.UserViewModel

@Composable
fun CreateNotificationScreen(
    navHostController: NavHostController,
    sharedPreferences: SharedPreferences,
    modifier: Modifier
){
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        var title by remember { mutableStateOf("") }
        var content by remember { mutableStateOf("") }
        var sendOption by remember { mutableStateOf("") }
        var userViewModel: UserViewModel = viewModel(factory = viewModelFactory {
            initializer { UserViewModel(sharedPreferences) }
        })

        val userInfo by userViewModel.user.collectAsState()
        var showSuccessDialog by remember { mutableStateOf(false) }
        var enable by remember { mutableStateOf<Boolean>(true) }
        val menuOptions = listOf("Tất cả", "Chủ đề 1", "Chủ đề 2", "Chủ đề 3")

        LaunchedEffect(Unit) {
            userViewModel.getUser()
        }
        // UI hiển thị
        if (showSuccessDialog) {
            SuccessDialog(
                message = "Tạo thông báo thành công!",
                onDismiss = {
                    showSuccessDialog = false
                    navHostController.navigate("NotificationManager")
                }
            )
        }
        var isErrorContent by remember { mutableStateOf(false) }
        var isErrorTitle by remember { mutableStateOf(false) }
        LaunchedEffect(title, content) {
            isErrorContent = false
            isErrorTitle = false
        }
        LazyColumn (
            modifier = Modifier.fillMaxSize()
        ) {
            stickyHeader {
                TopPost(
                    title = "Tạo thông báo",
                    nameButton = "Thêm",
                    navHostController = navHostController,
                    enable = enable,
                    onClickPush = {
                        isErrorTitle = title.trim().isEmpty()
                        isErrorContent = content.trim().isEmpty()
                        if (!isErrorTitle && !isErrorContent) {
                            showSuccessDialog = true
                        }
                    }
                )
            }
            item {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White),
                ) {
                    userInfo?.let { IconWithText(avatar = it.avatar, name = it.name) }
                    TitleChild(isErrorTitle){ title=it }
                    WritePost(isErrorContent){input ->
                        content = input
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth()
                            .background(Color(0xFFE3F2FD)),
                    ) {
                        Text (
                            text = "Gửi đến: ",
                            modifier = Modifier.padding(12.dp)
                        )
                        SendOptions(
                            menuOptions = menuOptions,
                            onOptionSelected = { sendOption = it }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SendOptions(
    menuOptions: List<String>,
    onOptionSelected: (String) -> Unit
){
    var expanded by remember { mutableStateOf(false) }
    var selectedOption by remember { mutableStateOf("Tất cả") }

    Box {
        Button(
            onClick = { expanded = true },
            shape = RoundedCornerShape(0.dp), // Bo góc viền
            contentPadding = PaddingValues(0.dp),
            modifier = Modifier.padding(end = 5.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent,
            ),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = selectedOption,
                    color = Color.Black
                )
                Icon(
                    imageVector = Icons.Default.ExpandMore,
                    contentDescription = "",
                    modifier = Modifier.size(15.dp),
                )
            }

        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            DropdownMenuItem(text = { Text("Tất cả") }, onClick = {
                selectedOption = "Tất cả"
                expanded = false
                onOptionSelected(selectedOption)
            })
            menuOptions.forEach { option ->
                DropdownMenuItem(text = { Text(option) }, onClick = {
                    selectedOption = option
                    expanded = false
                    onOptionSelected(selectedOption)
                })
            }
        }
    }
}