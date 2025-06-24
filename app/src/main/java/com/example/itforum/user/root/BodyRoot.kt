package com.example.itforum.user.root

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.util.Base64
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
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


import com.example.itforum.admin.adminComplaint.ManagementComplaintDetailScreen
import com.example.itforum.admin.adminComplaint.ManagementComplaintScreen
import com.example.itforum.user.Analytics.logScreenEnter
import com.example.itforum.user.Analytics.logScreenExit
import com.example.itforum.user.ReportPost.view.CreateReportPostScreen

import com.example.itforum.user.complaint.ComplaintPage
import com.example.itforum.user.modelData.response.GetVoteResponse
import com.example.itforum.user.modelData.response.PostResponse
import com.example.itforum.user.news.DetailNewsPage
import com.example.itforum.user.post.PostCommentScreen
import com.example.itforum.user.post.viewmodel.PostViewModel
import com.example.itforum.user.profile.OtherUserProfileScreen
import com.example.itforum.user.profile.UserProfileScreen
import com.example.itforum.user.setting.Setting
import com.example.itforum.user.utilities.chat.ChatAIApp
import org.json.JSONObject

fun isTokenExpired(token: String): Boolean {
    return try {
        val parts = token.split(".")
        if (parts.size != 3) return true

        val payload = String(Base64.decode(parts[1], Base64.URL_SAFE))
        val json = JSONObject(payload)
        val exp = json.getLong("exp") // thời gian hết hạn (giây)
        val now = System.currentTimeMillis() / 1000 // hiện tại (giây)
        exp < now
    } catch (e: Exception) {
        true // lỗi -> coi như token không hợp lệ
    }
}
@SuppressLint("CommitPrefEdits")
@Composable
fun SplashScreen(
    navController: NavHostController,
    sharedPreferences: SharedPreferences
) {
    LaunchedEffect(Unit) {
        val token = sharedPreferences.getString("access_token", null)
        val role = sharedPreferences.getString("role", null)
        var destination = "login"
        if (token != null && !isTokenExpired(token)) {
            if (role != null) {
                destination = if (role == "admin") "admin_root" else "home"
                navController.navigate(destination) {
                    popUpTo("splash") { inclusive = true }
                }
            }
        } else {
            val remove = sharedPreferences.edit().remove("access_token")
        }
        Log.d("splash1",destination)
        navController.navigate(destination) {
            if(destination == "login"){
                Log.d("splash2",destination)
                popUpTo(0) { inclusive = true }  // xóa hết stack
                launchSingleTop = true           // tránh tạo bản sao nếu đã ở login
            }
            else popUpTo("splash") { inclusive = true }
        }
    }

    // Giao diện loading đơn giản
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}
@Composable
fun BodyRoot(sharedPreferences: SharedPreferences, navHostController: NavHostController, modifier: Modifier, onToggleTheme: () -> Unit, darkTheme: Boolean = false){
    var postViewModel: PostViewModel = viewModel(factory = viewModelFactory {
        initializer { PostViewModel(navHostController,sharedPreferences) }
    })
    NavHost(navHostController, startDestination = "splash") {
        composable ("home") {

            val context = LocalContext.current

            LaunchedEffect(Unit) {
                logScreenEnter(context, "HomePage") // gửi screen_view
            }

            DisposableEffect(Unit) {
                onDispose {
                    logScreenExit(context, "HomePage") // khi thoát màn hình
                }
            }

            HomePage(navHostController, modifier, sharedPreferences, postViewModel)
        }

        composable("splash") {
            SplashScreen(navHostController, sharedPreferences)
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
                logScreenEnter(context, "comment_post_$postId") // hoặc "comment_post"
            }

            DisposableEffect(postId) {
                onDispose {
                    logScreenExit(context, "comment_post_$postId") // hoặc "comment_post"
                }
            }

            PostCommentScreen( postId, sharedPreferences)
        }

        composable("notification") {
            val context = LocalContext.current

            LaunchedEffect(Unit) {
                logScreenEnter(context, "NotificationPage") // gửi sự kiện screen_view
            }

            DisposableEffect(Unit) {
                onDispose {
                    logScreenExit(context, "NotificationPage") // khi rời màn hình
                }
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
                logScreenEnter(context, "chat") // bắt đầu ghi nhận màn hình chat
            }

            DisposableEffect(Unit) {
                onDispose {
                    logScreenExit(context, "chat") // kết thúc và ghi thời gian ở lại
                }
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
                logScreenEnter(context, "note")
            }

            DisposableEffect(Unit) {
                onDispose {
                    logScreenExit(context, "note")
                }
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
                logScreenEnter(context, "detail_notify")
            }

            DisposableEffect(Unit) {
                onDispose {
                    logScreenExit(context, "detail_notify")
                }
            }

            DetailNotify(modifier, navHostController)
        }

//        composable ("tool") {
//            ToolPage(modifier)
//        }
        composable("tool") {
            val context = LocalContext.current

            LaunchedEffect(Unit) {
                logScreenEnter(context, "tool")
            }

            DisposableEffect(Unit) {
                onDispose {
                    logScreenExit(context, "tool")
                }
            }

            ToolPage(modifier)
        }


//        composable ("personal") {
//            UserProfileScreen(sharedPreferences, navHostController)
//        }
        composable("personal") {
            val context = LocalContext.current

            LaunchedEffect(Unit) {
                logScreenEnter(context, "personal") // Gửi sự kiện screen_view
            }

            DisposableEffect(Unit) {
                onDispose {
                    logScreenExit(context, "personal") // Gửi screen_exit kèm thời gian
                }
            }

            UserProfileScreen(sharedPreferences, navHostController)
        }


//        composable ("otherprofile") {
//            OtherUserProfileScreen(sharedPreferences, navHostController,modifier)
//        }
        composable("otherprofile/{userId}") { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId")
            val context = LocalContext.current
            if (userId != null) {
                LaunchedEffect(Unit) {
                    logScreenEnter(context, "other_profile") // Bắt đầu tracking khi vào màn
                }

                DisposableEffect(Unit) {
                    onDispose {
                        logScreenExit(context, "other_profile") // Ghi thời gian khi rời màn
                    }
                }

                OtherUserProfileScreen(sharedPreferences, navHostController, modifier, userId)
            }
        }



//        composable ("editprofile") {
//            EditProfile(modifier,sharedPreferences,navHostController)
//        }
        composable("editprofile") {
            val context = LocalContext.current

            LaunchedEffect(Unit) {
                logScreenEnter(context, "edit_profile") // Ghi nhận khi vào màn hình
            }

            DisposableEffect(Unit) {
                onDispose {
                    logScreenExit(context, "edit_profile") // Ghi nhận khi rời màn hình
                }
            }

            EditProfile(modifier, sharedPreferences, navHostController)
        }

        composable("create_post") {
            val context = LocalContext.current

            LaunchedEffect(Unit) {
                logScreenEnter(context, "CreatePostPage") // Gửi screen_view
            }

            DisposableEffect(Unit) {
                onDispose {
                    logScreenExit(context, "CreatePostPage") // Gửi thời gian ở lại
                }
            }

            CreatePostPage(modifier, navHostController, sharedPreferences, postViewModel)
        }

//        composable("detail_post"){
//            DetailPostPage(navHostController)
//        }
        composable("detail_post") {
            val context = LocalContext.current

            LaunchedEffect(Unit) {
                logScreenEnter(context, "detail_post") // Khi vào màn hình
            }

            DisposableEffect(Unit) {
                onDispose {
                    logScreenExit(context, "detail_post") // Khi thoát màn hình
                }
            }

            DetailPostPage(navHostController,sharedPreferences)
        }


//        composable("listlike") {
//            ListLikePage(navHostController)
//        }
        composable("listlike") {
            val context = LocalContext.current

            LaunchedEffect(Unit) {
                logScreenEnter(context, "list_like") // Khi người dùng vào màn hình
            }

            DisposableEffect(Unit) {
                onDispose {
                    logScreenExit(context, "list_like") // Khi người dùng rời khỏi màn hình
                }
            }

            ListLikePage(navHostController)
        }


//        composable("intro") {
//            IntroScreen(navHostController)
//        }
        composable("intro") {
            val context = LocalContext.current

            LaunchedEffect(Unit) {
                logScreenEnter(context, "intro") // Khi người dùng vào màn hình
            }

            DisposableEffect(Unit) {
                onDispose {
                    logScreenExit(context, "intro") // Khi người dùng rời khỏi màn hình
                }
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
                logScreenEnter(context, "login") // Ghi nhận khi vào màn hình
            }

            DisposableEffect(Unit) {
                onDispose {
                    logScreenExit(context, "login") // Ghi nhận khi thoát màn hình
                }
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
                logScreenEnter(context, "Settings") // Gửi sự kiện screen_view
            }


            DisposableEffect(Unit) {
                onDispose {
                    logScreenExit(context, "Settings") // Tính thời gian ở lại và gửi screen_exit
                }
            }

            

            Setting(navHostController, sharedPreferences, onToggleTheme = onToggleTheme, darkTheme = darkTheme)

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
                logScreenEnter(context, "forgot_password") // Gửi sự kiện screen_view
            }

            DisposableEffect(Unit) {
                onDispose {
                    logScreenExit(context, "forgot_password") // Gửi thời gian ở lại
                }
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
                logScreenEnter(context, "phone_otp") // Ghi nhận khi vào màn hình
            }

            DisposableEffect(Unit) {
                onDispose {
                    logScreenExit(context, "phone_otp") // Ghi nhận khi thoát màn hình
                }
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
                logScreenEnter(context, "email_otp") // Gửi screen_view
            }

            DisposableEffect(Unit) {
                onDispose {
                    logScreenExit(context, "email_otp") // Gửi screen_exit kèm thời gian ở lại
                }
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
                logScreenEnter(context, "enter_otp") // Gửi screen_view
            }

            DisposableEffect(Unit) {
                onDispose {
                    logScreenExit(context, "enter_otp") // Gửi thời gian ở lại (screen_exit)
                }
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
                logScreenEnter(context, "reset_password") // Gửi sự kiện screen_view
            }

            DisposableEffect(Unit) {
                onDispose {
                    logScreenExit(context, "reset_password") // Gửi thời gian ở lại
                }
            }

            ResetPasswordScreen(
                onBackClick = { navHostController.popBackStack() }
            )
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
                logScreenEnter(context, "register") // Gửi screen_view
            }

            DisposableEffect(Unit) {
                onDispose {
                    logScreenExit(context, "register") // Gửi thời gian ở lại (screen_exit)
                }
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
                logScreenEnter(context, "otp_verification") // Gửi screen_view khi vào màn hình
            }

            DisposableEffect(Unit) {
                onDispose {
                    logScreenExit(context, "otp_verification") // Gửi thời gian ở lại khi thoát màn
                }
            }

            OtpVerificationScreen(
                onBackClick = { navHostController.popBackStack() },
                onSubmitClick = { navHostController.navigate("success") },
                onResendClick = { /* xử lý gửi lại */ },
                onLoginClick = {
                    navHostController.navigate("login") {
                        popUpTo(0) { inclusive = true }  // xóa hết stack
                        launchSingleTop = true           // tránh tạo bản sao nếu đã ở login
                    }
                }
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
                logScreenEnter(context, "registration_success")
            }

            DisposableEffect(Unit) {
                onDispose {
                    logScreenExit(context, "registration_success")
                }
            }

            RegistrationSuccessScreen(
                onLoginClick = {
                    navHostController.navigate("login") {
                        popUpTo(0) { inclusive = true }  // xóa hết stack
                        launchSingleTop = true           // tránh tạo bản sao nếu đã ở login
                    }
                }
            )
        }


//        composable("myfeed"){
//            MyFeedScreen(modifier)
//        }
        composable("myfeed") {
            val context = LocalContext.current

            LaunchedEffect(Unit) {
                logScreenEnter(context, "my_feed")
            }

            DisposableEffect(Unit) {
                onDispose {
                    logScreenExit(context, "my_feed")
                }
            }

            MyFeedScreen(modifier)
        }

//        composable("bookmark"){
//            BookMarkScreen(navHostController,sharedPreferences)
//        }
        composable("bookmark") {
            val context = LocalContext.current

            LaunchedEffect(Unit) {
                logScreenEnter(context, "bookmark")
            }

            DisposableEffect(Unit) {
                onDispose {
                    logScreenExit(context, "bookmark")
                }
            }

            BookMarkScreen(navHostController, sharedPreferences)
        }
        composable("follow") {
            val context = LocalContext.current

            LaunchedEffect(Unit) {
                logScreenEnter(context, "follow") // Gửi screen_view
            }

            DisposableEffect(Unit) {
                onDispose {
                    logScreenExit(context, "follow") // Gửi screen_exit kèm thời gian ở lại
                }
            }

            FollowScreen(navHostController,sharedPreferences)
        }


//        composable ("searchscreen"){
//            SearchScreen(modifier)
//        }
        composable("searchscreen") {
            val context = LocalContext.current

            LaunchedEffect(Unit) {
                logScreenEnter(context, "search_screen") // Gửi screen_view
            }

            DisposableEffect(Unit) {
                onDispose {
                    logScreenExit(context, "search_screen") // Gửi thời gian ở lại khi rời màn
                }
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
                    logScreenEnter(context, "detail_news_$newsId") // Ghi nhận screen_view
                }

                DisposableEffect(newsId) {
                    onDispose {
                        logScreenExit(context, "detail_news_$newsId") // Ghi nhận thời gian ở lại
                    }
                }

                DetailNewsPage(newsId, navHostController, sharedPreferences)
            }
        }


//        composable("complaint") {
//                ComplaintPage(navHostController, sharedPreferences)
//        }
        composable("complaint") {
            val context = LocalContext.current

            LaunchedEffect(Unit) {
                logScreenEnter(context, "complaint") // Gửi screen_view khi vào màn
            }

            DisposableEffect(Unit) {
                onDispose {
                    logScreenExit(context, "complaint") // Gửi screen_exit kèm thời gian ở lại
                }
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
                    logScreenEnter(context, "account_detail_$accountId")
                }

                DisposableEffect(accountId) {
                    onDispose {
                        logScreenExit(context, "account_detail_$accountId")
                    }
                }



            }
        }



//        composable("detail_news/{newsId}") { backStackEntry ->
//            val newsId = backStackEntry.arguments?.getString("newsId")
//            if (newsId != null) {
//                DetailNewsPage(newsId, modifier, navHostController, sharedPreferences)
//            }
//        }
        composable("manager_complaint"){
            ManagementComplaintScreen(navHostController,sharedPreferences, modifier)
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
