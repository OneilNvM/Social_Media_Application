package com.example.oneilassignment2

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PostDataViewModel: ViewModel() {
    private val _numOfCommentsListener = MutableLiveData<Int>()
    private val _isIncreasingListener = MutableLiveData<Boolean>()

    val numOfCommentsListener: LiveData<Int> = _numOfCommentsListener
    val isIncreasing: LiveData<Boolean> = _isIncreasingListener

    var post: PostData? = null

    fun currentValue(cv: Int) {
        _numOfCommentsListener.value = cv
    }

    fun initiateIncrease() {
        _isIncreasingListener.value = true
    }

    fun increaseValue(cv: Int) {
        _numOfCommentsListener.value = cv + 1
    }

    fun increaseComplete() {
        _isIncreasingListener.value = false
    }
}