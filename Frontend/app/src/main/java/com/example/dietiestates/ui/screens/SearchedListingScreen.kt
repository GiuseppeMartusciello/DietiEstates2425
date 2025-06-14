package com.example.dietiestates.ui.screens


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import com.example.dietiestates.ui.screens.components.ErrorHandle
import com.example.dietiestates.ui.screens.components.ListingCard
import com.example.dietiestates.ui.screens.components.TopBarOffer
import com.example.dietiestates.ui.viewModel.ResearchViewModel
import com.example.tuaapp.ui.components.NavBar
import com.google.accompanist.systemuicontroller.rememberSystemUiController

@Composable
fun SearchedListingScreen(
    viewmodel: ResearchViewModel,
    navController: NavController
) {

    val viewState by viewmodel.searchState
    val systemUiController = rememberSystemUiController()


    SideEffect {
        systemUiController.setStatusBarColor(
            Color(0xFF3F51B5),
            darkIcons = true
        ) // o false se immagine scura

        viewmodel.updateListResearch()
    }


    Scaffold(
        topBar = { TopBarOffer(navController = navController, modifier = Modifier, "Risultati") },
        bottomBar = { NavBar(navController = navController) }

    ) { paddingValues ->
        when {
            viewState.loading -> CircularProgressIndicator()

            viewState.error != null -> ErrorHandle()

            else -> {
                if (viewState.listings.isEmpty()) EmptyResearch(paddingValues, navController)
                else ListingScroll(paddingValues, viewmodel, navController)

            }

        }
    }
}

@Composable
fun EmptyResearch(paddingValues: PaddingValues, navController: NavController) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        contentAlignment = Alignment.Center
    ) {

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                Icons.Outlined.Info,
                contentDescription = null,
                tint = Color.Gray,
                modifier = Modifier.size(60.dp)
            )
            Text(
                text = "Nessun immobile trovato per questa ricerca",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun ListingScroll(
    paddingValues: PaddingValues,
    viewModel: ResearchViewModel,
    navController: NavController
) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(paddingValues)
    )
    {

        LazyColumn(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 2.dp)
                .zIndex(2f),
            contentPadding = PaddingValues(bottom = 50.dp)
        ) {
            items(viewModel.searchState.value.listings) { listing ->
                ListingCard(
                    listing = listing,
                    onClick = { navController.navigate("listingscreen/${listing.id}") },
                    onClickOptions = {},
                    onClickDelete = {}
                )
            }
            item {
                Text(
                    text = "Non ci sono altri immobili disponibili.",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    color = Color.Gray,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

