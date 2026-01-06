package com.nikunja.aquariusly.ui.screens.login

data class LoginState(
    val isLoading: Boolean = false,
    val isSigningIn: Boolean = false,
    val isLoggedIn: Boolean = false,
    val error: String? = null,
    val showEmailForm: Boolean = false,
    val email: String = "",
    val password: String = "",
    val isSignUp: Boolean = false
)
