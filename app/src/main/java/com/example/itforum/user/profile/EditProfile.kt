package com.example.itforum.user.profile

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable


import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.itforum.R
import com.example.itforum.user.effect.model.UiState
import com.example.itforum.user.model.request.UserUpdateRequest
import com.example.itforum.user.model.response.Certificate
import com.example.itforum.user.model.response.Skill
import com.example.itforum.user.model.response.UserProfileResponse
import com.example.itforum.user.profile.viewmodel.UserViewModel

class EditProfileViewModel : ViewModel() {

    // Dữ liệu hiện tại
    var avatarURL by mutableStateOf<Uri?>(null)
    var nameInput by mutableStateOf("")
    var usernameInput by mutableStateOf("")
    var emailInput by mutableStateOf("")
    var phoneInput by mutableStateOf("")
    var passInput by mutableStateOf("")
    var repassInput by mutableStateOf("")
    var introduce by mutableStateOf("")
    var skillsInput by mutableStateOf<List<Skill>?>(null)
    var certificateInput by mutableStateOf<List<Certificate>?>(null)

    // Lưu trữ dữ liệu gốc để so sánh
    private var originalAvatarURL: Uri? = null
    private var originalName: String = ""
    private var originalUsername: String = ""
    private var originalEmail: String = ""
    private var originalPhone: String = ""
    private var originalIntroduce: String = ""



    var originalSkills: List<Skill>? = null
    var originalCertificate: List<Certificate>? = null



    // Hàm để lưu dữ liệu gốc khi load user info
    fun setOriginalData(user: UserProfileResponse?) {
        user?.let {
            originalAvatarURL = it.avatar?.toUri()
            originalName = it.name ?: ""
            originalUsername = it.username ?: ""
            originalAvatarURL = it.avatar?.toUri()
            originalEmail = it.email ?: ""
            originalPhone = it.phone ?: ""
            originalIntroduce = it.introduce ?: ""
            originalSkills = it.skill
            originalCertificate = it.certificate

            // Set current values
            avatarURL = originalAvatarURL
            nameInput = originalName
            usernameInput = originalUsername
            emailInput = originalEmail
            phoneInput = originalPhone
            introduce = originalIntroduce
            skillsInput = originalSkills
            certificateInput = originalCertificate
        }
    }

    fun saveChanges(userViewModel: UserViewModel, context: Context) {
        val request = UserUpdateRequest(
            // Chỉ gửi các trường đã thay đổi
            name = if (nameInput != originalName && nameInput.isNotBlank()) nameInput else null,
            username = if (usernameInput != originalUsername && usernameInput.isNotBlank()) usernameInput else null,
            email = if (emailInput != originalEmail && emailInput.isNotBlank()) emailInput else null,
            phone = if (phoneInput != originalPhone && phoneInput.isNotBlank()) phoneInput else null,
            introduce = if (introduce != originalIntroduce && introduce.isNotBlank()) introduce else null,
            avatar = if (avatarURL != originalAvatarURL) avatarURL else null,
            password = passInput.takeIf { it.isNotBlank() }, // Password luôn gửi nếu có
            certificate = if (certificateInput != originalCertificate) certificateInput else null,
            skills = if (skillsInput != originalSkills) skillsInput else null,
        )

        userViewModel.ModifierUser(request, context)
    }
}
@Composable
fun EditProfileBody(user: UserProfileResponse?,viewModel: EditProfileViewModel) {
    var tempSkills = ""
    var tempCertificate = ""
    ChangeAvatar(
        user?.avatar,
        imageUri = viewModel.avatarURL,
        onImageSelected = { viewModel.avatarURL = it }
    )
    FieldText("Giới thiệu", "Tôi có đam mê...", viewModel.introduce) { viewModel.introduce = it }
    FieldText("Họ và tên", "Tên của bạn", viewModel.nameInput) { viewModel.nameInput = it }
    FieldText("Tên người dùng", "username123", viewModel.usernameInput) { viewModel.usernameInput = it }
    FieldText("Email", "user@gmail.com", viewModel.emailInput) { viewModel.emailInput = it }
    FieldText("Số điện thoại", "0912345678", viewModel.phoneInput) { viewModel.phoneInput = it }
    FieldText("Mật khẩu", "********", viewModel.passInput) { viewModel.passInput = it }
    FieldText("Nhập lại mật khẩu", "********", viewModel.repassInput) { viewModel.repassInput = it }
    Spacer(Modifier.height(12.dp))
    FieldText("Nhập thêm kĩ năng", "C++", tempSkills) { tempSkills = it }
    AddSkillButton(
        tempSkills,
        onAddSkill = {
            viewModel.skillsInput = viewModel.skillsInput?.plus(Skill(name = it))
        }
    )
    HorizontalDivider(thickness = 2.dp, color = Color.Gray)

}

@Composable
fun AddSkillButton(tempSkills: String, onAddSkill: (String) -> Unit){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ){
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "Add Skill",
            modifier = Modifier.clickable{
                onAddSkill(tempSkills)
            }
        )
    }
}

@Composable
fun ButtonSaveModify(onSave: () -> Unit) {
    Button(onClick = onSave, modifier = Modifier.padding(16.dp)) {
        Text("Lưu thay đổi")
    }
}


@Composable
fun ChangeAvatar(
    userAvatar: String?,
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
            println("Avatar: "+userAvatar)
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
                    model = userAvatar,
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfile(
    sharedPreferences: SharedPreferences,
    navHostController: NavHostController,
    viewModel: EditProfileViewModel = viewModel()
) {
    val context = LocalContext.current
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())

    var userViewModel: UserViewModel = viewModel(factory = viewModelFactory {
        initializer { UserViewModel(sharedPreferences) }
    })

    val userInfo by userViewModel.user.collectAsState()
    val uiState by userViewModel.uiState.collectAsState()

    LaunchedEffect(uiState) {
        println("UI State duoc thay doi")
        if (uiState is UiState.Success) {
            println("uiState là success")
            navHostController.popBackStack()
        }
    }

    LaunchedEffect(Unit) {
        userViewModel.getUser()
    }

    // Cập nhật dữ liệu khi userInfo thay đổi
    LaunchedEffect(userInfo) {
        userInfo?.let { user ->
            viewModel.setOriginalData(user)
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = { Text("Edit Profile", fontWeight = FontWeight.Bold, textAlign = TextAlign.Center) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                navigationIcon = {
                    Icon(
                        painter = painterResource(R.drawable.arrow_back),
                        contentDescription = "Back",
                        tint = Color.White,
                        modifier = Modifier
                            .size(50.dp)
                            .clickable { navHostController.popBackStack() }
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
                EditProfileBody(userInfo, viewModel)
            }
            item {
                ButtonSaveModify {
                    viewModel.saveChanges(userViewModel = userViewModel, context = context)
                }
            }
        }
    }
}