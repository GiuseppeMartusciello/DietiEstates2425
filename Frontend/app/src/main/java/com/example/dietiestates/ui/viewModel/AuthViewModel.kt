package com.example.dietiestates.ui.viewModel

import android.content.Intent
import android.util.Log
import android.util.Patterns

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf

import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dietiestates.AppContainer

import com.example.dietiestates.data.model.PostLoginNavigation

import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    object Success : LoginState()
    data class Error(val message: String) : LoginState()
}

sealed class ChangePasswordState {
    object Idle : ChangePasswordState()
    object Loading : ChangePasswordState()
    object Success : ChangePasswordState()
    data class Error(val message: String) : ChangePasswordState()
}


class AuthViewModel : ViewModel() {
    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState
    val isLoggedIn = mutableStateOf(false)
    val postLoginNavigation = mutableStateOf(PostLoginNavigation.HOME)


    private val _changePasswordState =
        MutableStateFlow<ChangePasswordState>(ChangePasswordState.Idle)
    val changePasswordState: StateFlow<ChangePasswordState> = _changePasswordState

    var email by mutableStateOf("")
        private set
    var password by mutableStateOf("")
        private set
    var passwordVisible by mutableStateOf(false)
        private set

    fun validateInputs(onInvalid: (String) -> Unit): Boolean {
        if (email.isBlank()) {
            onInvalid("Inserisci un'email.")
            return false
        }
//        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
//            onInvalid("L'email inserita non è valida.")
//            return false
//        }
        if (password.isBlank()) {
            onInvalid("Inserisci una password.")
            return false
        }
        return true
    }


    fun onEmailChanged(newEmail: String) {
        email = newEmail
    }

    fun onPasswordChanged(newPassword: String) {
        password = newPassword
    }

    fun togglePasswordVisibility() {
        passwordVisible = !passwordVisible
    }

    fun checkLogin() {
        isLoggedIn.value = AppContainer.tokenManager.isLoggedIn()
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            val result = AppContainer.authRepository.login(email, password)

            _loginState.value = when {
                result.isSuccess -> {
                    isLoggedIn.value = true
                    val authResult = result.getOrNull()
                    postLoginNavigation.value = if (authResult?.mustChangePassword == true) {
                        PostLoginNavigation.CHANGE_PASSWORD
                    } else {
                        PostLoginNavigation.HOME
                    }
                    LoginState.Success


                }

                result.isFailure -> {
                    val message = result.exceptionOrNull()?.message ?: "Errore"
                    LoginState.Error(message)
                }

                else -> LoginState.Error("Errore sconosciuto")
            }

        }
    }

    fun logout() {
        AppContainer.tokenManager.clearSession()
        isLoggedIn.value = false
    }

    fun handleGoogleSignInResult(data: Intent?) {
        val task = GoogleSignIn.getSignedInAccountFromIntent(data)
        try {
            val account = task.getResult(ApiException::class.java)
            val idToken = account.idToken
            if (idToken != null) {
                loginWithGoogle(idToken)
            } else {
                Log.e("GoogleLogin", "ID Token null")
            }
        } catch (e: ApiException) {
            Log.e("GoogleLogin", "Errore Google Sign In: ${e.message}")
        }

    }

    fun loginWithGoogle(idToken: String) {
        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            val result = AppContainer.authRepository.loginWithGoogle(idToken)

            _loginState.value = when {
                result.isSuccess -> {
                    isLoggedIn.value = true
                    LoginState.Success
                }

                result.isFailure -> {
                    val message = result.exceptionOrNull()?.message ?: "Errore sconosciuto"
                    LoginState.Error(message)
                }

                else -> LoginState.Error("Errore sconosciuto")
            }
        }
    }

    fun changePassword(currentPassword: String, newPassword: String) {
        viewModelScope.launch {
            _changePasswordState.value = ChangePasswordState.Loading
            val result = AppContainer.authRepository.changePassword(currentPassword, newPassword)

            _changePasswordState.value = when {
                result.isSuccess -> ChangePasswordState.Success
                result.isFailure -> {
                    val message = result.exceptionOrNull()?.message ?: "Errore"
                    ChangePasswordState.Error(message)
                }

                else -> ChangePasswordState.Error("Errore sconosciuto")
            }
        }
    }


}