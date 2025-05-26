package com.example.itforum.user.profile

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable


import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.itforum.R


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfile(navHostController: NavHostController){
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Edit Profile", fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                navigationIcon = {
                    Icon(
                        painter = painterResource(R.drawable.arrow_back),
                        contentDescription = "Back",
                        tint = Color.White,
                        modifier = Modifier
                            .size(50.dp)
                            .clickable {
                            navHostController.popBackStack()
                        }
                    )
                },
                scrollBehavior = scrollBehavior
            )
        }
    ) { innerPadding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            item {
                EditProfileBody()
            }
        }
    }
}

@Composable
fun ChangeAvatar(
    imageUri: Uri?,
    onImageSelected: (Uri) -> Unit)
{
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { onImageSelected(it) }
    }
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)

    ) {
        Box(contentAlignment = Alignment.Center,
            modifier = Modifier.clickable {
                launcher.launch("image/*")
            }
        ) {
            if (imageUri != null) {
                AsyncImage(
                    model = imageUri,
                    contentDescription = "avatar",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .border(2.dp, Color.White, CircleShape)
                )
            } else {
                AsyncImage(
                    model = R.drawable.avt,
                    contentDescription = "Avatar",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .border(2.dp, Color.White, CircleShape)
                )
            }
            Icon(
                painter = painterResource(R.drawable.camera),
                contentDescription = "Change Avatar",
                tint = Color.White,
                modifier = Modifier
                    .size(36.dp)
                    .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                    .padding(6.dp)
                    .clip(CircleShape)
            )
        }
    }
}
@Composable
fun EditProfileBody(){
    var avatarURL by remember { mutableStateOf<Uri?>(null) }
    var nameInput by remember { mutableStateOf("Tên") }
    var usernameInput by remember { mutableStateOf("Tên người dùng") }
    var emailInput by remember { mutableStateOf("Email") }
    var phoneInput by remember { mutableStateOf("SDT") }
    var descriptionInput by remember { mutableStateOf("Mô tả") }
    var passInput by remember { mutableStateOf("Mk") }
    var repassInput by remember { mutableStateOf("Mk") }

    ChangeAvatar(
        imageUri =avatarURL,
        onImageSelected = { avatarURL = it })
    FieldText(
        title = "Họ và tên",
        placeHolder = "Tên của bạn",
        text = nameInput,
        onTextChange = { nameInput = it }
    )
    FieldText(
        title = "Tên người dùng",
        placeHolder = "username123",
        text = usernameInput,
        onTextChange = { usernameInput = it }
    )
    FieldText(
        title = "Email",
        placeHolder = "user@gmail.com",
        text = emailInput,
        onTextChange = { emailInput = it }
    )
    FieldText(
        title = "Số điện thoại",
        placeHolder = "0912345678",
        text = phoneInput,
        onTextChange = { phoneInput = it }
    )
    FieldText(
        title = "Mật khẩu của bạn",
        placeHolder = "**********",
        text = passInput,
        onTextChange = { passInput = it }
    )
}

@Composable
fun FieldText(
    title: String,
    placeHolder: String,
    text: String,
    onTextChange: (String) -> Unit
) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = title,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        TextField(
            value = text,
            onValueChange = onTextChange,
            modifier = Modifier.fillMaxWidth()
        )
    }
}