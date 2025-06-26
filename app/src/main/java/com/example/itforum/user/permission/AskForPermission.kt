package com.example.itforum.user.permission

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController

@Composable
fun RequestPermissionUI() {
    val context = LocalContext.current
    var requestRight by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        requestRight = checkPermission(context)
    }

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            Toast.makeText(context, "Đã cấp quyền thông báo", Toast.LENGTH_SHORT).show()
            requestRight = checkPermission(context)
        } else {
            Toast.makeText(context, "Từ chối quyền thông báo", Toast.LENGTH_SHORT).show()
        }
    }
    val readMediaImagesPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ){ isGranted ->
        if (isGranted) {
            Toast.makeText(context, "Đã cấp quyền truy cập ảnh", Toast.LENGTH_SHORT).show()
            requestRight = checkPermission(context)
        } else {
            Toast.makeText(context, "Từ chối quyền truy cập ảnh", Toast.LENGTH_SHORT).show()
        }
    }
    // Show dialog when permission is needed
    if (showDialog) {
        println("Duoc goi quyen thong bao")
        when (requestRight) {
            "Request Notification Permission" -> {
                println("Co goi quuyen thong bao")

                PopUpRequestRight(
                    icon = Icons.Default.Notifications,
                    name = "Thông báo",
                    description = "Cần quyền cho phép gửi thông báo để tiếp tục",
                    onAllow = {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            notificationPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                        } else {
                            Toast.makeText(context, "Thông báo đã được bật", Toast.LENGTH_SHORT).show()
                            showDialog = false
                        }
                    },
                    onSkip = {
                        showDialog = false
                    },
                    onDismiss = {
                        showDialog = false
                    }
                )
            }
            "Request Read Media Images Permission" -> {
                PopUpRequestRight(
                    icon = Icons.Default.Image,
                    name = "Ảnh và phương tiện",
                    description = "Cần quyền truy cập tệp ảnh trong máy",
                    onAllow = {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            readMediaImagesPermissionLauncher.launch(android.Manifest.permission.READ_MEDIA_IMAGES)
                    }
                    },
                    onSkip = {
                        showDialog = false
                    },
                    onDismiss = {
                        showDialog = false
                    }
                )
            }
        }
    }

    // Show success message when all permissions are granted
    if (requestRight == "All right request") {
        LaunchedEffect(requestRight) {
            Toast.makeText(context, "Tất cả quyền đã được cấp!", Toast.LENGTH_SHORT).show()
        }
    }
}



//Kiểm tra có quyền đó chưa
fun checkPermission(context: Context): String {
    println("Vào check permission")

    // Kiểm tra quyền thông báo (chỉ từ Android 13+)
    if (ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_DENIED
    ) {
        return "Request Notification Permission"
    }

    if (ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.READ_MEDIA_IMAGES
        ) == PackageManager.PERMISSION_DENIED
    ){
        return "Request Read Media Images Permission"
    }

    return "All right request"
}



@Composable
fun PopUpRequestRight(
    icon: ImageVector,
    name: String,
    description: String,
    onAllow: () -> Unit,
    onSkip: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .fillMaxHeight(0.6f)
                .clip(RoundedCornerShape(20.dp))

                .background(MaterialTheme.colorScheme.secondaryContainer)
                .padding(horizontal = 40.dp, vertical= 50.dp)

            ,
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceAround
        ) {
            Image(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.fillMaxWidth(0.8f)
            )

            Text(
                text = name,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                textAlign = TextAlign.Center
            )

            Text(
                text = description,
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(20.dp))
            Column (
                modifier= Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = onAllow,
                    modifier = Modifier
                        .height(40.dp)
                        .fillMaxWidth(0.8f),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text("Cho phép")
                }
                Spacer(modifier = Modifier.height(20.dp))
                Button(
                    onClick = onSkip,
                    modifier = Modifier
                        .height(40.dp)
                        .border(1.dp, Color(0xFFF58229), RoundedCornerShape(20.dp))
                        .fillMaxWidth(0.8f),
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.background),
                ) {
                    Text("Bỏ qua", color = MaterialTheme.colorScheme.onSecondaryContainer)
                }
            }
        }
    }
}


