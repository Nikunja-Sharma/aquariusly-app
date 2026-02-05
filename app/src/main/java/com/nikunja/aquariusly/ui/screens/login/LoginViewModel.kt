package com.nikunja.aquariusly.ui.screens.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nikunja.aquariusly.domain.usecase.RegisterFcmTokenUseCase
import com.nikunja.aquariusly.domain.usecase.SignInWithEmailUseCase
import com.nikunja.aquariusly.domain.usecase.SignInWithGoogleUseCase
import com.nikunja.aquariusly.domain.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val signInWithGoogleUseCase: SignInWithGoogleUseCase,
    private val signInWithEmailUseCase: SignInWithEmailUseCase,
    private val registerFcmTokenUseCase: RegisterFcmTokenUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(LoginState())
    val state: StateFlow<LoginState> = _state.asStateFlow()

    fun setSigningIn(signingIn: Boolean) {
        _state.update { it.copy(isSigningIn = signingIn) }
    }

    fun signInWithGoogle(idToken: String) {
        viewModelScope.launch {
            android.util.Log.d("LoginViewModel", "signInWithGoogle called with token: ${idToken.take(20)}...")
            _state.update { it.copy(isLoading = true, error = null) }
            
            when (val result = signInWithGoogleUseCase(idToken)) {
                is Resource.Success -> {
                    android.util.Log.d("LoginViewModel", "Google sign-in successful")
                    registerFcmToken()
                    _state.update { it.copy(isLoading = false, isSigningIn = false, isLoggedIn = true) }
                }
                is Resource.Error -> {
                    android.util.Log.e("LoginViewModel", "Google sign-in error: ${result.message}")
                    _state.update { it.copy(isLoading = false, isSigningIn = false, error = result.message) }
                }
                is Resource.Loading -> {
                    android.util.Log.d("LoginViewModel", "Google sign-in loading")
                    _state.update { it.copy(isLoading = true) }
                }
            }
        }
    }

    fun signInWithFirebaseEmail(firebaseIdToken: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            
            when (val result = signInWithEmailUseCase(firebaseIdToken)) {
                is Resource.Success -> {
                    registerFcmToken()
                    _state.update { it.copy(isLoading = false, isSigningIn = false, isLoggedIn = true) }
                }
                is Resource.Error -> {
                    _state.update { it.copy(isLoading = false, isSigningIn = false, error = result.message) }
                }
                is Resource.Loading -> {
                    _state.update { it.copy(isLoading = true) }
                }
            }
        }
    }

    fun toggleEmailForm() {
        _state.update { it.copy(showEmailForm = !it.showEmailForm, error = null) }
    }

    fun updateEmail(email: String) {
        _state.update { it.copy(email = email) }
    }

    fun updatePassword(password: String) {
        _state.update { it.copy(password = password) }
    }

    fun toggleSignUpMode() {
        _state.update { it.copy(isSignUp = !it.isSignUp, error = null) }
    }

    private fun registerFcmToken() {
        viewModelScope.launch {
            registerFcmTokenUseCase()
        }
    }

    fun clearError() {
        _state.update { it.copy(error = null) }
    }
}
