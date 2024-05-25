package com.example.ahmatynov.notice

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ahmatynov.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class noticeFragment : Fragment() {

    private lateinit var database: DatabaseReference
    private lateinit var notificationAdapter: NotificationAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var addNotificationButton: Button
    private val notificationList = mutableListOf<Notification>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_notice, container, false)

        database = FirebaseDatabase.getInstance().getReference("notifications")
        recyclerView = view.findViewById(R.id.recyclerView)
        addNotificationButton = view.findViewById(R.id.addNotificationButton)
        notificationAdapter = NotificationAdapter(notificationList)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = notificationAdapter

        fetchNotifications()
        checkUserType()

        addNotificationButton.setOnClickListener {
            showAddNotificationDialog()
        }

        return view
    }

    private fun fetchNotifications() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                notificationList.clear()
                for (dataSnapshot in snapshot.children) {
                    val notification = dataSnapshot.getValue(Notification::class.java)
                    if (notification != null) {
                        notificationList.add(notification)
                    }
                }
                notificationAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle possible errors.
            }
        })
    }

    private fun checkUserType() {
        val sharedPreferences = requireActivity().getSharedPreferences("MyApp", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getString("USER_ID", "") ?: return

        val userRef = FirebaseDatabase.getInstance().getReference("users").child(userId)

        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val userType = snapshot.child("type").getValue(String::class.java)
                Log.d("NoticeFragment", "User type: $userType")
                if (userType == "Учитель") {
                    addNotificationButton.visibility = View.VISIBLE
                    Log.d("NoticeFragment", "Button is visible")
                } else {
                    addNotificationButton.visibility = View.GONE
                    Log.d("NoticeFragment", "Button is gone")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle possible errors.
            }
        })
    }

    private fun showAddNotificationDialog() {
        val builder = AlertDialog.Builder(requireContext())
        val inflater = layoutInflater
        val dialogLayout = inflater.inflate(R.layout.dialog_add_notification, null)
        val titleEditText = dialogLayout.findViewById<EditText>(R.id.titleEditText)
        val messageEditText = dialogLayout.findViewById<EditText>(R.id.messageEditText)

        with(builder) {
            setTitle("Добавить уведомление")
            setView(dialogLayout)
            setPositiveButton("Отправить") { dialog, which ->
                val title = titleEditText.text.toString().trim()
                val message = messageEditText.text.toString().trim()
                if (title.isNotEmpty() && message.isNotEmpty()) {
                    sendNotification(title, message)
                } else {
                    Toast.makeText(requireContext(), "Заполните все поля", Toast.LENGTH_SHORT).show()
                }
            }
            setNegativeButton("Отмена") { dialog, which -> }
            show()
        }
    }

    private fun sendNotification(title: String, message: String) {
        val notificationId = database.push().key ?: return
        val notification = Notification(message, title)
        database.child(notificationId).setValue(notification)
    }
}