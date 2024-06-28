package com.example.oneilassignment2

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.RecyclerView

class CommentsRecyclerViewAdapter(private val comments: ArrayList<CommentData>, private val commentsInterface: CommentsRecyclerViewInterface): RecyclerView.Adapter<CommentsRecyclerViewAdapter.CommentsViewHolder>() {

    class CommentsViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val commenterName: TextView = view.findViewById(R.id.comment_item_commenter_name)
        val commentDate: TextView = view.findViewById(R.id.comment_item_date)
        val commentText: TextView = view.findViewById(R.id.comment_item_comment_text)
        val numLikes: TextView = view.findViewById(R.id.comment_item_likes_no)
        val numDislikes: TextView = view.findViewById(R.id.comment_item_dislikes_no)

        val likeButton: ImageView = view.findViewById(R.id.comment_item_like_button)
        val dislikeButton: ImageView = view.findViewById(R.id.comment_item_dislike_button)

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CommentsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.comment_item, parent, false)

        return CommentsViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: CommentsViewHolder,
        position: Int
    ) {
        holder.commenterName.text = comments[position].commenterName
        holder.commentDate.text = comments[position].commentDate
        holder.commentText.text = comments[position].commentText
        holder.numLikes.text = comments[position].likes.toString()
        holder.numDislikes.text = comments[position].dislikes.toString()

//        Handle UI changes with dark mode
        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_NO) {
            holder.likeButton.setBackgroundResource(if (comments[position].isLiked) R.drawable.thumbs_up__1_ else R.drawable.thumbs)
            holder.dislikeButton.setBackgroundResource(if (comments[position].isDisliked) R.drawable.thumbs_down__1_ else R.drawable.thumbs_down)
        } else {
            holder.likeButton.setBackgroundResource(if (comments[position].isLiked) R.drawable.thumbs_up__1_ else R.drawable.thumbs_up__2_)
            holder.dislikeButton.setBackgroundResource(if (comments[position].isDisliked) R.drawable.thumbs_down__1_ else R.drawable.thumbs_down__2_)
        }

        holder.likeButton.setOnClickListener {
            commentsInterface.onLikeButtonClicked(comments[position], position)
        }
        holder.dislikeButton.setOnClickListener {
            commentsInterface.onDislikeButtonClicked(comments[position], position)
        }

    }

    override fun getItemCount() = comments.size

//    Update function which updates a specific item in the RecyclerView
    fun updateItem(newComment: CommentData, position: Int) {
        comments[position] = newComment
        notifyItemChanged(position)
    }

    fun insertItem(newComment: CommentData) {
        comments.add(0, newComment)
        notifyItemInserted(0)
    }

}