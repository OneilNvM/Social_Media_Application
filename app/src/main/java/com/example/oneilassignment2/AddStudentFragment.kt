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

class AddStudentFragment : Fragment() {
    @SuppressLint("SimpleDateFormat", "SetTextI18n", "ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_add_student, container, false)

//        Touch event set to nothing to prevent interaction with background views whilst this fragment is visible
        view.setOnTouchListener { _, _ ->  return@setOnTouchListener true}

//        Set variables for views, database and main activity
        val mainActivity = requireActivity() as MainActivity
        val db = SchoolSQLiteDatabase(mainActivity)

        val firstNameInput = view.findViewById<EditText>(R.id.admin_add_student_firstname_input)
        val surnameInput = view.findViewById<EditText>(R.id.admin_add_student_surname_input)
        val emailInput = view.findViewById<EditText>(R.id.admin_add_student_email_input)
        val dateOfBirthButton = view.findViewById<Button>(R.id.admin_add_student_dob_button)
        val dateOfBirthText = view.findViewById<TextView>(R.id.admin_add_student_dob_text)
        val passwordInput = view.findViewById<EditText>(R.id.admin_add_student_password_input)
        val addStudentButton = view.findViewById<Button>(R.id.admin_add_student_add_button)
        val backButton = view.findViewById<ImageButton>(R.id.admin_add_student_back_button)

//        Handle UI changes based on dark mode
        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            backButton.setBackgroundResource(R.drawable.baseline_arrow_back_24_white)
        } else {
            backButton.setBackgroundResource(R.drawable.ic_arrow_back)
        }

//        Show date picker dialog in order to select date of birth with the calendar
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

        addStudentButton.setOnClickListener {
//            Check if any of the fields are empty before adding the student
            if (firstNameInput.text.toString().isEmpty() && surnameInput.text.toString().isEmpty() && emailInput.text.toString().isEmpty() && passwordInput.text.toString().isEmpty()) {
                Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            } else {
                if (firstNameInput.text.toString().isEmpty()) {
                    firstNameInput.error = "Please enter a first name"
                } else if (surnameInput.text.toString().isEmpty()) {
                    surnameInput.error = "Please enter a surname"
                } else if (emailInput.text.toString().isEmpty()) {
                    emailInput.error = "Please enter an email"
                }  else if (!emailInput.text.toString().endsWith("@school.co.uk")) {
                    emailInput.error = "Email must end with '@school.co.uk'"
                } else if (dateOfBirthText.text.toString() == getString(R.string.date_of_birth_text)) {
                    Toast.makeText(context, "Please select a date of birth", Toast.LENGTH_SHORT).show()
                } else if (passwordInput.text.toString().isEmpty()) {
                    passwordInput.error = "Please enter a password"
                } else if (passwordInput.text.toString().length < 8) {
                    passwordInput.error = "Password must be at least 8 characters"
                } else {
                    try {
                        val firstName = firstNameInput.text.toString()
                        val surname = surnameInput.text.toString()
                        val email = emailInput.text.toString()
                        val dateOfBirth = dateOfBirthText.text.toString()
                        val password = passwordInput.text.toString()

                        val currentDateTime = Calendar.getInstance().time
                        val formattedDate = SimpleDateFormat("dd/MM/yyyy").format(currentDateTime)

//                        Insert student into database

                        db.insertStudent(
                            firstName,
                            surname,
                            email,
                            dateOfBirth,
                            password,
                            formattedDate
                        )

                        parentFragmentManager.popBackStack()

                        Toast.makeText(context, "Student added successfully", Toast.LENGTH_SHORT)
                            .show()

                        firstNameInput.text.clear()
                        surnameInput.text.clear()
                        emailInput.text.clear()
                        dateOfBirthText.text = getString(R.string.date_of_birth_text)
                        passwordInput.text.clear()
                    } catch (e: Exception) {
                        Toast.makeText(context, "There was an error when trying to add the student", Toast.LENGTH_SHORT).show()
                    }
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