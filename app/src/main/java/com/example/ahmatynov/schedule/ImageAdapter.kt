package com.example.ahmatynov.schedule

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ahmatynov.R

class ImageAdapter(
    private val imageUrlList: List<String>,
    private val navController: NavController
) : RecyclerView.Adapter<ImageAdapter.ImageViewHolder>() {

    class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_image, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val imageUrl = imageUrlList[position]
        Glide.with(holder.itemView.context)
            .load(imageUrl)
            .into(holder.imageView)

        holder.itemView.setOnClickListener {
            val action = ScheduleFragmentDirections.actionScheduleFragmentToFullScreenImageFragment(imageUrl)
            navController.navigate(action)
        }
    }

    override fun getItemCount(): Int = imageUrlList.size
}