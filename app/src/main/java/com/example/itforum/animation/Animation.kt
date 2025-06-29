package com.example.itforum.animation

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import kotlinx.coroutines.delay

@Composable
fun AnimatedVoteIcon(
    isActive: Boolean,
    triggerChange: Boolean,
    activeColor: Color,
    defaultColor: Color,
    icon: ImageVector,
    onAnimationEnd: () -> Unit = {}
) {
    // Transition control
    val transition = updateTransition(targetState = triggerChange, label = "VoteTransition")

    val size by transition.animateDp(
        transitionSpec = {
            keyframes {
                if (isActive) {
                    durationMillis = 500
                    50.dp at 150 using FastOutSlowInEasing
                    40.dp at 350
                }
                else {
                    durationMillis = 450
                    40.dp at 150 using FastOutSlowInEasing
                    30.dp at 350
                }
            }
        },
        label = "SizeAnimation"
    ) { state ->
        if (isActive && state) 40.dp else 30.dp
    }

    val color by transition.animateColor(
        transitionSpec = {
            keyframes {
                if (isActive) {
                    durationMillis = 450
                    defaultColor at 150 using FastOutSlowInEasing
                    Color.Yellow at 200
                    activeColor at 400
                }
                else {
                    durationMillis = 450
                    activeColor at 150 using FastOutSlowInEasing
                    defaultColor at 400
                }
            }
        },
        label = "ColorAnimation"
    ) { state ->
        if (isActive && state) activeColor else defaultColor
    }

    Icon(
        imageVector = icon,
        contentDescription = null,
        modifier = Modifier.size(size),
        tint = color
    )

}


@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AnimatedVoteNumber(
    voteCount: Int,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = TextStyle.Default,
    textColor: Color = Color.Black
) {
    AnimatedContent(
        targetState = voteCount,
        transitionSpec = {
            if (targetState > initialState) {
                // Tăng số → lăn lên
                slideInVertically { height -> -height } + fadeIn() with
                        slideOutVertically { height -> height } + fadeOut()
            } else {
                // Giảm số → lăn xuống
                slideInVertically { height -> height } + fadeIn() with
                        slideOutVertically { height -> - height } + fadeOut()
            }.using(SizeTransform(clip = false))
        },
        modifier = modifier
    ) { targetCount ->
        Text(
            text = targetCount.toString(),
            style = textStyle,
            color = textColor
        )
    }
}

