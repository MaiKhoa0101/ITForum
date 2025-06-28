package com.example.itforum.user.userProfile

import android.content.SharedPreferences
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.itforum.user.ReportPost.view.CreateReportPostScreen

import com.example.itforum.user.modelData.request.GetPostRequest
import com.example.itforum.user.modelData.response.Certificate
import com.example.itforum.user.modelData.response.Skill
import com.example.itforum.user.modelData.response.UserProfileResponse
import com.example.itforum.user.post.PostListScreen
import com.example.itforum.user.post.viewmodel.CommentViewModel
import com.example.itforum.user.post.viewmodel.PostViewModel
import com.example.itforum.user.userProfile.viewmodel.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserProfileScreen(
    sharedPreferences: SharedPreferences,
    navHostController: NavHostController,
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
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())

    var showReportDeatil by remember { mutableStateOf(false) }
    var postId by remember { mutableStateOf("") }


    LaunchedEffect(Unit) {
        val loginType = sharedPreferences.getString("loginType", "") ?: ""
        Log.d("UserProfileScreen", "Login type: $loginType")
        if (loginType == "google") {

            viewModel.getUserFromFirestore()
        } else {

            viewModel.getUser()
        }
    }


    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                modifier = Modifier.fillMaxWidth(),
                title = {
                    Box(
                        modifier = Modifier
                            .fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,

                        ) {
                            Text("Trang cá nhân")
                        }
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = null,
                            modifier = Modifier
                                .size(70.dp)
                                .padding(horizontal = 20.dp)
                                .align(Alignment.CenterEnd)
                                .clickable {
                                    navHostController.navigate("settings")
                            }
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                scrollBehavior = scrollBehavior
            )
        }
    ) { innerPadding ->
        ProfileContent(
            user = user,
            selectedTabIndex = selectedTabIndex,
            onTabSelected = { selectedTabIndex = it },
            navController = navHostController,
            modifier = Modifier.padding(innerPadding),
            tabs = tabs,
            sharedPreferences = sharedPreferences,
            onReportClick = {
                postId = it
                showReportDeatil = true
            },
            postViewModel,
            commentViewModel
        )
    }
    if (showReportDeatil){
        CreateReportPostScreen (sharedPreferences, postId, onDismiss = {
            showReportDeatil = false
        })
    }
}

@Composable
fun ProfileContent(
    user: UserProfileResponse?,
    selectedTabIndex: Int,
    onTabSelected: (Int) -> Unit,
    navController: NavHostController,
    modifier: Modifier = Modifier,
    tabs: List<String>,
    sharedPreferences: SharedPreferences,
    onReportClick: (String) -> Unit,
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
                       postViewModel,
                       commentViewModel
                   )
               }
           }
       }
    }
}

@Composable
fun UserHeader(user: UserProfileResponse?) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primaryContainer)
            .padding(horizontal = 10.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(start = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = user?.avatar?:"https://static.vecteezy.com/system/resources/previews/009/292/244/non_2x/default-avatar-icon-of-social-media-user-vector.jpg",
                contentDescription = "Ảnh đại diện người dùng",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.width(50.dp))
            Column{
                Text(user?.username?:"Người dùng", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                Text(user?.email?:"hi@gmail.com")


                Text(
                    "Tham gia 5 năm trước",
                    fontStyle = FontStyle.Italic,
                    fontWeight = FontWeight.Light,
                    fontSize = 15.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
        UserStats(user)
        Spacer(modifier = Modifier.height(10.dp))
    }
}

@Composable
fun UserStats(user: UserProfileResponse?) {
    Column (
        modifier = Modifier.padding(start = 10.dp)
    ) {
        Text("Thống kê",
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp)
        Text((user?.numberPost.toString()?:"Chưa có") + " bài viết")
        Text((user?.numberComment.toString()?:"Không có")+" câu trả lời")
        Text("Được đánh giá 4.5 điểm")
        Text("Xếp hạng thứ 22 trong hệ thống")
    }
}

@Composable
fun UserTabRow(
    tabs: List<String>,
    selectedTabIndex: Int,
    onTabSelected: (Int) -> Unit
) {
    TabRow(
        selectedTabIndex = selectedTabIndex,
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        indicator = {} // không vẽ gì cả
    ) {
        tabs.forEachIndexed { index, title ->
            Tab(
                modifier = Modifier
                    .clip(RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp))
                    .background(
                    if (selectedTabIndex == index)
                        MaterialTheme.colorScheme.background
                    else{
                        MaterialTheme.colorScheme.primaryContainer}
                )
                ,
                selected = selectedTabIndex == index,
                onClick = { onTabSelected(index) },
                text = {
                    Text(
                        title,
                        color = MaterialTheme.colorScheme.onBackground,
                        fontWeight = FontWeight.Bold,
                    )
                }
            )
        }
    }
}


@Composable
fun UserInfoDetail(user: UserProfileResponse?, modifier: Modifier) {
    LazyColumn(
        modifier = modifier
            .padding(horizontal = 16.dp)
    ) {
        item { Text("Giới thiệu", fontWeight = FontWeight.Bold, fontSize = 20.sp)}
        item { Spacer(modifier = Modifier.height(16.dp))}
        item { Text(user?.introduce?:"Chưa có giới thiệu", fontSize = 18.sp)}
        item { Spacer(modifier = Modifier.height(16.dp))}

        item { TagCertificateSection(title = "Chứng chỉ bằng cấp đã đạt:", tags = user?.certificate)}

        item { Spacer(modifier = Modifier.height(16.dp))}

        item { TagSkillSection(title = "Ngôn ngữ sử dụng:", tags = user?.skill)}
        item { TagSkillSection(title = "Ngôn ngữ sử dụng:", tags = user?.skill)}
        item { TagSkillSection(title = "Ngôn ngữ sử dụng:", tags = user?.skill)}
        item { TagSkillSection(title = "Ngôn ngữ sử dụng:", tags = user?.skill)}
        item { TagSkillSection(title = "Ngôn ngữ sử dụng:", tags = user?.skill)}
        item { TagSkillSection(title = "Ngôn ngữ sử dụng:", tags = user?.skill)}
        item { TagSkillSection(title = "Ngôn ngữ sử dụng:", tags = user?.skill)}
        item { TagSkillSection(title = "Ngôn ngữ sử dụng:", tags = user?.skill)}
        item { TagSkillSection(title = "Ngôn ngữ sử dụng:", tags = user?.skill)}
    }
}


@Composable
fun TagSkillSection(
    title: String,
    tags: List<String>?
) {
    Column () {
        Text(
            text = title,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        FlowRow (
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            tags?.forEach { tag ->
                TagItem(text = tag)
            }
        }
    }
}

@Composable
fun TagCertificateSection(
    title: String,
    tags: List<Certificate>?
) {
    Column () {
        Text(
            text = title,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        FlowRow (
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            tags?.forEach { tag ->
                TagItem(text = tag.name)
            }
        }
    }
}

@Composable
fun TagItem(text: String) {
    Box(
        modifier = Modifier
            .background(
                color = Color.Cyan,
                shape = RoundedCornerShape(50)
            )
            .width(100.dp)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text = text, color = Color.DarkGray)
    }
}