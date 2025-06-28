package com.example.itforum.user.root

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.itforum.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBarRoot(
    navHostController : NavHostController,
    onMenuClick: () -> Unit
) {
    var selectedTabIndex by remember { mutableStateOf(0) }
    val roles = listOf("My feed", "Tag", "Follow", "Bookmark")

    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.primaryContainer)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        // Top Bar: App name and icons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(R.drawable.utilities),
                    contentDescription = "Menu",
                    modifier = Modifier
                        .size(30.dp)
                        .clickable { onMenuClick() }
                )


            }

            Text(
                text = "Appname",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimary
            )


        }
        Spacer(modifier = Modifier.height(8.dp))
        // Tab Bar
        TabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = Color.Transparent,
            modifier = Modifier.padding(horizontal = 8.dp),
            indicator = {}
        ) {
            roles.forEachIndexed { index, title ->
                Tab(
                    modifier = Modifier
                        .clip(RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp))
                        .background(
                            if (selectedTabIndex == index)
                                MaterialTheme.colorScheme.background
                            else
                                MaterialTheme.colorScheme.primaryContainer
                        )
                        ,
                    selected = selectedTabIndex == index,
                    onClick = {
                        selectedTabIndex = index
                        when (index) {
                            0 -> navHostController.navigate("home")
                            1 -> navHostController.navigate("tag")
                            2 -> navHostController.navigate("follow")
                            3 -> navHostController.navigate("bookmark")
                        }

                              },
                    text = {
                        Text(
                            text = title,
                            color = MaterialTheme.colorScheme.onBackground,
                            fontSize = MaterialTheme.typography.titleSmall.fontSize.times(0.9f)
                        )
                    }
                )
            }
        }
    }
}
