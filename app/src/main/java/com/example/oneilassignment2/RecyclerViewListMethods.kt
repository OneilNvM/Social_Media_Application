package com.example.oneilassignment2

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.database.Cursor
import android.util.Log
import android.widget.Toast

class RecyclerViewListMethods(ctx: Context) {

    private val context = ctx
    private val db = SchoolSQLiteDatabase(context)

    fun addPostsToHomeList(listOfPosts: ArrayList<PostData>) {
        val userSession = context.getSharedPreferences("USER_SESSION", MODE_PRIVATE)
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
    }

    fun addPostsToProfileLikesList(listOfPosts: ArrayList<PostData>) {
        val userSession = context.getSharedPreferences(
            "USER_SESSION",
            MODE_PRIVATE
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
            Log.e(TAG, "Error trying to add posts to list: ${e.message}")
        }
        studentLikes?.close()
    }

    fun addPostsToProfilePostsList(listOfPosts: ArrayList<PostData>) {
        val userSession = context.getSharedPreferences("USER_SESSION",
            MODE_PRIVATE
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
            Log.e(TAG, "Error trying to add posts to list: ${e.message}")
        }
        cursor?.close()
    }

    fun addCommentsToList(
        postId: Int,
        arrayOfComments: ArrayList<CommentData>
    ) {
        val cursor = db.retrieveCommentsOrderedByCommentId(postId)
        val userSession = context.getSharedPreferences("USER_SESSION", MODE_PRIVATE)
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
            Toast.makeText(context, "Error trying to retrieve comments", Toast.LENGTH_SHORT).show()
        }
        cursor?.close()
    }

    fun addToSearches(search: String, students: ArrayList<StudentData>) {
        if (search.isBlank()) {
            return
        } else {
            val studentSearch = db.searchStudents(search)
            val userSession = context.getSharedPreferences("USER_SESSION", MODE_PRIVATE)
            val userId = userSession.getInt("student_id", 0)

            if (studentSearch != null) {
                if (studentSearch.count > 0) {
                    studentSearch.moveToFirst()

                    while (!studentSearch.isAfterLast) {
                        val studentId =
                            studentSearch.getInt(studentSearch.getColumnIndexOrThrow("student_id"))
                        val firstName =
                            studentSearch.getString(studentSearch.getColumnIndexOrThrow("first_name"))

                        if (studentId != userId) {

                            val chats: Cursor? =
                                if (db.retrieveSpecificChat(userId, studentId)?.count != 0) {
                                    db.retrieveSpecificChat(userId, studentId)
                                } else if (db.retrieveSpecificChat(studentId, userId)?.count != 0) {
                                    db.retrieveSpecificChat(studentId, userId)
                                } else {
                                    null
                                }

                            if (chats != null) {
                                if (chats.count > 0) {
                                    Log.d("ChatsCount", "Number of chats: ${chats.count}")
                                    chats.moveToFirst()

                                    val studentData = StudentData(
                                        studentId,
                                        firstName,
                                        true
                                    )

                                    students.add(studentData)

                                }
                            } else {
                                val studentData = StudentData(
                                    studentId,
                                    firstName,
                                    false
                                )

                                students.add(studentData)
                            }
                            chats?.close()
                        }

                        studentSearch.moveToNext()
                    }
                }
            }
            studentSearch?.close()
            Log.d("AddToSearches", "Number of students added: ${students.size}")
        }
    }

    fun addToContacts(contacts: ArrayList<ChatData>) {
        val userId =
            context.getSharedPreferences("USER_SESSION", MODE_PRIVATE).getInt("student_id", 0)
        val currentContacts = db.retrieveMultipleChats(userId)

        if (currentContacts != null) {
            if (currentContacts.count > 0) {
                currentContacts.moveToFirst()

                while (!currentContacts.isAfterLast) {
                    val chatId =
                        currentContacts.getInt(currentContacts.getColumnIndexOrThrow("chat_id"))
                    val dateCreated =
                        currentContacts.getString(currentContacts.getColumnIndexOrThrow("date_created"))
                    val studentId1 =
                        currentContacts.getInt(currentContacts.getColumnIndexOrThrow("student_id1"))
                    val studentId2 =
                        currentContacts.getInt(currentContacts.getColumnIndexOrThrow("student_id2"))

                    if (studentId1 == userId) {
                        val student = db.retrieveStudent(studentId2)

                        if (student != null) {
                            if (student.count > 0) {
                                student.moveToFirst()

                                val studentName =
                                    student.getString(student.getColumnIndexOrThrow("first_name"))

                                val message = db.retrieveLastMessage(chatId)

                                if (message != null) {
                                    if (message.count > 0) {
                                        message.moveToFirst()

                                        val messageText =
                                            message.getString(message.getColumnIndexOrThrow("text"))

                                        val chat = ChatData(
                                            chatId,
                                            studentName,
                                            dateCreated,
                                            studentId1,
                                            studentId2,
                                            messageText
                                        )

                                        contacts.add(chat)
                                    } else {
                                        val chat = ChatData(
                                            chatId,
                                            studentName,
                                            dateCreated,
                                            studentId1,
                                            studentId2,
                                            null
                                        )

                                        contacts.add(chat)
                                    }
                                }
                                message?.close()
                            }
                        }
                        student?.close()
                    } else {
                        val student = db.retrieveStudent(studentId1)

                        if (student != null) {
                            if (student.count > 0) {
                                student.moveToFirst()

                                val studentName =
                                    student.getString(student.getColumnIndexOrThrow("first_name"))

                                val message = db.retrieveLastMessage(chatId)

                                if (message != null) {
                                    if (message.count > 0) {
                                        message.moveToFirst()

                                        val messageText =
                                            message.getString(message.getColumnIndexOrThrow("text"))

                                        val chat = ChatData(
                                            chatId,
                                            studentName,
                                            dateCreated,
                                            studentId1,
                                            studentId2,
                                            messageText
                                        )

                                        contacts.add(chat)
                                    } else {
                                        val chat = ChatData(
                                            chatId,
                                            studentName,
                                            dateCreated,
                                            studentId1,
                                            studentId2,
                                            null
                                        )

                                        contacts.add(chat)
                                    }
                                }
                                message?.close()
                            }
                        }
                        student?.close()
                    }

                    currentContacts.moveToNext()
                }
            }
        }
        currentContacts?.close()
    }

    fun addToMessages(id: Int, messagesList: ArrayList<MessageData>) {
        val chatMessages = db.retrieveMessagesByChat(id)
        val userId = context.getSharedPreferences("USER_SESSION", MODE_PRIVATE).getInt("student_id", 0)

        if (chatMessages != null) {
            if (chatMessages.count > 0) {
                chatMessages.moveToFirst()

                while (!chatMessages.isAfterLast) {
                    val messageText = chatMessages.getString(chatMessages.getColumnIndexOrThrow("text"))
                    val messageDate = chatMessages.getString(chatMessages.getColumnIndexOrThrow("date"))
                    val chatId = chatMessages.getInt(chatMessages.getColumnIndexOrThrow("chat_id"))
                    val studentId = chatMessages.getInt(chatMessages.getColumnIndexOrThrow("student_id"))

                    if (studentId == userId) {
                        val message = MessageData(
                            messageText,
                            messageDate,
                            chatId,
                            studentId,
                            true
                        )

                        messagesList.add(message)
                    } else {
                        val message = MessageData(
                            messageText,
                            messageDate,
                            chatId,
                            studentId
                        )

                        messagesList.add(message)
                    }

                    chatMessages.moveToNext()
                }
            }
        }
        chatMessages?.close()
    }
}