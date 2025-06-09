package com.example.dietiestates.ui.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.dietiestates.AppContainer
import com.example.dietiestates.AppContainer.init
import com.example.dietiestates.ui.screens.components.CustomButton
import com.example.dietiestates.ui.screens.components.ListingCardMini
import com.example.dietiestates.ui.screens.components.OfferBubble
import com.example.dietiestates.ui.screens.components.TopBarOffer
import com.example.dietiestates.ui.theme.RobotoSerif
import com.example.dietiestates.ui.theme.customBlue
import com.example.dietiestates.ui.viewModel.ClientsOfferViewModel
import com.example.dietiestates.ui.viewModel.ListingOfferViewModel
import kotlinx.coroutines.launch

@Composable
fun ClientsOfferScreen(
    navController: NavController,
    viewModel: ClientsOfferViewModel = viewModel(),
    // onClick => Unit()
) {

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    val uiState by viewModel.uiState.collectAsState()
    val listing = viewModel.listing.value
//
//    val isWriting = viewModel.isWritingOffer.value
//    val price = viewModel.offerPrice.value

    val listState = rememberLazyListState()




    LaunchedEffect(uiState.error) {

        uiState.error?.let { errorMessage ->
            coroutineScope.launch {
                snackbarHostState.showSnackbar("Errore: $errorMessage")
            }
        }
    }


    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = customBlue,
        unfocusedBorderColor = customBlue,
        cursorColor = customBlue,
        focusedLabelColor = customBlue,
        unfocusedLabelColor = customBlue,
        focusedLeadingIconColor = customBlue,
        unfocusedLeadingIconColor = customBlue,
        errorBorderColor = MaterialTheme.colorScheme.error,
        errorLabelColor = MaterialTheme.colorScheme.error,
        errorLeadingIconColor = MaterialTheme.colorScheme.error
    )

    Scaffold(
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
                //.align(Alignment.TopCenter)
            )
        },
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    //.padding(top = 20.dp)
                    .background(Color(0xFF3F51B5)),
            ) {

                TopBarOffer(navController, Modifier, "Offerte")

            }
        },
        bottomBar = {
//            if (!isWriting) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .navigationBarsPadding(),
                //verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                CustomButton(
                    onClick = { navController.navigate("listing/offer/${listing?.id}/external") },
                    style = "blue",
                    modifier = Modifier
                        .fillMaxWidth()
//                            .padding(bottom = 30.dp)
                    ,
                    text = "Offerte esterne"
                )
            }

        }
    ) { padding ->
        when {
            uiState.loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            uiState.error != null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Errore: ${uiState.error}")
                }
            }

            else -> {
                Column(
                    Modifier
                        .padding(
                            top = padding.calculateTopPadding(),
                            bottom = padding.calculateBottomPadding()
                        )
                        .fillMaxSize()
                ) {
                    listing?.let {

                        ListingCardMini(
                            listing = it,
                            onClick = { navController.navigate("listingscreen/${it.id}") })


                    }
                    LazyColumn(
                        state = listState,
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                            ) {

                                Text(
                                    "Email",
                                    modifier = Modifier.weight(3f),
                                    fontFamily = RobotoSerif,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF3F51B5),
                                    fontSize = 14.sp,


                                    )
                                Text(
                                    "Data",
                                    modifier = Modifier.weight(1.2f),
                                    fontFamily = RobotoSerif,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF3F51B5),
                                    fontSize = 14.sp

                                )
                                Text(
                                    "Offerta",
                                    modifier = Modifier.weight(0.8f),
                                    fontFamily = RobotoSerif,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF3F51B5),
                                    fontSize = 14.sp
                                )
                            }
                        }


                        items(uiState.offers) { offer ->
                            Column(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                androidx.compose.material3.Divider(
                                    color = Color.LightGray,
                                    thickness = 0.8.dp
                                )
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                        .clickable {
                                            if (listing?.id != null) {

                                                navController.navigate("listing/offer/${listing.id}?clientId=${offer.userId}")
                                            }
                                        },

                                    ) {
//                                Text(offer.name, modifier = Modifier.weight(1f), maxLines = 1)
//                                Text(offer.surname, modifier = Modifier.weight(1f), maxLines = 1)


                                    Text(
                                        offer.email,
                                        modifier = Modifier.weight(3f),
                                        maxLines = 1,
                                        fontSize = 12.sp,
                                        overflow = TextOverflow.Ellipsis
                                    )

                                    val parsedDate =
                                        offer.lastOffer.date
                                            .split('T')[0] // "2025-05-27"
                                    Text(
                                        parsedDate,
                                        modifier = Modifier.weight(1.2f),
                                        fontSize = 12.sp
                                    )

                                    Text(
                                        "${String.format("${offer.lastOffer.price.toInt()}")}€",
                                        modifier = Modifier.weight(0.8f),
                                        maxLines = 1,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                            }

                        }

                    }


                }
            }
        }
    }


}