package com.example.itforum.root

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.itforum.home.HomePage
import com.example.itforum.intro.IntroScreen
import com.example.itforum.login.EnterEmailScreen
import com.example.itforum.login.EnterOtpScreen
import com.example.itforum.login.EnterPhoneNumberScreen
import com.example.itforum.login.ForgotPasswordScreen
import com.example.itforum.login.LoginScreen
import com.example.itforum.login.ResetPasswordScreen
import com.example.itforum.notification.NotificationPage
import com.example.itforum.post.CreatePostPage
import com.example.itforum.post.DetailPostPage
import com.example.itforum.post.ListLikePage
import com.example.itforum.profile.EditProfile
import com.example.itforum.profile.OtherProfile
import com.example.itforum.profile.UserProfile
import com.example.itforum.register.OtpVerificationScreen
import com.example.itforum.register.RegisterScreen
import com.example.itforum.register.RegistrationSuccessScreen
import com.example.itforum.tool.ToolPage
@Composable
fun BodyRoot(navHostController: NavHostController,modifier: Modifier){
    NavHost(navHostController, startDestination = "home") {
        composable ("home") {
            HomePage(modifier)
        }
        composable ("notification") {
            NotificationPage(modifier)
        }
        composable ("tool") {
            ToolPage(modifier)
        }
        composable ("personal") {
            UserProfile(modifier, navHostController)
        }
        composable ("otherprofile") {
            OtherProfile(modifier)
        }
        composable ("editprofile") {
            EditProfile(navHostController)
        }
        composable("create_post") {
            CreatePostPage(modifier, navHostController)
        }
        composable("detail_post"){
            DetailPostPage(navHostController)
        }
        composable("listlike") {
            ListLikePage(navHostController)
        }
    }
}
//
//ITForumTheme {
//    val navController = rememberNavController()
//    NavHost(
//        navController = navController,
//        startDestination = "intro" // Màn hình đầu tiên là intro
//    ) {
//        composable("intro") {
//            IntroScreen(navController)
//        }
//        composable("login") {
//            LoginScreen(
//                onRegisterClick = { navController.navigate("register") },
//                onForgotPasswordClick = { navController.navigate("forgot_password") }
//            )
//        }
//        composable("forgot_password") {
//            ForgotPasswordScreen(
//                onBackClick = { navController.popBackStack() },
//                onPhoneOptionClick = { navController.navigate("phone_otp") },
//                onEmailOptionClick = { navController.navigate("email_otp") }
//            )
//        }
//
//        composable("phone_otp") {
//            EnterPhoneNumberScreen(onBackClick = { navController.popBackStack() },
//                onContinueClick = {navController.navigate("enter_otp")})
//        }
//        composable("email_otp") {
//            EnterEmailScreen(onBackClick = { navController.popBackStack() },
//                onContinueClick = {navController.navigate("enter_otp")})
//        }
//        composable("enter_otp"){
//            EnterOtpScreen(onBackClick = { navController.popBackStack() },
//                onSubmitClick = {navController.navigate("sumit_otp")})
//
//        }
//        composable("sumit_otp"){
//            ResetPasswordScreen(onBackClick= {navController.popBackStack()},)
//            // thêm điều hướng cho nút
//        }
//        composable("com/example/itforum/register") {
//            RegisterScreen(navController) // Màn hình đăng ký mới thêm
//        }
//        composable("otp") {
//            OtpVerificationScreen(
//                onBackClick = { navController.popBackStack() },
//                onSubmitClick = { navController.navigate("success") }, //  Điều hướng sau khi xác thực thành công
//                onResendClick = { /* xử lý gửi lại */ },
//                onLoginClick = { navController.navigate("login") }
//            )
//        }
//        composable("success") {
//            RegistrationSuccessScreen(
//                onLoginClick = { navController.navigate("login") }
//            )
//        }
//    }
//}