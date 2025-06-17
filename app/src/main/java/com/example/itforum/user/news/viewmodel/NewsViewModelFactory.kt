package com.example.itforum.user.news.viewmodel

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.itforum.service.NewsDao

class NewsViewModelFactory (
    private val newsDao: NewsDao,
    private val sharedPreferences: SharedPreferences
) : ViewModelProvider.Factory {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(NewsViewModel::class.java)) {
                return NewsViewModel(newsDao, sharedPreferences) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
}