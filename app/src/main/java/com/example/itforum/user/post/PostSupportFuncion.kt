package com.example.itforum.user.post

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import java.time.Duration
import java.time.Instant

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