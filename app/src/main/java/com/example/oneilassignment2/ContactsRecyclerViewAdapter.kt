package com.example.oneilassignment2

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView

class ContactsRecyclerViewAdapter(private val contacts: ArrayList<ChatData>, private val contactsInterface: ContactsRecyclerViewInterface) : RecyclerView.Adapter<ContactsRecyclerViewAdapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val firstName: TextView = view.findViewById(R.id.contact_item_name)
        val removeButton: ImageButton = view.findViewById(R.id.contact_item_button)
        val previewMessage: TextView = view.findViewById(R.id.contact_item_recent_message)
        val contactCard: CardView = view.findViewById(R.id.contact_item_card)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.contact_item, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("NotifyDataSetChanged", "SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.firstName.text = contacts[position].firstName
        holder.previewMessage.text = if (contacts[position].message == null) "Say hi to ${contacts[position].firstName}" else contacts[position].message

        if (holder.previewMessage.text.length > 18 && contacts[position].message != null) {
            holder.previewMessage.text = holder.previewMessage.text.substring(0, 18) + "..."
        }

        //        Handle UI changes with dark mode
        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_NO) {
            holder.removeButton.setBackgroundResource(R.drawable.baseline_close_24)
        } else {
            holder.removeButton.setBackgroundResource(R.drawable.baseline_close_24_white)
        }

        holder.removeButton.setOnClickListener {
            contactsInterface.onRemoveButtonClicked(contacts[position], position)
        }

        holder.contactCard.setOnClickListener {
            contactsInterface.onContactClicked(contacts[position], position)
        }
    }

    override fun getItemCount() = contacts.size

}