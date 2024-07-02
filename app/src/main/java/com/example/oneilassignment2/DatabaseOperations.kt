package com.example.oneilassignment2

import android.content.Context
import android.widget.Toast

class DatabaseOperations(ctx: Context) {

    private val context = ctx

    fun deleteAccount(id: Int): Boolean {
        try {
            val db = SchoolSQLiteDatabase(context)

//        First we handle the number of likes, dislikes, and comments on other posts by the user

//                Handle updating of likes on posts and comments
            val studentLikes = db.retrieveStudentLikes(id)
            if (studentLikes != null) {
                if (studentLikes.count > 0) {
                    studentLikes.moveToFirst()

//                  This while loop loops through the likes by the user and handles update
//                  operations on each like
                    while (!studentLikes.isAfterLast) {
//                            Grab the post and comment id of the like
                        val postId =
                            studentLikes.getInt(studentLikes.getColumnIndexOrThrow("post_id"))
                        val commentId =
                            studentLikes.getInt(studentLikes.getColumnIndexOrThrow("comment_id"))

//                            Check if the post id is valid
                        if (postId != 0) {
//                                Retrieve the post from the database
                            val post = db.retrievePost(postId)

                            if (post != null) {
                                if (post.count > 0) {
                                    post.moveToFirst()

//                                        Grab the number of likes on the post
                                    val numOfLikes =
                                        post.getInt(post.getColumnIndexOrThrow("num_of_likes"))

//                                        Update the number of likes on the post by minus 1
                                    db.updatePost(
                                        postId,
                                        null,
                                        null,
                                        numOfLikes - 1,
                                        null,
                                        null
                                    )
                                }
                            }
                            post?.close()
                        }

//                            Check if the comment id is valid
                        if (commentId != 0) {
//                                Retrieve the comment from the database
                            val comment = db.retrieveComment(commentId)

                            if (comment != null) {
                                if (comment.count > 0) {
                                    comment.moveToFirst()

//                                        Grab the number of likes column
                                    val numOfLikes =
                                        comment.getInt(comment.getColumnIndexOrThrow("num_of_likes"))

//                                        Update the number of likes on the comment by minus 1
                                    db.updateComment(
                                        commentId,
                                        null,
                                        null,
                                        numOfLikes - 1,
                                        null
                                    )
                                }
                            }
                            comment?.close()
                        }
//                            Move to the next like id
                        studentLikes.moveToNext()
                    }
                }
            }
            studentLikes?.close()

//                Handle updating of dislikes on comments
            val commentDislikes = db.retrieveStudentDislikes(id)

            if (commentDislikes != null) {
                if (commentDislikes.count > 0) {
                    commentDislikes.moveToFirst()

//                        While loop loops through each dislike by the user and handles update
//                        operations on each dislike
                    while (!commentDislikes.isAfterLast) {
//                            Grab the comment id
                        val commentId =
                            commentDislikes.getInt(commentDislikes.getColumnIndexOrThrow("comment_id"))

//                            Check if the comment id is valid
                        if (commentId != 0) {
                            val comment = db.retrieveComment(commentId)

                            if (comment != null) {
                                if (comment.count > 0) {
                                    comment.moveToFirst()

//                                        Grab the number of dislikes on the comment
                                    val numOfDislikes =
                                        comment.getInt(comment.getColumnIndexOrThrow("num_of_dislikes"))

//                                        Update the number of dislikes on the comment by minus 1
                                    db.updateComment(
                                        commentId,
                                        null,
                                        null,
                                        null,
                                        numOfDislikes - 1
                                    )
                                }
                            }
                            comment?.close()
                        }
                        commentDislikes.moveToNext()
                    }
                }
            }
            commentDislikes?.close()

//                Handle updating of number of comments on posts
            val comments = db.retrieveStudentComments(id)

            if (comments != null) {
                if (comments.count > 0) {
                    comments.moveToFirst()

//                        While loop loops through each comment by the user and updates the number
//                        of comments on posts
                    while (!comments.isAfterLast) {
//                            Grab the post id
                        val postId = comments.getInt(comments.getColumnIndexOrThrow("post_id"))

//                            Check if the post id is valid
                        if (postId != 0) {
                            val post = db.retrievePost(postId)

                            if (post != null) {
                                if (post.count > 0) {
                                    post.moveToFirst()

//                                        Grab the number of comments on the post
                                    val numOfComments =
                                        post.getInt(post.getColumnIndexOrThrow("num_of_comments"))

//                                        Update the number of comments on the post by minus 1
                                    db.updatePost(
                                        postId,
                                        null,
                                        null,
                                        null,
                                        numOfComments - 1,
                                        null
                                    )
                                }
                            }
                            post?.close()
                        }
                        comments.moveToNext()
                    }
                }
            }
            comments?.close()

//                Next we handle the deletion of like and dislike data on the user's posts and comments

//                Handle deletion of likes and dislikes for the user's comments
            val studentComments = db.retrieveStudentComments(id)

            if (studentComments != null) {
                if (studentComments.count > 0) {
                    studentComments.moveToFirst()

//                        While loop loops through each comment by the user and handles deletion
//                        of like and dislike Ids
                    while (!studentComments.isAfterLast) {
//                            Grab the comment Id
                        val commentId =
                            studentComments.getInt(studentComments.getColumnIndexOrThrow("comment_id"))

//                            Retrieve the likes for the comment
                        val likes = db.retrieveCommentLikes(commentId)

                        if (likes != null) {
                            if (likes.count > 0) {
                                likes.moveToFirst()

//                                    Loop through the likes and delete each like
                                while (!likes.isAfterLast) {
                                    val likeId =
                                        likes.getInt(likes.getColumnIndexOrThrow("like_id"))

                                    db.deleteLike(likeId)

                                    likes.moveToNext()
                                }
                            }
                        }
                        likes?.close()

//                            Retrieve the dislikes for the comment
                        val dislikes = db.retrieveCommentDislikes(commentId)

                        if (dislikes != null) {
                            if (dislikes.count > 0) {
                                dislikes.moveToFirst()

//                                    Loop through the dislikes and delete each dislike
                                while (!dislikes.isAfterLast) {
                                    val dislikeId =
                                        dislikes.getInt(dislikes.getColumnIndexOrThrow("dislike_id"))

                                    db.deleteDislike(dislikeId)

                                    dislikes.moveToNext()
                                }
                            }
                        }
                        dislikes?.close()

                        studentComments.moveToNext()
                    }
                }
            }
            studentComments?.close()

//                Handle deletion of likes for the user's posts
            val studentPosts = db.retrieveStudentPosts(id)

            if (studentPosts != null) {
                if (studentPosts.count > 0) {
                    studentPosts.moveToFirst()

//                        While loop loops through each post by the user and handles deletion
//                        of like Ids
                    while (!studentPosts.isAfterLast) {
//                            Grab the post Id
                        val postId =
                            studentPosts.getInt(studentPosts.getColumnIndexOrThrow("post_id"))

//                            Retrieve the likes for the post
                        val likes = db.retrievePostLikes(postId)

                        if (likes != null) {
                            if (likes.count > 0) {
                                likes.moveToFirst()

//                                    Loop through the likes and delete each like
                                while (!likes.isAfterLast) {
                                    val likeId =
                                        likes.getInt(likes.getColumnIndexOrThrow("like_id"))

                                    db.deleteLike(likeId)

                                    likes.moveToNext()
                                }
                            }
                        }
                        likes?.close()

                        studentPosts.moveToNext()
                    }
                }
            }
            studentPosts?.close()

//            Handle deletion of chat messages in all of the chats they are involved in
            val studentChats = db.retrieveMultipleChats(id)

            if (studentChats != null) {
                if (studentChats.count > 0) {
                    studentChats.moveToFirst()

//                    While loop loops through the chats that the user is involved in and handles
//                    the deletion of the chat messages
                    while (!studentChats.isAfterLast) {
//                        Grab the chat Id
                        val chatId = studentChats.getInt(studentChats.getColumnIndexOrThrow("chat_id"))

//                        Delete the messages in the chat
                        db.deleteChatMessages(chatId)

                        studentChats.moveToNext()
                    }
                }
            }
            studentChats?.close()

//                Performs final deletes after referential data has been updated and/ or deleted
            db.deleteStudent(id)
            db.deleteStudentPosts(id)
            db.deleteStudentComments(id)
            db.deleteStudentLikes(id)
            db.deleteStudentDislikes(id)
            db.deleteStudentChats(id)

            return true
        } catch (e: Exception) {
            Toast.makeText(context, "There was an error with the delete operation", Toast.LENGTH_SHORT).show()
            return false
        }
    }

}