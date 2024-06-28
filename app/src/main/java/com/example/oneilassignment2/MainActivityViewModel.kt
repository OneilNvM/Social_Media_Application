package com.example.oneilassignment2

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainActivityViewModel: ViewModel() {
    private val profileEditListener = MutableLiveData<Boolean>()
    private val editCardVisibilityListener = MutableLiveData<Boolean>()

    val isEditCardVisible: LiveData<Boolean> = editCardVisibilityListener
    val profileEditValue: LiveData<Boolean> = profileEditListener

    fun editCardDisplayed() {
        editCardVisibilityListener.value = true
    }

    fun editCardHidden() {
        editCardVisibilityListener.value = false
    }

    fun hideEditCard() {
        profileEditListener.value = true
    }

    fun hideEditCardComplete() {
        profileEditListener.value = false
        editCardVisibilityListener.value = false
    }
}