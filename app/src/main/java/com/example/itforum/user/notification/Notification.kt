package com.example.itforum.user.notification

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
import androidx.navigation.NavHostController


data class Notify(
    val icon: ImageVector,
    val title: String,
    val content: String = "",
    val time: String,
)

@Composable
fun NotificationPage(modifier: Modifier, navHostController: NavHostController){
    var ListNotiNew: List<Notify> = listOf(
        Notify(Icons.Default.AccountCircle,"Zepenllin đã bình luận vào bài viết của bạn", "code sai chính tả ní ơi", "2 phút"),
        Notify(Icons.Default.AccountCircle,"Khoa đã thích bài viết của bạn", time = "5 phút"),
        Notify(Icons.Default.AccountCircle,"Zepenllin đã bình luận vào bài viết của bạn", "cái này bạn phải code là a+b+c+c+cfddfdfdfdfdffdf...", "5 phút"),
        Notify(Icons.Default.AccountCircle,"Khoa đã thích bài viết của bạn", time = "5 phút"),
        Notify(Icons.Default.AccountCircle,"Khoa đã thích bài viết của bạn", time = "5 phút"),
        Notify(Icons.Default.AccountCircle,"Khoa đã thích bài viết của bạn", time = "5 phút"),
        Notify(Icons.Default.AccountCircle,"Khoa đã thích bài viết của bạn", time = "5 phút"),
        Notify(Icons.Default.AccountCircle,"Khoa đã thích bài viết của bạn", time = "5 phút"),
        Notify(Icons.Default.AccountCircle,"Khoa đã thích bài viết của bạn", time = "5 phút"),
        Notify(Icons.Default.AccountCircle,"Khoa đã thích bài viết của bạn", time = "5 phút"),
        Notify(Icons.Default.AccountCircle,"Khoa đã thích bài viết của bạn", time = "5 phút"),
        Notify(Icons.Default.AccountCircle,"Khoa đã thích bài viết của bạn", time = "5 phút"),
        Notify(Icons.Default.AccountCircle,"Khoa đã thích bài viết của bạn", time = "5 phút"),
        Notify(Icons.Default.AccountCircle,"Khoa đã thích bài viết của bạn", time = "5 phút"),
        Notify(Icons.Default.AccountCircle,"Khoa đã thích bài viết của bạn", time = "5 phút"),
        Notify(Icons.Default.AccountCircle,"Khoa đã thích bài viết của bạn", time = "5 phút"),
        Notify(Icons.Default.AccountCircle,"Khoa đã thích bài viết của bạn", time = "5 phút"),
        Notify(Icons.Default.AccountCircle,"Khoa đã thích bài viết của bạn", time = "5 phút"),

        )
    var ListNotiOld: List<Notify> = listOf(
        Notify(Icons.Default.AccountCircle,"Thông báo hệ thống", "Cập nhật: Hệ thống sẽ bảo trì lúc 2AM", "Hôm qua"),
        Notify(Icons.Default.AccountCircle,"Nhu vừa nhắc đến bạn", "bài này đáng lẽ phải làm như này", "1 ngày"),
    )


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

                items(ListNotiNew) { notify ->
                    NotifyChild(notify, navHostController)
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

                items(ListNotiOld) { notify ->
                    NotifyChild(notify, navHostController)
                }
            }
        }
    }
}

@Composable
fun NotifyChild(
    notify: Notify,
    navHostController: NavHostController
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .clickable { navHostController.navigate("detail_notify") },
    ) {
        Divider(thickness = 2.dp)
        Row(
            modifier = Modifier
                .background( MaterialTheme.colorScheme.tertiaryContainer)
                .padding(vertical = 15.dp, horizontal = 12.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Icon(
                imageVector = notify.icon,
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
                        text = notify.time,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }
        }
    }
}