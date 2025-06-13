package com.example.dietiestates.ui.screens


import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.dietiestates.ui.screens.components.CustomButton
import com.example.dietiestates.ui.screens.components.ListingCardMini
import com.example.dietiestates.ui.screens.components.OfferBubble
import com.example.dietiestates.ui.theme.RobotoSerif
import com.example.dietiestates.ui.theme.customBlue
import com.example.dietiestates.ui.viewModel.ListingOfferViewModel
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.SavedStateHandle
import com.example.dietiestates.AppContainer
import com.example.dietiestates.ui.screens.components.TopBarOffer
import com.example.dietiestates.utility.TokenManager
import kotlinx.coroutines.launch


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun OfferScreen(navController: NavController,
                viewModel: ListingOfferViewModel = viewModel()) {
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    val savedStateHandle = SavedStateHandle()
    val clientId = savedStateHandle.get<String>("clientId")


    val uiState by  viewModel.uiState.collectAsState()
    val listing = viewModel.listing.value

    val isWriting = viewModel.isWritingOffer.value
    val price = viewModel.offerPrice.value


    val listState = rememberLazyListState()

    val userRole = TokenManager.getUserRole()


    LaunchedEffect(uiState.offers.size) {
        // Scrolla all'ultima offerta (se ce n'è almeno una)
        if (uiState.offers.isNotEmpty()) {
            listState.animateScrollToItem(uiState.offers.size - 1)
        }
    }

    LaunchedEffect(uiState.error) {
        uiState.error?.let { errorMessage ->
            coroutineScope.launch {
                snackbarHostState.showSnackbar("Errore: ${errorMessage}")
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

                TopBarOffer(navController = navController, modifier = Modifier)

            }
        },
        bottomBar = {
            when {
                viewModel.showExternalOfferDialog.value -> {
                    androidx.compose.material3.AlertDialog(
                        onDismissRequest = { viewModel.showExternalOfferDialog.value = false },
                        confirmButton = {
                            CustomButton(
                                text = "Invia",
                                style = "blue",
                                onClick = {
                                    viewModel.submitExternalOffer()
                                    viewModel.showExternalOfferDialog.value = false
                                }
                            )
                        },
                        dismissButton = {
                            CustomButton(
                                text = "Annulla",
                                style = "blue",
                                onClick = {
                                    viewModel.showExternalOfferDialog.value = false
                                }
                            )
                        },
                        title = {
                            Text(
                                text = "Offerta esterna",
                                fontFamily = RobotoSerif,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 30.sp,
                                color = Color(0xFF3F51B5)
                            )
                        },
                        text = {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedTextField(
                                    value = viewModel.guestName.value,
                                    onValueChange = { viewModel.guestName.value = it },
                                    label = { Text("Nome") },
                                    singleLine = true,
                                    shape = RoundedCornerShape(12.dp),
                                    colors = textFieldColors,
                                    textStyle = MaterialTheme.typography.bodyLarge,
                                )
                                OutlinedTextField(
                                    value = viewModel.guestSurname.value,
                                    onValueChange = { viewModel.guestSurname.value = it },
                                    label = { Text("Cognome") },
                                    singleLine = true,
                                    shape = RoundedCornerShape(12.dp),
                                    colors = textFieldColors,
                                    textStyle = MaterialTheme.typography.bodyLarge,
                                )
                                OutlinedTextField(
                                    value = viewModel.guestEmail.value,
                                    onValueChange = { viewModel.guestEmail.value = it },
                                    label = { Text("Email") },
                                    singleLine = true,
                                    shape = RoundedCornerShape(12.dp),
                                    colors = textFieldColors,
                                    textStyle = MaterialTheme.typography.bodyLarge,
                                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Email)
                                )
                                OutlinedTextField(
                                    value = viewModel.guestOffer.value,
                                    onValueChange = { viewModel.guestOffer.value = it },
                                    label = { Text("Offerta (€)") },
                                    singleLine = true,
                                    shape = RoundedCornerShape(12.dp),
                                    colors = textFieldColors,
                                    textStyle = MaterialTheme.typography.bodyLarge,
                                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
                                )
                            }
                        }
                    )
                }

                !isWriting -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                            .navigationBarsPadding(), // <-- per evitare che venga tagliato
                        contentAlignment = Alignment.Center
                        //.background(Color.Gray)
                    ) {
                        if (!viewModel.isExternalMode) {
                            CustomButton(
                                onClick = { viewModel.isWritingOffer.value = true },
                                style = "blue",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .align(Alignment.Center),
                                text = "Proponi offerta"
                            )
                        } else {
                            CustomButton(
                                onClick = {
                                    viewModel.showExternalOfferDialog.value = true
                                },
                                style = "blue",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .align(Alignment.Center),

                                text = "Inserisci offerta esterna"
                            )
                        }
                    }
                }

                else -> {
                    Row(
                        Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                            value = price,
                            onValueChange = { viewModel.offerPrice.value = it },
                            placeholder = { Text("€ Inserisci offerta") },
                            modifier = Modifier
                                .weight(1f)
                                .width(20.dp),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            textStyle = MaterialTheme.typography.bodyLarge,
                            colors = textFieldColors,

                        )

                        IconButton(
                            onClick = {
                                listing?.id?.let { id ->
                                    viewModel.submitOffer()
                                    viewModel.isWritingOffer.value = false
                                    viewModel.offerPrice.value = ""
                                }
                            }
                        ) {
                            Icon(Icons.Default.Send, contentDescription = "Invia")
                        }
                    }
                }
            }

        }
    ) { padding ->
        when {
            uiState.loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

//            uiState.error != null -> {
//                Box(
//                    modifier = Modifier
//                        .fillMaxSize()
//                        .padding(padding),
//                    contentAlignment = Alignment.Center
//                ) {
//                    Text("Errore: ${uiState.error}")
//                }
//            }

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
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {


                        items(
                            uiState.offers,
                            key = { "${it.id}_${it.state}" }
                        ) { offer ->


//                        Text("Offerta: € ${offer.price} - Stato: ${offer.state} - Data: ${offer.date}")
                            if (userRole == "CLIENT") {
                                if (!offer.madeByUser) {
                                    OfferBubble(
                                        offer = offer,
                                        onAccept = {
                                            viewModel.updateOfferStatus(
                                                offer.id,
                                                "ACCEPTED"
                                            )
                                        },
                                        onDecline = {
                                            viewModel.updateOfferStatus(
                                                offer.id,
                                                "DECLINED"
                                            )
                                        }
                                    )
                                } else {
                                    OfferBubble(
                                        offer = offer
                                    )
                                }
                            } else {
                                if (offer.madeByUser) {
                                    if (viewModel.isExternalMode)
                                        OfferBubble(
                                            offer = offer,
                                            onAccept = {
                                                viewModel.updateOfferStatus(
                                                    offer.id,
                                                    "ACCEPTED"
                                                )
                                            },
                                            onDecline = {
                                                viewModel.updateOfferStatus(
                                                    offer.id,
                                                    "DECLINED"
                                                )
                                            },
                                            name = offer.guestName,
                                            surname = offer.guestSurname,
                                            email = offer.guestEmail


                                        )
                                    else {
                                        OfferBubble(
                                            offer = offer,
                                            onAccept = {
                                                viewModel.updateOfferStatus(
                                                    offer.id,
                                                    "ACCEPTED"
                                                )
                                            },
                                            onDecline = {
                                                viewModel.updateOfferStatus(
                                                    offer.id,
                                                    "DECLINED"
                                                )
                                            }
                                        )
                                    }
                                } else {
                                    OfferBubble(
                                        offer = offer
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

