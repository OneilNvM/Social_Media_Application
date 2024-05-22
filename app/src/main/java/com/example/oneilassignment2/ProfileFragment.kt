package com.example.oneilassignment2

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.ContentValues
import android.content.Context.MODE_PRIVATE
import android.database.Cursor
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
import androidx.cardview.widget.CardView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import java.text.SimpleDateFormat
import java.util.Calendar

class ProfileFragment : Fragment(), ProfileRecyclerViewInterface {

    //    Create lateinit variables for the list of posts, the adapter, and the view model
    private lateinit var listOfPosts: ArrayList<PostData>
    private lateinit var rcvAdapter: ProfileRecyclerViewAdapter
    private lateinit var sharedViewModel: PostDataViewModel

    @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

//        Set teh variables for the main activity, database and other views
        val mainActivity = requireActivity() as MainActivity
        val db = SchoolSQLiteDatabase(mainActivity)
        sharedViewModel = ViewModelProvider(mainActivity)[PostDataViewModel::class.java]

        val userSession = mainActivity.getSharedPreferences("USER_SESSION", MODE_PRIVATE)
        val userId = userSession.getInt("student_id", 0)
        val userName = userSession.getString("first_name", "")

//        Grab the first name of the student from the shared preference
        val studentName = view.findViewById<TextView>(R.id.profile_student_first_name)
        val joinDate = view.findViewById<TextView>(R.id.profile_student_join_date)
        val profileRecyclerView = view.findViewById<RecyclerView>(R.id.profile_posts_recycler_view)
        val swipeRefreshLayout =
            view.findViewById<SwipeRefreshLayout>(R.id.profile_post_swipe_refresh_layout)

        val profileEditButton = view.findViewById<Button>(R.id.edit_profile_button)
        val profileEditCard = view.findViewById<CardView>(R.id.profile_edit_card)
        val profileEditBackButton = view.findViewById<ImageButton>(R.id.profile_edit_back_button)
        val profileEditSaveButton = view.findViewById<Button>(R.id.profile_edit_update_button)
        val profileEditFirstNameInput = view.findViewById<EditText>(R.id.profile_edit_firstname_input)
        val profileEditSurnameInput = view.findViewById<EditText>(R.id.profile_edit_surname_input)
        val profileEditEmailInput = view.findViewById<EditText>(R.id.profile_edit_email_input)
        val profileEditDateOfBirthButton = view.findViewById<Button>(R.id.profile_edit_dob_button)
        val profileEditDateOfBirthText = view.findViewById<TextView>(R.id.profile_edit_dob_text)

//        Handle UI changes based on dark mode
        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            profileEditBackButton.setBackgroundResource(R.drawable.baseline_arrow_back_24_white)
        } else {
            profileEditBackButton.setBackgroundResource(R.drawable.ic_arrow_back)
        }

        profileEditButton.setOnClickListener {
            profileEditCard.visibility = View.VISIBLE
        }

        profileEditBackButton.setOnClickListener {
            profileEditCard.visibility = View.GONE
        }

        //        Create date picker dialog when date of birth button is clicked
        profileEditDateOfBirthButton.setOnClickListener {
            val calendar = Calendar.getInstance()

            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(
                mainActivity,
                { _, selectedYear, selectedMonth, selectedDay ->
                    profileEditDateOfBirthText.text =
                        (selectedDay.toString() + "/" + (selectedMonth + 1) + "/" + selectedYear)
                }, year, month, day
            )

            datePickerDialog.datePicker.maxDate = calendar.timeInMillis

            datePickerDialog.show()
        }

        profileEditSaveButton.setOnClickListener {
            val currentDateTime = Calendar.getInstance().time
            val formattedDate = SimpleDateFormat("dd/MM/yyyy HH:mm").format(currentDateTime)
            var firstName: String? = profileEditFirstNameInput.text.toString()
            var surname: String? = profileEditSurnameInput.text.toString()
            var email: String? = profileEditEmailInput.text.toString()
            var dateOfBirth: String? = profileEditDateOfBirthText.text.toString()
            //            Check the conditions before updating the accounts info

            if (firstName?.isEmpty() == true && surname?.isEmpty() == true && email?.isEmpty() == true && dateOfBirth == getString(
                    R.string.date_of_birth_text
                )
            ) {
                Toast.makeText(mainActivity, "Please fill at least one field", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }
            if (firstName?.isEmpty() == true) {
                firstName = null
            }
            if (surname?.isEmpty() == true) {
                surname = null
            }
            if (email?.isEmpty() == true) {
                email = null
            } else {
                if (!email!!.endsWith("@school.co.uk")) {
                    profileEditEmailInput.error = "Email must end with '@school.co.uk'"
                    return@setOnClickListener
                }
            }
            if (dateOfBirth == getString(R.string.date_of_birth_text)) {
                dateOfBirth = null
            }

            try {

                db.updateStudent(
                    userId,
                    firstName,
                    surname,
                    email,
                    dateOfBirth,
                    null,
                    formattedDate
                )

                profileEditCard.visibility = View.GONE

                if (firstName != null) {
                    userSession.edit().putString("first_name", firstName).apply()

                    db.updateStudentPosts(userId, firstName, null)
                    db.updateStudentComments(userId, firstName, null)
                }

                profileEditFirstNameInput.text.clear()
                profileEditSurnameInput.text.clear()
                profileEditEmailInput.text.clear()
                profileEditDateOfBirthText.text = getString(R.string.date_of_birth_text)

                Toast.makeText(
                    mainActivity,
                    "Student info updated successfully",
                    Toast.LENGTH_SHORT
                ).show()
            } catch (e: Exception) {
                Toast.makeText(mainActivity, "Failed to update student info", Toast.LENGTH_SHORT)
                    .show()
            }
        }
//        Set the name of the student on the profile to the shared preference name
        studentName.text = userName

        val student = db.retrieveStudent(userId)

//        Set the join date field to the date the account was created
        try {
            if (student != null) {
                if (student.count > 0) {
                    student.moveToFirst()

                    val dateCreated =
                        student.getString(student.getColumnIndexOrThrow("date_created"))

                    joinDate.text = "Joined on $dateCreated"
                }
            }
        } catch (e: Exception) {
            Log.e(ContentValues.TAG, "Cursor error: ${e.message}")
        }
        student?.close()

//        Setup the swipe refresh functionality for the recycler view
        swipeRefreshLayout.setOnRefreshListener {
            listOfPosts.clear()
            addPostsToList()
            rcvAdapter.notifyDataSetChanged()
            swipeRefreshLayout.isRefreshing = false
        }

//        Setup the recycler view
        profileRecyclerView.itemAnimator = null

        listOfPosts = ArrayList()

        addPostsToList()

        profileRecyclerView.layoutManager = LinearLayoutManager(mainActivity)

        rcvAdapter = ProfileRecyclerViewAdapter(listOfPosts, this)

        profileRecyclerView.adapter = rcvAdapter

        db.close()
        return view
    }

    //    Function to add posts to the listOfPosts variable
    private fun addPostsToList() {
        val db = SchoolSQLiteDatabase(requireActivity())
        val userSession = requireActivity().getSharedPreferences("USER_SESSION", MODE_PRIVATE)
        val userId = userSession.getInt("student_id", 0)
        val cursor: Cursor? = db.retrieveStudentPosts(userId)

        try {
            if (cursor != null) {
                if (cursor.count > 0) {
                    cursor.moveToFirst()
                    while (!cursor.isAfterLast) {
                        val postId = cursor.getInt(cursor.getColumnIndexOrThrow("post_id"))
                        val posterId = cursor.getInt(cursor.getColumnIndexOrThrow("student_id"))
                        val posterName =
                            cursor.getString(cursor.getColumnIndexOrThrow("poster_name"))
                        val postCaption = cursor.getString(cursor.getColumnIndexOrThrow("caption"))
                        val postDate = cursor.getString(cursor.getColumnIndexOrThrow("date"))
                        val numLikes = cursor.getInt(cursor.getColumnIndexOrThrow("num_of_likes"))
                        val numComments =
                            cursor.getInt(cursor.getColumnIndexOrThrow("num_of_comments"))

                        val isLikedCursor = db.retrieveLike(postId, userId)
                        var isLiked = false

                        if (isLikedCursor != null) {
                            isLiked = isLikedCursor.count > 0
                        }

                        isLikedCursor?.close()

                        val post = PostData(
                            postId,
                            posterId,
                            posterName,
                            postCaption,
                            numLikes,
                            numComments,
                            postDate,
                            isLiked
                        )

                        listOfPosts.add(post)

                        cursor.moveToNext()
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(ContentValues.TAG, "Error trying to add posts to list: ${e.message}")
        }
        cursor?.close()
        db.close()
    }

    //    Interface function handles click events on the like button on the posts
    override fun onLikeButtonClicked(post: PostData, position: Int) {
        val mainActivity = requireActivity() as MainActivity
        val userSession = mainActivity.getSharedPreferences("USER_SESSION", MODE_PRIVATE)
        val userId = userSession.getInt("student_id", 0)
        val db = SchoolSQLiteDatabase(requireActivity())
        val dbWrite = SchoolSQLiteDatabase(requireActivity()).writableDatabase
        var cursor: Cursor?

        val query = "SELECT * FROM posts WHERE student_id = ? AND post_id = ?"

        cursor = dbWrite.rawQuery(query, arrayOf(post.posterId.toString(), post.postId.toString()))
        try {
            if (cursor != null) {
                if (cursor.count > 0) {
                    var exactPost = PostData(0, 0, "", "", 0, 0, "")

                    cursor.moveToFirst()

                    while (!cursor.isAfterLast) {
                        val postId = cursor.getInt(cursor.getColumnIndexOrThrow("post_id"))
                        val posterId = cursor.getInt(cursor.getColumnIndexOrThrow("student_id"))
                        val posterName =
                            cursor.getString(cursor.getColumnIndexOrThrow("poster_name"))
                        val postCaption = cursor.getString(cursor.getColumnIndexOrThrow("caption"))
                        val postDate = cursor.getString(cursor.getColumnIndexOrThrow("date"))
                        val numLikes = cursor.getInt(cursor.getColumnIndexOrThrow("num_of_likes"))
                        val numComments =
                            cursor.getInt(cursor.getColumnIndexOrThrow("num_of_comments"))

                        exactPost = PostData(
                            postId,
                            posterId,
                            posterName,
                            postCaption,
                            numLikes,
                            numComments,
                            postDate
                        )

                        cursor.moveToNext()
                    }

                    cursor = dbWrite.rawQuery(
                        "SELECT * FROM likes WHERE student_id = ? AND post_id = ?",
                        arrayOf(userId.toString(), exactPost.postId.toString())
                    )

                    if (cursor != null) {
                        if (cursor.count > 0) {
                            db.updatePost(
                                exactPost.postId,
                                null,
                                null,
                                exactPost.numOfLikes - 1,
                                null,
                                null
                            )
                            db.deletePostLike(userId, exactPost.postId)

                            cursor = db.retrievePost(exactPost.postId)

                            if (cursor != null) {
                                if (cursor.count > 0) {
                                    cursor.moveToFirst()
                                    var newestPost = PostData(0, 0, "", "", 0, 0, "")

                                    while (!cursor.isAfterLast) {
                                        val postId =
                                            cursor.getInt(cursor.getColumnIndexOrThrow("post_id"))
                                        val posterId =
                                            cursor.getInt(cursor.getColumnIndexOrThrow("student_id"))
                                        val posterName =
                                            cursor.getString(cursor.getColumnIndexOrThrow("poster_name"))
                                        val postCaption =
                                            cursor.getString(cursor.getColumnIndexOrThrow("caption"))
                                        val postDate =
                                            cursor.getString(cursor.getColumnIndexOrThrow("date"))
                                        val numLikes =
                                            cursor.getInt(cursor.getColumnIndexOrThrow("num_of_likes"))
                                        val numComments =
                                            cursor.getInt(cursor.getColumnIndexOrThrow("num_of_comments"))

                                        newestPost = PostData(
                                            postId,
                                            posterId,
                                            posterName,
                                            postCaption,
                                            numLikes,
                                            numComments,
                                            postDate
                                        )

                                        cursor.moveToNext()
                                    }
                                    val arrayOfPost = ArrayList<PostData>()
                                    arrayOfPost.add(newestPost)

                                    rcvAdapter.updateItems(arrayOfPost, position)
                                }
                            }
                        } else {
                            db.updatePost(
                                exactPost.postId,
                                null,
                                null,
                                exactPost.numOfLikes + 1,
                                null,
                                null
                            )
                            db.insertPostLike(userId, exactPost.postId)

                            cursor = db.retrievePost(exactPost.postId)

                            if (cursor != null) {
                                if (cursor.count > 0) {
                                    cursor.moveToFirst()
                                    var newestPost = PostData(0, 0, "", "", 0, 0, "")

                                    while (!cursor.isAfterLast) {
                                        val postId =
                                            cursor.getInt(cursor.getColumnIndexOrThrow("post_id"))
                                        val posterId =
                                            cursor.getInt(cursor.getColumnIndexOrThrow("student_id"))
                                        val posterName =
                                            cursor.getString(cursor.getColumnIndexOrThrow("poster_name"))
                                        val postCaption =
                                            cursor.getString(cursor.getColumnIndexOrThrow("caption"))
                                        val postDate =
                                            cursor.getString(cursor.getColumnIndexOrThrow("date"))
                                        val numLikes =
                                            cursor.getInt(cursor.getColumnIndexOrThrow("num_of_likes"))
                                        val numComments =
                                            cursor.getInt(cursor.getColumnIndexOrThrow("num_of_comments"))

                                        newestPost = PostData(
                                            postId,
                                            posterId,
                                            posterName,
                                            postCaption,
                                            numLikes,
                                            numComments,
                                            postDate,
                                            true
                                        )

                                        cursor.moveToNext()
                                    }
                                    val arrayOfPost = ArrayList<PostData>()
                                    arrayOfPost.add(newestPost)

                                    rcvAdapter.updateItems(arrayOfPost, position)
                                }
                            }
                        }
                    }
                    cursor?.close()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        cursor?.close()
        dbWrite.close()

        db.close()
    }

    //    Interface function handles click events on the comment button on the posts
    override fun onCommentButtonClicked(post: PostData, position: Int) {
        sharedViewModel.post = post

        parentFragmentManager.beginTransaction()
            .add(R.id.comments_fragment_container, CommentsFragment())
            .addToBackStack(null)
            .commit()
    }

    //    Interface function handles click events on the edit post button on the posts
    override fun onEditButtonClicked(post: PostData, position: Int) {
        sharedViewModel.post = post

        parentFragmentManager.beginTransaction()
            .add(R.id.full_frame_fragment_container, EditPostFragment())
            .addToBackStack(null)
            .commit()
    }
}