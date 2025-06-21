package com.example.itforum.user.post

import android.content.SharedPreferences
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
import androidx.compose.material.icons.outlined.BookmarkAdd
import androidx.compose.material.icons.outlined.InsertComment
import androidx.compose.material.icons.outlined.Share
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MovableContent
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
import androidx.compose.ui.platform.LocalContext
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
import com.example.itforum.user.modelData.response.PostResponse
import com.example.itforum.user.post.viewmodel.PostViewModel

@Composable
fun DetailPostPage(
    navHostController: NavHostController,
    sharedPreferences : SharedPreferences
) {

    var showImageDetail by remember { mutableStateOf(false) }
    var selectedImageIndex by remember { mutableStateOf(0) }
    val viewModel: PostViewModel = viewModel(factory = viewModelFactory {
        initializer { PostViewModel(navHostController, sharedPreferences) }
    })
    val post by viewModel.selectedPost.collectAsState()
    val vote by viewModel.selectedVote.collectAsState()
    LaunchedEffect(post) {

    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF00AEFF))
    ) {
        Column (
            modifier = Modifier.fillMaxSize()
        ) {
            TopDetailPost(
                navHostController
            )
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
            ) {
                item{
                    AvatarNameDetail(
                        avatar = R.drawable.avatar,
                        name = post?.userName?:"unknown",
                        time = post?.createdAt.toString()
                    )
                    ContentPost(post?.title?:"",post?.content?:"",  post?.tags ?: emptyList())
                    // Post image
                    Spacer(modifier = Modifier.height(12.dp))
                    if (!post?.imageUrls.isNullOrEmpty()) {
                        ImageGrid(
                            imageUrls = post?.imageUrls!!,
                            onImageClick = { _, index ->
                                selectedImageIndex = index
                                showImageDetail = true
                            }
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    // Image detail dialog
                    if (showImageDetail && !post?.imageUrls.isNullOrEmpty()) {
                        ImageDetailDialog(
                            imageUrls = post?.imageUrls!!,
                            initialIndex = selectedImageIndex,
                            onDismiss = { showImageDetail = false }
                        )
                    }
                    ActionsPost(navHostController)
                    ContentCommentPost(navHostController)
                }
            }

        }
//        var startPos = Offset(x = 50f,y = 1812f+100)
//        var endPos = Offset(x = 50f,y = 1922f+10)
//        CommentConnector(
//            startOffset = startPos,
//            endOffset = endPos
//        )
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
fun AvatarNameDetail(avatar: Int, name: String, time: String) {
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
fun ActionsPost(
    navHostController: NavHostController
) {
    Row(
        modifier = Modifier.padding(horizontal = 15.dp, vertical = 10.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        SectionLikePost(27,7, 101, navHostController)
        Icon(
            imageVector = Icons.Outlined.InsertComment,
            contentDescription = "Comment",
            modifier = Modifier.size(25.dp)
        )
        Icon(
            imageVector = Icons.Outlined.BookmarkAdd,
            contentDescription = "Lưu bài viết",
            modifier = Modifier.size(27.dp)
        )
        Icon(
            imageVector = Icons.Outlined.Share,
            contentDescription = "Chia sẻ",
            modifier = Modifier.size(27.dp)
        )
    }
}

@Composable
fun SectionLikePost(
    sizeIcon: Int,
    spaceHor: Int,
    totalLike: Int,
    navHostController: NavHostController
){
    // Trạng thái cho icon up và down
    var upvoted by remember { mutableStateOf(false) }
    var downvoted by remember { mutableStateOf(false) }
    var like = totalLike
    if (upvoted) {
        like += 1
    }else if (downvoted){
        like -= 1
    }
    Row(
        horizontalArrangement = Arrangement.spacedBy(spaceHor.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = if (upvoted) Icons.Default.ThumbUp else Icons.Default.ThumbUpOffAlt,
            contentDescription = "Upvote",
            modifier = Modifier.size(sizeIcon.dp)
                .clickable {
                    upvoted = !upvoted
                    if (upvoted)
                        downvoted = false  // chỉ được chọn một trong hai
                }
        )
        Text(
            text = "$like",
            modifier = Modifier.clickable {
                navHostController.navigate("listlike")
            }
        )
        Icon(
            imageVector = if (downvoted) Icons.Default.ThumbDown else Icons.Default.ThumbDownOffAlt,
            contentDescription = "Upvote",
            modifier = Modifier.size(sizeIcon.dp)
                .clickable {
                    downvoted = !downvoted
                    if (downvoted) upvoted = false
                }
        )
    }
}

@Composable
fun ContentCommentPost(
    navHostController: NavHostController
) {
    Column(
        modifier = Modifier
            .TopBorder()
            .fillMaxSize()
    ) {
        Spacer(modifier = Modifier.height(15.dp))
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            CommentChild(Icons.Default.AccountCircle, "Onana", "Bà nói thiệt hả bà thơ", "2 phút", 10, navHostController)
            CommentChild(Icons.Default.AccountCircle, "J97", "Thiên lý ơi em có thể ở lại đây không", "3 phút", 20, navHostController)
            CommentChild(Icons.Default.AccountCircle, "J97", "Thiên lý ơi em có thể ở lại đây không", "4 phút", 30, navHostController)
        }
        WriteComment()
    }
}

@Composable
fun CommentChild(
    icon: ImageVector,
    name: String,
    content: String,
    time: String,
    totalLike: Int,
    navHostController: NavHostController
) {
    Row(
        modifier = Modifier.padding(vertical = 10.dp, horizontal = 10.dp)
            .fillMaxWidth(),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = "Avatar tài khoản",
            modifier = Modifier.size(40.dp)
        )
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .background(
                        color = Color(0xFFE0E0E0),
                        shape = RoundedCornerShape(15.dp)
                    )
                    .padding(10.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = name,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = content,
                    fontSize = 12.sp
                )
            }
            ActionsComment(time, totalLike, navHostController)
        }

    }
}

@Composable
fun ActionsComment(
    time: String,
    totalLike: Int,
    navHostController: NavHostController
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = time
        )
        SectionLikePost(17,2, totalLike, navHostController)
        Text(
            text = "Trả lời",
            modifier = Modifier
                .clickable {  }
        )
    }
}

@Composable
fun WriteComment() {
    Row(
        modifier = Modifier.padding(vertical = 10.dp, horizontal = 10.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.AccountCircle,
            contentDescription = "Avatar tài khoản",
            modifier = Modifier.size(45.dp)
        )
        var inputComment by remember { mutableStateOf("") }
        TextField(
            value = inputComment,
            onValueChange = {inputComment = it},
            placeholder = { Text(
                "Viết bình luận...",
                fontSize = 12.sp,
                color = Color(0x4C000000)
            ) },
            textStyle = TextStyle(fontSize = 12.sp),
            shape = RoundedCornerShape(15.dp),
            modifier = Modifier.height(56.dp)
                .fillMaxWidth(),
            colors = TextFieldDefaults.textFieldColors(
                unfocusedIndicatorColor = Color.Transparent,
            ),
        )
    }
}
@Composable
fun CommentConnector(
    startOffset: Offset,
    endOffset: Offset,
    color: Color = Color.Red,
    strokeWidth: Float = 4f
) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val path = Path().apply {
            moveTo(startOffset.x, startOffset.y)
            quadraticBezierTo(
                startOffset.x, (startOffset.y + endOffset.y) / 2, // control point
                endOffset.x, endOffset.y                         // end point
            )
        }

        drawPath(
            path = path,
            color = color,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )
    }

}