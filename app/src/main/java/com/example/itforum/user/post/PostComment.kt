package com.example.itforum.user.post

import android.content.SharedPreferences
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.itforum.user.effect.model.UiStateComment
import com.example.itforum.user.modelData.response.Comment
import com.example.itforum.user.modelData.response.Reply
import com.example.itforum.user.post.viewmodel.CommentViewModel

@Composable
fun PostCommentScreen(
    navController: NavHostController,
    postId: String,
    sharedPreferences: SharedPreferences,
) {
    val viewModel: CommentViewModel = viewModel(factory = viewModelFactory {
        initializer { CommentViewModel(sharedPreferences) }
    })
    val uiState by viewModel.uiState.collectAsState()
    var comments by remember { mutableStateOf(listOf<Comment>()) }
    var fetchingReplyForCommentId by remember { mutableStateOf<String?>(null) }

    // Fetch comments only once when postId changes (first load)
    LaunchedEffect(postId) {
        viewModel.fetchComment(postId)
    }
    // When comments loaded, store them
    LaunchedEffect(uiState) {
        if (uiState is UiStateComment.SuccessFetchComment) {
            comments = (uiState as UiStateComment.SuccessFetchComment).comments
        }
    }

    Column(Modifier.fillMaxSize().background(Color(0xFFF0F2F5))) {
        Text("Comments", style = MaterialTheme.typography.h6, modifier = Modifier.padding(16.dp))
        Divider()
        when (uiState) {
            is UiStateComment.Loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            is UiStateComment.Error -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Error: ${(uiState as UiStateComment.Error).message}", color = Color.Red)
            }
            else -> LazyColumn(Modifier.weight(1f)) {
                items(comments, key = { it.id }) { comment ->
                    CommentCard(comment) { commentId, onLoaded ->
                        viewModel.fetchReply(commentId, onLoaded)
                    }
                    Divider()
                }
            }
        }
    }
}

@Composable
fun CommentCard(
    comment: Comment,
    fetchReply: (commentId: String, onLoaded: (List<Reply>) -> Unit) -> Unit
) {
    var showReplies by remember { mutableStateOf(false) }
    var replies by remember { mutableStateOf<List<Reply>>(emptyList()) }
    var isLoadingReplies by remember { mutableStateOf(false) }




    Column(
        Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Row(verticalAlignment = Alignment.Top) {
            if (!comment.avatar.isNullOrBlank()) {
                AsyncImage(
                    model = comment.avatar,
                    contentDescription = null,
                    modifier = Modifier.size(36.dp).clip(CircleShape)
                )
            } else {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = null,
                    tint = Color(0xFFB0B3B8),
                    modifier = Modifier.size(36.dp).clip(CircleShape)
                )
            }
            Spacer(Modifier.width(8.dp))
            Column(Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(comment.userName.toString(), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.body2)
                    Spacer(Modifier.width(6.dp))
                    Text("· ${getTimeAgo(comment.time)}", style = MaterialTheme.typography.caption, color = Color(0xFF65676B))
                }
                Box(
                    modifier = Modifier
                        .background(Color(0xFFF0F2F5), shape = MaterialTheme.shapes.medium)
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                        .fillMaxWidth()
                ) {
                    Text(comment.content, style = MaterialTheme.typography.body1, color = Color(0xFF050505))
                }
                if (comment.totalReply != null && comment.totalReply > 0) {
                Text(
                    text = if (!showReplies) "see more ${comment.totalReply} reply" else "hide reply",
                    style = MaterialTheme.typography.body2,
                    color = Color(0xFF1877F2),
                    modifier = Modifier
                        .padding(top = 4.dp)
                        .clickable {
                            if (!showReplies) {
                                isLoadingReplies = true
                                fetchReply(comment.id) { loadedReplies ->
                                    replies = loadedReplies
                                    isLoadingReplies = false
                                    showReplies = true
                                }
                            } else {
                                showReplies = false
                            }
                        }
                )}
                if (showReplies) {
                    if (isLoadingReplies) {
                        Row(Modifier.padding(start = 40.dp, top = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                            CircularProgressIndicator(Modifier.size(16.dp), strokeWidth = 2.dp)
                            Spacer(Modifier.width(8.dp))
                            Text("Loading replies...", style = MaterialTheme.typography.body2)
                        }
                    } else {
                        replies.forEach { reply ->
                            ReplyCard(reply, Modifier.padding(start = 40.dp, top = 4.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ReplyCard(reply: Reply, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        if (!reply.avatar.isNullOrBlank()) {
            AsyncImage(
                model = reply.avatar,
                contentDescription = null,
                modifier = Modifier.size(28.dp).clip(CircleShape)
            )
        } else {
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = null,
                tint = Color(0xFFB0B3B8),
                modifier = Modifier.size(28.dp).clip(CircleShape)
            )
        }
        Spacer(Modifier.width(8.dp))
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(reply.userName.toString(), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.body2, color = Color(0xFF050505))
                Spacer(Modifier.width(6.dp))
                Text("· ${getTimeAgo(reply.time)}", style = MaterialTheme.typography.caption, color = Color(0xFF65676B))
            }
            Box(
                modifier = Modifier
                    .background(Color(0xFFF0F2F5), shape = MaterialTheme.shapes.medium)
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Text(reply.content, style = MaterialTheme.typography.body2, color = Color(0xFF050505))
            }
        }
    }
}