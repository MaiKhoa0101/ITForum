package com.example.itforum.intro.pages

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.itforum.R
@Composable
fun WaveBackground() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height

        val path = Path().apply {
            // Bắt đầu từ góc dưới trái
            moveTo(0f, height)

            // Sóng đầu tiên (gợn lên)
            quadraticTo(
                width * 0.2f, height * 0.85f,
                width * 0.4f, height * 0.9f
            )

            // Sóng thứ hai (gợn xuống)
            quadraticTo(
                width * 0.6f, height * 0.95f,
                width * 0.8f, height * 0.8f
            )

            // Sóng cuối (kết thúc về giữa bên phải)
            quadraticTo(
                width * 0.9f, height * 0.7f,
                width, height * 0.6f
            )

            // Vẽ lên góc trên phải và quay lại trên trái
            lineTo(width, 0f)
            lineTo(0f, 0f)
            close()
        }

        clipPath(path) {
            drawRect(color = Color(0xFFF4F8EA))
        }
    }
}

@Composable
fun IntroPage0() {
    Box(modifier = Modifier.fillMaxSize()) {
        // Wave Background (zIndex mặc định là 0)
        WaveBackground()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                "CHÀO MỪNG BẠN!!!",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.zIndex(1f) // Đảm bảo nằm trên
            )
            Image(
                painter = painterResource(id = R.drawable.img_intro0),
                contentDescription = "Intro image 0",
                modifier = Modifier
                    .fillMaxWidth()
                    .zIndex(1f), // Đảm bảo nằm trên
                contentScale = ContentScale.FillWidth
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                "Thế giới dành cho IT chia sẻ học hỏi kiến thức",
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.zIndex(1f) // Đảm bảo nằm trên
            )
        }
    }
}


