package com.example.oneilassignment2

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.RecyclerView

class HomePostsRecyclerViewAdapter(private val dataset: ArrayList<PostData>, private val postsInterface: HomePostsRecyclerViewInterface) : RecyclerView.Adapter<HomePostsRecyclerViewAdapter.ViewHolder>() {

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val posterName: TextView = view.findViewById(R.id.post_item_author)
        val postCaption: TextView = view.findViewById(R.id.post_item_caption)
        val postLikes: TextView = view.findViewById(R.id.post_item_like_number)
        val postComments: TextView = view.findViewById(R.id.post_item_comment_number)
        val postDate: TextView = view.findViewById(R.id.post_item_date_posted)

        val likeButton: ImageView = view.findViewById(R.id.post_item_like_button)
        val commentButton: ImageView = view.findViewById(R.id.post_item_comment_button)

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.post_item, parent, false)

        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.posterName.text = dataset[position].postersName
        holder.postCaption.text = dataset[position].caption
        holder.postLikes.text = dataset[position].numOfLikes.toString()
        holder.postComments.text = dataset[position].numOfComments.toString()
        holder.postDate.text = "Posted on ${dataset[position].dateAndTime}"

//        Handle UI changes with dark mode
        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            holder.likeButton.setBackgroundResource(if (dataset[position].isLiked) R.drawable.heart_1_ else R.drawable.heart__1_)
            holder.commentButton.setBackgroundResource(R.drawable.comment__1_)
        } else {
            holder.likeButton.setBackgroundResource(if (dataset[position].isLiked) R.drawable.heart_1_ else R.drawable.heart)
            holder.commentButton.setBackgroundResource(R.drawable.comment)
        }

        holder.likeButton.setOnClickListener {
            postsInterface.onLikeButtonClicked(dataset[position], position)
        }
        holder.commentButton.setOnClickListener {
            postsInterface.onCommentButtonClicked(dataset[position], position)
        }
    }

    override fun getItemCount() = dataset.size

//    Update specific item with new PostData
    fun updateItems(newItems: ArrayList<PostData>, position: Int) {
        dataset[position] = newItems[0]
        notifyItemChanged(position)
    }
}