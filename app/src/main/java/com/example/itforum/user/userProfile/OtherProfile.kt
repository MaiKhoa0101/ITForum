package com.example.itforum.user.userProfile

import android.content.SharedPreferences
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
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
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavHostController
import com.example.itforum.admin.adminCrashlytic.UserSession.userId
import com.example.itforum.user.modelData.request.GetPostRequest

import com.example.itforum.user.modelData.response.UserProfileResponse
import com.example.itforum.user.post.PostListScreen
import com.example.itforum.user.post.viewmodel.CommentViewModel
import com.example.itforum.user.post.viewmodel.PostViewModel
import com.example.itforum.user.userProfile.viewmodel.UserViewModel
import com.google.android.play.integrity.internal.u

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OtherUserProfileScreen(
    sharedPreferences: SharedPreferences,
    navHostController: NavHostController,
    modifier: Modifier = Modifier,
    otherUserId: String
) {
    val viewModel: UserViewModel = viewModel(factory = viewModelFactory {
        initializer { UserViewModel(sharedPreferences) }
    })
    var postViewModel: PostViewModel = viewModel(factory = viewModelFactory {
        initializer { PostViewModel(sharedPreferences) }
    })
    var commentViewModel : CommentViewModel =  viewModel(factory = viewModelFactory {
        initializer { CommentViewModel(sharedPreferences) }})
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("Thông tin", "Bài viết")
    val user by viewModel.user.collectAsState()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())

    LaunchedEffect(Unit) {
        viewModel.getUser(otherUserId)
    }

    Scaffold(
//        modifier = modifier
////            .fillMaxSize()
////            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = {
                    Box(
//                        modifier = Modifier
//                            .fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,

                            ) {
                            Text("Hồ sơ")
                        }
                    }
                },
                navigationIcon = {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White,
                        modifier = Modifier
                            .size(40.dp)
                            .clickable { navHostController.popBackStack()}
                    )
                },
                actions = {
                    Icon(
                        imageVector = Icons.Default.MoreHoriz,
                        contentDescription = "more",
                        modifier = Modifier
                            .size(40.dp)
                            .clickable { navHostController.navigate("report_account/${otherUserId}") }
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                scrollBehavior = scrollBehavior
            )
        }
    ) { innerPadding ->
        OtherProfileContent(
            user = user,
            selectedTabIndex = selectedTabIndex,
            onTabSelected = { selectedTabIndex = it },
            navController = navHostController,
            modifier = Modifier.padding(innerPadding),
            tabs = tabs,
            sharedPreferences = sharedPreferences,
            postViewModel,
            commentViewModel

        )
    }
}

@Composable
fun OtherProfileContent(
    user: UserProfileResponse?,
    selectedTabIndex: Int,
    onTabSelected: (Int) -> Unit,
    navController: NavHostController,
    modifier: Modifier = Modifier,
    tabs: List<String>,
    sharedPreferences: SharedPreferences,
    postViewModel: PostViewModel,
    commentViewModel: CommentViewModel
) {
    Column(modifier = modifier.fillMaxSize()) {
        UserHeader(user)

        UserTabRow(
            tabs = tabs,
            selectedTabIndex = selectedTabIndex,
            onTabSelected = onTabSelected
        )

        when (selectedTabIndex) {
            0 -> {
                OtherInfoOverview()
                UserInfoDetail(user, modifier)
            }
            1 -> {
                if (user != null) {
                    PostListScreen(
                        sharedPreferences,
                        navController,
                        GetPostRequest(
                            page = 1,
                            userId = user.id
                        ),
                        postViewModel = postViewModel,
                        commentViewModel = commentViewModel
                    )
                }
            }
        }
    }
}



@Composable
fun OtherInfoOverview() {
    Column(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(20.dp))
        Text("Chi tiết Thông tin", fontWeight = FontWeight.Bold, fontSize = 20.sp)
        Spacer(modifier = Modifier.height(10.dp))
        Divider(thickness = 1.dp)
    }
}