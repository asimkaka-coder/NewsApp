package com.example.news.data.repository

import androidx.room.Dao
import com.example.news.data.ArticleDao
import com.example.news.data.api.NewsApi
import com.example.news.data.models.Article
import com.example.news.data.models.NewsResponse
import com.example.news.others.Resource
import javax.inject.Inject

class MainRepository @Inject constructor(private val api:NewsApi,
private val dao: ArticleDao) {


    suspend fun getBreakingNews(page:Int)  = api.getHeadLines(page = page)

    suspend fun searchNews(query:String) = api.searchNews(query)


    suspend fun insertArticle(article: Article) = dao.insert(article)

    suspend fun deleteArticle(article: Article) = dao.delete(article)

    fun getAllArticles() = dao.getAllArticles()

}