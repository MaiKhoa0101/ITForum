package com.example.itforum.user.post

import android.annotation.SuppressLint
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.zIndex
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import coil.compose.AsyncImage
import com.example.itforum.user.modelData.response.MediaItem
import com.example.itforum.user.modelData.response.MediaType
import com.example.itforum.user.modelData.response.PostWithVote
import com.example.itforum.user.post.viewmodel.PostViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.Instant
// funcion to create media type for images and vid
fun createMediaItems(imageUrls: List<String>?, videoUrls: List<String>?): List<MediaItem> {
    val mediaItems = mutableListOf<MediaItem>()

    imageUrls?.forEach { url ->
        mediaItems.add(MediaItem(url, MediaType.IMAGE))
    }

    videoUrls?.forEach { url ->
        mediaItems.add(MediaItem(url, MediaType.VIDEO))
    }

    return mediaItems
}
@androidx.annotation.OptIn(UnstableApi::class)
@Composable
fun VideoPlayer(
    videoUrl: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val mediaItem = androidx.media3.common.MediaItem.fromUri(videoUrl)
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            setMediaItem(mediaItem)
            prepare()
        }
    }

    DisposableEffect(
        AndroidView(
            factory = {
                PlayerView(context).apply {
                    player = exoPlayer
                    useController = true
                    resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
                }
            },
            modifier = modifier
        )
    ) {
        onDispose {
            exoPlayer.release()
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
            diffSeconds < 60 -> "Vừa xong"
            diffSeconds < 3600 -> "${diffSeconds / 60}p trước"
            diffSeconds < 86400 -> "${diffSeconds / 3600}h trước"
            diffSeconds < 2592000 -> "${diffSeconds / 86400}d trước"
            diffSeconds < 31536000 -> "${diffSeconds / 2592000}mo trước"
            else -> "${diffSeconds / 31536000}y trước"
        }
    } catch (e: Exception) {
        "Unknown"
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MediaGrid(
    mediaItems: List<MediaItem>,
    modifier: Modifier = Modifier,
    onMediaClick: ((MediaItem, Int) -> Unit)? = null
) {
    val maxMediaToShow = 5
    val extraMediaCount = mediaItems.size - maxMediaToShow

    when {
        mediaItems.isEmpty() -> {
            // Handle empty state
        }

        mediaItems.size == 1 -> {
            // Single media - full width with proper aspect ratio
            Box(
                modifier = modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onMediaClick?.invoke(mediaItems[0], 0) }
            ) {
                MediaItemContent(
                    mediaItem = mediaItems[0],
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(4f / 3f)
                )
            }
        }

        mediaItems.size == 2 -> {
            // Two media items side by side
            Row(
                modifier = modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                mediaItems.forEachIndexed { index, mediaItem ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(if (index == 0) 12.dp else 0.dp, if (index == 1) 12.dp else 0.dp, if (index == 1) 12.dp else 0.dp, if (index == 0) 12.dp else 0.dp))
                            .clickable { onMediaClick?.invoke(mediaItem, index) }
                    ) {
                        MediaItemContent(
                            mediaItem = mediaItem,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        }

        mediaItems.size == 3 -> {
            // Three media items: one large on left, two stacked on right
            Row(
                modifier = modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                // Large media on the left (first item)
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(topStart = 12.dp, bottomStart = 12.dp))
                        .clickable { onMediaClick?.invoke(mediaItems[0], 0) }
                ) {
                    MediaItemContent(
                        mediaItem = mediaItems[0],
                        modifier = Modifier.fillMaxSize()
                    )
                }

                // Two smaller media items stacked on the right (second and third items)
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(1f), // Add aspectRatio to match the left side
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    // Second item (top right)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .clip(RoundedCornerShape(topEnd = 12.dp))
                            .clickable { onMediaClick?.invoke(mediaItems[1], 1) }
                    ) {
                        MediaItemContent(
                            mediaItem = mediaItems[1],
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    // Third item (bottom right)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .clip(RoundedCornerShape(bottomEnd = 12.dp))
                            .clickable { onMediaClick?.invoke(mediaItems[2], 2) }
                    ) {
                        MediaItemContent(
                            mediaItem = mediaItems[2],
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        }


        mediaItems.size == 4 -> {
            // Four media items in 2x2 grid
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
                                .clickable { onMediaClick?.invoke(mediaItems[index], index) }
                        ) {
                            MediaItemContent(
                                mediaItem = mediaItems[index],
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    repeat(2) { index ->
                        val mediaIndex = index + 2
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
                                .clickable { onMediaClick?.invoke(mediaItems[mediaIndex], mediaIndex) }
                        ) {
                            MediaItemContent(
                                mediaItem = mediaItems[mediaIndex],
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }
            }
        }

        mediaItems.size >= 5 -> {
            // Five or more media items: 2x2 grid with overlay on last item
            val displayedMedia = mediaItems.take(maxMediaToShow)

            Column(
                modifier = modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                // First row with 2 media items
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
                                .clickable { onMediaClick?.invoke(displayedMedia[index], index) }
                        ) {
                            MediaItemContent(
                                mediaItem = displayedMedia[index],
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }

                // Second row with 3 media items (last one with overlay)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    repeat(3) { index ->
                        val mediaIndex = index + 2
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
                                .clickable { onMediaClick?.invoke(displayedMedia[mediaIndex], mediaIndex) }
                        ) {
                            MediaItemContent(
                                mediaItem = displayedMedia[mediaIndex],
                                modifier = Modifier.fillMaxSize()
                            )

                            // Overlay for the last media showing extra count
                            if (index == 2 && extraMediaCount > 0) {
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
                                            text = "+$extraMediaCount",
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

@Composable
fun MediaItemContent(
    mediaItem: MediaItem,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.background(Color.Gray.copy(alpha = 0.1f))) {
        when (mediaItem.type) {
            MediaType.IMAGE -> {
                AsyncImage(
                    model = mediaItem.url,
                    contentDescription = "Post image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
            MediaType.VIDEO -> {
                // Video thumbnail or placeholder
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black),
                    contentAlignment = Alignment.Center
                ) {
                    // You can load video thumbnail here using Coil or Glide
                    // For now, showing a play button overlay
                    AsyncImage(
                        model = mediaItem.url, // This might be a video thumbnail URL
                        contentDescription = "Video thumbnail",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

                    // Play button overlay
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(
                                Color.Black.copy(alpha = 0.6f),
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.PlayArrow,
                            contentDescription = "Play video",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MediaDetailDialog(
    mediaItems: List<MediaItem>,
    initialIndex: Int = 0,
    onDismiss: () -> Unit
) {
    var currentIndex by remember { mutableStateOf(initialIndex) }
    val pagerState = rememberPagerState(
        initialPage = initialIndex,
        pageCount = { mediaItems.size }
    )

    LaunchedEffect(pagerState.currentPage) {
        currentIndex = pagerState.currentPage
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        ) {
            // Close button
            IconButton(
                onClick = onDismiss,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
                    .zIndex(1f)
            ) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = "Close",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }

            // Media counter
            Text(
                text = "${currentIndex + 1} / ${mediaItems.size}",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 24.dp)
                    .zIndex(1f)
            )

            // Media pager
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable { onDismiss() },
                    contentAlignment = Alignment.Center
                ) {
                    when (mediaItems[page].type) {
                        MediaType.IMAGE -> {
                            AsyncImage(
                                model = mediaItems[page].url,
                                contentDescription = "Image ${page + 1}",
                                contentScale = ContentScale.Fit,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clickable(
                                        indication = null,
                                        interactionSource = remember { MutableInteractionSource() }
                                    ) { /* Prevent dismiss when clicking image */ }
                            )
                        }
                        MediaType.VIDEO -> {
                            VideoPlayer(
                                videoUrl = mediaItems[page].url,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clickable(
                                        indication = null,
                                        interactionSource = remember { MutableInteractionSource() }
                                    ) { /* Prevent dismiss when clicking video */ }
                            )
                        }
                    }
                }
            }

            // Page indicator dots (only show if more than 1 media item)
            if (mediaItems.size > 1) {
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 32.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    repeat(mediaItems.size) { index ->
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(
                                    if (index == currentIndex) Color.White
                                    else Color.White.copy(alpha = 0.4f)
                                )
                        )
                    }
                }
            }
        }
    }
}
@Composable
fun PostMediaSection(
    imageUrls: List<String>?,
    videoUrls: List<String>?,
    modifier: Modifier = Modifier
) {
    var showMediaDetail by remember { mutableStateOf(false) }
    var selectedMediaIndex by remember { mutableStateOf(0) }
    val mediaItems = createMediaItems(imageUrls, videoUrls)

    // Post media (images and videos)
    if (mediaItems.isNotEmpty()) {
        MediaGrid(
            mediaItems = mediaItems,
            modifier = modifier,
            onMediaClick = { _, index ->
                selectedMediaIndex = index
                showMediaDetail = true
            }
        )
        Spacer(modifier = Modifier.height(12.dp))
    }

    // Media detail dialog
    if (showMediaDetail && mediaItems.isNotEmpty()) {
        MediaDetailDialog(
            mediaItems = mediaItems,
            initialIndex = selectedMediaIndex,
            onDismiss = { showMediaDetail = false }
        )
    }
}
// Option Dialog
// Option Dialog
@Composable
fun OptionDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onShowReport: () -> Unit,
    onEditPost: () -> Unit,
    onDeletePost: () -> Unit,
    isMyPost: Boolean = false
) {
    if (showDialog) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Options") },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onShowReport,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Tố cáo")
                    }
                    Button(
                        onClick = onEditPost,
                        enabled = isMyPost,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            Color.Yellow
                        )
                    ) {
                        Text("Sửa bài viết")
                    }
                    Button(
                        onClick = onDeletePost,
                        enabled = isMyPost,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                           Color.Red
                        )
                    ) {
                        Text("Xóa")
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text("Cancel")
                }
            }
        )
    }
}

// Confirm Delete Dialog
@Composable
fun ConfirmDeleteDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    if (showDialog) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Delete Post") },
            text = {
                Text(
                    "Are you sure you want to delete this post?",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                )
            },
            confirmButton = {
                Button(
                    onClick = onConfirm,
                    colors = ButtonDefaults.buttonColors(
                        Color.Red
                    )
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text("Cancel")
                }
            }
        )
    }
}
