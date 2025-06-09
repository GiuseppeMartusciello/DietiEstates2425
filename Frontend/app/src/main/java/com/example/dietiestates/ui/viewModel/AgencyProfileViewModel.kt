package com.example.dietiestates.ui.viewModel

import android.os.Build
import android.util.Log
import android.util.Patterns
import androidx.annotation.RequiresApi
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dietiestates.AppContainer
import com.example.dietiestates.data.model.Agency
import com.example.dietiestates.data.model.Client
import com.example.dietiestates.data.model.dto.CreateAgentDto
import com.example.dietiestates.data.model.dto.CreateSupportAdminDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.time.LocalDate
import java.time.Period

class AgencyProfileViewModel() : ViewModel() {
    private val _agencyState = mutableStateOf(AgencyState())
    val agencyState: State<AgencyState> = _agencyState


    private val _createResult = MutableStateFlow<String?>(null)
    val createResult: StateFlow<String?> = _createResult

    init {
        getAgency()
    }

    fun getAgency() {
        viewModelScope.launch {
            try {
                val agency = AppContainer.agencyRepository.getAgency()

                _agencyState.value = _agencyState.value.copy(
                    agency = agency,
                    loading = false,
                    error = null
                )

            } catch (e: Exception) {
                _agencyState.value = _agencyState.value.copy(
                    loading = false,
                    error = "Error fetching agency ${e.message}"
                )
            }
        }
    }
    private fun createAgent(dto: CreateAgentDto) {
        viewModelScope.launch {
            try {
                AppContainer.agencyRepository.createAgent(dto)
                _createResult.value = "success"
            } catch (e: HttpException) {
                val code = e.code()
                val message = when (code) {
                    400 -> "Richiesta non valida. Controlla i campi inseriti."
                    401 -> "Non autorizzato. Effettua nuovamente il login."
                    403 -> "Accesso negato. Non hai i permessi per questa azione."
                    404 -> "Risorsa non trovata."
                    409 -> "Email o numero di telefono già utilizzati."
                    422 -> "Errore di validazione. Dati non accettabili."
                    500 -> "Errore interno del server. Riprova più tardi."
                    else -> "Errore sconosciuto. Riprova"
                }

                val errorBody = e.response()?.errorBody()?.string()
                val detailedMessage = if (!errorBody.isNullOrBlank()) "$message\nDettagli: $errorBody" else message

                _createResult.value = detailedMessage
                Log.e("AgentViewModel", "HttpException: $detailedMessage", e)
            } catch (e: Exception) {
                _createResult.value = "Errore sconosciuto: ${e.localizedMessage ?: e.toString()}"
                Log.e("AgentViewModel", "Unknown error", e)
            }
        }
    }

    private fun createSupportAdmin(dto: CreateSupportAdminDto) {
        viewModelScope.launch {
            try {
                AppContainer.agencyRepository.createSupportAdmin(dto)
                _createResult.value = "success"
            } catch (e: HttpException) {
                val code = e.code()
                var message = when (code) {
                    400 -> "Richiesta non valida."
                    401 -> "Non autorizzato. Effettua nuovamente il login."
                    403 -> "Accesso negato. Non hai i permessi per questa azione."
                    404 -> "Risorsa non trovata."
                    409 -> "Email o numero di telefono già utilizzati."
                    422 -> "Errore di validazione. Dati non accettabili."
                    500 -> "Errore interno del server. Riprova più tardi."
                    else -> "Errore sconosciuto. Riprova"
                }


                val errorBody = e.response()?.errorBody()?.string()
                if(code == 400 && !errorBody.isNullOrBlank())
                    if(errorBody.contains("phone must be a valid phone number"))
                        message += "Il numero di telefono non è un numero valido"


                _createResult.value = message
                Log.e("AgentViewModel", "HttpException: $errorBody", e)
            } catch (e: Exception) {
                _createResult.value = "Errore sconosciuto: ${e.localizedMessage ?: e.toString()}"
                Log.e("AgentViewModel", "Unknown error", e)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun validateAndCreateAgent(dto: CreateAgentDto) {
        val error = validateAgentDto(dto)

        if (error != null) {
            _createResult.value = error
            return
        }
        createAgent(dto)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun validateAndCreateSupportAdmin(dto: CreateSupportAdminDto) {
        val error = validateSupportDto(dto)

        if (error != null) {
            _createResult.value = error
            return
        }
        createSupportAdmin(dto)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun validateSupportDto(dto: CreateSupportAdminDto): String? {
        return validateCommonFields(
            name = dto.name,
            surname = dto.surname,
            email = dto.email,
            birthDate = dto.birthDate,
            gender = dto.gender,
            phone = dto.phone
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun validateAgentDto(dto: CreateAgentDto): String? {
        // Validazione campi comuni
        val commonError = validateCommonFields(
            name = dto.name,
            surname = dto.surname,
            email = dto.email,
            birthDate = dto.birthDate,
            gender = dto.gender,
            phone = dto.phone
        )
        if (commonError != null) return commonError

        val today = LocalDate.now()
        val startDateParsed = try {
            LocalDate.parse(dto.start_date)
        } catch (e: Exception) {
            return "Data di inizio non valida"
        }

        return when {
            dto.licenseNumber.length !in 6..12 -> "Il numero di licenza deve essere tra 6 e 12 caratteri"
            startDateParsed.isAfter(today) -> "La data di inizio non può essere futura"
            dto.languages.isEmpty() || dto.languages.any { it.isBlank() } -> "Le lingue non possono essere vuote"
            else -> null
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun validateCommonFields(
        name: String,
        surname: String,
        email: String,
        birthDate: String,
        gender: String,
        phone: String
    ): String? {
        val emailRegex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")
        val phoneRegex = Regex("^\\+?[0-9]{10,15}$")
        val today = LocalDate.now()

        val birthDateParsed = try {
            LocalDate.parse(birthDate)
        } catch (e: Exception) {
            return "Data di nascita non valida"
        }

        val age = Period.between(birthDateParsed, today).years

        return when {
            name.isBlank() -> "Il nome è obbligatorio"
            surname.isBlank() -> "Il cognome è obbligatorio"
            email.isBlank() || !emailRegex.matches(email) -> "Email non valida"
            age < 18 -> "L'utente deve avere almeno 18 anni"
            gender != "MALE" && gender != "FEMALE" -> "Genere non valido"
            phone.isBlank() || !phoneRegex.matches(phone) -> "Numero di telefono non valido"
            else -> null
        }
    }




    fun resetCreateResult() {
        _createResult.value = null
    }

    data class AgencyState(
        val loading: Boolean = true,
        val agency: Agency = Agency(),
        val error: String? = null
    )
}
