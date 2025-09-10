package com.sdstore.orders.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.sdstore.R
import com.sdstore.core.data.Result
import com.sdstore.orders.data.FeedbackRepository
import com.sdstore.core.models.User
import com.sdstore.core.viewmodels.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val application: Application,
    private val userRepository: UserRepository,
    private val feedbackRepository: FeedbackRepository
) : AndroidViewModel(application) {

    private val _userState = MutableStateFlow<UiState<User>>(UiState.Loading)
    val userState: StateFlow<UiState<User>> = _userState.asStateFlow()

    private val _profileUpdateStatus = MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val profileUpdateStatus: StateFlow<UiState<Unit>> = _profileUpdateStatus.asStateFlow()

    private val _feedbackStatus = MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val feedbackStatus: StateFlow<UiState<Unit>> = _feedbackStatus.asStateFlow()

    private val _loggedOutEvent = MutableStateFlow(false)
    val loggedOutEvent: StateFlow<Boolean> = _loggedOutEvent.asStateFlow()

    init {
        fetchUser()
    }

    private fun fetchUser() {
        _userState.value = UiState.Loading
        viewModelScope.launch {
            when (val result = userRepository.getUser()) {
                is Result.Success -> {
                    result.data?.let {
                        _userState.value = UiState.Success(it)
                    } ?: run {
                        _userState.value = UiState.Error(application.getString(R.string.error_loading_profile))
                    }
                }
                is Result.Error -> {
                    _userState.value = UiState.Error(application.getString(R.string.error_loading_profile))
                }
            }
        }
    }

    suspend fun checkUserStatus(): Result<User?> {
        return userRepository.getUser()
    }

    fun updateUserProfile(name: String, outletName: String) {
        _profileUpdateStatus.value = UiState.Loading
        viewModelScope.launch {
            when (userRepository.updateUserProfile(name, outletName)) {
                is Result.Success -> {
                    _profileUpdateStatus.value = UiState.Success(Unit)
                    fetchUser()
                }
                is Result.Error -> {
                    _profileUpdateStatus.value = UiState.Error(application.getString(R.string.profile_update_failed))
                }
            }
        }
    }

    fun submitFeedback(feedbackText: String) {
        _feedbackStatus.value = UiState.Loading
        viewModelScope.launch {
            when(feedbackRepository.submitFeedback(feedbackText)){
                is Result.Success -> _feedbackStatus.value = UiState.Success(Unit)
                is Result.Error -> _feedbackStatus.value = UiState.Error(application.getString(R.string.feedback_submit_failed))
            }
        }
    }

    fun logout() {
        Firebase.auth.signOut()
        _loggedOutEvent.value = true
    }

    fun onLogoutEventHandled() {
        _loggedOutEvent.value = false
    }

    fun resetProfileUpdateStatus() {
        _profileUpdateStatus.value = UiState.Idle
    }

    fun resetFeedbackStatus() {
        _feedbackStatus.value = UiState.Idle
    }
}