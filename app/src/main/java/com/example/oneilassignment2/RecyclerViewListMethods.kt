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

    fun addPostsToList(listOfPosts: ArrayList<PostData>): ArrayList<PostData> {
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
            return ArrayList()
        }
        cursor?.close()
        return listOfPosts
    }

    fun addCommentsToList(postId: Int, arrayOfComments: ArrayList<CommentData>): ArrayList<CommentData> {
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
            return ArrayList()
        }
        cursor?.close()
        return arrayOfComments
    }

    fun addToSearches(search: String, students: ArrayList<StudentData>): ArrayList<StudentData> {
        if (search.isBlank()) {
            return ArrayList()
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
                            val chats = db.retrieveMultipleChats(userId)

                            if (chats != null) {
                                if (chats.count > 0) {
                                    chats.moveToFirst()
                                    while (!chats.isAfterLast) {
                                        val studentId1 =
                                            chats.getInt(chats.getColumnIndexOrThrow("student_id1"))
                                        val studentId2 =
                                            chats.getInt(chats.getColumnIndexOrThrow("student_id2"))

                                        if (studentId1 == studentId || studentId2 == studentId) {
                                            val studentData = StudentData(
                                                studentId,
                                                firstName,
                                                true
                                            )

                                            students.add(studentData)

                                            break
                                        } else {
                                            val studentData = StudentData(
                                                studentId,
                                                firstName,
                                                false
                                            )

                                            students.add(studentData)
                                        }
                                        chats.moveToNext()
                                    }
                                } else {
                                    val studentData = StudentData(
                                        studentId,
                                        firstName,
                                        false
                                    )

                                    students.add(studentData)

                                }
                            }
                            chats?.close()
                        }

                        studentSearch.moveToNext()
                    }
                }
            }
            studentSearch?.close()
            Log.d("AddToSearches", "Number of students added: ${students.size}")
            return students
        }
    }

    fun addToContacts(contacts: ArrayList<ChatData>): ArrayList<ChatData> {
        val userId = context.getSharedPreferences("USER_SESSION", MODE_PRIVATE).getInt("student_id", 0)
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
        return contacts
    }
}