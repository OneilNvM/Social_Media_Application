package com.example.oneilassignment2

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Intent
import android.icu.util.Calendar
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.fragment.app.commit
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat

private lateinit var db: SchoolSQLiteDatabase

class ChatActivity : AppCompatActivity(), SearchContactRecyclerViewInterface, ContactsRecyclerViewInterface {

    private lateinit var studentsA: ArrayList<StudentData>
    private lateinit var contacts: ArrayList<ChatData>
    private lateinit var searchAdapter: SearchContactRecyclerViewAdapter
    private lateinit var contactsAdapter: ContactsRecyclerViewAdapter
    private lateinit var chatDataViewModel: ChatDataViewModel
    private lateinit var contactsViewModel: ContactsViewModel

    private lateinit var chatTopConstraint: ConstraintLayout
    private lateinit var chatDivider1: View
    private lateinit var chatDivider2: View
    private lateinit var contactsRecyclerView: RecyclerView

    private lateinit var listMethods: RecyclerViewListMethods


    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_chat)

        chatDataViewModel = ViewModelProvider(this)[ChatDataViewModel::class.java]
        contactsViewModel = ViewModelProvider(this)[ContactsViewModel::class.java]

        db = SchoolSQLiteDatabase(this)

        val closeButton = findViewById<ImageButton>(R.id.chat_close_button)
        val addContactButton = findViewById<ImageButton>(R.id.chat_add_contact_button)
        val addContactCard = findViewById<CardView>(R.id.chat_add_contact_card)
        val searchBar = findViewById<EditText>(R.id.chat_search_box)
        val addContactBackButton = findViewById<ImageButton>(R.id.chat_add_contact_back_button)
        val addContactRecyclerView = findViewById<RecyclerView>(R.id.chat_add_contact_recycler_view)
        val noResultsText = findViewById<TextView>(R.id.chat_no_results_text)

        chatTopConstraint = findViewById(R.id.chat_top_constraint)
        chatDivider1 = findViewById(R.id.chat_divider_1)
        chatDivider2 = findViewById(R.id.chat_divider_2)
        contactsRecyclerView = findViewById(R.id.chat_contacts_recycler_view)

        listMethods = RecyclerViewListMethods(this)

        //        Set back pressed callback to handle back button press
        onBackPressedDispatcher.addCallback(this, object :
            OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
//                Go back to previous fragment or exit the app if there is no previous fragment
                try {
                    if (addContactCard.isVisible) {
                        addContactCard.animate()
                            .alpha(0.0f)
                            .setDuration(250)
                            .setListener(object : AnimatorListenerAdapter() {
                                override fun onAnimationEnd(animation: Animator) {
                                    addContactCard.visibility = View.GONE
                                }
                            })

                        studentsA.clear()

                        searchBar.text.clear()

                        closeButton.isClickable = true
                        addContactButton.isClickable = true
                        contactsRecyclerView.visibility = View.VISIBLE
                    } else if (supportFragmentManager.backStackEntryCount >= 1) {
                        supportFragmentManager.popBackStackImmediate()

                        chatTopConstraint.visibility = View.VISIBLE
                        chatDivider1.visibility = View.VISIBLE
                        chatDivider2.visibility = View.VISIBLE
                        contactsRecyclerView.visibility = View.VISIBLE
                    } else {
                        val intent = Intent(this@ChatActivity, MainActivity::class.java)
                        startActivity(intent)

                        finish()
                    }
                } catch (e: Exception) {
                    Log.e(ContentValues.TAG, "Error: ${e.message}")
                }
            }
        })

        studentsA = ArrayList()
        contacts = ArrayList()

        addContactButton.setOnClickListener {
            addContactCard.visibility = View.VISIBLE
            addContactCard.animate()
                .setDuration(250)
                .alpha(1.0f)
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        addContactCard.visibility = View.VISIBLE
                    }
                })

            val textWatcher = object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
//                    No Implementation
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
//                    No Implementation
                }

                @SuppressLint("NotifyDataSetChanged")
                override fun afterTextChanged(s: Editable?) {
                    studentsA.clear()

                    studentsA = listMethods.addToSearches(s.toString(), studentsA)

                    Log.d("TotalStudents", "Here are the students: $studentsA")

                    if (studentsA.size == 0) {
                        noResultsText.visibility = View.VISIBLE
                    } else {
                        noResultsText.visibility = View.GONE
                    }

                    Log.d("TotalStudents", "Number of Students: ${studentsA.size}")
                    searchAdapter.updateItemRange(studentsA)
                }

            }

            addContactRecyclerView.itemAnimator = null

            searchBar.addTextChangedListener(textWatcher)

            searchAdapter = SearchContactRecyclerViewAdapter(studentsA, this)

            addContactRecyclerView.layoutManager = LinearLayoutManager(this)

            addContactRecyclerView.adapter = searchAdapter

            closeButton.isClickable = false
            addContactButton.isClickable = false
            contactsRecyclerView.visibility = View.INVISIBLE
        }

        addContactBackButton.setOnClickListener {
            addContactCard.animate()
                .alpha(0.0f)
                .setDuration(250)
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        addContactCard.visibility = View.GONE
                    }
                })

            studentsA.clear()

            searchBar.text.clear()

            closeButton.isClickable = true
            addContactButton.isClickable = true
            contactsRecyclerView.visibility = View.VISIBLE
        }

        closeButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)

            finish()
        }

//        This piece of code observes the ContactsViewModel's refreshValue LiveData and updates the UI accordingly.
        contactsViewModel.refreshValue.observe(this) {
                currentValue ->
            if (currentValue) {
                contacts.clear()

                contacts = listMethods.addToContacts(contacts)

                contactsAdapter.notifyDataSetChanged()

                contactsViewModel.refreshComplete()
                Log.d("ContactsViewModel", "Refresh Complete")
            }
        }

        contacts = listMethods.addToContacts(contacts)

        contactsAdapter = ContactsRecyclerViewAdapter(contacts, this)

        contactsRecyclerView.layoutManager = LinearLayoutManager(this)

        contactsRecyclerView.adapter = contactsAdapter

    }

    @SuppressLint("SimpleDateFormat", "NotifyDataSetChanged")
    override fun onActionButtonClicked(result: StudentData, position: Int) {
        val (studentId, firstName, isAdded) = result
        val userSession = this.getSharedPreferences("USER_SESSION", MODE_PRIVATE)
        val userId = userSession.getInt("student_id", 0)

        val studentData: StudentData?

        val date = Calendar.getInstance().time
        val formattedDate = SimpleDateFormat("yyyy-MM-dd HH:mm").format(date)

        if (!isAdded) {
            db.insertChat(formattedDate, userId, studentId)

            studentData = StudentData(
                studentId,
                firstName,
                true
            )

            contacts.clear()

            contacts = listMethods.addToContacts(contacts)

            contactsAdapter.notifyDataSetChanged()
        } else {
            val chat = db.retrieveSpecificChat(userId, studentId)

            Log.d("Cursor Count", "Count: ${chat?.count}")

            if (chat != null) {
                if (chat.count > 0) {
                    chat.moveToFirst()

                    val chatId = chat.getInt(chat.getColumnIndexOrThrow("chat_id"))

                    db.deleteChatMessages(chatId)
                    db.deleteChat(chatId)
                }
            }
            chat?.close()

            studentData = StudentData(
                studentId,
                firstName,
                false
            )

            contacts.clear()

            contacts = listMethods.addToContacts(contacts)

            contactsAdapter.notifyDataSetChanged()
        }


        val arrayOfStudent = ArrayList<StudentData>()
        arrayOfStudent.add(studentData)

        searchAdapter.updateItems(arrayOfStudent, position)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onRemoveButtonClicked(contact: ChatData, position: Int) {
        val (chatId, _, _, _, _, _) = contact

        db.deleteChatMessages(chatId)
        db.deleteChat(chatId)

        contacts.removeAt(position)
        contactsAdapter.notifyDataSetChanged()
    }

    override fun onContactClicked(contact: ChatData, position: Int) {
        chatDataViewModel.chatData = contact

        supportFragmentManager.commit {
            setCustomAnimations(
                R.anim.fade_in_fragment,
                R.anim.slide_out_fragment,
                R.anim.fade_in_fragment,
                R.anim.slide_out_fragment,
            )
            add(R.id.chat_contact_fragment_container, DirectMessageFragment())
            addToBackStack("Chat")
        }
    }

    override fun onStart() {
        super.onStart()

        val closeButton = findViewById<ImageButton>(R.id.chat_close_button)
        val addContactButton = findViewById<ImageButton>(R.id.chat_add_contact_button)
        val addContactBackButton = findViewById<ImageButton>(R.id.chat_add_contact_back_button)

        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            closeButton.setBackgroundResource(R.drawable.baseline_close_24_white)
            addContactButton.setBackgroundResource(R.drawable.add__1_)
            addContactBackButton.setBackgroundResource(R.drawable.baseline_arrow_back_24_white)
        } else {
            closeButton.setBackgroundResource(R.drawable.baseline_close_24)
            addContactButton.setBackgroundResource(R.drawable.add)
            addContactBackButton.setBackgroundResource(R.drawable.ic_arrow_back)
        }
    }

    override fun onDestroy() {
        db.close()

        super.onDestroy()
    }

}