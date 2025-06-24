package com.example.itforum.user.register

import android.content.SharedPreferences
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.itforum.user.register.viewmodel.RegisterViewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavHostController
import com.example.itforum.user.effect.model.UiState
import com.example.itforum.user.effect.UiStateMessage
import com.example.itforum.user.modelData.request.RegisterUser
import kotlin.text.contains

@Composable
fun RegisterScreen(
    navHostController: NavHostController,
    sharedPreferences: SharedPreferences,
    onBackClick: () -> Unit = {},
) {

    val registerViewModel: RegisterViewModel = viewModel(factory = viewModelFactory {
        initializer { RegisterViewModel(sharedPreferences) }
    })
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }


    var isEmailValid by remember { mutableStateOf(true)}
    var isNameValid by remember { mutableStateOf(true)}
    var isPhoneValid by remember { mutableStateOf(true)}
    var passwordError by remember { mutableStateOf(false) }
    var confirmPasswordError by remember { mutableStateOf(false) }


    val uiState by registerViewModel.uiState.collectAsState()
    var canSubmit by remember {mutableStateOf(false)}

    LaunchedEffect(uiState) {
        if (uiState is UiState.Success) {
            println("uiState là success")
            navHostController.navigate("success")
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // Form fields
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Title
            Text(
                text = "Đăng ký",
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                textDecoration = TextDecoration.Underline,
                modifier = Modifier
                    .padding(top = 70.dp)
            )
            // Họ và tên field
            OutlinedTextField(
                value = name,
                onValueChange = {
                    name = it
                    isNameValid = name.isNotEmpty() && name.all { it.isLetter() || it.isWhitespace() }

                },
                label = { Text("Họ và tên của bạn") },
                placeholder = { Text("Nguyễn Văn A") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                textStyle = androidx.compose.ui.text.TextStyle(color = Color.Black),
                modifier = Modifier.fillMaxWidth()
            )

            if (!isNameValid ) {
                Text(
                    text = "Ít nhất 2 từ và không chứa kí tự đặc biệt",
                    color = Color.Red,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }


            // Số điện thoại field
            OutlinedTextField(
                value = phone,
                onValueChange = {
                    phone = it
                    isPhoneValid = phone.all { it.isDigit() }
                },
                label = { Text("Số điện thoại") },
                placeholder = { Text("Số điện thoại của bạn") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                isError = !isPhoneValid,
                textStyle = androidx.compose.ui.text.TextStyle(color = Color.Black),
                modifier = Modifier.fillMaxWidth()
            )

            if (!isPhoneValid && phone.isNotEmpty()) {
                Text(
                    text = "Số điện thoại không hợp lệ",
                    color = Color.Red,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }

            // email field
            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it
                    isEmailValid = email.contains("@")
                },
                label = { Text("Email") },
                placeholder = { Text("Email của bạn") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                isError = !isEmailValid,
                textStyle = androidx.compose.ui.text.TextStyle(color = Color.Black),
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
                textStyle = androidx.compose.ui.text.TextStyle(color = Color.Black),
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
                textStyle = androidx.compose.ui.text.TextStyle(color = Color.Black),
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
                    canSubmit= isEmailValid && isNameValid && isPhoneValid && !passwordError && !confirmPasswordError
                    println("Đã nhấn đăng ký với cansubmit: $canSubmit")

                    if (canSubmit) {
                        registerViewModel.register(RegisterUser(name,phone, email, password))
                    }
                    else{
                        println("email: "+isEmailValid+" "+email)
                        println("name: "+isNameValid+" "+name)
                        println("phone: "+isPhoneValid+" "+phone)
                        println("password: "+passwordError+" "+password)
                        println("confirmPassword: "+confirmPasswordError+" "+confirmPassword)
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
                    modifier = Modifier.clickable {
                        navHostController.navigate("login") {
                            popUpTo(0) { inclusive = true }  // xóa hết stack
                            launchSingleTop = true           // tránh tạo bản sao nếu đã ở login
                        }
                    }
                )
            }
            UiStateMessage(uiState, canSubmit)

        }
    }
}

