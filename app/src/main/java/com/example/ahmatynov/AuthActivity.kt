package com.example.ahmatynov

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.database.*

class AuthActivity : AppCompatActivity() {

    private lateinit var database: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
