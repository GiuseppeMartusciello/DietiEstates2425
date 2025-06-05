package com.example.dietiestates.ui.screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Error
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LocalOffer
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.dietiestates.R
import com.example.dietiestates.ui.theme.LocalAppTypography
import com.example.dietiestates.ui.theme.Roboto
import com.example.dietiestates.ui.theme.RobotoSerif
import com.example.dietiestates.ui.theme.RobotoSlab
import com.example.dietiestates.ui.viewModel.ProfileViewModel
import com.example.tuaapp.ui.components.NavBar
import com.example.tuaapp.ui.components.NavItem

@Composable
fun ProfileScreen(navController: NavController) {
    val viewModel: ProfileViewModel = viewModel()
    val client = viewModel.clientState.value.client
    val state = viewModel.clientState.value
    val scrollState = rememberScrollState()

    LaunchedEffect(Unit) {
        viewModel.getMe()
    }

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
    }, bottomBar = {
        NavBar(
            navController = navController, items = listOf(
                NavItem(
                    "home",
                    Icons.Outlined.Home, Icons.Filled.Home
                ), NavItem(
                    "notification",
                    Icons.Outlined.Notifications, Icons.Filled.Notifications
                ), NavItem(
                    "offer",
                    Icons.Outlined.LocalOffer, Icons.Filled.LocalOffer,
                ),
                NavItem(
                    "profile",
                    Icons.Outlined.Person, Icons.Filled.Person,
                )

            )
        )
    }) { paddingValues ->
        when {
            state.loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.5f)),
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
                    Text(text = "Errore nel caricamento del profilo", fontSize = 22.sp)
                }
            }
            else -> {
                Log.d("output","mostro: ${client}")
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(horizontal = 25.dp)
                        .verticalScroll(scrollState),
                ) {

                    Image( /* ToDo aggiornare text con il reale valore */
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
                        "${client.name} ${client.surname}",
                        fontFamily = Roboto,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 22.sp,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    ProfileField("Email", client.email)
                    ProfileField("Indirizzo", client.address)
                    ProfileField("Data di nascita", client.birthDate)
                    ProfileField(
                        "Genere",
                        if (client.gender == "MALE") "Maschio" else "Femmina"
                    )
                    ProfileField("Telefono", "3666404242")

                    ProfileFieldSwitch(
                        label = "Notifiche promozionali",
                        checked = client.promotionalNotification,
                        onCheckedChange = {
                            viewModel.updateNotification("promotional", it)
                        }
                    )
                    ProfileFieldSwitch(
                        label = "Notifiche offerte",
                        checked = client.offerNotification,
                        onCheckedChange = {
                            viewModel.updateNotification("offer", it)
                        }
                    )
                    ProfileFieldSwitch(
                        label = "Notifiche ricerca",
                        checked = client.searchNotification,
                        onCheckedChange = {
                            viewModel.updateNotification("search", it)
                        }
                    )

                }
            }
        }

    }
}

@Composable
fun ProfileField(label: String, value: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = label,
                style = LocalAppTypography.current.featureTitle,
                fontSize = 16.sp
            )
            Text(
                text = value,
                fontFamily = Roboto,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                color = Color.Gray
            )
        }
        Divider(
            thickness = 0.7.dp,
            color = Color(0xFFE0E0E0),
            modifier = Modifier.padding(vertical = 10.dp)
        )
    }
}

@Composable
fun ProfileFieldSwitch(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = label,
                style = LocalAppTypography.current.featureTitle,
                fontSize = 16.sp
            )
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(checkedThumbColor = Color.Blue)
            )
        }
    }
}
