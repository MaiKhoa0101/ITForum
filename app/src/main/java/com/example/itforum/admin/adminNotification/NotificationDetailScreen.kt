package com.example.itforum.admin.adminNotification

import android.content.SharedPreferences
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
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
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavHostController
import com.example.itforum.R
import com.example.itforum.admin.components.convertDateTime
import com.example.itforum.user.notification.viewmodel.NotificationViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationDetailScreen(
    navHostController: NavHostController,
    sharedPreferences: SharedPreferences,
    notificationId: String
) {
    val notificationViewModel: NotificationViewModel = viewModel(factory = viewModelFactory {
        initializer { NotificationViewModel(sharedPreferences) }
    })
    val notification by notificationViewModel.notification.collectAsState()
    LaunchedEffect(Unit) {
        notificationViewModel.getNotificationById(notificationId)
    }
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = {
                    Box(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Chi tiết thông báo",
                            fontSize = 23.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                navigationIcon = {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White,
                        modifier = Modifier
                            .size(50.dp)
                            .clickable {
                                navHostController.popBackStack()
                            }
                    )
                },
                actions = {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Back",
                        tint = Color.Red,
                        modifier = Modifier
                            .size(40.dp)
                            .clickable {}
                    )
                },
                scrollBehavior = scrollBehavior
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.padding(innerPadding)
        ) {
            item { notification?.let { TitleWithCard(title = "Tiêu đề", content = it.title) } }
            item { notification?.let { TitleWithCard(title = "Nội dung", content = it.content) } }
            item { notification?.postId?.let { TitleWithCard(title = "Bài viết", content = it) } }
            item { TitleWithCard(title = "Thời gian gửi", content = notification?.let {
                convertDateTime(
                    it.createdAt)
            }.toString()) }
            item { TitleWithCard(title = "Người nhận", content = notification?.userReceiveNotificationId?:"Tất cả") }
            item { SectionButton({},{}) }
        }
    }
}

@Composable
fun TitleWithCard(
    title: String,
    content: String,
    color: Color = Color.Black
){
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(15.dp),
        verticalArrangement = Arrangement.spacedBy(7.dp)
    ) {
        Text(
            text = title,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFC0FCFF)
            )
        ) {
            Text(
                text = content,
                fontSize = 18.sp,
                color = color,
                modifier = Modifier.padding(15.dp)
            )
        }
    }
}


@Composable
fun SectionButton(
    saveClick: () -> Unit,
    sendClick: () -> Unit
){
    var showDialogSend by remember { mutableStateOf(false) }
    var showDialogSave by remember { mutableStateOf(false) }
    ConfirmDialog(
        showDialog = showDialogSend,
        title = "Gửi thành công",
        text = "Thông báo gửi thành công đến tất cả người dùng.",
        color = Color(0xFF1BB984),
        icon = Icons.Default.CloudDone,
        nameDismiss = "Chi tiết",
        nameConfirm = "Trang chủ",
        onDismiss = { showDialogSend = false },
        onConfirm = {
            showDialogSend = false
        },
    )
    ConfirmDialog(
        showDialog = showDialogSave,
        title = "Lưu nháp thành công",
        text = "Thông báo của bạn đã được lưu dưới dạng bản nháp.",
        color = Color(0xFF13B6D2),
        icon = Icons.Default.Save,
        nameDismiss = "Chỉnh sửa tiếp",
        nameConfirm = "Trang chủ",
        onDismiss = { showDialogSave = false },
        onConfirm = {
            showDialogSave = false
        },
    )
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(15.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Button(
            onClick = {
                saveClick()
                showDialogSave = true
                      },
            contentPadding = PaddingValues(0.dp),
            modifier = Modifier
                .width(100.dp)
                .border(
                width = 2.dp,
                color = Color(0xFF0FAAFF),
                shape = RoundedCornerShape(8.dp)
            ),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White
            )
        ) {
            Text(
                text = "Lưu",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }
        Button(
            onClick = {
                showDialogSend = true
                sendClick()
            },
            contentPadding = PaddingValues(12.dp),
            modifier = Modifier,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF00AEFF)
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = "Gửi ngay",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

@Composable
fun ConfirmDialog(
    showDialog: Boolean,
    title: String,
    text: String,
    color: Color,
    icon: ImageVector,
    nameDismiss: String,
    nameConfirm: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    if (!showDialog) return

    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = icon,
                contentDescription = "Icon",
                modifier = Modifier.size(30.dp),
                tint = color
            )
        },
        title = {
            Text(
                text = title,
                fontSize = 23.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
        },
        text = {
            Text(
                text = text,
                fontSize = 20.sp,
                color = color.copy(alpha = 0.8f)
            )
        },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = color
                )
            ) {
                Text(
                    text = nameConfirm,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = nameDismiss,
                    fontWeight = FontWeight.Bold,
                    color = color
                )
            }
        },
        containerColor = color.copy(alpha = 0.3f).compositeOver(Color.White)
    )
}