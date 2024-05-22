package com.example.oneilassignment2

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

class CommentsFragment : Fragment(), CommentsRecyclerViewInterface {

//    Create lateinit variables for the RecyclerView Adapter and the ArrayList of Comments
    private lateinit var rcvAdapter: CommentsRecyclerViewAdapter
    private lateinit var arrayOfComments: ArrayList<CommentData>
//    Retrieve the post ViewModel
    private val sharedViewModel: PostDataViewModel by activityViewModels()

    @SuppressLint("SetTextI18n", "SimpleDateFormat", "NotifyDataSetChanged",
        "ClickableViewAccessibility"
    )
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_comments, container, false)

        //        Touch event set to nothing to prevent interaction with background views whilst this fragment is visible
        view.setOnTouchListener { _, _ ->  return@setOnTouchListener true}

//        Set variables for main activity, database and other views
        val mainActivity = requireActivity() as MainActivity
        val db = SchoolSQLiteDatabase(mainActivity)
        val userSession = mainActivity.getSharedPreferences("USER_SESSION", MODE_PRIVATE)

        val recyclerView = view.findViewById<RecyclerView>(R.id.comments_recycler_view)
        val commentsTitle = view.findViewById<TextView>(R.id.comments_title)
        val commentInput = view.findViewById<EditText>(R.id.comments_comment_input)
        val sendButton = view.findViewById<TextView>(R.id.comments_send_button)
        val backButton = view.findViewById<ImageButton>(R.id.comments_back_button)

//        Handle UI changes based on dark mode
        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            backButton.setBackgroundResource(R.drawable.baseline_arrow_back_24_white)
        } else {
            backButton.setBackgroundResource(R.drawable.ic_arrow_back)
        }

//        Prevent refresh animation on RecyclerView
        recyclerView.itemAnimator = null

//        Store the data from the ViewModel
        val postData = sharedViewModel
        val postId = postData.post?.postId
        val numOfComments = postData.post?.numOfComments


//        Set the title to the correct number of comments
        when (postData.post?.numOfComments) {
            0 -> {
                commentsTitle.text = "No Comments"
            }

            1 -> {
                commentsTitle.text = "$numOfComments comment"
            }

            else -> {
                commentsTitle.text = "$numOfComments comments"
            }
        }

        backButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        arrayOfComments = ArrayList()

//        Add the comments to the list if postId is not null
        if (postId != null) {
            addCommentsToList(postId)
        } else {
            Log.d(TAG, "onCreateView: postId is null")
        }

        recyclerView.layoutManager = LinearLayoutManager(mainActivity)

//        Add the recycler view adapter to the RecyclerView
        rcvAdapter = CommentsRecyclerViewAdapter(arrayOfComments, this)
        recyclerView.adapter = rcvAdapter

        sendButton.setOnClickListener {
//            Check if the comment input is empty
            if (commentInput.text.isEmpty()) {
                commentInput.error = "Please enter a comment"
            } else {
//                Check if the post id and number of comments are not null
                if (postId != null && numOfComments != null) {
                    try {
                        val commenterId = userSession.getInt("student_id", 0)
                        val commenterName = userSession.getString("first_name", "")
                        val commentText = commentInput.text.toString()
                        val date = Calendar.getInstance().time
                        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm").format(date)

//                        Insert the comment into the database
                        db.insertComment(
                            commenterName!!,
                            commentText,
                            dateFormat,
                            commenterId,
                            postId
                        )
//                        Update the post with an extra comment
                        db.updatePost(postId, null, null, null, numOfComments.plus(1), null)

//                        Update the RecyclerView with a new comment
                        arrayOfComments.clear()

                        addCommentsToList(postId)

                        if (numOfComments.plus(1) == 1) {
                            commentsTitle.text = "${numOfComments + 1} comment"
                        } else {
                            commentsTitle.text = "${numOfComments + 1} comments"
                        }

                        rcvAdapter.notifyDataSetChanged()

                        commentInput.text.clear()

                        Toast.makeText(mainActivity, "Comment added", Toast.LENGTH_SHORT).show()
                    } catch (e: Exception) {
                        Toast.makeText(mainActivity, "Error trying to send comment", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Log.d(TAG, "onCreateView: postId or numOfComments is null")
                }
            }
        }

        db.close()

        return view
    }

//    Function retrieves all the comments for the post and adds the comments to the list
    private fun addCommentsToList(postId: Int) {
        val db = SchoolSQLiteDatabase(requireActivity())
        val cursor = db.retrieveCommentsOrderedByCommentId(postId)
        val userSession = requireActivity().getSharedPreferences("USER_SESSION", MODE_PRIVATE)
        val userId = userSession.getInt("student_id", 0)

        try {
            if (cursor != null) {
                if (cursor.count > 0) {
                    cursor.moveToFirst()

                    while (!cursor.isAfterLast) {
                        val commentId = cursor.getInt(cursor.getColumnIndexOrThrow("comment_id"))
                        val commenterName =
                            cursor.getString(cursor.getColumnIndexOrThrow("commenter_name"))
                        val commentText = cursor.getString(cursor.getColumnIndexOrThrow("text"))
                        val numOfLikes = cursor.getInt(cursor.getColumnIndexOrThrow("num_of_likes"))
                        val numOfDislikes =
                            cursor.getInt(cursor.getColumnIndexOrThrow("num_of_dislikes"))
                        val commentDate = cursor.getString(cursor.getColumnIndexOrThrow("date"))

                        val isLikedCursor = db.retrieveCommentLike(commentId, userId)
                        var isLiked = false

                        if (isLikedCursor != null) {
                            isLiked = isLikedCursor.count > 0
                        }

                        isLikedCursor?.close()

                        val isDislikedCursor = db.retrieveCommentDislike(commentId, userId)
                        var isDisliked = false

                        if (isDislikedCursor != null) {
                            isDisliked = isDislikedCursor.count > 0
                        }

                        isDislikedCursor?.close()

                        val commentData = CommentData(
                            commentId,
                            commenterName,
                            commentText,
                            numOfLikes,
                            numOfDislikes,
                            commentDate,
                            isLiked,
                            isDisliked
                        )

                        arrayOfComments.add(commentData)

                        cursor.moveToNext()
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error: ${e.message}")
            Toast.makeText(requireActivity(), "Error trying to retrieve comments", Toast.LENGTH_SHORT).show()
        }
        cursor?.close()
        db.close()
    }

//    Interface function which checks if the comment has been liked and updates the RecyclerView with the new data
    override fun onLikeButtonClicked(comment: CommentData, position: Int) {
        val db = SchoolSQLiteDatabase(requireActivity())
        val userSession = requireActivity().getSharedPreferences("USER_SESSION", MODE_PRIVATE)
        val studentId = userSession.getInt("student_id", 0)
        val commentId = comment.commentId

        val cursor = db.retrieveCommentLike(commentId, studentId)

        try {
            if (cursor != null) {
                if (cursor.count > 0) {
                    cursor.moveToFirst()
                    val likeId = cursor.getInt(cursor.getColumnIndexOrThrow("like_id"))

                    val commentData: CommentData
                    if (comment.isDisliked) {
                        commentData = CommentData(
                            comment.commentId,
                            comment.commenterName,
                            comment.commentText,
                            comment.likes.minus(1),
                            comment.dislikes,
                            comment.commentDate,
                            isLiked = false,
                            isDisliked = true
                        )
                    } else {
                        commentData = CommentData(
                            comment.commentId,
                            comment.commenterName,
                            comment.commentText,
                            comment.likes.minus(1),
                            comment.dislikes,
                            comment.commentDate,
                            isLiked = false,
                            isDisliked = false
                        )
                    }

                    val newArrayOfComments = ArrayList<CommentData>()

                    newArrayOfComments.add(commentData)

                    db.deleteLike(likeId)
                    db.updateComment(commentId, null, null, comment.likes.minus(1), null)

                    rcvAdapter.updateItems(newArrayOfComments, position)
                } else {
                    val commentData: CommentData
                    if (comment.isDisliked) {
                        commentData = CommentData(
                            comment.commentId,
                            comment.commenterName,
                            comment.commentText,
                            comment.likes.plus(1),
                            comment.dislikes,
                            comment.commentDate,
                            isLiked = true,
                            isDisliked = true
                        )
                    } else {
                        commentData = CommentData(
                            comment.commentId,
                            comment.commenterName,
                            comment.commentText,
                            comment.likes.plus(1),
                            comment.dislikes,
                            comment.commentDate,
                            isLiked = true,
                        )
                    }

                    val newArrayOfComments = ArrayList<CommentData>()

                    newArrayOfComments.add(commentData)

                    db.insertCommentLike(studentId, commentId)
                    db.updateComment(commentId, null, null, comment.likes.plus(1), null)

                    rcvAdapter.updateItems(newArrayOfComments, position)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error: ${e.message}")
            Toast.makeText(requireActivity(), "Error trying to like comment", Toast.LENGTH_SHORT).show()
        }
        cursor?.close()
        db.close()
    }

//    Interface function which checks if the comment has been disliked and updates the RecyclerView with the new data
    override fun onDislikeButtonClicked(comment: CommentData, position: Int) {
        val db = SchoolSQLiteDatabase(requireActivity())
        val userSession = requireActivity().getSharedPreferences("USER_SESSION", MODE_PRIVATE)
        val studentId = userSession.getInt("student_id", 0)
        val commentId = comment.commentId

        val cursor = db.retrieveCommentDislike(commentId, studentId)

        try {
            if (cursor != null) {
                if (cursor.count > 0) {
                    cursor.moveToFirst()
                    val dislikeId = cursor.getInt(cursor.getColumnIndexOrThrow("dislike_id"))

                    val commentData: CommentData
                    if (comment.isLiked) {
                        commentData = CommentData(
                            comment.commentId,
                            comment.commenterName,
                            comment.commentText,
                            comment.likes,
                            comment.dislikes.minus(1),
                            comment.commentDate,
                            isLiked = true,
                            isDisliked = false
                        )
                    } else {
                        commentData = CommentData(
                            comment.commentId,
                            comment.commenterName,
                            comment.commentText,
                            comment.likes,
                            comment.dislikes.minus(1),
                            comment.commentDate,
                            isLiked = false,
                            isDisliked = false
                        )
                    }

                    val newArrayOfComments = ArrayList<CommentData>()

                    newArrayOfComments.add(commentData)

                    db.deleteDislike(dislikeId)
                    db.updateComment(commentId, null, null, null, comment.dislikes.minus(1))

                    rcvAdapter.updateItems(newArrayOfComments, position)
                } else {
                    val commentData: CommentData
                    if (comment.isLiked) {
                        commentData = CommentData(
                            comment.commentId,
                            comment.commenterName,
                            comment.commentText,
                            comment.likes,
                            comment.dislikes.plus(1),
                            comment.commentDate,
                            isLiked = true,
                            isDisliked = true
                        )
                    } else {
                        commentData = CommentData(
                            comment.commentId,
                            comment.commenterName,
                            comment.commentText,
                            comment.likes,
                            comment.dislikes.plus(1),
                            comment.commentDate,
                            isLiked = false,
                            isDisliked = true
                        )
                    }

                    val newArrayOfComments = ArrayList<CommentData>()

                    newArrayOfComments.add(commentData)

                    db.insertCommentDislike(studentId, commentId)
                    db.updateComment(commentId, null, null, null, comment.dislikes.plus(1))

                    rcvAdapter.updateItems(newArrayOfComments, position)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error: ${e.message}")
            Toast.makeText(requireActivity(), "Error trying to dislike comment", Toast.LENGTH_SHORT).show()
        }
        cursor?.close()
        db.close()
    }
}