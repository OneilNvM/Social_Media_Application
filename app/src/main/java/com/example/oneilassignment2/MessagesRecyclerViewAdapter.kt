package com.example.oneilassignment2

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView

class MessagesRecyclerViewAdapter(private val messages: ArrayList<MessageData>): RecyclerView.Adapter<MessagesRecyclerViewAdapter.ViewHolder>() {
    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val leftCard: CardView = view.findViewById(R.id.message_item_left_card)
        val rightCard: CardView = view.findViewById(R.id.message_item_right_card)
        val leftMessage: TextView = view.findViewById(R.id.message_item_left_text)
        val rightMessage: TextView = view.findViewById(R.id.message_item_right_text)
        val dateTimeLeft: TextView = view.findViewById(R.id.message_item_date_time_left)
        val dateTimeRight: TextView = view.findViewById(R.id.message_item_date_time_right)
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
            holder.dateTimeLeft.visibility = View.INVISIBLE
            holder.rightCard.visibility = View.VISIBLE
            holder.dateTimeRight.visibility = View.VISIBLE
            holder.rightMessage.text = messages[position].message
            holder.dateTimeRight.text = messages[position].date
        } else {
            holder.rightCard.visibility = View.INVISIBLE
            holder.dateTimeRight.visibility = View.INVISIBLE
            holder.leftCard.visibility = View.VISIBLE
            holder.dateTimeLeft.visibility = View.VISIBLE
            holder.leftMessage.text = messages[position].message
            holder.dateTimeLeft.text = messages[position].date
        }


    }

    override fun getItemCount() = messages.size

    fun insertItem(message: MessageData) {
        messages.add(message)
        notifyItemInserted(messages.size - 1)
    }



}