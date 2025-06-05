package com.example.itforum.user.post

import android.content.SharedPreferences
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import com.example.itforum.R
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.IntOffset
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.itforum.user.post.viewmodel.PostViewModel
import com.example.itforum.user.effect.model.UiStatePost
import com.example.itforum.user.modelData.request.GetPostRequest
import com.example.itforum.user.modelData.response.PostResponse
import java.time.Instant
import java.time.Duration
import com.example.itforum.user.modelData.response.GetVoteResponse
import com.example.itforum.user.modelData.response.News
import com.example.itforum.user.news.viewmodel.NewsViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PostListScreen(
    sharedPreferences: SharedPreferences,
    navHostController: NavHostController
) {
    val newsViewModel: NewsViewModel = viewModel(factory = viewModelFactory {
        initializer { NewsViewModel(sharedPreferences) }
    })
    LaunchedEffect(Unit) {
        newsViewModel.getNews()
    }
    val listNews by newsViewModel.listNews.collectAsState()

    Column(
        modifier = Modifier.padding(10.dp)
    ) {
        if(listNews != null){
            Text(
                text = "Tin tức",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
            AdvancedMarqueeTextList(
                listNews!!,navHostController,
                modifier = Modifier
                    .padding(horizontal = 30.dp)
                    .width(300.dp)
                    .height(40.dp)
            )
            Box(
                modifier = Modifier
                    .padding(horizontal = 50.dp)
                    .fillMaxWidth()       // Chiều rộng 100%
                    .height(1.dp)         // Độ dày của đường
                    .background(Color.Black)
            )
        }
    }
    val viewModel: PostViewModel = viewModel(factory = viewModelFactory {
        initializer { PostViewModel(navHostController,sharedPreferences) }
    })

    val uiState by viewModel.uiState.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    val isLoadingMore by viewModel.isLoadingMore.collectAsState()

    // Fetch posts when screen loads
    LaunchedEffect(Unit) {
        viewModel.fetchPosts(GetPostRequest(page = 1))
    }

    // Pull to refresh state
    val pullRefreshState = rememberPullRefreshState(
        refreshing = isRefreshing,
        onRefresh = { viewModel.refreshPosts() }
    )


    Box(
        modifier = Modifier
            .fillMaxSize()
            .pullRefresh(pullRefreshState)
    ) {
        when (uiState) {
            is UiStatePost.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Đang tải bài viết...",
                            color = Color.Gray
                        )
                    }
                }
            }


            is UiStatePost.SuccessWithVotes -> {
                val postsWithVotes = (uiState as UiStatePost.SuccessWithVotes).posts
                if (postsWithVotes.isEmpty()) {
                    // Empty state
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Chưa có bài viết nào",
                                fontSize = 18.sp,
                                color = Color.Gray
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Hãy thử làm mới trang",
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                        }
                    }
                } else {
                    // Posts list with infinite scroll
                    val listState = rememberLazyListState()

                    LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 16.dp)
                    ) {
                        itemsIndexed(
                            items = postsWithVotes,
                            key = { _, postWithVote -> postWithVote.post.id ?: postWithVote.post.hashCode() }
                        ) { index, postWithVote ->
                            PostCardWithVote(
                                post = postWithVote.post,
                                vote = postWithVote.vote,
                                onUpvoteClick = { viewModel.votePost(postWithVote.post.id,"upvote")  },
                                onDownvoteClick = { viewModel.votePost(postWithVote.post.id,"downvote") },
                                onCommentClick = { navHostController.navigate("comment/${postWithVote.post.id}")  },
                                onBookmarkClick = {  },
                                onShareClick = {  },

                            )

                            // Separator between posts
                            Divider(
                                modifier = Modifier.padding(horizontal = 16.dp),
                                color = Color.Gray.copy(alpha = 0.3f),
                                thickness = 0.5.dp
                            )

                            // Load more when reaching near the end
                            if (index >= postsWithVotes.size - 3 && !isLoadingMore) {
                                LaunchedEffect(index) {
                                    viewModel.loadMorePosts()
                                }
                            }
                        }

                        // Load more indicator at the bottom
                        if (isLoadingMore) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(20.dp),
                                            strokeWidth = 2.dp
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "Đang tải thêm...",
                                            fontSize = 14.sp,
                                            color = Color.Gray
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
            is UiStatePost.SuccessWithVotes -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Không có dữ liệu bình chọn cho bài viết.", color = Color.Gray)
                }
            }

            is UiStatePost.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "⚠️",
                            fontSize = 48.sp
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = (uiState as UiStatePost.Error).message,
                            fontSize = 16.sp,
                            color = Color.Red
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { viewModel.refreshPosts() }
                        ) {
                            Text("Thử lại")
                        }
                    }
                }
            }

            is UiStatePost.Idle -> {
                // Handle idle state - maybe same as Success but without loading
            }

        }

        // Pull refresh indicator
        PullRefreshIndicator(
            refreshing = isRefreshing,
            state = pullRefreshState,
            modifier = Modifier.align(Alignment.TopCenter)
        )
    }
}

@Composable
fun PostCardWithVote(

    post: PostResponse,
    vote: GetVoteResponse?,
    onUpvoteClick: () -> Unit = {},
    onDownvoteClick: () -> Unit = {},
    onCommentClick: () -> Unit = {},
    onBookmarkClick: () -> Unit = {},
    onShareClick: () -> Unit = {},

) {
    var isChange by remember { mutableStateOf(false) }
    var upvotes by remember { mutableStateOf(vote?.data?.upVoteData?.total?: 0) }
    var isVote by remember { mutableStateOf(vote?.data?.userVote) }


    LaunchedEffect(isChange) {
        if (isChange) {


            isChange = false
        }
    }
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
                    )
                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = post.userName ?: "Unknown User",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
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
                    IconButton(onClick = { /* Handle more options */ }) {
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
                ImageGrid(post.imageUrls)
                Spacer(modifier = Modifier.height(12.dp))
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
                    IconButton(onClick = onBookmarkClick) {
                        Icon(
                            painter = painterResource(id = R.drawable.bookmark),
                            contentDescription = "Bookmark",
                            modifier = Modifier.size(30.dp),
                            tint = Color.Unspecified
                        )
                    }
                    IconButton(onClick = onShareClick) {
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
@Composable
fun getTimeAgo(isoTimestamp: String): String {
    return try {
        val timestamp = Instant.parse(isoTimestamp)
        val now = Instant.now()
        val diffSeconds = Duration.between(timestamp, now).seconds

        when {
            diffSeconds < 60 -> "Just now"
            diffSeconds < 3600 -> "${diffSeconds / 60}m ago"
            diffSeconds < 86400 -> "${diffSeconds / 3600}h ago"
            diffSeconds < 2592000 -> "${diffSeconds / 86400}d ago"
            diffSeconds < 31536000 -> "${diffSeconds / 2592000}mo ago"
            else -> "${diffSeconds / 31536000}y ago"
        }
    } catch (e: Exception) {
        "Unknown"
    }
}

@Composable
fun AdvancedMarqueeText(
    news: News,
    navHostController: NavHostController,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = TextStyle.Default,
    durationMillis: Int = 10000
) {
    var isPaused by remember { mutableStateOf(false) }
    val animOffset = remember { Animatable(0f) }

    var textWidth by remember { mutableStateOf(0f) }
    var containerWidth by remember { mutableStateOf(0f) }

    LaunchedEffect(isPaused) {
        while (true) {
            if (!isPaused) {
                // Nếu đã chạy hết thì mới reset lại
                if (animOffset.value <= -textWidth) {
                    animOffset.snapTo(10f)
                }
                animOffset.animateTo(
                    targetValue = -textWidth,
                    animationSpec = tween(durationMillis = durationMillis, easing = LinearEasing)
                )
            } else {
                delay(100)
            }
        }
    }

    Box(
        modifier = modifier
            .clipToBounds()
            .onGloballyPositioned {
                containerWidth = it.size.width.toFloat()
            }
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        isPaused = true
                        tryAwaitRelease()
                        isPaused = false
                    }
                )
            },
        contentAlignment = Alignment.CenterStart
    ) {
        Text(
            text = news.title,
            style = textStyle,
            maxLines = 1,
            softWrap = false,
            overflow = TextOverflow.Visible,
            modifier = Modifier
                .clickable {
                    navHostController.navigate("detail_news/${news.id}")
                }
                .wrapContentWidth()
                .offset { IntOffset(animOffset.value.toInt(), 0) }
                .onGloballyPositioned {
                    textWidth = it.size.width.toFloat()
                }
        )
    }
}

@Composable
fun AdvancedMarqueeTextList(
    items: List<News>,
    navHostController: NavHostController,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = TextStyle.Default,
    durationMillis: Int = 10000,
    separator: String = "   •   "
) {
    var isPaused by remember { mutableStateOf(false) }
    val animOffset = remember { Animatable(0f) }

    var contentWidth by remember { mutableStateOf(0f) }
    var containerWidth by remember { mutableStateOf(0f) }

    LaunchedEffect(isPaused, contentWidth) {
        while (true) {
            if (!isPaused && contentWidth > 0f && containerWidth > 0f) {
                if (animOffset.value <= -contentWidth) {
                    animOffset.snapTo(containerWidth)
                }
                animOffset.animateTo(
                    targetValue = -contentWidth,
                    animationSpec = tween(durationMillis = durationMillis, easing = LinearEasing)
                )
            } else {
                delay(100)
            }
        }
    }

    Box(
        modifier = modifier
            .clipToBounds()
            .onGloballyPositioned {
                containerWidth = it.size.width.toFloat()
            }
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        isPaused = true
                        tryAwaitRelease()
                        isPaused = false
                    }
                )
            },
        contentAlignment = Alignment.CenterStart
    ) {
        SubcomposeLayout { constraints ->
            val rowPlaceables = subcompose("content") {
                Row(
                    modifier = Modifier.wrapContentWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    items.forEachIndexed { index, news ->
                        Text(
                            text = news.title,
                            style = textStyle,
                            maxLines = 1,
                            softWrap = false,
                            overflow = TextOverflow.Visible,
                            modifier = Modifier
                                .clickable {
                                    navHostController.navigate("detail_news/${news.id}")
                                }
                        )
                        if (index != items.lastIndex) {
                            Text(
                                text = separator,
                                style = textStyle,
                                overflow = TextOverflow.Visible
                            )
                        }
                    }
                }
            }.map {
                it.measure(constraints.copy(minWidth = 0, maxWidth = Constraints.Infinity))
            }

            val maxWidth = rowPlaceables.maxOfOrNull { it.width } ?: 0
            val maxHeight = rowPlaceables.maxOfOrNull { it.height } ?: 0
            contentWidth = maxWidth.toFloat()

            layout(constraints.maxWidth, maxHeight) {
                rowPlaceables.forEach {
                    it.placeRelative(x = animOffset.value.toInt(), y = 0)
                }
            }
        }
    }
}
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ImageGrid(
    imageUrls: List<String>,
    modifier: Modifier = Modifier,
    onImageClick: ((String, Int) -> Unit)? = null
) {
    val maxImagesToShow = 5
    val extraImageCount = imageUrls.size - maxImagesToShow

    when {
        imageUrls.isEmpty() -> {
            // Handle empty state
        }

        imageUrls.size == 1 -> {
            // Single image - full width with proper aspect ratio
            Box(
                modifier = modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onImageClick?.invoke(imageUrls[0], 0) }
            ) {
                AsyncImage(
                    model = imageUrls[0],
                    contentDescription = "Post image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(4f / 3f) // Instagram-like aspect ratio
                        .background(Color.Gray.copy(alpha = 0.1f))
                )
            }
        }

        imageUrls.size == 2 -> {
            // Two images side by side
            Row(
                modifier = modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                imageUrls.forEachIndexed { index, url ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(if (index == 0) 12.dp else 0.dp, if (index == 1) 12.dp else 0.dp, if (index == 1) 12.dp else 0.dp, if (index == 0) 12.dp else 0.dp))
                            .clickable { onImageClick?.invoke(url, index) }
                    ) {
                        AsyncImage(
                            model = url,
                            contentDescription = "Post image ${index + 1}",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Gray.copy(alpha = 0.1f))
                        )
                    }
                }
            }
        }

        imageUrls.size == 3 -> {
            // Three images: one large on left, two stacked on right (Instagram style)
            Row(
                modifier = modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                // Large image on the left
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(12.dp, 0.dp, 0.dp, 12.dp))
                        .clickable { onImageClick?.invoke(imageUrls[0], 0) }
                ) {
                    AsyncImage(
                        model = imageUrls[0],
                        contentDescription = "Post image 1",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Gray.copy(alpha = 0.1f))
                    )
                }

                // Two smaller images stacked on the right
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .clip(RoundedCornerShape(0.dp, 12.dp, 0.dp, 0.dp))
                            .clickable { onImageClick?.invoke(imageUrls[1], 1) }
                    ) {
                        AsyncImage(
                            model = imageUrls[1],
                            contentDescription = "Post image 2",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Gray.copy(alpha = 0.1f))
                        )
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .clip(RoundedCornerShape(0.dp, 0.dp, 12.dp, 0.dp))
                            .clickable { onImageClick?.invoke(imageUrls[2], 2) }
                    ) {
                        AsyncImage(
                            model = imageUrls[2],
                            contentDescription = "Post image 3",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Gray.copy(alpha = 0.1f))
                        )
                    }
                }
            }
        }

        imageUrls.size == 4 -> {
            // Four images in 2x2 grid
            Column(
                modifier = modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    repeat(2) { index ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .clip(
                                    RoundedCornerShape(
                                        topStart = if (index == 0) 12.dp else 0.dp,
                                        topEnd = if (index == 1) 12.dp else 0.dp
                                    )
                                )
                                .clickable { onImageClick?.invoke(imageUrls[index], index) }
                        ) {
                            AsyncImage(
                                model = imageUrls[index],
                                contentDescription = "Post image ${index + 1}",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color.Gray.copy(alpha = 0.1f))
                            )
                        }
                    }
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    repeat(2) { index ->
                        val imageIndex = index + 2
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .clip(
                                    RoundedCornerShape(
                                        bottomStart = if (index == 0) 12.dp else 0.dp,
                                        bottomEnd = if (index == 1) 12.dp else 0.dp
                                    )
                                )
                                .clickable { onImageClick?.invoke(imageUrls[imageIndex], imageIndex) }
                        ) {
                            AsyncImage(
                                model = imageUrls[imageIndex],
                                contentDescription = "Post image ${imageIndex + 1}",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color.Gray.copy(alpha = 0.1f))
                            )
                        }
                    }
                }
            }
        }

        imageUrls.size >= 5 -> {
            // Five or more images: 2x2 grid with overlay on last image
            val displayedImages = imageUrls.take(maxImagesToShow)

            Column(
                modifier = modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                // First row with 2 images
                Row(
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    repeat(2) { index ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .clip(
                                    RoundedCornerShape(
                                        topStart = if (index == 0) 12.dp else 0.dp,
                                        topEnd = if (index == 1) 12.dp else 0.dp
                                    )
                                )
                                .clickable { onImageClick?.invoke(displayedImages[index], index) }
                        ) {
                            AsyncImage(
                                model = displayedImages[index],
                                contentDescription = "Post image ${index + 1}",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color.Gray.copy(alpha = 0.1f))
                            )
                        }
                    }
                }

                // Second row with 3 images (last one with overlay)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    repeat(3) { index ->
                        val imageIndex = index + 2
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .clip(
                                    RoundedCornerShape(
                                        bottomStart = if (index == 0) 12.dp else 0.dp,
                                        bottomEnd = if (index == 2) 12.dp else 0.dp
                                    )
                                )
                                .clickable { onImageClick?.invoke(displayedImages[imageIndex], imageIndex) }
                        ) {
                            AsyncImage(
                                model = displayedImages[imageIndex],
                                contentDescription = "Post image ${imageIndex + 1}",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color.Gray.copy(alpha = 0.1f))
                            )

                            // Overlay for the last image showing extra count
                            if (index == 2 && extraImageCount > 0) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(
                                            Brush.verticalGradient(
                                                colors = listOf(
                                                    Color.Transparent,
                                                    Color.Black.copy(alpha = 0.7f)
                                                )
                                            )
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = "+$extraImageCount",
                                            color = Color.White,
                                            fontSize = 24.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "more",
                                            color = Color.White.copy(alpha = 0.9f),
                                            fontSize = 12.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}



