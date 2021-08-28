package com.example.news.data

import androidx.room.*
import com.example.news.data.models.Article
import com.example.news.others.Converters


@Database(
    entities = [Article::class],
    version = 3
)
@TypeConverters(Converters::class)
abstract class ArticleDatabase:RoomDatabase() {

    abstract fun getDao():ArticleDao
}