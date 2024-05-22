package com.example.oneilassignment2

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate

class DeleteStudentFragment : Fragment() {
    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_delete_student, container, false)
        //        Touch event set to nothing to prevent interaction with background views whilst this fragment is visible
        view.setOnTouchListener { _, _ -> return@setOnTouchListener true }

//        Sets variables for the main activity, database and other views
        val mainActivity = requireActivity() as MainActivity
        val db = SchoolSQLiteDatabase(mainActivity)

        val studentIdInput = view.findViewById<EditText>(R.id.admin_delete_student_id)
        val deleteButton = view.findViewById<Button>(R.id.admin_delete_student_delete_button)
        val backButton = view.findViewById<ImageButton>(R.id.admin_delete_student_back_button)

//        Handle UI changes for dark mode
        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            backButton.setBackgroundResource(R.drawable.baseline_arrow_back_24_white)
        } else {
            backButton.setBackgroundResource(R.drawable.ic_arrow_back)
        }

        /*        Delete button searches through the database to delete the relevant rows and update the
        relevant tables which references the student
         */
        deleteButton.setOnClickListener {
            try {
                val studentLikes = db.retrieveStudentLikes(studentIdInput.text.toString().toInt())
                if (studentLikes != null) {
                    if (studentLikes.count > 0) {
                        studentLikes.moveToFirst()

                        while (!studentLikes.isAfterLast) {
                            val postId =
                                studentLikes.getInt(studentLikes.getColumnIndexOrThrow("post_id"))
                            val commentId =
                                studentLikes.getInt(studentLikes.getColumnIndexOrThrow("comment_id"))

                            if (postId != 0) {
                                val post = db.retrievePost(postId)

                                if (post != null) {
                                    if (post.count > 0) {
                                        post.moveToFirst()

                                        while (!post.isAfterLast) {
                                            val numOfLikes =
                                                post.getInt(post.getColumnIndexOrThrow("num_of_likes"))

                                            db.updatePost(
                                                postId,
                                                null,
                                                null,
                                                numOfLikes - 1,
                                                null,
                                                null
                                            )
                                            post.moveToNext()
                                        }
                                    }
                                }
                                post?.close()

                                val likes = db.retrievePostLikes(postId)

                                if (likes != null) {
                                    if (likes.count > 0) {
                                        likes.moveToFirst()

                                        while (!likes.isAfterLast) {
                                            val likeId =
                                                likes.getInt(likes.getColumnIndexOrThrow("like_id"))

                                            db.deleteLike(likeId)
                                            likes.moveToNext()
                                        }
                                    }
                                }

                                likes?.close()
                            }
                            if (commentId != 0) {
                                val comment = db.retrieveComments(commentId)

                                if (comment != null) {
                                    if (comment.count > 0) {
                                        comment.moveToFirst()

                                        while (!comment.isAfterLast) {
                                            val numOfLikes =
                                                comment.getInt(comment.getColumnIndexOrThrow("num_of_likes"))

                                            db.updateComment(
                                                commentId,
                                                null,
                                                null,
                                                numOfLikes - 1,
                                                null
                                            )
                                            comment.moveToNext()
                                        }
                                    }
                                }

                                comment?.close()

                                val likes = db.retrieveCommentLikes(commentId)

                                if (likes != null) {
                                    if (likes.count > 0) {
                                        likes.moveToFirst()

                                        while (!likes.isAfterLast) {
                                            val likeId =
                                                likes.getInt(likes.getColumnIndexOrThrow("like_id"))

                                            db.deleteLike(likeId)
                                            likes.moveToNext()
                                        }
                                    }
                                }

                                likes?.close()
                            }
                            studentLikes.moveToNext()
                        }
                    }
                }
                studentLikes?.close()

                val commentDislikes =
                    db.retrieveStudentDislikes(studentIdInput.text.toString().toInt())

                if (commentDislikes != null) {
                    if (commentDislikes.count > 0) {
                        commentDislikes.moveToFirst()

                        while (!commentDislikes.isAfterLast) {
                            val commentId =
                                commentDislikes.getInt(commentDislikes.getColumnIndexOrThrow("comment_id"))

                            if (commentId != 0) {
                                val comment = db.retrieveComments(commentId)

                                if (comment != null) {
                                    if (comment.count > 0) {
                                        comment.moveToFirst()

                                        while (!comment.isAfterLast) {
                                            val numOfDislikes =
                                                comment.getInt(comment.getColumnIndexOrThrow("num_of_dislikes"))

                                            db.updateComment(
                                                commentId,
                                                null,
                                                null,
                                                null,
                                                numOfDislikes - 1
                                            )
                                            comment.moveToNext()
                                        }
                                    }
                                }
                                comment?.close()

                                val dislikes = db.retrieveCommentDislikes(commentId)

                                if (dislikes != null) {
                                    if (dislikes.count > 0) {
                                        dislikes.moveToFirst()

                                        while (!dislikes.isAfterLast) {
                                            val dislikeId =
                                                dislikes.getInt(dislikes.getColumnIndexOrThrow("dislike_id"))

                                            db.deleteDislike(dislikeId)
                                            dislikes.moveToNext()
                                        }
                                    }
                                }
                                dislikes?.close()
                            }
                            commentDislikes.moveToNext()
                        }
                    }
                }
                commentDislikes?.close()

                val comments = db.retrieveStudentComments(studentIdInput.text.toString().toInt())

                if (comments != null) {
                    if (comments.count > 0) {
                        comments.moveToFirst()

                        while (!comments.isAfterLast) {
                            val postId = comments.getInt(comments.getColumnIndexOrThrow("post_id"))

                            if (postId != 0) {
                                val post = db.retrievePost(postId)

                                if (post != null) {
                                    if (post.count > 0) {
                                        post.moveToFirst()

                                        while (!post.isAfterLast) {
                                            val numOfComments =
                                                post.getInt(post.getColumnIndexOrThrow("num_of_comments"))

                                            db.updatePost(
                                                postId,
                                                null,
                                                null,
                                                null,
                                                numOfComments - 1,
                                                null
                                            )
                                            post.moveToNext()
                                        }
                                    }
                                }
                                post?.close()
                            }
                            comments.moveToNext()
                        }
                    }
                }
                comments?.close()


                db.deleteStudent(studentIdInput.text.toString().toInt())
                db.deleteStudentPosts(studentIdInput.text.toString().toInt())
                db.deleteStudentComments(studentIdInput.text.toString().toInt())
                db.deleteStudentLikes(studentIdInput.text.toString().toInt())
                db.deleteStudentDislikes(studentIdInput.text.toString().toInt())

                Toast.makeText(mainActivity, "Account Deleted", Toast.LENGTH_SHORT).show()

                parentFragmentManager.popBackStack()

            } catch (e: Exception) {
                Log.e(TAG, "Error: ${e.message}")
                Toast.makeText(
                    mainActivity,
                    "There was an error deleting the account",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        db.close()

        backButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
        return view
    }


}