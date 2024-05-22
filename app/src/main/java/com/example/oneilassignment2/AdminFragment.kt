package com.example.oneilassignment2

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView

class AdminFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_admin, container, false)

//        Store variables for main activity, and other views
        val mainActivity = requireActivity() as MainActivity
        val currentFragment = this

        val mainHeader1 = mainActivity.findViewById<TextView>(R.id.main_header_1)
        val mainHeader2 = mainActivity.findViewById<TextView>(R.id.main_header_2)
        val mainHeader3 = mainActivity.findViewById<TextView>(R.id.main_header_3)
        val mainLoginButton = mainActivity.findViewById<Button>(R.id.main_login_button)
        val mainRegisterButton = mainActivity.findViewById<Button>(R.id.main_register_button)
        val mainBackgroundImage = mainActivity.findViewById<ImageView>(R.id.main_background_image)

        val updatePasswordButton = view.findViewById<Button>(R.id.admin_update_password_button)
        val updateStudentInfoButton = view.findViewById<Button>(R.id.admin_update_info_button)
        val addNewStudentButton = view.findViewById<Button>(R.id.admin_add_student_button)
        val deleteStudentButton = view.findViewById<Button>(R.id.admin_delete_student_button)
        val logoutButton = view.findViewById<Button>(R.id.admin_logout_button)

        updatePasswordButton.setOnClickListener {
            // Handle update password button click
            if (currentFragment.childFragmentManager.findFragmentById(R.id.admin_popup_fragment_container) !is UpdatePasswordFragment) {
                currentFragment.childFragmentManager.beginTransaction()
                    .add(R.id.admin_popup_fragment_container, UpdatePasswordFragment())
                    .addToBackStack(null)
                    .commit()
            }
        }


        updateStudentInfoButton.setOnClickListener {
            // Handle update student info button click
            if (currentFragment.childFragmentManager.findFragmentById(R.id.admin_popup_fragment_container) !is UpdateInfoFragment) {
                currentFragment.childFragmentManager.beginTransaction()
                    .add(R.id.admin_popup_fragment_container, UpdateInfoFragment())
                    .addToBackStack(null)
                    .commit()
            }
        }

        addNewStudentButton.setOnClickListener {
            // Handle add new student button click
            if (currentFragment.childFragmentManager.findFragmentById(R.id.admin_popup_fragment_container) !is AddStudentFragment) {
                currentFragment.childFragmentManager.beginTransaction()
                    .add(R.id.admin_popup_fragment_container, AddStudentFragment())
                    .addToBackStack(null)
                    .commit()
            }
        }

        deleteStudentButton.setOnClickListener {
            // Handle delete student button click
            if (currentFragment.childFragmentManager.findFragmentById(R.id.admin_popup_fragment_container) !is DeleteStudentFragment) {
                currentFragment.childFragmentManager.beginTransaction()
                    .add(R.id.admin_popup_fragment_container, DeleteStudentFragment())
                    .addToBackStack(null)
                    .commit()

            }
        }

        logoutButton.setOnClickListener {
            // Handle logout button click
            mainActivity.supportFragmentManager.popBackStack()

            mainBackgroundImage.visibility = View.VISIBLE

            mainHeader1.visibility = View.VISIBLE
            mainHeader2.visibility = View.VISIBLE
            mainHeader3.visibility = View.VISIBLE
            mainLoginButton.visibility = View.VISIBLE
            mainRegisterButton.visibility = View.VISIBLE
        }

        return view
    }

}