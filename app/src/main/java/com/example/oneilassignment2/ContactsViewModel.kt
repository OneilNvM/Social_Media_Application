package com.example.oneilassignment2

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ContactsViewModel: ViewModel() {
    private val refreshListener = MutableLiveData<Boolean>()

    val refreshValue: LiveData<Boolean> = refreshListener

    fun refresh() {
        refreshListener.value = true
    }

    fun refreshComplete() {
        refreshListener.value = false
    }


}