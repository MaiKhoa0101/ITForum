package com.example.itforum.admin.adminController

import android.content.SharedPreferences
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Interests
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Report
import androidx.compose.material.icons.filled.ReportGmailerrorred
import androidx.compose.material.icons.filled.ReportProblem
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavHostController
import com.example.itforum.admin.adminComplaint.viewmodel.ComplaintViewModel
import com.example.itforum.admin.adminReport.ReportAccount.model.response.ReportedUser
import com.example.itforum.admin.adminReport.ReportAccount.viewmodel.ReportViewModelFactory
import com.example.itforum.admin.adminReport.ReportAccount.viewmodel.ReportedUserViewModel
import com.example.itforum.admin.adminReport.ReportPost.model.response.ReportedPostList
import com.example.itforum.admin.adminReport.ReportPost.viewmodel.ReportedPostViewModel
import com.example.itforum.admin.adminReport.ReportPost.viewmodel.ReportedPostViewModelFactory
import com.example.itforum.repository.ReportPostRepository
import com.example.itforum.repository.ReportRepository
import com.example.itforum.retrofit.RetrofitInstance
import com.example.itforum.user.modelData.response.Complaint
import com.example.itforum.user.modelData.response.PostResponse
import com.example.itforum.user.post.viewmodel.PostViewModel
import com.example.itforum.user.userProfile.viewmodel.UserViewModel
import java.time.LocalDate
import java.time.OffsetDateTime


@Composable
fun ControllerManagerScreen(
    navHostController:NavHostController,
    sharedPreferences: SharedPreferences,
    modifier: Modifier
) {
    val userViewModel: UserViewModel = viewModel(factory = viewModelFactory {
        initializer { UserViewModel(sharedPreferences) }
    })
    val postViewModel: PostViewModel = viewModel(factory = viewModelFactory {
        initializer { PostViewModel(sharedPreferences) }
    })
    val complaintViewModel: ComplaintViewModel = viewModel()
    val rpPostViewModel: ReportedPostViewModel = viewModel(
        factory = ReportedPostViewModelFactory(
            ReportPostRepository(RetrofitInstance.reportPostService)
        )
    )
    val rpUserViewModel: ReportedUserViewModel = viewModel(factory = ReportViewModelFactory(
        ReportRepository(
            RetrofitInstance.reportAccountService)
        )
    )

    val listUser by userViewModel.listUser.collectAsState()
    val listPost by postViewModel.listPost.collectAsState()
    val listComplaint by complaintViewModel.listComplaint.collectAsState()
    val listRpPosts by rpPostViewModel.Posts.collectAsState()
    val listRpUsers by rpUserViewModel.users.collectAsState()

    var totalActiveUsers by remember { mutableIntStateOf(0) }
    var totalUsersBanned by remember { mutableIntStateOf(0) }
    var totalPostsNew by remember { mutableIntStateOf(0) }
    var totalComplaintsNew by remember { mutableIntStateOf(0) }
    var totalRpPostsNew by remember { mutableIntStateOf(0) }
    var totalRpUsersNew by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        userViewModel.getAllUser()
        postViewModel.getAllPost()
        complaintViewModel.getAllComplaint()
        rpPostViewModel.loadReportedPosts()
        rpUserViewModel.loadUsers()
    }

    LaunchedEffect(listUser, listComplaint, listPost, listRpPosts, listRpUsers) {
        totalActiveUsers = listUser.count { !it.isBanned }
        totalUsersBanned = listUser.count { it.isBanned }
        totalPostsNew = countItemsCreatedToday(listPost) { (it as PostResponse).createdAt }
        totalComplaintsNew = countItemsCreatedToday(listComplaint) { (it as Complaint).createdAt }
        totalRpPostsNew = countItemsCreatedToday(listRpPosts) { (it as ReportedPostList).createdAt }
        totalRpUsersNew = countItemsCreatedToday(listRpUsers) { (it as ReportedUser).createdAt }
    }

    LazyColumn (
        modifier = modifier.background(MaterialTheme.colorScheme.background)
    ){
        item {
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                modifier = Modifier.padding(10.dp),
                text = "Bảng điều khiển",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Black,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            ActiveAccount(totalActiveUsers)
            RevenueReportScreen()
            NotificationCard(totalUsersBanned, totalPostsNew, totalComplaintsNew, totalRpPostsNew, totalRpUsersNew)
            AppointmentRevenueCard()
        }
    }
}


@Composable
fun ActiveAccount(
    activeUsers: Int
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Column(
            modifier = Modifier.background(Color.White)
        )
        {
            Text(
                "Tài khoản đang hoạt động",
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp,
                color = Color.Gray
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .padding(10.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                InfoCard(
                    value = "$activeUsers",
                    title = "Người dùng đang hoạt động",
                    backgroundColor = Color(0xFF00BCD4),
                    icon = Icons.Default.Person
                )
            }
        }
    }
}

@Composable
fun InfoCard(
    value: String,
    title: String,
    backgroundColor: Color,
    icon: ImageVector
) {
    Box(
        modifier = Modifier
            .width(150.dp)
            .height(200.dp)
            .background(backgroundColor, shape = RoundedCornerShape(8.dp))
            .padding(4.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.height(100.dp)
        ) {
            Text(
                text = value,
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Text(
                text = title,
                fontSize = 12.sp,
                color = Color.White.copy(alpha = 0.8f),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
                    .background(Color.White.copy(alpha = 0.2f), shape = RoundedCornerShape(4.dp))
                    .padding(4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "More info",
                    fontSize = 10.sp,
                    color = Color.White
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
fun RevenueReportScreen() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(16.dp),
    ) {
        var selectedFilter by remember { mutableStateOf("Tháng") }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                "Báo cáo doanh thu",
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp,
                color = Color.Gray
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Lọc: ", color = Color.Gray)
                FilterDropdown(
                    options = listOf("Ngày", "Tuần", "Tháng", "Trước giờ"),
                    selectedOption = selectedFilter,
                    onOptionSelected = { selectedFilter = it }
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(horizontalAlignment = Alignment.Start) {
                Text("Tháng này", color = Color.Gray)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "$12,582",
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .background(Color(0xFFDFF5E6), shape = RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text("+15%", color = Color(0xFF27AE60), fontSize = 12.sp)
                    }
                }
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("Tháng trước", color = Color.Gray)
                Text(
                    text = "$98,741",
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "25.2%↑",
                color = Color(0xFF27AE60),
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("So với tháng trước", color = Color.Gray)
        }
    }
}

@Composable
fun NotificationCard(
    totalUsersBanned: Int,
    totalPostsNew: Int,
    totalComplaintsNew: Int,
    totalRpPostsNew: Int,
    totalRpUsersNew: Int,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(16.dp),
    ) {
        Text(
            text = "Thông báo",
            fontWeight = FontWeight.SemiBold,
            fontSize = 18.sp,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(12.dp))

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            NotificationItem(Icons.Default.Person, "$totalUsersBanned tài khoản bị khóa", Color(0xFFFFE5E5), Color.Black)
            NotificationItem(Icons.Default.Interests, "$totalPostsNew bài viết mới hôm nay", Color(0xFFB3E5FC), Color.Black)
            NotificationItem(Icons.Default.Report, "$totalComplaintsNew khiếu nại mới hôm nay", Color(0xFFE6F4FF), Color.Black)
            NotificationItem(Icons.Default.ReportGmailerrorred, "$totalRpPostsNew tố cáo bài viết mới hôm nay", Color(0xFFFFF4CC), Color.Black)
            NotificationItem(Icons.Default.ReportProblem, "$totalRpUsersNew tố cáo tài khoản mới hôm nay", Color(0xFFFFE5E5), Color.Black)
        }
    }
}

@Composable
fun NotificationItem(icon: ImageVector, text: String, backgroundColor: Color, iconColor: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconColor,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black
        )
    }
}

@Composable
fun AppointmentRevenueCard() {
    Card(
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(16.dp),
        colors = CardDefaults.cardColors(Color.Transparent)
    ) {
        Column() {
            var selectedFilter by remember { mutableStateOf("Tháng") }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "Doanh thu từ lịch hẹn",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp,
                    color = Color.Gray
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Lọc: ", color = Color.Gray)
                    FilterDropdown(
                        options = listOf("Ngày", "Tuần", "Tháng", "Trước giờ"),
                        selectedOption = selectedFilter,
                        onOptionSelected = { selectedFilter = it }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text("$895.02", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
            Text("doanh thu tháng này", fontSize = 14.sp, color = Color.Gray)

            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val path = Path().apply {
                        moveTo(0f, size.height * 0.7f)
                        quadraticBezierTo(size.width * 0.2f, size.height * 0.4f, size.width * 0.4f, size.height * 0.7f)
                        quadraticBezierTo(size.width * 0.6f, size.height * 0.4f, size.width * 0.8f, size.height * 0.7f)
                        quadraticBezierTo(size.width * 0.9f, size.height * 0.9f, size.width, size.height * 0.7f)
                    }
                    drawPath(
                        path = path,
                        color = Color(0xFF42A5F5),
                        style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
                    )
                }
            }
        }
    }
}

@Composable
fun FilterDropdown(
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFFEDEDED))
                .padding(horizontal = 8.dp, vertical = 4.dp)
                .clickable { expanded = true }
        ) {
            Text(selectedOption, color = Color(0xFF5C6BC0))
            Icon(Icons.Default.ArrowDropDown, contentDescription = null)
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onOptionSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

fun countItemsCreatedToday(list: List<Any>?, getCreatedAt: (Any) -> String?): Int {
    return list?.count { item ->
        try {
            val createdDate = OffsetDateTime.parse(getCreatedAt(item)).toLocalDate()
            createdDate == LocalDate.now()
        } catch (e: Exception) {
            false
        }
    } ?: 0
}