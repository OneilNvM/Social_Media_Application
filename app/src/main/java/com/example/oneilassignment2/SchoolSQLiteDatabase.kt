package com.example.oneilassignment2

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class SchoolSQLiteDatabase(context: Context): SQLiteOpenHelper(context, "SCHOOL_DB", null, 20) {
    private val studentsTable = "students"
    private val studentsID = "student_id"
    private val studentsFirstName = "first_name"
    private val studentsSurname = "surname"
    private val studentsEmail = "email"
    private val studentsDOB = "dob"
    private val studentsPassword = "password"
    private val dateCreated = "date_created"
    private val dateUpdated = "date_updated"

    private val adminsTable = "admins"
    private val adminsId = "admin_id"
    private val adminsEmail = "email"
    private val adminsPassword = "password"

    private val postsTable = "posts"
    private val postsId = "post_id"
    private val posterName = "poster_name"
    private val postCaption = "caption"
    private val postsDate = "date"
    private val numOfLikes = "num_of_likes"
    private val numOfComments = "num_of_comments"
    private val postsStudentIDRef = "student_id"

    private val commentsTable = "comments"
    private val commentsId = "comment_id"
    private val commenterName = "commenter_name"
    private val commentsText = "text"
    private val commentsDate = "date"
    private val numOfLikesOnComment = "num_of_likes"
    private val numOfDislikesOnComment = "num_of_dislikes"
    private val commentsStudentIDRef = "student_id"
    private val commentsPostIDRef = "post_id"

    private val likesTable = "likes"
    private val likesId = "like_id"
    private val likesStudentIDRef = "student_id"
    private val likesPostIDRef = "post_id"
    private val likesCommentIDRef = "comment_id"

    private val dislikesTable = "dislikes"
    private val dislikesId = "dislike_id"
    private val dislikesStudentIDRef = "student_id"
    private val dislikesCommentsIDRef = "comment_id"


    override fun onCreate(db: SQLiteDatabase?) {
        val createStudentsTable = "CREATE TABLE IF NOT EXISTS $studentsTable ($studentsID INTEGER PRIMARY KEY AUTOINCREMENT, $studentsFirstName TEXT, $studentsSurname TEXT, $studentsEmail TEXT, $studentsDOB TEXT, $studentsPassword TEXT, $dateCreated TEXT, $dateUpdated TEXT)"
        val createAdminsTable = "CREATE TABLE IF NOT EXISTS $adminsTable ($adminsId INTEGER PRIMARY KEY AUTOINCREMENT, $adminsEmail TEXT, $adminsPassword TEXT)"
        val createPostsTable = "CREATE TABLE IF NOT EXISTS $postsTable ($postsId INTEGER PRIMARY KEY AUTOINCREMENT, $posterName TEXT, $postCaption TEXT, $postsDate TEXT, $numOfLikes INTEGER DEFAULT 0, $numOfComments INTEGER DEFAULT 0, $postsStudentIDRef INTEGER)"
        val createCommentsTable = "CREATE TABLE IF NOT EXISTS $commentsTable ($commentsId INTEGER PRIMARY KEY AUTOINCREMENT, $commenterName TEXT, $commentsText TEXT, $commentsDate TEXT, $numOfLikesOnComment INTEGER DEFAULT 0, $numOfDislikesOnComment INTEGER DEFAULT 0, $commentsStudentIDRef INTEGER, $commentsPostIDRef INTEGER)"
        val createLikesTable = "CREATE TABLE IF NOT EXISTS $likesTable ($likesId INTEGER PRIMARY KEY AUTOINCREMENT, $likesStudentIDRef INTEGER, $likesPostIDRef INTEGER, $likesCommentIDRef INTEGER)"
        val createDislikesTable = "CREATE TABLE IF NOT EXISTS $dislikesTable ($dislikesId INTEGER PRIMARY KEY AUTOINCREMENT, $dislikesStudentIDRef INTEGER, $dislikesCommentsIDRef INTEGER)"
        db?.execSQL(createStudentsTable)
        db?.execSQL(createAdminsTable)
        db?.execSQL(createPostsTable)
        db?.execSQL(createCommentsTable)
        db?.execSQL(createLikesTable)
        db?.execSQL(createDislikesTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        val dropStudentsTable = "DROP TABLE IF EXISTS $studentsTable"
        val dropAdminsTable = "DROP TABLE IF EXISTS $adminsTable"
        val dropPostsTable = "DROP TABLE IF EXISTS $postsTable"
        val dropCommentsTable = "DROP TABLE IF EXISTS $commentsTable"
        val dropLikesTable = "DROP TABLE IF EXISTS $likesTable"
        val dropDislikesTable = "DROP TABLE IF EXISTS $dislikesTable"
        val values = ContentValues()
        values.put(adminsEmail, "admin@school.co.uk")
        values.put(adminsPassword, "admin")

        if (oldVersion < newVersion) {
            db?.execSQL(dropStudentsTable)
            db?.execSQL(dropAdminsTable)
            db?.execSQL(dropPostsTable)
            db?.execSQL(dropCommentsTable)
            db?.execSQL(dropLikesTable)
            db?.execSQL(dropDislikesTable)
            onCreate(db)
            db?.insert(adminsTable, null, values)
        }
    }

//    Students Methods
    fun retrieveStudent(studentId: Int) : Cursor? {
        val db = readableDatabase
        return db.rawQuery("SELECT * FROM $studentsTable WHERE $studentsID = $studentId", null)
    }

    fun insertStudent(firstName: String, surname: String, email: String, dob: String, password: String, dateOfCreation: String) {
        if (firstName.isNotEmpty() && surname.isNotEmpty() && email.isNotEmpty() && dob.isNotEmpty() && password.isNotEmpty()) {
            val db = writableDatabase
            val values = ContentValues()

            values.put(studentsFirstName, firstName)
            values.put(studentsSurname, surname)
            values.put(studentsEmail, email)
            values.put(studentsDOB, dob)
            values.put(studentsPassword, password)
            values.put(dateCreated, dateOfCreation)

            db.insert(studentsTable, null, values)
            db.close()
        }
    }

    fun updateStudent(id: Int, firstName: String?, surname: String?, email: String?, dob: String?, password: String?, dateOfUpdate: String) {
        val db = writableDatabase
        val values = ContentValues()
        if (firstName != null) {
            values.put(studentsFirstName, firstName)
        }
        if (surname != null) {
            values.put(studentsSurname, surname)
        }
        if (email != null) {
            values.put(studentsEmail, email)
        }
        if (dob != null) {
            values.put(studentsDOB, dob)
        }
        if (password != null) {
            values.put(studentsPassword, password)
        }
        values.put(dateUpdated, dateOfUpdate)

        db.update(studentsTable, values, "$studentsID = ?", arrayOf(id.toString()))
        db.close()
    }

    fun deleteStudent(id: Int) {
        val db = writableDatabase
        db.delete(studentsTable, "$studentsID = ?", arrayOf(id.toString()))
        db.close()
    }

//    Posts Methods
    fun insertPost(name: String?, caption: String, date: String, studentID: Int?) {
        if (posterName.isNotEmpty() && caption.isNotEmpty() && date.isNotEmpty() && studentID != null) {
            val db = writableDatabase
            val values = ContentValues()

            values.put(posterName, name)
            values.put(postCaption, caption)
            values.put(postsDate, date)
            values.put(postsStudentIDRef, studentID)

            db.insert(postsTable, null, values)
            db.close()
        }
    }

    fun updatePost(id: Int, caption: String?, date: String?, likes: Int?, comments: Int?, studentID: Int?) {
        val db = writableDatabase
        val values = ContentValues()

        if (caption != null) {
            values.put(postCaption, caption)
        }
        if (date != null) {
            values.put(postsDate, date)
        }
        if (likes != null) {
            values.put(numOfLikes, likes)
        }
        if (comments != null) {
            values.put(numOfComments, comments)
        }
        if (studentID != null) {
            values.put(postsStudentIDRef, studentID)
        }
        db.update(postsTable, values, "$postsId = ?", arrayOf(id.toString()))
        db.close()
    }

    fun updateStudentPosts(studentId: Int, firstName: String?, caption: String?) {
        val db = writableDatabase
        val values = ContentValues()
        if (firstName != null) {
            values.put(posterName, firstName)
        }
        if (caption != null) {
            values.put(postCaption, caption)
        }
        db.update(postsTable, values, "$postsStudentIDRef = ?", arrayOf(studentId.toString()))
        db.close()
    }

    fun retrievePost(id: Int): Cursor? {
        val db = readableDatabase
        return db.rawQuery("SELECT * FROM $postsTable WHERE $postsId = $id", null)
    }

    fun retrieveAllPosts(): Cursor? {
        val db = readableDatabase
        return db.rawQuery("SELECT * FROM $postsTable ORDER BY $postsId DESC", null)
    }

    fun retrieveStudentPosts(studentId: Int): Cursor? {
        val db = readableDatabase
        return db.rawQuery("SELECT * FROM $postsTable WHERE $postsStudentIDRef = $studentId ORDER BY $postsId DESC", null)
    }

    fun deleteStudentPosts(studentID: Int) {
        val db = writableDatabase
        db.delete(postsTable, "$postsStudentIDRef = ?", arrayOf(studentID.toString()))
        db.close()
    }

//    Likes Methods

    fun retrieveLike(postID: Int, studentID: Int): Cursor? {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $likesTable WHERE $likesPostIDRef = $postID AND $likesStudentIDRef = $studentID", null)

        return cursor
    }

    fun retrieveCommentLike(commentID: Int, studentID: Int): Cursor? {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $likesTable WHERE $likesCommentIDRef = $commentID AND $likesStudentIDRef = $studentID", null)

        return cursor
    }

    fun retrieveStudentLikes(studentID: Int): Cursor? {
        val db = readableDatabase
        return db.rawQuery("SELECT * FROM $likesTable WHERE $likesStudentIDRef = $studentID", null)
    }

    fun retrievePostLikes(postId: Int): Cursor? {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $likesTable WHERE $likesPostIDRef = $postId", null)
        return cursor
    }

    fun retrieveCommentLikes(commentId: Int): Cursor? {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $likesTable WHERE $likesCommentIDRef = $commentId", null)
        return cursor
    }

    fun insertCommentLike(studentID: Int, commentID: Int) {
        val db = writableDatabase
        val values = ContentValues()
        values.put(likesStudentIDRef, studentID)
        values.put(likesCommentIDRef, commentID)

        db.insert(likesTable, null, values)
        db.close()
    }

    fun insertPostLike(studentID: Int, postID: Int) {
        val db = writableDatabase
        val values = ContentValues()
        values.put(likesStudentIDRef, studentID)
        values.put(likesPostIDRef, postID)
        db.insert(likesTable, null, values)
        db.close()
    }

    fun deletePostLike(studentID: Int, postID: Int) {
        val db = writableDatabase
        db.delete(likesTable, "$likesStudentIDRef = ? AND $likesPostIDRef = ?", arrayOf(studentID.toString(), postID.toString()))
        db.close()
    }

    fun deleteLike(likeId: Int) {
        val db = writableDatabase
        db.delete(likesTable, "$likesId = ?", arrayOf(likeId.toString()))
        db.close()
    }

    fun deleteStudentLikes(studentID: Int) {
        val db = writableDatabase
        db.delete(likesTable, "$likesStudentIDRef = ?", arrayOf(studentID.toString()))
        db.close()
    }

//    Dislike Methods

    fun retrieveCommentDislike(commentID: Int, studentID: Int): Cursor? {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $dislikesTable WHERE $dislikesCommentsIDRef = $commentID AND $dislikesStudentIDRef = $studentID", null)

        return cursor
    }

    fun retrieveStudentDislikes(studentID: Int): Cursor? {
        val db = readableDatabase
        return db.rawQuery("SELECT * FROM $dislikesTable WHERE $dislikesStudentIDRef = $studentID", null)
    }

    fun retrieveCommentDislikes(commentId: Int): Cursor? {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $dislikesTable WHERE $dislikesCommentsIDRef = $commentId", null)
        return cursor
    }

    fun insertCommentDislike(studentID: Int, commentID: Int) {
        val db = writableDatabase
        val values = ContentValues()
        values.put(dislikesStudentIDRef, studentID)
        values.put(dislikesCommentsIDRef, commentID)

        db.insert(dislikesTable, null, values)
        db.close()
    }

    fun deleteDislike(dislikeId: Int) {
        val db = writableDatabase
        db.delete(dislikesTable, "$dislikesId = ?", arrayOf(dislikeId.toString()))
        db.close()
    }

    fun deleteStudentDislikes(studentID: Int) {
        val db = writableDatabase
        db.delete(dislikesTable, "$dislikesStudentIDRef = ?", arrayOf(studentID.toString()))
        db.close()
    }

//    Comments Methods

    fun retrieveComments(postId: Int): Cursor? {
        val db = readableDatabase
        return db.rawQuery("SELECT * FROM $commentsTable WHERE $commentsPostIDRef = $postId", null)
    }

    fun retrieveCommentsOrderedByCommentId(postId: Int): Cursor? {
        val db = readableDatabase
        return db.rawQuery("SELECT * FROM $commentsTable WHERE $commentsPostIDRef = $postId ORDER BY $commentsId DESC", null)
    }

    fun retrieveStudentComments(studentId: Int): Cursor? {
        val db = readableDatabase
        return db.rawQuery("SELECT * FROM $commentsTable WHERE $commentsStudentIDRef = $studentId", null)
    }

    fun insertComment(name: String, text: String, date: String, studentID: Int, postID: Int) {
        val db = writableDatabase
        val values = ContentValues()
        values.put(commenterName, name)
        values.put(commentsText, text)
        values.put(commentsDate, date)
        values.put(commentsStudentIDRef, studentID)
        values.put(commentsPostIDRef, postID)

        db.insert(commentsTable, null, values)
        db.close()
    }

    fun updateComment(id: Int, text: String?, date: String?, likes: Int?, dislikes: Int?) {
        val db = writableDatabase
        val values = ContentValues()

        if (text != null) {
            values.put(commentsText, text)
        }
        if (date != null) {
            values.put(commentsDate, date)
        }
        if (likes != null) {
            values.put(numOfLikesOnComment, likes)
        }
        if (dislikes != null) {
            values.put(numOfDislikesOnComment, dislikes)
        }
        db.update(commentsTable, values, "$commentsId = ?", arrayOf(id.toString()))
        db.close()
    }

    fun updateStudentComments(studentId: Int, firstName: String?, text: String?) {
        val db = writableDatabase
        val values = ContentValues()
        if (firstName != null) {
            values.put(commenterName, firstName)
        }
        if (text != null) {
            values.put(commentsText, text)
        }
        db.update(commentsTable, values, "$commentsStudentIDRef = ?", arrayOf(studentId.toString()))
        db.close()
    }

    fun deleteStudentComments(studentID: Int) {
        val db = writableDatabase
        db.delete(commentsTable, "$commentsStudentIDRef = ?", arrayOf(studentID.toString()))
        db.close()
    }

}