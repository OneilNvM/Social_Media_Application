package com.example.oneilassignment2

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SwitchCompat
import androidx.cardview.widget.CardView
import java.text.SimpleDateFormat
import java.util.Calendar

class SettingsActivity : AppCompatActivity() {

    private var isDarkMode: Boolean = false

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putBoolean("isDarkMode", isDarkMode)

        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        isDarkMode = savedInstanceState.getBoolean("isDarkMode")

        super.onRestoreInstanceState(savedInstanceState)
    }

    @SuppressLint("SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_settings)

//        Set variables for main activity, database, and other views
        val db = SchoolSQLiteDatabase(this)

        val userSession = getSharedPreferences("USER_SESSION", MODE_PRIVATE)
        val userId = userSession.getInt("student_id", 0)

        val darkModeSwitch = findViewById<SwitchCompat>(R.id.settings_toggle_dark_switch)
        val resetPasswordButton = findViewById<Button>(R.id.settings_reset_password_button)
        val deleteAccountButton = findViewById<Button>(R.id.settings_delete_acc_button)
        val closeButton = findViewById<ImageButton>(R.id.settings_close_button)

        val deleteAccountCard = findViewById<CardView>(R.id.settings_delete_acc_card)
        val confirmDelete = findViewById<TextView>(R.id.settings_delete_acc_yes_button)
        val cancelDelete = findViewById<TextView>(R.id.settings_delete_acc_no_button)

        val resetPasswordCard = findViewById<CardView>(R.id.settings_reset_password_card)
        val oldPasswordInput = findViewById<EditText>(R.id.settings_reset_password_old_input)
        val newPasswordInput = findViewById<EditText>(R.id.settings_reset_password_new_input)
        val confirmReset = findViewById<TextView>(R.id.settings_reset_password_confirm_button)
        val cancelReset = findViewById<ImageView>(R.id.settings_reset_password_cancel_button)

        //        Check the current UI configuration and adjust the value of the dark mode switch
        try {
            if (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES) {
                darkModeSwitch.isChecked = true
                isDarkMode = true
                darkModeSwitch.text = getString(R.string.enabled_text)
            }
            if (isDarkMode) {
                darkModeSwitch.isChecked = true
                darkModeSwitch.text = getString(R.string.enabled_text)
            } else {
                darkModeSwitch.isChecked = false
                darkModeSwitch.text = getString(R.string.disabled_text)
            }
        } catch (e: Exception) {
            Log.e("SettingsActivity", "There was an error when checking UI configuration")
        }

        //        Dark mode switch checks what value the dark mode variable is set to and adjusts the UI accordingly
        darkModeSwitch.setOnClickListener {
            try {
                if (!isDarkMode) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    isDarkMode = true

                    this.recreate()

                    Toast.makeText(this, "Dark Mode Enabled", Toast.LENGTH_SHORT).show()
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    isDarkMode = false

                    this.recreate()

                    Toast.makeText(this, "Dark Mode Disabled", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("SettingsFragment", "There was an error when changing UI configuration")
            }
        }

        //        Handles UI based on dark mode
        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            closeButton.setBackgroundResource(R.drawable.baseline_close_24_white)
            cancelReset.setBackgroundResource(R.drawable.baseline_close_24_white)
        } else {
            closeButton.setBackgroundResource(R.drawable.baseline_close_24)
            cancelReset.setBackgroundResource(R.drawable.baseline_close_24)
        }

        resetPasswordButton.setOnClickListener {
            resetPasswordCard.visibility = View.VISIBLE
        }

        //        Confirms the password reset
        confirmReset.setOnClickListener {
            val student = db.retrieveStudent(userId)
            val currentDateTime = Calendar.getInstance().time
            val formattedDate = SimpleDateFormat("dd/MM/yyyy HH:mm").format(currentDateTime)

            try {
                if (student != null) {
                    if (student.count > 0) {
                        student.moveToFirst()

                        val studentPassword =
                            student.getString(student.getColumnIndexOrThrow("password"))

                        val oldPassword = oldPasswordInput.text.toString()
                        val newPassword = newPasswordInput.text.toString()

//                        Checks the conditions before resetting the password
                        if (oldPasswordInput.text.isEmpty()) {
                            oldPasswordInput.error = "Please enter your current password"
                        } else if (newPasswordInput.text.isEmpty()) {
                            newPasswordInput.error = "Please enter your new password"
                        } else if (oldPassword != studentPassword) {
                            oldPasswordInput.error = "This does not match the current password"
                        } else if (newPasswordInput.text.length < 8) {
                            newPasswordInput.error = "Password must be at least 8 characters"
                        } else {
                            db.updateStudent(
                                userId,
                                null,
                                null,
                                null,
                                null,
                                newPassword,
                                formattedDate
                            )
                            Toast.makeText(this, "Password Updated", Toast.LENGTH_SHORT)
                                .show()

                            resetPasswordCard.visibility = View.GONE

                            oldPasswordInput.text.clear()
                            newPasswordInput.text.clear()

//                            Performs operations to log the user out
                            userSession.edit().clear().apply()

                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)

                            finish()
                        }
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(
                    this,
                    "There was an error updating the password",
                    Toast.LENGTH_SHORT
                ).show()
            }
            student?.close()
        }

        cancelReset.setOnClickListener {
            resetPasswordCard.visibility = View.GONE
        }

        deleteAccountButton.setOnClickListener {
            deleteAccountCard.visibility = View.VISIBLE
        }

        //        Confirms the deletion of an account
        confirmDelete.setOnClickListener {
            try {
//                First we handle the number of likes, dislikes, and comments on other posts by the user

//                Handle updating of likes on posts and comments
                val studentLikes = db.retrieveStudentLikes(userId)
                if (studentLikes != null) {
                    if (studentLikes.count > 0) {
                        studentLikes.moveToFirst()

//                  This while loop loops through the likes by the user and handles update
//                  operations on each like
                        while (!studentLikes.isAfterLast) {
//                            Grab the post and comment id of the like
                            val postId =
                                studentLikes.getInt(studentLikes.getColumnIndexOrThrow("post_id"))
                            val commentId =
                                studentLikes.getInt(studentLikes.getColumnIndexOrThrow("comment_id"))

//                            Check if the post id is valid
                            if (postId != 0) {
//                                Retrieve the post from the database
                                val post = db.retrievePost(postId)

                                if (post != null) {
                                    if (post.count > 0) {
                                        post.moveToFirst()

//                                        Grab the number of likes on the post
                                        val numOfLikes =
                                            post.getInt(post.getColumnIndexOrThrow("num_of_likes"))

//                                        Update the number of likes on the post by minus 1
                                        db.updatePost(
                                            postId,
                                            null,
                                            null,
                                            numOfLikes - 1,
                                            null,
                                            null
                                        )
                                    }
                                }
                                post?.close()
                            }

//                            Check if the comment id is valid
                            if (commentId != 0) {
//                                Retrieve the comment from the database
                                val comment = db.retrieveComment(commentId)

                                if (comment != null) {
                                    if (comment.count > 0) {
                                        comment.moveToFirst()

//                                        Grab the number of likes column
                                        val numOfLikes =
                                            comment.getInt(comment.getColumnIndexOrThrow("num_of_likes"))

//                                        Update the number of likes on the comment by minus 1
                                        db.updateComment(
                                            commentId,
                                            null,
                                            null,
                                            numOfLikes - 1,
                                            null
                                        )
                                    }
                                }
                                comment?.close()
                            }
//                            Move to the next like id
                            studentLikes.moveToNext()
                        }
                    }
                }
                studentLikes?.close()

//                Handle updating of dislikes on comments
                val commentDislikes = db.retrieveStudentDislikes(userId)

                if (commentDislikes != null) {
                    if (commentDislikes.count > 0) {
                        commentDislikes.moveToFirst()

//                        While loop loops through each dislike by the user and handles update
//                        operations on each dislike
                        while (!commentDislikes.isAfterLast) {
//                            Grab the comment id
                            val commentId =
                                commentDislikes.getInt(commentDislikes.getColumnIndexOrThrow("comment_id"))

//                            Check if the comment id is valid
                            if (commentId != 0) {
                                val comment = db.retrieveComment(commentId)

                                if (comment != null) {
                                    if (comment.count > 0) {
                                        comment.moveToFirst()

//                                        Grab the number of dislikes on the comment
                                        val numOfDislikes =
                                            comment.getInt(comment.getColumnIndexOrThrow("num_of_dislikes"))

//                                        Update the number of dislikes on the comment by minus 1
                                        db.updateComment(
                                            commentId,
                                            null,
                                            null,
                                            null,
                                            numOfDislikes - 1
                                        )
                                    }
                                }
                                comment?.close()
                            }
                            commentDislikes.moveToNext()
                        }
                    }
                }
                commentDislikes?.close()

//                Handle updating of number of comments on posts
                val comments = db.retrieveStudentComments(userId)

                if (comments != null) {
                    if (comments.count > 0) {
                        comments.moveToFirst()

//                        While loop loops through each comment by the user and updates the number
//                        of comments on posts
                        while (!comments.isAfterLast) {
//                            Grab the post id
                            val postId = comments.getInt(comments.getColumnIndexOrThrow("post_id"))

//                            Check if the post id is valid
                            if (postId != 0) {
                                val post = db.retrievePost(postId)

                                if (post != null) {
                                    if (post.count > 0) {
                                        post.moveToFirst()

//                                        Grab the number of comments on the post
                                        val numOfComments =
                                            post.getInt(post.getColumnIndexOrThrow("num_of_comments"))

//                                        Update the number of comments on the post by minus 1
                                        db.updatePost(
                                            postId,
                                            null,
                                            null,
                                            null,
                                            numOfComments - 1,
                                            null
                                        )
                                    }
                                }
                                post?.close()
                            }
                            comments.moveToNext()
                        }
                    }
                }
                comments?.close()

//                Next we handle the deletion of like and dislike data on the user's posts and comments

//                Handle deletion of likes and dislikes for the user's comments
                val studentComments = db.retrieveStudentComments(userId)

                if (studentComments != null) {
                    if (studentComments.count > 0) {
                        studentComments.moveToFirst()

//                        While loop loops through each comment by the user and handles deletion
//                        of like and dislike Ids
                        while (!studentComments.isAfterLast) {
//                            Grab the comment Id
                            val commentId =
                                studentComments.getInt(studentComments.getColumnIndexOrThrow("comment_id"))

//                            Retrieve the likes for the comment
                            val likes = db.retrieveCommentLikes(commentId)

                            if (likes != null) {
                                if (likes.count > 0) {
                                    likes.moveToFirst()

//                                    Loop through the likes and delete each like
                                    while (!likes.isAfterLast) {
                                        val likeId =
                                            likes.getInt(likes.getColumnIndexOrThrow("like_id"))

                                        db.deleteLike(likeId)

                                        likes.moveToNext()
                                    }
                                }
                            }
                            likes?.close()

//                            Retrieve the dislikes for the comment
                            val dislikes = db.retrieveCommentDislikes(commentId)

                            if (dislikes != null) {
                                if (dislikes.count > 0) {
                                    dislikes.moveToFirst()

//                                    Loop through the dislikes and delete each dislike
                                    while (!dislikes.isAfterLast) {
                                        val dislikeId =
                                            dislikes.getInt(dislikes.getColumnIndexOrThrow("dislike_id"))

                                        db.deleteDislike(dislikeId)

                                        dislikes.moveToNext()
                                    }
                                }
                            }
                            dislikes?.close()

                            studentComments.moveToNext()
                        }
                    }
                }
                studentComments?.close()

//                Handle deletion of likes for the user's posts
                val studentPosts = db.retrieveStudentPosts(userId)

                if (studentPosts != null) {
                    if (studentPosts.count > 0) {
                        studentPosts.moveToFirst()

//                        While loop loops through each post by the user and handles deletion
//                        of like Ids
                        while (!studentPosts.isAfterLast) {
//                            Grab the post Id
                            val postId =
                                studentPosts.getInt(studentPosts.getColumnIndexOrThrow("post_id"))

//                            Retrieve the likes for the post
                            val likes = db.retrievePostLikes(postId)

                            if (likes != null) {
                                if (likes.count > 0) {
                                    likes.moveToFirst()

//                                    Loop through the likes and delete each like
                                    while (!likes.isAfterLast) {
                                        val likeId =
                                            likes.getInt(likes.getColumnIndexOrThrow("like_id"))

                                        db.deleteLike(likeId)

                                        likes.moveToNext()
                                    }
                                }
                            }
                            likes?.close()

                            studentPosts.moveToNext()
                        }
                    }
                }
                studentPosts?.close()

//                Performs final deletes after referential data has been updated and/ or deleted
                db.deleteStudent(userId)
                db.deleteStudentPosts(userId)
                db.deleteStudentComments(userId)
                db.deleteStudentLikes(userId)
                db.deleteStudentDislikes(userId)

                deleteAccountCard.visibility = View.GONE

//                Final operations to log the user out
                userSession.edit().clear().apply()

                Toast.makeText(this, "Account Deleted", Toast.LENGTH_SHORT).show()

                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)

                finish()
            } catch (e: Exception) {
                Toast.makeText(
                    this,
                    "There was an error deleting the account",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        cancelDelete.setOnClickListener {
            deleteAccountCard.visibility = View.GONE
        }

        closeButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)

            finish()
        }

        db.close()
    }
}