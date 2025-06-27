package com.example.itforum.user.news

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import androidx.navigation.NavHostController
import com.example.itforum.user.modelData.response.News
import kotlinx.coroutines.delay


@Composable
fun AdvancedMarqueeTextList(
    items: List<News>,
    navHostController: NavHostController,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = TextStyle.Default,
    durationMillis: Int = 3000,
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
                    animationSpec = tween(durationMillis = durationMillis*items.size, easing = LinearEasing)
                )
            } else {
                delay(100)
            }
        }
    }

    Box(
        modifier = modifier
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
                            color = MaterialTheme.colorScheme.onBackground,
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

