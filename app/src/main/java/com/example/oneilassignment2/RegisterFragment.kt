package com.example.oneilassignment2

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.database.Cursor
import android.os.Bundle
import android.util.Log
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

class RegisterFragment : Fragment() {

    private lateinit var db: SchoolSQLiteDatabase
    @SuppressLint("SetTextI18n", "SimpleDateFormat")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_register, container, false)
//        Set variables for main activity, database, and other views
        val mainActivity = requireActivity() as MainActivity

        db = SchoolSQLiteDatabase(mainActivity)

        val firstNameInput = view.findViewById<EditText>(R.id.register_firstname_input)
        val surnameInput = view.findViewById<EditText>(R.id.register_surname_input)
        val emailInput = view.findViewById<EditText>(R.id.register_email_input)
        val dateOfBirthText = view.findViewById<TextView>(R.id.register_dob_text)
        val dateOfBirthButton = view.findViewById<Button>(R.id.register_dob_button)
        val passwordInput = view.findViewById<EditText>(R.id.register_password_input)
        val confirmPasswordInput = view.findViewById<EditText>(R.id.register_c_password_input)
        val registerButton = view.findViewById<Button>(R.id.register_register_button)
        val backButton = view.findViewById<ImageButton>(R.id.register_back_button)

//        Handle UI changes based on dark mode
        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            backButton.setBackgroundResource(R.drawable.baseline_arrow_back_24_white)
        } else {
            backButton.setBackgroundResource(R.drawable.ic_arrow_back)
        }

//        Create date picker dialog when the date of birth button is clicked
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

        registerButton.setOnClickListener {
//            Check all of these conditions before registering the account
            if (firstNameInput.text.isEmpty()) {
                firstNameInput.error = "Please enter your first name"
            } else if (surnameInput.text.isEmpty()) {
                surnameInput.error = "Please enter your surname"
            } else if (emailInput.text.isEmpty()) {
                emailInput.error = "Please enter your email"
            } else if (!emailInput.text.toString().endsWith("@school.co.uk")) {
                emailInput.error = "Email must end with '@school.co.uk'"
            } else if (dateOfBirthText.text == getString(R.string.date_of_birth_text)) {
                Toast.makeText(mainActivity, "Please select your date of birth", Toast.LENGTH_SHORT).show()
            } else if (passwordInput.text.isEmpty()) {
                passwordInput.error = "Please enter your password"
            } else if (confirmPasswordInput.text.isEmpty()) {
                confirmPasswordInput.error = "Please confirm your password"
            } else if (passwordInput.text.toString().length < 8) {
                passwordInput.error = "Password must be at least 8 characters long"
            } else if (passwordInput.text.toString() != confirmPasswordInput.text.toString()) {
                confirmPasswordInput.error = "Passwords do not match"
            } else {
                // Perform registration logic here
                val readDb = db.readableDatabase
                val cursor: Cursor?

                val query = "SELECT * FROM students WHERE email = ?"

                val firstName = firstNameInput.text.toString()
                val surname = surnameInput.text.toString()
                val email = emailInput.text.toString()
                val dateOfBirth = dateOfBirthText.text.toString()
                val password = passwordInput.text.toString()

                cursor = readDb.rawQuery(query, arrayOf(email))

//                Insert the new student into the database if the email is not already taken
                try {
                    if (cursor.count > 0) {
                        emailInput.error = "Email already exists"
                    } else {
                        val currentDateTime = Calendar.getInstance().time
                        val formattedDate = SimpleDateFormat("dd/MM/yyyy").format(currentDateTime)

                        db.insertStudent(
                            firstName,
                            surname,
                            email,
                            dateOfBirth,
                            password,
                            formattedDate
                        )

                        firstNameInput.text.clear()
                        surnameInput.text.clear()
                        emailInput.text.clear()
                        dateOfBirthText.text = getString(R.string.date_of_birth_text)
                        passwordInput.text.clear()
                        confirmPasswordInput.text.clear()

                        mainActivity.supportFragmentManager.popBackStack()

//                        Reloads the login fragment
                        mainActivity.supportFragmentManager
                            .beginTransaction()
                            .replace(R.id.main_fragment_container, LoginFragment())
                            .addToBackStack(null)
                            .commit()
                    }
                } catch (e: Exception) {
                    Log.e("RegisterFragment", "Error: ${e.message}", e)
                    Toast.makeText(mainActivity, "Error occurred trying to register account", Toast.LENGTH_SHORT).show()
                }
                cursor.close()
                readDb.close()
            }
        }

        val mainHeader1 = mainActivity.findViewById<TextView>(R.id.main_header_1)
        val mainHeader2 = mainActivity.findViewById<TextView>(R.id.main_header_2)
        val mainHeader3 = mainActivity.findViewById<TextView>(R.id.main_header_3)
        val loginButton = mainActivity.findViewById<Button>(R.id.main_login_button)
        val mainRegisterButton = mainActivity.findViewById<Button>(R.id.main_register_button)

        backButton.setOnClickListener {
            mainHeader1.visibility = View.VISIBLE
            mainHeader2.visibility = View.VISIBLE
            mainHeader3.visibility = View.VISIBLE
            loginButton.visibility = View.VISIBLE
            mainRegisterButton.visibility = View.VISIBLE

            firstNameInput.text.clear()
            surnameInput.text.clear()
            emailInput.text.clear()
            dateOfBirthText.text = getString(R.string.date_of_birth_text)
            passwordInput.text.clear()
            confirmPasswordInput.text.clear()

            mainActivity
                .supportFragmentManager
                .popBackStack()
        }

        return view
    }

    override fun onDestroyView() {
        db.close()
        super.onDestroyView()
    }
}