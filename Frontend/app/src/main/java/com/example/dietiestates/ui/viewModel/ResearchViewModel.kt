package com.example.dietiestates.ui.viewModel

import androidx.compose.runtime.State

import androidx.compose.runtime.mutableStateOf

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dietiestates.AppContainer
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.example.dietiestates.data.model.CreateResearchDto

import com.example.dietiestates.data.model.Research
import kotlinx.coroutines.launch

class ResearchViewModel: ViewModel() {
    private val _researchState = mutableStateOf(ResearchState())
    val researchState: State<ResearchState> = _researchState

    var researchFormState by mutableStateOf(ResearchFormState())
        private set

    var researchFormErrors by mutableStateOf(ResearchFormErrors())
        private set


    fun createResearch()
    {
        viewModelScope.launch {
            try {
                val dto = validateAndBuildDto() ?: return@launch
                AppContainer.researchRepository.createResearch(dto)

                //vengono aggiornate le ricerche
                val updatedResearches = AppContainer.researchRepository.getLast10Researches()
                _researchState.value = _researchState.value.copy(
                    researches = updatedResearches,
                    error = null
                )

            } catch (e: Exception) {
                _researchState.value = _researchState.value.copy(
                    error = "Errore durante la creazione della ricerca: ${e.message}")
            }
        }
    }



     fun fetchResearch10()
    {
        try {
            viewModelScope.launch {
                val researches = AppContainer.researchRepository.getLast10Researches()

                _researchState.value = _researchState.value.copy(
                    loading = false,
                    error = null,
                    researches = researches
                )

            }
        }catch (e: Exception)
        {
            _researchState.value.copy(
            error = "Errore durante il recupero dei dati: ${e.message}"
            )
        }
    }


    fun deleteResearch(id: String) {
        viewModelScope.launch {
            try {
                AppContainer.researchRepository.deleteResearch(id)

                _researchState.value = _researchState.value.copy(
                    researches = _researchState.value.researches.filterNot { it.id == id },
                    error = null
                )

            } catch (e: Exception) {
                _researchState.value = _researchState.value.copy(
                    error = "Errore durante l'eliminazione: ${e.message}"
                )
            }
        }
    }

    fun updateResearch(id:String) {
        viewModelScope.launch {
            try{
                AppContainer.researchRepository.updateResearch(id)

                val updatedList = AppContainer.researchRepository.getLast10Researches()

                _researchState.value = _researchState.value.copy(
                    researches = updatedList,
                    error = null
                )

            }
            catch (e: Exception)
            {
                _researchState.value = _researchState.value.copy(
                    error = "Errore aggiornamento ricerca: ${e.message}"
                )
            }
        }
    }



    fun validateAndBuildDto(): CreateResearchDto? {
        val errors = ResearchFormErrors(
            search = if(researchFormState.searchType.isBlank()) "Campo Obbligatorio" else null,
            municipality = if (researchFormState.municipality.isBlank()) null else null, // validità gestita sotto
            radius = if (researchFormState.radius.toDoubleOrNull()?.let { it > 0 } != true) "Raggio non valido" else null,
            minPrice = if (researchFormState.minPrice.toIntOrNull() == null) "Prezzo minimo non valido" else null,
            maxPrice = if (researchFormState.maxPrice.toIntOrNull() == null) "Prezzo massimo non valido" else null,
            numberOfRooms = if (researchFormState.numberOfRooms.toIntOrNull() == null) "Numero di stanze non valido" else null,
        )

        // latitudine+longitudine+radius oppure municipality, non entrambi
        val hasLatLongRadius = researchFormState.latitude.isNotBlank()
                && researchFormState.longitude.isNotBlank()
                && researchFormState.radius.isNotBlank()

        val hasMunicipality = researchFormState.municipality.isNotBlank()

        if (hasLatLongRadius && hasMunicipality) {
            researchFormErrors = errors.copy(
                municipality = "Non puoi usare sia la posizione che il comune",
            )
            return null
        }

        if (!hasLatLongRadius && !hasMunicipality) {
            researchFormErrors = errors.copy(
                municipality = "Devi compilare il comune o la posizione"
            )
            return null
        }

        researchFormErrors = errors

        val hasErrors = listOfNotNull(
            errors.radius,
            errors.minPrice,
            errors.maxPrice,
            errors.numberOfRooms,
            errors.municipality
        ).isNotEmpty()

        if (hasErrors) return null

        return CreateResearchDto(
            searchType = researchFormState.searchType,
            municipality = researchFormState.municipality.ifBlank { null },
            latitude = researchFormState.latitude.toDoubleOrNull(),
            longitude = researchFormState.longitude.toDoubleOrNull(),
            radius = researchFormState.radius.toDoubleOrNull(),
            minPrice = researchFormState.minPrice.toIntOrNull(),
            maxPrice = researchFormState.maxPrice.toIntOrNull(),
            numberOfRooms = researchFormState.numberOfRooms.toIntOrNull(),
            category = researchFormState.category,
            minSize = researchFormState.minSize,
            energyClass = researchFormState.energyClass,
            hasElevator = researchFormState.hasElevator,
            hasAirConditioning = researchFormState.hasAirConditioning,
            hasGarage = researchFormState.hasGarage
        )
    }
}




data class ResearchState(
    val loading: Boolean = true,
    val researches: List<Research> = emptyList(),
    val error: String? = null
)

data class ResearchFormErrors(
    val search: String? = null,
    val municipality: String? = null,
    val radius: String? = null,
    val minPrice: String? = null,
    val maxPrice: String? = null,
    val numberOfRooms: String? = null,
)

data class ResearchFormState(
    val searchType: String = "MUNICIPALITY",
    val municipality: String = "",
    val latitude: String = "",
    val longitude: String = "",
    val radius: String = "0",
    val minPrice: String = "",
    val maxPrice: String = "",
    val numberOfRooms: String = "1",
    val category: String = "Vendita",
    val minSize: String = "",
    val energyClass: String = "A",
    val hasElevator: Boolean = false,
    val hasAirConditioning: Boolean = false,
    val hasGarage: Boolean = false
)




