package com.example.itforum.user.effect

import androidx.compose.foundation.layout.padding
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.itforum.user.effect.model.UiState
import com.example.itforum.user.skeleton.SkeletonPost
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.Alignment

//@Composable
//fun UiStateMessage(uiState: UiState, canSubmit: Boolean) {
//    if (!canSubmit) return
//
//    when (uiState) {
//        is UiState.Loading -> {
//            SkeletonPost()
//        }
//
//        is UiState.Error -> {
//            Text(
//                text = uiState.message,
//                color = Color.Red,
//                modifier = Modifier.padding(8.dp)
//            )
//        }
//
//        is UiState.Success -> {
//            Text(
//                text = uiState.message,
//                color = Color.Green,
//                modifier = Modifier.padding(8.dp)
//            )
//        }
//
//        else -> return
//    }
//}
@Composable
fun UiStateMessage(
    uiState: UiState,
    canSubmit: Boolean,
    isLoginScreen: Boolean
) {
    if (!canSubmit) return

    when (uiState) {
        is UiState.Loading -> {
            if (isLoginScreen) {
                Box(
                    modifier = Modifier.padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                SkeletonPost()
            }
        }


        is UiState.Error -> {
            Text(
                text = uiState.message,
                color = Color.Red,
                modifier = Modifier.padding(8.dp)
            )
        }

        is UiState.Success -> {
            Text(
                text = uiState.message,
                color = Color.Green,
                modifier = Modifier.padding(8.dp)
            )
        }

        else -> return
    }
}
