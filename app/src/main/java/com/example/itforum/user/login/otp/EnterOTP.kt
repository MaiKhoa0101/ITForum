package com.example.itforum.user.login.otp

import android.widget.Toast
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
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


//@Composable
//fun EnterOtpScreen(
//    email: String,
//    onOtpSubmitted: (String) -> Unit,
//    onBackClick: () -> Unit = {},
//    onResendClick: () -> Unit = {}
//) {
//    var otp by remember { mutableStateOf("") }
//    val context = LocalContext.current
//
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(horizontal = 24.dp, vertical = 32.dp),
//        horizontalAlignment = Alignment.CenterHorizontally
//    ) {
//        // Tiêu đề + nút back cùng dòng, giống thiết kế
//        Row(
//            verticalAlignment = Alignment.CenterVertically,
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(top = 70.dp)
//        ) {
//            Icon(
//                imageVector = Icons.Default.ArrowBack,
//                contentDescription = "Back",
//                tint = Color.Black,
//                modifier = Modifier
//                    .size(40.dp)
//                    .clickable { onBackClick() }
//            )
//            Spacer(modifier = Modifier.width(12.dp))
//            Text(
//                text = "Nhập mã OTP",
//                fontSize = 35.sp,
//                fontWeight = FontWeight.Bold,
//            )
//        }
//
//        Spacer(modifier = Modifier.height(32.dp))
//
//        Text("OTP đã được gửi đến email: $email", fontSize = 16.sp)
//
//        Spacer(modifier = Modifier.height(24.dp))
//
//        OutlinedTextField(
//            value = otp,
//            onValueChange = { otp = it },
//            label = { Text("Nhập mã OTP") },
//            singleLine = true,
//            modifier = Modifier.fillMaxWidth()
//        )
//
//        Spacer(modifier = Modifier.height(24.dp))
//
//        Button(
//            onClick = {
//                if (otp.length == 6) {
//                    onOtpSubmitted(otp)
//                } else {
//                    Toast.makeText(context, "Mã OTP không hợp lệ", Toast.LENGTH_SHORT).show()
//                }
//            },
//            modifier = Modifier.fillMaxWidth(),
//            colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF6FA9FF))
//        ) {
//            Text("Xác minh OTP",color = Color.White)
//        }
//
//        Spacer(modifier = Modifier.height(16.dp))
//
//        TextButton(onClick = onResendClick) {
//            Text("Gửi lại mã OTP", color = Color.White)
//        }
//    }
//}

@Composable
fun EnterOtpScreen(
    email: String,
    onOtpSubmitted: (String) -> Unit,
    onBackClick: () -> Unit = {},
    onResendClick: () -> Unit = {}
) {
    var otp by remember { mutableStateOf("") }
    val context = LocalContext.current

    var remainingTime by remember { mutableStateOf(90) }  // 90 giây đếm ngược
    var isTimeout by remember { mutableStateOf(false) }

    // Đếm ngược thời gian
    LaunchedEffect(Unit) {
        while (remainingTime > 0) {
            kotlinx.coroutines.delay(1000)
            remainingTime--
        }
        isTimeout = true
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 70.dp)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back",
                tint = Color.Black,
                modifier = Modifier
                    .size(40.dp)
                    .clickable { onBackClick() }
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "Nhập mã OTP",
                fontSize = 35.sp,
                fontWeight = FontWeight.Bold,
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text("OTP đã được gửi đến email: $email", fontSize = 16.sp)

        if (!isTimeout) {
            Text("Thời gian còn lại: $remainingTime giây", fontSize = 14.sp, color = Color.Gray)
        } else {
            Text("OTP đã hết hạn, vui lòng gửi lại", fontSize = 14.sp, color = Color.Red)
        }

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = otp,
            onValueChange = { otp = it },
            label = { Text("Nhập mã OTP") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            enabled = !isTimeout
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                if (otp.length == 6) {
                    onOtpSubmitted(otp)
                } else {
                    Toast.makeText(context, "Mã OTP không hợp lệ", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF6FA9FF)),
            enabled = !isTimeout
        ) {
            Text("Xác minh OTP", color = Color.White)
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(onClick = onResendClick) {
            Text("Gửi lại mã OTP", color = Color(0xFF6FA9FF))
        }
    }
}




