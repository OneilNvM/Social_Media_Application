package com.example.oneilassignment2

interface HomePostsRecyclerViewInterface {
    fun onLikeButtonClicked(post: PostData, position: Int)
    fun onCommentButtonClicked(post: PostData, position: Int)
}