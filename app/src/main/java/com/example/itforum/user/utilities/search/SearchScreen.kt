package com.example.itforum.utilities

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.itforum.R
import com.example.itforum.user.utilities.search.SearchHistoryManager
import kotlinx.coroutines.launch
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import com.example.itforum.user.home.tag.AllTagsWidget
import com.example.itforum.user.home.tag.TagRepository
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState

@Composable
fun SearchScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }
    var historyItems by remember { mutableStateOf<List<String>>(emptyList()) }
    val coroutineScope = rememberCoroutineScope()
    val tagList = TagRepository.tagList
    var showMenu by remember { mutableStateOf(false) }

    // Load history when screen loads
    LaunchedEffect(true) {
        historyItems = SearchHistoryManager.getSearchHistory(context)
    }

    Column(
        modifier = Modifier
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
                        SearchHistoryManager.addSearchQuery(context, query)
                        historyItems = SearchHistoryManager.getSearchHistory(context)
                    }
                )
            }

            Box {
                IconButton(onClick = { showMenu = true }) {
                    Icon(Icons.Default.MoreVert, contentDescription = "Menu")
                }

                DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                    DropdownMenuItem(onClick = {
                        coroutineScope.launch {
                            SearchHistoryManager.clearHistory(context)
                            historyItems = SearchHistoryManager.getSearchHistory(context)
                        }
                        showMenu = false
                    }) {
                        Text("Xoá tất cả lịch sử")
                    }
                }
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
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
                        modifier = Modifier.weight(1f).clickable {
                            searchQuery = keyword
                        }
                    )
                    IconButton(onClick = {
                        coroutineScope.launch {
                            SearchHistoryManager.removeSearchQuery(context, keyword)
                            historyItems = SearchHistoryManager.getSearchHistory(context)
                        }
                    }) {
                        Icon(Icons.Default.Delete, contentDescription = "Xoá mục này")
                    }
                }
            }

            item {
                Text(
                    text = "Hashtags",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                )
            }

            item {
                AllTagsWidget(
                    tags = tagList,
                    onTagClick = { tag ->
                        searchQuery = tag
                        coroutineScope.launch {
                            SearchHistoryManager.addSearchQuery(context, tag)
                            historyItems = SearchHistoryManager.getSearchHistory(context)
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun Headbar(
    searchQuery: String,
    onChange: (String) -> Unit,
    onSearch: suspend (String) -> Unit
) {
    val context = LocalContext.current
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
            Image(
                painter = painterResource(id = R.drawable.searchicon),
                contentDescription = "Icon tìm kiếm",
                modifier = Modifier.size(20.dp)
            )
        },
        singleLine = true,
        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(
            onSearch = {
                keyboardController?.hide()
                if (searchQuery.isNotBlank()) {
                    coroutineScope.launch {
                        onSearch(searchQuery)
                    }
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

//package com.example.itforum.utilities
//
//import androidx.compose.foundation.Image
//import androidx.compose.foundation.background
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.items
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material.*
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.History
//import androidx.compose.material.icons.filled.MoreVert
//import androidx.compose.material.icons.filled.Delete
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.res.painterResource
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import com.example.itforum.R
//import com.example.itforum.user.utilities.search.SearchHistoryManager
//import kotlinx.coroutines.launch
//import androidx.compose.ui.text.input.ImeAction
//import androidx.compose.ui.platform.LocalSoftwareKeyboardController
//import androidx.compose.ui.text.input.TextFieldValue
//import androidx.compose.foundation.text.KeyboardActions
//import androidx.compose.foundation.text.KeyboardOptions
//import com.example.itforum.user.home.tag.AllTagsWidget
//import com.example.itforum.user.home.tag.TagRepository
//import androidx.compose.foundation.verticalScroll
//import androidx.compose.foundation.rememberScrollState
//
//@Composable
//fun SearchScreen(modifier: Modifier = Modifier) {
//    val context = LocalContext.current
//    var searchQuery by remember { mutableStateOf("") }
//    var historyItems by remember { mutableStateOf<List<String>>(emptyList()) }
//    val coroutineScope = rememberCoroutineScope()
//    val tagList = TagRepository.tagList
//    var showMenu by remember { mutableStateOf(false) }
//
//    // Load history when screen loads
//    LaunchedEffect(true) {
//        historyItems = SearchHistoryManager.getSearchHistory(context)
//    }
//
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .background(Color.White)
//            .width(300.dp)
//    ) {
//        TopAppBar(
//            backgroundColor = MaterialTheme.colorScheme.primaryContainer,
//            modifier = Modifier.height(130.dp)
//        ) {
//            Box(modifier = Modifier.weight(1f)) {
//                Headbar(
//                    searchQuery = searchQuery,
//                    onChange = { searchQuery = it },
//                    onSearch = { query ->
//                        SearchHistoryManager.addSearchQuery(context, query)
//                        historyItems = SearchHistoryManager.getSearchHistory(context)
//                    }
//                )
//            }
//
//            IconButton(onClick = {
//                if (searchQuery.isNotBlank()) {
//                    coroutineScope.launch {
//                        SearchHistoryManager.addSearchQuery(context, searchQuery)
//                        historyItems = SearchHistoryManager.getSearchHistory(context)
//                    }
//                }
//            }) {
//                Icon(
//                    painter = painterResource(id = R.drawable.searchicon),
//                    contentDescription = "Tìm kiếm",
//                    modifier = Modifier
//                        .size(25.dp)
//                        .offset(x = (-18).dp)
//                )
//            }
//
//            Box {
//                IconButton(onClick = { showMenu = true }) {
//                    Icon(Icons.Default.MoreVert, contentDescription = "Menu")
//                }
//
//                DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
//                    DropdownMenuItem(onClick = {
//                        coroutineScope.launch {
//                            SearchHistoryManager.clearHistory(context)
//                            historyItems = SearchHistoryManager.getSearchHistory(context)
//                        }
//                        showMenu = false
//                    }) {
//                        Text("Xoá tất cả lịch sử")
//                    }
//                }
//            }
//        }
//
//        LazyColumn(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(horizontal = 16.dp)
//        ) {
//            item {
//                Text(
//                    text = "Lịch sử tìm kiếm",
//                    modifier = Modifier.padding(top = 16.dp),
//                    fontWeight = FontWeight.Bold,
//                    fontSize = 20.sp,
//                    color = MaterialTheme.colorScheme.onBackground
//                )
//            }
//
//            items(historyItems) { keyword ->
//                Row(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(8.dp),
//                    verticalAlignment = Alignment.CenterVertically
//                ) {
//                    Icon(Icons.Default.History, contentDescription = null)
//                    Spacer(modifier = Modifier.width(8.dp))
//                    Text(
//                        text = keyword,
//                        modifier = Modifier.weight(1f).clickable {
//                            searchQuery = keyword
//                        }
//                    )
//                    IconButton(onClick = {
//                        coroutineScope.launch {
//                            SearchHistoryManager.removeSearchQuery(context, keyword)
//                            historyItems = SearchHistoryManager.getSearchHistory(context)
//                        }
//                    }) {
//                        Icon(Icons.Default.Delete, contentDescription = "Xoá mục này")
//                    }
//                }
//            }
//
//            item {
//                Text(
//                    text = "Hashtags",
//                    fontWeight = FontWeight.Bold,
//                    fontSize = 20.sp,
//                    modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
//                )
//            }
//
//            item {
//                AllTagsWidget(
//                    tags = tagList,
//                    onTagClick = { tag ->
//                        searchQuery = tag
//                        coroutineScope.launch {
//                            SearchHistoryManager.addSearchQuery(context, tag)
//                            historyItems = SearchHistoryManager.getSearchHistory(context)
//                        }
//                    }
//                )
//            }
//        }
//    }
//}
//
//@Composable
//fun Headbar(
//    searchQuery: String,
//    onChange: (String) -> Unit,
//    onSearch: suspend (String) -> Unit
//) {
//    val context = LocalContext.current
//    val keyboardController = LocalSoftwareKeyboardController.current
//    val coroutineScope = rememberCoroutineScope()
//
//    TextField(
//        modifier = Modifier
//            .padding(horizontal = 20.dp)
//            .fillMaxWidth(),
//        value = searchQuery,
//        onValueChange = onChange,
//        placeholder = {
//            Text("Tìm kiếm", fontSize = 15.sp)
//        },
//        leadingIcon = {
//            Image(
//                painter = painterResource(id = R.drawable.searchicon),
//                contentDescription = "Icon tìm kiếm",
//                modifier = Modifier.size(20.dp)
//            )
//        },
//        singleLine = true,
//        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search),
//        keyboardActions = KeyboardActions(
//            onSearch = {
//                keyboardController?.hide()
//                if (searchQuery.isNotBlank()) {
//                    coroutineScope.launch {
//                        onSearch(searchQuery)
//                    }
//                }
//            }
//        ),
//        colors = TextFieldDefaults.textFieldColors(
//            backgroundColor = Color.White,
//            focusedIndicatorColor = Color.White,
//            unfocusedIndicatorColor = Color.White
//        ),
//        shape = RoundedCornerShape(12.dp)
//    )
//}
//
//
//
//
