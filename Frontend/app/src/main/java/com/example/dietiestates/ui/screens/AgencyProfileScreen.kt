package com.example.dietiestates.ui.screens

import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Error
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LocalOffer
import androidx.compose.material.icons.outlined.Logout
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.dietiestates.R
import com.example.dietiestates.data.model.dto.CreateAgentDto
import com.example.dietiestates.data.model.dto.CreateSupportAdminDto
import com.example.dietiestates.ui.screens.components.CustomButton
import com.example.dietiestates.ui.screens.components.LabeledNumberField
import com.example.dietiestates.ui.screens.components.LabeledTextField
import com.example.dietiestates.ui.screens.components.TopBarOffer
import com.example.dietiestates.ui.screens.components.dropDownMenu
import com.example.dietiestates.ui.theme.LocalAppTypography
import com.example.dietiestates.ui.theme.Roboto
import com.example.dietiestates.ui.theme.RobotoSerif
import com.example.dietiestates.ui.theme.RobotoSlab
import com.example.dietiestates.ui.viewModel.AgencyProfileViewModel
import com.example.dietiestates.ui.viewModel.ProfileViewModel
import com.example.dietiestates.utility.TokenManager
import com.example.tuaapp.ui.components.NavBar
import com.example.tuaapp.ui.components.NavItem
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AgencyProfileScreen(navController: NavController) {
    val viewModel: AgencyProfileViewModel = viewModel()
    val agency = viewModel.agencyState.value.agency
    val state = viewModel.agencyState.value
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = { TopBarOffer(navController = navController, modifier = Modifier, "Profilo Agenzia") },
        bottomBar = { NavBar(navController = navController) }) {
        paddingValues ->
        when {
            state.loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Gray.copy(alpha = 0.4f)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color.White)
                }
            }

            state.error != null -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        Icons.Outlined.Error,
                        contentDescription = null,
                        tint = Color.Red,
                        modifier = Modifier.size(70.dp)
                    )
                    Text(text = "Errore nel caricamento dell agenzia", fontSize = 22.sp, modifier  = Modifier.align(Alignment.CenterHorizontally))
                }
            }

            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .verticalScroll(scrollState),
                ) {

                    Image(
                        painter = painterResource(R.drawable.profile),
                        contentDescription = null,
                        modifier = Modifier
                            .height(150.dp)
                            .width(150.dp)
                            .padding(5.dp)
                            .align(Alignment.CenterHorizontally),
                        contentScale = ContentScale.Crop
                    )
                    Text(
                        text = agency.name,
                        fontFamily = Roboto,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 22.sp,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    ProfileField("Indirizzo", agency.legalAddress)
                    ProfileField("Numero Vat", agency.vatNumber)
                    ProfileField("Telefono", agency.phone)

                    Spacer(modifier = Modifier.height(40.dp))
                    Divider(
                        thickness = 0.7.dp,
                        color = Color(0xFFE0E0E0),
                        modifier = Modifier.padding(vertical = 10.dp)
                    )
                    CreateAgentButtonAndForm("Agente")
                    Divider(
                        thickness = 0.7.dp,
                        color = Color(0xFFE0E0E0),
                        modifier = Modifier.padding(vertical = 10.dp)
                    )
                    CreateAgentButtonAndForm("Admin Di Supporto")
                    Divider(
                        thickness = 0.7.dp,
                        color = Color(0xFFE0E0E0),
                        modifier = Modifier.padding(vertical = 10.dp)
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp,12.dp,12.dp,30.dp)
                            .border(
                                width = 1.dp,
                                color = Color.LightGray,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clip(RoundedCornerShape(12.dp))
                            .padding(vertical = 12.dp, horizontal = 20.dp)
                            .clickable {
                                TokenManager.clearSession()
                                navController.navigate("loginscreen")},
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Disconnetti",
                            style = LocalAppTypography.current.featureTitle,
                            fontSize = 18.sp,
                            color = Color(0xFF3F51B5)
                        )
                        Icon(
                            imageVector = Icons.Outlined.Logout,
                            tint = Color(0xFF3F51B5),
                            contentDescription = null,
                            modifier = Modifier.size(28.dp),
                        )

                    }
                }
            }
        }

    }
}


@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateAgentButtonAndForm(type: String) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val showSheet = remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    if (showSheet.value) {
        ModalBottomSheet(
            onDismissRequest = { showSheet.value = false },
            sheetState = sheetState,
            windowInsets = WindowInsets.systemBars
        ) {
            Scaffold(
                snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
                containerColor = MaterialTheme.colorScheme.background,
                content = { paddingValues ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(0.9f)
                            .verticalScroll(rememberScrollState())
                            .imePadding()
                            .padding(paddingValues)
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                    ) {
                        if (type == "Agente")
                            CreateAgentFormModal(
                                onClose = { showSheet.value = false },
                                snackbarHostState = snackbarHostState
                            )
                        else
                            CreateSupportAdminFormModal(
                                onClose = { showSheet.value = false },
                                snackbarHostState = snackbarHostState
                            )
                    }
                }
            )
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
            .clickable { showSheet.value = true },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "Aggiungi ${type}",
            style = LocalAppTypography.current.featureTitle,
            fontSize = 18.sp
        )
        Icon(
            imageVector = Icons.Outlined.Add,
            contentDescription = null,
            modifier = Modifier.size(28.dp),
        )
    }
}




@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CreateSupportAdminFormModal(
    onClose: () -> Unit,
    viewModel: AgencyProfileViewModel = viewModel(),
    snackbarHostState: SnackbarHostState

) {
    var name by remember { mutableStateOf("") }
    var surname by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("Maschio") }
    var phone by remember { mutableStateOf("") }
    var birthDate by remember { mutableStateOf("") }

    val genderOptions = listOf("Maschio", "Femmina")
    val createResult by viewModel.createResult.collectAsState()

    LaunchedEffect(createResult) {
        createResult?.let {
            val message = if (it == "success") "Admin di supporto creato con successo" else it
            snackbarHostState.showSnackbar(message)
            if (it == "success") onClose()
            viewModel.resetCreateResult()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 20.dp, horizontal = 10.dp)
    ) {
        LabeledTextField(
            label = "Nome",
            value = name,
            onValueChange = { name = it },
            isError = name.isBlank(),
            maxChars = 40
        )

        LabeledTextField(
            label = "Cognome",
            value = surname,
            onValueChange = { surname = it },
            isError = surname.isBlank(),
            maxChars = 40
        )

        LabeledTextField(
            label = "Email",
            value = email,
            onValueChange = { email = it },
            isError = !email.contains("@"),
            maxChars = 40
        )
        LabeledTextField(
            label = "Telefono",
            value = phone,
            onValueChange = { phone = it },
            maxChars = 13,
            isError = phone.length < 10,
            modifier = Modifier.width(200.dp)
        )

        dropDownMenu(
            label = "Genere",
            value = gender,
            options = genderOptions,
            onValueChange = { gender = it },
            modifier = Modifier.width(170.dp)
        )

        DatePickerField(
            label = "Data di nascita",
            date = birthDate,
            onDateSelected = { birthDate = it }
        )

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            CustomButton(
                onClick = { onClose() },
                style = "white",
                text = "Annulla",
                modifier = Modifier.width(130.dp)
            )

            CustomButton(
                onClick = {
                    val genderValue = if (gender == "Maschio") "MALE" else "FEMALE"
                    val dto = CreateSupportAdminDto(
                        name = name,
                        surname = surname,
                        email = email,
                        birthDate = birthDate,
                        phone = phone,
                        gender = genderValue,
                    )
                    viewModel.validateAndCreateSupportAdmin(dto)
                },
                style = "blue",
                text = "Crea",
                modifier = Modifier.width(130.dp)
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CreateAgentFormModal(
    onClose: () -> Unit,
    viewModel: AgencyProfileViewModel = viewModel(),
    snackbarHostState: SnackbarHostState
) {
    var licenseNumber by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var surname by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("Maschio") }
    var phone by remember { mutableStateOf("") }
    var birthDate by remember { mutableStateOf("") }
    var startDate by remember { mutableStateOf("") }
    var languages by remember { mutableStateOf("") }

    val genderOptions = listOf("Maschio", "Femmina")
    val createResult by viewModel.createResult.collectAsState()

    LaunchedEffect(createResult) {
        createResult?.let {
            val message = if (it == "success") "Agente creato con successo" else it
            snackbarHostState.showSnackbar(message)
            if (it == "success") onClose()
            viewModel.resetCreateResult()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 20.dp, horizontal = 10.dp)
    ) {
        LabeledTextField(
            label = "Numero Licenza",
            value = licenseNumber,
            onValueChange = { licenseNumber = it },
            isError = licenseNumber.length !in 6..12,
            maxChars = 12
        )

        LabeledTextField(
            label = "Nome",
            value = name,
            onValueChange = { name = it },
            isError = name.isBlank(),
            maxChars = 40
        )

        LabeledTextField(
            label = "Cognome",
            value = surname,
            onValueChange = { surname = it },
            isError = surname.isBlank(),
            maxChars = 40
        )

        LabeledTextField(
            label = "Email",
            value = email,
            onValueChange = { email = it },
            isError = !email.contains("@"),
            maxChars = 40
        )
        LabeledTextField(
            label = "Telefono",
            value = phone,
            onValueChange = { phone = it },
            maxChars = 13,
            isError = phone.length < 10,
            modifier = Modifier.width(200.dp)
        )

        dropDownMenu(
            label = "Genere",
            value = gender,
            options = genderOptions,
            onValueChange = { gender = it },
            modifier = Modifier.width(170.dp)
        )


        DatePickerField(
            label = "Data di nascita",
            date = birthDate,
            onDateSelected = { birthDate = it }
        )

        DatePickerField(
            label = "Data di inizio",
            date = startDate,
            onDateSelected = { startDate = it }
        )

        LabeledTextField(
            label = "Lingue (separate da virgola)",
            value = languages,
            onValueChange = { languages = it },
            isError = languages.isBlank(),
            maxChars = 100
        )

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            CustomButton(
                onClick = { onClose() },
                style = "white",
                text = "Annulla",
                modifier = Modifier.width(130.dp)
            )

            CustomButton(
                onClick = {
                    val genderValue = if (gender == "Maschio") "MALE" else "FEMALE"
                    val dto = CreateAgentDto(
                        licenseNumber = licenseNumber,
                        name = name,
                        surname = surname,
                        email = email,
                        birthDate = birthDate,
                        gender = genderValue,
                        phone = phone,
                        start_date = startDate,
                        languages = languages.split(",").map { it.trim() }
                    )
                    viewModel.validateAndCreateAgent(dto)
                },
                style = "blue",
                text = "Crea",
                modifier = Modifier.width(130.dp)
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("RememberReturnType")
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DatePickerField(
    label: String,
    date: String,
    onDateSelected: (String) -> Unit
) {
    val context = LocalContext.current
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val dateState = remember { mutableStateOf(date) }
    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) {
                val initialDate = if (date.isNotBlank()) {
                    try {
                        LocalDate.parse(date, formatter)
                    } catch (e: Exception) {
                        LocalDate.now()
                    }
                } else {
                    LocalDate.now()
                }

                android.app
                    .DatePickerDialog(
                        context,
                        { _, year, month, dayOfMonth ->
                            val pickedDate = LocalDate.of(year, month + 1, dayOfMonth)
                            val formatted = pickedDate.format(formatter)
                            dateState.value = formatted
                            onDateSelected(formatted)
                        },
                        initialDate.year,
                        initialDate.monthValue - 1,
                        initialDate.dayOfMonth
                    )
                    .show()
            }
    ) {
        OutlinedTextField(
            value = dateState.value,
            onValueChange = {},
            label = { Text(label) },
            modifier = Modifier
                .fillMaxWidth(),
            readOnly = true,
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = "Scegli data",
                    tint = Color(0xFF3F51B5) // tono di blu per contrasto
                )
            },
            enabled = false,
            colors = TextFieldDefaults.outlinedTextFieldColors(
                disabledTextColor = Color.Black,
                disabledBorderColor = Color.Gray,
                disabledLabelColor = Color.Gray,
                disabledTrailingIconColor = Color(0xFF3F51B5),
            )
        )
    }
}






