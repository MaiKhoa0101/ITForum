package com.example.itforum.user.post

import android.R.attr.label
import android.content.SharedPreferences
import android.util.Log
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowCircleDown
import androidx.compose.material.icons.filled.ArrowCircleUp
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Comment
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.itforum.animation.AnimatedVoteIcon
import com.example.itforum.animation.AnimatedVoteNumber
import com.example.itforum.user.modelData.response.GetVoteResponse
import com.example.itforum.user.modelData.response.PostResponse

@Composable
fun PostCardWithVote(
    post: PostResponse,
    vote: GetVoteResponse?,
    isBookMark: Boolean,
    onUpvoteClick: (String?) -> Unit = {},
    onDownvoteClick: (String?) -> Unit = {},
    onCommentClick: () -> Unit = {},
    onBookmarkClick: () -> Unit = {},
    onShareClick: () -> Unit = {},
    onCardClick : () -> Unit = {},
    onReportClick: (String) -> Unit = {},
    navHostController: NavHostController,
    sharedPreferences: SharedPreferences
) {
    var isChange by remember { mutableStateOf(false) }
    var upvotes by remember { mutableStateOf(vote?.data?.upVoteData?.total?: 0) }
    var isVote by remember { mutableStateOf(vote?.data?.userVote) }
    var isSavedPost by remember { mutableStateOf(isBookMark) }


    var isChangeUp by remember { mutableStateOf(false) }
    var isChangeDown by remember { mutableStateOf(false) }


    Card(
        elevation = 4.dp,
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .fillMaxWidth()
            .clickable{onCardClick()},
        backgroundColor = MaterialTheme.colorScheme.secondaryContainer,
    ) {
        Column(
            modifier = Modifier
                .padding(20.dp)
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
                        model =  post.avatar,
                        contentDescription = "avatar",
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .clickable {
                                val myUserId = sharedPreferences.getString("userId", null)
                                if(post.userId == myUserId)
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
                                    val myUserId = sharedPreferences.getString("userId", null)
                                    if(post.userId == myUserId)
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
                        post.id?.let { onReportClick(it) }
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
                fontWeight = FontWeight.Normal
                ,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(12.dp))

            // media section
           PostMediaSection(post.imageUrls,post.videoUrls)


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
                            isChangeUp=true
                        }
                    ){
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
                        textStyle = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.SemiBold),
                        textColor = MaterialTheme.colorScheme.onBackground
                    )
                    IconButton(
                        modifier = Modifier.size(60.dp),
                        onClick = {
                            if(isVote == "downvote"){
                                isVote = "none"
                            }else if(isVote == "upvote" ){
                                upvotes--
                                isVote = "downvote"
                            }else if(isVote=="none"){
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
                IconButton(onClick = onCommentClick) {
                    Icon(
                        imageVector = Icons.Default.Comment,
                        contentDescription = "Comment",
                        modifier = Modifier.size(30.dp),
                        tint = MaterialTheme.colorScheme.onBackground
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
                        imageVector = Icons.Default.Bookmark,
                        contentDescription = "Bookmark",
                        modifier = Modifier.size(30.dp),
                        tint = if (isSavedPost) Color.Green else MaterialTheme.colorScheme.onBackground
                    )
                }

            }
        }
    }
}