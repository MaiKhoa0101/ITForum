package com.example.itforum.user.post

import android.R.bool
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.itforum.R
import com.example.itforum.user.modelData.response.GetVoteResponse
import com.example.itforum.user.modelData.response.PostResponse

@Composable
fun PostCardWithVote(

    post: PostResponse,
    vote: GetVoteResponse?,
    isBookMark: Boolean,
    onUpvoteClick: () -> Unit = {},
    onDownvoteClick: () -> Unit = {},
    onCommentClick: () -> Unit = {},
    onBookmarkClick: () -> Unit = {},
    onReportClick: (String) -> Unit = {},
    navHostController: NavHostController
)
 {
    var isChange by remember { mutableStateOf(false) }
    var upvotes by remember { mutableStateOf(vote?.data?.upVoteData?.total?: 0) }
    var isVote by remember { mutableStateOf(vote?.data?.userVote) }
    var isSavedPost by remember { mutableStateOf(isBookMark) }
    Log.d("bookmark", isSavedPost.toString())
     var selectedImageIndex by remember { mutableStateOf(0) }
     var showImageDetail by remember { mutableStateOf(false) }


    Card(
        elevation = 4.dp,
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
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
                        model =  R.drawable.avatar,
                        contentDescription = "avatar",
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .clickable { navHostController.navigate("otherprofile/${post.userId}") }
                    )
                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = post.userName ?: "Unknown User",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            modifier = Modifier
                                .clickable { navHostController.navigate("otherprofile/${post.userId}") }
                        )
                        Text(
                            text = "${getTimeAgo(post.createdAt ?: "")} • ${""}",
                            color = Color.Gray,
                            fontSize = 11.sp
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { /* Handle resource click */ }) {
                        Icon(
                            painter = painterResource(id = R.drawable.resource),
                            contentDescription = "resource",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    IconButton(onClick = {
                        post.id?.let { onReportClick(it) }
                    }
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.more),
                            contentDescription = "more",
                            modifier = Modifier.size(21.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Post title
            Text(
                text = post.title ?: "",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Post image
            if (!post.imageUrls.isNullOrEmpty()) {
                ImageGrid(
                    imageUrls = post.imageUrls,
                    onImageClick = { _, index ->
                        selectedImageIndex = index
                        showImageDetail = true
                    }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Image detail dialog
            if (showImageDetail && !post.imageUrls.isNullOrEmpty()) {
                ImageDetailDialog(
                    imageUrls = post.imageUrls,
                    initialIndex = selectedImageIndex,
                    onDismiss = { showImageDetail = false }
                )
            }


            // Action buttons row
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Upvote/Downvote section
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = {
                        onUpvoteClick()
                        if(isVote == "upvote"){
                            upvotes --
                            isVote = "none"
                        }else if(isVote == "downvote" || isVote == "none"){
                            upvotes++
                            isVote = "upvote"
                        }
                        isChange = true
                    } ) {
                        Icon(
                            painter = painterResource(id = R.drawable.upvote),
                            contentDescription = "Upvote",
                            modifier = Modifier.size(30.dp),
                            tint = if (isVote == "upvote") Color.Green else Color.Unspecified
                        )
                    }
                    Text(
                        text = "${upvotes}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    IconButton(onClick = {
                        onDownvoteClick()
                        if(isVote == "downvote"){
                            isVote = "none"
                        }else if(isVote == "upvote" ){
                            upvotes--
                            isVote = "downvote"
                        }else if(isVote=="none"){
                            isVote = "downvote"
                        }

                    }) {
                        Icon(
                            painter = painterResource(id = R.drawable.downvote),
                            contentDescription = "Downvote",
                            modifier = Modifier.size(30.dp),
                            tint = if (isVote == "downvote") Color.Red else Color.Unspecified
                        )
                    }
                }

                // Other actions
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    IconButton(onClick = onCommentClick) {
                        Icon(
                            painter = painterResource(id = R.drawable.comment),
                            contentDescription = "Comment",
                            modifier = Modifier.size(30.dp),
                            tint = Color.Unspecified
                        )
                    }
                    IconButton(onClick = {onBookmarkClick()
                        //neu save post r doi icon qua chua save va nguoc lai
                        if(isSavedPost){
                            isSavedPost = false
                        }else{
                            isSavedPost = true
                        }}
                    )
                    {
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
    }
}