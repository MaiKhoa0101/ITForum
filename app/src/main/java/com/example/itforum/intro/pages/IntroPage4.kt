package com.example.itforum.intro.pages

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.itforum.R

@Composable
fun IntroPage4() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp)
    ) {
        // Phần tiêu đề với gạch chân (giữ nguyên)
        Column(
            modifier = Modifier
                .padding(top = 65.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "BẮT ĐẦU HÀNH TRÌNH",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .height(2.dp)
                    .fillMaxWidth(0.7f)
                    .background(Color.White)
            )
        }

        // Phần hình ảnh và text ở vị trí (x=13, y=162)
        Box(
            modifier = Modifier
                .offset(x = 13.dp, y = 162.dp) // Vị trí chính xác
        ) {
            Column {
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "Intro Image 4",
                    modifier = Modifier.size(100.dp)
                )

                // Hai dòng chữ nghiêng
                Column(
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Text(
                        text = "Hello world",
                        fontStyle = FontStyle.Italic, // Chữ nghiêng
                        fontSize = 20.sp
                    )
                    Text(
                        text = "Hello dev!",
                        fontStyle = FontStyle.Italic, // Chữ nghiêng
                        fontSize = 20.sp
                    )
                }
            }
        }

        // Phần nội dung chính (đẩy xuống dưới)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "HÃY ĐĂNG NHẬP HOẶC TẠO TÀI KHOẢN ĐỂ BẮT ĐẦU TRẢI NGHIỆM NGAY THÔI NÀO!",
                fontSize = 20.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}
