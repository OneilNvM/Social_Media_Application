package oneil.library.databaseintermediary

import android.content.Context
import com.example.oneilassignment2.SchoolSQLiteDatabase

object DatabaseMedium {
    lateinit var schoolDataBase: SchoolSQLiteDatabase

    fun setDatabase(context: Context) {
        schoolDataBase = SchoolSQLiteDatabase(context)
    }
}