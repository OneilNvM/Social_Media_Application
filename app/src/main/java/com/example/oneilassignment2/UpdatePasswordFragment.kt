package com.example.oneilassignment2

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import java.text.SimpleDateFormat
import java.util.Calendar

class UpdatePasswordFragment : Fragment() {

    @SuppressLint("SimpleDateFormat", "ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_update_password, container, false)
        view.setOnTouchListener { _, _ ->  return@setOnTouchListener true}
        val mainActivity = requireActivity() as MainActivity
        val db = SchoolSQLiteDatabase(mainActivity)

        val studentIdInput = view.findViewById<EditText>(R.id.admin_update_password_id_input)
        val newPasswordInput = view.findViewById<EditText>(R.id.admin_update_password_input)
        val updatePasswordButton = view.findViewById<Button>(R.id.admin_update_password_update_button)
        val backButton = view.findViewById<ImageButton>(R.id.admin_update_password_back_button)

        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            backButton.setBackgroundResource(R.drawable.baseline_arrow_back_24_white)
        } else {
            backButton.setBackgroundResource(R.drawable.ic_arrow_back)
        }

        updatePasswordButton.setOnClickListener {
            // Handle update password button click
            val studentId = studentIdInput.text.toString()
            val newPassword = newPasswordInput.text.toString()
            val currentDateTime = Calendar.getInstance().time
            val formattedDate = SimpleDateFormat("dd/MM/yyyy HH:mm").format(currentDateTime)
            // Perform update password operation here
            try {
                if (studentId.isEmpty() || newPassword.isEmpty()) {
                    Toast.makeText(mainActivity, "Please fill in both fields", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    db.updateStudent(
                        studentId.toInt(),
                        null,
                        null,
                        null,
                        null,
                        newPassword,
                        formattedDate
                    )
                }
                parentFragmentManager.popBackStack()
                Toast.makeText(mainActivity, "Password updated successfully", Toast.LENGTH_SHORT)
                    .show()
            } catch (e: Exception) {
                Toast.makeText(mainActivity, "There was an error trying to update password", Toast.LENGTH_SHORT).show()
            }
        }

        db.close()

        backButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        return view
    }
}