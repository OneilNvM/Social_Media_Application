package com.example.oneilassignment2

interface ContactsRecyclerViewInterface {
    fun onRemoveButtonClicked(contact: ChatData, position: Int)
    fun onContactClicked(contact: ChatData, position: Int)
}