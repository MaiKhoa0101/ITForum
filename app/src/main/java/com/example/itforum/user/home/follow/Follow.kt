package com.example.itforum.user.home.follow

import Follower
import GetFollowerResponse
import android.content.SharedPreferences
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.itforum.R
import com.example.itforum.user.post.viewmodel.PostViewModel
import com.example.itforum.user.skeleton.SkeletonBox
import kotlinx.coroutines.launch

@Composable
fun FollowScreen(
    navHostController: NavHostController,
    sharedPreferences: SharedPreferences,

) {
    val viewModel: FollowViewModel = viewModel(factory = viewModelFactory {
        initializer { FollowViewModel(navHostController, sharedPreferences) }
    })
    var userId = sharedPreferences.getString("userId", null)
    val followerData by viewModel.followerData
    val randomUsers by viewModel.randomUsers
    val isLoadingFollowers by viewModel.isLoadingFollowers
    val isLoadingRandomUsers by viewModel.isLoadingRandomUsers
    val followersError by viewModel.followersError
    val randomUsersError by viewModel.randomUsersError

    val coroutineScope = rememberCoroutineScope()
    var currentUserId = userId.toString()

    // Load data when screen is first displayed
    LaunchedEffect(currentUserId) {
        viewModel.loadFollowerData(currentUserId.toString())
        viewModel.loadRandomUsers(currentUserId, "10")
    }

    LazyColumn (modifier = Modifier.padding(top = 40.dp)){

        item {
            Spacer(Modifier.height(160.dp))
            Text(
                "Người đang theo dõi",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(8.dp),
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        // Display followers user
        item {
            when {
                isLoadingFollowers -> {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        SkeletonBox()
                    }
                }
                followersError != null -> {
                    Text(
                        text = "Error loading followers: $followersError",
                        color = Color.Red,
                        modifier = Modifier.padding(8.dp)
                    )
                }
                followerData?.followers?.isNotEmpty() == true -> {
                    Column {
                        followerData!!.followers.forEach { follower ->
                            FollowerWidget(
                                follower = follower,
                                isFollowing = true,
                                onFollowClick = {
                                    coroutineScope.launch {
                                        val result = viewModel.followUser(currentUserId, follower._id)
                                        if (result != null) {
                                            viewModel.loadFollowerData(currentUserId)
                                            viewModel.loadRandomUsers(currentUserId, "10")
                                        }
                                    }
                                }
                            )
                        }
                    }
                }
                else -> {
                    Text(
                        text = "Không có người theo dõi",
                        modifier = Modifier.padding(8.dp),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "Gợi ý",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(8.dp),
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        // Suggest user
        item {
            when {
                isLoadingRandomUsers -> {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        SkeletonBox()
                    }
                }
                randomUsersError != null -> {
                    Text(
                        text = "Error loading suggestions: $randomUsersError",
                        color = Color.Red,
                        modifier = Modifier.padding(8.dp)
                    )
                }
                randomUsers?.followers?.isNotEmpty() == true -> {
                    Column {
                        randomUsers!!.followers.forEach { user ->
                            FollowerWidget(
                                follower = user,
                                isFollowing = false,
                                onFollowClick = {
                                    coroutineScope.launch {
                                        val result = viewModel.followUser(currentUserId, user._id)
                                        if (result != null) {
                                            viewModel.loadFollowerData(currentUserId)
                                            viewModel.loadRandomUsers(currentUserId, "10")
                                        }
                                    }
                                }
                            )
                        }
                    }
                }
                else -> {
                    Text(
                        text = "No suggestions available",
                        modifier = Modifier.padding(8.dp),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        }

//        item {
//            Spacer(modifier = Modifier.height(16.dp))
//            Text(
//                "Posts from your following",
//                fontSize = 20.sp,
//                fontWeight = FontWeight.Bold,
//                modifier = Modifier.padding(8.dp)
//            )
//            Spacer(modifier = Modifier.height(16.dp))
//        }
    }
}

@Composable
fun FollowerWidget(
    follower: Follower,
    isFollowing: Boolean,
    onFollowClick: () -> Unit
) {
    val btnColor = if (!isFollowing) Color.Green else Color.Red
    val btnText = if (!isFollowing) "Follow" else "Unfollow"
    val textColor = if (!isFollowing) Color.White else Color.Black

    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .padding(horizontal = 20.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {

            AsyncImage(
                model = follower.avatar,
                contentDescription = "avatar",
                modifier = Modifier.size(40.dp).clip(RoundedCornerShape(20.dp)),
                placeholder = painterResource(id = R.drawable.avatar),
                error = painterResource(id = R.drawable.avatar)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = follower.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }

        Button(
            onClick = onFollowClick,
            modifier = Modifier
                .width(90.dp)
                .height(30.dp),
            colors = ButtonDefaults.buttonColors(btnColor)
        ) {
            Text(text = btnText, fontSize = 12.sp, color = textColor)
        }
    }
}