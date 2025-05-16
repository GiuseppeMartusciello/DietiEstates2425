package com.example.dietiestates.ui.viewModel

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dietiestates.AppContainer
import com.example.dietiestates.data.model.Listing
import com.example.dietiestates.data.remote.RetrofitClient
import com.example.dietiestates.data.repository.ListingRepository
import kotlinx.coroutines.launch


class HomeViewModel : ViewModel() {
    private val _listingState = mutableStateOf(ListingState())
    val listingState : State<ListingState> = _listingState

    init {
        fetchListing()
    }

    data class ListingState(
        val loading: Boolean = true,
        val list: List<Listing> = emptyList(),
        val error: String? = null
    )

    private fun fetchListing(){
        viewModelScope.launch {
            try{
                val listings = AppContainer.listingRepository.getListings()
                _listingState.value = _listingState.value.copy(
                    list = listings,
                    loading = false,
                    error = null
                )
            }catch (e: Exception){
                _listingState.value = _listingState.value.copy(
                    loading = false,
                    error = "Error fetching listings ${e.message}"
                )
            }
        }
    }
}