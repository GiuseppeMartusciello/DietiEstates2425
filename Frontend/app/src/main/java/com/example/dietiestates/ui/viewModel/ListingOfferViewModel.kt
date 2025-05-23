package com.example.dietiestates.ui.viewModel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dietiestates.AppContainer
import com.example.dietiestates.data.model.Listing
import com.example.dietiestates.data.model.PropertyOffer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch



data class ListingOffersState(
    val loading: Boolean = true,
    val offers: List<PropertyOffer> = emptyList(),
    val error: String? = null
)

class ListingOfferViewModel(
    private val savedStateHandle: SavedStateHandle
): ViewModel() {
    private val _uiState = mutableStateOf(ListingOffersState())
    val uiState: State<ListingOffersState> = _uiState

    init {

        val listingId = savedStateHandle.get<String>("listingId")
        if (listingId == null) {
            _uiState.value = _uiState.value.copy(
                error = "Listing id non trovato"
            )

        } else {
            fetchOffers(listingId)
        }
    }

    private fun fetchOffers(listingId: String) {
        viewModelScope.launch {
           try {
               val offers = AppContainer.offerRepository.getOffersByListing(listingId = listingId)


               _uiState.value = _uiState.value.copy(
                   offers = offers,
                   loading = false,
                   error = null
               )

           } catch (e: Exception) {
               _uiState.value = _uiState.value.copy(
                   offers = emptyList(),
                   loading = false,
                   error = "Errore nessuna offerta fetchata: ${e.message}"
               )
           }
        }
    }



}

