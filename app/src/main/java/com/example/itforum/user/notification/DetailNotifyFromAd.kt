package com.example.itforum.user.notification

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.PermIdentity
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.itforum.user.Analytics.logScreenView
import com.example.itforum.user.post.IconWithText
import com.example.itforum.user.post.TopListLike

@Composable
fun DetailNotify(
    modifier: Modifier,
    navHostController: NavHostController
){

    Column (
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF00AEFF))
    ) {
        TopListLike(
            navHostController,
            "Thông báo mới", 55.dp)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFEEEEEE))
        ) {
            FormNotify()
            Button(
                onClick =  {},
                modifier = Modifier
                    .align(Alignment.CenterHorizontally),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color.White
                )
            ) {
                IconWithText(
                    "https://photo.znews.vn/w660/Uploaded/mdf_eioxrd/2021_07_06/2.jpg",
                    "Liên hệ hỗ trợ",
                    25.dp,
                    modifier = Modifier.padding(0.dp)
                    )
            }
        }
    }
}

@Composable
fun FormNotify() {
   Card(
       modifier = Modifier.padding(horizontal = 15.dp, vertical = 20.dp),
       shape = RoundedCornerShape(12.dp),
       colors = CardDefaults.cardColors(
           containerColor = Color.White
       )
   ) {
       Column(
           modifier = Modifier.padding(horizontal = 10.dp, vertical = 25.dp),
           verticalArrangement = Arrangement.spacedBy(17.dp)
       ) {
           Text(
               "Cập nhật chính sách sử dụng",
               fontSize = 22.sp,
               fontWeight = FontWeight.Bold,
               modifier = Modifier.fillMaxWidth(),
               textAlign = TextAlign.Center
           )
           IconWithText("https://photo.znews.vn/w660/Uploaded/mdf_eioxrd/2021_07_06/2.jpg","Ngày: 01/04/2025 11:35 AM", sizeIcon = 35.dp)
           IconWithText("https://photo.znews.vn/w660/Uploaded/mdf_eioxrd/2021_07_06/2.jpg","Từ: Admin hệ thống", sizeIcon = 35.dp)
           IconWithText("https://photo.znews.vn/w660/Uploaded/mdf_eioxrd/2021_07_06/2.jpg","Nội dung thông báo:", sizeIcon = 35.dp)
           Text(
               text = "Chúng tôi xing thông báo rằng chính sách sử dụng dịch vụ" +
                       " ádaskdasdafjafjs sjfsjsifjsdfjjsd fdfkjdf  sdfsd fsf  sf" +
                       " sf sdf sdf sd sdfsf df df df df df df d d fdf df df df d" +
                       " f asdasfjkhsafj kajshdfkajs fsad hjsfdka",
               fontSize = 20.sp,
               modifier = Modifier.padding(start = 40.dp),
               lineHeight = 30.sp
               )
           Spacer(modifier = Modifier.height(10.dp))
       }
   }
}