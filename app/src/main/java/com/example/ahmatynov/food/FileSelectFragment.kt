package com.example.ahmatynov.food

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.ahmatynov.R
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class FileSelectFragment : Fragment() {

    private lateinit var storageReference: StorageReference
    private lateinit var fileListView: ListView
    private val fileList: MutableList<String> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_file_select, container, false)

        storageReference = FirebaseStorage.getInstance().reference

        fileListView = view.findViewById(R.id.file_list_view)

        loadFileList()

        fileListView.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            val selectedFile = fileList[position]
            val action = FileSelectFragmentDirections.actionFileSelectFragmentToFoodFragment(selectedFile)
            findNavController().navigate(action)
        }

        return view
    }

    private fun loadFileList() {
        fileList.clear() // Очистка списка файлов перед загрузкой

        val userClass = getUserClassFromSharedPreferences()
        val path = "food/"

        val listRef = storageReference.child(path)
        listRef.listAll()
            .addOnSuccessListener { listResult ->
                for (item in listResult.items) {
                    fileList.add(item.name)
                }
                val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, fileList)
                fileListView.adapter = adapter
            }
            .addOnFailureListener { exception ->
                Toast.makeText(context, "Ошибка загрузки списка файлов: ${exception.message}", Toast.LENGTH_LONG).show()
                Log.e("FileSelectFragment", "Ошибка загрузки списка файлов", exception)
            }
    }

    private fun getUserClassFromSharedPreferences(): String {
        val sharedPreferences = requireContext().getSharedPreferences("MyApp", Context.MODE_PRIVATE)
        return sharedPreferences.getString("USER_CLASS", "") ?: ""
    }
}