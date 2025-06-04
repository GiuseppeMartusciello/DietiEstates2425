package com.example.dietiestates.ui.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dietiestates.AppContainer
import com.example.dietiestates.data.model.Listing
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


    init {
        Log.d("DEBUG", "Imizio fetchListings() da ViewModel")
        fetchListings()
    }


    private fun fetchListings() {
        viewModelScope.launch {
            _uiState.value = MyOffersState.Loading
            val result = AppContainer.offerRepository.getListingsByUser()

            _uiState.value = when {
                result.isSuccess -> {
                    val listings = result.getOrNull().orEmpty().map { listing ->
                        listing.copy(imageUrls = listing.imageUrls ?: emptyList())
                    }

                    MyOffersState.Success(listings) // sono già ListingOffer
                }

                result.isFailure -> {
                    Log.e("DEBUG", "Errore: ${result.exceptionOrNull()?.message}")
                    MyOffersState.Error(result.exceptionOrNull()?.message ?: "Errore")
                }

                else -> MyOffersState.Error("Errore sconosciuto")
            }
        }
    }


}