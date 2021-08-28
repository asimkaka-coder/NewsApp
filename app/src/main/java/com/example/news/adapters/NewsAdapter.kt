package com.example.news.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.news.R
import com.example.news.data.models.Article
import com.example.news.databinding.NewsItemBinding

class NewsAdapter() : ListAdapter<Article, NewsAdapter.ArticleViewHolder>(diffCallBack) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        return ArticleViewHolder(
            NewsItemBinding.inflate(
                LayoutInflater.from(
                    parent.context
                ), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        val currentArticle = getItem(position)
        holder.apply {
            bind(article = currentArticle)
            binding.root.setOnClickListener {
                onArticleListener?.let { it(currentArticle) }
            }

            binding.shareButton1.setOnClickListener {
                onArticleShareListener?.let { it(currentArticle.url!!) }
            }

            binding.addFav.setOnClickListener {
                onArticleSaveListener?.let { it(currentArticle) }
            }


        }



    }

    inner class ArticleViewHolder(val binding: NewsItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(article: Article) {
            binding.newsImage.load(article.urlToImage) {
                crossfade(true)
                crossfade(1000)
            }
            binding.newsTitle.text = article.title
            binding.newsSubtitle.text = article.description
            binding.date.text = article.publishedAt
            binding.srcTitle.text = article.source?.name

        }
    }

    companion object {
        val diffCallBack = object : DiffUtil.ItemCallback<Article>() {
            override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
                return oldItem.url == newItem.url
            }

            override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
                return oldItem.content == newItem.content
            }
        }
    }

    private var onArticleListener: ((Article) -> Unit)? = null
    private var onArticleShareListener: ((String) -> Unit)? = null
    private var onArticleSaveListener: ((Article) -> Unit)? = null

    fun setOnArticleListener(listener: (Article) -> Unit) {
        onArticleListener = listener
    }

    fun setOnArticleShareListener(listener: (String) -> Unit) {
        onArticleShareListener = listener
    }

    fun setOnArticleSaveListener(listener: (Article) -> Unit){
        onArticleSaveListener = listener
    }


}