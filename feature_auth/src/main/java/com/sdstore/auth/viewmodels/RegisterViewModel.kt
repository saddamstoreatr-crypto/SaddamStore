package com.sdstore.auth.viewmodels

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.sdstore.core.data.repository.RegisterRepository
import com.sdstore.core.models.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val application: Application,
    private val registerRepository: RegisterRepository
) : AndroidViewModel(application) {

    sealed class RegistrationState {
        object Idle : RegistrationState()
        object Loading : RegistrationState()
        object Success : RegistrationState()
        data class Error(val message: String) : RegistrationState()
    }

    private val _registrationState = MutableStateFlow<RegistrationState>(RegistrationState.Idle)
    val registrationState: StateFlow<RegistrationState> = _registrationState.asStateFlow()

    // Data held across registration screens
    var userName: String? = null
    var email: String? = null
    var password: String? = null
    var outletName: String? = null
    var location: String? = null
    var userPhone: String? = null
    var imageUri: Uri? = null

    fun saveUserName(name: String) {
        userName = name
    }

    fun saveUserEmailAndPassword(emailValue: String, passwordValue: String) {
        email = emailValue
        password = passwordValue
    }

    fun saveOutletName(name: String) {
        outletName = name
    }

    fun saveUserPhone(phone: String) {
        userPhone = phone
    }

    fun saveLocation(loc: String) {
        location = loc
    }

    fun saveUserImage(uri: Uri) {
        imageUri = uri
    }

    fun saveRegistrationData() {
        if (userName != null && email != null && password != null && outletName != null && location != null && userPhone != null && imageUri != null) {
            _registrationState.value = RegistrationState.Loading
            viewModelScope.launch {
                val user = User(
                    name = userName!!,
                    email = email!!,
                    outletName = outletName!!,
                    location = location!!,
                    phone = userPhone!!
                )
                registerRepository.registerUser(user, password!!, imageUri!!)
                    .catch {
                        _registrationState.value = RegistrationState.Error(it.message ?: "Registration failed")
                    }
                    .collect { success ->
                        if (success) {
                            _registrationState.value = RegistrationState.Success
                        } else {
                            _registrationState.value = RegistrationState.Error("Registration failed")
                        }
                    }
            }
        } else {
            _registrationState.value = RegistrationState.Error("User details are incomplete.")
        }
    }

    fun resetRegistrationState() {
        _registrationState.value = RegistrationState.Idle
    }
}