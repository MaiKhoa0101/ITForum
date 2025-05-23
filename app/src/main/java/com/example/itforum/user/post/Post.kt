package com.example.itforum.user.post

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import com.example.itforum.R
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.Alignment

@Composable
fun PostListScreen() {
    LazyColumn {
        items(postList.size) { index ->
            PostCard(post = postList[index])
            // separate post box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.dp)
                    .background(Color.Black)
            )
        }
    }
}

@Composable
fun PostCard(post: PostModel) {
    Card(
        elevation = 4.dp,
        modifier = Modifier
            .padding(0.dp)
            .fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // avatar and author
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.avatar),
                        contentDescription = "avatar",
                        modifier = Modifier.size(40.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = post.author,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                        Text(
                            text = "${post.readTime} • ${post.date}",
                            color = Color.Gray,
                            fontSize = 11.sp
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.resource),
                        contentDescription = "resource",
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Image(
                        painter = painterResource(id = R.drawable.more),
                        contentDescription = "more",
                        modifier = Modifier.size(21.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            // post title
            Text(
                text = post.title,
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(12.dp))
            // post img
            Image(
                painter = painterResource(id = post.imageRes),
                contentDescription = "Post Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.Yellow)
            )

            Spacer(modifier = Modifier.height(12.dp))
            // icon bar
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { println("Upvote was clicked") }) {
                        Icon(
                            painter = painterResource(id = R.drawable.upvote),
                            contentDescription = "Upvote",
                            modifier = Modifier.size(30.dp),
                            tint = Color.Unspecified
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "${post.likes}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    IconButton(onClick = { println("Downvote was clicked") }) {
                        Icon(
                            painter = painterResource(id = R.drawable.downvote),
                            contentDescription = "Downvote",
                            modifier = Modifier.size(30.dp),
                            tint = Color.Unspecified
                        )
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(30.dp)) {
                    IconButton(onClick = { println("Comment was clicked") }) {
                        Icon(
                            painter = painterResource(id = R.drawable.comment),
                            contentDescription = "Comment",
                            modifier = Modifier.size(30.dp),
                            tint = Color.Unspecified
                        )
                    }
                    IconButton(onClick = { println("Bookmark was clicked") }) {
                        Icon(
                            painter = painterResource(id = R.drawable.bookmark),
                            contentDescription = "Bookmark",
                            modifier = Modifier.size(30.dp),
                            tint = Color.Unspecified
                        )
                    }
                    IconButton(onClick = { println("Share was clicked") }) {
                        Icon(
                            painter = painterResource(id = R.drawable.share),
                            contentDescription = "Share",
                            modifier = Modifier.size(30.dp),
                            tint = Color.Unspecified
                        )
                    }
                }
            }




        }
    }
}