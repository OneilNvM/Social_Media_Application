package com.example.oneilassignment2

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.cardview.widget.CardView
import androidx.fragment.app.activityViewModels
import java.text.SimpleDateFormat
import java.util.Calendar

class EditPostFragment : Fragment() {

//    Get the ViewModel for PostData
    private val sharedViewModel: PostDataViewModel by activityViewModels()
    private lateinit var db: SchoolSQLiteDatabase

    @SuppressLint("SimpleDateFormat", "ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_edit_post, container, false)

        //        Touch event set to nothing to prevent interaction with background views whilst this fragment is visible
        view.setOnTouchListener { _, _ ->  return@setOnTouchListener true}

//        Set variables for main activity, database and other views
        val mainActivity = requireActivity() as MainActivity
        db = SchoolSQLiteDatabase(mainActivity)

//        Retrieve data from ViewModel
        val postData = sharedViewModel
        val editPostCard = view.findViewById<CardView>(R.id.profile_edit_post_card)
        val editPostCaption = view.findViewById<EditText>(R.id.profile_edit_post_caption_input)
        val confirmEditButton = view.findViewById<Button>(R.id.profile_edit_post_edit_button)
        val cancelEditButton = view.findViewById<ImageButton>(R.id.profile_edit_post_close_button)

//        Handle UI changes with dark mode
        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            cancelEditButton.setBackgroundResource(R.drawable.baseline_close_24_white)
        } else {
            cancelEditButton.setBackgroundResource(R.drawable.baseline_close_24)
        }

//        Set the caption to the current post caption
        editPostCaption.setText(postData.post?.caption)

        editPostCard.visibility = View.VISIBLE

        confirmEditButton.setOnClickListener {
            val currentDateTime = Calendar.getInstance().time
            val formattedDate = SimpleDateFormat("dd/MM/yyyy HH:mm").format(currentDateTime)

//            Update the post with the new caption and update date
            try {
                if (postData.post != null) {
                    val post = postData.post!!
                    db.updatePost(
                        post.postId,
                        editPostCaption.text.toString(),
                        formattedDate,
                        null,
                        null,
                        null
                    )

                    parentFragmentManager.popBackStack()

                    editPostCaption.text.clear()

                    Toast.makeText(mainActivity, "Post edited", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error editing post: ${e.message}")
                Toast.makeText(mainActivity, "There was an error editing your post", Toast.LENGTH_SHORT).show()
            }
        }

        cancelEditButton.setOnClickListener {
            parentFragmentManager.popBackStack()

            editPostCaption.text.clear()
        }

        return view
    }

    override fun onDestroyView() {
        db.close()
        super.onDestroyView()
    }
}