package com.example.dietiestates.ui.screens



import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBackIosNew
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
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
import com.example.dietiestates.ui.theme.RobotoSerif
import com.example.dietiestates.ui.theme.RobotoSlab
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState


@Composable
fun MapSearchScreen(
    navController: NavController,
) {
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(40.85, 14.27), 12f)
    }
    val markerPosition = remember { mutableStateOf<LatLng?>(null) }
    val context = LocalContext.current


    Scaffold(topBar = {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(0.dp, 0.dp, 0.dp, 8.dp)
                .background(Color(0xFF3F51B5)),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "DietiEstates25", fontFamily = RobotoSerif,
                fontWeight = FontWeight.SemiBold, fontSize = 40.sp, color = Color.White
            )
            Text(
                modifier = Modifier
                    .padding(0.dp, 0.dp, 0.dp, 5.dp),
                text = "Perchè perder tempo quando ci siamo noi?", fontFamily = RobotoSlab,
                fontWeight = FontWeight.Normal, fontSize = 16.sp, color = Color.White
            )
        }
    }) { paddingValues ->
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

            Button(
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF3F51B5),
                    contentColor = Color.White),
                onClick = { navController.navigate("researchscreen") },
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(top=15.dp)
                    .padding(horizontal = 10.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.ArrowBackIosNew ,
                    contentDescription = "Icona posizione"
                )
            }

            Button(
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF3F51B5),
                    contentColor = Color.White
                ),
                onClick = {
                    val selected = markerPosition.value
                    if (selected != null) onClick(navController, markerPosition)
                    else {
                        Toast.makeText(
                            context,
                            "Seleziona una posizione sulla mappa",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 40.dp)
            ) {
                Text(
                    modifier = Modifier.padding(10.dp),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    text = "Continua")
            }
        }
    }
}

fun onClick(navController: NavController, markerPosition: MutableState<LatLng?>) {

    val lat = markerPosition.value?.latitude
    val lng = markerPosition.value?.longitude

}

