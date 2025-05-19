package com.example.itforum.headbar.myfeed

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

interface TagRepository {
    fun getSuggestedTags(): List<String>
    fun getMyTags(): List<String>
    fun addTag(tag: String)
    fun removeTag(tag: String)
}

class MockTagRepository : TagRepository {
    private val suggestedTags = listOf(
        "React", "React", "React",
        "React", "React", "React",
        "React", "React", "React",
        "React", "React", "React"
    )

    private val myTags = listOf(
        "AI",
        "Big data",
        "ChatGPT",
        "Docker",
        "Linux",
        "Go",
        "AAAAA"
    )

    private val selectedTags = mutableListOf<String>()

    override fun getSuggestedTags(): List<String> = suggestedTags

    override fun getMyTags(): List<String> = myTags

    override fun addTag(tag: String) {
        if (tag !in selectedTags) {
            selectedTags.add(tag)
        }
    }

    override fun removeTag(tag: String) {
        selectedTags.remove(tag)
    }
}

@Composable
fun rememberTagRepository(): TagRepository {
    return remember { MockTagRepository() }
}