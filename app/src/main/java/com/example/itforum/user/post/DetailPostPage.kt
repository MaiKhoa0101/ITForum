package com.example.itforum.user.post

import android.content.SharedPreferences
import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.ArrowCircleDown
import androidx.compose.material.icons.filled.ArrowCircleUp
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Comment
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.ThumbDown
import androidx.compose.material.icons.filled.ThumbDownOffAlt
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.filled.ThumbUpOffAlt
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.itforum.R
import com.example.itforum.animation.AnimatedVoteIcon
import com.example.itforum.animation.AnimatedVoteNumber

import com.example.itforum.user.modelData.response.GetVoteResponse
import com.example.itforum.user.post.viewmodel.CommentViewModel
import com.example.itforum.user.post.viewmodel.PostViewModel
import com.example.itforum.user.skeleton.SkeletonBox
import com.example.itforum.user.skeleton.SkeletonPost

@Composable
fun DetailPostPage(
    navHostController: NavHostController,
    sharedPreferences: SharedPreferences,
    postId: String,
    commentViewModel: CommentViewModel,
    onUpvoteClick: (String?) -> Unit = {},
    onDownvoteClick: (String?) -> Unit = {},
    onBookmarkClick: () -> Unit = {},
    onReportClick: (String) -> Unit = {}
) {


    val viewModel: PostViewModel = viewModel(factory = viewModelFactory {
        initializer { PostViewModel(sharedPreferences) }
    })

    val postWithVote by viewModel.selectedPostWithVote.collectAsState()

    var upvotes by remember { mutableStateOf(postWithVote?.vote?.data?.upVoteData?.total?: 0) }
    var isSavedPost by remember { mutableStateOf(postWithVote?.isBookMark) }

    var isVote by remember { mutableStateOf(postWithVote?.vote?.data?.userVote) }

    var isChangeUp by remember { mutableStateOf(false) }
    var isChangeDown by remember { mutableStateOf(false) }

    LaunchedEffect(postId) {
        viewModel.fetchPostById(postId)
    }

    if (postWithVote != null && postWithVote!!.post.isHidden == false) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.primaryContainer)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                TopDetailPost(navHostController)
                postWithVote?.post?.let { post ->
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight()
                            .background( MaterialTheme.colorScheme.secondaryContainer)
                    ) {
                        item {
                            Column(
                                modifier = Modifier
                                    .padding(20.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    // Avatar and author
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        AsyncImage(
                                            model = post.avatar,
                                            contentDescription = "avatar",
                                            modifier = Modifier
                                                .size(40.dp)
                                                .clip(RoundedCornerShape(20.dp))
                                                .clickable {
                                                    val myUserId =
                                                        sharedPreferences.getString("userId", null)
                                                    if (post.userId == myUserId)
                                                        navHostController.navigate("personal")
                                                    else navHostController.navigate("otherprofile/${post.userId}")
                                                }
                                        )
                                        Spacer(modifier = Modifier.width(12.dp))

                                        Column {
                                            Text(
                                                text = post.userName ?: "Unknown User",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 12.sp,
                                                color = MaterialTheme.colorScheme.onBackground,
                                                modifier = Modifier
                                                    .clickable {
                                                        val myUserId = sharedPreferences.getString(
                                                            "userId",
                                                            null
                                                        )
                                                        if (post.userId == myUserId)
                                                            navHostController.navigate("personal")
                                                        else navHostController.navigate("otherprofile/${post.userId}")
                                                    }
                                            )
                                            Text(
                                                text = "${getTimeAgo(post.createdAt ?: "")} • ${""}",
                                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                                fontSize = 11.sp
                                            )
                                        }
                                    }

                                    Row(
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        IconButton(onClick = {
                                            post.id?.let { onReportClick(postWithVote!!.post.userId.toString()) }
                                        }
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.MoreHoriz,
                                                contentDescription = "more",
                                                modifier = Modifier
                                                    .size(21.dp),
                                                tint = MaterialTheme.colorScheme.onSecondaryContainer
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                // Post title
                                Text(
                                    text = post.title ?: "",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                // Post content
                                Text(
                                    text = post.content ?: "",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Normal,
                                    color = MaterialTheme.colorScheme.onBackground
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                // media section
                                PostMediaSection(post.imageUrls, post.videoUrls)

                                // Action buttons row
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceAround,
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    // Upvote/Downvote section
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(22.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        IconButton(
                                            modifier = Modifier.size(60.dp),
                                            onClick = {
                                                if (isVote == "upvote") {
                                                    upvotes--
                                                    isVote = "none"
                                                } else if (isVote == "downvote" || isVote == "none") {
                                                    upvotes++
                                                    isVote = "upvote"
                                                }
                                                onUpvoteClick(isVote)
                                                isChangeUp = true
                                            }
                                        ) {
                                            AnimatedVoteIcon(
                                                isActive = isVote == "upvote",
                                                triggerChange = isChangeUp,
                                                activeColor = Color.Green,
                                                defaultColor = MaterialTheme.colorScheme.onBackground,
                                                icon = Icons.Default.ArrowCircleUp,
                                                onAnimationEnd = { isChangeUp = false }
                                            )
                                        }
                                        AnimatedVoteNumber(
                                            voteCount = upvotes,
                                            textStyle = TextStyle(
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.SemiBold
                                            ),
                                            textColor = MaterialTheme.colorScheme.onBackground
                                        )
                                        IconButton(
                                            modifier = Modifier.size(60.dp),
                                            onClick = {
                                                if (isVote == "downvote") {
                                                    isVote = "none"
                                                } else if (isVote == "upvote") {
                                                    upvotes--
                                                    isVote = "downvote"
                                                } else if (isVote == "none") {
                                                    isVote = "downvote"
                                                }
                                                onDownvoteClick(isVote)
                                                isChangeDown = true
                                            })
                                        {
                                            AnimatedVoteIcon(
                                                isActive = isVote == "downvote",
                                                triggerChange = isChangeDown,
                                                activeColor = Color.Red,
                                                defaultColor = MaterialTheme.colorScheme.onBackground,
                                                icon = Icons.Default.ArrowCircleDown,
                                                onAnimationEnd = { isChangeDown = false }
                                            )
                                        }
                                    }

                                    IconButton(onClick = {
                                        onBookmarkClick()
                                        //neu save post r doi icon qua chua save va nguoc lai
                                        if (isSavedPost == true) {
                                            isSavedPost = false
                                        } else {
                                            isSavedPost = true
                                        }
                                    }
                                    )
                                    {
                                        Icon(
                                            imageVector = Icons.Default.Bookmark,
                                            contentDescription = "Đánh dấu",
                                            modifier = Modifier.size(30.dp),
                                            tint = if (isSavedPost == true) Color.Green else MaterialTheme.colorScheme.onBackground
                                        )
                                    }
                                }
                                PostCommentScreen(
                                    postId = post.id.toString(),
                                    sharedPreferences = sharedPreferences,
                                    commentViewModel = commentViewModel,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
    else {
        Column (
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column (
                modifier = Modifier.fillMaxSize(0.8f).padding(vertical = 80.dp),
            ) {
                IconButton(
                    modifier = Modifier.size(30.dp),
                    onClick = { navHostController.popBackStack() }
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBackIosNew,
                        contentDescription = "Lùi",
                        modifier = Modifier.size(100.dp)
                    )
                }
                SkeletonPost()
            }
        }
    }
}


@Composable
fun TopDetailPost(
    navHostController: NavHostController
) {
    Spacer(modifier = Modifier.height(30.dp))
    Row(
        modifier = Modifier
            .padding(horizontal = 13.dp, vertical = 10.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = {
            navHostController.popBackStack()
        }
        ) {
            Icon(
                imageVector = Icons.Default.KeyboardArrowLeft,
                contentDescription = "Quay lại",
                tint = Color.White,
                modifier = Modifier.size(40.dp)
            )
        }

        Text(
            "Bài viết của bạn",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Icon(
            imageVector = Icons.Default.MoreVert,
            contentDescription = "More options post",
            tint = Color.White,
            modifier = Modifier.size(30.dp)
                .clickable {  }
        )
    }
}

@Composable
fun AvatarNameDetail(avatar: String, name: String, time: String) {

    Row(
        modifier = Modifier
            .padding(horizontal = 13.dp, vertical = 6.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = avatar,
            contentDescription = "Avatar tài khoản",
            modifier = Modifier.size(45.dp)
        )
        Spacer(modifier = Modifier.width(5.dp))
        Column(
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Text(
                text = name,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "${getTimeAgo(time)} • ${""}",
                fontSize = 12.sp,
            )
        }

    }
}

@Composable
fun ContentPost(title : String,content: String,tags : List<String>) {
    Column(
        modifier = Modifier.padding(start = 18.dp, end = 15.dp, bottom = 7.dp)
    ) {
        Text(
            text = title,
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = content,
            fontSize = 12.sp
        )
        TagPost(tags)
    }
}

@Composable
fun TagPost(tags: List<String>) {
    val tagText = tags.joinToString(" ") { "#$it" }
    Spacer(modifier = Modifier.padding(top = 10.dp))
    Text(
        text = tagText,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF007ACC)
    )
}

@Composable
fun MediaPost() {
    Row(
        modifier = Modifier.fillMaxWidth()
            .TopBorder()
            .BottomBorder()
    ) {
        Image(
            painter = painterResource(id = R.drawable.img_post),
            contentDescription = "",
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            contentScale = ContentScale.Crop
        )
    }
}

@Composable
fun VoteSection(
    vote: GetVoteResponse?,
    isBookMark: Boolean,
    onUpvoteClick: (String) -> Unit,
    onDownvoteClick: (String) -> Unit,
    onCommentClick: () -> Unit,
    onBookmarkClick: (Boolean) -> Unit
) {
    var isChange by remember { mutableStateOf(false) }
    var upvotes by remember { mutableStateOf(vote?.data?.upVoteData?.total?: 0) }
    var isVote by remember { mutableStateOf(vote?.data?.userVote) }
    var isSavedPost by remember { mutableStateOf(isBookMark) }


    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = Modifier.fillMaxWidth()
    ) {
        // Upvote/Downvote
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = {
                when (isVote) {
                    "upvote" -> {
                        upvotes--
                        isVote = "none"
                    }
                    "downvote", "none" -> {
                        upvotes++
                        isVote = "upvote"
                    }
                }
                onUpvoteClick(isVote.toString())
            }) {
                Icon(
                    painter = painterResource(id = R.drawable.upvote),
                    contentDescription = "Upvote",
                    modifier = Modifier.size(30.dp),
                    tint = if (isVote == "upvote") Color.Green else Color.Unspecified
                )
            }
            Text(
                text = "$upvotes",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
            IconButton(onClick = {
                when (isVote) {
                    "downvote" -> isVote = "none"
                    "upvote" -> {
                        upvotes--
                        isVote = "downvote"
                    }
                    "none" -> isVote = "downvote"
                }
                onDownvoteClick(isVote.toString())
            }) {
                Icon(
                    painter = painterResource(id = R.drawable.downvote),
                    contentDescription = "Downvote",
                    modifier = Modifier.size(30.dp),
                    tint = if (isVote == "downvote") Color.Red else Color.Unspecified
                )
            }
        }

        // Comment & Bookmark
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            IconButton(onClick = onCommentClick) {
                Icon(
                    painter = painterResource(id = R.drawable.comment),
                    contentDescription = "Comment",
                    modifier = Modifier.size(30.dp)
                )
            }

            IconButton(onClick = {
                isSavedPost = !isSavedPost
                onBookmarkClick(isSavedPost)
            }) {
                Icon(
                    painter = painterResource(id = R.drawable.bookmark),
                    contentDescription = "Bookmark",
                    modifier = Modifier.size(30.dp),
                    tint = if (isSavedPost) Color.Green else Color.Unspecified
                )
            }
        }
    }
}
