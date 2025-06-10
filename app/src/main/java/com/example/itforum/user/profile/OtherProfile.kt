package com.example.itforum.user.profile

import android.content.SharedPreferences
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavHostController
import com.example.itforum.user.Analytics.logScreenView
import com.example.itforum.user.modelData.response.UserProfileResponse
import com.example.itforum.user.profile.viewmodel.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OtherUserProfileScreen(
    sharedPreferences: SharedPreferences,
    navHostController: NavHostController,
    modifier: Modifier = Modifier,
    viewModel: UserViewModel = viewModel(factory = viewModelFactory {
        initializer { UserViewModel(sharedPreferences) }
    })
) {
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("Thông tin", "Bài viết")
    val user by viewModel.user.collectAsState()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())


    LaunchedEffect(Unit) { viewModel.getUser() }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = {},
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                scrollBehavior = scrollBehavior
            )
        }
    ) { innerPadding ->
        OtherProfileContent(
            user = user,
            selectedTabIndex = selectedTabIndex,
            onTabSelected = { selectedTabIndex = it },
            navController = navHostController,
            modifier = Modifier.padding(innerPadding),
            tabs = tabs
        )
    }
}


@Composable
fun OtherProfileContent(
    user: UserProfileResponse?,
    selectedTabIndex: Int,
    onTabSelected: (Int) -> Unit,
    navController: NavHostController,
    modifier: Modifier = Modifier,
    tabs: List<String>
) {
    LazyColumn(modifier = modifier.fillMaxSize()) {
        item { UserHeader(user) }

        stickyHeader {
            UserTabRow(
                tabs = tabs,
                selectedTabIndex = selectedTabIndex,
                onTabSelected = onTabSelected
            )
        }

        when (selectedTabIndex) {
            0 -> {
                item { OtherInfoOverview() }
                item { UserInfoDetail(user) }
            }
            1 -> {
                item {
                    Text("Bài viết sẽ hiển thị ở đây", modifier = Modifier.padding(16.dp))
                }
            }
        }
    }
}



@Composable
fun OtherInfoOverview() {
    Column(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(20.dp))
        Text("Chi tiết Thông tin", fontWeight = FontWeight.Bold, fontSize = 20.sp)
        Spacer(modifier = Modifier.height(10.dp))
        Divider(thickness = 1.dp)
    }
}
