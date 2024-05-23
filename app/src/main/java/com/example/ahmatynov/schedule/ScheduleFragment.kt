package com.example.ahmatynov.schedule

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ahmatynov.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage

class ScheduleFragment : Fragment() {

    private lateinit var database: DatabaseReference
    private lateinit var storage: FirebaseStorage
    private lateinit var imageUrlList: MutableList<String>
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ImageAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_schedule, container, false)

        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)
        imageUrlList = mutableListOf()

        val navController = findNavController()
        adapter = ImageAdapter(imageUrlList, navController)
        recyclerView.adapter = adapter

        val spacing = resources.getDimensionPixelSize(R.dimen.recycler_view_spacing)
        recyclerView.addItemDecoration(SpacingItemDecoration(spacing))

        database = FirebaseDatabase.getInstance().reference.child("images")
        storage = FirebaseStorage.getInstance()

        fetchImages()

        return view
    }

    private fun fetchImages() {
        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (dataSnapshot in snapshot.children) {
                    val imageUrl = dataSnapshot.getValue(String::class.java)
                    imageUrl?.let { imageUrlList.add(it) }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle possible errors.
            }
        })
    }
}