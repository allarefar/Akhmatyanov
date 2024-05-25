package com.example.ahmatynov

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.ahmatynov.reg.AuthActivity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class profileFragment : Fragment() {

    private lateinit var database: DatabaseReference

    private lateinit var last_name: TextView
    private lateinit var first_name: TextView
    private lateinit var class_name: TextView
    private lateinit var type_name: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        last_name = view.findViewById(R.id.last_name)
        first_name = view.findViewById(R.id.first_name)
        class_name = view.findViewById(R.id.class_name)
        type_name = view.findViewById(R.id.type_name)

        database = FirebaseDatabase.getInstance().getReference("users")

        loadUserProfile()

        val buttonLogout: Button = view.findViewById(R.id.button_logout)
        buttonLogout.setOnClickListener {
            logout()
        }

        return view
    }

    private fun logout() {
        // Удаляем сохраненные данные пользователя из SharedPreferences
        val sharedPreferences = requireContext().getSharedPreferences("MyApp", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.remove("USER_ID")
        editor.remove("IS_LOGGED_IN")
        editor.apply()

        // Перенаправляем пользователя на страницу авторизации
        val intent = Intent(requireContext(), AuthActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    private fun loadUserProfile() {
        val sharedPreferences = requireContext().getSharedPreferences("MyApp", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getString("USER_ID", null)

        userId?.let {
            database.child(it).get().addOnSuccessListener { dataSnapshot ->
                if (dataSnapshot.exists()) {
                    val firstname = dataSnapshot.child("firstname").value.toString()
                    val lastname = dataSnapshot.child("lastname").value.toString()
                    val className = dataSnapshot.child("className").value.toString()
                    val type = dataSnapshot.child("type").value.toString()

                    type_name.text = type
                    class_name.text = className
                    first_name.text = firstname
                    last_name.text = lastname
                } else {
                    // Обработка случая, когда данные не найдены
                    Toast.makeText(requireContext(), "Пользовательские данные не найдены", Toast.LENGTH_LONG).show()
                    val intent = Intent(requireContext(), AuthActivity::class.java)
                    startActivity(intent)
                    activity?.finish()
                }
            }.addOnFailureListener {
                // Обработка ошибки при получении данных
                Toast.makeText(requireContext(), "Ошибка при загрузке данных: ${it.message}", Toast.LENGTH_LONG).show()
            }
        } ?: run {
            // Обработка случая, когда идентификатор пользователя не найден
            Toast.makeText(requireContext(), "Пользователь не авторизован", Toast.LENGTH_LONG).show()
            // Перенаправляем на экран логина
            val intent = Intent(requireContext(), AuthActivity::class.java)
            startActivity(intent)
            activity?.finish()
        }
    }
}