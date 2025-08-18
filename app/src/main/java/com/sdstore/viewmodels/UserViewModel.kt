package com.sdstore.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.sdstore.data.repository.UserRepository
import com.sdstore.models.User
import kotlinx.coroutines.launch

class UserViewModel(application: Application, private val repository: UserRepository) : AndroidViewModel(application) {
    private val _user = MutableLiveData<User?>()
    val user: LiveData<User?> = _user

    init { fetchUser() }

    fun fetchUser() {
        viewModelScope.launch { _user.postValue(repository.getUser()) }
    }
}