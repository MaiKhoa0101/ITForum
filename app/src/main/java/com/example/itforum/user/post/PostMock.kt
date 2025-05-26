package com.example.itforum.user.post


import com.example.itforum.R
import com.example.itforum.user.post.model.postModel

val postList = listOf(
    postModel(
        id = 1,
        author = "ByteByteGo",
        readTime = "5m read time",
        date = "Apr05",
        title = "Javascript is easy... Until you meet these confusing concepts",
        imageRes = R.drawable.post,
        likes = 50
    ),
    postModel(
        id = 2,
        author = "DevTo",
        readTime = "4m read time",
        date = "Apr12",
        title = "Why 'this' in JavaScript is still confusing in 2025",
        imageRes = R.drawable.post,
        likes = 34
    ),
    postModel(
        id = 3,
        author = "DevTo",
        readTime = "4m read time",
        date = "Apr12",
        title = "Why 'this' in JavaScript is still confusing in 2025",
        imageRes = R.drawable.post,
        likes = 34
    )
)
