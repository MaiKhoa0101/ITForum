package com.example.itforum.user.login




import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun EnterEmailScreen(onBackClick: () -> Unit = {}, onContinueClick: () -> Unit = {}, onRegisterClick: () -> Unit = {}) {
    var phoneNumber by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
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
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Quên mật khẩu",
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(60.dp))

        var email by remember { mutableStateOf("") }
        val email_test = "user@gmail.com"  // email đã đăng ký (ví dụ)

        var isEmailValid by remember { mutableStateOf(true) }
        var isEmailRegistered by remember { mutableStateOf(true) }

        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it

                // Kiểm tra định dạng email hợp lệ đơn giản bằng Regex
                isEmailValid = android.util.Patterns.EMAIL_ADDRESS.matcher(it).matches()

                // Nếu email hợp lệ thì kiểm tra có trùng với email_test (đã đăng ký) hay không
                isEmailRegistered = (it == email_test)
            },
            label = { Text("Email") },
            placeholder = { Text("Email của bạn") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            isError = !isEmailValid || !isEmailRegistered,
            modifier = Modifier.fillMaxWidth(0.9f)
        )

// Hiển thị lỗi định dạng email
        if (!isEmailValid && email.isNotEmpty()) {
            Text(
                text = "Email không hợp lệ",
                color = Color.Red,
                style = MaterialTheme.typography.body2,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }

// Hiển thị lỗi email chưa đăng ký
        if (isEmailValid && !isEmailRegistered && email.isNotEmpty()) {
            Text(
                text = "Email chưa được đăng ký",
                color = Color.Red,
                style = MaterialTheme.typography.body2,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }


        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { onContinueClick() },
            colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF6FA9FF)),
            shape = RoundedCornerShape(30.dp),
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .height(50.dp),
        ) {
            Text("Tiếp tục", color = Color.White)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row {
            Text("Chưa có tài khoản?")
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "Đăng ký ngay",
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable { onRegisterClick() }
            )
        }
    }
}
