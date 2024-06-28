package com.example.oneilassignment2

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate

class DeleteStudentFragment : Fragment() {

    private lateinit var db: SchoolSQLiteDatabase

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_delete_student, container, false)
        //        Touch event set to nothing to prevent interaction with background views whilst this fragment is visible
        view.setOnTouchListener { _, _ -> return@setOnTouchListener true }

//        Sets variables for the main activity, database and other views
        val mainActivity = requireActivity() as MainActivity
        db = SchoolSQLiteDatabase(mainActivity)

        val studentIdInput = view.findViewById<EditText>(R.id.admin_delete_student_id)
        val deleteButton = view.findViewById<Button>(R.id.admin_delete_student_delete_button)
        val backButton = view.findViewById<ImageButton>(R.id.admin_delete_student_back_button)

        val databaseOps = DatabaseOperations(mainActivity)

//        Handle UI changes for dark mode
        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            backButton.setBackgroundResource(R.drawable.baseline_arrow_back_24_white)
        } else {
            backButton.setBackgroundResource(R.drawable.ic_arrow_back)
        }

        /*        Delete button searches through the database to delete the relevant rows and update the
        relevant tables which references the student
         */
        deleteButton.setOnClickListener {
            val studentId = studentIdInput.text.toString().toInt()
            try {
                val isDeleted = databaseOps.deleteAccount(studentId)

                if (isDeleted) {
                    return@setOnClickListener
                } else {
                    Toast.makeText(mainActivity, "Account Deleted", Toast.LENGTH_SHORT).show()

                    parentFragmentManager.popBackStack()
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error: ${e.message}")
                Toast.makeText(
                    mainActivity,
                    "There was an error deleting the account",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        backButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
        return view
    }

    override fun onDestroyView() {
        db.close()

        super.onDestroyView()
    }


}