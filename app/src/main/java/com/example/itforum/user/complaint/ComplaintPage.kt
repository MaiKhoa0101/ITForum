package com.example.itforum.user.complaint

import android.content.SharedPreferences
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavHostController
import com.example.itforum.R
import com.example.itforum.admin.adminComplaint.viewmodel.ComplaintViewModel
import com.example.itforum.user.effect.model.UiState
import com.example.itforum.user.modelData.request.ComplaintRequest
import com.example.itforum.user.post.BottomBorder
import com.example.itforum.user.post.IconWithText
import com.example.itforum.user.post.ImgOrVdMedia
import com.example.itforum.user.post.TopBorder
import com.example.itforum.user.post.TopPost
import com.example.itforum.user.post.WritePost
import com.example.itforum.user.profile.viewmodel.UserViewModel

@Composable
fun ComplaintPage(
    navHostController: NavHostController,
    sharedPreferences: SharedPreferences
){
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        val context = LocalContext.current
        var title by remember { mutableStateOf("") }
        var content by remember { mutableStateOf("") }
        var imageUrl by remember  { mutableStateOf<Uri?>(null) }

        var userViewModel: UserViewModel = viewModel(factory = viewModelFactory {
            initializer { UserViewModel(sharedPreferences) }
        })
        var complaintViewModel: ComplaintViewModel = viewModel()

        val userInfo by userViewModel.user.collectAsState()
        val uiState by complaintViewModel.uiStateCreate.collectAsState()
        var showSuccessDialog by remember { mutableStateOf(false) }
        var enable by remember { mutableStateOf<Boolean>(true) }

        LaunchedEffect(uiState) {
            println("UI State duoc thay doi")
            if (uiState is UiState.Success) {
                println("uiState là success")
                showSuccessDialog = true
            }else if(uiState is UiState.Loading){
                enable = false
            }
        }
        // UI hiển thị
        if (showSuccessDialog) {
            SuccessDialog(
                message = "Cảm ơn bạn đã góp ý!",
                onDismiss = {
                    showSuccessDialog = false
                    navHostController.navigate("home")
                }
            )
        }
        LaunchedEffect(Unit) {
            userViewModel.getUser()
        }

        LazyColumn (
            modifier = Modifier.fillMaxSize()
        ) {
            stickyHeader { TopPost("Góp ý", "Gửi",navHostController, enable, uiState) {
                complaintViewModel.createComplaint(
                    ComplaintRequest(
                        userId = userInfo?.id ?: "",
                        title = title,
                        reason = content,
                        img = imageUrl
                    ),
                    context
                )
                Log.d("Img", imageUrl.toString())
            }
            }
            item {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.secondaryContainer),
                ) {
                    userInfo?.let { IconWithText(avatar = it.avatar, name = it.name) }
                    TitleChild(){ title=it }
                    WritePost(){input ->
                        content = input
                    }
                    AddImage(){imageUrl=it}
                }
            }
        }
    }
}

@Composable
fun TitleChild(
    onChange: (String) -> Unit
){
    var text by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }
    var isFocused by remember { mutableStateOf(false) }
    OutlinedTextField(
        value = text,
        onValueChange = {
            text = it
            onChange(text)
            isError = it.trim().isEmpty()
        },
        placeholder = { Text("Nhập tiêu đề", color = MaterialTheme.colorScheme.onBackground, fontSize = 16.sp) },
        shape = RoundedCornerShape(7.dp),
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedBorderColor = MaterialTheme.colorScheme.onSecondaryContainer
        ),
        textStyle = TextStyle(color = MaterialTheme.colorScheme.onBackground),
        modifier = Modifier
            .padding(horizontal = 10.dp)
            .onFocusChanged { focusState ->
                isFocused = focusState.isFocused
                // Khi mất focus, reset lỗi
                if (!focusState.isFocused) {
                    isError = false
                }
            }
    )
}

@Composable
fun AddImage(
    onChange: (Uri?) -> Unit
) {
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let {
            imageUri = it
            onChange(it)
        }
    }

    Column(
        modifier = Modifier
            .padding(horizontal = 10.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(color = MaterialTheme.colorScheme.background)
            .fillMaxWidth()
            .TopBorder()
            .BottomBorder()
    ) {
        if (imageUri == null) {
            IconButton(
                onClick = {
                    launcher.launch(arrayOf("image/*"))
                }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Image,
                        contentDescription = "Thêm ảnh",
                        tint = MaterialTheme.colorScheme.secondaryContainer,
                        modifier = Modifier.size(130.dp)
                    )
                    Text(
                        "Thêm ảnh nếu có",
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.width(130.dp)
                    )
                }
            }
        } else {
            Box(modifier = Modifier.fillMaxWidth()) {
                ImgOrVdMedia(
                    type = "image",
                    index = 0,
                    uri = imageUri!!,
                    ListUri = listOf(imageUri!!),
                    removeUri = {
                        imageUri = null
                    }
                )
            }
        }
    }
}

@Composable
fun SuccessDialogExample() {
    var showDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button (onClick = { showDialog = true }) {
            Text("Hiển thị thông báo")
        }

        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = {
                    Text(text = "Thành công", fontWeight = FontWeight.Bold)
                },
                text = {
                    Text("Thao tác của bạn đã được thực hiện thành công!")
                },
                confirmButton = {
                    TextButton(onClick = { showDialog = false }) {
                        Text("Đồng ý")
                    }
                }
            )
        }
    }
}

@Composable
fun SuccessDialog(
    title: String = "Thành công",
    color: Color = Color(0xFF3EB641),
    message: String = "Thao tác của bạn đã được thực hiện thành công!",
    nameButton: String = "Quay về trang chủ",
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.icon_success),
                    contentDescription = "Success",
                    modifier = Modifier.size(30.dp),
                    tint = Color.Unspecified
                )
                Spacer(modifier = Modifier.width(5.dp))
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 30.sp,
                    color = color
                )
            }
                },
        text = {
            Text(
                text = message,
                fontSize = 18.sp
            )
               },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(nameButton)
            }
        },
        containerColor = Color.White
    )
}