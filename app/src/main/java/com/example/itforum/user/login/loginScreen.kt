package com.example.itforum.user.login

import android.content.SharedPreferences
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Canvas

import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.style.TextDecoration
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavHostController
import com.example.itforum.user.login.viewmodel.LoginViewModel


@Composable

fun LoginScreen(
    navHostController: NavHostController,
    sharedPreferences: SharedPreferences,
    onRegisterClick: () -> Unit = {},
    onForgotPasswordClick: () -> Unit = {} // Thêm callback
) {

    val loginViewModel : LoginViewModel = viewModel(factory = viewModelFactory {
        initializer { LoginViewModel(sharedPreferences) }
    })

    val isSuccess by loginViewModel.isSuccess
    LaunchedEffect(isSuccess) {
        if (isSuccess) {
            loginViewModel.resetLoginState()
            navHostController.navigate("home")
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(350.dp)
        ) {
            // Đường cong nền xanh
            Canvas(
                modifier = Modifier
                    .matchParentSize()
            ) {
                val width = size.width
                val height = size.height

                val path = Path().apply {
                    moveTo(0f, 0f)
                    lineTo(0f, height * 0.75f)

                    quadraticBezierTo(
                        width * 0.1f, height * 1.2f,  // Điểm điều khiển kéo xuống để tạo hình chữ V nhọn
                        width * 0.3f, height.toFloat() // Kết thúc tại đáy gần 1/3 màn hình
                    )

                    // 2. Vẽ đường cong như cầu vồng bên phải
                    quadraticBezierTo(
                        width * 0.9f, height * 0.1f,  // Điểm điều khiển cao hơn để tạo hình cầu vồng (nhô lên)
                        width*1.5f, height * 1.25f.toFloat()       // Kết thúc ở góc dưới bên phải
                    )
                    lineTo(width.toFloat(), 0f)
                    lineTo(0f, 0f)
                    close()

                }

                drawPath(
                    path = path,
                    color = Color(0xFF6FA9FF)
                )
            }

            // Văn bản "Đăng nhập"
            Text(
                text = "Đăng nhập",
                color = Color.White,
                fontSize = 43.sp,
                fontWeight = FontWeight.Bold,
                textDecoration = TextDecoration.Underline, // Gạch chân
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 150.dp)
            )


            // Icon + Hello text (góc dưới-trái)
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(start = 24.dp, bottom = 24.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(RoundedCornerShape(25.dp))
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "</>",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Hello world\nHello dev!",
                    color = Color.White,
                    fontSize = 20.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Số điện thoại
        var phoneNumberOrEmail by remember { mutableStateOf("") }
        val isPhoneOrEmailValid = phoneNumberOrEmail.all { it.isDigit() } || phoneNumberOrEmail.contains("@")

        OutlinedTextField(
            value = phoneNumberOrEmail,
            onValueChange = {
                phoneNumberOrEmail = it
            },
            label = { Text("Số điện thoại", fontSize = 20.sp) },
            placeholder = { Text("Số điện thoại của bạn") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            isError = !isPhoneOrEmailValid,
            modifier = Modifier
                .fillMaxWidth(0.9f)
        )

        if (!isPhoneOrEmailValid && phoneNumberOrEmail.isNotEmpty()) {
            Text(
                text = "Số điện thoại không hợp lệ",
                color = Color.Red,
                style = MaterialTheme.typography.body2,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }


        Spacer(modifier = Modifier.height(16.dp))


        var password by remember { mutableStateOf("") }
        val password_test = "password123"
        val isPasswordValid = password.length >= 6

        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
            },
            label = { Text("Mật khẩu", fontSize = 20.sp) },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            isError = isPasswordValid,
            modifier = Modifier.fillMaxWidth(0.9f)
        )



        if (!isPasswordValid) {
            Text(
                text = "Mật khẩu không hợp lệ",
                color = Color.Red,
                style = MaterialTheme.typography.body2,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }


        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Quên mật khẩu",
            fontSize = 16.sp,
            color = Color.Black,
            modifier = Modifier
                .align(Alignment.End)
                .padding(end = 24.dp)
                .clickable { onForgotPasswordClick() }
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                if (isPasswordValid && isPhoneOrEmailValid ) {
                    loginViewModel.userLogin(phoneNumberOrEmail, password)
                }

            },
            colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF6FA9FF)),
            shape = RoundedCornerShape(50),
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .height(50.dp)
        ) {
            Text("Đăng nhập", color = Color.White, fontSize = 24.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row {
            Text("Chưa có tài khoản?", fontSize = 16.sp)
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                "Đăng ký ngay",
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable { onRegisterClick() }
            )
        }
    }
}
