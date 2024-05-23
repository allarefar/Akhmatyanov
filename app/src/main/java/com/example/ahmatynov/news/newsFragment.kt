package com.example.ahmatynov.news

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ahmatynov.databinding.FragmentNewsBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup

class newsFragment : Fragment() {
    private var _binding: FragmentNewsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNewsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val newsAdapter = NewsAdapter(emptyList())
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = newsAdapter
        }

        lifecycleScope.launch {
            val news = fetchNews()
            newsAdapter.newsList = news
            newsAdapter.notifyDataSetChanged()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private suspend fun fetchNews(): List<NewsItem> = withContext(Dispatchers.IO) {
        val newsList = mutableListOf<NewsItem>()
        try {
            val doc = Jsoup.connect("https://schooltashkinovo.02edu.ru/life/news/").get()
            val newsElements = doc.select("div[class=iblock-list-item-text w-md-60 pl-md-4]") // Предполагаемый селектор

            for (element in newsElements) {
                val title = element.select("h3").text()
                val date = element.select("p[class=t--1 c-text-secondary mb-2]").text()
                val link = element.select("a").attr("href")
                val description = element.select("p[class=t--3 t-uppercase px-3 py-2 bg-theme-1 с-text-primary l-inherit l-hover-primary d-inline-block mb-2]").text()

                val newsItem = NewsItem(title, date, link, description)
                newsList.add(newsItem)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return@withContext newsList
    }
}