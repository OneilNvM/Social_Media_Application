package com.example.oneilassignment2

interface CommentsRecyclerViewInterface {
    fun onLikeButtonClicked(comment: CommentData, position: Int)
    fun onDislikeButtonClicked(comment: CommentData, position: Int)
}