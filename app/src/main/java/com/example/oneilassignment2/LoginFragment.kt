package com.example.oneilassignment2

import android.content.Context.MODE_PRIVATE
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
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.commit

class LoginFragment : Fragment() {

    private lateinit var db: SchoolSQLiteDatabase
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

//        Set variables for main activity, database, and other views
        val mainActivity = requireActivity() as MainActivity
        db = SchoolSQLiteDatabase(mainActivity)

        val view = inflater.inflate(R.layout.fragment_login, container, false)
        val backButton = view.findViewById<ImageButton>(R.id.login_back_button)

        val emailInput = view.findViewById<EditText>(R.id.login_email_input)
        val passwordInput = view.findViewById<EditText>(R.id.login_password_input)
        val loginButton = view.findViewById<Button>(R.id.login_login_button)
        val homeNavBar = mainActivity.findViewById<ConstraintLayout>(R.id.home_navigation_bar)
        val homeTopHeader = mainActivity.findViewById<ConstraintLayout>(R.id.home_top_header)
        val mainBackgroundImage = mainActivity.findViewById<ImageView>(R.id.main_background_image)

        val mainHeader1 = mainActivity.findViewById<TextView>(R.id.main_header_1)
        val mainHeader2 = mainActivity.findViewById<TextView>(R.id.main_header_2)
        val mainHeader3 = mainActivity.findViewById<TextView>(R.id.main_header_3)
        val mainLoginButton = mainActivity.findViewById<Button>(R.id.main_login_button)
        val registerButton = mainActivity.findViewById<Button>(R.id.main_register_button)

        val mainDivider1 = mainActivity.findViewById<View>(R.id.main_divider_1)
        val mainDivider2 = mainActivity.findViewById<View>(R.id.main_divider_2)

//        Handle UI changes based on dark mode
        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            backButton.setBackgroundResource(R.drawable.baseline_arrow_back_24_white)
        } else {
            backButton.setBackgroundResource(R.drawable.ic_arrow_back)
        }

        loginButton.setOnClickListener {
//            Check if email and password is empty or if the email and password is for the admin account
            if (emailInput.text.isEmpty()) {
                emailInput.error = "Email is required"
            } else if (passwordInput.text.isEmpty()) {
                passwordInput.error = "Password is required"
            } else if (emailInput.text.toString() == "admin@school.co.uk" && passwordInput.text.toString() == "admin") {
//                Logs into the admin account if the credentials are correct
                mainActivity.supportFragmentManager.popBackStack()

                mainActivity
                    .supportFragmentManager.commit {
                        setCustomAnimations(
                            R.anim.slide_in_fragment,
                            R.anim.slide_out_fragment,
                            R.anim.slide_in_fragment,
                            R.anim.slide_out_fragment
                        )
                        add(R.id.full_frame_fragment_container, AdminFragment())
                        addToBackStack(null)
                    }

                mainBackgroundImage.visibility = View.GONE
                emailInput.text.clear()
                passwordInput.text.clear()
            } else {
                // TODO: Implement login logic
                val readDb = db.readableDatabase
                val cursor: Cursor?

                val query = "SELECT * FROM students WHERE email = ?"
                cursor = readDb.rawQuery(query, arrayOf(emailInput.text.toString()))

//                Check if the account exists and then log in to the account
                try {
                    if (cursor.count == 0) {
                        emailInput.error = "Incorrect email"
                    } else {
                        try {
                            var studentPassword: String? = null
                            var studentId: Int? = null
                            var firstName: String? = null

                            cursor.moveToFirst()
                            while (!cursor.isAfterLast) {
                                studentPassword =
                                    cursor.getString(cursor.getColumnIndexOrThrow("password"))
                                studentId =
                                    cursor.getInt(cursor.getColumnIndexOrThrow("student_id"))
                                firstName =
                                    cursor.getString(cursor.getColumnIndexOrThrow("first_name"))

                                cursor.moveToNext()
                            }

                            if (studentPassword != passwordInput.text.toString()) {
                                passwordInput.error = "Incorrect password"
                            } else {
                                mainActivity.supportFragmentManager.popBackStack()

                                mainActivity
                                    .supportFragmentManager.commit {
                                        setCustomAnimations(
                                            R.anim.slide_in_fragment,
                                            R.anim.slide_out_fragment,
                                            R.anim.slide_in_fragment,
                                            R.anim.slide_out_fragment
                                        )
                                        replace(R.id.main_fragment_container, HomeFragment())
                                        addToBackStack("HOME_FRAGMENT")
                                    }

//                                Set up shared preference for the user session
                                val userSession =
                                    mainActivity.getSharedPreferences("USER_SESSION", MODE_PRIVATE)
                                val editor = userSession.edit()
                                editor
                                    .putInt("student_id", studentId!!)
                                    .putString("first_name", firstName)
                                    .apply()

                                emailInput.text.clear()
                                passwordInput.text.clear()

                                mainBackgroundImage.visibility = View.GONE
                                homeNavBar.visibility = View.VISIBLE
                                homeTopHeader.visibility = View.VISIBLE

                                mainDivider1.visibility = View.VISIBLE
                                mainDivider2.visibility = View.VISIBLE
                            }
                        } catch (e: Exception) {
                            Log.e("LoginFragment", "Error: ${e.message}")
                            emailInput.error =
                                "Internal application error - Please contact the administrator or developer of the application"
                            passwordInput.error =
                                "Internal application error - Please contact the administrator or developer of the application"
                        }
                    }
                } catch (e: Exception) {
                    Log.e("LoginFragment", "Error: ${e.message}")
                    Toast.makeText(mainActivity, "Error occurred when logging into account", Toast.LENGTH_SHORT).show()
                }
                cursor.close()
                readDb.close()
            }
        }

        backButton.setOnClickListener {
            mainHeader1.visibility = View.VISIBLE
            mainHeader2.visibility = View.VISIBLE
            mainHeader3.visibility = View.VISIBLE
            mainLoginButton.visibility = View.VISIBLE
            registerButton.visibility = View.VISIBLE
            mainActivity.supportFragmentManager.popBackStack()
        }
        return view
    }

    override fun onDestroyView() {
        db.close()
        super.onDestroyView()
    }

}