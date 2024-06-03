package com.example.oneilassignment2

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView

class ChatActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_chat)

        val closeButton = findViewById<ImageButton>(R.id.chat_close_button)
        val addContactButton = findViewById<ImageButton>(R.id.chat_add_contact_button)
        val addContactCard = findViewById<CardView>(R.id.chat_add_contact_card)
        val searchBar = findViewById<EditText>(R.id.chat_search_box)
        val addContactBackButton = findViewById<ImageButton>(R.id.chat_add_contact_back_button)
        val contactsRecyclerView = findViewById<RecyclerView>(R.id.chat_contacts_recycler_view)
        val addContactRecyclerView = findViewById<RecyclerView>(R.id.chat_add_contact_recycler_view)

        addContactButton.setOnClickListener {
            addContactCard.visibility = View.VISIBLE

            closeButton.isClickable = false
            addContactButton.isClickable = false
            contactsRecyclerView.visibility = View.INVISIBLE
        }

        addContactBackButton.setOnClickListener {
            addContactCard.visibility = View.GONE

            closeButton.isClickable = true
            addContactButton.isClickable = true
            contactsRecyclerView.visibility = View.VISIBLE
        }

        closeButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)

            finish()
        }
    }
}