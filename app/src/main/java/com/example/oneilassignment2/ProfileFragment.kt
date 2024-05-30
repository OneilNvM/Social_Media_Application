package com.example.oneilassignment2

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.ContentValues
import android.content.Context.MODE_PRIVATE
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
import androidx.cardview.widget.CardView
import java.text.SimpleDateFormat
import java.util.Calendar

class ProfileFragment : Fragment() {

    @SuppressLint("SetTextI18n", "NotifyDataSetChanged", "SimpleDateFormat")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

//        Set teh variables for the main activity, database and other views
        val mainActivity = requireActivity() as MainActivity
        val db = SchoolSQLiteDatabase(mainActivity)

        val userSession = mainActivity.getSharedPreferences("USER_SESSION", MODE_PRIVATE)
        val userId = userSession.getInt("student_id", 0)
        val userName = userSession.getString("first_name", "")

//        Grab the first name of the student from the shared preference
        val studentName = view.findViewById<TextView>(R.id.profile_student_first_name)
        val joinDate = view.findViewById<TextView>(R.id.profile_student_join_date)

        val profileEditButton = view.findViewById<Button>(R.id.edit_profile_button)
        val profileEditCard = view.findViewById<CardView>(R.id.profile_edit_card)
        val profileEditBackButton = view.findViewById<ImageButton>(R.id.profile_edit_back_button)
        val profileEditSaveButton = view.findViewById<Button>(R.id.profile_edit_update_button)
        val profileEditFirstNameInput = view.findViewById<EditText>(R.id.profile_edit_firstname_input)
        val profileEditSurnameInput = view.findViewById<EditText>(R.id.profile_edit_surname_input)
        val profileEditEmailInput = view.findViewById<EditText>(R.id.profile_edit_email_input)
        val profileEditDateOfBirthButton = view.findViewById<Button>(R.id.profile_edit_dob_button)
        val profileEditDateOfBirthText = view.findViewById<TextView>(R.id.profile_edit_dob_text)
        val profileHomeButton = view.findViewById<Button>(R.id.profile_home_button)
        val profileLikesButton = view.findViewById<Button>(R.id.profile_likes_button)

        parentFragmentManager.beginTransaction()
            .add(R.id.profile_fragment_container, ProfilePostsFragment())
            .commit()

        profileHomeButton.setTextColor(resources.getColor(R.color.magenta, null))

//        Handle UI changes based on dark mode
        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            profileEditBackButton.setBackgroundResource(R.drawable.baseline_arrow_back_24_white)
        } else {
            profileEditBackButton.setBackgroundResource(R.drawable.ic_arrow_back)
        }

        profileEditButton.setOnClickListener {
            profileEditCard.visibility = View.VISIBLE
        }

        profileEditBackButton.setOnClickListener {
            profileEditCard.visibility = View.GONE
        }

        //        Create date picker dialog when date of birth button is clicked
        profileEditDateOfBirthButton.setOnClickListener {
            val calendar = Calendar.getInstance()

            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(
                mainActivity,
                { _, selectedYear, selectedMonth, selectedDay ->
                    profileEditDateOfBirthText.text =
                        (selectedDay.toString() + "/" + (selectedMonth + 1) + "/" + selectedYear)
                }, year, month, day
            )

            datePickerDialog.datePicker.maxDate = calendar.timeInMillis

            datePickerDialog.show()
        }

        profileEditSaveButton.setOnClickListener {
            val currentDateTime = Calendar.getInstance().time
            val formattedDate = SimpleDateFormat("dd/MM/yyyy HH:mm").format(currentDateTime)
            var firstName: String? = profileEditFirstNameInput.text.toString()
            var surname: String? = profileEditSurnameInput.text.toString()
            var email: String? = profileEditEmailInput.text.toString()
            var dateOfBirth: String? = profileEditDateOfBirthText.text.toString()
            //            Check the conditions before updating the accounts info

            if (firstName?.isEmpty() == true && surname?.isEmpty() == true && email?.isEmpty() == true && dateOfBirth == getString(
                    R.string.date_of_birth_text
                )
            ) {
                Toast.makeText(mainActivity, "Please fill at least one field", Toast.LENGTH_SHORT)
                    .show()
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
                    profileEditEmailInput.error = "Email must end with '@school.co.uk'"
                    return@setOnClickListener
                }
            }
            if (dateOfBirth == getString(R.string.date_of_birth_text)) {
                dateOfBirth = null
            }

            try {

                db.updateStudent(
                    userId,
                    firstName,
                    surname,
                    email,
                    dateOfBirth,
                    null,
                    formattedDate
                )

                profileEditCard.visibility = View.GONE

                if (firstName != null) {
                    userSession.edit().putString("first_name", firstName).apply()

                    db.updateStudentPosts(userId, firstName, null)
                    db.updateStudentComments(userId, firstName, null)
                }

                profileEditFirstNameInput.text.clear()
                profileEditSurnameInput.text.clear()
                profileEditEmailInput.text.clear()
                profileEditDateOfBirthText.text = getString(R.string.date_of_birth_text)

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
//        Set the name of the student on the profile to the shared preference name
        studentName.text = userName

        val student = db.retrieveStudent(userId)

//        Set the join date field to the date the account was created
        try {
            if (student != null) {
                if (student.count > 0) {
                    student.moveToFirst()

                    val dateCreated =
                        student.getString(student.getColumnIndexOrThrow("date_created"))

                    joinDate.text = "Joined on $dateCreated"
                }
            }
        } catch (e: Exception) {
            Log.e(ContentValues.TAG, "Cursor error: ${e.message}")
        }
        student?.close()

        profileHomeButton.setOnClickListener {
            if (parentFragmentManager.findFragmentById(R.id.profile_fragment_container) !is ProfilePostsFragment) {
                parentFragmentManager.beginTransaction()
                    .replace(R.id.profile_fragment_container, ProfilePostsFragment())
                    .commit()

                if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
                    profileLikesButton.setTextColor(resources.getColor(R.color.white, null))
                } else {
                    profileLikesButton.setTextColor(resources.getColor(R.color.black, null))
                }

                profileHomeButton.setTextColor(resources.getColor(R.color.magenta, null))
            }
        }

        profileLikesButton.setOnClickListener {
            if (parentFragmentManager.findFragmentById(R.id.profile_fragment_container) !is ProfileLikesFragment) {
                parentFragmentManager.beginTransaction()
                    .replace(R.id.profile_fragment_container, ProfileLikesFragment())
                    .commit()

                if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
                    profileHomeButton.setTextColor(resources.getColor(R.color.white, null))
                } else {
                    profileHomeButton.setTextColor(resources.getColor(R.color.black, null))
                }

                profileLikesButton.setTextColor(resources.getColor(R.color.magenta, null))
            }
        }

        db.close()
        return view
    }
}