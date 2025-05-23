package com.example.itforum.user.login

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.material.ButtonDefaults



@Composable
fun ForgotPasswordScreen(onBackClick: () -> Unit = {}, onPhoneOptionClick: () -> Unit = {}, onEmailOptionClick: () -> Unit = {}) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFFF)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(

            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Spacer(modifier = Modifier.height(200.dp))
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back",
                tint = Color.Black,
                modifier = Modifier
                    .size(60.dp)
                    .clickable { onBackClick() }
                    .border(2.dp, Color.White, shape = RoundedCornerShape(4.dp))
                    .padding(4.dp)
            )

            Spacer(modifier = Modifier.width(10.dp))

            Text(
                text = "Quên mật khẩu",
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }

        Spacer(modifier = Modifier.height(200.dp))

        Button(
            onClick = { onPhoneOptionClick() },
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = Color.White,
                contentColor = Color.Black
            ),
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .height(48.dp)
        ) {
            Text(
                text = "Gửi mã otp qua số điện thoại",
                fontSize = 18.sp // 🔼 tăng kích thước chữ ở đây
            )
        }




        Spacer(modifier = Modifier.height(70.dp))

        OutlinedButton(
            onClick = { onEmailOptionClick() },
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = Color.White,    // ✅ màu nền
                contentColor = Color.Black        // ✅ màu chữ
            ),
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .height(48.dp)
        ) {
            Text(
                text = "Gửi mã otp qua email",
                fontSize = 18.sp // 🔼 tăng kích thước chữ ở đây
//
            )

        }
    }
}
