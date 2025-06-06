package com.example.itforum.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    //Màu chủ đạo app xanh
    primaryContainer = MainTheme,
    //Đối tượng trên nền màu chủ đạo
    onPrimaryContainer = Color.Black,
    //Màu nền phần nội dung
    background = Color(0xFF2A2A2A),
    //Đối tượng trên background
    onBackground = Color(0xFFFFFFFF),
    //Màu nền nội dung cấp 2
    secondary = BoxGrey,
    onSecondary = secondDarkContainer,
    secondaryContainer = Color.White,
    )

private val LightColorScheme = lightColorScheme(
    //Màu chủ đạo app xanh
    primaryContainer = MainTheme,
    //Đối tượng trên nền màu chủ đạo
    onPrimaryContainer = Color.Black,
    //Màu nền phần nội dung
    background = Color.White,
    //Đối tượng trên background
    onBackground = Color.Black,
    //Màu nền nội dung cấp 2
    secondary = BoxLightGrey,
    onSecondary = Color.Black,
    secondaryContainer = secondContainer,
    onSecondaryContainer = Color.White,


)

@Composable
fun ITForumTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}