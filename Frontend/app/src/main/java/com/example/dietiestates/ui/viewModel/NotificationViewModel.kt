package com.example.dietiestates.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dietiestates.AppContainer
import com.example.dietiestates.data.model.Notification
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
    

class NotificationViewModel(
) : ViewModel() {

    private val _uiState = MutableStateFlow<NotificationState>(NotificationState.Loading)
    val uiState: StateFlow<NotificationState> get() = _uiState

    fun loadNotifications() {
        viewModelScope.launch {
            _uiState.value = NotificationState.Loading
            try {
                val result = AppContainer.notificationRepository.notifications()
                _uiState.value = NotificationState.Notifications(result)
            } catch (e: Exception) {
                _uiState.value = NotificationState.Error(e.message ?: "Errore sconosciuto")
            }
        }
    }


    fun markAsRead(userNotificationId: String) {
        viewModelScope.launch {
            try {
                AppContainer.notificationRepository.updateNotification(userNotificationId)
                loadNotifications() // ricarica la lista dopo aggiornamento
            } catch (e: Exception) {
                _uiState.value = NotificationState.Error(e.message ?: "Errore nella marcatura come letta")
            }
        }
    }

}

sealed class NotificationState {
    object Loading : NotificationState()
    data class Notifications(val notifications: List<Notification>) : NotificationState()
    data class Error(val message: String) : NotificationState()
}