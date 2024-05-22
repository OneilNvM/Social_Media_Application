package com.example.oneilassignment2

data class PostData (
    val postId: Int,
    val posterId: Int,
    val postersName: String,
    val caption: String,
    val numOfLikes: Int,
    val numOfComments: Int,
    val dateAndTime: String,
    val isLiked: Boolean = false
)