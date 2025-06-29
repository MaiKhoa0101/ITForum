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

    val uiState by commentViewModel.uiState.collectAsState()
    var comments by remember { mutableStateOf(listOf<Comment>()) }
    var isSubmittingComment by remember { mutableStateOf(false) }

    LaunchedEffect(postId) {
        commentViewModel.fetchComment(postId)
    }

    LaunchedEffect(uiState) {
        if (uiState is UiStateComment.SuccessFetchComment) {
            comments = (uiState as UiStateComment.SuccessFetchComment).comments
        }
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.secondaryContainer)
    ) {
        Divider()
        Text(
            text = "Comments",
            modifier = Modifier.padding(16.dp),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        Divider()

        when (uiState) {
            is UiStateComment.Loading -> {
                println("Loading Comment")
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    SkeletonBox()
                }
            }

            is UiStateComment.Error -> {
                println("Binh luan bi loi")
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Error: ${(uiState as UiStateComment.Error).message}",
                        color = Color.Red
                    )
                }
            }

            else -> {
                println("Co binh luan")
                // Comments list takes available space
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Comment input section at the bottom
                    CommentInputSection(
                        onSubmitComment = { commentText ->
                            isSubmittingComment = true
                            commentViewModel.postComment(postId, commentText) { newComment ->
                                comments = listOf(newComment) + comments
                                isSubmittingComment = false
                            }
                        },
                        isLoading = isSubmittingComment,
                        modifier = Modifier.fillMaxWidth()
                    )
                    for (comment in comments) {
                        CommentCard(
                            comment = comment,
                            fetchReply = { commentId, onLoaded ->
                                commentViewModel.fetchReply(commentId, onLoaded)
                            },
                            onSubmitReply = { commentId, replyText, onReplyPosted ->
                                commentViewModel.postReply(commentId, replyText) { newReply ->
                                    onReplyPosted(newReply)
                                }
                            }
                        )
                        Divider()
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
                    .fillMaxHeight() // Take most of the screen height
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
                            .background(
                                Color.Gray.copy(alpha = 0.4f),
                                RoundedCornerShape(2.dp)
                            )
                    )
                }

                // Comment screen content
                PostCommentScreen(
                    postId = postId,
                    sharedPreferences = sharedPreferences,
                    commentViewModel = commentViewModel
                )
            }
        },
        modifier = Modifier.fillMaxSize()
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



// Updated CommentCard with fixed reply logic
@Composable
fun CommentCard(
    comment: Comment,
    fetchReply: (commentId: String, onLoaded: (List<Reply>) -> Unit) -> Unit,
    onSubmitReply: (commentId: String, replyText: String, onReplyPosted: (Reply) -> Unit) -> Unit
) {
    var showReplies by remember { mutableStateOf(false) }
    var replies by remember { mutableStateOf<List<Reply>>(emptyList()) }
    var isLoadingReplies by remember { mutableStateOf(false) }
    var showReplyInput by remember { mutableStateOf(false) }
    var isSubmittingReply by remember { mutableStateOf(false) }

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
                    Text(comment.userName.toString(), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                    Spacer(Modifier.width(6.dp))
                    Text("· ${getTimeAgo(comment.time)}", color = MaterialTheme.colorScheme.onSecondaryContainer)
                }
                Box(
                    modifier = Modifier
                        .background(Color(0xFFF0F2F5), shape = MaterialTheme.shapes.medium)
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                        .fillMaxWidth()
                ) {
                    Text(comment.content,  color = MaterialTheme.colorScheme.onBackground)
                }

                // Action buttons row
                Row(
                    modifier = Modifier.padding(top = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if ( comment.totalReply > 0) {
                        Text(
                            text = if (!showReplies) "see more ${comment.totalReply} reply" else "hide reply",
                            color = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.clickable {
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
                        )
                        Spacer(Modifier.width(16.dp))
                    }

                    Text(
                        text = "Reply",
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.clickable {
                            showReplyInput = !showReplyInput
                        }
                    )
                }

                if (showReplies) {
                    if (isLoadingReplies) {
                        Row(Modifier.padding(start = 40.dp, top = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                            SkeletonBox(Modifier.size(16.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Loading replies...")
                        }
                    } else {
                        replies.forEach { reply ->
                            ReplyCard(reply, Modifier.padding(start = 40.dp, top = 4.dp))
                        }
                    }
                }

                if (showReplyInput) {
                    ReplyInputSection(
                        commentId = comment.id,
                        onSubmitReply = { commentId, replyText ->
                            isSubmittingReply = true
                            onSubmitReply(commentId, replyText) { newReply ->
                                replies = listOf(newReply) + replies // Add new reply to top
                                isSubmittingReply = false
                                showReplyInput = false // Hide the input after successful post
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
    onSubmitComment: (String) -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false
) {
    var commentText by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.secondaryContainer),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // User avatar placeholder
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = null,
                tint = Color(0xFFB0B3B8),
                modifier = Modifier.size(36.dp).clip(CircleShape)
            )

            Spacer(modifier = Modifier.width(8.dp))


            Column(modifier = Modifier.weight(1f)) {
                OutlinedTextField(
                    value = commentText,
                    onValueChange = { commentText = it },
                    placeholder = {
                        Text(
                            "Write a comment...",
                            color = Color(0xFF65676B),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        backgroundColor = Color(0xFFF0F2F5),
                        focusedBorderColor = Color(0xFF1877F2),
                        unfocusedBorderColor = Color(0xFFDDDDDD)
                    ),
                    shape = MaterialTheme.shapes.medium,
                    maxLines = 4,
                    textStyle = MaterialTheme.typography.bodyLarge.copy(color = Color(0xFF050505))
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = {
                            if (commentText.isNotBlank()) {
                                onSubmitComment(commentText.trim())
                                commentText = ""
                            }
                        },
                        enabled = commentText.isNotBlank() && !isLoading,
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = Color(0xFF1877F2),
                            contentColor = Color.White,
                            disabledBackgroundColor = Color(0xFFE4E6EA)
                        ),
                        shape = MaterialTheme.shapes.medium,
                        modifier = Modifier.height(36.dp)
                    ) {
                        if (isLoading) {
                            SkeletonBox()
                        } else {
                            Text("Post", style = MaterialTheme.typography.labelLarge)
                        }

            OutlinedTextField(
                value = commentText,
                onValueChange = { commentText = it },
                placeholder = {
                    Text(
                        "Write a comment...",
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                },
                modifier = Modifier.fillMaxWidth(0.7f),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    backgroundColor = Color(0xFFF0F2F5),
                    focusedBorderColor = Color(0xFF1877F2),
                    unfocusedBorderColor = Color(0xFFDDDDDD)
                ),
                shape = MaterialTheme.shapes.medium,
                maxLines = 4,
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = {
                    if (commentText.isNotBlank()) {
                        onSubmitComment(commentText.trim())
                        commentText = ""

                    }
                },
                enabled = commentText.isNotBlank() && !isLoading,
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color(0xFF1877F2),
                    contentColor = Color.White,
                    disabledBackgroundColor = Color(0xFFE4E6EA)
                ),
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.height(36.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp,
                        color = Color.White
                    )
                } else {
                    Text("Post", color = MaterialTheme.colorScheme.onBackground)
                }
            }
        }
        Divider()
    }
}}}}

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

