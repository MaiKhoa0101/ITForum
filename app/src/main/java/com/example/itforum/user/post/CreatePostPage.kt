package com.example.itforum.user.post

import android.content.SharedPreferences
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil3.compose.rememberAsyncImagePainter
import com.example.itforum.user.effect.model.UiState
import com.example.itforum.user.modelData.request.CreatePostRequest
import com.example.itforum.user.post.viewmodel.PostViewModel
import com.example.itforum.user.profile.viewmodel.UserViewModel

data class icontext(
    val icon: ImageVector = Icons.Default.Visibility,
    val text: String = "",
)

@Composable
fun CreatePostPage(
    modifier: Modifier,
    navHostController: NavHostController,
    sharedPreferences: SharedPreferences
) {
    val context = LocalContext.current
    var userViewModel: UserViewModel = viewModel(factory = viewModelFactory {
        initializer { UserViewModel(sharedPreferences) }
    })

    var postViewModel: PostViewModel = viewModel(factory = viewModelFactory {
        initializer { PostViewModel(navHostController,sharedPreferences) }
    })

    val userInfo by userViewModel.user.collectAsState()
    val uiState by postViewModel.uiStateCreate.collectAsState()

    LaunchedEffect(uiState) {
        println("UI State duoc thay doi")
        if (uiState is UiState.Success) {
            println("uiState là success")
            navHostController.navigate("home")
        }
    }
    LaunchedEffect(Unit) {
        userViewModel.getUser()
    }

    var imageUrls by remember  { mutableStateOf<List<Uri>?>(emptyList()) }
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var tags by remember { mutableStateOf(listOf<String?>(null)) }
    var isPublished by remember { mutableStateOf("public") }
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        LazyColumn (
            modifier = Modifier.fillMaxSize()
        ) {

            stickyHeader {
                TopPost(navHostController) {
                    postViewModel.createPost(
                        CreatePostRequest(
                            imageUrls = imageUrls,
                            userId = userInfo?.id ?: "",
                            title = title,
                            content = content,
                            tags = tags,
                            isPublished = isPublished
                        ),
                        context
                    )
                }
            }
            item {

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.secondary),
                ) {
                    userInfo?.let { IconWithText(it.avatar, it.name) }
                    WritePost(){input ->
                        title = input
                        content = input
                    }
                    AddTagPost(){tags = it}
                    AddMedia(){imageUrls = it}
                    CustomPost()
                }
            }
        }
    }
}

@Composable
fun TopPost(
    navHostController: NavHostController,
    onClickPush: () -> Unit = {}
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
                    imageVector = Icons.Default.KeyboardArrowLeft,
                    contentDescription = "Quay lại",
                    tint = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.size(40.dp)
                )
            }

            Text(
                "Bài viết mới",
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
                )
            ) {
                Text(
                    "Đăng",
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(3.dp),
                )
            }
        }
    }
}

@Composable
fun IconWithText(
    avatar: String,
    name: String,
    sizeIcon: Dp = 45.dp,
    textStyle: TextStyle = TextStyle(fontSize = 20.sp),
    modifier: Modifier = Modifier
        .padding(vertical = 6.dp)
        .fillMaxWidth(),
) {
    Row(
        modifier = modifier
            .padding(horizontal = 10.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.background),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = avatar,
            contentDescription = "Avatar tài khoản",
            modifier = Modifier.size(sizeIcon)
        )
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
fun WritePost(onChange: (String) -> Unit) {
    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.secondary)
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
            )
        )
        Text(
            "${textPost.length}/10000 kí tự",
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .align(Alignment.End)
        )
    }
}


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AddTagPost(
    onChange: (List<String?>) -> Unit
) {
    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.secondary)
            .padding(10.dp)
            .clip(RoundedCornerShape(10.dp))
    ) {
        var items by remember { mutableStateOf(listOf<String?>(null)) }
        var textTag by remember { mutableStateOf("") }
        var isError by remember { mutableStateOf(false) }
        var isFocused by remember { mutableStateOf(false) }
        Row(
            modifier = Modifier
                .height(130.dp)
                .background(MaterialTheme.colorScheme.background),
            verticalAlignment = Alignment.CenterVertically
        ){
            OutlinedTextField(
                value = textTag,
                onValueChange = {
                    textTag = it
                    isError = it.trim().isEmpty()
                },
                placeholder = { Text("Nhập tag", color = MaterialTheme.colorScheme.onBackground, fontSize = 16.sp) },
                shape = RoundedCornerShape(7.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = MaterialTheme.colorScheme.onSecondary
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
            IconButton(
                onClick = {
                    if(textTag.trim().isNotEmpty()) {
                        items = items + textTag
                        onChange(items)
                    }
                    else
                        isError = true
                },
                modifier = Modifier
                    .padding(horizontal = 10.dp)
                    .size(54.dp)
                    .weight(1f)
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.onSecondary,
                        shape = RoundedCornerShape(7.dp)
                    )
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Nút add tag",
                    modifier = Modifier
                        .size(40.dp),
                    tint = MaterialTheme.colorScheme.onSecondary
                )
            }
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
            items.forEach { item ->
                if (item != null) {
                    TagChild(
                        item,items
                    )
                    {newList->
                        items = newList
                        onChange(items)
                    }
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
    onChange: (List<Uri>) -> Unit
) {
    val context = LocalContext.current
    var imageUris by remember { mutableStateOf<List<Uri>>(emptyList()) }
    var videoUri by remember { mutableStateOf<List<Uri>>(emptyList()) }
    var applicationUri by remember { mutableStateOf<List<Uri>>(emptyList()) }
    var mediaUris by remember { mutableStateOf<List<Uri>>(emptyList()) }
    var mediaTypes by remember { mutableStateOf<List<String>>(emptyList()) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenMultipleDocuments()
    ) { uris: List<Uri> ->
        uris.forEach{uri ->
            val type =context.contentResolver.getType(uri) ?:""
            when{
                type.startsWith("image") -> {
                    imageUris = imageUris + uri
                    onChange(imageUris)
                }
                type.startsWith("video") -> videoUri = videoUri + uri
                type.startsWith("application") -> applicationUri = applicationUri + uri
            }
        }
        mediaUris = mediaUris + uris  // Nối list cũ với list mới
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
        Text(
            "Tối đa: 3GB",
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(top = 5.dp, end = 12.dp)
                .align(Alignment.End)
        )
        if(imageUris.isEmpty() && videoUri.isEmpty() && applicationUri.isEmpty()){
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
                if (imageUris.isNotEmpty()){
                    imageUris.forEachIndexed(){index, uri ->
                        ImgOrVdMedia("image",index, uri, imageUris, removeUri = {newList-> imageUris = newList})
                    }
                }
            }
            Box(modifier = Modifier.fillMaxWidth()) {
                if (videoUri.isNotEmpty()) {
                    videoUri.forEachIndexed() { index, uri ->
                        ImgOrVdMedia("video", index, uri, videoUri, removeUri = {newList-> videoUri = newList})
                    }
                }
            }
            if (applicationUri.isNotEmpty()){
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    maxItemsInEachRow = 3,
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                ){
                    applicationUri.forEach{ uri ->
                        val name = context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                            if (cursor.moveToFirst()) {
                                val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                                if (nameIndex != -1) cursor.getString(nameIndex) else "kocoten"
                            } else "kocoten"
                        }
                        if (name != null) {
                            TagFile(name, applicationUri, Icons.Default.AttachFile, uri, removeTag = {newList -> applicationUri = newList as List<Uri>})
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
                tint = MaterialTheme.colorScheme.onSecondary
            )
            Icon(
                imageVector = Icons.Default.UploadFile,
                contentDescription = "Thêm file",
                modifier = Modifier
                    .size(30.dp)
                    .clickable {
                        launcher.launch(arrayOf("application/*"))  //Chọn ảnh file
                    },
                tint = MaterialTheme.colorScheme.onSecondary
            )
        }
    }
}

@Composable
fun ImgOrVdMedia(type: String, index: Int = 0, uri: Uri, ListUri: List<Uri>, removeUri: (List<Uri>) -> Unit) {
    val pxValue = with(LocalDensity.current) { (70.dp * (ListUri.size-1-index)).roundToPx() }
    Box(
        modifier = Modifier.offset { IntOffset(pxValue, 0) }
    ) {
        if (type == "video") {
            val context = LocalContext.current
            val bitmap = remember(uri) {
                val retriever = MediaMetadataRetriever()
                try {
                    retriever.setDataSource(context, uri)
                    retriever.frameAtTime
                } catch (e: Exception) {
                    null
                } finally {
                    retriever.release()
                }
            }
            bitmap?.let {
                var painter = remember(it) {
                    BitmapPainter(it.asImageBitmap())
                }
                Image(
                    painter = painter,
                    contentDescription = "",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .padding(3.dp)
                        .size(150.dp)
                )
            }
        }else {
            Image(
                painter = rememberAsyncImagePainter(uri),
                contentDescription = "",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .padding(3.dp)
                    .size(150.dp)
            )
        }
        Icon(
            imageVector = Icons.Default.Close,
            contentDescription = "",
            modifier = Modifier
                .padding(5.dp)
                .align(Alignment.TopEnd)
                .clickable {
                    var newList = ListUri - uri
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
    color: Color = MaterialTheme.colorScheme.secondary,
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
    color: Color = MaterialTheme.colorScheme.secondary,
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