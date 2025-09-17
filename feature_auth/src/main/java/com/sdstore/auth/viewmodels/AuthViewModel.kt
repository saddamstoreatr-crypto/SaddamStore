package com.sdstore.auth.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.sdstore.core.data.Result
import com.sdstore.core.data.repository.UserRepository // THEEK KIYA GAYA
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    application: Application,
    private val userRepository: UserRepository // THEEK KIYA GAYA
) : AndroidViewModel(application) {

    private val auth: FirebaseAuth = Firebase.auth
    private val db = Firebase.firestore

    // --- Authentication States ---
    sealed class AuthState {
        object Idle : AuthState()
        object Loading : AuthState()
        object Success : AuthState()
        data class Error(val message: String) : AuthState()
    }

    sealed class OtpVerificationState {
        object Loading : OtpVerificationState()
        data class Success(val isNewUser: Boolean) : OtpVerificationState()
        data class Error(val message: String) : OtpVerificationState()
    }

    private val _loginState = MutableStateFlow<AuthState>(AuthState.Idle)
    val loginState: StateFlow<AuthState> = _loginState.asStateFlow()

    private val _signUpState = MutableStateFlow<AuthState>(AuthState.Idle)
    val signUpState: StateFlow<AuthState> = _signUpState.asStateFlow()

    private val _resetEmailState = MutableStateFlow<Boolean?>(null)
    val resetEmailState: StateFlow<Boolean?> = _resetEmailState.asStateFlow()

    private val _otpVerificationState = MutableStateFlow<OtpVerificationState?>(null)
    val otpVerificationState: StateFlow<OtpVerificationState?> = _otpVerificationState.asStateFlow()

    fun loginWithEmail(email: String, password: String) {
        viewModelScope.launch {
            _loginState.value = AuthState.Loading
            try {
                val result = auth.signInWithEmailAndPassword(email, password).await()
                if (result.user?.isEmailVerified == true) {
                    _loginState.value = AuthState.Success
                } else {
                    auth.signOut()
                    _loginState.value = AuthState.Error("Barah karam pehle apna email verify karein.")
                }
            } catch (e: Exception) {
                _loginState.value = AuthState.Error("Login nakaam hua. Email ya password ghalat hai.")
            }
        }
    }

    fun signUpWithEmail(email: String, password: String, name: String) {
        viewModelScope.launch {
            _signUpState.value = AuthState.Loading
            try {
                val result = auth.createUserWithEmailAndPassword(email, password).await()
                val user = result.user
                if (user != null) {
                    user.sendEmailVerification().await()
                    val newUser = mapOf(
                        "uid" to user.uid, "phone" to "", "name" to name, "email" to email,
                        "createdAt" to com.google.firebase.firestore.FieldValue.serverTimestamp()
                    )
                    db.collection("users").document(user.uid).set(newUser).await()
                    _signUpState.value = AuthState.Success
                } else {
                    _signUpState.value = AuthState.Error("Sign up nakaam hua: User is null")
                }
            } catch (e: Exception) {
                _signUpState.value = AuthState.Error(e.localizedMessage ?: "Sign up nakaam hua")
            }
        }
    }

    fun sendPasswordResetEmail(email: String) {
        viewModelScope.launch {
            try {
                auth.sendPasswordResetEmail(email).await()
                _resetEmailState.value = true
            } catch (e: Exception) {
                _resetEmailState.value = false
            }
        }
    }

    fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        _otpVerificationState.value = OtpVerificationState.Loading
        viewModelScope.launch {
            try {
                val authResult = auth.signInWithCredential(credential).await()
                val user = authResult.user
                if (user != null) {
                    when (val result = userRepository.getUser()) {
                        is Result.Success -> {
                            val userData = result.data
                            val isNewUser = (userData == null || userData.name.isEmpty())
                            if (userData == null) {
                                val newUser = mapOf(
                                    "uid" to user.uid,
                                    "phone" to (user.phoneNumber ?: ""),
                                    "name" to "",
                                    "createdAt" to com.google.firebase.firestore.FieldValue.serverTimestamp()
                                )
                                db.collection("users").document(user.uid).set(newUser).await()
                            }
                            _otpVerificationState.value = OtpVerificationState.Success(isNewUser)
                        }
                        is Result.Error -> {
                            _otpVerificationState.value = OtpVerificationState.Error("User data check karne mein masla hua.")
                        }
                    }
                } else {
                    _otpVerificationState.value = OtpVerificationState.Error("Login nakaam hua.")
                }
            } catch (e: Exception) {
                _otpVerificationState.value = OtpVerificationState.Error("OTP ghalat hai. Barah karam dobara koshish karein.")
            }
        }
    }

    // --- State ko reset karne ke liye Functions ---
    fun resetLoginState() {
        _loginState.value = AuthState.Idle
    }

    fun resetSignUpState() {
        _signUpState.value = AuthState.Idle
    }

    fun resetOtpState() {
        _otpVerificationState.value = null
    }

    fun resetPasswordState() {
        _resetEmailState.value = null
    }
}