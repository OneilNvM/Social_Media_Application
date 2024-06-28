package com.example.oneilassignment2

import android.annotation.SuppressLint
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Calendar


class DirectMessageFragment : Fragment(), ContactsRecyclerViewInterface {

    private val chatDataViewModel: ChatDataViewModel by activityViewModels()
    private val contactsRefreshTrigger: ContactsViewModel by activityViewModels()
    private lateinit var db: SchoolSQLiteDatabase
    private lateinit var messagesList: ArrayList<MessageData>
    private lateinit var contactsList: ArrayList<ChatData>
    private lateinit var messagesAdapter: MessagesRecyclerViewAdapter

    private lateinit var chatActivity: ChatActivity
    private lateinit var userSession: SharedPreferences

    @SuppressLint("ClickableViewAccessibility", "SimpleDateFormat", "NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_direct_message, container, false)
        view.setOnTouchListener { _, _ ->  return@setOnTouchListener true}

        chatActivity = requireActivity() as ChatActivity
        db = SchoolSQLiteDatabase(chatActivity)
        userSession = chatActivity.getSharedPreferences("USER_SESSION", MODE_PRIVATE)
        val userId = userSession.getInt("student_id", 0)

        val recyclerView = view.findViewById<RecyclerView>(R.id.dm_recycler_view)
        val contactName = view.findViewById<TextView>(R.id.dm_contact_name)
        val messageInput = view.findViewById<EditText>(R.id.dm_message_input)
        val messageSendButton = view.findViewById<Button>(R.id.dm_send_message_button)
        val defaultText = view.findViewById<TextView>(R.id.dm_default_message)
        val backButton = view.findViewById<ImageButton>(R.id.dm_back_button)

        contactsList = ArrayList()
        messagesList = ArrayList()

        //        Handle UI changes based on dark mode
        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            backButton.setBackgroundResource(R.drawable.baseline_arrow_back_24_white)
        } else {
            backButton.setBackgroundResource(R.drawable.ic_arrow_back)
        }
        backButton.setOnClickListener {
            contactsRefreshTrigger.refresh()

            parentFragmentManager.popBackStack()
        }

        val chatData: ChatData

        if (chatDataViewModel.chatData != null) {
            chatData = chatDataViewModel.chatData!!
        } else {
            parentFragmentManager.popBackStack()
            return view
        }

        messageSendButton.setOnClickListener {
            val message = messageInput.text.toString()
            val currentDateTime = Calendar.getInstance().time
            val formattedDate = SimpleDateFormat("dd/MM/yyyy HH:mm").format(currentDateTime)

            try {
                if (message.isNotBlank()) {
                    db.insertMessage(message.trim(), formattedDate, chatData.id, userId)

                    val messageData = MessageData(
                        message,
                        formattedDate,
                        chatData.id,
                        userId,
                        true
                    )

                    if (chatData.message == null) {
                        defaultText.visibility = View.INVISIBLE
                    }

                    messagesAdapter.insertItem(messageData)

                    recyclerView.scrollToPosition(messagesList.size - 1)

                    messageInput.text.clear()
                } else {
                    Toast.makeText(chatActivity, "Please enter a message", Toast.LENGTH_SHORT)
                        .show()
                }
            } catch (e: Exception) {
                Log.d("DM_ERROR", "Error: ${e.message}")
            }
        }

        contactName.text = chatData.firstName

        if (chatData.message != null) {
            defaultText.visibility = View.GONE
            addToMessages(chatData.id)
        } else {
            defaultText.visibility = View.VISIBLE
        }

        recyclerView.layoutManager = LinearLayoutManager(chatActivity)

        messagesAdapter = MessagesRecyclerViewAdapter(messagesList)

        recyclerView.adapter = messagesAdapter

        recyclerView.scrollToPosition(messagesList.size - 1)

        return view
    }

    private fun addToMessages(id: Int) {
        val chatMessages = db.retrieveMessagesByChat(id)
        val userId = userSession.getInt("student_id", 0)

        if (chatMessages != null) {
            if (chatMessages.count > 0) {
                chatMessages.moveToFirst()

                while (!chatMessages.isAfterLast) {
                    val messageText = chatMessages.getString(chatMessages.getColumnIndexOrThrow("text"))
                    val messageDate = chatMessages.getString(chatMessages.getColumnIndexOrThrow("date"))
                    val chatId = chatMessages.getInt(chatMessages.getColumnIndexOrThrow("chat_id"))
                    val studentId = chatMessages.getInt(chatMessages.getColumnIndexOrThrow("student_id"))

                    if (studentId == userId) {
                        val message = MessageData(
                            messageText,
                            messageDate,
                            chatId,
                            studentId,
                            true
                        )

                        messagesList.add(message)
                    } else {
                        val message = MessageData(
                            messageText,
                            messageDate,
                            chatId,
                            studentId
                        )

                        messagesList.add(message)
                    }

                    chatMessages.moveToNext()
                }
            }
        }
        chatMessages?.close()
    }

    override fun onDestroyView() {
        super.onDestroyView()

        db.close()
    }

    override fun onRemoveButtonClicked(contact: ChatData, position: Int) {
//        Not implemented
    }

    override fun onContactClicked(contact: ChatData, position: Int) {
//        Not implemented
    }

}