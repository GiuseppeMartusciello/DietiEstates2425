package com.example.dietiestates.ui.viewModel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dietiestates.AppContainer
import com.example.dietiestates.data.model.Client
import kotlinx.coroutines.launch

class ProfileViewModel() : ViewModel() {
    private val _clientState = mutableStateOf(ClientState())
    val clientState: State<ClientState> = _clientState

    init {
        getMe()
    }

    fun getMe() {
        viewModelScope.launch {
            try {
                val client = AppContainer.clientRepository.getMe()

                _clientState.value = _clientState.value.copy(
                    client = client,
                    loading = false,
                    error = null
                )

            } catch (e: Exception) {
                _clientState.value = _clientState.value.copy(
                    loading = false,
                    error = "Error fetching client ${e.message}"
                )
            }
        }
    }

    fun updateNotification(type: String, value: Boolean) {
        val current = _clientState.value

        // calcola nuovo client aggiornato
        val updatedClient = when (type) {
            "promotional" -> current.client.copy(promotionalNotification = value)
            "offer" -> current.client.copy(offerNotification = value)
            "search" -> current.client.copy(searchNotification = value)
            else -> current.client
        }

        // salva vecchio valore per eventuale rollback
        val previousClient = current.client

        // aggiorna localmente subito
        _clientState.value = current.copy(client = updatedClient)

        viewModelScope.launch {
            try {
                AppContainer.clientRepository.updateNotification(type, value)
            } catch (e: Exception) {
                // ROLLBACK se fallisce la chiamata
                _clientState.value = _clientState.value.copy(
                    client = previousClient,
                    error = "Errore aggiornamento preferenza: ${e.message}"
                )
            }
        }
    }


    data class ClientState(
        val loading: Boolean = true,
        val client: Client = Client(),
        val error: String? = null
    )
}
