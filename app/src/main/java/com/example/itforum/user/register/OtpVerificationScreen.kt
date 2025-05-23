package com.example.itforum.user.register

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.itforum.R
import kotlinx.coroutines.delay

@Composable
fun OtpVerificationScreen(
    onBackClick: () -> Unit = {},
    onResendClick: () -> Unit = {},
    onSubmitClick: () -> Unit = {},
    onLoginClick: () -> Unit = {}
) {
    var otp by remember { mutableStateOf("") }
    var remainingTime by remember { mutableStateOf(90) }
    var isOtpExpired by remember { mutableStateOf(false) }
    val otpTest = "123456"
    var isOtpCorrect by remember { mutableStateOf(true) }

    LaunchedEffect(key1 = remainingTime) {
        if (remainingTime > 0) {
            delay(1000L)
            remainingTime--
        } else {
            isOtpExpired = true
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
    Column(
        modifier = Modifier
            .fillMaxSize(),
//            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.height(97.dp))
        // Top bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 40.dp, start = 16.dp, end = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = Color.Black,
                modifier = Modifier
                    .size(40.dp)
                    .clickable { onBackClick() }
                    .border(1.dp, Color.Gray, shape = RoundedCornerShape(4.dp))
                    .padding(4.dp)
            )

//            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = "Nhập mã OTP",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier
                .size(40.dp)
                .padding(4.dp))
        }



        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Mã OTP đã được gửi tới email của bạn.\nVui lòng kiểm tra hộp thư.",
            textAlign = TextAlign.Center,
            fontSize = 16.sp


        )

        Spacer(modifier = Modifier.height(12.dp))

        if (!isOtpExpired) {
            Text("Còn lại $remainingTime giây trước khi OTP hết hạn",fontSize = 16.sp, fontWeight = FontWeight.Bold)

        } else {
            Text("Mã OTP đã hết hạn", color = Color.Red, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            buildAnnotatedString {
                append("Không nhận được mã? ")
                withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = Color.Black)) {
                    append("Gửi lại")
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
                    isOtpCorrect = true
                }
            },
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.fillMaxWidth(0.85f),
            isError = !isOtpCorrect || isOtpExpired
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
                if (isOtpExpired) return@Button
                if (otp == otpTest) {
                    isOtpCorrect = true
                    onSubmitClick()

                } else {
                    isOtpCorrect = false
                }
            },
            colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF6FA9FF)),
            shape = RoundedCornerShape(30.dp),
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .height(50.dp)
        ) {
            Text("Xác nhận", color = Color.White, fontSize = 20.sp)
        }
        Spacer(modifier = Modifier.height(200.dp))
    }
        Image(
            painter = painterResource(id = R.drawable.tamgiac), // thay bằng ảnh của bạn
            contentDescription = "Decorative Image",
            modifier = Modifier
                .align(Alignment.BottomEnd)
//                .padding(16.dp)
                .width(800.dp)
                .height(280.dp)
        )

    }

}



@Preview (showBackground = true)
@Composable
fun myscreen(){
    OtpVerificationScreen()
}