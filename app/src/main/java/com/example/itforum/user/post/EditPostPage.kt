package com.example.itforum.user.post

import android.content.SharedPreferences
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Text
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavHostController
import com.example.itforum.user.FilterWords.ToastHelper
import com.example.itforum.user.FilterWords.WordFilter
import com.example.itforum.user.modelData.request.CreatePostRequest
import com.example.itforum.user.post.viewmodel.PostViewModel
import com.example.itforum.user.userProfile.viewmodel.UserViewModel

@Composable
fun EditPostPage(
    modifier: Modifier,
    navHostController: NavHostController,
    sharedPreferences: SharedPreferences,
    postId: String,
    postViewModel: PostViewModel
) {
    val context = LocalContext.current
    val userViewModel: UserViewModel = viewModel(factory = viewModelFactory {
        initializer { UserViewModel(sharedPreferences) }
    })

    val userId = sharedPreferences.getString("userId", null)
    val userInfo by userViewModel.user.collectAsState()
    val progress by postViewModel.uploadProgress.collectAsState()
    val post by postViewModel.post.collectAsState()

    LaunchedEffect(Unit) {
        userViewModel.getUser()
        postViewModel.fetchPostById(postId)
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
    LaunchedEffect(post) {
        imageUrls.value = post?.imageUrls?.map { Uri.parse(it) } ?: emptyList()
        videoUrls.value = post?.videoUrls?.map { Uri.parse(it) } ?: emptyList()
        tags.value = post?.tags?: emptyList()
        title = post?.title?: ""
        content = post?.content?: ""
        isPublished = post?.isPublished?: "public"
        Log.d("imageUrls", imageUrls.toString())
        Log.d("videoUrls", videoUrls.toString())
    }
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
                TopPost("Sửa bài viết", "Lưu", navHostController) {
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
                        postViewModel.updatePost(
                            postId = postId,
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
            item{
                if (progress in 0f..1f && progress != 0f) {
                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        CircularProgressIndicator(progress = progress)
                        Text("Đang đăng bài: ${(progress * 100).toInt()}%")
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

                    AddTagPost(tags = tags)
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