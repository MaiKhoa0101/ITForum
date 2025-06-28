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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.ThumbDown
import androidx.compose.material.icons.filled.ThumbDownOffAlt
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.filled.ThumbUpOffAlt
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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

import com.example.itforum.user.modelData.response.GetVoteResponse
import com.example.itforum.user.post.viewmodel.CommentViewModel
import com.example.itforum.user.post.viewmodel.PostViewModel

@Composable
fun DetailPostPage(
    navHostController: NavHostController,
    sharedPreferences: SharedPreferences,
    postId: String,
    commentViewModel: CommentViewModel
) {


    val viewModel: PostViewModel = viewModel(factory = viewModelFactory {
        initializer { PostViewModel(sharedPreferences) }
    })

    var userId = sharedPreferences.getString("userId", null)
    val postWithVote by viewModel.selectedPostWithVote.collectAsState()

    LaunchedEffect(postId) {
        viewModel.fetchPostById(postId)
    }


    postWithVote?.post?.let { post ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF00AEFF))
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                TopDetailPost(navHostController)

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White)
                ) {
                    AvatarNameDetail(
                        avatar = post.avatar.orEmpty(),
                        name = post.userName ?: "unknown",
                        time = post.createdAt ?: ""
                    )

                    ContentPost(
                        title = post.title.orEmpty(),
                        content = post.content.orEmpty(),
                        tags = post.tags ?: emptyList()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Log.d("imgs", post.imageUrls.toString())

                    PostMediaSection(
                        imageUrls = post.imageUrls ?: emptyList(),
                        videoUrls = post.videoUrls ?: emptyList()
                    )

                    VoteSection(
                        vote = postWithVote?.vote,
                        isBookMark = postWithVote?.isBookMark ?: false,
                        onUpvoteClick = {viewModel.handleUpVote("upvote",-1,postId)},
                        onCommentClick = {},
                        onBookmarkClick = {viewModel.handleBookmark(-1,postId, userId)},
                        onDownvoteClick = {viewModel.handleDownVote("downvote",-1,postId)}
                    )

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
