package com.example.dietiestates.ui.screens


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.dietiestates.ui.screens.components.ListingCardMini
import com.example.dietiestates.ui.viewModel.ListingOfferViewModel





@Composable
fun OfferScreen(navController: NavController, viewModel: ListingOfferViewModel = viewModel()) {
    val uiState by viewModel.uiState

    Scaffold(
        topBar = {
            Box(modifier = Modifier.fillMaxWidth()) {
                Text("Le tue offerte")

                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Indietro")

                }
            }
        }
    ) { padding ->
        when  {
            uiState.loading -> {
                CircularProgressIndicator()
            }
            uiState.error != null -> Text("Errore:")
            else -> {
                Box(
                    Modifier
                        .padding(padding)
                        .fillMaxSize(),
                    contentAlignment = Alignment.TopCenter
                ) {


//                    ListingCardMini(listing = ) {
//                    }

                    val offers = uiState.offers
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(offers) { offer ->
                            Text("Offerta: € ${offer.price} - Stato: ${offer.state} - Data: ${offer.date}")
                        }
                    }
                }
            }
        }
    }

}