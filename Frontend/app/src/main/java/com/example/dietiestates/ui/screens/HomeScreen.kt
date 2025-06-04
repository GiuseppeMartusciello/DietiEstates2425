package com.example.dietiestates.ui.screens

import android.app.Activity
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
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Error
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LocalOffer
import androidx.compose.material.icons.outlined.Logout
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.dietiestates.ui.screens.components.ListingCard
import com.example.dietiestates.ui.theme.Roboto
import com.example.dietiestates.ui.theme.RobotoSerif
import com.example.dietiestates.ui.theme.RobotoSlab
import com.example.dietiestates.ui.viewModel.HomeViewModel
import com.example.tuaapp.ui.components.NavBar
import com.example.tuaapp.ui.components.NavItem
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
        ) // o false se immagine scura
    }

    //Aggiorna al ritorno della modifica di un listing
    LaunchedEffect(Unit) {
        savedStateHandle?.getLiveData<Boolean>("listingModified")?.observeForever { modified ->
            if (modified == true) {
                homeViewModel.fetchListings() // 🔄 ricarica la lista dei listing
                savedStateHandle.set("listingModified", false) // reset
            }
        }
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
                    "researchScreen",
                    Icons.Outlined.Search, Icons.Filled.Search
                ), NavItem(
                    "offer",
                    Icons.Outlined.LocalOffer, Icons.Filled.LocalOffer,
                ),
                NavItem(
                    "logout",
                    Icons.Outlined.Person, Icons.Filled.Person,
                )

            )
        )
    }) { paddingValues ->
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
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center // centra il contenuto nel Box
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


