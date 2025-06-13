package com.example.dietiestates.ui.screens

import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.dietiestates.AppContainer
import com.example.dietiestates.ui.screens.components.BottomBar
import com.example.dietiestates.ui.screens.components.CustomButton
import com.example.dietiestates.ui.screens.components.ImageGalleryPager
import com.example.dietiestates.ui.screens.components.LabeledCheckBoxField
import com.example.dietiestates.ui.screens.components.LabeledNumberField
import com.example.dietiestates.ui.screens.components.LabeledTextField
import com.example.dietiestates.ui.screens.components.LocalPhotoEditor
import com.example.dietiestates.ui.screens.components.SimpleDropdownSelector
import com.example.dietiestates.ui.screens.components.dropDownMenu
import com.example.dietiestates.ui.theme.LocalAppTypography
import com.example.dietiestates.ui.theme.RobotoSlab
import com.example.dietiestates.ui.viewModel.EditOperation
import com.example.dietiestates.ui.viewModel.ModifyOrCreateListingViewModel
import com.example.dietiestates.utility.TokenManager
import com.google.accompanist.systemuicontroller.rememberSystemUiController


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateListingScreen(navController: NavController) {
    val viewModel: ModifyOrCreateListingViewModel = viewModel()
    val editState = viewModel.editListingState.value //Stato della modifica/aggiornamento del listing
    val scrollState = rememberScrollState()
    val systemUiController = rememberSystemUiController()
    var showPhotoEditor by remember { mutableStateOf(false) }
    val context = LocalContext.current
    var expanded by remember { mutableStateOf(false) }
    val form = viewModel.formState
    val errors = viewModel.formErrors

    var showErrorsDialog by remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris ->
        if (!uris.isNullOrEmpty()) {
            viewModel.addImage(uris)
        }
    }
    SideEffect {
        systemUiController.setStatusBarColor(
            Color.Transparent,
            darkIcons = true
        )
    }

    LaunchedEffect(editState) {
        when {
            editState.success -> {
                val message = when (editState.operation) {
                    EditOperation.UPDATE -> "✅ Inserito con successo"
                    else -> "✅ Operazione completata"
                }

                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                if (editState.operation === EditOperation.POST) {
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set("listingModified", true)
                    navController.popBackStack()
                    navController.navigateUp()
                }

            }

            editState.error != null -> {
                val errorMessage = when (editState.operation) {
                    EditOperation.POST -> "❌ Errore durante il caricamento"
                    else -> "❌ Errore generico"
                }
                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
            }
        }
    }

// Calcola errorMessages in modo reattivo in base a errors
    val errorMessages = listOfNotNull(
        errors.address,
        errors.title,
        errors.municipality,
        errors.postalCode,
        errors.province,
        errors.size,
        errors.description,
        errors.price,
        errors.images,
        errors.agentId
    )

    LaunchedEffect(errorMessages) {
        if (errorMessages.isNotEmpty()) {
            showErrorsDialog = true
        }
    }

    if (showErrorsDialog) {
        AlertDialog(
            onDismissRequest = { showErrorsDialog = false },
            title = { Text("Errori nel form") },
            text = {
                Column {
                    errorMessages.forEach {
                        Text(text = "• $it")
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showErrorsDialog = false }) {
                    Text("OK")
                }
            }
        )

    }


    when {
        viewModel.uiState.value.loading -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color.White)
            }
        }

        else -> {
            Box(modifier = Modifier.fillMaxSize()) {
                Column( //Colonna che controlla la possibilità di scrollare
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                        .padding(bottom = 100.dp)
                ) {
                    Column() {
                        ImageGalleryPager(images = viewModel.selectedImages.map { it.toString() })
                        viewModel.formErrors.images?.let {
                            Text(
                                text = it,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(start = 12.dp, top = 4.dp)
                            )
                        }
                        CustomButton(
                            onClick = { showPhotoEditor = true },
                            style = "white",
                            text = "Modifica",
                            icon = Icons.Outlined.Edit,
                            modifier = Modifier
                                .align(
                                    Alignment.End
                                )
                                .padding(end = 10.dp)
                        )
                    }
                    if (showPhotoEditor) {
                        ModalBottomSheet(onDismissRequest = { showPhotoEditor = false }) {
                            LocalPhotoEditor(
                                selectedImages = viewModel.selectedImages,
                                onImageRemove = { viewModel.removeImage(it) },
                                onAddImagesClick = { launcher.launch("image/*") } // questo apre la galleria
                            )

                        }
                    }
                    Column(
                        modifier = Modifier
                            .padding(horizontal = 10.dp)
                    ) {

                        LabeledTextField(
                            label = "Titolo",
                            value = form.title,
                            onValueChange = {
                                viewModel.onFieldChange { current ->
                                    current.copy(
                                        title = it
                                    )
                                }
                            },
                            isError = errors.title != null,
                            maxChars = 80
                        )
                        LabeledTextField(
                            label = "Indirizzo",
                            value = form.address,
                            onValueChange = {
                                viewModel.onFieldChange { current ->
                                    current.copy(
                                        address = it
                                    )
                                }
                            },
                            isError = errors.address != null,
                            maxChars = 80
                        )
                        LabeledTextField(
                            label = "Comune",
                            value = form.municipality,
                            onValueChange = {
                                viewModel.onFieldChange { current ->
                                    current.copy(
                                        municipality = it
                                    )
                                }
                            },
                            isError = errors.municipality != null,
                            maxChars = 30
                        )
                        LabeledNumberField(
                            label = "Codice Postale",
                            value = form.postalCode,
                            onValueChange = {
                                viewModel.onFieldChange { current ->
                                    current.copy(
                                        postalCode = it
                                    )
                                }
                            },
                            maxChars = 5,
                            isError = errors.postalCode != null,
                            modifier = Modifier.width(70.dp),
                        )
                        Row(
                            modifier = Modifier
                                .padding(vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Provincia",
                                style = LocalAppTypography.current.featureTitle,
                                fontSize = 16.sp,
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            Column(horizontalAlignment = Alignment.End) {
                                TextField(
                                    value = form.province,
                                    onValueChange = {
                                        if (it.length <= 2) {
                                            viewModel.onFieldChange { current ->
                                                current.copy(
                                                    province = it
                                                )
                                            }
                                        }
                                    },
                                    textStyle = TextStyle(
                                        fontFamily = RobotoSlab,
                                        fontWeight = FontWeight.Light,
                                        fontSize = 12.sp
                                    ),
                                    colors = TextFieldDefaults.colors(
                                        unfocusedContainerColor = Color.Transparent,
                                        focusedContainerColor = Color.Transparent,
                                        disabledContainerColor = Color.Transparent
                                    ),
                                    isError = errors.province != null,
                                    modifier = Modifier
                                        .height(48.dp)
                                        .width(70.dp)
                                )
                                Text(
                                    text = "${form.province.length} / 2",
                                    fontSize = 12.sp,
                                    color = if (form.province.length >= 2) Color.Red else Color.Gray,
                                )
                            }

                        }
                        LabeledNumberField(
                            label = "Dimensione",
                            value = form.size,
                            onValueChange = { viewModel.onFieldChange { current -> current.copy(size = it) } },
                            maxChars = 5,
                            isError = errors.size != null,
                            modifier = Modifier.width(70.dp)
                        )
                        dropDownMenu(
                            label = "Stanze",
                            value = form.numberOfRooms,
                            options = (0..20).map { it.toString() },
                            onValueChange = {
                                viewModel.onFieldChange { current ->
                                    current.copy(
                                        numberOfRooms = it
                                    )
                                }
                            },
                        )
                        dropDownMenu(
                            label = "Classe Energetica",
                            value = form.energyClass,
                            options = listOf("A", "B", "C", "D", "E"),
                            onValueChange = {
                                viewModel.onFieldChange { current ->
                                    current.copy(
                                        energyClass = it
                                    )
                                }
                            },
                        )
                        LabeledNumberField(
                            label = "Prezzo",
                            value = form.price,
                            maxChars = 15,
                            isError = errors.price != null,
                            onValueChange = {
                                viewModel.onFieldChange { current ->
                                    current.copy(
                                        price = it
                                    )
                                }
                            },
                        )
                        dropDownMenu(
                            label = "Categoria",
                            value = form.category,
                            options = listOf("Vendita", "Affitto"),
                            onValueChange = {
                                viewModel.onFieldChange { current ->
                                    current.copy(
                                        category = it
                                    )
                                }
                            },
                            modifier = Modifier.width(130.dp)
                        )
                        dropDownMenu(
                            label = "Piani",
                            value = form.floor,
                            options = (0..20).map { it.toString() },
                            onValueChange = {
                                viewModel.onFieldChange { current ->
                                    current.copy(
                                        floor = it
                                    )
                                }
                            },
                        )
                        LabeledCheckBoxField(
                            label = "Ascensore",
                            value = form.hasElevator,
                            onValueChange = {
                                viewModel.onFieldChange { current ->
                                    current.copy(
                                        hasElevator = it
                                    )
                                }
                            }
                        )
                        LabeledCheckBoxField(
                            label = "Aria Condizionata",
                            value = form.hasAirConditioning,
                            onValueChange = {
                                viewModel.onFieldChange { current ->
                                    current.copy(
                                        hasAirConditioning = it
                                    )
                                }
                            }
                        )
                        LabeledCheckBoxField(
                            label = "Garage",
                            value = form.hasGarage,
                            onValueChange = {
                                viewModel.onFieldChange { current ->
                                    current.copy(
                                        hasGarage = it
                                    )
                                }
                            }
                        )
                        if (TokenManager.getUserRole() == "MANAGER" || TokenManager.getUserRole() == "SUPPORT_ADMIN") {
                            Column(
                                modifier = Modifier
                                    .padding(vertical = 10.dp)
                            ) {
                                Text(
                                    text = "Agente Referente",
                                    style = LocalAppTypography.current.featureTitle,
                                    fontSize = 16.sp,
                                )

                                ExposedDropdownMenuBox(
                                    expanded = expanded,
                                    onExpandedChange = { expanded = !expanded },

                                    ) {
                                    OutlinedTextField(
                                        value = viewModel.uiState.value.selectedAgentId?.let {
                                            val selectedAgent = viewModel.uiState.value.agents.find { agent -> agent.userId == it }
                                            selectedAgent?.let { "${it.name} ${it.surname}" } ?: ""
                                        } ?: "",
                                        onValueChange = {},
                                        readOnly = true,
                                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                                        modifier = Modifier
                                            .menuAnchor()
                                            .fillMaxWidth()
                                            .height(50.dp)
                                    )

                                    ExposedDropdownMenu(
                                        expanded = expanded,
                                        onDismissRequest = { expanded = false }
                                    ) {
                                        viewModel.uiState.value.agents.forEach { agent ->
                                            DropdownMenuItem(
                                                text = { Text("${agent.name} ${agent.surname}") },
                                                onClick = {
                                                    viewModel.setSelectedAgent(agent.userId)
                                                    expanded = false
                                                }
                                            )
                                        }
                                    }
                                }

                            }
                        }

                        Text(
                            text = "Descrizione",
                            style = LocalAppTypography.current.featureTitle,
                            fontSize = 16.sp,
                            modifier = Modifier
                                .padding(vertical = 10.dp),
                        )
                        TextField(
                            value = form.description,
                            onValueChange = {
                                viewModel.onFieldChange { current ->
                                    current.copy(
                                        description = it
                                    )
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 120.dp),
                            textStyle = TextStyle(
                                fontSize = 14.sp,
                                lineHeight = 20.sp
                            ),
                            isError = errors.description != null,
                            singleLine = false,
                            maxLines = 10
                        )


                    }
                }
                BottomBar(
                    navController,
                    onClick = {
                        val dto = viewModel.validateAndBuildCreateDto()
                        if (dto != null) {
                            viewModel.postListing(
                                dto = dto,
                                context
                            )
                        }
                    },
                    modifier = Modifier.align(Alignment.BottomCenter)
                )
                if (editState.isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.5f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(color = Color.White)
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Attendi mentre inseriamo l'appartamento",
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}
