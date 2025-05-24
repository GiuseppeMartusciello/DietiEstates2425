package com.example.dietiestates.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import com.example.dietiestates.ui.screens.components.CustomButton
import com.example.dietiestates.ui.screens.components.ListingCardMini
import com.example.dietiestates.ui.theme.RobotoSerif

@Composable
fun MyOffersScreen(
    navController: NavController, viewModel: MyOfferViewModel = viewModel()
) {

    Log.d("DEBUG", "MyOffersScreen inizializzata")

    val uiState by viewModel.uiState.collectAsState()

    Log.d("DEBUG", "uistate corrente: ${uiState}")
    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            IconButton(
                modifier = Modifier.padding(top = 26.dp),
                onClick = { navController.navigate("home") }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Indietro")

            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 30.dp),
                contentAlignment = Alignment.TopCenter
            ) {
                Text(
                    text = "Le mie Offerte", fontFamily = RobotoSerif,
                    fontWeight = FontWeight.SemiBold, fontSize = 40.sp, color = Color(0xFF3F51B5)
                )


            }


        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.TopCenter

        ) {
            when (uiState) {
                is MyOffersState.Loading -> {
                    CircularProgressIndicator()
                }

                is MyOffersState.Error -> {
                    Text(
                        text = (uiState as MyOffersState.Error).message,
                        color = Color.Red,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                is MyOffersState.Success -> {
                    val listings = (uiState as MyOffersState.Success).listings
                    if (listings.isEmpty())
                        Text(
                            text = "Non hai mai fatto ancora nessuna offerta.",
                            fontFamily = RobotoSerif,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(16.dp)
                        )
                    else {
                        LazyColumn(
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(listings) { listing ->
                                Log.d("DEBUG", "Rendering listing: ${listing.title}")
                                ListingCardMini(listing = listing,
                                    onClick = {
                                        navController.currentBackStackEntry?.savedStateHandle?.set("listing", listing)
                                        navController.navigate("listing/offer/${listing.id}")


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
