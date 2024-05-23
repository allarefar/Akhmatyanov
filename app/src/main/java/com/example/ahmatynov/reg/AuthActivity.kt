package com.example.ahmatynov.reg

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.ahmatynov.HomeActivity
import com.example.ahmatynov.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AuthActivity : AppCompatActivity() {

    private lateinit var database: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sharedPreferences = getSharedPreferences("MyApp", Context.MODE_PRIVATE)
        val isLoggedIn = sharedPreferences.getBoolean("IS_LOGGED_IN", false)

        if (isLoggedIn) {
            // Пользователь уже авторизован, переходим на основной экран
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
            return
        }

        setContentView(R.layout.activity_auth)

        val userLogin: EditText = findViewById(R.id.user_login_auth)
        val userPass: EditText = findViewById(R.id.user_pass_auth)
        val button: Button = findViewById(R.id.button_auth)
        val linkToReg: TextView = findViewById(R.id.link_to_reg)

        database = FirebaseDatabase.getInstance()

        linkToReg.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        button.setOnClickListener {
            val login = userLogin.text.toString().trim()
            val pass = userPass.text.toString().trim()

            if (login.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Не все поля заполнены", Toast.LENGTH_LONG).show()
            } else {
                authenticateUser(login, pass)
            }
        }
    }

    private fun authenticateUser(email: String, pass: String) {
        val usersRef = database.reference.child("users")
        val query = usersRef.orderByChild("email").equalTo(email)

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (userSnapshot in snapshot.children) {
                        val user = userSnapshot.getValue(User::class.java)
                        if (user != null && user.password == pass) {
                            // Сохраняем идентификатор пользователя в SharedPreferences
                            val sharedPreferences = getSharedPreferences("MyApp", Context.MODE_PRIVATE)
                            val editor = sharedPreferences.edit()
                            editor.putString("USER_ID", userSnapshot.key)
                            editor.putBoolean("IS_LOGGED_IN", true)
                            editor.apply()

                            Toast.makeText(this@AuthActivity, "Успешная авторизация", Toast.LENGTH_LONG).show()
                            val intent = Intent(this@AuthActivity, HomeActivity::class.java)
                            startActivity(intent)
                            finish()
                            return
                        } else {
                            Toast.makeText(this@AuthActivity, "Неверный пароль", Toast.LENGTH_LONG).show()
                        }
                    }
                } else {
                    Toast.makeText(this@AuthActivity, "Пользователь не найден", Toast.LENGTH_LONG).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@AuthActivity, "Ошибка базы данных: ${error.message}", Toast.LENGTH_LONG).show()
            }
        })
    }
}