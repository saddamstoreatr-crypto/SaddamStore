package com.sdstore.auth.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.sdstore.core.data.repository.RegisterRepository
import com.sdstore.core.data.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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
    var outletName: String? = null
    var location: String? = null
    var userPhone: String? = null

    fun registerUser() {
        if (userName != null && outletName != null && location != null && userPhone != null) {
            _registrationState.value = RegistrationState.Loading
            viewModelScope.launch {
                val result = registerRepository.registerUser(
                    name = userName!!,
                    outletName = outletName!!,
                    location = location!!,
                    phone = userPhone!!
                )
                when (result) {
                    is Result.Success -> _registrationState.value = RegistrationState.Success
                    is Result.Error -> _registrationState.value =
                        RegistrationState.Error(result.exception.message ?: "Registration failed")
                }
            }
        } else {
            _registrationState.value = RegistrationState.Error("User details are incomplete.")
        }
    }

    fun resetState() {
        _registrationState.value = RegistrationState.Idle
    }
}