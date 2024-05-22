package com.example.oneilassignment2

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Context.MODE_PRIVATE
import android.database.Cursor
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

class HomeFragment : Fragment(), HomePostsRecyclerViewInterface {

//    Set lateinit variables for the RecyclerView and ViewModel
    private lateinit var listOfPosts: ArrayList<PostData>
    private lateinit var rcvAdapter: HomePostsRecyclerViewAdapter
    private lateinit var sharedViewModel: PostDataViewModel

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)

//        Set variables for main activity and other views
        val mainActivity = requireActivity() as MainActivity
        val swipeRefresh = view.findViewById<SwipeRefreshLayout>(R.id.home_swipe_refresh_layout)
//        Get access to the PostDataViewModel to change its data
        sharedViewModel = ViewModelProvider(mainActivity)[PostDataViewModel::class.java]

//        Refresh the RecyclerView to get latest post data
        swipeRefresh.setOnRefreshListener {
            listOfPosts.clear()
            addPostsToList()
            rcvAdapter.notifyDataSetChanged()
            swipeRefresh.isRefreshing = false
        }

//        Setup the RecyclerView and add the posts to the list
        val rcvPosts = view.findViewById<RecyclerView>(R.id.home_recycler_view_container)

        rcvPosts.itemAnimator = null

        listOfPosts = ArrayList()

        addPostsToList()

        rcvPosts.layoutManager = LinearLayoutManager(mainActivity)
        rcvAdapter = HomePostsRecyclerViewAdapter(listOfPosts, this)

        rcvPosts.adapter = rcvAdapter

        return view
    }

//    Function to add the posts to the list for the RecyclerView to display
    private fun addPostsToList() {
        val db = SchoolSQLiteDatabase(requireActivity())
        val userSession = requireActivity().getSharedPreferences("USER_SESSION", MODE_PRIVATE)
        val userId = userSession.getInt("student_id", 0)
        val cursor: Cursor? = db.retrieveAllPosts()

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
            Log.e(TAG, "Error: Posts could not be added to list")
        }
        cursor?.close()
        db.close()
    }

/*    This function is called when the like button is pressed and updates the database and
RecyclerView based on whether the user has like the post or not
 */
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

    override fun onCommentButtonClicked(post: PostData, position: Int) {
//        Send PostData for the specific post to the ViewModel for the CommentsFragment to display its comments
        sharedViewModel.post = post

        parentFragmentManager.beginTransaction()
            .add(R.id.comments_fragment_container, CommentsFragment())
            .addToBackStack(null)
            .commit()

    }
}