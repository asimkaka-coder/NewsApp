package com.example.news.di

import android.content.Context
import androidx.room.Room
import com.example.news.data.ArticleDao
import com.example.news.data.ArticleDatabase
import com.example.news.data.api.NewsApi
import com.example.news.data.repository.MainRepository
import com.example.news.others.constants
import com.example.news.others.constants.BASE_URL
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {


    @Provides
    fun provideBaseurl() = constants.BASE_URL


    @Provides
    @Singleton
    fun provideDataBase(
        @ApplicationContext context: Context
    ) = Room.databaseBuilder(context,
        ArticleDatabase::class.java,"ArtcleTable.db"
        ).fallbackToDestructiveMigration().build()


    @Provides
    @Singleton
    fun provideDao(database: ArticleDatabase) = database.getDao()

    @Provides
    @Singleton
    fun provideNewsApi():NewsApi = Retrofit
        .Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build().create(NewsApi::class.java)

    @Provides
    @Singleton
    fun providesRepository(api: NewsApi,dao: ArticleDao) = MainRepository(api,dao)
}