package com.example.news.data

import androidx.room.*
import com.example.news.data.models.Article
import kotlinx.coroutines.flow.Flow


@Dao
interface ArticleDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(article: Article)

    @Delete
    suspend fun delete(article: Article)

    @Query("select * from article_db")
    fun getAllArticles(): Flow<List<Article>>

}