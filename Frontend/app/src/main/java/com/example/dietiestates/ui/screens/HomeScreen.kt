package com.example.dietiestates.ui.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Error
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.dietiestates.ui.screens.components.AppTopBar
import com.example.dietiestates.ui.screens.components.ListingCard
import com.example.dietiestates.ui.theme.RobotoSerif
import com.example.dietiestates.ui.theme.RobotoSlab
import com.example.dietiestates.ui.viewModel.HomeViewModel
import com.example.tuaapp.ui.components.NavBar
import com.google.accompanist.systemuicontroller.rememberSystemUiController

@Composable
fun HomeScreen(navController: NavController) {
    val homeViewModel: HomeViewModel = viewModel()
    val viewState by homeViewModel.listingState
    val systemUiController = rememberSystemUiController()

    //Per aggiornare in caso di modifica di un listing
    val currentBackStackEntry = navController.currentBackStackEntry
    val savedStateHandle = currentBackStackEntry?.savedStateHandle
    val context = LocalContext.current

    SideEffect {
        systemUiController.setStatusBarColor(
            Color(0xFF3F51B5),
            darkIcons = true
        )
    }

    //Aggiorna al ritorno della modifica di un listing
    LaunchedEffect(Unit) {
        savedStateHandle?.getLiveData<Boolean>("listingModified")?.observeForever { modified ->
            if (modified == true) {
                homeViewModel.fetchListings()
                savedStateHandle.set("listingModified", false) // reset
            }
        }
    }

    Scaffold(
        topBar = {
            AppTopBar()
        },
        bottomBar = {
            NavBar(navController = navController) })
    { paddingValues ->
        when {
            viewState.loading -> {
                CircularProgressIndicator()
            }

            viewState.error != null -> {
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
                    Text(text = "Errore nel caricamento degli immobili", fontSize = 22.sp)
                }
            }

            else -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White)
                        .padding(paddingValues),
                    contentAlignment = Alignment.TopCenter // centra il contenuto nel Box
                ) {
                    LazyColumn(
                        horizontalAlignment = Alignment.CenterHorizontally, // per centrare gli elementi dentro
                        modifier = Modifier.wrapContentHeight()
                    ) {
                        items(viewState.list) { listing ->
                            ListingCard(
                                listing = listing,
                                onClick = { navController.navigate("listingscreen/${listing.id}") },
                                onClickOptions = {
                                    navController.navigate("modifylistingscreen/${listing.id}")
                                },
                                onClickDelete ={ homeViewModel.deleteListing(    listingId = listing.id,
                                    onSuccess = {
                                        Toast.makeText(context, "Annuncio eliminato ✅", Toast.LENGTH_SHORT).show()
                                    },
                                    onError = { message ->
                                        Toast.makeText(context, "Errore eliminazione ❌: $message", Toast.LENGTH_SHORT).show()
                                    })}
                            )
                        }
                    }
                }
            }
        }
    }
}


