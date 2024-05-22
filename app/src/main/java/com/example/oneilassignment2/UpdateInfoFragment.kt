package com.example.oneilassignment2

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import java.text.SimpleDateFormat
import java.util.Calendar

class UpdateInfoFragment : Fragment() {

    @SuppressLint("SimpleDateFormat", "SetTextI18n", "ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_update_info, container, false)
        //        Touch event set to nothing to prevent interaction with background views whilst this fragment is visible
        view.setOnTouchListener { _, _ ->  return@setOnTouchListener true}
//        Set variables for main activity, database and other views
        val mainActivity = requireActivity() as MainActivity
        val db = SchoolSQLiteDatabase(mainActivity)

        val studentIdInput = view.findViewById<EditText>(R.id.admin_update_info_id_input)
        val firstNameInput = view.findViewById<EditText>(R.id.admin_update_info_firstname_input)
        val surnameInput = view.findViewById<EditText>(R.id.admin_update_info_surname_input)
        val emailInput = view.findViewById<EditText>(R.id.admin_update_info_email_input)
        val dateOfBirthButton = view.findViewById<Button>(R.id.admin_update_info_dob_button)
        val dateOfBirthText = view.findViewById<TextView>(R.id.admin_update_info_dob_text)
        val updateButton = view.findViewById<Button>(R.id.admin_update_info_update_button)
        val backButton = view.findViewById<ImageButton>(R.id.admin_update_info_back_button)

//        Handle UI changes based on dark mode
        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            backButton.setBackgroundResource(R.drawable.baseline_arrow_back_24_white)
        } else {
            backButton.setBackgroundResource(R.drawable.ic_arrow_back)
        }

//        Create date picker dialog when date of birth button is clicked
        dateOfBirthButton.setOnClickListener {
            val calendar = Calendar.getInstance()

            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(
                mainActivity,
                { _, selectedYear, selectedMonth, selectedDay ->
                    dateOfBirthText.text =
                        (selectedDay.toString() + "/" + (selectedMonth + 1) + "/" + selectedYear)
                }, year, month, day
            )

            datePickerDialog.datePicker.maxDate = calendar.timeInMillis

            datePickerDialog.show()
        }

//        Update the student info when update button is clicked
        updateButton.setOnClickListener {
            // Handle update button click
            val studentId = studentIdInput.text.toString()
            var firstName: String? = firstNameInput.text.toString()
            var surname: String? = surnameInput.text.toString()
            var email: String? = emailInput.text.toString()
            var dateOfBirth: String? = dateOfBirthText.text.toString()

            val currentDateTime = Calendar.getInstance().time
            val formattedDate = SimpleDateFormat("dd/MM/yyyy HH:mm").format(currentDateTime)

//            Check the conditions before updating the accounts info
            if (studentId.isEmpty()) {
                studentIdInput.error = "Student ID is required"
            } else {
                if (firstName?.isEmpty() == true && surname?.isEmpty() == true && email?.isEmpty() == true && dateOfBirth == getString(R.string.date_of_birth_text)) {
                    Toast.makeText(mainActivity, "Please fill at least one field", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                if (firstName?.isEmpty() == true) {
                    firstName = null
                }
                if (surname?.isEmpty() == true) {
                    surname = null
                }
                if (email?.isEmpty() == true) {
                    email = null
                } else {
                    if (!email!!.endsWith("@school.co.uk")) {
                        emailInput.error = "Email must end with '@school.co.uk'"
                        return@setOnClickListener
                    }
                }
                if (dateOfBirth == getString(R.string.date_of_birth_text)) {
                    dateOfBirth = null
                }

                try {

                    db.updateStudent(
                        studentId.toInt(),
                        firstName,
                        surname,
                        email,
                        dateOfBirth,
                        null,
                        formattedDate
                    )

                    if (firstName != null) {
                        db.updateStudentPosts(studentId.toInt(), firstName, null)
                        db.updateStudentComments(studentId.toInt(), firstName, null)
                    }

                    parentFragmentManager.popBackStack()

                    firstNameInput.text.clear()
                    surnameInput.text.clear()
                    emailInput.text.clear()
                    dateOfBirthText.text = getString(R.string.date_of_birth_text)
                    studentIdInput.text.clear()

                    Toast.makeText(
                        mainActivity,
                        "Student info updated successfully",
                        Toast.LENGTH_SHORT
                    ).show()
                } catch (e: Exception) {
                    Toast.makeText(mainActivity, "Failed to update student info", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }

        db.close()

        backButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        return view
    }

}