package com.example.ahmatynov.news

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.ahmatynov.databinding.ItemNewsBinding

class NewsAdapter(var newsList: List<NewsItem>) : RecyclerView.Adapter<NewsAdapter.NewsViewHolder>() {

    class NewsViewHolder(val binding: ItemNewsBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val binding = ItemNewsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NewsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        with(holder.binding) {
            newsTitle.text = newsList[position].title
            newsDate.text = newsList[position].date
            newsDescription.text = newsList[position].description
        }
    }

    override fun getItemCount(): Int = newsList.size
}