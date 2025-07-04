package com.example.itforum.user.userProfile

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable


import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
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
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.PopupProperties
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.itforum.R

import com.example.itforum.user.effect.model.UiState
import com.example.itforum.user.modelData.request.UserUpdateRequest
import com.example.itforum.user.modelData.response.Certificate
import com.example.itforum.user.modelData.response.Skill
import com.example.itforum.user.modelData.response.UserProfileResponse
import com.example.itforum.user.userProfile.viewmodel.SkillViewModel
import com.example.itforum.user.userProfile.viewmodel.UserViewModel

var paddingHorizontal = 26.dp

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
    var skillsInput by mutableStateOf<List<String>?>(null)
    var certificateInput by mutableStateOf<List<Certificate>?>(null)

    // Lưu trữ dữ liệu gốc để so sánh
    private var originalAvatarURL: Uri? = null
    private var originalName: String = ""
    private var originalUsername: String = ""
    private var originalEmail: String = ""
    private var originalPhone: String = ""
    private var originalIntroduce: String = ""



    var originalSkills: List<String>? = null
    var originalCertificate: List<Certificate>? = null

    fun isOriginalDataLoaded(): Boolean {
        return originalName.isNotBlank()
    }


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
        if (passInput.isNotBlank() && passInput != repassInput) {
            Toast.makeText(context, "Mật khẩu nhập lại không khớp", Toast.LENGTH_SHORT).show()
            return
        }

        val request = UserUpdateRequest(
            name = if (nameInput != originalName && nameInput.isNotBlank()) nameInput else null,
            username = if (usernameInput != originalUsername && usernameInput.isNotBlank()) usernameInput else null,
            email = if (emailInput != originalEmail && emailInput.isNotBlank()) emailInput else null,
            phone = if (phoneInput != originalPhone && phoneInput.isNotBlank()) phoneInput else null,
            introduce = if (introduce != originalIntroduce && introduce.isNotBlank()) introduce else null,
            avatar = if (avatarURL != originalAvatarURL) avatarURL else null,
            password = passInput.takeIf { it.isNotBlank() },
            certificate = if (certificateInput != originalCertificate) certificateInput else null,
            skill = if (skillsInput != originalSkills) skillsInput else null,
        )

        println("Gửi request cập nhật: $request")
        userViewModel.ModifierUser(request, context)
    }

    fun hasChanges(): Boolean {
        return nameInput != originalName ||
                usernameInput != originalUsername ||
                emailInput != originalEmail ||
                phoneInput != originalPhone ||
                introduce != originalIntroduce ||
                avatarURL != originalAvatarURL ||
                passInput.isNotBlank() || // luôn coi như có thay đổi nếu có nhập pass
                skillsInput != originalSkills ||
                certificateInput != originalCertificate
    }

}
@Composable
fun EditProfileBody(modifier: Modifier, user: UserProfileResponse?, viewModel: EditProfileViewModel) {
    val skillViewModel: SkillViewModel = viewModel()
    LaunchedEffect(Unit) {
        skillViewModel.getAllSkill()
    }
    var tempSkills by remember { mutableStateOf("") }
    var tempCertificate by remember { mutableStateOf("") }

    var expandedFilterField by remember { mutableStateOf(false) }
    var hasFocus by remember { mutableStateOf(false) }

    val listSkill by skillViewModel.listSkill.collectAsState()
    val filterOptions = listSkill.filter { it.name!!.contains(tempSkills, ignoreCase = true) }
    val focusManager = LocalFocusManager.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(onTap = {
                    focusManager.clearFocus()
                })
            }
    ) {
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
        Divider()
        Title("Kĩ năng")
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = paddingHorizontal),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            FieldTagText(
                placeHolder = "C++",
                text = tempSkills,
                shape = RoundedCornerShape(7.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = MaterialTheme.colorScheme.onSecondaryContainer
                ),
                textStyle = TextStyle(color = MaterialTheme.colorScheme.onBackground),
                expanded = expandedFilterField,
                hasFocus = hasFocus,
                onFocusChange = { focus ->
                    hasFocus = focus
                    expandedFilterField = focus
                },
                onDismiss = { expandedFilterField = false },
                onFilterChange = {
                    if(filterOptions.isNotEmpty()) {
                        filterOptions.forEach { skill ->
                            DropdownMenuItem(
                                text = { skill.name?.let { Text(it, color = MaterialTheme.colorScheme.onBackground) } },
                                onClick = {
                                    tempSkills = skill.name!!
                                    expandedFilterField = false
                                }
                            )
                        }
                    } else{
                        DropdownMenuItem(
                            text = { Text("Không có dữ liệu") },
                            onClick = {}
                        )
                    }
                },
                onTextChange = { newText ->
                    tempSkills = newText
                    expandedFilterField = true // Mỗi lần gõ thì mở dropdown nếu đang focus
                }
            )
            AddButton(
                tempSkills,
                onAdd = {
                    if (it.isNotBlank() && !viewModel.skillsInput?.contains(it)!! && listSkill.any {skill -> skill.name.equals(it, ignoreCase = true) }) {
                        viewModel.skillsInput = viewModel.skillsInput?.plus(it)
                        tempSkills = ""
                    }
                    println("Viewmodel: " + viewModel.skillsInput)

                }
            )
        }

        TagSkill(
            title = "Kĩ năng đã thêm:",
            tempSkills= viewModel.skillsInput,
            onDelete = {
                if (it.isNotBlank() && viewModel.skillsInput?.contains(it)!!) {
                    viewModel.skillsInput =
                        viewModel.skillsInput?.minus(it)
                }
            }
        )
//        HorizontalDivider(thickness = 2.dp, color = Color.Gray)
//        Title("Chứng chỉ")
//        Row (
//            modifier = Modifier.fillMaxWidth().padding(horizontal = paddingHorizontal),
//            horizontalArrangement = Arrangement.SpaceBetween,
//            verticalAlignment = Alignment.CenterVertically
//        ){
//            var tempCertificate by remember { mutableStateOf("") }
//            var expandedCertificateField by remember { mutableStateOf(false) }
//            var certificateFieldHasFocus by remember { mutableStateOf(false) }
//
//            FieldTagText(
//                placeHolder = "Đại học...",
//                text = tempCertificate,
//                expanded = expandedCertificateField,
//                hasFocus = certificateFieldHasFocus,
//                onFocusChange = { focus ->
//                    certificateFieldHasFocus = focus
//                    expandedCertificateField = focus
//                },
//                onDismiss = { expandedCertificateField = false },
//                onFilterChange = {
//                    DropdownMenuItem(
//                        text = { Text("Gợi ý: IELTS, TOEIC...") },
//                        onClick = {
//                            tempCertificate = "IELTS"
//                            expandedCertificateField = false
//                        }
//                    )
//                },
//                onTextChange = {
//                    tempCertificate = it
//                    expandedCertificateField = true
//                }
//            )
//
//            AddButton(
//                tempCertificate,
//                onAdd = {
//                    if (it.isNotBlank() && !viewModel.certificateInput?.contains(Certificate(name = it))!!) {
//                        viewModel.certificateInput =
//                            viewModel.certificateInput?.plus(Certificate(name = it))
//                    }
//                    println("Viewmodel: " + viewModel.certificateInput)
//
//                }
//            )
//        }
//        TagCertificate(
//            title = "Chứng chỉ bằng cấp đã thêm:",
//            tempCertificate = viewModel.certificateInput,
//            onDelete = {
//                if (it.isNotBlank() && viewModel.certificateInput?.contains(Certificate(name = it))!!) {
//                    viewModel.certificateInput =
//                        viewModel.certificateInput?.minus(Certificate(name = it))
//                }
//            }
//        )
    }
}

@Composable
fun Title(title: String) {
    Column (
        modifier = Modifier.fillMaxWidth().padding(16.dp)
    ){
        Text(
            text = title,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )
    }
}


@Composable
fun AddButton(tempSkills: String, onAdd: (String) -> Unit){
    IconButton(
        onClick = {onAdd(tempSkills)},
        modifier = Modifier
            .padding(horizontal = 10.dp)
            .size(54.dp)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                shape = RoundedCornerShape(7.dp)
            )
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "Nút add tag",
            modifier = Modifier
                .size(40.dp),
            tint = MaterialTheme.colorScheme.onSecondaryContainer
        )
    }
}




@Composable
fun TagSkill(title: String, tempSkills: List<String>?, onDelete: (String) -> Unit){
    Column (
        modifier = Modifier.padding(16.dp)
    ) {
        Text(
            text = title,
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        FlowRow (
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            tempSkills?.forEach { tag ->
                TagEditableItem(text = tag, onDelete = onDelete)
            }
        }
    }
}


@Composable
fun TagCertificate(title: String, tempCertificate: List<Certificate>?, onDelete: (tagName: String) -> Unit){
    Column (
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {
        Text(
            text = title,
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        FlowRow (
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            tempCertificate?.forEach { tag ->
                TagEditableItem(text = tag.name, onDelete = onDelete)
            }
        }
    }
}

@Composable
fun TagEditableItem(text: String, onDelete: (tagName: String) -> Unit = {}) {
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
        Row (
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ){
            Text(text = text, color = Color.DarkGray)
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Delete Skill",
                tint = Color.DarkGray,
                modifier = Modifier.clickable {
                    onDelete(text)
                }
            )
        }
    }
}



//@Composable
//fun ButtonSaveModify(onSave: () -> Unit) {
//    OutlinedButton (
//        onClick = onSave,
//        modifier = Modifier
//            .padding(16.dp)
//            .fillMaxWidth(),
//        colors = ButtonDefaults.buttonColors(
//            containerColor = MaterialTheme.colorScheme.primaryContainer,        // Màu nền (background)
//            contentColor = Color.White,         // Màu chữ / icon
//            disabledContainerColor = Color.Gray,  // Màu nền khi disabled
//            disabledContentColor = Color.LightGray // Màu chữ khi disabled
//        )
//    ) {
//        Text("Lưu thay đổi")
//    }
//}
@Composable
fun ButtonSaveModify(
    hasChanges: Boolean,
    onSave: () -> Unit
) {
    OutlinedButton(
        onClick = onSave,
        enabled = hasChanges,
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (hasChanges) MaterialTheme.colorScheme.primaryContainer else Color.Gray,
            contentColor = Color.White,
            disabledContainerColor = Color.LightGray,
            disabledContentColor = Color.DarkGray
        )
    ) {
        Text(if (hasChanges) "Lưu thay đổi" else "Không có thay đổi gì")
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
    Column(modifier = Modifier.padding(paddingHorizontal)) {
        Text(
            text = title,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        TextField(
            value = text,
            placeholder = { Text(text = placeHolder) },
            onValueChange = onTextChange,
            modifier = Modifier.fillMaxWidth()
        )
    }
}


@Composable
fun FieldTagText(
    placeHolder: String,
    text: String,
    shape: Shape = TextFieldDefaults.shape,
    colors: TextFieldColors = TextFieldDefaults.colors(),
    textStyle: TextStyle = LocalTextStyle.current,
    expanded: Boolean,
    hasFocus: Boolean,
    onFilterChange: @Composable () -> Unit,
    onFocusChange: (Boolean) -> Unit,
    onDismiss: () -> Unit,
    onTextChange: (String) -> Unit
) {
    Box {
        OutlinedTextField(
            value = text,
            placeholder = { Text(placeHolder, color = MaterialTheme.colorScheme.onBackground, fontSize = 16.sp) },
            onValueChange = onTextChange,
            shape = shape,
            colors = colors,
            textStyle = textStyle,
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .onFocusChanged { focusState ->
                    onFocusChange(focusState.isFocused)
                }
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = onDismiss,
            properties = PopupProperties(focusable = !hasFocus),
            modifier = Modifier.heightIn(max = 230.dp)
        ) {onFilterChange()}
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfile(
    modifier: Modifier,
    sharedPreferences: SharedPreferences,
    navHostController: NavHostController,
) {
    val viewModel: EditProfileViewModel by remember { mutableStateOf(EditProfileViewModel()) }
    val context = LocalContext.current

    val userViewModel: UserViewModel = viewModel(factory = viewModelFactory {
        initializer { UserViewModel(sharedPreferences) }
    })

    val userInfo by userViewModel.user.collectAsState()
    val uiState by userViewModel.uiState.collectAsState()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())



//    LaunchedEffect(uiState) {
//        println("UI State duoc thay doi")
//        when (uiState) {
//            is UiState.Success -> {
//                println("uiState là success")
//                navHostController.popBackStack()
//            }
//            is UiState.Error -> {
//                println("uiState là error")
//            }
//            is UiState.Loading -> {
//                // Đang tải
//            }
//            else -> {}
//        }
//    }
    LaunchedEffect(uiState) {
        println("UI State duoc thay doi: ${uiState::class.simpleName}")
        when (uiState) {
            is UiState.Success -> {
                println("uiState là success")
                navHostController.popBackStack()
            }
            is UiState.Error -> {
                println("uiState là error: ${(uiState as UiState.Error).message}")
            }
            is UiState.Loading -> {
                println("uiState là loading")
            }
            else -> {
                println("uiState là trạng thái khác không xác định: $uiState")
            }
        }
    }


    LaunchedEffect(Unit) {
        userViewModel.getUser()
    }

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
                title = {
                    Text(
                        "Chỉnh sửa trang cá nhân",
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                navigationIcon = {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White,
                        modifier = Modifier
                            .size(35.dp)
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
                EditProfileBody(modifier, userInfo, viewModel)
            }

            item {
                ButtonSaveModify(
                    hasChanges = viewModel.hasChanges(),
                    onSave = {
                        viewModel.saveChanges(userViewModel = userViewModel, context = context)
                    }
                )

            }
        }
    }
}
