package com.example.dietiestates.ui.viewModel

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dietiestates.AppContainer
import com.example.dietiestates.data.model.ClientsOffer
import com.example.dietiestates.data.model.Guest
import com.example.dietiestates.data.model.Listing
import com.example.dietiestates.data.model.PropertyOffer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


//AppContainer.offerRepository.getOffersByListingAgent(listingId = listingId)

data class ClientOffersState(
    val loading: Boolean = true,
    val offers: List<ClientsOffer> = emptyList(),
    val error: String? = null
)


class ClientsOfferViewModel  (savedStateHandle: SavedStateHandle): ViewModel(){

    private val _uiState = MutableStateFlow(ClientOffersState())
    val uiState: StateFlow<ClientOffersState> = _uiState.asStateFlow()

    private val _listing = mutableStateOf<Listing?>(null)
    val listing: State<Listing?> = _listing

    init {
        val listingId = savedStateHandle.get<String>("listingId")

        if (listingId == null) {
            _uiState.value = _uiState.value.copy(error = "ID listing mancante", loading = false)

        } else {
            fetchOffers(listingId)
            fetchListing(listingId)
        }
    }

    private fun fetchOffers(listingId: String) {
        viewModelScope.launch {
            try {

                val offers = AppContainer.offerRepository.getClientByListingAgent(listingId = listingId)


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

    private fun fetchListing(id: String) {
        viewModelScope.launch {
            try {
                val listing = AppContainer.listingRepository.getListing(id)

                _listing.value = listing

            } catch (e: Exception) {
                Log.d("LISTING", "FETCJ LISTING ANDATO MALE")
            }
        }
    }


}