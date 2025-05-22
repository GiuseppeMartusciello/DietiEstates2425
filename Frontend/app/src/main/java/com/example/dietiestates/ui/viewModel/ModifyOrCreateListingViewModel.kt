package com.example.dietiestates.ui.viewModel

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dietiestates.AppContainer
import com.example.dietiestates.data.model.Agent
import com.example.dietiestates.data.model.Listing
import com.example.dietiestates.data.model.dto.ListingFormErrors
import com.example.dietiestates.data.model.dto.ListingFormState
import com.example.dietiestates.data.model.dto.ModifyOrCreateListingDto
import kotlinx.coroutines.launch
data class ListingState(
    val loading: Boolean = true,
    val listing: Listing? = null,
    val error: String? = null
)

data class ListingEditUiState(
    val isLoading: Boolean = true,
    val success: Boolean = false,
    val error: String? = null,
    val operation: EditOperation? = null
)
data class ListingUiState(
    val agents: List<Agent> = emptyList(),
    val selectedAgentId: String? = null,
)

class ModifyOrCreateListingViewModel(    savedStateHandle: SavedStateHandle ) : ViewModel() {
    private val _listingState = mutableStateOf(ListingState())
    val listingState : State<ListingState> = _listingState

    private val _editListingState = mutableStateOf(ListingEditUiState(false,false,null))
    val editListingState : State<ListingEditUiState> = _editListingState

    val uiState = mutableStateOf(ListingUiState())

    var selectedImages by mutableStateOf<List<Uri>>(emptyList()) //foto locali per nuovo listing
        private set

    var formState by mutableStateOf(ListingFormState())
        private set
    var formErrors by mutableStateOf(ListingFormErrors())
        private set

    init {
        val id = savedStateHandle.get<String>("listingId")
        if (id != null) {
            fetchListing(id)
        }
        if (AppContainer.tokenManager.getUserRole() == "MANAGER" || AppContainer.tokenManager.getUserRole() == "SUPPORT_ADMIN")
            loadAgents()
    }

    private fun fetchListing(id: String){
        viewModelScope.launch {
            try {
                val listing = AppContainer.listingRepository.getListing(id)

                _listingState.value = _listingState.value.copy(
                    listing = listing,
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
    private fun loadAgents(){
        viewModelScope.launch {
                try {
                    val agents = AppContainer.agencyRepository.getAgents()

                    uiState.value = uiState.value.copy(agents = agents)
                    Log.d("output","agenti: $agents")
                } catch (e: Exception) {
                    //eccezione da gestire
                }
        }
    }

    fun uploadListingImages(
        context: Context,
        listingId: String,
        uris: List<Uri>,
    ) {
        viewModelScope.launch {
            _editListingState.value = ListingEditUiState(
                isLoading = true,
                success = false,
                error = null,
                operation = EditOperation.UPLOAD_IMAGE
            )
            try {
                AppContainer.listingRepository.uploadImagesToListing(context, listingId, uris)
                fetchListing(listingId)
                _editListingState.value = ListingEditUiState(
                    isLoading = false,
                    success = true,
                )
            } catch (e: Exception) {
                _editListingState.value = ListingEditUiState(
                    isLoading = false,
                    success = false,
                    error = e.message
                )
            }
        }
    }

    fun deleteImage(
        listingId: String,
        filename: String,
    ) {
        viewModelScope.launch {
            _editListingState.value = ListingEditUiState(
                isLoading = true,
                success = false,
                error = null,
                operation = EditOperation.DELETE_IMAGE
            )
            try {
                AppContainer.listingRepository.deleteImageFromListing(listingId, filename)
                fetchListing(listingId)
                _editListingState.value = ListingEditUiState(
                    isLoading = false,
                    success = true,
                )
            } catch (e: Exception) {
                _editListingState.value = ListingEditUiState(
                    isLoading = false,
                    success = false,
                    error = e.message
                )
            }
        }
    }

    fun updateListing(
        listingId: String,
        dto: ModifyOrCreateListingDto,
    ) {
        viewModelScope.launch {
            _editListingState.value = ListingEditUiState(
                isLoading = true,
                success = false,
                error = null,
                operation = EditOperation.UPDATE
            )
            try {
                val updatedListing = AppContainer.listingRepository.modifyListing(listingId, dto)
                _listingState.value = _listingState.value.copy(listing = updatedListing)
                _editListingState.value = ListingEditUiState(
                    isLoading = false,
                    success = true,
                    operation = EditOperation.UPDATE
                )

            } catch (e: Exception) {
                _editListingState.value = ListingEditUiState(
                    isLoading = false,
                    success = false,
                    error = e.message,
                    operation = EditOperation.UPDATE
                )

            }
        }
    }
    fun postListing(
        dto: ModifyOrCreateListingDto,
        context: Context
    ) {
        viewModelScope.launch {
            _editListingState.value = ListingEditUiState(
                isLoading = true,
                success = false,
                error = null,
                operation = EditOperation.POST
            )
            try {
                val createdListing = AppContainer.listingRepository.postListing(dto)
                uploadListingImages(context, createdListing.id, selectedImages)
                _listingState.value = _listingState.value.copy(listing = createdListing)
                _editListingState.value = ListingEditUiState(
                    isLoading = false,
                    success = true,
                    operation = EditOperation.POST
                )

            } catch (e: Exception) {
                _editListingState.value = ListingEditUiState(
                    isLoading = false,
                    success = false,
                    error = e.message,
                    operation = EditOperation.POST
                )

            }
        }
    }
    fun setSelectedAgent(agentId: String) {
        uiState.value = uiState.value.copy(selectedAgentId = agentId)
    }

    fun addImage(uri: List<Uri>) {
        selectedImages = selectedImages + uri
    }

    fun removeImage(uri: Uri) {
        selectedImages = selectedImages - uri
    }

    fun onFieldChange(field: (ListingFormState) -> ListingFormState) {
        formState = field(formState)
    }

    fun validateAndBuildDto(): ModifyOrCreateListingDto? {
        val errors = ListingFormErrors(
            address = if (formState.address.isBlank()) "Campo obbligatorio" else null,
            title = if (formState.title.isBlank()) "Campo obbligatorio" else null,
            municipality = if (formState.municipality.isBlank()) "Campo obbligatorio" else null,
            postalCode = if (formState.postalCode.isBlank()) "Campo obbligatorio" else null,
            province = if (formState.province.isBlank()) "Campo obbligatorio" else null,
            size = if (formState.size.isBlank()) "Campo obbligatorio" else null,
            description = if (formState.description.isBlank()) "Campo obbligatorio" else null,
            price = if (formState.price.toLongOrNull()?.let { it > 0 } == true) null else "Prezzo non valido" ,
            images = if (selectedImages.size < 2) "Seleziona almeno 2 immagini" else null

        )

        formErrors = errors

        val hasErrors = listOfNotNull(
            errors.address, errors.title, errors.municipality, errors.postalCode,
            errors.province, errors.size, errors.price, errors.description, errors.images
        ).isNotEmpty()

        if (hasErrors) return null

        return ModifyOrCreateListingDto(
            address = formState.address,
            title = formState.title,
            municipality = formState.municipality,
            postalCode = formState.postalCode,
            province = formState.province,
            size = formState.size,
            numberOfRooms = formState.numberOfRooms.toInt(),
            energyClass = formState.energyClass,
            description = formState.description,
            price = formState.price.toLong(),
            category = if(formState.category == "Vendita") "SALE" else "RENT",
            floor = formState.floor,
            hasElevator = formState.hasElevator,
            hasAirConditioning = formState.hasAirConditioning,
            hasGarage = formState.hasGarage
        )
    }

    fun loadListingForEdit(listing: Listing) {
        formState = ListingFormState(
            title = listing.title,
            address = listing.address,
            municipality = listing.municipality,
            postalCode = listing.postalCode,
            province = listing.province,
            size = listing.size,
            numberOfRooms = listing.numberOfRooms,
            energyClass = listing.energyClass.toString(),
            description = listing.description,
            price = listing.price.toString(),
            category = listing.category,
            floor = listing.floor,
            hasElevator = listing.hasElevator,
            hasAirConditioning = listing.hasAirConditioning,
            hasGarage = listing.hasGarage
        )
    }


}
enum class EditOperation {
    UPDATE, DELETE_IMAGE, UPLOAD_IMAGE, POST, DELETE_LISTING
}