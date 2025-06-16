package com.example.dietiestates.ui.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dietiestates.AppContainer
import com.example.dietiestates.data.model.Listing
import com.example.dietiestates.utility.TokenManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


sealed class MyOffersState {
    object Loading : MyOffersState()
    data class Success(val listings: List<Listing>) : MyOffersState()
    data class Error(val message: String) : MyOffersState()
}
class MyOfferViewModel: ViewModel(
) {
    private val _uiState = MutableStateFlow<MyOffersState>(MyOffersState.Loading)
    val uiState: StateFlow<MyOffersState> = _uiState

    val userRole = TokenManager.getUserRole()

    init {
        fetchListings()
    }

    private fun fetchListings() {
        viewModelScope.launch {
            _uiState.value = MyOffersState.Loading

            try {
                val listings = if (userRole == "CLIENT") {
                    AppContainer.offerRepository.getListingsByUser().getOrThrow()
                } else {
                    AppContainer.listingRepository.getListings()
                }

                _uiState.value = MyOffersState.Success(listings)
            } catch (e: Exception) {
                _uiState.value = MyOffersState.Error(e.message ?: "Errore")
            }
        }
    }



}