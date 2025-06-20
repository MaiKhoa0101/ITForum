package com.example.itforum.user.post

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController


@Composable
fun ListLikePage(navHostController: NavHostController) {
    var ListUserLike: List<Pair<ImageVector, String>> = listOf(
        Icons.Default.AccountCircle to "Doremon",
        Icons.Default.AccountCircle to "JACK",
        Icons.Default.AccountCircle to "Phương Tuấn",
        Icons.Default.AccountCircle to "J97",
        Icons.Default.AccountCircle to "MEO Meo",
        Icons.Default.AccountCircle to "Trịnh Tổng",
        Icons.Default.AccountCircle to "Doremon",
        Icons.Default.AccountCircle to "JACK",
        Icons.Default.AccountCircle to "Phương Tuấn",
        Icons.Default.AccountCircle to "J97",
        Icons.Default.AccountCircle to "MEO Meo",
        Icons.Default.AccountCircle to "Trịnh Tổng",
        Icons.Default.AccountCircle to "Doremon",
        Icons.Default.AccountCircle to "JACK",
        Icons.Default.AccountCircle to "Phương Tuấn",
        Icons.Default.AccountCircle to "J97",
        Icons.Default.AccountCircle to "MEO Meo",
        Icons.Default.AccountCircle to "Trịnh Tổng",
        Icons.Default.AccountCircle to "Doremon",
        Icons.Default.AccountCircle to "JACK",
        Icons.Default.AccountCircle to "Phương Tuấn",
        Icons.Default.AccountCircle to "J97",
        Icons.Default.AccountCircle to "MEO Meo",
        Icons.Default.AccountCircle to "Trịnh Tổng"
    )

    Column (
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF00AEFF))
    ) {
        TopListLike(
            navHostController,
            "Lượt chọn hữu ích"
        )
        Column(
            modifier = Modifier.fillMaxSize()
                .background(Color.White)
        ) {
            Text(
                text ="Tất cả 50",
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(10.dp)
                    .fillMaxWidth()

            )
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .TopBorder(Color(0xFF000000))
            ) {
                ListUserLike.forEach { (avatar, name)->
                    item {
                        IconWithText(avatar = "avatar",name = name)
                    }
                }
            }
        }
    }
}

@Composable
fun TopListLike(
    navHostController: NavHostController,
    title: String,
    space: Dp = 40.dp,
) {
    Spacer(modifier = Modifier.height(30.dp))
    Row(
        modifier = Modifier
            .padding(horizontal = 13.dp, vertical = 10.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = {
            navHostController.popBackStack()
        }
        ) {
            Icon(
                imageVector = Icons.Default.KeyboardArrowLeft,
                contentDescription = "Quay lại",
                tint = Color.White,
                modifier = Modifier.size(40.dp)
            )
        }
        Spacer(modifier = Modifier.width(space))
        Text(
            text = title,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}