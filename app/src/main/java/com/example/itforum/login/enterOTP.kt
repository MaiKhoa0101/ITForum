package com.example.itforum.login

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun EnterOtpScreen(
    onBackClick: () -> Unit = {},
    onResendClick: () -> Unit = {},
    onSubmitClick: () -> Unit = {},
    onLoginClick: () -> Unit = {}
) {
    var otp by remember { mutableStateOf("") }
    var remainingTime by remember { mutableStateOf(90) }
    var isOtpExpired by remember { mutableStateOf(false) }
    val otp_test = "123456"  // Mã OTP mẫu để kiểm tra
    var isOtpCorrect by remember { mutableStateOf(true) }  // Trạng thái kiểm tra OTP đúng hay sai


    // Bắt đầu đếm ngược khi composable này chạy
    LaunchedEffect(key1 = remainingTime) {
        if (remainingTime > 0) {
            kotlinx.coroutines.delay(1000L)
            remainingTime--
        } else {
            isOtpExpired = true
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top bar
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
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
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Nhập mã OTP",
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Mã OTP đã được gửi qua địa chỉ email của bạn\nhãy kiểm tra trong tin nhắn nhé!",
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(12.dp))

        if (!isOtpExpired) {
            Text("Còn lại $remainingTime giây trước khi OTP hết hạn", fontWeight = FontWeight.Bold)
        } else {
            Text("Mã OTP đã quá hạn", color = Color.Red, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            buildAnnotatedString {
                append("Không nhận được OTP? ")
                withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = Color.Black)) {
                    append("Gửi lại ngay")
                }
            },
            modifier = Modifier.clickable {
                onResendClick()
                remainingTime = 90
                isOtpExpired = false
                otp = ""
                isOtpCorrect = true
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = otp,
            onValueChange = {
                if (!isOtpExpired) {
                    otp = it
                    isOtpCorrect = true // reset trạng thái lỗi khi nhập mới
                }
            },
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.fillMaxWidth(0.85f),
            isError = !isOtpCorrect || isOtpExpired  // viền đỏ khi sai OTP hoặc hết hạn
        )


        if (!isOtpCorrect) {
            Text(
                text = "Mã OTP không đúng",
                color = Color.Red,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                if (isOtpExpired) {
                    // Có thể hiển thị toast hoặc dialog cảnh báo ở đây
                } else {
                    if (otp == otp_test) {
                        isOtpCorrect = true
                        onSubmitClick()
                    } else {
                        isOtpCorrect = false
                    }
                }
            },
            colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF6FA9FF)),
            shape = RoundedCornerShape(30.dp),
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .height(50.dp)
        ) {
            Text("Gửi", color = Color.White, fontSize = 25.sp)
        }

        Spacer(modifier = Modifier.weight(1f))

        Text(
            buildAnnotatedString {
                append("Đã có tài khoản? ")
                withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                    append("Đăng nhập ngay")
                }
            },
            modifier = Modifier.clickable { onLoginClick() }
        )
    }
}
