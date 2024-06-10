package com.example.oneilassignment2

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.RecyclerView

class SearchContactRecyclerViewAdapter(private val students: ArrayList<StudentData>, private val searchInterface: SearchContactRecyclerViewInterface) : RecyclerView.Adapter<SearchContactRecyclerViewAdapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val firstName: TextView = view.findViewById(R.id.search_contact_item_name)
        val actionButton: ImageButton = view.findViewById(R.id.search_contact_item_button)
        val addOrRemoveText: TextView = view.findViewById(R.id.search_contact_item_text)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.search_contact_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        holder.firstName.text = students[position].firstName
        holder.addOrRemoveText.text = if (students[position].isAdded) "Remove contact" else "Add as new contact"

        //        Handle UI changes with dark mode
        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_NO) {
            holder.actionButton.setBackgroundResource(if (students[position].isAdded) R.drawable.baseline_close_24 else R.drawable.add)
        } else {
            holder.actionButton.setBackgroundResource(if (students[position].isAdded) R.drawable.baseline_close_24_white else R.drawable.add__1_)
        }

        holder.actionButton.setOnClickListener {
            searchInterface.onActionButtonClicked(students[position], position)
        }
    }

    override fun getItemCount() = students.size

    fun updateItems(newItems: ArrayList<StudentData>, position: Int) {
        students[position] = newItems[0]
        notifyItemChanged(position)
    }
}