package com.example.dietiestates.ui.viewModel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dietiestates.AppContainer
import com.example.dietiestates.data.model.LoginRequest
import com.example.dietiestates.data.remote.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    object Success : LoginState()
    data class Error(val message: String) : LoginState()
}

class AuthViewModel : ViewModel() {
    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState
    val isLoggedIn = mutableStateOf(false)

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
    fun logout() {
        AppContainer.tokenManager.clearToken()
        isLoggedIn.value = false
    }
}