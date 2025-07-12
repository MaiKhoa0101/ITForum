package com.example.itforum.user.post
import android.content.SharedPreferences
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import coil.compose.AsyncImage
import com.example.itforum.user.effect.model.UiStateComment
import com.example.itforum.user.modelData.response.Comment
import com.example.itforum.user.modelData.response.Reply
import com.example.itforum.user.post.viewmodel.CommentViewModel
import com.example.itforum.user.skeleton.SkeletonBox
import com.example.itforum.user.skeleton.SkeletonPost


@Composable
fun PostCommentScreen(
    postId: String,
    sharedPreferences: SharedPreferences,
    commentViewModel: CommentViewModel
) {

    val isLoading by commentViewModel.isLoading.collectAsState()
    val comments by commentViewModel.comments.collectAsState()


    val isSubmittingReply by commentViewModel.isSubmittingReply.collectAsState()

    LaunchedEffect(postId) {
        commentViewModel.fetchComment(postId)
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.secondaryContainer)
    ) {
        Divider()
        Text(
            text = "Bình luận",
            modifier = Modifier.padding(16.dp),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        Divider()

        when {
            isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    SkeletonBox()
                }
            }

            !isLoading && comments.isEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Chưa có bình luận nào",
                        color = Color.Gray,
                        fontSize = 16.sp
                    )
                }
            }

            else -> {
                println("Có bình luận")

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 80.dp) // Leave room for input bar
                ) {
                    items(comments) { comment ->
                        Column {
                            CommentCard(
                                comment = comment,
                                fetchReply = { commentId, onLoaded ->
                                    commentViewModel.fetchReply(commentId, onLoaded)
                                },
                                onSubmitReply = { commentId, replyText, onReplyPosted ->
                                    commentViewModel.postReply(commentId, replyText) { newReply ->
                                        onReplyPosted(newReply)
                                    }
                                },
                                isSubmittingReply = isSubmittingReply
                            )
                            Divider()
                        }
                    }
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PostCommentBottomSheet(
    onDismissRequest: () -> Unit,
    postId: String,
    sharedPreferences: SharedPreferences,
    commentViewModel: CommentViewModel
) {
    val bottomSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Expanded,
        skipHalfExpanded =true

    )
    val isSubmittingComment by commentViewModel.isSubmittingComment.collectAsState()


    LaunchedEffect(bottomSheetState.currentValue) {
        if (bottomSheetState.currentValue == ModalBottomSheetValue.Hidden) {
            onDismissRequest()
        }
    }

    ModalBottomSheetLayout(
        sheetState = bottomSheetState,
        sheetContent = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
            ) {
                // Drag handle
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .width(32.dp)
                            .height(4.dp)
                            .background(Color.Gray.copy(alpha = 0.4f), RoundedCornerShape(2.dp))
                    )
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                ) {
                    PostCommentScreen(
                        postId = postId,
                        sharedPreferences = sharedPreferences,
                        commentViewModel = commentViewModel
                    )
                }

                // Fixed comment input at bottom
                CommentInputSection(
                    onSubmitComment = { commentText ->
                        commentViewModel.postComment(postId, commentText) {}
                    },
                    isLoading = isSubmittingComment,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                )
            }
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Transparent)
        )
    }
}
@Composable
fun CommentDialogWrapper(
    postId: String,
    sharedPreferences: SharedPreferences,
    onDismiss: () -> Unit,
    commentViewModel: CommentViewModel
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            PostCommentBottomSheet(
                postId = postId,
                sharedPreferences = sharedPreferences,
                onDismissRequest = onDismiss,
                commentViewModel = commentViewModel
            )
        }
    }
}



@Composable
fun CommentCard(
    comment: Comment,
    fetchReply: (commentId: String, onLoaded: (List<Reply>) -> Unit) -> Unit,
    onSubmitReply: (commentId: String, replyText: String, onReplyPosted: (Reply) -> Unit) -> Unit,
    isSubmittingReply: Boolean
) {
    var showReplies by remember { mutableStateOf(false) }
    var replies by remember { mutableStateOf<List<Reply>>(emptyList()) }
    var isLoadingReplies by remember { mutableStateOf(false) }
    var showReplyInput by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.Top
    ) {
        UserAvatar(comment.avatar, size = 40.dp)

        Spacer(Modifier.width(8.dp))

        Column(Modifier.weight(1f)) {
            Column(
                modifier = Modifier
                    .background(Color(0xFFF0F2F5), shape = RoundedCornerShape(16.dp))
                    .padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = comment.userName ?: "Unknown",
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = "· ${getTimeAgo(comment.time)}",
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        fontSize = 12.sp
                    )
                }

                Text(
                    text = comment.content,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }


            Row(modifier = Modifier.padding(top = 4.dp), verticalAlignment = Alignment.CenterVertically) {

                Spacer(Modifier.width(16.dp))
                Text(
                    text = "Reply",
                    fontSize = 13.sp,
                    color = Color.Gray,
                    modifier = Modifier.clickable {
                        showReplyInput = !showReplyInput
                    }
                )
                if (comment.totalReply > 0) {
                    Spacer(Modifier.width(16.dp))
                    Text(
                        text = if (!showReplies) "View ${comment.totalReply} replies" else "Hide replies",
                        fontSize = 13.sp,
                        color = Color.Gray,
                        modifier = Modifier.clickable {
                            if (!showReplies) {
                                isLoadingReplies = true
                                fetchReply(comment.id) {
                                    replies = it
                                    showReplies = true
                                    isLoadingReplies = false
                                }
                            } else {
                                showReplies = false
                            }
                        }
                    )
                }
            }


            if (showReplies) {
                if (isLoadingReplies) {
                    Row(Modifier.padding(start = 40.dp, top = 4.dp)) {
                        CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                        Spacer(Modifier.width(8.dp))
                        Text("Loading replies...", fontSize = 13.sp)
                    }
                } else {
                    replies.forEach {
                        ReplyCard(reply = it, modifier = Modifier.padding(start = 40.dp, top = 8.dp))
                    }
                }
            }
            if (showReplyInput) {
                ReplyInputSection(
                    commentId = comment.id,
                    onSubmitReply = { cid, text ->
                        onSubmitReply(cid, text) { newReply ->
                            replies = listOf(newReply) + replies
                            showReplyInput = false
                            showReplies = true
                        }
                    },
                    onCancel = { showReplyInput = false },
                    isLoading = isSubmittingReply
                )
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
                Text(reply.userName.toString(), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                Spacer(Modifier.width(6.dp))
                Text("· ${getTimeAgo(reply.time)}", color = MaterialTheme.colorScheme.onSecondaryContainer)
            }
            Box(
                modifier = Modifier
                    .background(Color(0xFFF0F2F5), shape = MaterialTheme.shapes.medium)
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Text(reply.content,  color = MaterialTheme.colorScheme.onBackground)
            }
        }
    }
}

@Composable
fun CommentInputSection(
    modifier: Modifier = Modifier,
    onSubmitComment: (String) -> Unit,
    isLoading: Boolean
) {
    var commentText by remember { mutableStateOf("") }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Optional: User avatar
        UserAvatar(avatarUrl = null, size = 50.dp)

        Spacer(Modifier.width(8.dp))

        // Rounded input box
        TextField(
            value = commentText,
            onValueChange = { commentText = it },
            placeholder = { Text("Bình luận gì đó...") },
            modifier = Modifier
                .weight(1f)
                .height(50.dp),
            shape = RoundedCornerShape(20.dp),
            colors = TextFieldDefaults.textFieldColors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent
            ),
            singleLine = true
        )

        Spacer(Modifier.width(8.dp))


        Button(
            onClick = {
                if (commentText.isNotBlank()) {
                    onSubmitComment(commentText.trim())
                    commentText = ""
                }
            },
            enabled = commentText.isNotBlank() && !isLoading,
            colors = ButtonDefaults.buttonColors(
                contentColor = Color.White,
                disabledContentColor = Color.White
            ),
            shape = RoundedCornerShape(20.dp),
            contentPadding = PaddingValues(horizontal = 16.dp),
            modifier = Modifier.height(45.dp)
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    strokeWidth = 2.dp,
                    color = Color.White
                )
            } else {
                Text("Gửi")
            }
        }
    }
}


@Composable
fun ReplyInputSection(
    commentId: String,
    onSubmitReply: (String, String) -> Unit, // commentId, replyText
    onCancel: () -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false
) {
    var replyText by remember { mutableStateOf("") }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 40.dp, top = 8.dp),
        color = Color(0xFFF8F9FA),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.Top
            ) {
                // User avatar placeholder (smaller for reply)
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = null,
                    tint = Color(0xFFB0B3B8),
                    modifier = Modifier.size(28.dp).clip(CircleShape)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Column(modifier = Modifier.weight(1f)) {
                    OutlinedTextField(
                        value = replyText,
                        onValueChange = { replyText = it },
                        placeholder = {
                            Text(
                                "Write a reply...",
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            backgroundColor = MaterialTheme.colorScheme.secondaryContainer,
                        ),
                        shape = MaterialTheme.shapes.medium,
                        maxLines = 3,
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Improved alignment for Cancel and Reply buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = onCancel,
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = Color(0xFF65676B)
                    ),
                    modifier = Modifier.height(32.dp) // Ensure same height as Reply button
                ) {
                    Text("Cancel", color = MaterialTheme.colorScheme.onBackground)
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = {
                        if (replyText.isNotBlank()) {
                            onSubmitReply(commentId, replyText.trim())
                            replyText = ""
                        }
                    },
                    enabled = replyText.isNotBlank() && !isLoading,
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color(0xFF1877F2),
                        contentColor = Color.White,
                        disabledBackgroundColor = Color(0xFFE4E6EA)
                    ),
                    shape = MaterialTheme.shapes.medium,
                    modifier = Modifier.height(32.dp)
                ) {
                    if (isLoading) {
                        SkeletonBox()
                    } else {
                        Text("Reply", color = MaterialTheme.colorScheme.onBackground)
                    }
                }
            }
        }
    }}

@Composable
fun UserAvatar(avatarUrl: String?, size: Dp) {
    if (!avatarUrl.isNullOrBlank()) {
        AsyncImage(
            model = avatarUrl,
            contentDescription = null,
            modifier = Modifier.size(size).clip(CircleShape)
        )
    } else {
        Icon(
            imageVector = Icons.Default.AccountCircle,
            contentDescription = null,
            tint = Color(0xFFB0B3B8),
            modifier = Modifier.size(size).clip(CircleShape)
        )
    }
}
