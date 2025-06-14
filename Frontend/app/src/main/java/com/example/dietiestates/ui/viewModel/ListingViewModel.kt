package com.example.dietiestates.ui.viewModel

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dietiestates.AppContainer
import com.example.dietiestates.data.model.Agent
import com.example.dietiestates.data.model.Listing
import kotlinx.coroutines.launch


class ListingViewModel(savedStateHandle: SavedStateHandle) : ViewModel() {
    private val _listingState = mutableStateOf(ListingState())
    val listingState: State<ListingState> = _listingState

    init {
        val id = savedStateHandle.get<String>("listingId")
        if (id != null) {
            fetchListing(id)
            fetchAgent(id)
        }
    }

    private fun fetchListing(id: String) {
        viewModelScope.launch {
            try {
                val listing = AppContainer.listingRepository.getListing(id)

                _listingState.value = _listingState.value.copy(
                    listing = listing,
                    loadingListing = false,
                    error = null
                )

            } catch (e: Exception) {
                _listingState.value = _listingState.value.copy(
                    loadingListing = false,
                    error = "Error fetching listing ${e.message}"
                )
            }
        }
    }

    private fun fetchAgent(id: String) {
        viewModelScope.launch {
            try {
                val agent = AppContainer.listingRepository.getAgentOfListing(id)

                _listingState.value = _listingState.value.copy(
                    agent = agent,
                    loadingAgent = false,
                    error = null
                )
            } catch (e: Exception) {
                _listingState.value = _listingState.value.copy(
                    loadingAgent = false,
                    error = "Error fetching agent ${e.message}"
                )
            }
        }
    }


    data class ListingState(
        val loadingListing: Boolean = true,
        val loadingAgent: Boolean = true,
        val listing: Listing? = null,
        val agent: Agent? = null,
        val error: String? = null
    )

}