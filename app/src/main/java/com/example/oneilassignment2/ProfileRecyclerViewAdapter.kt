package com.example.oneilassignment2

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.RecyclerView

class ProfileRecyclerViewAdapter(private val posts: ArrayList<PostData>, private val postsInterface: ProfileRecyclerViewInterface) :
    RecyclerView.Adapter<ProfileRecyclerViewAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val posterName: TextView = view.findViewById(R.id.profile_post_item_author)
        val postCaption: TextView = view.findViewById(R.id.profile_post_item_caption)
        val postLikes: TextView = view.findViewById(R.id.profile_post_item_like_number)
        val postComments: TextView = view.findViewById(R.id.profile_post_item_comment_number)
        val postDate: TextView = view.findViewById(R.id.profile_post_item_date_posted)

        val likeButton: ImageButton = view.findViewById(R.id.profile_post_item_like_button)
        val commentButton: ImageButton = view.findViewById(R.id.profile_post_item_comment_button)
        val editButton: ImageButton = view.findViewById(R.id.profile_post_item_edit_post_button)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.profile_post_item, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.posterName.text = posts[position].postersName
        holder.postCaption.text = posts[position].caption
        holder.postLikes.text = posts[position].numOfLikes.toString()
        holder.postComments.text = posts[position].numOfComments.toString()
        holder.postDate.text = posts[position].dateAndTime

//        Handle UI changes based on dark mode
        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_NO) {
            holder.likeButton.setBackgroundResource(if (posts[position].isLiked) R.drawable.heart_1_ else R.drawable.heart)
            holder.commentButton.setBackgroundResource(R.drawable.comment)
            holder.editButton.setBackgroundResource(R.drawable.edit)
        } else {
            holder.likeButton.setBackgroundResource(if (posts[position].isLiked) R.drawable.heart_1_ else R.drawable.heart__1_)
            holder.commentButton.setBackgroundResource(R.drawable.comment__1_)
            holder.editButton.setBackgroundResource(R.drawable.edit__1_)
        }

        holder.likeButton.setOnClickListener {
            postsInterface.onLikeButtonClicked(posts[position], position)
        }
        holder.commentButton.setOnClickListener {
            postsInterface.onCommentButtonClicked(posts[position], position)
        }
        holder.editButton.setOnClickListener {
            postsInterface.onEditButtonClicked(posts[position], position)
        }
    }

    override fun getItemCount() = posts.size

//    Update item in the recycler view
    fun updateItems(newItems: ArrayList<PostData>, position: Int) {
        posts[position] = newItems[0]
        notifyItemChanged(position)
    }

}