package com.example.itforum.user.root

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.itforum.admin.AdminRoot.AdminScreen
import com.example.itforum.user.home.HomePage
import com.example.itforum.user.home.bookmark.BookMarkScreen
import com.example.itforum.user.home.follow.FollowScreen
import com.example.itforum.user.intro.IntroScreen

import com.example.itforum.user.login.otp.EnterOtpScreen
import com.example.itforum.user.login.EnterPhoneNumberScreen
import com.example.itforum.user.login.otp.ForgotPasswordScreen
import com.example.itforum.user.login.LoginScreen
import com.example.itforum.user.login.otp.ResetPasswordScreen
import com.example.itforum.user.notification.DetailNotify
import com.example.itforum.user.home.myfeed.MyFeedScreen
import com.example.itforum.user.notification.NotificationPage
import com.example.itforum.user.post.CreatePostPage
import com.example.itforum.user.post.DetailPostPage
import com.example.itforum.user.post.ListLikePage
import com.example.itforum.user.userProfile.EditProfile

import com.example.itforum.user.register.OtpVerificationScreen
import com.example.itforum.user.register.RegisterScreen
import com.example.itforum.user.register.RegistrationSuccessScreen
import com.example.itforum.user.tool.ToolPage

import com.example.itforum.utilities.SearchScreen
import com.example.itforum.utilities.note.NotesApp


import com.example.itforum.admin.adminComplaint.ManagementComplaintDetailScreen
import com.example.itforum.admin.adminComplaint.ManagementComplaintScreen
import com.example.itforum.admin.adminCrashlytic.UserSession.email
import com.example.itforum.service.AuthRepository
import com.example.itforum.user.Analytics.logScreenEnter
import com.example.itforum.user.Analytics.logScreenExit
import com.example.itforum.user.ReportAccount.view.CreateReportAccountScreen
import com.example.itforum.user.ReportPost.view.ReportPostDialog

import com.example.itforum.user.complaint.ComplaintPage
import com.example.itforum.user.home.tag.TagScreen
import com.example.itforum.user.home.tag.ViewModel.TagViewModel
import com.example.itforum.user.news.DetailNewsPage
import com.example.itforum.user.post.CommentDialogWrapper
import com.example.itforum.user.post.ConfirmDeleteDialog
import com.example.itforum.user.post.OptionDialog
import com.example.itforum.user.post.PostCommentScreen
import com.example.itforum.user.post.viewmodel.CommentViewModel
import com.example.itforum.user.post.viewmodel.PostViewModel
import com.example.itforum.user.userProfile.OtherUserProfileScreen
import com.example.itforum.user.userProfile.UserProfileScreen
import com.example.itforum.user.setting.Setting
import com.example.itforum.user.skeleton.SkeletonBox
import com.example.itforum.user.utilities.chat.ChatAIApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.example.itforum.user.utilities.search.SearchViewModel
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
        SkeletonBox()
    }
}

@Composable
fun StartRoot(navHostController: NavHostController, sharedPreferences: SharedPreferences) {
    NavHost(navHostController, startDestination = "splash") {
        composable("forgot_password") {

            val coroutineScope = rememberCoroutineScope()
            val context = LocalContext.current
            LaunchedEffect(Unit) {
                logScreenEnter(context, "enter_otp")
            }

            DisposableEffect(Unit) {
                onDispose {
                    logScreenExit(context, "enter_otp")
                }
            }


            ForgotPasswordScreen(
                onBackClick = { navHostController.popBackStack() },
                onEmailSubmitted = { email ->
                    coroutineScope.launch {
                        val result = AuthRepository.sendOtp(email)
                        if (result.isSuccess) {
                            navHostController.navigate("enter_otp?email=$email")
                        } else {
                            Toast.makeText(context, "Gửi OTP thất bại", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            )

        }

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

        composable("splash") {
            SplashScreen(navHostController, sharedPreferences)
        }

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


        composable(
            "enter_otp?email={email}",
            arguments = listOf(navArgument("email") { type = NavType.StringType })
        ) { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""

            val scope = rememberCoroutineScope()

            val context = LocalContext.current
            LaunchedEffect(Unit) {
                logScreenEnter(context, "enter_otp")
            }

            DisposableEffect(Unit) {
                onDispose {
                    logScreenExit(context, "enter_otp")
                }
            }

            EnterOtpScreen(
                onBackClick = { navHostController.popBackStack() },
                email = email,
                onOtpSubmitted = { otp ->
                    navHostController.navigate("reset_password_screen?email=$email&otp=$otp")
                },
                onResendClick = {
                    scope.launch {
                        try {
                            val result = AuthRepository.sendOtp(email)
                            if (result.isSuccess) {
                                Toast.makeText(context, "Mã OTP đã được gửi lại", Toast.LENGTH_SHORT).show()
                            } else {
                                val errorMsg = result.exceptionOrNull()?.message ?: "Không thể gửi lại OTP"
                                Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show()
                            }
                        } catch (e: Exception) {
                            Toast.makeText(context, "Lỗi khi gửi lại OTP: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }

            )
        }

        composable(
            "reset_password_screen?email={email}&otp={otp}",
            arguments = listOf(
                navArgument("email") { type = NavType.StringType },
                navArgument("otp") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val context = LocalContext.current
            val email = backStackEntry.arguments?.getString("email") ?: ""
            val otp = backStackEntry.arguments?.getString("otp") ?: ""

            val coroutineScope = rememberCoroutineScope()

            LaunchedEffect(Unit) {
                logScreenEnter(context, "enter_otp")
            }

            DisposableEffect(Unit) {
                onDispose {
                    logScreenExit(context, "enter_otp")
                }
            }
            ResetPasswordScreen(
                onBack = { navHostController.popBackStack() },
                email = email,
                otp = otp,
                onReset = { newPassword ->
                    coroutineScope.launch {
                        val result = withContext(Dispatchers.IO) {
                            AuthRepository.resetPassword(email, otp, newPassword)
                        }
                        result.onSuccess {
                            Toast.makeText(context, "Đặt lại mật khẩu thành công", Toast.LENGTH_SHORT).show()
                            navHostController.navigate("login") {
                                popUpTo("reset_password_screen") { inclusive = true }
                            }
                        }.onFailure {
                            Toast.makeText(context, "Thất bại: ${it.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            )

        }

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

        composable ("home") {
            return@composable
        }
        composable ("admin_root"){
            return@composable
        }

    }
}

@Composable
fun BodyRoot(sharedPreferences: SharedPreferences,
             navHostController: NavHostController,
             modifier: Modifier, onToggleTheme: () -> Unit,
             darkTheme: Boolean = false,
             role:String?
){
    var postViewModel: PostViewModel = viewModel(factory = viewModelFactory {
        initializer { PostViewModel(sharedPreferences) }
    })
    var commentViewModel : CommentViewModel =  viewModel(factory = viewModelFactory {
        initializer { CommentViewModel(sharedPreferences) }})
    var tagViewModel : TagViewModel = viewModel(factory = viewModelFactory {
        initializer { TagViewModel() }})
    NavHost(navHostController, startDestination = "home") {
    val startDestination = if (role == "admin") {
        "admin_root"
    } else {
        "home"
    }
    println("role "+ role)
    NavHost(navHostController,
        startDestination = startDestination,
    ) {

        composable ("admin_root"){
            AdminScreen(sharedPreferences)
        }
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

            HomePage(navHostController, modifier, sharedPreferences, postViewModel,commentViewModel)
        }

        composable("tag") {
            TagScreen(tagViewModel)
        }
        composable("login"){
            return@composable
        }
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

            PostCommentScreen( postId,sharedPreferences,commentViewModel)
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

            NotificationPage(modifier, sharedPreferences,navHostController)
        }

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

            CreatePostPage(modifier, navHostController, sharedPreferences, postViewModel,tagViewModel)
        }

        composable("detail_post/{postId}") { backStackEntry ->
            val context = LocalContext.current


            LaunchedEffect(Unit) {
                logScreenEnter(context, "detail_post")
            }

            DisposableEffect(Unit) {
                onDispose {
                    logScreenExit(context, "detail_post")
                }
            }

            val postId = backStackEntry.arguments?.getString("postId") ?: return@composable
            var userId = sharedPreferences.getString("userId", null)
            var selectedPostId by remember { mutableStateOf<String?>(null) }
            var showReportDialog by remember { mutableStateOf(false) }

            var showOptionDialog by remember { mutableStateOf(false) }
            var showDeleteDialog by remember { mutableStateOf(false) }
            var selectedUserId by remember { mutableStateOf<String?>(null) }

            DetailPostPage(
                navHostController,
                sharedPreferences,
                postId,
                commentViewModel,
                onUpvoteClick = {
                    postViewModel.handleUpVote(
                        "upvote",
                        -1,
                        postId
                    )
                },
                onDownvoteClick = {
                    postViewModel.handleDownVote(
                        "downvote",
                        -1,
                        postId
                    )
                },
                onBookmarkClick = {
                    postViewModel.handleBookmark(
                            -1,
                        postId,
                        userId
                    )
                },
                onReportClick = {it ->
                    selectedUserId = it
                    selectedPostId = postId
                    showOptionDialog =  true
                },
            )
            if (showOptionDialog && selectedUserId!= null){
                OptionDialog(showOptionDialog,
                    onDismiss = {
                        showOptionDialog = false
                    },
                    onShowReport = {
                        showReportDialog = true
                        showOptionDialog = false
                    },
                    onDeletePost = {
                        showDeleteDialog = true
                        showOptionDialog =  false
                    },
                    isMyPost = (selectedUserId == userId ))
            }

            if (showReportDialog && selectedPostId != null) {
                ReportPostDialog(
                    sharedPreferences = sharedPreferences,
                    reportedPostId = selectedPostId!!,
                    onDismissRequest = {
                        showReportDialog = false
                        selectedPostId = null
                    }
                )
            }
            if (showDeleteDialog && selectedPostId!= null){
                ConfirmDeleteDialog(
                    showDialog = showDeleteDialog,
                    onDismiss = {
                        showDeleteDialog = false
                    },
                    onConfirm = {postViewModel.handleHidePost(postId = selectedPostId)
                        showDeleteDialog =  false}
                )
            }
        }

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



        composable("searchscreen") {
            val context = LocalContext.current

            LaunchedEffect(Unit) {
                logScreenEnter(context, "search_screen")
            }

            DisposableEffect(Unit) {
                onDispose {
                    logScreenExit(context, "search_screen")
                }
            }

            val viewModel: SearchViewModel = viewModel(factory = viewModelFactory {
                initializer { SearchViewModel(sharedPreferences) }
            })
            SearchScreen(
                modifier = modifier,
                viewModel = viewModel,
                postViewModel = postViewModel,
                navHostController,
                sharedPreferences,
                tagViewModel
            )
        }


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



        composable("report_account/{reportedUserId}") { backStackEntry ->
            val reportedUserId = backStackEntry.arguments?.getString("reportedUserId") ?: ""
            val reporterUserId = sharedPreferences.getString("userId", "") ?: ""

            CreateReportAccountScreen(
                reporterUserId = reporterUserId,
                reportedUserId = reportedUserId,
                onBack = { navHostController.popBackStack() }
            )
        }
    }
}
