package com.example.itforum.user.login
import androidx.compose.ui.graphics.graphicsLayer
import kotlinx.coroutines.delay
import android.content.SharedPreferences
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Canvas
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.ui.draw.scale
import com.example.itforum.R

import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.itforum.user.complaint.SuccessDialog
import com.example.itforum.user.effect.model.UiState
import com.example.itforum.user.effect.UiStateMessage
import com.example.itforum.user.login.loginGoogle.GoogleSignInButton
import com.example.itforum.user.login.loginGoogle.saveUserToFirestore
import com.example.itforum.user.login.viewmodel.LoginViewModel
import com.example.itforum.user.notification.subscribeToAppTopic
import com.google.android.gms.auth.api.signin.GoogleSignIn

import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.tasks.await


@Composable
fun LoginScreen(
    navHostController: NavHostController,
    sharedPreferences: SharedPreferences,
    onRegisterClick: () -> Unit = {},
    onForgotPasswordClick: () -> Unit = {},
) {
    val loginViewModel: LoginViewModel = viewModel(factory = viewModelFactory {
        initializer { LoginViewModel(sharedPreferences) }
    })

    var phoneNumberOrEmail by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    var isPhoneOrEmailValid by remember { mutableStateOf(true) }
    var isPasswordValid by remember { mutableStateOf(true) }


    var canSubmit by remember {mutableStateOf(false)}
    var showSuccessDialog by remember { mutableStateOf(false) }
    var enable by remember { mutableStateOf<Boolean>(true) }
    var error by remember { mutableStateOf<String>("") }

    val uiState by loginViewModel.uiState.collectAsState()

    val context = LocalContext.current

    LaunchedEffect(uiState) {
        println("uiState da bi thay doi: $uiState")
        if (uiState is UiState.Success) {
            val role = sharedPreferences.getString("role", null)
            if (role != null) {
                if (role == "admin") {
                    navHostController.navigate("admin_root")
                }
                else {
                    navHostController.navigate("home")
//                    subscribeToAppTopic()
                }
            }
        }else if(uiState is UiState.Loading){
            enable = false
        }else if(uiState is UiState.Idle){
            enable = true
        }else if(uiState is UiState.Error){
            showSuccessDialog = true
            error = (uiState as UiState.Error).message
        }
    }

    // UI hiển thị
    if (showSuccessDialog) {
        SuccessDialog(
            title = "Thông báo!!!",
            color = Color.Red,
            message = error,
            nameButton = "Đóng",
            onDismiss = {
                showSuccessDialog = false
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HeaderSection()
        Spacer(modifier = Modifier.height(24.dp))

        PhoneOrEmailInput(phoneNumberOrEmail, isPhoneOrEmailValid) {
            phoneNumberOrEmail = it
        }

        Spacer(modifier = Modifier.height(16.dp))

        PasswordInput(password, isPasswordValid) {
            password = it
        }

        ForgotPasswordText(onForgotPasswordClick)
        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                isPhoneOrEmailValid = phoneNumberOrEmail.all { it.isDigit() } || phoneNumberOrEmail.contains("@")
                isPasswordValid = password.length >= 6
                canSubmit = isPhoneOrEmailValid && isPasswordValid

                if (canSubmit) {
                    loginViewModel.userLogin(phoneNumberOrEmail, password)
                } else {
                    println("phone: $isPhoneOrEmailValid $phoneNumberOrEmail")
                    println("password: $isPasswordValid $password")
                }
            },
            colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF6FA9FF)),
            shape = RoundedCornerShape(50),
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .height(50.dp),
            enabled = enable
        ) {
            Text("Đăng nhập", color = Color.White, fontSize = 24.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ✅ Nút Google đã đóng gói xử lý bên trong
        GoogleSignInButton(
            onTokenReceived = { firebaseUser ->
                println("Firebase User: "+firebaseUser)
                if (firebaseUser != null) {
                    loginViewModel.handleGoogleLoginWithToken(firebaseUser)
                }
                else{
                    Toast.makeText(context, "Đăng nhập Google thất bại", Toast.LENGTH_SHORT).show()
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))
        RegisterText(onRegisterClick)
        UiStateMessage(uiState, canSubmit)
    }
}


@Composable
fun HeaderSection() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(350.dp)
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            val width = size.width
            val height = size.height
            val path = Path().apply {
                moveTo(0f, 0f)
                lineTo(0f, height * 0.75f)
                quadraticBezierTo(width * 0.1f, height * 1.2f, width * 0.3f, height)
                quadraticBezierTo(width * 0.9f, height * 0.1f, width * 1.5f, height * 1.25f)
                lineTo(width, 0f)
                lineTo(0f, 0f)
                close()
            }
            drawPath(path = path, color = Color(0xFF6FA9FF))
        }

        Text(
            text = "Đăng nhập",
            color = Color.White,
            fontSize = 43.sp,
            fontWeight = FontWeight.Bold,
            textDecoration = TextDecoration.Underline,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 150.dp)
        )

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
                Text(text = "</>", color = Color.Black, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text("Hello world\nHello dev!", color = Color.White, fontSize = 20.sp)
        }
    }
}
@Composable
fun PhoneOrEmailInput(value: String, isValid: Boolean, onValueChange: (String) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth(0.9f)) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text("Số điện thoại hoặc Email") },
            isError = value.isNotEmpty() && !isValid,
            modifier = Modifier.fillMaxWidth()
        )

        if (!isValid && value.isNotEmpty()) {
            Text("Số điện thoại hoặc email không hợp lệ", color = Color.Red, fontSize = 14.sp)
        }
    }
}

@Composable
fun PasswordInput(value: String, isValid: Boolean, onValueChange: (String) -> Unit) {
    var showPassword by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth(0.9f)) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text("Mật khẩu") },
            visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                val icon = if (showPassword) Icons.Rounded.Visibility else Icons.Rounded.VisibilityOff
                val description = if (showPassword) "Ẩn mật khẩu" else "Hiện mật khẩu"
                IconButton(onClick = { showPassword = !showPassword }) {
                    Icon(imageVector = icon, contentDescription = description)
                }
            },
            isError = value.isNotEmpty() && !isValid,
            modifier = Modifier.fillMaxWidth()
        )

        if (!isValid && value.isNotEmpty()) {
            Text("Mật khẩu phải từ 6 ký tự", color = Color.Red, fontSize = 14.sp)
        }
    }
}


@Composable
fun ForgotPasswordText(onClick: () -> Unit) {
    Row {
        Spacer(modifier = Modifier.weight(1f))
        Text(
            "Quên mật khẩu?",
            modifier = Modifier
                .padding(end = 30.dp, top = 16.dp)
                .clickable { onClick() },
            textAlign = TextAlign.End,
        )
    }

}


@Composable
fun RegisterText(onClick: () -> Unit) {
    Row {
        Text("Chưa có tài khoản?", fontSize = 16.sp)
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            "Đăng ký ngay",
            color = Color.Black,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.clickable { onClick() }
        )
    }
}

