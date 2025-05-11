package com.example.dietiestates.ui.viewModel

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dietiestates.AppContainer
import com.example.dietiestates.data.model.Listing
import com.example.dietiestates.data.remote.RetrofitClient
import com.example.dietiestates.data.repository.ListingRepository
import kotlinx.coroutines.launch


class ListingViewModel(    savedStateHandle: SavedStateHandle ) : ViewModel() {
    private val _listingState = mutableStateOf(ListingState())
    val listingState : State<ListingState> = _listingState
    var imageUrls by mutableStateOf<List<String>>(emptyList())
        private set

    init {
        val id = savedStateHandle.get<String>("listingId")
        if (id != null) {
            fetchListing(id)
        }
    }

    data class ListingState(
        val loading: Boolean = true,
        val listing: Listing? = null,
        val error: String? = null
    )

    private fun fetchListing(id: String){
        viewModelScope.launch {
            try {
                val listing = AppContainer.listingRepository.getListing(id)
                val images = AppContainer.listingRepository.getListingImages(id)

                imageUrls = images

                _listingState.value = _listingState.value.copy(
                    listing = listing?.copy(imageUrls = images),
                    loading = false,
                    error = null
                )
            } catch (e: Exception) {
                _listingState.value = _listingState.value.copy(
                    loading = false,
                    error = "Error fetching listing ${e.message}"
                )
            }
        }

    }
}