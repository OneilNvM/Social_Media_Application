package com.example.oneilassignment2

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.app.NotificationChannel
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.database.Cursor
import android.graphics.Color
import android.os.Build
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
import androidx.core.app.NotificationCompat
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Calendar

// Create a constant value for the channel id
private const val CHANNEL_ID = "post_channel"

class PostFragment : Fragment(), HomePostsRecyclerViewInterface{

//    Create a lateinit variable to store the list of posts
    private lateinit var listOfPosts: ArrayList<PostData>

    @SuppressLint("SimpleDateFormat")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_post, container, false)

//        Set variables for main activity, database, and other views
        val mainActivity = requireActivity() as MainActivity
        val homeFragment = inflater.inflate(R.layout.fragment_home, container, false)
        val db = SchoolSQLiteDatabase(mainActivity)

        listOfPosts = ArrayList()

        val userSession = mainActivity.getSharedPreferences("USER_SESSION", Context.MODE_PRIVATE)
        val userId = userSession.getInt("student_id", 0)
        val posterName = userSession.getString("first_name", "")

        val captionInput = view.findViewById<EditText>(R.id.post_caption_input)
        val postButton = view.findViewById<Button>(R.id.post_create_post_button)
        val mainPostButton = mainActivity.findViewById<ImageButton>(R.id.home_add_button)
        val homeButton = mainActivity.findViewById<ImageButton>(R.id.h_home_button)
        val homeTitle = mainActivity.findViewById<TextView>(R.id.home_text_view)

        postButton.setOnClickListener {
            val caption = captionInput.text.toString()
            val currentDateTime = Calendar.getInstance().time
            val formattedDate = SimpleDateFormat("dd/MM/yyyy HH:mm").format(currentDateTime)

//            Insert post into the database and update the RecyclerView with the new post
            try {
                if (caption.isNotEmpty()) {
                    db.insertPost(posterName, caption, formattedDate, userId)

                    listOfPosts.clear()

                    addPostsToList()

                    val adapter = HomePostsRecyclerViewAdapter(listOfPosts, this)
                    val recyclerView =
                        homeFragment.findViewById<RecyclerView>(R.id.home_recycler_view_container)

                    recyclerView.adapter = adapter

                    sendNotification(posterName!!)

                    captionInput.text.clear()

                    mainActivity.supportFragmentManager.popBackStack("HOME_FRAGMENT", 0)

                    homeTitle.text = getString(R.string.home_text)

                    homeButton.setBackgroundResource(R.drawable.house_1_)

                    if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_NO) {
                        mainPostButton.setBackgroundResource(R.drawable.add)
                    } else {
                        mainPostButton.setBackgroundResource(R.drawable.add__1_)
                    }
                } else {
                    captionInput.error = "Caption is required"
                }
            } catch (e: Exception) {
                Log.e("PostFragment", "Error inserting post: ${e.message}")
                Toast.makeText(mainActivity, "Error occurred when attempting to post", Toast.LENGTH_SHORT).show()
            }
        }

        db.close()

        return view
    }

//    Function to add post to the list
    private fun addPostsToList() {
        val db = SchoolSQLiteDatabase(requireActivity())
        val cursor: Cursor? = db.retrieveAllPosts()

        try {
            if (cursor != null) {
                cursor.moveToFirst()
                while (!cursor.isAfterLast) {
                    val postId = cursor.getInt(cursor.getColumnIndexOrThrow("post_id"))
                    val posterId = cursor.getInt(cursor.getColumnIndexOrThrow("student_id"))
                    val posterName = cursor.getString(cursor.getColumnIndexOrThrow("poster_name"))
                    val postCaption = cursor.getString(cursor.getColumnIndexOrThrow("caption"))
                    val postDate = cursor.getString(cursor.getColumnIndexOrThrow("date"))
                    val numLikes = cursor.getInt(cursor.getColumnIndexOrThrow("num_of_likes"))
                    val numComments = cursor.getInt(cursor.getColumnIndexOrThrow("num_of_comments"))

                    val post = PostData(
                        postId,
                        posterId,
                        posterName,
                        postCaption,
                        numLikes,
                        numComments,
                        postDate
                    )

                    listOfPosts.add(post)

                    cursor.moveToNext()
                }
            }
        } catch (e: Exception) {
            Log.e("PostFragment", "Error adding posts to list: ${e.message}")
        }
        cursor?.close()
    }

//    Function which builds the notification and sends it to the user
    private fun sendNotification(name: String) {
        val mainActivity = requireActivity() as MainActivity
        val notificationManager = mainActivity.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val builder = NotificationCompat.Builder(mainActivity, CHANNEL_ID)

        val title = "School App"
        val description = "New Post by $name"

        builder.setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(description)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        createNotificationChannel()

        notificationManager.notify(0, builder.build())

    }

    @SuppressLint("ObsoleteSdkInt")
    private fun createNotificationChannel() {
        val mainActivity = requireActivity() as MainActivity
        val notificationManager = mainActivity.getSystemService(NOTIFICATION_SERVICE) as NotificationManager

//        Check the version before creating the notification channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = notificationManager.getNotificationChannel(CHANNEL_ID)
            if (notificationChannel == null) {
                val channel = NotificationChannel(
                    CHANNEL_ID,
                    "Post Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
                )
                channel.lightColor = Color.GREEN
                notificationManager.createNotificationChannel(channel)
            }
        }
    }

    override fun onLikeButtonClicked(post: PostData, position: Int) {
//        No implementation needed
    }

    override fun onCommentButtonClicked(post: PostData, position: Int) {
//        No implementation needed
    }
}