package com.example.itforum.user.setting

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.ModeNight
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.itforum.R

@Composable
fun Setting(navHostController: NavHostController,onToggleTheme:()->Unit, darkTheme:Boolean){
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(30.dp)
    ) {
        Icon(
            imageVector = Icons.Default.ArrowBackIosNew,
            contentDescription = "Settings",
            modifier = Modifier
                .size(30.dp)
                .clickable {
                    navHostController.popBackStack()
                }
        )
        Icon(
            imageVector =
                if (darkTheme == false) {
                    Icons.Default.LightMode
                }
                else {
                    Icons.Default.DarkMode
                }
            ,
            contentDescription = "Toggle Theme",
            modifier = Modifier
                .size(30.dp)
                .clickable {
                    onToggleTheme()
                }
        )

    }
}