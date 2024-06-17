package com.example.oneilassignment2

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView

class MessagesRecyclerViewAdapter(private val messages: List<MessageData>): RecyclerView.Adapter<MessagesRecyclerViewAdapter.ViewHolder>() {
    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val leftCard: CardView = view.findViewById(R.id.message_item_left_card)
        val rightCard: CardView = view.findViewById(R.id.message_item_right_card)
        val leftMessage: TextView = view.findViewById(R.id.message_item_left_text)
        val rightMessage: TextView = view.findViewById(R.id.message_item_right_text)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.message_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (messages[position].isUser) {
            holder.leftCard.visibility = View.INVISIBLE
            holder.rightCard.visibility = View.VISIBLE
            holder.rightMessage.text = messages[position].message
        } else {
            holder.rightCard.visibility = View.INVISIBLE
            holder.leftCard.visibility = View.VISIBLE
            holder.leftMessage.text = messages[position].message
        }


    }

    override fun getItemCount() = messages.size

}