package com.example.itforum.user.post

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.util.Log
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
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
import com.example.itforum.user.modelData.response.PostWithVote
import com.example.itforum.user.modelData.response.VoteResponse
import com.example.itforum.user.news.viewmodel.NewsViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PostListScreen(
    sharedPreferences: SharedPreferences,
    navHostController: NavHostController,
    getPostRequest: GetPostRequest
) {
    fun handleUpVote(viewModel: PostViewModel, postWithVote: PostWithVote, index: Int, scope: CoroutineScope) {
        scope.launch {
            val voteResponse = viewModel.votePost(
                postId = postWithVote.post.id,
                type = "upvote",
                index = index
            )
            voteResponse?.let { response ->
                postWithVote.vote?.data?.upVoteData?.total = response.data?.upvotes
                postWithVote.vote?.data?.userVote = response.data?.userVote
            }
        }
    }
    fun handleDownVote(viewModel: PostViewModel, postWithVote: PostWithVote, index: Int, scope: CoroutineScope) {
        scope.launch {
            val voteResponse = viewModel.votePost(
                postId = postWithVote.post.id,
                type = "downvote",
                index = index
            )
            voteResponse?.let { response ->
                postWithVote.vote?.data?.upVoteData?.total = response.data?.upvotes
                postWithVote.vote?.data?.userVote = response.data?.userVote
            }
        }
    }

    fun handleBookmark(viewModel: PostViewModel, postWithVote: PostWithVote, userId: String?, scope: CoroutineScope) {
        scope.launch {
            val bookmarkResponse = viewModel.savePost(
                postId = postWithVote.post.id,
                userId = userId
            )
            postWithVote.isBookMark = !postWithVote.isBookMark
        }
    }

    val viewModel: PostViewModel = viewModel(factory = viewModelFactory {
        initializer { PostViewModel(navHostController, sharedPreferences) }
    })

    val postsFromVm by viewModel.postsWithVotes.collectAsState()
    var postsWithVotes by remember { mutableStateOf(postsFromVm) }
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    val isLoadingMore by viewModel.isLoadingMore.collectAsState()
    var showCommentDialog by remember { mutableStateOf(false) }
    var selectedPostId by remember { mutableStateOf<String?>(null) }
    var userId = sharedPreferences.getString("userId", null)

    // Fetch posts when screen loads
    LaunchedEffect(getPostRequest) {
        postsWithVotes = emptyList()
        viewModel.fetchPosts(getPostRequest)
        Log.d("PostListScreen", "Fetching posts for request: $getPostRequest")

    }
    LaunchedEffect(postsFromVm) {
        postsWithVotes = postsFromVm
        Log.d("load page when postfromvm",postsWithVotes.toString())
    }// keep local data async

    // Pull to refresh state
    val pullRefreshState = rememberPullRefreshState(
        refreshing = isRefreshing,
        onRefresh = { viewModel.refreshPosts(getPostRequest) }
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pullRefresh(pullRefreshState)
    ) {
        if (postsWithVotes.isEmpty() && !isRefreshing && !isLoadingMore) {
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
        } else if (postsWithVotes.isEmpty() && !isRefreshing && !isLoadingMore) {
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
                    val scope = rememberCoroutineScope()
                    PostCardWithVote(
                        post = postWithVote.post,
                        vote = postWithVote.vote,
                        isBookMark = postWithVote.isBookMark,
                        onUpvoteClick = {
                            handleUpVote(viewModel, postWithVote, index, scope)
                        },
                        onDownvoteClick = {
                            handleDownVote(viewModel, postWithVote, index, scope)
                        },
                        onCommentClick = {
                            selectedPostId = postWithVote.post.id
                            showCommentDialog = true
                        },
                        onBookmarkClick = {
                            handleBookmark(viewModel, postWithVote, userId, scope)
                        },
                        onShareClick = { }
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

        // Pull refresh indicator
        PullRefreshIndicator(
            refreshing = isRefreshing,
            state = pullRefreshState,
            modifier = Modifier.align(Alignment.TopCenter)
        )

        if (showCommentDialog && selectedPostId != null) {
            Dialog(
                onDismissRequest = {
                    showCommentDialog = false
                    selectedPostId = null
                },
                properties = DialogProperties(
                    usePlatformDefaultWidth = false
                )
            ) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White,
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.9f)
                ) {
                    PostCommentScreen(
                        navController = navHostController,
                        postId = selectedPostId!!,
                        sharedPreferences = sharedPreferences
                    )
                }
            }
        }
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








