package com.example.news.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.net.toUri
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels

import androidx.recyclerview.widget.LinearLayoutManager

import com.example.news.R
import com.example.news.adapters.NewsAdapter
import com.example.news.databinding.SearchnewsFragmentBinding
import com.example.news.others.Resource
import com.example.news.others.constants.SEARCH_NEWS_TIME_DELAY
import com.example.news.ui.viewmodels.SavedNewsViewModel
import com.example.news.ui.viewmodels.SearchViewModel
import com.google.android.material.chip.Chip
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@AndroidEntryPoint
class SearchNewsFragment:Fragment(R.layout.searchnews_fragment) {


    lateinit var madapter:NewsAdapter
    private var _binding:SearchnewsFragmentBinding? = null
    val binding get() = _binding!!
    val searchViewModel:SearchViewModel by viewModels()
    val saveViewModel : SavedNewsViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = SearchnewsFragmentBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpRecyclerView()



        madapter.setOnArticleListener { article ->

            val customTabIntent = CustomTabsIntent.Builder().build()
            customTabIntent.launchUrl(requireContext(),article.url!!.toUri())
        }


        madapter.setOnArticleShareListener { articleUrl->
            val intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT,articleUrl)
                type = "text/plain"
            }
            startActivity(Intent.createChooser(intent,"Share News using: "))
        }

        madapter.setOnArticleSaveListener { article ->
            saveViewModel.insertArticle(article)
            Snackbar.make(view,"Article Saved Succesfully", Snackbar.LENGTH_SHORT).show()
        }


        var job: Job? = null
        binding.searchView.addTextChangedListener { editable->


            job?.cancel()
            job = MainScope().launch {
                delay(SEARCH_NEWS_TIME_DELAY)
                editable?.let {
                    if(editable.toString().isNotEmpty()) {

                        binding.chipGroup.clearCheck()
                        searchViewModel.setQuery(editable.toString())
                    }
                    else{
                        madapter.submitList(listOf())
                    }
                }
            }

        }

        searchViewModel.searchQuery.observe(viewLifecycleOwner,{
            searchViewModel.searchForNews(it)
        })

        searchViewModel.searchedNews.observe(viewLifecycleOwner,  {
            when(it){
                is Resource.Loading ->{
                    binding.errorImage.visibility = View.GONE
                    binding.pgBar.visibility = View.VISIBLE

                }
                is Resource.Error->{

                    binding.errorImage.visibility = View.VISIBLE
                    binding.pgBar.visibility = View.GONE

                }
                is Resource.Success->{
                    binding.errorImage.visibility = View.GONE
                    binding.pgBar.visibility = View.GONE
                    madapter.submitList(it.data?.articles)
                }
            }
        })




        binding.chipGroup.setOnCheckedChangeListener { group, checkedId ->

            val chip:Chip? = group.findViewById<Chip>(checkedId)
            chip?.let {
                binding.searchView.clearFocus()
                binding.searchView.text.clear()
                searchViewModel.setQuery(chip.text.toString())
            }
        }


    }

    fun setUpRecyclerView(){
        madapter = NewsAdapter()
        binding.rvSeachNews.apply {

            adapter = madapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}