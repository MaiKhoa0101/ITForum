package com.example.itforum.user.news

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.itforum.service.NewsDao
import com.example.itforum.user.modelData.request.NewsEntity

@Database(entities = [NewsEntity::class], version = 2)
abstract class NewsDatabase : RoomDatabase() {
    abstract fun newsDao(): NewsDao
}