package com.example.oneilassignment2

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.ContentValues.TAG
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
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.activityViewModels
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import java.text.SimpleDateFormat
import java.util.Calendar

private const val NUM_PAGES = 2

class ProfileFragment : Fragment() {

    private lateinit var profileLikesButton: Button
    private lateinit var profileHomeButton: Button

    private lateinit var profilePostsFragment: ProfilePostsFragment
    private lateinit var profileLikesFragment: ProfileLikesFragment

    private lateinit var viewPager: ViewPager2
    private lateinit var db: SchoolSQLiteDatabase
    private val mainActivityViewModel: MainActivityViewModel by activityViewModels()

    @SuppressLint(
        "SetTextI18n", "NotifyDataSetChanged", "SimpleDateFormat",
        "ClickableViewAccessibility"
    )
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

//        Set teh variables for the main activity, database and other views
        val mainActivity = requireActivity() as MainActivity
        db = SchoolSQLiteDatabase(mainActivity)

        val userSession = mainActivity.getSharedPreferences("USER_SESSION", MODE_PRIVATE)
        val userId = userSession.getInt("student_id", 0)
        val userName = userSession.getString("first_name", "")

        val chatButton = mainActivity.findViewById<ImageButton>(R.id.home_chat_button)
        val settingsButton = mainActivity.findViewById<ImageButton>(R.id.home_settings_button)
        val homeButton = mainActivity.findViewById<ImageButton>(R.id.h_home_button)
        val postButton = mainActivity.findViewById<ImageButton>(R.id.home_add_button)
        val profileButton = mainActivity.findViewById<ImageButton>(R.id.home_profile_button)
        val logoutButton = mainActivity.findViewById<ImageButton>(R.id.home_logout_button)

//        Grab the first name of the student from the shared preference
        val studentName = view.findViewById<TextView>(R.id.profile_student_first_name)
        val joinDate = view.findViewById<TextView>(R.id.profile_student_join_date)

        val profileEditButton = view.findViewById<Button>(R.id.edit_profile_button)
        val profileEditCard = view.findViewById<CardView>(R.id.profile_edit_card)
        val profileEditBackButton = view.findViewById<ImageButton>(R.id.profile_edit_back_button)
        val profileEditSaveButton = view.findViewById<Button>(R.id.profile_edit_update_button)
        val profileEditFirstNameInput =
            view.findViewById<EditText>(R.id.profile_edit_firstname_input)
        val profileEditSurnameInput = view.findViewById<EditText>(R.id.profile_edit_surname_input)
        val profileEditEmailInput = view.findViewById<EditText>(R.id.profile_edit_email_input)
        val profileEditDateOfBirthButton = view.findViewById<Button>(R.id.profile_edit_dob_button)
        val profileEditDateOfBirthText = view.findViewById<TextView>(R.id.profile_edit_dob_text)

        profileHomeButton = view.findViewById(R.id.profile_home_button)
        profileLikesButton = view.findViewById(R.id.profile_likes_button)

        profilePostsFragment = ProfilePostsFragment()
        profileLikesFragment = ProfileLikesFragment()

        viewPager = view.findViewById(R.id.profile_viewpager2)

        val viewPagerAdapter = ProfileAdapter(requireActivity())

        viewPager.adapter = viewPagerAdapter

        viewPager.registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)

                val currentFragment = viewPagerAdapter.createFragment(position)

                when (currentFragment) {
                    profilePostsFragment -> {
                        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
                            profileLikesButton.setTextColor(resources.getColor(R.color.white, null))
                        } else {
                            profileLikesButton.setTextColor(resources.getColor(R.color.black, null))
                        }
                        profileHomeButton.setTextColor(resources.getColor(R.color.magenta, null))
                    }
                    profileLikesFragment -> {
                        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
                            profileHomeButton.setTextColor(resources.getColor(R.color.white, null))
                        } else {
                            profileHomeButton.setTextColor(resources.getColor(R.color.black, null))
                        }
                        profileLikesButton.setTextColor(resources.getColor(R.color.magenta, null))
                    }
                    else -> {
                        throw IllegalStateException("Invalid position: $position")
                    }
                }
            }
        })

        profileHomeButton.setTextColor(resources.getColor(R.color.magenta, null))

        mainActivityViewModel.profileEditValue.observe(mainActivity) { currentValue ->

            if (currentValue) {
                profileEditCard.animate()
                    .alpha(0.0f)
                    .setDuration(300)
                    .setListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator) {
                            profileEditCard.visibility = View.INVISIBLE
                            mainActivityViewModel.hideEditCardComplete()
                        }
                    })

                chatButton.isClickable = true
                settingsButton.isClickable = true
                homeButton.isClickable = true
                postButton.isClickable = true
                profileButton.isClickable = true
                logoutButton.isClickable = true
                profileLikesButton.isClickable = true
                profileHomeButton.isClickable = true
            }
        }

//        Handle UI changes based on dark mode
        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            profileEditBackButton.setBackgroundResource(R.drawable.baseline_arrow_back_24_white)
        } else {
            profileEditBackButton.setBackgroundResource(R.drawable.ic_arrow_back)
        }

        profileEditButton.setOnClickListener {
            mainActivityViewModel.editCardDisplayed()

            profileEditCard.visibility = View.VISIBLE
            profileEditCard.animate()
                .alpha(1.0f)
                .setDuration(300)
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        profileEditCard.visibility = View.VISIBLE
                    }
                })

            chatButton.isClickable = false
            settingsButton.isClickable = false
            homeButton.isClickable = false
            postButton.isClickable = false
            profileButton.isClickable = false
            logoutButton.isClickable = false
            profileLikesButton.isClickable = false
            profileHomeButton.isClickable = false
            viewPager.visibility = View.GONE
        }

        profileEditBackButton.setOnClickListener {
            mainActivityViewModel.editCardHidden()
            profileEditCard.animate()
                .alpha(0.0f)
                .setDuration(300)
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        profileEditCard.visibility = View.INVISIBLE
                    }
                })

            chatButton.isClickable = true
            settingsButton.isClickable = true
            homeButton.isClickable = true
            postButton.isClickable = true
            profileButton.isClickable = true
            logoutButton.isClickable = true
            profileLikesButton.isClickable = true
            profileHomeButton.isClickable = true
            viewPager.visibility = View.VISIBLE
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

            if (firstName?.isBlank() == true && surname?.isBlank() == true && email?.isBlank() == true && dateOfBirth == getString(
                    R.string.date_of_birth_text
                )
            ) {
                Toast.makeText(mainActivity, "Please fill at least one field", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }
            if (firstName?.isBlank() == true) {
                firstName = null
            }
            if (surname?.isBlank() == true) {
                surname = null
            }
            if (email?.isBlank() == true) {
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

                profileEditCard.animate()
                    .alpha(0.0f)
                    .setDuration(300)
                    .setListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator) {
                            profileEditCard.visibility = View.INVISIBLE
                        }
                    })

                if (firstName != null) {
                    userSession.edit().putString("first_name", firstName).apply()

                    db.updateStudentPosts(userId, firstName, null)
                    db.updateStudentComments(userId, firstName, null)
                }

                profileEditFirstNameInput.text.clear()
                profileEditSurnameInput.text.clear()
                profileEditEmailInput.text.clear()
                profileEditDateOfBirthText.text = getString(R.string.date_of_birth_text)

                chatButton.isClickable = true
                settingsButton.isClickable = true
                homeButton.isClickable = true
                postButton.isClickable = true
                profileButton.isClickable = true
                logoutButton.isClickable = true
                profileLikesButton.isClickable = true
                profileHomeButton.isClickable = true

                mainActivityViewModel.editCardHidden()

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
            Log.e(TAG, "Cursor error: ${e.message}")
        }
        student?.close()

        profileHomeButton.setOnClickListener {
            if (viewPager.currentItem == 1) {
                if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
                    profileLikesButton.setTextColor(resources.getColor(R.color.white, null))
                } else {
                    profileLikesButton.setTextColor(resources.getColor(R.color.black, null))
                }
                viewPager.currentItem = 0
                profileHomeButton.setTextColor(resources.getColor(R.color.magenta, null))
            }
        }

        profileLikesButton.setOnClickListener {
            if (viewPager.currentItem == 0) {
                if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
                    profileHomeButton.setTextColor(resources.getColor(R.color.white, null))
                } else {
                    profileHomeButton.setTextColor(resources.getColor(R.color.black, null))
                }

                viewPager.currentItem = 1
                profileLikesButton.setTextColor(resources.getColor(R.color.magenta, null))
            }
        }

        return view
    }

    private inner class ProfileAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {
        override fun getItemCount(): Int = NUM_PAGES

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> profilePostsFragment

                1 -> profileLikesFragment

                else -> throw IllegalStateException("Invalid position: $position")
            }
        }

    }

    override fun onDestroyView() {
        db.close()
        super.onDestroyView()
    }
}