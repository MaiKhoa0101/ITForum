package com.example.itforum.user.notification

import android.content.SharedPreferences
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Divider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavHostController
import com.example.itforum.user.notification.viewmodel.NotificationViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.example.itforum.user.modelData.response.Notification
import com.google.firebase.messaging.FirebaseMessaging


@Composable
fun NotificationPage(modifier: Modifier, sharedPreferences: SharedPreferences, navHostController: NavHostController ){

    val notificationViewModel: NotificationViewModel = viewModel(factory = viewModelFactory {
        initializer { NotificationViewModel(sharedPreferences) }
    })
    val notifications by notificationViewModel.notificationList.collectAsState()

    val userId = sharedPreferences.getString("userId", null)
    LaunchedEffect(Unit) {
        if (userId != null) {
            notificationViewModel.getNotification(userId = userId)
        }
    }

    Column (
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primaryContainer)
    ) {
        Spacer(modifier = Modifier.height(30.dp))
        Text(
            text = "Thông báo",
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(horizontal = 13.dp, vertical = 10.dp)
                .fillMaxWidth(),
            textAlign = TextAlign.Center
        )

        Column(
            modifier = modifier
                .fillMaxSize()
                .background( MaterialTheme.colorScheme.background)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize(),
            ) {
                item {
                    Text(
                        text = "Mới",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        modifier = Modifier.padding(start = 15.dp, top = 15.dp),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }

                items(notifications) { notify ->
                    NotifyChild(notify, onClick = {
                        notificationViewModel.readNotification(id = notify.id)
                        println("notify id: ${notify.id}" + "notify postId: ${notify.postId}")
                        navHostController.navigate("detail_post/${notify.postId}")
                    })
                }

                item {
                    Text(
                        text = "Trước đó",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        modifier = Modifier.padding(start = 15.dp, top = 10.dp),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }

//                items(ListNotiOld) { notify ->
//                    NotifyChild(notify, navHostController)
//                }
            }
        }
    }
}

@Composable
fun NotifyChild(
    notify: Notification,
    onClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Divider(thickness = 2.dp)
        Row(
            modifier = Modifier
                .background(
                    if (!notify.isRead) {
                        MaterialTheme.colorScheme.tertiaryContainer
                    }
                    else{
                        MaterialTheme.colorScheme.background
                    }
                )
                .padding(vertical = 15.dp, horizontal = 12.dp)
                .fillMaxWidth()
                .clickable(){
                    onClick()
                },
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = "Avatar tài khoản",
                modifier = Modifier.size(45.dp)
            )
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Text(
                    text = notify.title,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = FontWeight.Bold
                )
                if (notify.content != "")
                    Text(
                        text = notify.content,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontSize = 12.sp
                    )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = "",
                        modifier = Modifier.size(12.dp),
                        tint =  MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = notify.createdAt,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }
        }
    }
}
fun subscribeToAppTopic() {
    FirebaseMessaging.getInstance().subscribeToTopic("all_users")
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d("FCM", "Subscribed to all_users topic")
            } else {
                Log.e("FCM", "Topic subscription failed", task.exception)
            }
        }
}