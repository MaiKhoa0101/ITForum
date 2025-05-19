package com.example.itforum.register

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.itforum.R

@Composable
fun RegisterScreen(
    onBackClick: () -> Unit = {},
    onLoginClick: () -> Unit = {},
    onRegisterSuccess: () -> Unit = {}
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var isEmailValid by remember { mutableStateOf(true) }
    var passwordError by remember { mutableStateOf(false) }
    var confirmPasswordError by remember { mutableStateOf(false) }

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
            // Background wave design
            Canvas(modifier = Modifier.fillMaxSize()) {
                val width = size.width
                val height = size.height

                val path = Path().apply {
                    moveTo(0f, 0f)
                    lineTo(0f, height * 0.75f)
                    quadraticTo(
                        width * 0.07f, height * 1.25f,
                        width * 0.5f, height * 0.8f
                    )
                    quadraticTo(
                        width * 0.75f, height * 0.5f,
                        width, height * 0.9f
                    )
                    lineTo(width, 0f)
                    close()
                }

                drawPath(
                    path = path,
                    color = Color(0xFF6FA9FF)
                )
            }

            // Title
            Text(
                text = "Đăng ký",
                color = Color.White,
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                textDecoration = TextDecoration.Underline,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 70.dp)
            )

            // Logo and text
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
        }

        // Form fields
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Phone number field
            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it
                    isEmailValid = android.util.Patterns.EMAIL_ADDRESS.matcher(it).matches()
                },
                label = { Text("Email") },
                placeholder = { Text("Email của bạn") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                isError = !isEmailValid,
                modifier = Modifier.fillMaxWidth()
            )

            if (!isEmailValid && email.isNotEmpty()) {
                Text(
                    text = "Email không hợp lệ",
                    color = Color.Red,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }

            // Password field
            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                    passwordError = false
                },
                label = { Text("Mật khẩu") },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                isError = passwordError,
                modifier = Modifier.fillMaxWidth()
            )

            if (passwordError) {
                Text(
                    text = "Mật khẩu cần ít nhất 6 ký tự",
                    color = Color.Red,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }

            // Confirm password field
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = {
                    confirmPassword = it
                    confirmPasswordError = false
                },
                label = { Text("Xác nhận mật khẩu") },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                isError = confirmPasswordError,
                modifier = Modifier.fillMaxWidth()
            )

            if (confirmPasswordError) {
                Text(
                    text = "Mật khẩu không khớp",
                    color = Color.Red,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }

            // Register button
            Button(
                onClick = {
                    // Validate inputs
                    passwordError = password.length < 6
                    confirmPasswordError = password != confirmPassword
                    isEmailValid = android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()

                    if (!passwordError && !confirmPasswordError && isEmailValid) {
                        onRegisterSuccess()
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF6FA9FF),
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text("Đăng ký", fontSize = 18.sp)
            }

            // Login link
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text("Đã có tài khoản? ")
                Text(
                    "Đăng nhập ngay",
                    color = Color(0xFF6FA9FF),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onLoginClick() }
                )
            }
        }
    }
}
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun RegisterScreenPreview(){
    RegisterScreen()
}
