package com.example.dietiestates.ui.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.AlertDialog
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.TabRowDefaults.Divider
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.dietiestates.ui.theme.RobotoSlab
import com.example.dietiestates.ui.viewModel.ResearchViewModel
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Elevator
import androidx.compose.material3.*
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.zIndex
import com.example.dietiestates.ui.screens.components.AppTopBar
import com.example.dietiestates.ui.screens.components.TopBarOffer


@Composable
fun FilterScreen(
    navController: NavController,
    viewModel: ResearchViewModel,
){

    Scaffold(
        topBar = {
            TopBarOffer(navController = navController, modifier = Modifier, "Filtri")
        }
    ) { paddingValues ->
        Box (
            modifier = Modifier
                .fillMaxWidth()
                .padding(paddingValues)
                .padding(vertical = 0.dp)
        ) {

            Filtering(viewModel,navController)

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = 50.dp)
            )
            {
                SearchButton(
                    viewModel,
                    navController,
                    modifier = Modifier
                        .zIndex(2f)
                        .padding(horizontal = 16.dp)
                        .align(Alignment.BottomCenter)
                )
            }
        }
    }
}


@Composable
fun Filtering(viewModel: ResearchViewModel, navController: NavController) {

    val category = viewModel.researchFormState.category
    val energyClass = viewModel.researchFormState.energyClass

    val hasElevator = viewModel.researchFormState.hasElevator
    val hasAirConditioning = viewModel.researchFormState.hasAirConditioning
    val hasGarage = viewModel.researchFormState.hasGarage

    val minPrice = viewModel.researchFormState.minPrice
    val maxPrice = viewModel.researchFormState.maxPrice

    val numberOfRooms = viewModel.researchFormState.numberOfRooms
    val minSize = viewModel.researchFormState.minSize

    var numberOfRoomsText by remember { mutableStateOf(numberOfRooms.toString()) }
    var minSizeText by remember { mutableStateOf(minSize.toString()) }

    val enabled = !viewModel.isOldResearch


    val (currentMinValue, currentMaxValue, currentStep) = when (category) {
        "SALE" -> listOf(100000f, 10000000f, 10000f)
        else -> listOf(300f, 10000f, 100f)
    }

    Column(
        modifier = Modifier
            .padding(horizontal = 15.dp)
    ) {
        Spacer(modifier = Modifier.height(12.dp))
        ContractTypeSelector(
            category,
            onTypeSelected = {
                if (it == "SALE") {
                    viewModel.updateResearchFormState {
                        copy(category = "SALE", minPrice = 100000, maxPrice = 10000000)
                    }
                } else {
                    viewModel.updateResearchFormState {
                        copy(category = "RENT", minPrice = 300, maxPrice = 10000000)
                    }
                }
                viewModel.updateResearchFormState {
                    copy(category = it)
                }
            },
            enabled
        )

        CustomSlider(
            minPrice,
            maxPrice,
            { viewModel.updateResearchFormState { copy(minPrice = it.toInt()) }},
            { viewModel.updateResearchFormState {copy(maxPrice = it.toInt())}},
            currentMinValue,
            currentMaxValue,
            currentStep,
            enabled
        )

       Row() {
           CostumNumberOfRoom(
               modifier = Modifier.weight(1f),
               text = numberOfRoomsText.toString(),
               onValidNumber = { viewModel.updateResearchFormState { copy(numberOfRooms = it) } },
               onTextChange = { numberOfRoomsText = it },
               enabled
           )
           CostumSizeInput(
               modifier = Modifier.weight(1f),
               value = minSizeText.toString(),
               onValueChange = { minSizeText = it },
               onValid = { viewModel.updateResearchFormState { copy(minSize = it) } },
               enabled
           )
       }
        Spacer(modifier = Modifier.height(25.dp))

        Text(
            text = "Altre opzioni",
            fontFamily = RobotoSlab,
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )

        Divider(
            thickness = 1.dp,
            color = Color(0xFF3F51B5),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )

        EnergyClassSelector(
            selectedClass = energyClass,
            onClassSelected = { viewModel.updateResearchFormState{copy(energyClass = it)}},
            enabled
        )

        BooleanOptionsSelector(
            hasElevator,
            hasAirConditioning,
            hasGarage,
            onOptionChanged = { option, value ->
                when (option) {
                    "elevator" -> viewModel.updateResearchFormState { copy(hasElevator = value) }
                    "air" -> viewModel.updateResearchFormState { copy(hasAirConditioning = value) }
                    "garage" -> viewModel.updateResearchFormState { copy(hasGarage = value)}
                }
            },
            enabled
        )
    }


}



@Composable
fun SearchButton(
    viewModel: ResearchViewModel,
    navController: NavController,
    modifier: Modifier
    )
{
    Button(
        onClick = {
            //se non è una ricerca vecchia, crea una nuova
            if(!viewModel.isOldResearch) {viewModel.createResearch()}
            navController.navigate("searchedscreen")
        },
        colors = ButtonDefaults.buttonColors(Color(0xFF3F51B5)),
        shape = RoundedCornerShape(20),
        modifier = modifier
    ) {
        Text(
            "Cerca",
            fontFamily = RobotoSlab,
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp,
        )
    }

}


@Composable
fun CustomSlider(
    minPrice: Int,
    maxPrice: Int,
    onMinPriceChange: (Float) -> Unit,
    onMaxPriceChange: (Float) -> Unit,
    minValue: Float ,
    maxValue: Float,
    step: Float,
    enabled : Boolean,
) {

    Text("Prezzo Minimo: €${minPrice.toInt()}")
    Slider(
        value = minPrice.toFloat(),
        onValueChange = { onMinPriceChange(it.coerceAtMost(maxPrice.toFloat())) },
        valueRange = minValue..maxValue,
        steps = ((maxValue - minValue) / step).toInt() - 1,
        colors = SliderDefaults.colors(
            thumbColor = Color(0xFF3F51B5),
            activeTrackColor = Color(0xFF3F51B5),
        ),
        enabled = enabled
    )

    Spacer(modifier = Modifier.height(16.dp))

    Text("Prezzo Massimo: €${maxPrice.toInt()}")
    Slider(
        value = maxPrice.toFloat(),
        onValueChange = { onMaxPriceChange(it.coerceAtLeast(minPrice.toFloat())) },
        valueRange = minValue..maxValue,
        steps = ((maxValue - minValue) / step).toInt() - 1,
        colors = SliderDefaults.colors(
            thumbColor = Color(0xFF3F51B5),
            activeTrackColor = Color(0xFF3F51B5),
        ),
        enabled = enabled
    )
}

@Composable
fun ContractTypeSelector(
    category: String,
    onTypeSelected: (String) -> Unit,
    enabled: Boolean
) {
    val options = listOf("SALE", "RENT")

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
            .padding(bottom = 10.dp),
    horizontalArrangement = Arrangement.Center,
    ) {
        options.forEach { type ->
            Button(
                onClick = { if(enabled) onTypeSelected(type) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (category == type) Color(0xFF3F51B5) else Color.LightGray,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(20),
                modifier = Modifier
                    .padding(horizontal =  8.dp)
            ) {
                Text(type)
            }
        }
    }
}


@Composable
fun CostumNumberOfRoom(
    modifier : Modifier,
    text: String,
    onValidNumber: (Int) -> Unit,
    onTextChange: (String) -> Unit,
    enabled: Boolean
) {
    val maxChars = 2
    val isError = false

    var hasFocusedText by remember { mutableStateOf(false) }

        OutlinedTextField(
            value = text,
            onValueChange = {
                if (it.length <= maxChars) {
                    onTextChange(it)
                    onValidNumber(it.toIntOrNull() ?: 1)
                }
            },
            label = { Text("Numero di stanze") },
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            isError = isError,
            singleLine = true,
            modifier = modifier
                .heightIn(min = 56.dp)
                .onFocusChanged { focusState ->
                    if (focusState.isFocused && !hasFocusedText) {
                        onTextChange("")
                        hasFocusedText = true
                    }
                },
            enabled = enabled
        )
}

@Composable
fun CostumSizeInput(
    modifier: Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    onValid: (Int) -> Unit,
    enabled: Boolean
)
{
    var hasFocusedValue by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = value,
        onValueChange = {
            onValueChange(it)
            onValid(it.toIntOrNull() ?: 1)
        },
        label = { Text("Superficie min.") },
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = KeyboardType.Number ,
            imeAction = ImeAction.Done
        ),
        singleLine = true,
        modifier = modifier
            .heightIn(min = 56.dp)
            .onFocusChanged { focusState ->
                if (focusState.isFocused && !hasFocusedValue) {
                    onValueChange("")
                    hasFocusedValue = true
                }
            },
        enabled = enabled
    )
}


@Composable
fun EnergyClassSelector(
    selectedClass: String,
    onClassSelected: (String) -> Unit,
    enabled: Boolean
) {
    val options = listOf("A", "B", "C", "D", "E", "F", "G")
    var showDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (enabled) Modifier.clickable { showDialog = true }
                else Modifier
            )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .border(1.dp, Color.LightGray, RoundedCornerShape(4.dp))
        ) {
            Text(
                text = "  Classe energetica",
                fontFamily = RobotoSlab,
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp,
                )

            Spacer(modifier = Modifier.width(137.dp))

            Text(
                text = selectedClass,
                fontSize = 18.sp,
                modifier = Modifier
                    .padding(horizontal = 4.dp, vertical = 10.dp)
            )
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Seleziona classe energetica") },
            text = {
                Column {
                    options.forEach { item ->
                        Text(
                            text = item,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onClassSelected(item)
                                    showDialog = false
                                }
                                .padding(12.dp)
                        )
                    }
                }
            },
            confirmButton = {},
            dismissButton = {}
        )
    }
}


@Composable
fun BooleanOptionsSelector(
    hasElevator: Boolean?,
    hasAirConditioning: Boolean?,
    hasGarage: Boolean?,
    onOptionChanged: (String, Boolean) -> Unit,
    enabled: Boolean
) {
    Column(modifier = Modifier.padding(16.dp)) {

        CheckOptionWithIcon(
            label = "Ascensore",
            icon = Icons.Default.Elevator,
            checked = hasElevator == true,
            onCheckedChange = { onOptionChanged("elevator", it) },
            enabled = enabled

        )

        CheckOptionWithIcon(
            label = "Aria condizionata",
            icon = Icons.Default.Air,
            checked = hasAirConditioning == true,
            onCheckedChange = { onOptionChanged("air", it) },
            enabled = enabled
        )

        CheckOptionWithIcon(
            label = "Garage",
            icon = Icons.Default.DirectionsCar,
            checked = hasGarage == true,
            onCheckedChange = { onOptionChanged("garage", it) },
            enabled = enabled
        )
    }
}

@Composable
fun CheckOptionWithIcon(
    label: String,
    icon: ImageVector,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    enabled: Boolean
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { if(enabled) onCheckedChange(!checked) }
            .padding(vertical = 6.dp)
            .alpha(if (enabled) 1f else 0.5f)
    ) {
        Icon(imageVector = icon, contentDescription = label)
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            label,
            fontFamily = RobotoSlab,
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp ,
            modifier = Modifier.weight(1f))
        Checkbox(
            checked = checked,
            onCheckedChange = { if (enabled) onCheckedChange(it) },
            colors = CheckboxDefaults.colors(
                checkedColor = Color(0xFF3F51B5),
            )
        )
    }
}



