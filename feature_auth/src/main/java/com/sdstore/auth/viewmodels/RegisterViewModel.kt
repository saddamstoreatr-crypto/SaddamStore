package com.sdstore.auth.viewmodels

import android.app.Application
import androidx.lifecycle.*
import com.google.firebase.Firebase
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.sdstore.auth.data.RegisterRepository
import com.sdstore.core.R
import com.sdstore.core.data.Result
import com.sdstore.core.viewmodels.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val USER_NAME_KEY = "userName"
private const val OUTLET_NAME_KEY = "outletName"
private const val USER_PHONE_KEY = "userPhone"
private const val USER_LOCATION_KEY = "userLocation"

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val application: Application,
    private val repository: RegisterRepository,
    private val savedStateHandle: SavedStateHandle
) : AndroidViewModel(application) {

    val userName: StateFlow<String> = savedStateHandle.getStateFlow(USER_NAME_KEY, "")
    val outletName: StateFlow<String> = savedStateHandle.getStateFlow(OUTLET_NAME_KEY, "")
    val userPhone: StateFlow<String> = savedStateHandle.getStateFlow(USER_PHONE_KEY, "")
    val location: StateFlow<String> = savedStateHandle.getStateFlow(USER_LOCATION_KEY, "")

    private val _registrationState = MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val registrationState: StateFlow<UiState<Unit>> = _registrationState.asStateFlow()

    fun saveUserName(name: String) {
        savedStateHandle[USER_NAME_KEY] = name
    }

    fun saveOutletName(outlet: String) {
        savedStateHandle[OUTLET_NAME_KEY] = outlet
    }

    fun saveUserPhone(phone: String) {
        savedStateHandle[USER_PHONE_KEY] = phone
    }

    fun saveLocation(loc: String) {
        savedStateHandle[USER_LOCATION_KEY] = loc
    }

    fun saveRegistrationData() {
        _registrationState.value = UiState.Loading
        viewModelScope.launch {
            val name = userName.value
            val outlet = outletName.value
            val loc = location.value
            val phone = userPhone.value.ifEmpty { Firebase.auth.currentUser?.phoneNumber ?: "" }

            if (name.isNotEmpty() && loc.isNotEmpty() && phone.isNotEmpty()) {
                when (repository.registerUser(name, outlet, loc, phone)) {
                    is Result.Success -> _registrationState.value = UiState.Success(Unit)
                    is Result.Error -> _registrationState.value = UiState.Error(application.getString(R.string.registration_failed))
                }
            } else {
                _registrationState.value = UiState.Error(application.getString(R.string.registration_data_missing))
            }
        }
    }

    fun resetRegistrationState() {
        _registrationState.value = UiState.Idle
    }
}