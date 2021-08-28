package com.example.news.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.news.R
import com.example.news.adapters.NewsAdapter
import com.example.news.databinding.SavednewsFragmentBinding
import com.example.news.ui.viewmodels.SavedNewsViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class SavedNewsFragment:Fragment(R.layout.savednews_fragment) {


    private var _binding:SavednewsFragmentBinding? = null
    val binding get() = _binding!!
    private val viewModel:SavedNewsViewModel by viewModels()
    lateinit var sadapter: NewsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = SavednewsFragmentBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpRecyclerView()
        viewModel.listSavedNews.observe(viewLifecycleOwner,{
            sadapter.submitList(it)
        })

        sadapter.setOnArticleSaveListener { article ->
            viewModel.deleteArticle(article)
            Snackbar.make(view,"Article Removed Succesfully",Snackbar.LENGTH_SHORT).show()
        }

        sadapter.setOnArticleShareListener { articleUrl->
            val intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT,articleUrl)
                type = "text/plain"
            }
            startActivity(Intent.createChooser(intent,"Share News using: "))
        }

        sadapter.setOnArticleListener { article->

            val customTabIntent = CustomTabsIntent.Builder().build()
            customTabIntent.launchUrl(requireContext(),article.url!!.toUri())



        }

        val callback = object :ItemTouchHelper.SimpleCallback(
            0,ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ){
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val removedArticlePosition = viewHolder.adapterPosition
                val removedArticle = sadapter.currentList[removedArticlePosition]
                viewModel.deleteArticle(removedArticle)
                Snackbar.make(view,"Removed Saved Article",Snackbar.LENGTH_LONG).apply {
                    setAction("Undo"){
                        viewModel.insertArticle(removedArticle)
                    }
                    show()
                }
            }
        }

        ItemTouchHelper(callback).attachToRecyclerView(binding.rvSavedNews)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


    fun setUpRecyclerView(){
        sadapter = NewsAdapter()
        binding.rvSavedNews.apply {

            adapter = sadapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }
}