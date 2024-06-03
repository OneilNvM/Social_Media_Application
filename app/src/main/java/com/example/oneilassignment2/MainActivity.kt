package com.example.oneilassignment2

import android.Manifest
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE

class MainActivity : AppCompatActivity() {

    private var isDarkMode: Boolean = false

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)


        isDarkMode = savedInstanceState.getBoolean("isDarkMode")

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

//        Check android API version before requesting permission

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), 101)
            }
        }

//        Set variables for views

        val mainHeader1 = findViewById<TextView>(R.id.main_header_1)
        val mainHeader2 = findViewById<TextView>(R.id.main_header_2)
        val mainHeader3 = findViewById<TextView>(R.id.main_header_3)
        val mainLoginButton = findViewById<Button>(R.id.main_login_button)
        val mainRegisterButton = findViewById<Button>(R.id.main_register_button)

        val logoutButton = findViewById<ImageButton>(R.id.home_logout_button)
        val homeButton = findViewById<ImageButton>(R.id.h_home_button)
        val postButton = findViewById<ImageButton>(R.id.home_add_button)
        val profileButton = findViewById<ImageButton>(R.id.home_profile_button)
        val settingsButton = findViewById<ImageButton>(R.id.home_settings_button)
        val chatButton = findViewById<ImageButton>(R.id.home_chat_button)
        val homeTitle = findViewById<TextView>(R.id.home_text_view)
        val mainBackgroundImage = findViewById<ImageView>(R.id.main_background_image)

        val homeNavBar = findViewById<ConstraintLayout>(R.id.home_navigation_bar)
        val homeTopHeader = findViewById<ConstraintLayout>(R.id.home_top_header)
        val mainDivider1 = findViewById<View>(R.id.main_divider_1)
        val mainDivider2 = findViewById<View>(R.id.main_divider_2)

        val loginFragment = LoginFragment()
        val registerFragment = RegisterFragment()

//        Retrieve shared preference for user session
        val userSession = getSharedPreferences("USER_SESSION", MODE_PRIVATE)

//        Set back pressed callback to handle back button press
        onBackPressedDispatcher.addCallback(this, object :
            OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
//                Go back to previous fragment or exit the app if there is no previous fragment
                try {
                    if (supportFragmentManager.findFragmentById(R.id.main_fragment_container) is HomeFragment) {
                        finish()
                    }
                    if (supportFragmentManager.backStackEntryCount > 1) {
                        supportFragmentManager.popBackStackImmediate()
                    } else {
                        finish()
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error: ${e.message}")
                }

//                Adjust UI based on which fragment is currently displayed
                try {
                    when (supportFragmentManager.findFragmentById(R.id.main_fragment_container)) {
                        is HomeFragment -> {
                            if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_NO) {
                                homeButton.setBackgroundResource(R.drawable.house_1_)
                                postButton.setBackgroundResource(R.drawable.add)
                                profileButton.setBackgroundResource(R.drawable.user)
                            } else {
                                homeButton.setBackgroundResource(R.drawable.house_1_)
                                postButton.setBackgroundResource(R.drawable.add__1_)
                                profileButton.setBackgroundResource(R.drawable.user__1_)
                            }
                            homeTitle.text = getString(R.string.home_text)
                            Log.d(TAG, "Reached homefragment in the when statement")
                        }

                        is PostFragment -> {
                            if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_NO) {
                                homeButton.setBackgroundResource(R.drawable.house)
                                postButton.setBackgroundResource(R.drawable.add_1_)
                                profileButton.setBackgroundResource(R.drawable.user)
                            } else {
                                homeButton.setBackgroundResource(R.drawable.house__1_)
                                postButton.setBackgroundResource(R.drawable.add_1_)
                                profileButton.setBackgroundResource(R.drawable.user__1_)
                            }
                            homeTitle.text = getString(R.string.post_text)
                            Log.d(TAG, "Reached postfragment in the when statement")
                        }

                        is ProfileFragment -> {
                            if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_NO) {
                                homeButton.setBackgroundResource(R.drawable.house)
                                postButton.setBackgroundResource(R.drawable.add)
                                profileButton.setBackgroundResource(R.drawable.user_1_)
                            } else {
                                homeButton.setBackgroundResource(R.drawable.house__1_)
                                postButton.setBackgroundResource(R.drawable.add__1_)
                                profileButton.setBackgroundResource(R.drawable.user_1_)
                            }
                            homeTitle.text = getString(R.string.profile_text)
                            Log.d(TAG, "Reached profilefragment in the when statement")
                        }

                        else -> {
                            Log.d(TAG, "Reached else in the when statement")
                        }
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error configuring UI: ${e.message}")
                }
            }
        })

/*        Check if the user session shared preference contains a student_id key, if so, log in to
their account immediately
 */
        if (userSession.contains("student_id")) {
            supportFragmentManager
                .beginTransaction()
                .add(R.id.main_fragment_container, HomeFragment())
                .addToBackStack("HOME_FRAGMENT")
                .commit()

            homeNavBar.visibility = View.VISIBLE
            homeTopHeader.visibility = View.VISIBLE
            mainDivider1.visibility = View.VISIBLE
            mainDivider2.visibility = View.VISIBLE

            mainBackgroundImage.visibility = View.INVISIBLE

            mainHeader1.visibility = View.INVISIBLE
            mainHeader2.visibility = View.INVISIBLE
            mainHeader3.visibility = View.INVISIBLE
            mainLoginButton.visibility = View.INVISIBLE
            mainRegisterButton.visibility = View.INVISIBLE
        }

//        Make the saturation of background image on the main activity 30%
        val colorMatrix = ColorMatrix()

        colorMatrix.setSaturation(0.3f)

        val filter = ColorMatrixColorFilter(colorMatrix)

        mainBackgroundImage.colorFilter = filter

        mainLoginButton.setOnClickListener {
            mainHeader1.visibility = View.INVISIBLE
            mainHeader2.visibility = View.INVISIBLE
            mainHeader3.visibility = View.INVISIBLE
            mainLoginButton.visibility = View.INVISIBLE
            mainRegisterButton.visibility = View.INVISIBLE
            supportFragmentManager
                .beginTransaction()
                .add(R.id.main_fragment_container, loginFragment)
                .addToBackStack(null)
                .commit()
        }

        mainRegisterButton.setOnClickListener {
            mainHeader1.visibility = View.INVISIBLE
            mainHeader2.visibility = View.INVISIBLE
            mainHeader3.visibility = View.INVISIBLE
            mainLoginButton.visibility = View.INVISIBLE
            mainRegisterButton.visibility = View.INVISIBLE
            supportFragmentManager
                .beginTransaction()
                .add(R.id.main_fragment_container, registerFragment)
                .addToBackStack(null)
                .commit()
        }

//        Open the home fragment
        homeButton.setOnClickListener {
//            Checks if the home fragment is already open, if so, do nothing
            if (supportFragmentManager.findFragmentById(R.id.main_fragment_container) !is HomeFragment) {
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.main_fragment_container, HomeFragment())
                    .addToBackStack("HOME_FRAGMENT")
                    .commit()

                homeButton.setBackgroundResource(R.drawable.house_1_)

                homeTitle.text = getString(R.string.home_text)

                if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_NO) {
                    Log.d(TAG, "Day Mode")
                    postButton.setBackgroundResource(R.drawable.add)
                    profileButton.setBackgroundResource(R.drawable.user)
                } else {
                    Log.d(TAG, "Night Mode")
                    postButton.setBackgroundResource(R.drawable.add__1_)
                    profileButton.setBackgroundResource(R.drawable.user__1_)
                }
            }
        }

//        Open the post fragment
        postButton.setOnClickListener {
            //            Checks if the post fragment is already open, if so, do nothing
            if (supportFragmentManager.findFragmentById(R.id.main_fragment_container) !is PostFragment) {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.main_fragment_container, PostFragment())
                    .addToBackStack(null)
                    .commit()

                postButton.setBackgroundResource(R.drawable.add_1_)

                homeTitle.text = getString(R.string.post_text)

                if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_NO) {
                    Log.d(TAG, "Day Mode")
                    homeButton.setBackgroundResource(R.drawable.house)
                    profileButton.setBackgroundResource(R.drawable.user)
                } else {
                    Log.d(TAG, "Night Mode")
                    homeButton.setBackgroundResource(R.drawable.house__1_)
                    profileButton.setBackgroundResource(R.drawable.user__1_)
                }
            }
        }

//        Open the profile fragment
        profileButton.setOnClickListener {
            if (supportFragmentManager.findFragmentById(R.id.main_fragment_container) !is ProfileFragment) {
                //            Checks if the profile fragment is already open, if so, do nothing
                supportFragmentManager.beginTransaction()
                    .replace(R.id.main_fragment_container, ProfileFragment())
                    .addToBackStack(null)
                    .commit()

                profileButton.setBackgroundResource(R.drawable.user_1_)

                homeTitle.text = getString(R.string.profile_text)

                if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_NO) {
                    Log.d(TAG, "Day Mode")
                    homeButton.setBackgroundResource(R.drawable.house)
                    postButton.setBackgroundResource(R.drawable.add)
                } else {
                    Log.d(TAG, "Night Mode")
                    homeButton.setBackgroundResource(R.drawable.house__1_)
                    postButton.setBackgroundResource(R.drawable.add__1_)
                }
            }
        }

/*        Logout button removes all fragments from the backstack, clears the shared preference,
and restores the main activity views
 */
        logoutButton.setOnClickListener {
            supportFragmentManager.popBackStack(null, POP_BACK_STACK_INCLUSIVE)

            userSession.edit().clear().apply()

            mainHeader1.visibility = View.VISIBLE
            mainHeader2.visibility = View.VISIBLE
            mainHeader3.visibility = View.VISIBLE
            mainLoginButton.visibility = View.VISIBLE
            mainRegisterButton.visibility = View.VISIBLE

            if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_NO) {
                Log.d(TAG, "Day Mode")
                homeButton.setBackgroundResource(R.drawable.house_1_)
                postButton.setBackgroundResource(R.drawable.add)
                profileButton.setBackgroundResource(R.drawable.user)
            } else {
                Log.d(TAG, "Night Mode")
                homeButton.setBackgroundResource(R.drawable.house_1_)
                postButton.setBackgroundResource(R.drawable.add__1_)
                profileButton.setBackgroundResource(R.drawable.user__1_)
            }

            homeTitle.text = getString(R.string.home_text)

            mainBackgroundImage.visibility = View.VISIBLE
            homeNavBar.visibility = View.GONE
            homeTopHeader.visibility = View.GONE
            mainDivider1.visibility = View.GONE
            mainDivider2.visibility = View.GONE
        }

//        Opens the settings fragment
        settingsButton.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)

            finish()
        }

        chatButton.setOnClickListener {
            val intent = Intent(this, ChatActivity::class.java)
            startActivity(intent)

            finish()
        }
    }

//    onStart checks the current system UI configuration and changes the UI accordingly
    override fun onStart() {
        super.onStart()

        val logoutButton = findViewById<ImageButton>(R.id.home_logout_button)
        val postButton = findViewById<ImageButton>(R.id.home_add_button)
        val profileButton = findViewById<ImageButton>(R.id.home_profile_button)
        val settingsButton = findViewById<ImageButton>(R.id.home_settings_button)

        if (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_NO) {
            Log.d(TAG, "Day Mode")
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            postButton.setBackgroundResource(R.drawable.add)
            profileButton.setBackgroundResource(R.drawable.user)
            logoutButton.setBackgroundResource(R.drawable.exit)
            settingsButton.setBackgroundResource(R.drawable.setting)
        } else {
            Log.d(TAG, "Night Mode")
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            postButton.setBackgroundResource(R.drawable.add__1_)
            profileButton.setBackgroundResource(R.drawable.user__1_)
            logoutButton.setBackgroundResource(R.drawable.exit__1_)
            settingsButton.setBackgroundResource(R.drawable.setting__1_)
        }
    }
}