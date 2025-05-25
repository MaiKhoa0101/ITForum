package com.example.itforum.user.effect

import androidx.compose.foundation.layout.padding
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun UiStateMessage(uiState: UiStateLogin, canSubmit: Boolean) {
    if (!canSubmit) return

    when (uiState) {
        is UiStateLogin.Loading -> {
            CircularProgressIndicator()
        }

        is UiStateLogin.Error -> {
            Text(
                text = uiState.message,
                color = Color.Red,
                modifier = Modifier.padding(8.dp)
            )
        }

        is UiStateLogin.Success -> {
            Text(
                text = uiState.message,
                color = Color.Green,
                modifier = Modifier.padding(8.dp)
            )
        }

        else -> Unit
    }
}
