package com.example.ahmatynov

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.database.*

class MainActivity : AppCompatActivity() {

    private lateinit var database: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val userLogin: EditText = findViewById(R.id.user_login)
        val userEmail: EditText = findViewById(R.id.user_email)
        val userPass: EditText = findViewById(R.id.user_pass)
        val button: Button = findViewById(R.id.button_reg)
        val linkToAuth: TextView = findViewById(R.id.link_to_auth)

        database = FirebaseDatabase.getInstance()

        linkToAuth.setOnClickListener {
            val intent = Intent(this, AuthActivity::class.java)
            startActivity(intent)
        }

        button.setOnClickListener {
            val login = userLogin.text.toString().trim()
            val email = userEmail.text.toString().trim()
            val pass = userPass.text.toString().trim()

            if (login.isEmpty() || email.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Не все поля заполнены", Toast.LENGTH_LONG).show()
            } else {
                registerUser(login, email, pass)
            }
        }
    }

    private fun registerUser(login: String, email: String, pass: String) {
        val user = User(login, email, pass)
        val userId = database.reference.child("users").push().key

        userId?.let {
            database.reference.child("users").child(it).setValue(user)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Пользователь $login добавлен", Toast.LENGTH_LONG).show()
                        clearFields()
                    } else {
                        Toast.makeText(this, "Ошибка при добавлении пользователя в базу данных: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                    }
                }
        }
    }

    private fun clearFields() {
        findViewById<EditText>(R.id.user_login).text.clear()
        findViewById<EditText>(R.id.user_email).text.clear()
        findViewById<EditText>(R.id.user_pass).text.clear()
    }
}