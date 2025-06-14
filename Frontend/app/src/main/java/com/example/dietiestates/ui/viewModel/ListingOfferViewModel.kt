package com.example.dietiestates.ui.viewModel

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dietiestates.AppContainer
import com.example.dietiestates.data.model.Guest
import com.example.dietiestates.data.model.Listing
import com.example.dietiestates.data.model.PropertyOffer
import com.example.dietiestates.utility.TokenManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


data class ListingOffersState(
    val loading: Boolean = true,
    val offers: List<PropertyOffer> = emptyList(),
    val error: String? = null
)

class ListingOfferViewModel(
    savedStateHandle: SavedStateHandle
) : ViewModel() {
//    private val _uiState = mutableStateOf(ListingOffersState())
//    val uiState: State<ListingOffersState> = _uiState

    private val _uiState = MutableStateFlow(ListingOffersState())
    val uiState: StateFlow<ListingOffersState> = _uiState.asStateFlow()

    private val _listing = mutableStateOf<Listing?>(null)
    val listing: State<Listing?> = _listing

    val isWritingOffer = mutableStateOf(false)
    val offerPrice = mutableStateOf("")

    val userRole = TokenManager.getUserRole()

    val listingId = savedStateHandle.get<String>("listingId")
    val clientId = savedStateHandle.get<String>("clientId")

    val isExternalMode = savedStateHandle.get<String>("clientId") == null &&
            savedStateHandle.keys().contains("listingId") &&
            savedStateHandle.keys().none { it.contains("clientId") } // verifica non passato

    val showExternalOfferDialog = mutableStateOf(false)

    val guestName = mutableStateOf("")
    val guestSurname = mutableStateOf("")
    val guestEmail = mutableStateOf("")
    val guestOffer = mutableStateOf("")


    init {
        if (listingId == null) {
            _uiState.value = _uiState.value.copy(error = "ID listing mancante", loading = false)

        } else if (isExternalMode) {
            fetchExternalOffers(listingId)
        } else if (clientId == null && userRole != "CLIENT") {
            _uiState.value = _uiState.value.copy(error = "ID client mancante", loading = false)
        } else {
            fetchOffers()

        }
        fetchListing()
    }

    private fun fetchOffersClient(listingId: String) {
        viewModelScope.launch {
            try {

                val offers =
                    AppContainer.offerRepository.getOffersByListingClient(listingId = listingId)

                _uiState.value = _uiState.value.copy(
                    offers = offers,
                    loading = false,
                    error = null
                )

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    offers = emptyList(),
                    loading = false,
                    error = e.message ?: "Errore sconosciuto"
                )
            }
        }
    }

    private fun fetchOffersAgent(listingId: String, clientId: String) {
        viewModelScope.launch {
            try {
                val offers =
                    AppContainer.offerRepository.getOffersByListingAgent(listingId, clientId)

                _uiState.value = _uiState.value.copy(
                    offers = offers,
                    loading = false,
                    error = null
                )

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    offers = emptyList(),
                    loading = false,
                    error = e.message ?: "Errore sconosciuto"
                )
            }
        }
    }

    private fun fetchExternalOffers(listingId: String) {
        viewModelScope.launch {
            try {
                val guestOffers = AppContainer.offerRepository.getExternalOffers(listingId)
                val mapped = guestOffers.map {
                    PropertyOffer(
                        id = it.lastOffer.id,
                        price = it.lastOffer.price,
                        date = it.lastOffer.date,
                        state = it.lastOffer.state,
                        madeByUser = it.lastOffer.madeByUser,
                        guestEmail = it.email,
                        guestName = it.name,
                        guestSurname = it.surname
                    )
                }
                _uiState.value = _uiState.value.copy(offers = mapped, loading = false)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "Errore nel caricamento delle offerte esterne",
                    loading = false
                )
            }
        }
    }


    private fun fetchListing() {
        val listingId = listingId!!

        viewModelScope.launch {
            try {
                val listing = AppContainer.listingRepository.getListing(listingId)

                _listing.value = listing

            } catch (e: Exception) {
                Log.d("LISTING", "FETCJ LISTING ANDATO MALE")
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }


    suspend fun submitOffer(): Boolean {
        val listingId = listingId!!
        return try {
                val price = offerPrice.value.toDouble()

                val listingPrice = listing.value?.price
                if (listingPrice == null) {
                    _uiState.value =
                        _uiState.value.copy(error = "Errore: prezzo dell'immobile non disponibile")
                    return false
                }

                // 🔒 Check: offerta almeno al 50% del prezzo dell'immobile
                val minimumPrice = listingPrice * 0.5
                if (price < minimumPrice) {
                    _uiState.value = _uiState.value.copy(
                        error = "L'offerta deve essere almeno il 50% del prezzo dell'immobile (€${minimumPrice.toInt()})"
                    )
                    return false
                }



                if (userRole == "CLIENT")
                    AppContainer.offerRepository.postOfferClient(listingId, price)
                else {
                    val clientId = clientId
                    if (clientId == null) {
                        Log.e("OFFER_ERROR", "ClientId nullo per utente agente")
                        _uiState.value = _uiState.value.copy(
                            error = "Errore interno: clientId mancante per l'agente"
                        )
                        return false
                    }
                    AppContainer.offerRepository.postOfferAgent(listingId, clientId, price)
                }
                // aggiorna UI o refetcha lista offerte

                fetchOffers()
                return true
            } catch (e: Exception) {
                Log.e("OFFER_ERROR", "Errore: ${e.message}", e)
                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "Errore sconosciuto"
                )
                return false
            }

    }

    fun updateOfferStatus(offerId: String, status: String) {
        //val listingId = listing.value?.id ?: return
        val listingId = listingId!!

        viewModelScope.launch {
            try {

                val currentOffers = _uiState.value.offers
                val updatedOffers = currentOffers.map { offer ->
                    if (offer.id == offerId) {
                        offer.copy(state = status) // Assumendo che PropertyOffer abbia un metodo copy
                    } else {
                        offer
                    }
                }

                // Aggiorna immediatamente l'UI
                _uiState.value = _uiState.value.copy(offers = updatedOffers)

                // Poi fai la chiamata al server
                val updatedOffer = AppContainer.offerRepository.updateOfferState(offerId, status)

                // Ricarica dal server per essere sicuri che tutto sia sincronizzato
                fetchOffers()
//                val updatedOffer = AppContainer.offerRepository.updateOfferState(offerId, status)
//                Log.d("DEBUG", "Offerta aggiornata: ${updatedOffer.state}")
//
//
////
//                val updatedList = _uiState.value.offers.map {
//                    if (it.id == updatedOffer.id) {
//                        updatedOffer
//                    } else {
//                        it
//                    }
//                }
//
//
//
//                _uiState.value = _uiState.value.copy(
//                    offers = updatedList
//                )
//
//                fetchOffers(listingId) // ricarica offerte aggiornate
            } catch (e: Exception) {
                Log.e("OfferUpdate", "Errore nell'aggiornamento dell'offerta: ${e.message}")
                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "Errore sconosciuto"
                )
                fetchOffers()
            }
        }
    }

    private fun fetchOffers() {
        val listingId = listingId!!


        if (userRole == "CLIENT") {
            fetchOffersClient(listingId)
        } else if (!isExternalMode) {
            if (clientId != null) {
                fetchOffersAgent(listingId, clientId)
            }
            else {
                _uiState.value = _uiState.value.copy(
                    loading = false,
                    error = "Impossibile ottenere le offerte: ID cliente mancante"
                )
            }
        } else {
            fetchExternalOffers(listingId)
        }
    }

    suspend fun submitExternalOffer() : Boolean{
        val listingId = listing.value?.id ?: return false
       return try {
                val name = guestName.value
                val surname = guestSurname.value
                val email = guestEmail.value
                val price = guestOffer.value.toDoubleOrNull()

                if (name.isBlank() || surname.isBlank() || email.isBlank() || price == null) {
                    _uiState.value =
                        _uiState.value.copy(error = "Compila tutti i campi correttamente")
                    return false
                }

//                val listingPrice = listing.value?.price.value.toDoubleOrNull()
//                val minimumPrice = listingPrice * 0.5
//
//                if (price <  minimumPrice) {
//                    _uiState.value = _uiState.value.copy(error = "Offerta inferiore al 50% del prezzo")
//                    return false
//                }

                val guest = Guest(name, surname, email)
                AppContainer.offerRepository.postOfferExternal(listingId, guest, price)

                // Resetta form e aggiorna offerte
                guestName.value = ""
                guestSurname.value = ""
                guestEmail.value = ""
                guestOffer.value = ""
                fetchOffers()
                return true


            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = "Errore: ${e.message}")
                return false
            }


        }
}

