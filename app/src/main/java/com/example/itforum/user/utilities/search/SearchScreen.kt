package com.example.itforum.utilities

import android.content.SharedPreferences
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.itforum.user.utilities.search.SearchHistoryManager
import kotlinx.coroutines.launch
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import com.example.itforum.user.home.tag.AllTagsWidget
import com.example.itforum.user.home.tag.TagRepository
import androidx.compose.material.icons.filled.Search
import androidx.navigation.NavHostController
import com.example.itforum.user.home.tag.TagModel
import com.example.itforum.user.home.tag.ViewModel.TagViewModel
import com.example.itforum.user.modelData.response.PostWithVote
import com.example.itforum.user.modelData.response.TagItem
import com.example.itforum.user.post.PostCardWithVote
import com.example.itforum.user.post.viewmodel.PostViewModel
import com.example.itforum.user.utilities.search.SearchViewModel

@Composable
fun SearchScreen(
    modifier: Modifier = Modifier,
    viewModel: SearchViewModel,
    postViewModel: PostViewModel,
    navHostController: NavHostController,
    sharedPreferences: SharedPreferences,
    tagViewModel: TagViewModel
) {
    val context = LocalContext.current

    val userId = remember {
        sharedPreferences.getString("userId", null)
    }
    var searchQuery by remember { mutableStateOf("") }
    var historyItems by remember { mutableStateOf<List<String>>(emptyList()) }
    val coroutineScope = rememberCoroutineScope()
    val tagList by tagViewModel.tagList.collectAsState()
    var showMenu by remember { mutableStateOf(false) }
    var selectedTabIndex by remember { mutableStateOf(0) }

    // Observe search results for logging
    val postsByTitle = viewModel.postsByTitle.collectAsState()
    val postsByTags = viewModel.postsByTags.collectAsState()


    // Load history when screen loads
    LaunchedEffect(userId) {
        if (userId != null) {
            tagViewModel.getAllTags()
            historyItems = SearchHistoryManager.getSearchHistory(context, userId)
        }

    }


    // When searchQuery changes, trigger both searches and log results
    LaunchedEffect(searchQuery) {
        if (searchQuery.isNotBlank()) {
            viewModel.resultByTitle(searchQuery)
            viewModel.resultByTags(searchQuery)
        }
    }

    // Log results every time they update
    LaunchedEffect(postsByTitle.value) {
        Log.d("SearchScreen", "Posts By Title: ${postsByTitle.value}")
    }
    LaunchedEffect(postsByTags.value) {
        Log.d("SearchScreen", "Posts By Tags: ${postsByTags.value}")
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .width(300.dp)
    ) {
        TopAppBar(
            backgroundColor = MaterialTheme.colorScheme.primaryContainer,
            modifier = Modifier.height(130.dp)
        ) {
            Box(modifier = Modifier.weight(1f)) {
                Headbar(
                    searchQuery = searchQuery,
                    onChange = { searchQuery = it },
                    onSearch = { query ->
                        if (userId != null) {
                            coroutineScope.launch {
                                SearchHistoryManager.addSearchQuery(context, userId, query)
                                historyItems = SearchHistoryManager.getSearchHistory(context, userId)
                            }
                        }
                        searchQuery = query
                    }

                )
            }

            Box {
                IconButton(onClick = { showMenu = true }) {
                    Icon(Icons.Default.MoreVert, contentDescription = "Menu")
                }

                DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                    DropdownMenuItem(onClick = {
                        if (userId != null) {
                            coroutineScope.launch {
                                SearchHistoryManager.clearHistory(context, userId)
                                historyItems = SearchHistoryManager.getSearchHistory(context, userId)
                            }
                        }
                        showMenu = false
                    }) {
                        Text("Xoá tất cả lịch sử")
                    }

                }
            }
        }

        // TabRow for switching between "By Title" and "By Tags"
        TabRow(
            selectedTabIndex = selectedTabIndex,
            modifier = Modifier.fillMaxWidth(),
            backgroundColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ) {
            Tab(
                selected = selectedTabIndex == 0,
                onClick = { selectedTabIndex = 0 },
                text = { Text("Theo tiêu đề") }
            )
            Tab(
                selected = selectedTabIndex == 1,
                onClick = { selectedTabIndex = 1 },
                text = { Text("Theo tags") }
            )
        }

        // Content based on selected tab
        val currentResults = if (selectedTabIndex == 0) postsByTitle.value else postsByTags.value
        val searchTitle = if (selectedTabIndex == 0) "Search Results by Title" else "Search Results by Tags"

        if (searchQuery.isNotBlank() && currentResults.isNotEmpty()) {
            ResultSearchWidget(
                postsWithVote = currentResults,
                title = searchTitle,
                navHostController = navHostController,
                sharedPreferences = sharedPreferences,
                postViewModel = postViewModel
            )
        } else {
            SearchHistoryAndTags(
                historyItems = historyItems,
                tagList = tagList,
                onHistoryClick = { keyword -> searchQuery = keyword },
                onTagClick = { tag ->
                    searchQuery = tag
                    if (userId != null) {
                        coroutineScope.launch {
                            SearchHistoryManager.addSearchQuery(context, userId, tag)
                            historyItems = SearchHistoryManager.getSearchHistory(context, userId)
                        }
                    }
                },

                onDeleteHistory = { keyword ->
                    if (userId != null) {
                        coroutineScope.launch {
                            SearchHistoryManager.removeSearchQuery(context, userId, keyword)
                            historyItems = SearchHistoryManager.getSearchHistory(context, userId)
                        }
                    }
                }

            )
        }
    }
}

@Composable
private fun SearchHistoryAndTags(
    historyItems: List<String>,
    tagList: List<TagItem>,
    onHistoryClick: (String) -> Unit,
    onTagClick: (String) -> Unit,
    onDeleteHistory: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 10.dp)
    ) {
        item {
            Text(
                text = "Lịch sử tìm kiếm",
                modifier = Modifier.padding(top = 16.dp),
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        items(historyItems) { keyword ->
            HistoryItem(
                keyword = keyword,
                onHistoryClick = onHistoryClick,
                onDeleteHistory = onDeleteHistory
            )
        }

        item {
            Text(
                text = "Hashtags",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                modifier = Modifier.padding(top = 16.dp, bottom = 8.dp),
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        item {
            AllTagsWidget(
                tags = tagList,
                onTagClick = onTagClick
            )
        }
    }
}

@Composable
private fun HistoryItem(
    keyword: String,
    onHistoryClick: (String) -> Unit,
    onDeleteHistory: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Default.History, contentDescription = null)
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = keyword,
            fontSize = 20.sp,
            modifier = Modifier
                .weight(1f)
                .clickable { onHistoryClick(keyword) }
        )
        IconButton(onClick = { onDeleteHistory(keyword) }) {
            Icon(Icons.Default.Delete, contentDescription = "Xoá mục này")
        }
    }
}
@Composable
fun ResultSearchWidget(postsWithVote: List<PostWithVote>, title: String,navHostController: NavHostController,sharedPreferences: SharedPreferences,postViewModel: PostViewModel) {
    val listState = rememberLazyListState()

    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {

        item {
            Text(
                text = title,
                modifier = Modifier.padding(16.dp),
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        items(postsWithVote) { postWithVote ->
            PostCardWithVote(
                post = postWithVote.post,
                vote = postWithVote.vote,
                isBookMark = postWithVote.isBookMark,
                onUpvoteClick = {postViewModel.handleUpVote("upvote",-1,postWithVote.post.id)},
                onCommentClick = {},
                onBookmarkClick = {postViewModel.handleBookmark(-1,postWithVote.post.id,sharedPreferences.getString("userId", null))},
                onDownvoteClick = {postViewModel.handleDownVote("downvote",-1,postWithVote.post.id)},
                onCardClick = {  navHostController.navigate("detail_post/${postWithVote.post.id}")},
                onReportClick = {},
                onShareClick = {},
                navHostController = navHostController,
                sharedPreferences = sharedPreferences

            )
        }
    }
}
@Composable
fun Headbar(
    searchQuery: String,
    onChange: (String) -> Unit,
    onSearch: suspend (String) -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val coroutineScope = rememberCoroutineScope()

    TextField(
        modifier = Modifier
            .padding(horizontal = 20.dp)
            .fillMaxWidth(),
        value = searchQuery,
        onValueChange = onChange,
        placeholder = {
            Text("Tìm kiếm", fontSize = 20.sp)
        },
        leadingIcon = {
            // Replace with your icon
            Icon(Icons.Default.Search, contentDescription = "Tìm kiếm", modifier = Modifier.size(20.dp))
        },
        singleLine = true,
        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(
            onSearch = {
                keyboardController?.hide()
                if (searchQuery.isNotBlank()) {
                    coroutineScope.launch { onSearch(searchQuery) }
                }
            }
        ),
        colors = TextFieldDefaults.textFieldColors(
            backgroundColor = Color.White,
            focusedIndicatorColor = Color.White,
            unfocusedIndicatorColor = Color.White
        ),
        shape = RoundedCornerShape(12.dp)
    )
}
