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

class ProfilePostsFragment : Fragment(), ProfileRecyclerViewInterface {

    private lateinit var sharedViewModel: PostDataViewModel
    private lateinit var listOfPosts: ArrayList<PostData>
    private lateinit var rcvAdapter: ProfileRecyclerViewAdapter
    private lateinit var db: SchoolSQLiteDatabase

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_profile_posts, container, false)
        val mainActivity = requireActivity() as MainActivity
        sharedViewModel = ViewModelProvider(mainActivity)[PostDataViewModel::class.java]

        db = SchoolSQLiteDatabase(mainActivity)

        val swipeRefreshLayout = view.findViewById<SwipeRefreshLayout>(R.id.profile_posts_swipe_refresh)
        val recyclerView = view.findViewById<RecyclerView>(R.id.profile_posts_recycler_view)
        val noPostsText = view.findViewById<TextView>(R.id.profile_posts_text)

        //        Setup the swipe refresh functionality for the recycler view
        swipeRefreshLayout.setOnRefreshListener {
            if (listOfPosts.isEmpty()) {
                swipeRefreshLayout.isRefreshing = false
                return@setOnRefreshListener
            } else {
                listOfPosts.clear()
                addPostsToList()
                if (listOfPosts.isEmpty()) {
                    noPostsText.text = getString(R.string.no_posts_text)
                    noPostsText.visibility = View.VISIBLE
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
            noPostsText.text = getString(R.string.no_posts_text)
        } else {
            noPostsText.visibility = View.GONE

            recyclerView.layoutManager = LinearLayoutManager(mainActivity)

            rcvAdapter = ProfileRecyclerViewAdapter(listOfPosts, this)

            recyclerView.adapter = rcvAdapter
        }

        return view
    }

    private fun addPostsToList() {
        val userSession = requireActivity().getSharedPreferences("USER_SESSION",
            Context.MODE_PRIVATE
        )
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

    //    Interface function handles click events on the edit post button on the posts
    override fun onEditButtonClicked(post: PostData, position: Int) {
        sharedViewModel.post = post

        parentFragmentManager.beginTransaction()
            .add(R.id.full_frame_fragment_container, EditPostFragment())
            .addToBackStack(null)
            .commit()
    }

    override fun onDestroyView() {
        db.close()
        super.onDestroyView()
    }
}