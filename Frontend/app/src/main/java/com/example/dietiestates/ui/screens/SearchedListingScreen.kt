package com.example.dietiestates.ui.screens


import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import com.example.dietiestates.ui.screens.components.AppTopBar
import com.example.dietiestates.ui.screens.components.ErrorHandle
import com.example.dietiestates.ui.screens.components.GoBackButton
import com.example.dietiestates.ui.screens.components.ListingCard
import com.example.dietiestates.ui.viewModel.ResearchViewModel
import com.google.accompanist.systemuicontroller.rememberSystemUiController

@Composable
fun SearchedListingScreen(
    viewmodel: ResearchViewModel,
    navController: NavController
) {



    val viewState by viewmodel.searchState
    val systemUiController = rememberSystemUiController()

    //Per aggiornare in caso di modifica di un listing
    val currentBackStackEntry = navController.currentBackStackEntry
    val savedStateHandle = currentBackStackEntry?.savedStateHandle

    LaunchedEffect(Unit) {
        println("📦 Listings count: ${viewState.listings.size}")
    }

    SideEffect {
        systemUiController.setStatusBarColor(
            Color(0xFF3F51B5),
            darkIcons = true
        ) // o false se immagine scura
    }

    //Aggiorna al ritorno della modifica di un listing
    LaunchedEffect(Unit) {
        savedStateHandle?.getLiveData<Boolean>("listingModified")?.observeForever { modified ->
            if (modified == true) {
                 //todo mettere qui i linsting trovati// 🔄 ricarica la lista dei listing
                savedStateHandle.set("listingModified", false) // reset
            }
        }
    }

    Scaffold(topBar = {

        AppTopBar(modifier = Modifier)

    }) { paddingValues ->
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
fun EmptyResearch(paddingValues : PaddingValues, navController : NavController)
{
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        contentAlignment = Alignment.Center
    ) {
        GoBackButton(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top=10.dp)
                .padding(horizontal = 10.dp),
            navController,
            "researchscreen"
        )


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
fun ListingScroll(paddingValues : PaddingValues, viewModel : ResearchViewModel , navController : NavController)
{
    val viewState by viewModel.searchState

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),

        contentAlignment = Alignment.Center
    ) {
        GoBackButton(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top=0.dp)
                .padding(horizontal = 10.dp)
                .zIndex(1f),
            navController,
            "researchscreen"
        )

        LazyColumn(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.wrapContentHeight()
                .padding(vertical = 5.dp),
        ) {
            items(viewState.listings) { listing ->
                ListingCard(
                    listing = listing,
                    onClick = { navController.navigate("listingscreen/${listing.id}") },
                    onClickOptions = {},
                    onClickDelete = {}
                )
            }
        }
    }
}

