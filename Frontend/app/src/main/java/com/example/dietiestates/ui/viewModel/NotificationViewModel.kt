package com.example.dietiestates.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dietiestates.data.model.Notification
import com.example.dietiestates.data.repository.NotificationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
    

class NotificationViewModel(
    private val repository: NotificationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<NotificationState>(NotificationState.Loading)
    val uiState: StateFlow<NotificationState> get() = _uiState

    private val _selectedNotification = MutableStateFlow<Notification?>(null)
    val selectedNotification: StateFlow<Notification?> get() = _selectedNotification

    fun loadNotifications() {
        viewModelScope.launch {
            _uiState.value = NotificationState.Loading
            try {
                val result = repository.notifications()
                _uiState.value = NotificationState.Notifications(result)
            } catch (e: Exception) {
                _uiState.value = NotificationState.Error(e.message ?: "Errore sconosciuto")
            }
        }
    }

    fun getNotificationById(id: String) {
        viewModelScope.launch {
            try {
                val notification = repository.getNotificationById(id)
                _selectedNotification.value = notification
            } catch (e: Exception) {
                _selectedNotification.value = null
                _uiState.value = NotificationState.Error(e.message ?: "Errore nel caricamento della notifica")
            }
        }
    }

    fun markAsRead(userNotificationId: String) {
        viewModelScope.launch {
            try {
                repository.markNotificationAsRead(userNotificationId)
                loadNotifications() // ricarica la lista dopo aggiornamento
            } catch (e: Exception) {
                _uiState.value = NotificationState.Error(e.message ?: "Errore nella marcatura come letta")
            }
        }
    }

    fun clearSelectedNotification() {
        _selectedNotification.value = null
    }
}

sealed class NotificationState {
    object Loading : NotificationState()
    data class Notifications(val notifications: List<Notification>) : NotificationState()
    data class Error(val message: String) : NotificationState()
}