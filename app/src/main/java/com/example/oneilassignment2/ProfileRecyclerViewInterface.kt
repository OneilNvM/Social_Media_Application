package com.example.oneilassignment2

interface ProfileRecyclerViewInterface {
    fun onLikeButtonClicked(post: PostData, position: Int)
    fun onCommentButtonClicked(post: PostData, position: Int)
    fun onEditButtonClicked(post: PostData, position: Int)
}