package com.example.dietiestates.ui.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AttachMoney
import androidx.compose.material.icons.outlined.Error
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.dietiestates.data.model.Listing
import com.example.dietiestates.ui.screens.components.CustomButton
import com.example.dietiestates.ui.theme.AppTypography
import com.example.dietiestates.ui.theme.LocalAppTypography
import com.example.dietiestates.ui.theme.RobotoSlab
import com.example.dietiestates.ui.viewModel.ListingViewModel
import com.example.dietiestates.utility.formatNumberWithDots
import com.google.accompanist.systemuicontroller.rememberSystemUiController

data class ListingState(
    val loading: Boolean = true,
    val listing: Listing? = null,
    val error: String? = null
)

@Composable
fun ModifyListingScreen(navController: NavController) {
    val viewModel: ListingViewModel = viewModel()
    val listing = viewModel.listingState.value.listing
    val state = viewModel.listingState.value
    val scrollState = rememberScrollState()
    val systemUiController = rememberSystemUiController()

    SideEffect {
        systemUiController.setStatusBarColor(
            Color.Transparent,
            darkIcons = true
        ) // o false se immagine scura
    }

    when {
        state.loading -> {
            // Mostra spinner o contenuto vuoto
        }

        state.listing == null -> {
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
                Text(text = "Errore nel caricamento dell'annuncio", fontSize = 22.sp)
            }
        }

        else -> {
            // Contenuto normale con listing
            if (listing != null) {
                val systemUiController = rememberSystemUiController()
                var title by remember { mutableStateOf(listing.title) }
                var address by remember { mutableStateOf(listing.address) }
                var municipality by remember { mutableStateOf(listing.municipality) }
                var postalCode by remember { mutableStateOf(listing.postalCode) }
                var province by remember { mutableStateOf(listing.province) }
                var size by remember { mutableStateOf(listing.size) }
                var numberOfRooms by remember { mutableStateOf(listing.numberOfRooms) }
                var energyClassText by remember { mutableStateOf(listing.energyClass.toString()) }
                val energyClass: Char = energyClassText.firstOrNull() ?: ' '

                var description by remember { mutableStateOf(listing.description) }
                var priceText by remember { mutableStateOf(listing.price.toString()) }
                val price: Long = priceText.toLongOrNull() ?: 0L
                var category by remember { mutableStateOf(listing.category) }
                var floor by remember { mutableStateOf(listing.floor) }
                var hasElevator by remember { mutableStateOf(listing.hasElevator) }
                var hasAirConditioning by remember { mutableStateOf(listing.hasAirConditioning) }
                var hasGarage by remember { mutableStateOf(listing.hasGarage) }

                Box(modifier = Modifier.fillMaxSize()) {
                    Column( //Colonna che controlla la possibilità di scrollare
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(scrollState)
                            .padding(start = 10.dp,end = 10.dp, top = 50.dp, bottom = 100.dp)
                    ) {
                        Text(
                            text = "Modifica Informazioni",
                            style = LocalAppTypography.current.sectionTitle,
                            modifier = Modifier.padding(vertical = 6.dp),
                        )

                        LabeledTextField(
                            label = "Titolo",
                            value = title,
                            onValueChange = { title = it })
                        LabeledTextField(
                            label = "Indirizzo",
                            value = address,
                            onValueChange = { address = it })
                        LabeledTextField(
                            label = "Comune",
                            value = municipality,
                            onValueChange = { municipality = it })
                        LabeledNumberField(
                            label = "Codice Postale",
                            value = postalCode,
                            onValueChange = { postalCode = it })
                        LabeledTextField(
                            label = "Provincia",
                            value = province,
                            onValueChange = { province = it })
                        LabeledNumberField(
                            label = "Dimensione",
                            value = size,
                            onValueChange = { size = it })
                        dropDownMenu(
                            label = "Stanze",
                            value = numberOfRooms,
                            options = (1..20).map { it.toString() },
                            onValueChange = { numberOfRooms = it })

                        dropDownMenu(
                            label = "Classe Energetica",
                            value = energyClassText,
                            options = listOf("A", "B", "C", "D", "E"),
                            onValueChange = { energyClassText = it })
                        LabeledNumberField(
                            label = "Prezzo",
                            value = priceText,
                            onValueChange = { priceText = it })
                        dropDownMenu(
                            label = "Categoria",
                            value = category,
                            options = listOf("Vendita", "Affitto"),
                            onValueChange = { category = it })
                        dropDownMenu(
                            label = "Piani",
                            value = floor,
                            options = (1..20).map { it.toString() },
                            onValueChange = { floor = it })

                        Row(
                            modifier = Modifier
                                .padding(15.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Ascensore",
                                modifier = Modifier.weight(1f)
                            )
                            Checkbox(
                                checked = hasElevator,
                                onCheckedChange = { hasElevator = it }
                            )
                        }
                        LabeledTextField(
                            label = "Aria Condizionata",
                            value = address,
                            onValueChange = { title = it })
                        LabeledTextField(
                            label = "Garage",
                            value = address,
                            onValueChange = { title = it })

                        LabeledTextField(
                            label = "Descrizione",
                            value = description,
                            onValueChange = { description = it })


                    }
                    BottomBar(
                        formatNumberWithDots(listing.price),
                        modifier = Modifier.align(Alignment.BottomCenter)
                    )
                }
            }
        }
    }
}

@Composable
fun dropDownMenu(
    label: String,
    value: String,
    options: List<String>,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .padding(15.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = LocalAppTypography.current.featureTitle,
            fontSize = 16.sp
        )
        Spacer(modifier = Modifier.weight(1f))
        SimpleDropdownSelector(
            options = options,
            selectedOption = value,
            onOptionSelected = { onValueChange(it) }
        )
    }
}

@Composable
fun LabeledTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .padding(15.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = LocalAppTypography.current.featureTitle,
            fontSize = 16.sp
        )
        Spacer(modifier = Modifier.width(30.dp))
        TextField(
            value = value,
            onValueChange = onValueChange,
            textStyle = TextStyle(
                fontFamily = RobotoSlab,
                fontWeight = FontWeight.Light,
                fontSize = 12.sp
            ),
            modifier = Modifier
                .height(48.dp)
        )
    }
}

@Composable
fun LabeledNumberField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(15.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = LocalAppTypography.current.featureTitle,
            fontSize = 16.sp
        )

        Spacer(modifier = Modifier.weight(1f))
        TextField(
            value = value,
            onValueChange = { input ->
                if (input.all { it.isDigit() }) {
                    onValueChange(input)
                }
            },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            singleLine = true,
            textStyle = TextStyle(
                fontFamily = RobotoSlab,
                fontWeight = FontWeight.Light,
                fontSize = 12.sp
            ),
            modifier = Modifier
                .height(48.dp)
                .width(120.dp)
        )
    }
}


@Composable
private fun BottomBar(price: String, modifier: Modifier = Modifier) {
    val navBarPadding = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    Surface(
        modifier
            .fillMaxWidth()
            .height(80.dp)
            .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
            .padding(bottom = navBarPadding)
            .border(1.dp, Color(0xFF9E9E9E), RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)),
        color = Color.White,
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {

            CustomButton(
                text = "Annulla",
                style = "white",
                onClick = { /*TODO*/ },
                modifier = Modifier.width(140.dp)
            )

            CustomButton(
                text = "Salva",
                icon = Icons.Outlined.AttachMoney,
                style = "white",
                onClick = { /*TODO*/ },
                modifier = Modifier.width(140.dp)
            )

        }

    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> SimpleDropdownSelector(
    options: List<T>,
    selectedOption: T,
    onOptionSelected: (T) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = selectedOption.toString(),
            onValueChange = {},
            readOnly = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            modifier = Modifier
                .menuAnchor()
                .width(80.dp)
                .height(50.dp)
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option.toString()) },
                    onClick = {
                        onOptionSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun prova() {
    val navController = rememberNavController()
    val testListing = Listing(
        id = "1",
        title = "Appartamento moderno con vista",
        address = "Via delle Rose 15",
        municipality = "San Giovanni",
        postalCode = "00100",
        province = "RM",
        size = "85",
        latitude = 41.9028,
        longitude = 12.4964,
        numberOfRooms = "3",
        energyClass = 'B',
        nearbyPlaces = arrayListOf("Scuola:200m", "Parco:150m", "Supermercato:100m"),
        description = "Appartamento luminoso con ampio soggiorno, cucina abitabile, due camere da letto e bagno. Situato in zona tranquilla con tutti i servizi nelle vicinanze.",
        price = 285000,
        category = "Vendita",
        floor = "2",
        hasElevator = true,
        hasAirConditioning = true,
        hasGarage = false,
        imageUrls = listOf()
    )
    //ModifyListingScreen(navController, testListing)
}