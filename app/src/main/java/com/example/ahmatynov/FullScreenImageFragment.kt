package com.example.ahmatynov

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide

class FullScreenImageFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_full_screen_image, container, false)
        val imageView: ImageView = view.findViewById(R.id.fullScreenImageView)

        val args: FullScreenImageFragmentArgs by navArgs()
        val imageUrl = args.imageUrl

        Glide.with(this)
            .load(imageUrl)
            .into(imageView)

        return view
    }
}