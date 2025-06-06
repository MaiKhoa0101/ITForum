package com.example.itforum.user.modelData.request

import android.net.Uri

data class ComplaintRequest(
    val userId: String,
    val title: String,
    val reason: String,
    val img: Uri?
)