package com.example.itforum.login

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ResetPasswordScreen(
    onBackClick: () -> Unit = {},
    onLoginClick: () -> Unit = {}
) {
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var isConfirmPasswordValid by remember { mutableStateOf(true) }

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
                text = "Cài lại mật khẩu",
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = newPassword,
            onValueChange = {
                newPassword = it
                // Mỗi khi mật khẩu mới thay đổi, kiểm tra lại confirm password
                isConfirmPasswordValid = confirmPassword.isEmpty() || confirmPassword == newPassword
            },
            label = { Text("Mật khẩu mới", fontSize = 20.sp)},
            visualTransformation = PasswordVisualTransformation(), // Ẩn mật khẩu
            modifier = Modifier.fillMaxWidth(0.85f)
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = confirmPassword,
            onValueChange = {
                confirmPassword = it
                isConfirmPasswordValid = confirmPassword == newPassword
            },
            label = { Text("Nhập lại mật khẩu mới", fontSize = 20.sp) },
            isError = !isConfirmPasswordValid,
            visualTransformation = PasswordVisualTransformation(), // Ẩn mật khẩu
            modifier = Modifier.fillMaxWidth(0.85f)
        )

        if (!isConfirmPasswordValid) {
            Text(
                text = "Mật khẩu nhập lại không khớp, vui lòng nhập lại",
                color = Color.Red,
                style = MaterialTheme.typography.caption,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onLoginClick,
            colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF6FA9FF)),
            shape = RoundedCornerShape(30.dp),
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .height(50.dp)
        ) {
            Text("Đăng nhập", color = Color.White)
        }

        Spacer(modifier = Modifier.weight(1f))

        Text(
            buildAnnotatedString {
                append("Chưa có tài khoản? ")
                withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                    append("Đăng ký ngay")
                }
            },
            modifier = Modifier.clickable { /* onRegisterClick() */ }
        )
    }
}
