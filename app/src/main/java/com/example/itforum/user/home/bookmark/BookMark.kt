package com.example.itforum.user.home.bookmark

import android.content.SharedPreferences
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.itforum.user.modelData.request.GetPostRequest
import com.example.itforum.user.post.PostListScreen
import com.example.itforum.user.post.viewmodel.PostViewModel


@Composable
fun BookMarkScreen(
    navHostController: NavHostController,
    sharedPreferences: SharedPreferences
) {
    val viewModel: PostViewModel = viewModel(factory = viewModelFactory {
        initializer { PostViewModel(navHostController, sharedPreferences) }
    })
    val bookmarkedPostIds = remember { mutableStateListOf<String>("") }
    val userId = sharedPreferences.getString("userId", null)

    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        val response = viewModel.getSavePost(userId)
        response?.postsId?.let { ids ->
            bookmarkedPostIds.clear()
            bookmarkedPostIds.addAll(ids)
        }
        isLoading = false
    }

    Column(modifier = Modifier.fillMaxSize().padding(top = 40.dp, bottom = 120.dp)) {
        SearchField()
        Spacer(Modifier.height(40.dp))

        if (isLoading) {
            Text("Loading bookmarks...")
        } else {
            PostListScreen(
                sharedPreferences = sharedPreferences,
                navHostController = navHostController,
                getPostRequest = GetPostRequest(postsId = bookmarkedPostIds, page = 1)
            )
        }
    }
}

@Composable
fun SearchField(){
    var postName by remember { mutableStateOf("") }
Box(
    modifier = Modifier
    .fillMaxWidth()
    .height(100.dp)
    .padding(horizontal = 16.dp),
    contentAlignment = Alignment.Center

){
    OutlinedTextField(
        value = postName,
        onValueChange = { postName = it },
        label = { Text("Search Your Book Marks") },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search"
            )
        },
        modifier = Modifier.fillMaxWidth()
    )
}
}