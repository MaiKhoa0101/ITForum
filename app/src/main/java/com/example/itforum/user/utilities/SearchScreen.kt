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
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.itforum.R

@Composable
fun SearchScreen(modifier: Modifier) {
    // ✅ Biến state cho ô tìm kiếm
    var searchQuery by remember { mutableStateOf("") }

    val historyItems = listOf("Machine learning", "pytorch", "deep learning", "CNN là gì")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        TopAppBar(
            backgroundColor = MaterialTheme.colorScheme.primaryContainer,
            modifier = Modifier.height(130.dp)
        ){
            Headbar(searchQuery,{searchQuery=it})
        }
        Text(
            text = "Lịch sử tìm kiếm",
            modifier = Modifier.padding(16.dp),
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        LazyColumn {
            items(historyItems) { keyword ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            searchQuery = keyword // ✅ Click là gán vào ô tìm kiếm
                        }
                        .padding(8.dp)
                ) {
                    Icon(Icons.Default.History, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(keyword)
                }
            }
        }
    }
}

@Composable
fun Headbar(searchQuery: String, onChange: (String) -> Unit) {

    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextField(
            modifier = Modifier
                .padding(horizontal=20.dp)
                .fillMaxWidth(),
            value = searchQuery,
            onValueChange = {
                onChange(it)        // call parent callback
            },
            placeholder = {
                Text(
                    "Tìm kiếm",
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )
            },
            leadingIcon = {
                Image(
                    painter = painterResource(id = R.drawable.searchicon),
                    contentDescription = "Icon tìm kiếm",
                    modifier = Modifier.size(20.dp)
                )
            },
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = Color.White,
                focusedIndicatorColor = Color.White,
                unfocusedIndicatorColor = Color.White
            ),
            shape = RoundedCornerShape(12.dp),
            singleLine = true
        )
    }
}