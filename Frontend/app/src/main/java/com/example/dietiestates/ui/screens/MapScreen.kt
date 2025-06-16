package com.example.dietiestates.ui.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.dietiestates.ui.screens.components.TopBarOffer
import com.example.dietiestates.ui.viewModel.ResearchViewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.Circle
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState


@Composable
fun MapSearchScreen(
    viewModel: ResearchViewModel,
    navController: NavController,
) {
    val sliderValue = remember { mutableFloatStateOf(0f) } // valore iniziale in km

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(40.85, 14.27), 12f)
    }
    val markerPosition = remember { mutableStateOf<LatLng?>(null) }
    val context = LocalContext.current

    Scaffold(
        topBar = {  TopBarOffer(navController = navController, modifier = Modifier, "Ricerca") }
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
                    Circle(
                        center = position,
                        radius = (sliderValue.value * 500).toDouble(), // in metri
                        strokeColor = Color(0x663F51B5),
                        fillColor = Color(0x333F51B5),
                        strokeWidth = 2f
                    )
                }
            }
            Box(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 0.dp)
                    .height(300.dp), // altezza visibile dello slider
                contentAlignment = Alignment.Center
            ) {
                CustomSlider(
                    sliderValue ,
                    enabled = markerPosition.value != null
                )
            }

                CostumeButton(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 15.dp),
                context,
                markerPosition,
                navController,
                viewModel,
                    sliderValue.value,
            )

        }
    }
}


@Composable
fun CustomSlider(sliderValue: MutableState<Float>,enabled: Boolean)
{
        Box(
            modifier = Modifier
                .offset(x = 120.dp) // ⬅ COMPENSA la rotazione
                .rotate(-90f)
                .width(300.dp)     // ⬅ questa è la "lunghezza" dopo la rotazione
        ) {
            Slider(
                modifier = Modifier
                    .width(250.dp),
                value = sliderValue.value,
                onValueChange = { sliderValue.value = it },
                valueRange = 0f..50f,
                steps = 100,
                enabled = enabled,
                colors = SliderDefaults.colors(
                    thumbColor = Color(0xFF3F51B5),
                    activeTrackColor = Color(0xFF3F51B5),
                    inactiveTrackColor = Color.White
                )
            )
        }
    }



@Composable
fun CostumeButton(
    modifier: Modifier,
    context: Context,
    markerPosition: MutableState<LatLng?>,
    navController: NavController,
    viewModel: ResearchViewModel,
    sliderValue: Float
) {

    Button(
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF3F51B5),
            contentColor = Color.White
        ),
        onClick = {
            val selected = markerPosition.value
            if (selected != null) {
                onClick(navController,sliderValue, markerPosition, viewModel)

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
            modifier = Modifier.padding(3.dp),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            text = "Continua"
        )
    }
}


fun onClick(
    navController: NavController,
    quantity: Float,
    markerPosition: MutableState<LatLng?>,
    viewModel: ResearchViewModel
) {

    val lat = markerPosition.value?.latitude.toString()
    val lng = markerPosition.value?.longitude.toString()

    viewModel.updateResearchFormState {copy(latitude = lat,longitude = lng,radius = (quantity * 1000).toString())}
    navController.navigate("filterScreen")
}



