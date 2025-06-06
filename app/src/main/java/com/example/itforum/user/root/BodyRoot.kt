package com.example.itforum.user.root

import android.content.SharedPreferences
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.itforum.admin.AdminRoot.AdminScreen
import com.example.itforum.user.home.HomePage
import com.example.itforum.user.home.bookmark.BookMarkScreen
import com.example.itforum.user.home.follow.FollowScreen
import com.example.itforum.user.intro.IntroScreen
import com.example.itforum.user.login.EnterEmailScreen
import com.example.itforum.user.login.EnterOtpScreen
import com.example.itforum.user.login.EnterPhoneNumberScreen
import com.example.itforum.user.login.ForgotPasswordScreen
import com.example.itforum.user.login.LoginScreen
import com.example.itforum.user.login.ResetPasswordScreen
import com.example.itforum.user.notification.DetailNotify
import com.example.itforum.user.home.myfeed.MyFeedScreen
import com.example.itforum.user.notification.NotificationPage
import com.example.itforum.user.post.CreatePostPage
import com.example.itforum.user.post.DetailPostPage
import com.example.itforum.user.post.ListLikePage
import com.example.itforum.user.profile.EditProfile

import com.example.itforum.user.register.OtpVerificationScreen
import com.example.itforum.user.register.RegisterScreen
import com.example.itforum.user.register.RegistrationSuccessScreen
import com.example.itforum.user.tool.ToolPage

import com.example.itforum.utilities.SearchScreen
import com.example.itforum.utilities.note.NotesApp


import com.example.itforum.admin.adminAccount.AccountDetailScreen
<<<<<<< HEAD
import com.example.itforum.admin.adminAccount.AccountManagementScreen
import com.example.itforum.admin.adminController.ControllerManagerScreen
import com.example.itforum.admin.postManagement.PostManagementScreen
=======
import com.example.itforum.admin.adminComplaint.ManagementComplaintDetailScreen
import com.example.itforum.admin.adminComplaint.ManagementComplaintScreen
import com.example.itforum.admin.adminReport.ReportPost.model.request.ReportedPost
import com.example.itforum.admin.adminReport.ReportPost.view.ReportedPostDetailScreen
import com.example.itforum.admin.adminReport.ReportPost.viewmodel.ReportedPostDetailViewModel
import com.example.itforum.user.complaint.ComplaintPage
>>>>>>> 8722b6b3f52743fe4b18cd40ece20b93f5302e4c
import com.example.itforum.user.news.DetailNewsPage
import com.example.itforum.user.post.PostCommentScreen
import com.example.itforum.user.profile.OtherUserProfileScreen
import com.example.itforum.user.profile.UserProfileScreen
import com.example.itforum.user.setting.Setting
import com.example.itforum.user.utilities.chat.ChatAIApp

@Composable
fun BodyRoot(sharedPreferences: SharedPreferences, navHostController: NavHostController, modifier: Modifier, onToggleTheme: () -> Unit, darkTheme: Boolean = false){
    NavHost(navHostController, startDestination = "login") {
        composable ("home") {
            HomePage(navHostController,modifier,sharedPreferences)
        }
        // NavGraphBuilder
        composable("comment/{postId}") { backStackEntry ->
            val postId = backStackEntry.arguments?.getString("postId") ?: ""
            PostCommentScreen(navHostController, postId,sharedPreferences)
        }
        composable ("notification") {
            NotificationPage(modifier, navHostController)
        }
        composable("chat") {
            ChatAIApp(onExitToHome = {
                navHostController.navigate("home") {
                    popUpTo("home") { inclusive = false }
                    launchSingleTop = true
                }
            })
        }
        composable("note") {
            NotesApp(
                onBackToHome = {
                    navHostController.navigate("home") {
                        popUpTo("home") { inclusive = false }
                        launchSingleTop = true
                    }
                }
            )
        }
        composable ("detail_notify") {
            DetailNotify(modifier, navHostController)
        }
        composable ("tool") {
            ToolPage(modifier)
        }
        composable ("personal") {
            UserProfileScreen(sharedPreferences, navHostController)
        }
        composable ("otherprofile") {
            OtherUserProfileScreen(sharedPreferences, navHostController,modifier)
        }
        composable ("editprofile") {
            EditProfile(modifier,sharedPreferences,navHostController)
        }
        composable("create_post") {
            CreatePostPage(modifier, navHostController, sharedPreferences)
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
                navHostController = navHostController,
                sharedPreferences = sharedPreferences,
                onRegisterClick = { navHostController.navigate("register") },
                onForgotPasswordClick = { navHostController.navigate("forgot_password") },
            )
        }
        composable ("settings"){
            Setting(navHostController, onToggleTheme = onToggleTheme, darkTheme = darkTheme)
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

        composable("register") {
            RegisterScreen(
                navHostController,
                sharedPreferences=sharedPreferences,
            ) // Màn hình đăng ký mới thêm
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
            BookMarkScreen(navHostController,sharePreferences)
        }
        composable("follow"){
            FollowScreen()
        }
        composable ("searchscreen"){
            SearchScreen(modifier)
        }
        composable("detail_news/{newsId}") { backStackEntry ->
            val newsId = backStackEntry.arguments?.getString("newsId")
            if (newsId != null) {
                DetailNewsPage(newsId,modifier,navHostController, sharePreferences)
            }
        }
        composable("complaint") {
                ComplaintPage(navHostController, sharePreferences)
        }
        composable("account_detail/{accountId}") { backStackEntry ->
            val accountId = backStackEntry.arguments?.getString("accountId")?.toIntOrNull()
            if (accountId != null) {
                AccountDetailScreen(accountId)
            } else {
                Text("Không tìm thấy tài khoản.")
            }
        }
<<<<<<< HEAD
        composable("detail_news/{newsId}") { backStackEntry ->
            val newsId = backStackEntry.arguments?.getString("newsId")
            if (newsId != null) {
                DetailNewsPage(newsId,modifier,navHostController, sharedPreferences)
=======
        composable("manager_complaint"){
            ManagementComplaintScreen(navHostController,sharePreferences)
        }
        composable("complaint_detail/{complaintId}"){ backStackEntry ->
            val complaintId = backStackEntry.arguments?.getString("complaintId")
            if (complaintId != null) {
                ManagementComplaintDetailScreen(modifier,navHostController,sharePreferences,complaintId)
            } else {
                Text("Không tìm thấy khiếu nại.")
>>>>>>> 8722b6b3f52743fe4b18cd40ece20b93f5302e4c
            }
        }

        composable ("admin_root"){
            AdminScreen(sharedPreferences)
        }


    }
}
