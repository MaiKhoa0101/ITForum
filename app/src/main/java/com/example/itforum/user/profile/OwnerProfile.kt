package com.example.itforum.user.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserProfile(modifier: Modifier = Modifier, navHostController: NavHostController) {
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("Thông tin", "Bài viết")

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())

    Scaffold(
        modifier = Modifier
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            item { UserHeader() }

            stickyHeader {
                UserTabRow(
                    tabs = tabs,
                    selectedTabIndex = selectedTabIndex,
                    onTabSelected = { selectedTabIndex = it }
                )
            }

            when (selectedTabIndex) {
                0 -> {
                    item { UserInfoOverview(navHostController = navHostController) }
                    item { UserInfoDetail() }
                }
                1 -> {
                    item {
                        // TODO: Bài viết content
                        Text(
                            "Bài viết sẽ hiển thị ở đây",
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun UserHeader() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primaryContainer)
            .padding(horizontal = 10.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = "https://media.istockphoto.com/id/1197071216/photo/portrait-of-a-smart-and-handsome-it-specialist-wearing-glasses-smiles-behind-him-personal.jpg?s=612x612&w=0&k=20&c=Dy8TjvDmeXWhR6gAZ_OuqLu3ytUJmtycEYdVQenpWoI=",
                contentDescription = "Ảnh đại diện người dùng",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
            )
            Column(modifier = Modifier.padding(start = 16.dp)) {
                Text("Mai Khoa", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                Text("maikhoa@gmail.com")
                Text("Thạc sĩ Công nghệ thông tin")
                Text(
                    "Tham gia 5 năm trước",
                    fontStyle = FontStyle.Italic,
                    fontWeight = FontWeight.Light,
                    fontSize = 15.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
        UserStats()
        Spacer(modifier = Modifier.height(10.dp))
    }
}

@Composable
fun UserStats() {
    Column {
        Text("Giới thiệu")
        Text("10 Bài viết đã đăng")
        Text("100 Câu trả lời")
        Text("Được đánh giá 4.5 điểm")
        Text("Xếp hạng thứ 22 trong hệ thống")
    }
}

@Composable
fun UserTabRow(
    tabs: List<String>,
    selectedTabIndex: Int,
    onTabSelected: (Int) -> Unit
) {
    TabRow(
        selectedTabIndex = selectedTabIndex,
        containerColor = MaterialTheme.colorScheme.primaryContainer
    ) {
        tabs.forEachIndexed { index, title ->
            Tab(
                modifier = Modifier
                    .clip(RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp))
                    .background(
                    if (selectedTabIndex == index)
                        MaterialTheme.colorScheme.background
                    else{
                        MaterialTheme.colorScheme.primaryContainer}
                )
                ,
                selected = selectedTabIndex == index,
                onClick = { onTabSelected(index) },
                text = {
                    Text(
                        title,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontWeight = FontWeight.Bold
                    )
                }
            )
        }
    }
}


@Composable
fun UserInfoOverview(navHostController: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(20.dp))
        Text("Chi tiết Thông tin", fontWeight = FontWeight.Bold, fontSize = 20.sp)
        Spacer(modifier = Modifier.height(20.dp))
        Divider(thickness = 1.dp)
        Spacer(modifier = Modifier.height(10.dp))

        Box(modifier = Modifier
            .clip(CircleShape)
            .clickable(onClick = { navHostController.navigate("editprofile") })
            .width(150.dp)
            .background(MaterialTheme.colorScheme.primaryContainer)
            .padding(vertical = 10.dp)
        ) {
            Text("Chỉnh sửa", modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }

    }
}

@Composable
fun UserInfoDetail() {
    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .padding(bottom = 100.dp)
    ) {
        Text("Giới thiệu", fontWeight = FontWeight.Bold, fontSize = 20.sp)
        Text("Tôi là kĩ sư kiến trúc", fontSize = 15.sp)
        Spacer(modifier = Modifier.height(16.dp))

        Text("Bằng cấp & chứng chỉ", fontWeight = FontWeight.Bold, fontSize = 20.sp)
        Text("Thạc sĩ Công nghệ thông tin trường Đại học Công nghệ thông tinc", fontSize = 15.sp)
        Text("Thạc sĩ Công nghệ thông tin trường Đại học Công nghệ thông tin", fontSize = 15.sp)
        Spacer(modifier = Modifier.height(16.dp))

        Text("Nơi làm việc", fontWeight = FontWeight.Bold, fontSize = 20.sp)
        Text("Google", fontSize = 15.sp)
        Text("NASA", fontSize = 15.sp)
        Text("META", fontSize = 15.sp)
        Spacer(modifier = Modifier.height(16.dp))

        Text("Nơi làm việc", fontWeight = FontWeight.Bold, fontSize = 20.sp)
        Text("Google", fontSize = 15.sp)
        Text("NASA", fontSize = 15.sp)
        Text("META", fontSize = 15.sp)
        Spacer(modifier = Modifier.height(16.dp))

        val languages = listOf("Flutter", "HTML", "Java", "C++", "Python", "Kotlin")
        TagSection(title = "Ngôn ngữ sử dụng:", tags = languages)

    }
}


@Composable
fun TagSection(
    title: String,
    tags: List<String>
) {
    Column () {
        Text(
            text = title,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        FlowRow (
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            tags.forEach { tag ->
                TagItem(text = tag)
            }
        }
    }
}

@Composable
fun TagItem(text: String) {
    Box(
        modifier = Modifier
            .background(
                color = Color.Cyan,
                shape = RoundedCornerShape(50)
            )
            .width(100.dp)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text = text, color = Color.DarkGray)
    }
}