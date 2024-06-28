package com.example.oneilassignment2

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SwitchCompat
import androidx.cardview.widget.CardView
import androidx.core.view.isVisible
import java.text.SimpleDateFormat
import java.util.Calendar

class SettingsActivity : AppCompatActivity() {

    private var isDarkMode: Boolean = false
    private lateinit var db: SchoolSQLiteDatabase

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putBoolean("isDarkMode", isDarkMode)

        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        isDarkMode = savedInstanceState.getBoolean("isDarkMode")

        super.onRestoreInstanceState(savedInstanceState)
    }

    @SuppressLint("SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_settings)

//        Set variables for main activity, database, and other views
        db = SchoolSQLiteDatabase(this)

        val userSession = getSharedPreferences("USER_SESSION", MODE_PRIVATE)
        val userId = userSession.getInt("student_id", 0)

        val darkModeSwitch = findViewById<SwitchCompat>(R.id.settings_toggle_dark_switch)
        val resetPasswordButton = findViewById<Button>(R.id.settings_reset_password_button)
        val deleteAccountButton = findViewById<Button>(R.id.settings_delete_acc_button)
        val closeButton = findViewById<ImageButton>(R.id.settings_close_button)

        val deleteAccountCard = findViewById<CardView>(R.id.settings_delete_acc_card)
        val confirmDelete = findViewById<TextView>(R.id.settings_delete_acc_yes_button)
        val cancelDelete = findViewById<TextView>(R.id.settings_delete_acc_no_button)

        val resetPasswordCard = findViewById<CardView>(R.id.settings_reset_password_card)
        val oldPasswordInput = findViewById<EditText>(R.id.settings_reset_password_old_input)
        val newPasswordInput = findViewById<EditText>(R.id.settings_reset_password_new_input)
        val confirmReset = findViewById<TextView>(R.id.settings_reset_password_confirm_button)
        val cancelReset = findViewById<ImageView>(R.id.settings_reset_password_cancel_button)

        val databaseOps = DatabaseOperations(this)

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                try {
                    if (resetPasswordCard.isVisible) {
                        resetPasswordCard.animate()
                            .alpha(0.0f)
                            .setDuration(300)
                            .setListener(object: AnimatorListenerAdapter() {
                                override fun onAnimationEnd(animation: Animator) {
                                    resetPasswordCard.visibility = View.GONE
                                }
                            })

                        resetPasswordButton.isClickable = true
                        deleteAccountButton.isClickable = true
                        darkModeSwitch.isClickable = true
                        closeButton.isClickable = true
                    } else if (deleteAccountCard.isVisible) {
                        deleteAccountCard.animate()
                            .alpha(0.0f)
                            .setDuration(300)
                            .setListener(object: AnimatorListenerAdapter() {
                                override fun onAnimationEnd(animation: Animator) {
                                    deleteAccountCard.visibility = View.GONE
                                }
                            })

                        resetPasswordButton.isClickable = true
                        deleteAccountButton.isClickable = true
                        darkModeSwitch.isClickable = true
                        closeButton.isClickable = true
                    } else {
                        val intent = Intent(this@SettingsActivity, MainActivity::class.java)

                        startActivity(intent)

                        finish()
                    }
                } catch (e: Exception) {
                    Log.e(ContentValues.TAG, "Error: ${e.message}")
                }
            }

        })

        //        Check the current UI configuration and adjust the value of the dark mode switch
        try {
            if (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES) {
                darkModeSwitch.isChecked = true
                isDarkMode = true
                darkModeSwitch.text = getString(R.string.enabled_text)
            }
            if (isDarkMode) {
                darkModeSwitch.isChecked = true
                darkModeSwitch.text = getString(R.string.enabled_text)
            } else {
                darkModeSwitch.isChecked = false
                darkModeSwitch.text = getString(R.string.disabled_text)
            }
        } catch (e: Exception) {
            Log.e("SettingsActivity", "There was an error when checking UI configuration")
        }

        //        Dark mode switch checks what value the dark mode variable is set to and adjusts the UI accordingly
        darkModeSwitch.setOnClickListener {
            try {
                if (!isDarkMode) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    isDarkMode = true

                    this.recreate()

                    Toast.makeText(this, "Dark Mode Enabled", Toast.LENGTH_SHORT).show()
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    isDarkMode = false

                    this.recreate()

                    Toast.makeText(this, "Dark Mode Disabled", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("SettingsFragment", "There was an error when changing UI configuration")
            }
        }

        //        Handles UI based on dark mode
        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            closeButton.setBackgroundResource(R.drawable.baseline_close_24_white)
            cancelReset.setBackgroundResource(R.drawable.baseline_close_24_white)
        } else {
            closeButton.setBackgroundResource(R.drawable.baseline_close_24)
            cancelReset.setBackgroundResource(R.drawable.baseline_close_24)
        }

        resetPasswordButton.setOnClickListener {
            resetPasswordCard.visibility = View.VISIBLE
            resetPasswordCard.animate()
                .alpha(1.0f)
                .setDuration(300)
                .setListener(object: AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        resetPasswordCard.visibility = View.VISIBLE
                    }
                })

            resetPasswordButton.isClickable = false
            deleteAccountButton.isClickable = false
            darkModeSwitch.isClickable = false
            closeButton.isClickable = false
        }

        //        Confirms the password reset
        confirmReset.setOnClickListener {
            val student = db.retrieveStudent(userId)
            val currentDateTime = Calendar.getInstance().time
            val formattedDate = SimpleDateFormat("dd/MM/yyyy HH:mm").format(currentDateTime)

            try {
                if (student != null) {
                    if (student.count > 0) {
                        student.moveToFirst()

                        val studentPassword =
                            student.getString(student.getColumnIndexOrThrow("password"))

                        val oldPassword = oldPasswordInput.text.toString()
                        val newPassword = newPasswordInput.text.toString()

//                        Checks the conditions before resetting the password
                        if (oldPasswordInput.text.isBlank()) {
                            oldPasswordInput.error = "Please enter your current password"
                        } else if (newPasswordInput.text.isBlank()) {
                            newPasswordInput.error = "Please enter your new password"
                        } else if (oldPassword != studentPassword) {
                            oldPasswordInput.error = "This does not match the current password"
                        } else if (newPasswordInput.text.length < 8) {
                            newPasswordInput.error = "Password must be at least 8 characters"
                        } else {
                            db.updateStudent(
                                userId,
                                null,
                                null,
                                null,
                                null,
                                newPassword,
                                formattedDate
                            )
                            Toast.makeText(this, "Password Updated", Toast.LENGTH_SHORT)
                                .show()

                            resetPasswordCard.visibility = View.GONE

                            oldPasswordInput.text.clear()
                            newPasswordInput.text.clear()

//                            Performs operations to log the user out
                            userSession.edit().clear().apply()

                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)

                            finish()
                        }
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(
                    this,
                    "There was an error updating the password",
                    Toast.LENGTH_SHORT
                ).show()
            }
            student?.close()
        }

        cancelReset.setOnClickListener {
            resetPasswordCard.animate()
                .alpha(0.0f)
                .setDuration(300)
                .setListener(object: AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        resetPasswordCard.visibility = View.GONE
                    }
                })

            resetPasswordButton.isClickable = true
            deleteAccountButton.isClickable = true
            darkModeSwitch.isClickable = true
            closeButton.isClickable = true
        }

        deleteAccountButton.setOnClickListener {
            deleteAccountCard.visibility = View.VISIBLE
            deleteAccountCard.animate()
                .alpha(1.0f)
                .setDuration(300)
                .setListener(object: AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        deleteAccountCard.visibility = View.VISIBLE
                    }
                })

            resetPasswordButton.isClickable = false
            deleteAccountButton.isClickable = false
            darkModeSwitch.isClickable = false
            closeButton.isClickable = false
        }

        //        Confirms the deletion of an account
        confirmDelete.setOnClickListener {
            try {
                val isDeleted = databaseOps.deleteAccount(userId)

                if (!isDeleted) {
                    return@setOnClickListener
                } else {
                    deleteAccountCard.visibility = View.GONE

//                Final operations to log the user out
                    userSession.edit().clear().apply()

                    Toast.makeText(this, "Account Deleted", Toast.LENGTH_SHORT).show()

                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)

                    finish()
                }
            } catch (e: Exception) {
                Toast.makeText(
                    this,
                    "There was an error deleting the account",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        cancelDelete.setOnClickListener {
            deleteAccountCard.animate()
                .alpha(0.0f)
                .setDuration(300)
                .setListener(object: AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        deleteAccountCard.visibility = View.GONE
                    }
                })

            resetPasswordButton.isClickable = true
            deleteAccountButton.isClickable = true
            darkModeSwitch.isClickable = true
            closeButton.isClickable = true
        }

        closeButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)

            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        db.close()
    }
}