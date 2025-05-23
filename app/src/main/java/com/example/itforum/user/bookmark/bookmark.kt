package com.example.itforum.user.bookmark

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.itforum.user.post.PostCard
import com.example.itforum.user.post.postList

@Composable
fun BookMarkScreen(){
    LazyColumn {
        item { SearchField() }
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