package com.example.itforum.user.home.tag

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.itforum.user.home.myfeed.TagChip
import androidx.compose.material3.MaterialTheme

@Composable
fun TagScreen(){
    val tagList = listOf(
        TagModel("Kotlin","essse223"),
        TagModel("Jetpack Compose","essse223"),
        TagModel("Android","essse223"),
        TagModel("AI","essse223"),
        TagModel("Big Data","essse223")
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 30.dp, vertical = 50.dp)
    ) {
        item{
            Spacer(Modifier.height(10.dp))
            Text(text = "Trending Tags", fontWeight = FontWeight.Bold, fontSize = 30.sp)
            Spacer(Modifier.height(20.dp))
        }
        items(tagList.size) { i ->
            ListTagsWidget(tag = tagList[i], index = (i + 1).toString())
        }
        item{
            Spacer(Modifier.height(10.dp))
            Text(text = "Popular Tags", fontWeight = FontWeight.Bold, fontSize = 30.sp)
            Spacer(Modifier.height(20.dp))
        }
        items(tagList.size) { i ->
            ListTagsWidget(tag = tagList[i], index = (i + 1).toString())
        }
        item {
            Spacer(Modifier.height(30.dp))
            Text(text = "All Tags", fontWeight = FontWeight.Bold, fontSize = 30.sp)
            Spacer(Modifier.height(10.dp))

            AllTagsWidget(tags = tagList)
        }
    }
}
@Composable
fun ListTagsWidget(tag: TagModel, index: String){
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp)
    ) {
        Text(text = index, color = Color.Gray, fontSize = 18.sp)
        Spacer(Modifier.width(20.dp))
        Text(text = tag.name, fontSize = 18.sp, fontWeight = FontWeight.Bold)
    }
}
@Composable
fun AllTagsWidget(tags: List<TagModel>, modifier: Modifier = Modifier) {
    val groupedTags = tags.groupBy { it.name.first().uppercaseChar() }
    val sortedKeys = groupedTags.keys.sorted()

    Column(modifier = modifier.padding(16.dp)) {
        sortedKeys.forEach { key ->
            Text(
                text = key.toString(),
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            groupedTags[key]?.forEach { tag ->
                TagChip(
                    text = tag.name,
                    selected = false,
                    onSelected = { /* Toggle selection */ },
                    modifier = Modifier
                        .padding(vertical = 4.dp)
                        .wrapContentSize()
                )
            }
        }
    }
}
