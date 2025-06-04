package com.example.dietiestates.ui.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.dietiestates.ui.screens.components.AppTopBar
import com.example.dietiestates.ui.screens.components.GoBackButton
import com.example.dietiestates.ui.viewModel.ResearchViewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState


@Composable
fun MapSearchScreen(
    viewModel: ResearchViewModel,
    navController: NavController,
) {
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(40.85, 14.27), 12f)
    }
    val markerPosition = remember { mutableStateOf<LatLng?>(null) }
    val context = LocalContext.current

    Scaffold(
        topBar = { AppTopBar(modifier = Modifier) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {

            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                onMapClick = { latLng ->
                    markerPosition.value = latLng
                }
            ) {
                markerPosition.value?.let { position ->
                    Marker(
                        state = MarkerState(position = position),
                        title = "Punto selezionato"
                    )
                }
            }

            GoBackButton(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(top=10.dp)
                    .padding(horizontal = 10.dp),
                navController,
                "researchscreen")

            CostumeButton(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 40.dp),
                context,
                markerPosition,
                navController,
                viewModel
            )

        }
    }
}

@Composable
fun CostumeButton(
    modifier: Modifier,
    context: Context,
    markerPosition: MutableState<LatLng?>,
    navController: NavController,
    viewModel: ResearchViewModel
) {
    var showDialog by remember { mutableStateOf(false) }
    var quantity by remember { mutableIntStateOf(0) }


    if (showDialog) {
        QuantityPickerDialog(
            showDialog = true,
            onDismiss = { showDialog = false },
            onConfirm = {
                quantity = it
                showDialog = false
                // Chiamata alla logica solo dopo conferma
                onClick(navController,quantity, markerPosition, viewModel)
            }
        )
    }

    Button(
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF3F51B5),
            contentColor = Color.White
        ),
        onClick = {
            val selected = markerPosition.value
            if (selected != null) {
                showDialog = true
            } else {
                Toast.makeText(
                    context,
                    "Seleziona una posizione sulla mappa",
                    Toast.LENGTH_SHORT
                ).show()
            }
        },
        modifier = modifier
    ) {
        Text(
            modifier = Modifier.padding(10.dp),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            text = "Continua"
        )
    }
}


fun onClick(
    navController: NavController,
    quantity: Int,
    markerPosition: MutableState<LatLng?>,
    viewModel: ResearchViewModel
) {

    val lat = markerPosition.value?.latitude.toString()
    val lng = markerPosition.value?.longitude.toString()

    viewModel.updateResearchFormState {copy(latitude = lat,longitude = lng,radius = (quantity * 1000).toString())}
    navController.navigate("filterScreen")
}



@Composable
fun QuantityPickerDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (Int) -> Unit
) {
    var sliderValue by remember { mutableFloatStateOf(0f) }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Seleziona una distanza") },
            text = {
                Column {
                    Text(text = "${sliderValue.toInt()} km", style = MaterialTheme.typography.bodyLarge)
                    Spacer(modifier = Modifier.height(16.dp))
                    Slider(
                        value = sliderValue,
                        onValueChange = { sliderValue = it },
                        valueRange = 0f..100f,
                        steps = 99 // 100 valori interi
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { onConfirm(sliderValue.toInt()) }) {
                    Text("Conferma")
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text("Annulla")
                }
            }
        )
    }
}

