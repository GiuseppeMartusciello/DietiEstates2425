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
import com.example.dietiestates.data.model.Research
import com.example.dietiestates.data.model.dto.CreateResearchDto
import kotlinx.coroutines.launch


class ResearchViewModel : ViewModel() {


    //gestisce  le ultime 10 ricerche
    private val _researchState = mutableStateOf(ResearchState())
    val researchState: State<ResearchState> = _researchState

    private val _searchState = mutableStateOf(SearchedState())
    val searchState: State<SearchedState> = _searchState

    //gestisce la effettiva ricerca
    var researchFormState by mutableStateOf(ResearchFormState())
        private set

    var researchFormErrors by mutableStateOf(ResearchFormErrors())
        private set

    //gestisce le ricerche ripetute tramite History component
    var selectedResearch by mutableStateOf<Research?>(null)
        private set

    //serve per gestire la ricerca vecchia
    var isOldResearch by mutableStateOf(false)


    fun resetResearchForm() {
        researchFormState = ResearchFormState()
    }

    fun updateSelectedResearch(research: Research?) {
        selectedResearch = research
        if (research != null) {
            checkOldResearch()
        }
    }

    fun updateResearchFormState(update: ResearchFormState.() -> ResearchFormState) {
        researchFormState = researchFormState.update()
    }

    fun createResearch() {
        viewModelScope.launch {
            try {
                _searchState.value = _searchState.value.copy(loading = true, error = null)

                val dto = validateAndBuildDto() ?: run {
                    return@launch
                }

                println("📋 Form valido: $researchFormState")


                val result = AppContainer.researchRepository.createResearch(dto)

                _searchState.value = SearchedState(
                    loading = false,
                    listings = result,
                    error = null
                )

                //vengono aggiornate le ricerche
                val updatedResearches = AppContainer.researchRepository.getLast10Researches()
                _researchState.value = _researchState.value.copy(
                    researches = updatedResearches,
                    error = null
                )

            } catch (e: Exception) {

                Log.e("CreateResearch", "❌ Errore durante la creazione: ${e.message}", e)

                _searchState.value = _searchState.value.copy(
                    loading = false,
                    error = "Errore durante la creazione della ricerca: ${e.message}"
                )
            }
        }
    }


    fun fetchResearch10() {
        try {
            viewModelScope.launch {
                val researches = AppContainer.researchRepository.getLast10Researches()

                _researchState.value = _researchState.value.copy(
                    loading = false,
                    error = null,
                    researches = researches
                )

            }
        } catch (e: Exception) {
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

    fun updateResearch() {
        viewModelScope.launch {
            try {

                val listings = AppContainer.researchRepository.updateResearch(selectedResearch!!.id)

                _searchState.value = SearchedState(
                    loading = false,
                    listings = listings,
                    error = null
                )

            } catch (e: Exception) {
                _researchState.value = _researchState.value.copy(
                    error = "Errore aggiornamento ricerca: ${e.message}"
                )
            }
        }
    }


    fun updateListResearch()
    {
        viewModelScope.launch {
            try {
                val updatedList = AppContainer.researchRepository.getLast10Researches()

                _researchState.value = _researchState.value.copy(
                    researches = updatedList,
                    error = null
                )

            }
            catch (e: Exception) {
                _researchState.value = _researchState.value.copy(
                    error = "Errore aggiornamento ricerca: ${e.message}"
                )
            }
        }

    }



    fun validateAndBuildDto(): CreateResearchDto? {

        val errors = ResearchFormErrors(
            search = if (researchFormState.searchType.isBlank()) "Campo Obbligatorio" else null,
            category = if (researchFormState.category.isBlank()) "Campo Obbligatorio" else null,
        )

        //  Verifica che i campi numerici siano numeri validi
        val hasLatLongRadius =
            researchFormState.latitude.toDoubleOrNull() != null &&
                    researchFormState.longitude.toDoubleOrNull() != null &&
                    researchFormState.radius.toDoubleOrNull() != null

        val hasMunicipality = researchFormState.municipality.trim().isNotEmpty()

        //  Entrambi compilati
        if (hasLatLongRadius && hasMunicipality) {
            researchFormErrors = errors.copy(
                municipality = "Non puoi usare sia la posizione che il comune"
            )
            return null
        }

        //  Nessuno dei due compilato
        if (!hasLatLongRadius && !hasMunicipality) {
            researchFormErrors = errors.copy(
                municipality = "Devi compilare il comune o la posizione"
            )
            return null
        }

        // Nessun errore
        researchFormErrors = errors

        val hasErrors = listOfNotNull(
            errors.search,
            errors.category
        ).isNotEmpty()

        if (hasErrors) return null

        return CreateResearchDto(
            searchType = researchFormState.searchType,
            municipality = researchFormState.municipality.ifBlank { null },
            latitude = researchFormState.latitude.toDoubleOrNull(),
            longitude = researchFormState.longitude.toDoubleOrNull(),
            radius = researchFormState.radius.toDoubleOrNull(),
            minPrice = researchFormState.minPrice,
            maxPrice = researchFormState.maxPrice,
            numberOfRooms = researchFormState.numberOfRooms,
            category = researchFormState.category,
            minSize = researchFormState.minSize,
            energyClass = researchFormState.energyClass,
            hasElevator = researchFormState.hasElevator,
            hasAirConditioning = researchFormState.hasAirConditioning,
            hasGarage = researchFormState.hasGarage
        )
    }

    fun checkOldResearch() {

        val selected = this.selectedResearch
        if (selected != null) {

            this.updateResearchFormState {
                copy(
                    searchType = selected.searchType,
                    municipality = selected.municipality ?: "",
                    latitude = selected.latitude?.toString() ?: "",
                    longitude = selected.longitude?.toString() ?: "",
                    radius = selected.radius?.toString() ?: "0",
                    minPrice = selected.minPrice?.toInt() ?: 10000,
                    maxPrice = selected.maxPrice?.toInt() ?: 1000000,
                    numberOfRooms = selected.numberOfRooms?.toInt() ?: 1,
                    category = selected.category.toString(),
                    minSize = selected.minSize?.toInt() ?: 50,
                    energyClass = selected.energyClass.toString(),
                    hasElevator = selected.hasElevator == true,
                    hasAirConditioning = selected.hasAirConditioning == true,
                    hasGarage = selected.hasGarage == true,
                )
            }
            this.updateResearchFormState {
                copy(
                    numberOfRooms = selected.numberOfRooms?.toInt() ?: 1
                )
            }
        }
    }

}


data class ResearchState(
    val loading: Boolean = true,
    val researches: List<Research> = emptyList(),
    val error: String? = null
)

data class SearchedState(
    val loading: Boolean = false,
    val listings: List<Listing> = emptyList(),
    val error: String? = null
)

data class ResearchFormErrors(
    val category: String? = null,
    val search: String? = null,
    val municipality: String? = null,
    val radius: String? = null,
)

data class ResearchFormState(
    val searchType: String = "MUNICIPALITY",
    val municipality: String = "",
    val latitude: String = "",
    val longitude: String = "",
    val radius: String = "0",
    val minPrice: Int = 100000,
    val maxPrice: Int = 1000000,
    val numberOfRooms: Int = 1,
    val category: String = "SALE",
    val minSize: Int = 50,
    val energyClass: String = "A",
    val hasElevator: Boolean = false,
    val hasAirConditioning: Boolean = false,
    val hasGarage: Boolean = false
)




