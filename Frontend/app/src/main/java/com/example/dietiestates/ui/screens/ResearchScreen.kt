package com.example.dietiestates.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.TabRowDefaults.Divider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.dietiestates.data.model.Research
import com.example.dietiestates.ui.screens.components.CustomButton
import com.example.dietiestates.ui.screens.components.TopBarOffer
import com.example.dietiestates.ui.theme.RobotoSlab
import com.example.dietiestates.ui.viewModel.ResearchViewModel
import com.example.tuaapp.ui.components.NavBar
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.Circle
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun ResearchScreen(
    viewModel: ResearchViewModel,
    navController: NavController,
) {
    LaunchedEffect(Unit) {
        if (viewModel.isOldResearch) {
            viewModel.updateListResearch()
        }

        viewModel.fetchResearch10()

        viewModel.updateSelectedResearch(null)
        viewModel.isOldResearch = false

        viewModel.resetResearchForm()
    }

    Scaffold(
        topBar = {
            TopBarOffer(navController = navController, modifier = Modifier, "Ricerca")
        },
        bottomBar = { NavBar(navController = navController) }
    ) { paddingValues ->

        Box(
            modifier = Modifier
                .padding(paddingValues)
                .background(Color.White)
        ) {
            Research(
                navController,
                viewModel = viewModel
            )
        }
    }
}

@Composable
fun Research(
    navController: NavController,
    viewModel: ResearchViewModel = viewModel()
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    var query by remember { mutableStateOf("") }


    Column(modifier = Modifier
        .fillMaxWidth()
        .pointerInput(Unit)
        {
            detectTapGestures(onTap = {
                focusManager.clearFocus()
                keyboardController?.hide()
            })
        }
    ) {
        CustomOutlineTextField(
            viewModel,
            query,
            onQueryChange = { query = it },
            navController,
            keyboardController
        )

        History(
            navController,
            onSelect = { selectedQuery, research ->
                query = selectedQuery
                viewModel.updateSelectedResearch(research)
                viewModel.isOldResearch = true
            },
            viewModel
        )
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        MapButton(
            viewModel,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 30.dp),
            navController
        )
    }
}


@Composable
fun History(
    navController: NavController,
    onSelect: (String, Research) -> Unit,
    viewModel: ResearchViewModel
) {

    val state = viewModel.searchState.value

    Box(modifier = Modifier.fillMaxSize()) {
        Column {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = "Cronologia",
                textAlign = TextAlign.Center,
                fontFamily = RobotoSlab,
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp,
            )
            Divider(
                thickness = 1.dp,
                color = Color.LightGray,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )

            when {
                state.loading -> {
                    // puoi anche usare uno Spacer se vuoi spazio sopra il loader
                    Spacer(modifier = Modifier.height(32.dp))
                }

                state.error != null -> {
                    Text(
                        text = "Errore: ${state.error}",
                        color = Color.Red,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        textAlign = TextAlign.Center
                    )
                }

                viewModel.researchState.value.researches.isEmpty() -> {
                    Text(
                        text = "Ancora nessuna ricerca effettuata",
                        fontFamily = RobotoSlab,
                        fontWeight = FontWeight.Normal,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 100.dp),
                        verticalArrangement = Arrangement.spacedBy(0.dp),
                        contentPadding = PaddingValues(bottom = 16.dp)
                    ) {
                        items(viewModel.researchState.value.researches) { research ->
                            ResearchItem(
                                navController = navController,
                                research,
                                onSelect,
                                onDelete = { id ->
                                    viewModel.deleteResearch(id);
                                    viewModel.fetchResearch10()
                                },
                                viewModel
                            )
                        }
                    }
                }
            }
        }

        if (state.loading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0x88000000)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color(0xFF3F51B5))
            }
        }
    }

}


@Composable
fun ResearchItem(
    navController: NavController,
    research: Research,
    onSelect: (String, Research) -> Unit,
    onDelete: (String) -> Unit,
    viewModel: ResearchViewModel
) {
    var showDialog by remember { mutableStateOf(false) }

    val content = if (!research.municipality.isNullOrBlank()) {
        research.municipality
    } else {
        "latitudine:${research.latitude} longitudine:${research.longitude}"
    }


    if (showDialog) {
        val pos = LatLng(research.latitude!!, research.longitude!!)
        val markerPosition = remember { mutableStateOf<LatLng?>(null) }
        val targetLatLng = LatLng(research.latitude ?: 0.0, research.longitude ?: 0.0)

        val cameraPositionState = rememberCameraPositionState {
            position = CameraPosition.fromLatLngZoom(targetLatLng, 10f)
        }

        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Dettagli Ricerca") },
            text = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .padding(2.dp)
                ) {
                    GoogleMap(
                        modifier = Modifier.fillMaxSize(),
                        cameraPositionState = cameraPositionState,
                        onMapClick = { latLng ->
                            markerPosition.value = latLng
                        }
                    ) {
                        Marker(
                            state = MarkerState(position = pos),
                            title = "Posizione della ricerca"
                        )
                        Circle(
                            center = pos,
                            radius = research.radius!!.toDouble(), // in metri
                            strokeColor = Color(0x663F51B5),
                            fillColor = Color(0x333F51B5),
                            strokeWidth = 2f
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    showDialog = false

                    onSelect(content, research)
                    viewModel.updateResearch()

                    navController.navigate("searchedscreen")
                }) {
                    Text("Ripeti ricerca", fontSize = 16.sp)
                }
            },

            dismissButton = {
                TextButton(onClick = {
                    showDialog = false
                    //onExit()
                }) {
                    Text("Esci", fontSize = 16.sp)
                }
            }
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp, horizontal = 10.dp)
            .clickable(onClick = {
                if (!research.municipality.isNullOrBlank()) {
                    onSelect(content, research); viewModel.updateResearch()
                } else {
                    showDialog = true
                }
            }),
        border = BorderStroke(1.dp, Color.LightGray),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF3F3F3),
        )
    )
    {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.History,
                contentDescription = "Icona cronologia",
                modifier = Modifier
                    .size(30.dp)
                    .padding(end = 8.dp)
            )

            val text = if (!research.municipality.isNullOrBlank()) research.municipality
            else "Ricerca per raggio ${research.radius?.toInt()}m"

            Box(modifier = Modifier.weight(1f)) {
                Text(
                    text = text,
                    fontFamily = RobotoSlab,
                    fontWeight = FontWeight.Normal,
                    fontSize = 14.sp
                )
            }

            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Rimuovi ricerca",
                tint = Color.Red,
                modifier = Modifier
                    .size(20.dp)
                    .clickable { onDelete(research.id) }
            )
        }
    }
}


@Composable
fun MapButton(
    viewModel: ResearchViewModel,
    modifier: Modifier = Modifier,
    navController: NavController,
) {
    CustomButton(onClick = {
        viewModel.updateResearchFormState {
            copy(
                municipality = "",
                searchType = "COORDINATES"
            )
        }
        navController.navigate("mapscreen")
    },
        style = "blue",
        icon = Icons.Outlined.Map,
        text = "Ricerca Avanzata",
        modifier = modifier.padding(10.dp)
    )
}

@Composable
fun CustomOutlineTextField(
    viewModel: ResearchViewModel,
    query: String,
    onQueryChange: (String) -> Unit,
    navController: NavController,
    keyboardController: SoftwareKeyboardController?
) {
    val focusRequester = remember { FocusRequester() }
    var textFieldWidth by remember { mutableIntStateOf(0) }
    var showAlert by remember { mutableStateOf(false) }

    fun onSearch() {
        if (!query.isBlank()) {
            viewModel.updateResearchFormState {
                copy(
                    searchType = "MUNICIPALITY",
                    municipality = query,
                    latitude = "",
                    longitude = "",
                    radius = ""
                )
            }
            if (viewModel.isOldResearch) {
                navController.navigate("searchedscreen")
            } else {
                navController.navigate("filterscreen")
                keyboardController?.hide()
            }
        } else {
            showAlert = true
        }
    }

    Spacer(modifier = Modifier.height(8.dp))

    OutlinedTextField(
        value = query,
        onValueChange = {
            onQueryChange(it)
            viewModel.updateSelectedResearch(null)
            viewModel.isOldResearch = false
        },
        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(onDone = { onSearch() }),

        trailingIcon = {
            IconButton(
                onClick = { onSearch() }) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Cerca"
                )
            }
        },
        modifier = Modifier

            .padding(horizontal = 32.dp)
            .fillMaxWidth()
            .onGloballyPositioned {
                textFieldWidth = it.size.width
            }
            .focusRequester(focusRequester)
            .clickable {
                focusRequester.requestFocus()
                keyboardController?.show()
            },
        singleLine = true
    )

    if (showAlert) {
        AlertDialog(
            onDismissRequest = { showAlert = false },
            title = { Text("Attenzione") },
            text = { Text("Inserisci un comune per avviare la ricerca.") },
            confirmButton = {
                TextButton(onClick = { showAlert = false }) {
                    Text("Ok")
                }
            }
        )
    }
}
