package com.example.itforum.user.post

import android.content.SharedPreferences
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.OptIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CommentsDisabled
import androidx.compose.material.icons.filled.DriveFileMoveRtl
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.LinkOff
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.PopupProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil3.compose.rememberAsyncImagePainter
import com.example.itforum.user.FilterWords.ToastHelper
import com.example.itforum.user.FilterWords.WordFilter

import com.example.itforum.user.effect.UiStateMessage

import com.example.itforum.user.effect.model.UiState
import com.example.itforum.user.home.tag.ViewModel.TagViewModel
import com.example.itforum.user.modelData.request.CreatePostRequest
import com.example.itforum.user.post.viewmodel.PostViewModel
import com.example.itforum.user.skeleton.SkeletonPost
import com.example.itforum.user.userProfile.AddButton
import com.example.itforum.user.userProfile.FieldTagText
import com.example.itforum.user.userProfile.viewmodel.UserViewModel

data class icontext(
    val icon: ImageVector = Icons.Default.Visibility,
    val text: String = "",
    val onClick: (String) -> Unit = {}
)

@Composable
fun CreatePostPage(
    modifier: Modifier,
    navHostController: NavHostController,
    sharedPreferences: SharedPreferences,
    postViewModel: PostViewModel,
    tagViewModel: TagViewModel
) {
    val context = LocalContext.current
    var userViewModel: UserViewModel = viewModel(factory = viewModelFactory {
        initializer { UserViewModel(sharedPreferences) }
    })

    val userId = sharedPreferences.getString("userId", null)
    val userInfo by userViewModel.user.collectAsState()
    val progress by postViewModel.uploadProgress.collectAsState()

    LaunchedEffect(Unit) {
        userViewModel.getUser()
    }

    var imageUrls = remember  { mutableStateOf<List<Uri>?>(emptyList()) }
    var videoUrls = remember  { mutableStateOf<List<Uri>?>(emptyList()) }
    var applicationUrls = remember  { mutableStateOf<List<Uri>?>(emptyList()) }
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var tags = remember { mutableStateOf<List<String>?>(emptyList()) }
    var isPublished by remember { mutableStateOf("public") }
    val focusManager = LocalFocusManager.current

    var isErrorTitle by remember { mutableStateOf(false) }
    LaunchedEffect(title, content) {
        isErrorTitle = false
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        LazyColumn (
            modifier = Modifier.fillMaxSize()
        ) {
            stickyHeader {
                TopPost("Bài viết mới", "Đăng", navHostController) {
                    val combinedText = "$title $content"
                    if (userId!=null) {
                        val hasBadWords = WordFilter.containsBannedWordsAndLog(userId, combinedText)

                        if (hasBadWords) {
                            ToastHelper.show("Nội dung vi phạm chính sách ngôn từ")
                            return@TopPost
                        }
                    }
                    if(title.trim().isEmpty() || content.trim().isEmpty()){
                        isErrorTitle = true
                    }
                    else {
                        postViewModel.createPost(
                            CreatePostRequest(
                                imageUrls = imageUrls.value,
                                videoUrls = videoUrls.value,
                                userId = userInfo?.id ?: "",
                                title = title,
                                content = content,
                                tags = tags.value,
                                isPublished = isPublished
                            ),
                            context
                        )
                        navHostController.navigate("home")
                    }
                }
            }
            item {
                if (progress in 0f..1f && progress != 0f) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Đang đăng bài: ${(progress * 100).toInt()}%",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        SkeletonPost() // mô phỏng một bài viết đang được tải lên
                    }
                }
            }

            item {  3
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(Unit) {
                            detectTapGestures(onTap = {
                                focusManager.clearFocus()
                            })
                        }
                        .background(MaterialTheme.colorScheme.secondaryContainer),
                ) {
                    userInfo?.let { IconWithText(avatar = it.avatar, name = it.name) }
                    WriteTitleField(
                        value = title,
                        onChange = { title = it },
                        isError = isErrorTitle
                    )

                    WriteContentField(
                        value = content,
                        onChange = { content = it }
                    )

                    AddTagPost(
                        tagViewModel = tagViewModel,
                        tags = tags
                    )
                    AddMedia(
                        imageUris = imageUrls,
                        videoUris = videoUrls,
                        applicationUris = applicationUrls
                    )
                    CustomPost()
                }
            }
        }
    }
}

@Composable
fun TopPost(
    title: String,
    nameButton: String,
    navHostController: NavHostController,
    enable: Boolean = true,
    uiState: UiState = UiState.Idle,
    onClickPush: () -> Unit = {},
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primaryContainer)
    ) {
        Spacer(modifier = Modifier.height(30.dp))

        Row(
            modifier = Modifier
                .padding(horizontal = 13.dp, vertical = 10.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            IconButton(onClick = { navHostController.popBackStack() }) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Quay lại",
                    tint = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.size(40.dp)
                )
            }

            Text(
                text = title,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Button(
                onClick = {
                    onClickPush()
                },
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color.White,
                    contentColor = MaterialTheme.colorScheme.primaryContainer
                ),
                enabled = enable
            ) {
                Text(
                    text = nameButton,
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(3.dp),
                )
            }
            UiStateMessage(uiState = uiState, canSubmit = true, false)
        }
    }
}

@Composable
fun IconWithText(
    avatar: String? = null,
    name: String,
    fallbackIcon: ImageVector? = null,
    sizeIcon: Dp = 45.dp,
    textStyle: TextStyle = TextStyle(fontSize = 20.sp),
    modifier: Modifier = Modifier
        .padding(vertical = 6.dp)
        .fillMaxWidth(),
) {
    Row(
        modifier = modifier
            .padding(horizontal = 10.dp)
            .clip(RoundedCornerShape(10.dp)),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        if (!avatar.isNullOrEmpty()) {
            AsyncImage(
                model = avatar,
                contentDescription = "Avatar tài khoản",
                modifier = Modifier.size(sizeIcon).clip(CircleShape),
            )
        } else {
            fallbackIcon?.let {
                Icon(
                    imageVector = it,
                    contentDescription = "Default icon",
                    modifier = Modifier.size(sizeIcon),
                    tint = Color.Gray // tuỳ chọn
                )
            }
        }
        Spacer(modifier = Modifier.width(5.dp))
        Text(
            text = name,
            style = textStyle,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun WritePost(
    isError: Boolean = false,
    onChange: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.secondaryContainer)
            .TopBorder()
            .BottomBorder(),
    ) {
        var textPost by remember { mutableStateOf("") }
        TextField(
            value = textPost,
            onValueChange = { newText ->
                if (newText.length <= 1000) {
                    textPost = newText
                    onChange(newText)
                }
            },
            placeholder = { Text(
                "Nhập mô tả...",
                fontSize = 15.sp,
                fontWeight = FontWeight.Light,
                fontStyle = FontStyle.Italic,
                color = MaterialTheme.colorScheme.onBackground
            ) },
            textStyle = TextStyle(fontSize = 20.sp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
                .clip(RoundedCornerShape(10.dp))
                .heightIn(min = 230.dp, max = 230.dp),
            singleLine = false,
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = MaterialTheme.colorScheme.background,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                textColor = MaterialTheme.colorScheme.onBackground
            ),
        )
        Text(
            "${textPost.length}/1000 kí tự",
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .align(Alignment.End)
        )
        if (isError) {
            Text(
                text = "Không được để trống",
                color = Color.Red,
            )
        }
    }
}

@Composable
fun AddTagPost(
    tagViewModel: TagViewModel,
    tags: MutableState<List<String>?>,
) {

    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.secondaryContainer)
            .padding(10.dp)
            .clip(RoundedCornerShape(10.dp))
    ) {
        var textTag by remember { mutableStateOf("") }
        var isError by remember { mutableStateOf(false) }
        var isFocused by remember { mutableStateOf(false) }


        var expandedFilterField by remember { mutableStateOf(false) }
        var hasFocus by remember { mutableStateOf(false) }
        val listTag by tagViewModel.tagList.collectAsState()

        LaunchedEffect(listTag) {
            tagViewModel.getAllTags()
        }

        val filterOptions = listTag.filter { it.name.contains(textTag, ignoreCase = true) }
        Row(
            modifier = Modifier
                .height(130.dp)
                .background(MaterialTheme.colorScheme.background),
            verticalAlignment = Alignment.CenterVertically
        ){
            FieldTagText(
                placeHolder = "Nhập tag",
                text = textTag,
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
                        filterOptions.forEach { tag ->
                            DropdownMenuItem(
                                text = { tag.name.let { Text(it, color = MaterialTheme.colorScheme.onBackground) } },
                                onClick = {
                                    textTag = tag.name
                                    expandedFilterField = false
                                }
                            )
                        }
                    } else{
                        DropdownMenuItem(
                            text = { androidx.compose.material3.Text("Không có dữ liệu") },
                            onClick = {}
                        )
                    }
                },
                onTextChange = { newText ->
                    textTag = newText
                    expandedFilterField = true // Mỗi lần gõ thì mở dropdown nếu đang focus
                }
            )
            Spacer(modifier = Modifier.weight(1f))
            AddButton(
                textTag,
                onAdd = {
                    if (it.isNotBlank()) {
                        if(!tags.value?.contains(it)!!) {
                            if( !listTag.any {tag -> tag.name.equals(it, ignoreCase = true) }) tagViewModel.createTag(it)
                            tags.value = tags.value?.plus(it)
                            textTag = ""
                        }
                    } else{
                        isError = true
                    }
                    println("Viewmodel: " + tags.value)
                }
            )
//            Box{
//            OutlinedTextField(
//                value = textTag,
//                onValueChange = {
//                    textTag = it
//                    isError = it.trim().isEmpty()
//                },
//                placeholder = { Text("Nhập tag", color = MaterialTheme.colorScheme.onBackground, fontSize = 16.sp) },
//                shape = RoundedCornerShape(7.dp),
//                colors = OutlinedTextFieldDefaults.colors(
//                    unfocusedBorderColor = MaterialTheme.colorScheme.onSecondaryContainer
//                ),
//                textStyle = TextStyle(color = MaterialTheme.colorScheme.onBackground),
//                modifier = Modifier
//                    .weight(3f)
//                    .padding(horizontal = 10.dp)
//                    .onFocusChanged { focusState ->
//                        isFocused = focusState.isFocused
//                        // Khi mất focus, reset lỗi
//                        if (!focusState.isFocused) {
//                            isError = false
//                        }
//                    }
//            )
//            DropdownMenu(
//                expanded = expanded,
//                onDismissRequest = onDismiss,
//                properties = PopupProperties(focusable = !hasFocus),
//                modifier = Modifier.heightIn(max = 230.dp)
//            ) {onFilterChange()}
//            IconButton(
//                onClick = {
//                    if(textTag.trim().isNotEmpty()) {
//                        tags.value = tags.value?.plus(textTag)
//                        textTag = ""
//                    }
//                    else
//                        isError = true
//                },
//                modifier = Modifier
//                    .padding(horizontal = 10.dp)
//                    .size(54.dp)
//                    .weight(1f)
//                    .border(
//                        width = 1.dp,
//                        color = MaterialTheme.colorScheme.onSecondaryContainer,
//                        shape = RoundedCornerShape(7.dp)
//                    )
//            ) {
//                Icon(
//                    imageVector = Icons.Default.Add,
//                    contentDescription = "Nút add tag",
//                    modifier = Modifier
//                        .size(40.dp),
//                    tint = MaterialTheme.colorScheme.onSecondaryContainer
//                )
//            }
        }
        if (isError) {
            Text(
                text = "Tag không được nhập trống",
                color = Color.Red,
            )
        }
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            maxItemsInEachRow = 3,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ){
            tags.value?.forEach { item ->
                TagChild(
                    item, tags.value!!
                )
                {newList->
                    tags.value = newList
                }
            }
        }
    }
}

@Composable
fun TagChild(
    item: String,
    items: List<String?>,
    icon: ImageVector? = null,
    removeTag: (List<String>) -> Unit
) {
    Row(
        modifier = Modifier
            .padding(vertical = 8.dp, horizontal = 5.dp)
            .background(
                Color(0xFF00FBFF),
                RoundedCornerShape(8.dp)
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if(icon != null){
            Icon(
                imageVector = Icons.Default.AttachFile,
                contentDescription = "Xóa tag",
                modifier = Modifier
                    .padding(start = 2.dp)
                    .size(17.dp)
            )
        }
        Text(
            text = item,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(if (icon == null) (9.dp) else 5.dp)
                .widthIn(max = 100.dp)
        )
        Icon(
            imageVector = Icons.Default.Close,
            contentDescription = "Xóa tag",
            modifier = Modifier
                .padding(end = 5.dp)
                .size(17.dp)
                .clickable {
                    if (items.isNotEmpty()) {
                        val newList = items - item
                        removeTag(newList as List<String>)
                    }
                }
        )
    }
}

@Composable
fun AddMedia(
    imageUris: MutableState<List<Uri>?>,
    videoUris: MutableState<List<Uri>?>,
    applicationUris: MutableState<List<Uri>?>,
) {
    val context = LocalContext.current
    val MAX_TOTAL_SIZE = 100 * 1024 * 1024L // 100MB
    var mediaUris by remember { mutableStateOf<List<Uri>>(emptyList()) }
    var mediaTypes by remember { mutableStateOf<List<String>>(emptyList()) }
//    var totalUsedMB = remember(mediaUris) {
//        mediaUris.sumOf {
//            context.contentResolver.openAssetFileDescriptor(it, "r")?.length ?: 0L
//        } / (1024 * 1024.0)
//    }
    LaunchedEffect(imageUris, videoUris, applicationUris, Unit) {
        mediaUris = (imageUris.value ?: emptyList()) +
                (videoUris.value ?: emptyList()) +
                (applicationUris.value ?: emptyList())
    }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenMultipleDocuments()
    ) { uris: List<Uri> ->
        uris.forEach{uri ->
//            val size = context.contentResolver.openAssetFileDescriptor(uri, "r")?.length ?: 0L
//            totalUsedMB += size/ (1024 * 1024.0)
//            if (totalUsedMB > (MAX_TOTAL_SIZE/ (1024 * 1024.0))) {
//                Toast.makeText(context, "Vượt quá dung lượng cho phép (100MB)", Toast.LENGTH_SHORT).show()
//                return@forEach
//            }

            val type =context.contentResolver.getType(uri) ?:""
            when{
                type.startsWith("image") -> {
                    imageUris.value = imageUris.value?.plus(uri)
                }
                type.startsWith("video") -> {
                    videoUris.value = videoUris.value?.plus(uri)
                }
                type.startsWith("application") -> {
                    applicationUris.value = applicationUris.value?.plus(uri)
                }
            }
        }

        mediaUris = (imageUris.value ?: emptyList()) +
                (videoUris.value ?: emptyList()) +
                (applicationUris.value ?: emptyList())  // Nối list cũ với list mới
        mediaTypes = mediaUris.map { uri ->
            context.contentResolver.getType(uri) ?: "unknown"
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

//        Text(
//            text = "Đã sử dụng: %.2f/100 MB".format(totalUsedMB),
//            color = MaterialTheme.colorScheme.onBackground,
//            fontSize = 16.sp,
//            fontWeight = FontWeight.Bold,
//            modifier = Modifier
//                .padding(end = 10.dp)
//                .align(Alignment.End)
//        )
        if(imageUris.value!!.isEmpty() && videoUris.value!!.isEmpty() && applicationUris.value!!.isEmpty()){
            IconButton(
                onClick = {
                    launcher.launch(arrayOf("*/*"))  //Chọn tệp bất kì
                }
            ){
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.DriveFileMoveRtl,
                        contentDescription = "Thêm tệp",
                        tint = MaterialTheme.colorScheme.secondaryContainer,
                        modifier = Modifier.size(130.dp)
                    )
                    Text(
                        "Chưa có tệp, ảnh hoặc video",
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.width(130.dp)
                    )
                }
            }
        }else {
            Box(modifier = Modifier.fillMaxWidth()) {
                if (imageUris.value!!.isNotEmpty()){
                    imageUris.value!!.forEachIndexed(){index, uri ->
                        ImgOrVdMedia("image",index, uri, imageUris.value!!, removeUri = {newList->
                            imageUris.value = newList
                        })
                    }
                }
            }
            Box(modifier = Modifier.fillMaxWidth()) {
                if (videoUris.value!!.isNotEmpty()) {
                    videoUris.value!!.forEachIndexed() { index, uri ->
                        ImgOrVdMedia("video", index, uri, videoUris.value!!, removeUri = {newList->
                            videoUris.value = newList
                        })
                    }
                }
            }
            if (applicationUris.value!!.isNotEmpty()){
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    maxItemsInEachRow = 3,
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                ){
                    applicationUris.value!!.forEach{ uri ->
                        val name = context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                            if (cursor.moveToFirst()) {
                                val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                                if (nameIndex != -1) cursor.getString(nameIndex) else "kocoten"
                            } else "kocoten"
                        }
                        if (name != null) {
                            TagFile(name, applicationUris.value!!, Icons.Default.AttachFile, uri, removeTag = {newList ->
                                applicationUris.value = newList as List<Uri>
                            })
                        }
                    }
                }

            }
        }
        Row(
            modifier = Modifier.padding(10.dp),
            horizontalArrangement = Arrangement.spacedBy(5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Image,
                contentDescription = "Thêm ảnh",
                modifier = Modifier
                    .size(30.dp)
                    .clickable {
                        launcher.launch(arrayOf("image/*", "video/*"))  //Chọn ảnh, video
                    },
                tint = MaterialTheme.colorScheme.onSecondaryContainer
            )
            Icon(
                imageVector = Icons.Default.UploadFile,
                contentDescription = "Thêm file",
                modifier = Modifier
                    .size(30.dp)
                    .clickable {
                        launcher.launch(arrayOf("application/*"))  //Chọn ảnh file
                    },
                tint = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
    }
}

@Composable
fun ImgOrVdMedia(type: String, index: Int = 0, uri: Uri, listUri: List<Uri>, removeUri: (List<Uri>) -> Unit) {
    val pxValue = with(LocalDensity.current) { (70.dp * (listUri.size-1-index)).roundToPx() }
    Box(
        modifier = Modifier
            .offset { IntOffset(pxValue, 0) }
            .border(2.dp, MaterialTheme.colorScheme.background)
            .background(MaterialTheme.colorScheme.background)
    ) {
        if (type == "video") {
            VideoPlayer(uri)
        }else {
            if(uri.scheme == "http" || uri.scheme == "https"){
                AsyncImage(
                    model = uri,
                    contentDescription = "",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .padding(3.dp)
                        .size(150.dp)
                )
            } else{
                Image(
                    painter = rememberAsyncImagePainter(uri),
                    contentDescription = "",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .padding(3.dp)
                        .size(150.dp)
                )
            }

        }
        Icon(
            imageVector = Icons.Default.Close,
            contentDescription = "",
            modifier = Modifier
                .padding(5.dp)
                .align(Alignment.TopEnd)
                .clickable {
                    var newList = listUri - uri
                    removeUri(newList)
                }
        )
        if (type == "video"){
            Icon(
                imageVector = Icons.Default.PlayCircle,
                contentDescription = "",
                modifier = Modifier
                    .padding(5.dp)
                    .align(Alignment.BottomEnd)
            )
        }
    }
}


@Composable
fun CustomPost() {
    Spacer(modifier = Modifier.height(15.dp))
    Column(
        modifier = Modifier
            .padding(vertical = 10.dp, horizontal = 10.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.background)
            .padding(10.dp)
            .fillMaxWidth()
    ) {
        Text(
            "Tùy chỉnh",
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        RadioOption(Icons.Default.CommentsDisabled, "Tắt bình luận")
        RadioOption(Icons.Default.LinkOff, "Tắt chia sẻ")
    }
}

@Composable
fun RadioOption(img: ImageVector, text: String) {
    var isSelected by remember { mutableStateOf(false) }
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = isSelected,
            onClick = {
                isSelected = !isSelected
            },
            colors = RadioButtonDefaults.colors(
                selectedColor = Color(0xFF00AEFF)
            )
        )
        Icon(
            imageVector = img,
            contentDescription = "Icon "+text,
            tint = MaterialTheme.colorScheme.onBackground,
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = text,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Composable
fun Modifier.TopBorder(
    color: Color = MaterialTheme.colorScheme.secondaryContainer,
    thickness: Dp = 1.dp
): Modifier = this.then(
    Modifier.drawBehind {
        val strokeWidthPx = thickness.toPx()

        // Vẽ đường trên
        drawLine(
            color = color,
            start = Offset(0f, 0f),
            end = Offset(size.width, 0f),
            strokeWidth = strokeWidthPx
        )
    }
)

@Composable
fun Modifier.BottomBorder(
    color: Color = MaterialTheme.colorScheme.secondaryContainer,
    thickness: Dp = 1.dp
): Modifier = this.then(
    Modifier.drawBehind {
        val strokeWidthPx = thickness.toPx()
        // Vẽ đường dưới
        drawLine(
            color = color,
            start = Offset(0f, size.height),
            end = Offset(size.width, size.height),
            strokeWidth = strokeWidthPx
        )
    }
)

@Composable
fun TagFile(
    item: Any,
    items: List<Any>,
    icon: ImageVector? = null,
    uri: Uri? = null,
    removeTag: (List<Any>) -> Unit
) {
    Row(
        modifier = Modifier
            .padding(vertical = 8.dp, horizontal = 5.dp)
            .background(
                Color(0xFF00FBFF),
                RoundedCornerShape(8.dp)
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if(icon != null){
            Icon(
                imageVector = Icons.Default.AttachFile,
                contentDescription = "Xóa tag",
                modifier = Modifier
                    .padding(start = 2.dp)
                    .size(17.dp)
            )
        }
        Text(
            text = item.toString(),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(if (icon == null) (9.dp) else 5.dp)
                .widthIn(max = 100.dp)
        )
        Icon(
            imageVector = Icons.Default.Close,
            contentDescription = "Xóa tag",
            modifier = Modifier
                .padding(end = 5.dp)
                .size(17.dp)
                .clickable {
                    if (items.isNotEmpty() && items.first() is String) {
                        val newList = items - item
                        removeTag(newList)
                    } else if (uri != null) {
                        val newList = items - uri
                        removeTag(newList)
                    }
                }
        )
    }
}

//VideoPlayer(uri = Uri.parse("https://www.example.com/video.mp4"))
@OptIn(UnstableApi::class)
@Composable
fun VideoPlayer(
    uri: Uri,
    modifier: Modifier = Modifier.padding(3.dp)
        .size(150.dp)
        .aspectRatio(16 / 9f)
        .background(Color.Black)
) {
    val context = LocalContext.current

    val exoPlayer = remember(uri) {
        ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(uri))
            prepare()
            playWhenReady = false // KHÔNG tự động phát
        }
    }

    DisposableEffect(exoPlayer) {
        onDispose {
            exoPlayer.release()
        }
    }
    Box(
        modifier = modifier
    ) {
        AndroidView(
            factory = {
                PlayerView(context).apply {
                    player = exoPlayer
                    useController = true
                    controllerShowTimeoutMs = 2000 // tự ẩn sau 2s
                    showController() // hiện lần đầu (tuỳ chọn)
                    controllerAutoShow = false //  KHÔNG tự hiện controller khi chưa phát
                }
            },
        )
    }
}

@Composable
fun WriteTitleField(
    value: String,
    onChange: (String) -> Unit,
    isError: Boolean = false
) {
    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.secondaryContainer)
            .TopBorder()
            .BottomBorder()
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = {
                if (it.length <= 100) {
                    onChange(it)
                }
            },
            placeholder = { Text("Nhập tiêu đề bài viết...", fontSize = 18.sp) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            isError = isError,
            maxLines = 2,
            textStyle = TextStyle(fontSize = 20.sp)
        )
        if (isError) {
            Text("Tiêu đề không được để trống", color = Color.Red, modifier = Modifier.padding(start = 15.dp))
        }
    }
}

@Composable
fun WriteContentField(
    value: String,
    onChange: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.secondaryContainer)
            .TopBorder()
            .BottomBorder()
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = {
                if (it.length <= 1000) {
                    onChange(it)
                }
            },
            placeholder = { Text("Nhập nội dung bài viết...", fontSize = 16.sp,
                fontWeight = FontWeight.Normal)
                          },
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
                .heightIn(min = 200.dp),
            maxLines = 10,
            textStyle = TextStyle(fontSize = 18.sp)
        )
        Text(
            "${value.length}/1000 ký tự",
            modifier = Modifier
                .padding(end = 16.dp)
                .align(Alignment.End),
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

