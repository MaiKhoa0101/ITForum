package com.example.itforum.user.home.myfeed

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.foundation.clickable
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.Icon
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MyFeedScreen( modifier: Modifier = Modifier) {
    val tagRepository = rememberTagRepository()
    var feedName by remember { mutableStateOf(TextFieldValue("My Feed")) }
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Suggested", "My tags")
    var searchQuery by remember { mutableStateOf(TextFieldValue("")) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Feed Name Input
        OutlinedTextField(
            value = feedName,
            onValueChange = { feedName = it },
            label = { Text("Feed Name") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        // Tags Search
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("Tags") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search"
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        // Tab Selector
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            tabs.forEachIndexed { index, title ->
                val isSelected = selectedTab == index
                Box(
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .clip(RoundedCornerShape(50))
                        .background(
                            if (isSelected) Color(0xFF1CC3FF)
                            else Color.Transparent
                        )
                        .clickable { selectedTab = index }
                        .padding(8.dp)
                ) {
                    Text(
                        text = title,
                        color = if (isSelected) Color.Black else Color.Gray,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        // Tag Content based on selected tab
        Box(modifier = Modifier.weight(1f)) {
            when (selectedTab) {
                0 -> SuggestedTagsContent(tagRepository)
                1 -> MyTagsContent(tagRepository)
            }
        }

        // Apply Button
        Button(
            onClick = { /* Apply logic */ },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1CC3FF))
        ) {
            Text("Áp Dụng", fontSize = 25.sp,)

        }
    }
}

@Composable
fun SuggestedTagsContent(tagRepository: TagRepository) {
    val suggestedTags = tagRepository.getSuggestedTags()

    LazyColumn {
        items(suggestedTags.chunked(3)) { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                row.forEach { tag ->
                    TagChip(
                        text = tag,
                        selected = false,
                        onSelected = { /* Toggle selection */ },
                        modifier = Modifier.weight(1f)
                    )
                }
                // Fill empty spaces to maintain grid
                repeat(3 - row.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
fun MyTagsContent(tagRepository: TagRepository) {
    val myTags = tagRepository.getMyTags()
    val groupedTags = myTags.groupBy { it.first().uppercase() }
    val sortedKeys = groupedTags.keys.sorted()

    LazyColumn {
        sortedKeys.forEach { key ->
            item {
                Text(
                    text = key,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }

            items(groupedTags[key] ?: emptyList()) { tag ->
                TagChip(
                    text = tag,
                    selected = true,
                    onSelected = { /* Toggle selection */ },
                    modifier = Modifier.wrapContentWidth()
                )
                Spacer(modifier = Modifier.height(4.dp))
            }
        }
    }
}

@Composable
fun TagChip(
    text: String,
    selected: Boolean,
    onSelected: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .clickable { onSelected(!selected) },
        color = MaterialTheme.colorScheme.secondaryContainer,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = text,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                modifier = Modifier.padding(end = 4.dp),
                fontWeight = FontWeight.Bold
            )
            if (selected) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Remove tag",
                    tint = Color.Black,
                    modifier = Modifier.size(16.dp)
                )
            } else {
                Text(
                    text = "+",
                    color = Color.Black
                )
            }
        }
    }
}