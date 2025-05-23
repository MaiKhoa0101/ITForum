package com.example.itforum.user.root

import android.content.SharedPreferences
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.itforum.user.home.bookmark.BookMarkScreen
import com.example.itforum.user.home.follow.FollowScreen
import com.example.itforum.user.home.HomePage
import com.example.itforum.user.intro.IntroScreen
import com.example.itforum.user.login.EnterEmailScreen
import com.example.itforum.user.login.EnterOtpScreen
import com.example.itforum.user.login.EnterPhoneNumberScreen
import com.example.itforum.user.login.ForgotPasswordScreen
import com.example.itforum.user.login.LoginScreen
import com.example.itforum.user.login.ResetPasswordScreen
import com.example.itforum.user.home.myfeed.MyFeedScreen
import com.example.itforum.user.notification.NotificationPage
import com.example.itforum.user.post.CreatePostPage
import com.example.itforum.user.post.DetailPostPage
import com.example.itforum.user.post.ListLikePage
import com.example.itforum.user.profile.EditProfile
import com.example.itforum.user.profile.OtherProfile
import com.example.itforum.user.profile.UserProfile
import com.example.itforum.user.register.OtpVerificationScreen
import com.example.itforum.user.register.RegisterScreen
import com.example.itforum.user.register.RegistrationSuccessScreen
import com.example.itforum.user.tool.ToolPage

@Composable
fun BodyRoot(sharePreferences: SharedPreferences, navHostController: NavHostController, modifier: Modifier){
    NavHost(navHostController, startDestination = "login") {
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
        composable("intro") {
            IntroScreen(navHostController)
        }
        composable("login") {
            LoginScreen(
                navHostController= navHostController,
                sharedPreferences = sharePreferences,
                onRegisterClick = { navHostController.navigate("register") },
                onForgotPasswordClick = { navHostController.navigate("forgot_password") }
            )
        }
        composable("forgot_password") {
            ForgotPasswordScreen(
                onBackClick = { navHostController.popBackStack() },
                onPhoneOptionClick = { navHostController.navigate("phone_otp") },
                onEmailOptionClick = { navHostController.navigate("email_otp") }
            )
        }

        composable("phone_otp") {
            EnterPhoneNumberScreen(onBackClick = { navHostController.popBackStack() },
                onContinueClick = {navHostController.navigate("enter_otp")})
        }
        composable("email_otp") {
            EnterEmailScreen(onBackClick = { navHostController.popBackStack() },
                onContinueClick = {navHostController.navigate("enter_otp")})
        }
        composable("enter_otp"){
            EnterOtpScreen(onBackClick = { navHostController.popBackStack() },
                onSubmitClick = {navHostController.navigate("sumit_otp")})

        }
        composable("sumit_otp"){
            ResetPasswordScreen(onBackClick= {navHostController.popBackStack()},)
            // thêm điều hướng cho nút
        }
        composable("com/example/itforum/register") {
            RegisterScreen(navHostController) // Màn hình đăng ký mới thêm
        }
        composable("otp") {
            OtpVerificationScreen(
                onBackClick = { navHostController.popBackStack() },
                onSubmitClick = { navHostController.navigate("success") }, //  Điều hướng sau khi xác thực thành công
                onResendClick = { /* xử lý gửi lại */ },
                onLoginClick = { navHostController.navigate("login") }
            )
        }
        composable("success") {
            RegistrationSuccessScreen(
                onLoginClick = { navHostController.navigate("login") }
            )
        }
        composable("myfeed"){
            MyFeedScreen(modifier)
        }
        composable("bookmark"){
            BookMarkScreen()
        }
        composable("follow"){
            FollowScreen()
        }
    }
}
