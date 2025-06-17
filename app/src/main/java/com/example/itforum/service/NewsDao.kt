package com.example.itforum.service

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.itforum.user.modelData.request.NewsEntity
import com.example.itforum.user.modelData.response.News
import kotlinx.coroutines.flow.Flow

@Dao
interface NewsDao {
    @Query("SELECT * FROM news ORDER BY createdAt DESC")
    fun getAllNews(): Flow<List<NewsEntity>>

    @Query("SELECT * FROM news WHERE id = :id")
    suspend fun getNewsById(id: String): NewsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNews(news: NewsEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(news: List<NewsEntity>)

    @Query("SELECT * FROM news WHERE isSynced = 0")
    suspend fun getPendingNews(): List<NewsEntity>

    @Query("DELETE FROM news WHERE id = :id")
    suspend fun deleteNews(id: String)

    @Query("DELETE FROM news")
    suspend fun clearAllNews()
}

fun News.toEntity() = NewsEntity(id, adminId, title, content, img, createdAt)
fun NewsEntity.toModel() = News(id, adminId, title, content, img, createdAt)