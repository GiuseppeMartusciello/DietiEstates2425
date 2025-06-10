package com.example.dietiestates.ui.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.dietiestates.ui.screens.components.ListingCard
import com.example.dietiestates.ui.viewModel.MyOfferViewModel
import com.example.dietiestates.ui.viewModel.MyOffersState
import com.example.dietiestates.ui.viewModel.RegistrationViewModel
import androidx.compose.ui.Modifier
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.dietiestates.AppContainer
import com.example.dietiestates.ui.screens.components.AppBottomBar
import com.example.dietiestates.ui.screens.components.CustomButton
import com.example.dietiestates.ui.screens.components.ListingCardMini
import com.example.dietiestates.ui.screens.components.TopBarOffer
import com.example.dietiestates.ui.theme.RobotoSerif

@Composable
fun MyOffersScreen(
    navController: NavController, viewModel: MyOfferViewModel = viewModel()
) {
    val userRole = AppContainer.tokenManager.getUserRole()

    Log.d("DEBUG", "MyOffersScreen inizializzata")

    val uiState by viewModel.uiState.collectAsState()

    Log.d("DEBUG", "uistate corrente: ${uiState}")
    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            var text = if (userRole == "CLIENT") "Le mie offerte" else "Offerte"
            TopBarOffer(navController = navController, modifier = Modifier, text)
        },
        bottomBar = {
            AppBottomBar(navController = navController) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.TopCenter

        ) {
            when (uiState) {
                is MyOffersState.Loading -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator()
                    }

                }

                is MyOffersState.Error -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = (uiState as MyOffersState.Error).message,
                            color = Color.Red,
                        )
                    }

                }

                is MyOffersState.Success -> {
                    val listings = (uiState as MyOffersState.Success).listings
                    if (listings.isEmpty())
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "Ancora nessuna offerta.",
                                fontFamily = RobotoSerif,
                                fontSize = 25.sp,
                                style = MaterialTheme.typography.bodyLarge,
                            )
                        }
                    else {
                        LazyColumn(
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(listings) { listing ->
                                Log.d("AJA", "Rendering listing: ${listing.id}")

                                ListingCardMini(listing = listing,
                                    onClick = {
                                        if (userRole == "CLIENT")
                                            navController.navigate("listing/offer/${listing.id}")
                                        else
                                            navController.navigate("agent/listing/offer/${listing.id}")
                                    }
                                )

                            }
                        }

                    }


                }
            }
        }
    }
}

