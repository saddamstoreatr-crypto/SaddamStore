package com.sdstore.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.sdstore.data.repository.RegisterRepository
import kotlinx.coroutines.launch

class RegisterViewModel(application: Application, private val repository: RegisterRepository) : AndroidViewModel(application) {
    val userName = MutableLiveData<String>()
    val outletName = MutableLiveData<String>()
    val location = MutableLiveData<String>()
    val registrationSuccess = MutableLiveData<Boolean>()

    fun saveUserName(name: String) {
        userName.value = name
    }

    fun saveOutletName(outlet: String) {
        outletName.value = outlet
    }

    fun saveRegistrationData() {
        viewModelScope.launch {
            val name = userName.value ?: ""
            val outlet = outletName.value ?: ""
            val loc = location.value ?: "Dummy Location"
            val success = repository.registerUser(name, outlet, loc)
            registrationSuccess.postValue(success)
        }
    }
}