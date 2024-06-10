package com.example.oneilassignment2

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

class ProfileLikesFragment : Fragment(), HomePostsRecyclerViewInterface {

    private lateinit var sharedViewModel: PostDataViewModel
    private lateinit var listOfPosts: ArrayList<PostData>
    private lateinit var rcvAdapter: HomePostsRecyclerViewAdapter
    private lateinit var db: SchoolSQLiteDatabase

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_profile_likes, container, false)
        val mainActivity = requireActivity() as MainActivity
        sharedViewModel = ViewModelProvider(mainActivity)[PostDataViewModel::class.java]

        db = SchoolSQLiteDatabase(mainActivity)

        val recyclerView = view.findViewById<RecyclerView>(R.id.profile_likes_recycler_view)
        val swipeRefreshLayout = view.findViewById<SwipeRefreshLayout>(R.id.profile_likes_swipe_refresh)
        val noLikesText = view.findViewById<TextView>(R.id.profile_likes_text)

        //        Setup the swipe refresh functionality for the recycler view
        swipeRefreshLayout.setOnRefreshListener {
            if (listOfPosts.isEmpty()) {
                swipeRefreshLayout.isRefreshing = false
                return@setOnRefreshListener
            } else {
                listOfPosts.clear()
                addPostsToList()
                if (listOfPosts.isEmpty()) {
                    noLikesText.text = getString(R.string.no_likes_text)
                    noLikesText.visibility = View.VISIBLE
                }
                rcvAdapter.notifyDataSetChanged()
                swipeRefreshLayout.isRefreshing = false
            }
        }

        //        Setup the recycler view
        recyclerView.itemAnimator = null

        listOfPosts = ArrayList()

        addPostsToList()

        if (listOfPosts.isEmpty()) {
            noLikesText.text = getString(R.string.no_likes_text)
        } else {
            noLikesText.visibility = View.GONE

            recyclerView.layoutManager = LinearLayoutManager(mainActivity)

            rcvAdapter = HomePostsRecyclerViewAdapter(listOfPosts, this)

            recyclerView.adapter = rcvAdapter
        }
        return view
    }

    private fun addPostsToList() {
        val userSession = requireActivity().getSharedPreferences(
            "USER_SESSION",
            Context.MODE_PRIVATE
        )
        val userId = userSession.getInt("student_id", 0)
        val studentLikes = db.retrieveStudentLikesOrdered(userId)
        try {
            if (studentLikes != null) {
                if (studentLikes.count > 0) {
                    studentLikes.moveToFirst()

                    while (!studentLikes.isAfterLast) {
                        val postId =
                            studentLikes.getInt(studentLikes.getColumnIndexOrThrow("post_id"))

                        val post = db.retrievePost(postId)

                        if (post != null) {
                            if (post.count > 0) {
                                post.moveToFirst()
                                val posterId = post.getInt(post.getColumnIndexOrThrow("student_id"))
                                val posterName =
                                    post.getString(post.getColumnIndexOrThrow("poster_name"))
                                val postCaption =
                                    post.getString(post.getColumnIndexOrThrow("caption"))
                                val postDate = post.getString(post.getColumnIndexOrThrow("date"))
                                val numLikes =
                                    post.getInt(post.getColumnIndexOrThrow("num_of_likes"))
                                val numComments =
                                    post.getInt(post.getColumnIndexOrThrow("num_of_comments"))


                                val postData = PostData(
                                    postId,
                                    posterId,
                                    posterName,
                                    postCaption,
                                    numLikes,
                                    numComments,
                                    postDate,
                                    true
                                )

                                listOfPosts.add(postData)
                            }
                        }

                        post?.close()

                        studentLikes.moveToNext()
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(ContentValues.TAG, "Error trying to add posts to list: ${e.message}")
        }
        studentLikes?.close()
    }

    //    Interface function handles click events on the like button on the posts
    override fun onLikeButtonClicked(post: PostData, position: Int) {
        val mainActivity = requireActivity() as MainActivity
        val userSession = mainActivity.getSharedPreferences("USER_SESSION", Context.MODE_PRIVATE)
        val userId = userSession.getInt("student_id", 0)
        val dbWrite = db.writableDatabase
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
    }

    //    Interface function handles click events on the comment button on the posts
    override fun onCommentButtonClicked(post: PostData, position: Int) {
        sharedViewModel.post = post

        parentFragmentManager.beginTransaction()
            .add(R.id.comments_fragment_container, CommentsFragment())
            .addToBackStack(null)
            .commit()
    }

    override fun onDestroyView() {
        db.close()
        super.onDestroyView()
    }
}