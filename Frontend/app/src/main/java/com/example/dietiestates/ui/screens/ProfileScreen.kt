package com.example.dietiestates.ui.screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Error
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LocalOffer
import androidx.compose.material.icons.outlined.Logout
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.example.dietiestates.ui.screens.components.TopBarOffer
import com.example.dietiestates.ui.theme.LocalAppTypography
import com.example.dietiestates.ui.theme.Roboto
import com.example.dietiestates.ui.theme.RobotoSerif
import com.example.dietiestates.ui.theme.RobotoSlab
import com.example.dietiestates.ui.viewModel.ProfileViewModel
import com.example.dietiestates.utility.TokenManager
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

    Scaffold(
        topBar = { TopBarOffer(navController = navController, modifier = Modifier, "Profilo") },
        bottomBar = {
        NavBar(navController = navController)
    }) { paddingValues ->
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
                    Text(text = "Errore nel caricamento del profilo", fontSize = 22.sp)
                }
            }

            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .background(Color.White)
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
                        "${client.name} ${client.surname}",
                        fontFamily = Roboto,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 22.sp,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    ProfileField("Email", client.email)
                    ProfileField("Indirizzo", client.address ?: "Non presente")
                    ProfileField("Data di nascita", client.birthDate ?: "Non presente")
                    ProfileField(
                        "Genere",
                        when (client.gender) {
                            "MALE" -> "Maschio"
                            "FEMALE" -> "Femmina"
                            else -> "Non specificato"
                        }
                    )
                    ProfileField("Telefono", client.phone ?: "Non presente")

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
                    Spacer(modifier = Modifier.height(40.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp)
                            .border(
                                width = 1.dp,
                                color = Color.LightGray,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clip(RoundedCornerShape(12.dp))
                            .padding(vertical = 15.dp, horizontal = 20.dp)
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

@Composable
fun ProfileField(label: String, value: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
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
    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp)) {
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
