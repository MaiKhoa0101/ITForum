package com.example.itforum.admin.components

import androidx.compose.runtime.compositionLocalOf

val LocalDrawerOpener = compositionLocalOf<() -> Unit> {
    error("Drawer opener not provided")
}