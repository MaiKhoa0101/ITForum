package com.example.itforum.user.root

import android.content.SharedPreferences
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
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
import com.example.itforum.admin.adminAccount.AccountManagementScreen
import com.example.itforum.admin.adminController.ControllerManagerScreen
import com.example.itforum.admin.postManagement.PostManagementScreen
import com.example.itforum.admin.adminComplaint.ManagementComplaintDetailScreen
import com.example.itforum.admin.adminComplaint.ManagementComplaintScreen
import com.example.itforum.admin.adminReport.ReportPost.model.request.ReportedPost
import com.example.itforum.admin.adminReport.ReportPost.view.ReportedPostDetailScreen
import com.example.itforum.admin.adminReport.ReportPost.viewmodel.ReportedPostDetailViewModel
import com.example.itforum.user.Analytics.logScreenView
import com.example.itforum.user.complaint.ComplaintPage
import com.example.itforum.user.news.DetailNewsPage
import com.example.itforum.user.post.PostCommentScreen
import com.example.itforum.user.profile.OtherUserProfileScreen
import com.example.itforum.user.profile.UserProfileScreen
import com.example.itforum.user.setting.Setting
import com.example.itforum.user.utilities.chat.ChatAIApp
import kotlinx.coroutines.delay

@Composable
fun BodyRoot(sharedPreferences: SharedPreferences, navHostController: NavHostController, modifier: Modifier, onToggleTheme: () -> Unit, darkTheme: Boolean = false){
    NavHost(navHostController, startDestination = "login") {
        composable ("home") {
            val context = LocalContext.current
            LaunchedEffect(Unit) {
                logScreenView(context, "HomePage")
                delay(1200)
            }
            HomePage(navHostController,modifier,sharedPreferences)
        }
        // NavGraphBuilder
//        composable("comment/{postId}") { backStackEntry ->
//            val postId = backStackEntry.arguments?.getString("postId") ?: ""
//            PostCommentScreen(navHostController, postId,sharedPreferences)
//        }
        composable("comment/{postId}") { backStackEntry ->
            val postId = backStackEntry.arguments?.getString("postId") ?: ""

            val context = LocalContext.current
            LaunchedEffect(postId) {
                logScreenView(context, "comment_post")
                delay(1200)
            }

            PostCommentScreen(navHostController, postId, sharedPreferences)
        }

        composable ("notification") {
            val context = LocalContext.current
            LaunchedEffect(Unit) {
                logScreenView(context, "NotificationPage")
                delay(1200)
            }
            NotificationPage(modifier, navHostController)
        }
//        composable("chat") {
//            ChatAIApp(onExitToHome = {
//                navHostController.navigate("home") {
//                    popUpTo("home") { inclusive = false }
//                    launchSingleTop = true
//                }
//            })
//        }
        composable("chat") {
            val context = LocalContext.current
            LaunchedEffect(Unit) {
                logScreenView(context, "chat")
                delay(1200)
            }
            ChatAIApp(
                onExitToHome = {
                    navHostController.navigate("home") {
                        popUpTo("home") { inclusive = false }
                        launchSingleTop = true
                    }
                }
            )
        }

//        composable("note") {
//            NotesApp(
//                onBackToHome = {
//                    navHostController.navigate("home") {
//                        popUpTo("home") { inclusive = false }
//                        launchSingleTop = true
//                    }
//                }
//            )
//        }
        composable("note") {
            val context = LocalContext.current
            LaunchedEffect(Unit) {
                logScreenView(context, "note")
                delay(1200)
            }

            NotesApp(
                onBackToHome = {
                    navHostController.navigate("home") {
                        popUpTo("home") { inclusive = false }
                        launchSingleTop = true
                    }
                }
            )
        }

//        composable ("detail_notify") {
//            DetailNotify(modifier, navHostController)
//        }
        composable("detail_notify") {
            val context = LocalContext.current
            LaunchedEffect(Unit) {
                logScreenView(context, "detail_notify")
            }

            DetailNotify(modifier, navHostController)
        }

//        composable ("tool") {
//            ToolPage(modifier)
//        }
        composable("tool") {
            val context = LocalContext.current
            LaunchedEffect(Unit) {
                logScreenView(context, "tool")
            }

            ToolPage(modifier)
        }

//        composable ("personal") {
//            UserProfileScreen(sharedPreferences, navHostController)
//        }
        composable("personal") {
            val context = LocalContext.current
            LaunchedEffect(Unit) {
                logScreenView(context, "personal")
            }

            UserProfileScreen(sharedPreferences, navHostController)
        }

//        composable ("otherprofile") {
//            OtherUserProfileScreen(sharedPreferences, navHostController,modifier)
//        }
        composable("otherprofile") {
            val context = LocalContext.current
            LaunchedEffect(Unit) {
                logScreenView(context, "other_profile")
            }

            OtherUserProfileScreen(sharedPreferences, navHostController, modifier)
        }

//        composable ("editprofile") {
//            EditProfile(modifier,sharedPreferences,navHostController)
//        }
        composable("editprofile") {
            val context = LocalContext.current
            LaunchedEffect(Unit) {
                logScreenView(context, "edit_profile")
            }

            EditProfile(modifier, sharedPreferences, navHostController)
        }

        composable("create_post") {
            val context = LocalContext.current
            LaunchedEffect(Unit) {
                logScreenView(context, "CreatePostPage")
            }
            CreatePostPage(modifier, navHostController, sharedPreferences)
        }
//        composable("detail_post"){
//            DetailPostPage(navHostController)
//        }
        composable("detail_post") {
            val context = LocalContext.current
            LaunchedEffect(Unit) {
                logScreenView(context, "detail_post")
            }

            DetailPostPage(navHostController)
        }

//        composable("listlike") {
//            ListLikePage(navHostController)
//        }
        composable("listlike") {
            val context = LocalContext.current
            LaunchedEffect(Unit) {
                logScreenView(context, "list_like")
            }

            ListLikePage(navHostController)
        }

//        composable("intro") {
//            IntroScreen(navHostController)
//        }
        composable("intro") {
            val context = LocalContext.current
            LaunchedEffect(Unit) {
                logScreenView(context, "intro")
            }

            IntroScreen(navHostController)
        }

//        composable("login") {
//            LoginScreen(
//                navHostController = navHostController,
//                sharedPreferences = sharedPreferences,
//                onRegisterClick = { navHostController.navigate("register") },
//                onForgotPasswordClick = { navHostController.navigate("forgot_password") },
//            )
//        }
        composable("login") {
            val context = LocalContext.current
            LaunchedEffect(Unit) {
                logScreenView(context, "login")
            }

            LoginScreen(
                navHostController = navHostController,
                sharedPreferences = sharedPreferences,
                onRegisterClick = { navHostController.navigate("register") },
                onForgotPasswordClick = { navHostController.navigate("forgot_password") },
            )
        }

//        composable ("settings"){
//            val context = LocalContext.current
//            LaunchedEffect(Unit) {
//                logScreenView(context, "Settings")
//            }
//            Setting(navHostController, onToggleTheme = onToggleTheme, darkTheme = darkTheme)
//        }
        composable("settings") {
            val context = LocalContext.current
            LaunchedEffect(Unit) {
                logScreenView(context, "Settings")
            }
            Setting(navHostController, onToggleTheme = onToggleTheme, darkTheme = darkTheme)
        }

//        composable("forgot_password") {
//            ForgotPasswordScreen(
//                onBackClick = { navHostController.popBackStack() },
//                onPhoneOptionClick = { navHostController.navigate("phone_otp") },
//                onEmailOptionClick = { navHostController.navigate("email_otp") }
//            )
//        }
        composable("forgot_password") {
            val context = LocalContext.current
            LaunchedEffect(Unit) {
                logScreenView(context, "forgot_password")
            }

            ForgotPasswordScreen(
                onBackClick = { navHostController.popBackStack() },
                onPhoneOptionClick = { navHostController.navigate("phone_otp") },
                onEmailOptionClick = { navHostController.navigate("email_otp") }
            )
        }


//        composable("phone_otp") {
//            EnterPhoneNumberScreen(onBackClick = { navHostController.popBackStack() },
//                onContinueClick = {navHostController.navigate("enter_otp")})
//        }
        composable("phone_otp") {
            val context = LocalContext.current
            LaunchedEffect(Unit) {
                logScreenView(context, "phone_otp")
            }

            EnterPhoneNumberScreen(
                onBackClick = { navHostController.popBackStack() },
                onContinueClick = { navHostController.navigate("enter_otp") }
            )
        }

//        composable("email_otp") {
//            EnterEmailScreen(onBackClick = { navHostController.popBackStack() },
//                onContinueClick = {navHostController.navigate("enter_otp")})
//        }
        composable("email_otp") {
            val context = LocalContext.current
            LaunchedEffect(Unit) {
                logScreenView(context, "email_otp")
            }

            EnterEmailScreen(
                onBackClick = { navHostController.popBackStack() },
                onContinueClick = { navHostController.navigate("enter_otp") }
            )
        }

//        composable("enter_otp"){
//            EnterOtpScreen(onBackClick = { navHostController.popBackStack() },
//                onSubmitClick = {navHostController.navigate("sumit_otp")})
//
//        }
        composable("enter_otp") {
            val context = LocalContext.current
            LaunchedEffect(Unit) {
                logScreenView(context, "enter_otp")
            }

            EnterOtpScreen(
                onBackClick = { navHostController.popBackStack() },
                onSubmitClick = { navHostController.navigate("sumit_otp") }
            )
        }

//        composable("sumit_otp"){
//            ResetPasswordScreen(onBackClick= {navHostController.popBackStack()},)
//            // thêm điều hướng cho nút
//        }
        composable("sumit_otp") {
            val context = LocalContext.current
            LaunchedEffect(Unit) {
                logScreenView(context, "reset_password")
                delay(1200)
            }

            ResetPasswordScreen(
                onBackClick = { navHostController.popBackStack() }
            )
            // thêm điều hướng cho nút nếu cần
        }


//        composable("register") {
//            RegisterScreen(
//                navHostController,
//                sharedPreferences=sharedPreferences,
//            ) // Màn hình đăng ký mới thêm
//        }
        composable("register") {
            val context = LocalContext.current
            LaunchedEffect(Unit) {
                logScreenView(context, "register")
                delay(1200)
            }

            RegisterScreen(
                navHostController,
                sharedPreferences = sharedPreferences,
            )
        }


//        composable("otp") {
//            OtpVerificationScreen(
//                onBackClick = { navHostController.popBackStack() },
//                onSubmitClick = { navHostController.navigate("success") }, //  Điều hướng sau khi xác thực thành công
//                onResendClick = { /* xử lý gửi lại */ },
//                onLoginClick = { navHostController.navigate("login") }
//            )
//        }
        composable("otp") {
            val context = LocalContext.current
            LaunchedEffect(Unit) {
                logScreenView(context, "otp_verification")
                delay(1200)

            }

            OtpVerificationScreen(
                onBackClick = { navHostController.popBackStack() },
                onSubmitClick = { navHostController.navigate("success") }, // Điều hướng sau khi xác thực thành công
                onResendClick = { /* xử lý gửi lại */ },
                onLoginClick = { navHostController.navigate("login") }
            )
        }

//        composable("success") {
//            RegistrationSuccessScreen(
//                onLoginClick = { navHostController.navigate("login") }
//            )
//        }
        composable("success") {
            val context = LocalContext.current
            LaunchedEffect(Unit) {
                logScreenView(context, "registration_success")
                delay(1200)
            }

            RegistrationSuccessScreen(
                onLoginClick = { navHostController.navigate("login") }
            )
        }

//        composable("myfeed"){
//            MyFeedScreen(modifier)
//        }
        composable("myfeed") {
            val context = LocalContext.current
            LaunchedEffect(Unit) {
                logScreenView(context, "my_feed")
                delay(1200)
            }

            MyFeedScreen(modifier)
        }

//        composable("bookmark"){
//            BookMarkScreen(navHostController,sharedPreferences)
//        }
        composable("bookmark") {
            val context = LocalContext.current
            LaunchedEffect(Unit) {
                logScreenView(context, "bookmark")
                delay(1200)
            }

            BookMarkScreen(navHostController, sharedPreferences)
        }

//        composable("follow"){
//            FollowScreen()
//        }
        composable("follow") {
            val context = LocalContext.current
            LaunchedEffect(Unit) {
                logScreenView(context, "follow")
                delay(1200)
            }

            FollowScreen()
        }

//        composable ("searchscreen"){
//            SearchScreen(modifier)
//        }
        composable("searchscreen") {
            val context = LocalContext.current
            LaunchedEffect(Unit) {
                logScreenView(context, "search_screen")
                delay(1200)
            }

            SearchScreen(modifier)
        }

//        composable("detail_news/{newsId}") { backStackEntry ->
//            val newsId = backStackEntry.arguments?.getString("newsId")
//            if (newsId != null) {
//                DetailNewsPage(newsId,modifier,navHostController, sharedPreferences)
//            }
//        }
//        composable("detail_news/{newsId}") { backStackEntry ->
//            val newsId = backStackEntry.arguments?.getString("newsId")
//            if (newsId != null) {
//                DetailNewsPage(newsId, modifier, navHostController, sharedPreferences)
//            }
//        }
        composable("detail_news/{newsId}") { backStackEntry ->
            val newsId = backStackEntry.arguments?.getString("newsId")
            val context = LocalContext.current

            if (newsId != null) {

                LaunchedEffect(newsId) {
                    logScreenView(context, "detail_news")
                    delay(1200)
                }

//                DetailNewsPage(newsId, modifier, navHostController, sharedPreferences)

                 DetailNewsPage(newsId, navHostController, sharedPreferences)

            }
        }


//        composable("complaint") {
//                ComplaintPage(navHostController, sharedPreferences)
//        }
        composable("complaint") {
            val context = LocalContext.current
            LaunchedEffect(Unit) {
                logScreenView(context, "complaint")
            }

            ComplaintPage(navHostController, sharedPreferences)
        }

//        composable("account_detail/{accountId}") { backStackEntry ->
//            val accountId = backStackEntry.arguments?.getString("accountId")?.toIntOrNull()
//            if (accountId != null) {
//                AccountDetailScreen(accountId)
//            } else {
//                Text("Không tìm thấy tài khoản.")
//            }
//        }
        composable("account_detail/{accountId}") { backStackEntry ->
            val accountId = backStackEntry.arguments?.getString("accountId")?.toIntOrNull()
            val context = LocalContext.current

            if (accountId != null) {
                LaunchedEffect(accountId) {
                    logScreenView(context, "account_detail")
                }

                AccountDetailScreen(accountId)
            } else {
                Text("Không tìm thấy tài khoản.")
            }
        }


//        composable("detail_news/{newsId}") { backStackEntry ->
//            val newsId = backStackEntry.arguments?.getString("newsId")
//            if (newsId != null) {
//                DetailNewsPage(newsId, modifier, navHostController, sharedPreferences)
//            }
//        }
        composable("manager_complaint"){
            ManagementComplaintScreen(navHostController,sharedPreferences)
        }
        composable("complaint_detail/{complaintId}"){ backStackEntry ->
            val complaintId = backStackEntry.arguments?.getString("complaintId")
            if (complaintId != null) {
                ManagementComplaintDetailScreen(navHostController,sharedPreferences,complaintId)
            } else {
                Text("Không tìm thấy khiếu nại.")
            }
        }


        composable ("admin_root"){
            AdminScreen(sharedPreferences)
        }


    }
}
