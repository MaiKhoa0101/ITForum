package com.example.itforum.admin.adminComplaint

import android.content.SharedPreferences
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.itforum.admin.adminComplaint.viewmodel.ComplaintViewModel
import com.example.itforum.user.modelData.response.Complaint
import com.example.itforum.user.post.IconWithText
import com.example.itforum.user.post.TopPost
import com.example.itforum.user.post.getTimeAgo
import com.example.itforum.user.profile.viewmodel.UserViewModel

@Composable
fun ManagementComplaintDetailScreen(
    navHostController: NavHostController,
    sharedPreferences: SharedPreferences,
    complaintId: String
){

    var complaintViewModel: ComplaintViewModel = viewModel()
    LaunchedEffect(Unit) {
        complaintViewModel.getByIdComplaint(complaintId)
    }

    val complaint by complaintViewModel.complaint.collectAsState()

    Column (
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF00AEFF))
    ) {
        TopPost("Chi tiết", "Xử lý",navHostController) {}
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFEEEEEE))
        ) {
            complaint?.let { FormComplaint(it,sharedPreferences) }
        }
    }
}

@Composable
fun FormComplaint(
    complaint: Complaint,
    sharedPreferences: SharedPreferences
) {
    var userViewModel: UserViewModel = viewModel(factory = viewModelFactory {
        initializer { UserViewModel(sharedPreferences) }
    })
    LaunchedEffect(complaint) {
         userViewModel.getUser(complaint.userId)
    }
    val user by userViewModel.user.collectAsState()
    Card(
        modifier = Modifier.padding(horizontal = 15.dp, vertical = 20.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        LazyColumn(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 25.dp),
            verticalArrangement = Arrangement.spacedBy(17.dp)
        ) {
            item{
                IconWithText(
                    avatar = "https://photo.znews.vn/w660/Uploaded/mdf_eioxrd/2021_07_06/2.jpg",
                    name = "Trạng thái: ${complaint.status}",
                    sizeIcon = 35.dp
                )
                IconWithText(
                    avatar = "https://photo.znews.vn/w660/Uploaded/mdf_eioxrd/2021_07_06/2.jpg",
                    name = "Ngày: ${ getTimeAgo(complaint.createdAt) } • ${""}",
                    sizeIcon = 35.dp
                )
                IconWithText(
                    avatar = "https://photo.znews.vn/w660/Uploaded/mdf_eioxrd/2021_07_06/2.jpg",
                    name = "Từ: ${user?.name}",
                    sizeIcon = 35.dp
                )
                IconWithText(
                    avatar = "https://photo.znews.vn/w660/Uploaded/mdf_eioxrd/2021_07_06/2.jpg",
                    name = "Tiêu đề: ${complaint.title}",
                    sizeIcon = 35.dp
                )
                IconWithText(
                    avatar = "https://photo.znews.vn/w660/Uploaded/mdf_eioxrd/2021_07_06/2.jpg",
                    name = "Nội dung:",
                    sizeIcon = 35.dp
                )

                Text(
                    text = complaint.reason,
                    fontSize = 20.sp,
                    modifier = Modifier.padding(start = 40.dp),
                    lineHeight = 30.sp
                )
                AsyncImage(
                    model = complaint.img,
                    contentDescription = "Ảnh minh chứng",
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(10.dp))
            }
        }
    }
}