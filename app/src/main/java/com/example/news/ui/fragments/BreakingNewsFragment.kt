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
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.news.R
import com.example.news.adapters.NewsAdapter
import com.example.news.databinding.BreakingnewsFragmentBinding
import com.example.news.others.Resource
import com.example.news.ui.viewmodels.BreakingViewModel
import com.example.news.ui.viewmodels.SavedNewsViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class BreakingNewsFragment:Fragment(R.layout.breakingnews_fragment) {

    val viewModel : BreakingViewModel by viewModels()
    val saveViewModel : SavedNewsViewModel by viewModels()
    private var _binding:BreakingnewsFragmentBinding? = null
    val binding get() = _binding!!
    lateinit var madapter:NewsAdapter


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = BreakingnewsFragmentBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpRecyclerView()

        madapter.setOnArticleListener { article->

            val customTabIntent = CustomTabsIntent.Builder().build()
            customTabIntent.launchUrl(requireContext(),article.url!!.toUri())



        }

        madapter.setOnArticleSaveListener { article ->
            saveViewModel.insertArticle(article)
            Snackbar.make(view,"Article Saved Succesfully",Snackbar.LENGTH_SHORT).show()
        }

        madapter.setOnArticleShareListener { articleUrl->
            val intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT,articleUrl)
                type = "text/plain"
            }
            startActivity(Intent.createChooser(intent,"Share News using: "))
        }

        viewModel.breakingNews.observe(viewLifecycleOwner,{newsResource->
            when(newsResource){
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
                    madapter.submitList(newsResource.data?.articles)
                }
            }
        })

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    fun setUpRecyclerView(){
        madapter = NewsAdapter()
        binding.rvBreakingNews.apply {

            adapter = madapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }


}