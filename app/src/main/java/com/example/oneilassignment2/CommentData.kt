package com.example.oneilassignment2

data class CommentData(
    val commentId: Int,
    val commenterName: String,
    val commentText: String,
    val likes: Int,
    val dislikes: Int,
    val commentDate: String,
    val isLiked: Boolean = false,
    val isDisliked: Boolean = false,
)
