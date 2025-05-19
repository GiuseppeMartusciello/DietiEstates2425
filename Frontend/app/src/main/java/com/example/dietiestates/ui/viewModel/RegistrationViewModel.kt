package com.example.dietiestates.ui.viewModel

import android.util.Patterns
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dietiestates.AppContainer
import com.example.dietiestates.data.model.SignUpRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class RegistrationState {
    object Idle : RegistrationState()
    object Loading : RegistrationState()
    object Success : RegistrationState()
    data class Error(val message: String) : RegistrationState()
}

class RegistrationViewModel: ViewModel() {
    private val _registrationState = MutableStateFlow<RegistrationState>(RegistrationState.Idle)
    val registrationState: StateFlow <RegistrationState> = _registrationState

    val isRegistrated = mutableStateOf(false)

    var name by mutableStateOf("")
        private set
    var surname by mutableStateOf("")
        private set

    var email by mutableStateOf("")
        private set
    var password by mutableStateOf("")
        private set
    var passwordVisible by mutableStateOf(false)
        private set

    var phone by mutableStateOf("")
        private set


    fun validateInputs(onInvalid: (String) -> Unit): Boolean {
        if(name.isBlank()) {
            onInvalid("Inserisci il tuo nome."); return false
        }

        if(surname.isBlank()) {
            onInvalid("Inserisci il tuo cognome."); return false
        }

        if (email.isBlank()) {
            onInvalid("Inserisci un'email.")
            return false
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            onInvalid("L'email inserita non è valida.")
            return false
        }
        if (password.isBlank()) {
            onInvalid("Inserisci una password.")
            return false
        }

        if(phone.isBlank()) {
            onInvalid("Inserisci il tuo numero di telefono.")
            return false
        }

        if(!phone.isDigitsOnly()){
            onInvalid("Inserisci un numero di telefono valido."); return false
        }
        return true
    }

    fun onNameChanged (_name: String) {
        name = _name
    }

    fun onSurnameChanged (_surname: String ) {
        surname = _surname
    }

    fun onPhoneChanged (_phone : String) {
        phone = _phone
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
        isRegistrated.value = AppContainer.tokenManager.isLoggedIn()
    }

    fun register (request: SignUpRequest, onValidationError: (String) -> Unit) {

        if (!validateInputs(onValidationError)) return

        viewModelScope.launch {
            _registrationState.value = RegistrationState.Loading
            val result = AppContainer.authRepository.signUp(request)

            _registrationState.value = when {
                result.isSuccess -> {
                    isRegistrated.value = true
                    RegistrationState.Success
                }
                result.isFailure -> {
                    val message = result.exceptionOrNull()?.message ?: "Errore"
                    RegistrationState.Error(message)
                }
                else -> RegistrationState.Error("Errore sconosciuto")
            }
        }
    }

}

