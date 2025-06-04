package com.example.dietiestates.ui.screens


import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.outlined.Air
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.AttachMoney
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.DirectionsBus
import androidx.compose.material.icons.outlined.DirectionsCar
import androidx.compose.material.icons.outlined.Elevator
import androidx.compose.material.icons.outlined.Error
import androidx.compose.material.icons.outlined.Forest
import androidx.compose.material.icons.outlined.MeetingRoom
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.RealEstateAgent
import androidx.compose.material.icons.outlined.School
import androidx.compose.material.icons.outlined.SolarPower
import androidx.compose.material.icons.outlined.Stairs
import androidx.compose.material.icons.outlined.ViewInAr
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.dietiestates.AppContainer
import com.example.dietiestates.R
import com.example.dietiestates.data.model.Listing
import com.example.dietiestates.ui.screens.components.CustomButton
import com.example.dietiestates.ui.screens.components.ImageGalleryPager
import com.example.dietiestates.ui.screens.components.Map
import com.example.dietiestates.ui.theme.CustomTypography
import com.example.dietiestates.ui.theme.LocalAppTypography
import com.example.dietiestates.ui.theme.Roboto
import com.example.dietiestates.ui.theme.RobotoSerif
import com.example.dietiestates.ui.theme.RobotoSlab
import com.example.dietiestates.ui.viewModel.ListingViewModel
import com.example.dietiestates.utility.formatNumberWithDots
import com.google.accompanist.systemuicontroller.rememberSystemUiController

@Composable
fun ListingScreen(navController: NavController) {

    val viewModel: ListingViewModel = viewModel()
    val listing = viewModel.listingState.value.listing
    val state = viewModel.listingState.value
    val scrollState = rememberScrollState()
    val systemUiController = rememberSystemUiController()

    val userRole = AppContainer.tokenManager.getUserRole()

    SideEffect {
        systemUiController.setStatusBarColor(
            Color.Transparent,
            darkIcons = true
        ) // o false se immagine scura
    }

    when {
        state.loading -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                    CircularProgressIndicator(color = Color.White)
            }
        }

        state.listing == null -> {
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
                Text(text = "Errore nel caricamento dell'annuncio", fontSize = 22.sp)
            }
        }

        else -> {
            // Contenuto normale con listing
            if (listing != null)
                Box(modifier = Modifier.fillMaxSize()) {
                    Column( //Colonna che controlla la possibilità di scrollare
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(scrollState)
                            .padding(bottom = 100.dp)
                    ) {
                        Box() {
                            ImageGalleryPager(images = listing.imageUrls)
                            IconButton(
                                onClick = { navController.navigateUp() },
                                modifier = Modifier
                                    .align(Alignment.TopStart)
                                    .padding(
                                        top = WindowInsets.statusBars
                                            .asPaddingValues()
                                            .calculateTopPadding(),
                                        start = 8.dp
                                    )
                                    .shadow(
                                        elevation = 10.dp,
                                        shape = CircleShape,
                                        clip = false
                                    )
                                    .background(
                                        color = Color(0xFF3F51B5),
                                        shape = CircleShape
                                    )
                                    .size(40.dp),

                                ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                                    contentDescription = "Chiudi",
                                    tint = Color.White
                                )
                            }
                        }

                        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp))
                        {//Primo blocco, titolo ecc
                            Text(
                                text = listing.title,
                                style = LocalAppTypography.current.listingTitle
                            )
                            Text(
                                text = listing.address,
                                style = LocalAppTypography.current.listingAddress
                            )
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(
                                    text = "€ ${formatNumberWithDots(listing.price)}",
                                    style = LocalAppTypography.current.listingPrice
                                )
                                if(userRole == "CLIENT")
                                    CustomButton(
                                        onClick = { navController.navigate("listing/offer/${listing.id}") },
                                        style = "blue",
                                        text = "Proponi Offerta"
                                    )
                                else {
                                    CustomButton(
                                        onClick = { navController.navigate("agent/listing/offer/${listing.id}") },
                                        style = "blue",
                                        text = "Offerte",
                                    )
                                }
                            }
                        }//Primo blocco, titolo ecc

                        Divisore() //------//

                        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp))
                        {
                            Text(
                                text = "Caratteristiche",
                                style = LocalAppTypography.current.sectionTitle,
                                modifier = Modifier.padding(vertical = 6.dp),
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column() {
                                    FeatureItem(
                                        Icons.Outlined.MeetingRoom,
                                        "Stanze",
                                        listing.numberOfRooms
                                    )
                                    FeatureItem(
                                        Icons.Outlined.ViewInAr,
                                        "Dimensione",
                                        "${listing.size}mq"
                                    )
                                    FeatureItem(Icons.Outlined.Stairs, "Piano", "${listing.floor}")
                                    FeatureItem(
                                        Icons.Outlined.RealEstateAgent,
                                        "Contratto",
                                        listing.category
                                    )
                                }
                                Spacer(modifier = Modifier.width(60.dp))
                                Column() {
                                    FeatureItem(
                                        Icons.Outlined.SolarPower,
                                        "Classe Energetica",
                                        listing.energyClass.toString()
                                    )
                                    FeatureItem(
                                        Icons.Outlined.Elevator,
                                        "Ascensore",
                                        if (listing.hasElevator) "Presente" else "Assente"
                                    )
                                    FeatureItem(
                                        Icons.Outlined.Air,
                                        "Aria Condizionata",
                                        if (listing.hasAirConditioning) "Presente" else "Assente"
                                    )
                                    FeatureItem(
                                        Icons.Outlined.DirectionsCar,
                                        "Garage",
                                        if (listing.hasGarage) "Presente" else "Assente"
                                    )
                                }
                            }
                        }//Secondo blocco, caratteristiche

                        Divisore() //------//

                        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp))
                        {
                            Text(
                                text = "Descrizione",
                                style = LocalAppTypography.current.sectionTitle,
                                modifier = Modifier.padding(vertical = 6.dp),
                            )
                            Text(
                                maxLines = 7,
                                overflow = TextOverflow.Ellipsis,
                                text = listing.description,
                                fontFamily = RobotoSlab,
                                fontWeight = FontWeight.Light,
                                fontSize = 14.sp,
                                color = Color.Black
                            )
                            Spacer(modifier = Modifier.height(15.dp))
                            /* ToDo bottone mostrato solo se supero le 7 righe */
                            CustomButton(
                                onClick = { navController.navigate("listingviewdescriptionscreen/${listing.description}") },
                                modifier = Modifier.align(Alignment.CenterHorizontally),
                                style = "blue",
                                text = "Leggi di più"
                            )

                        }//Terzo blocco, descrizione

                        Divisore() //------//

                        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp))
                        {
                            Text(
                                text = "Indirizzo",
                                style = LocalAppTypography.current.sectionTitle
                            )
                            Text(
                                text = listing.address,
                                fontFamily = RobotoSerif,
                                fontWeight = FontWeight.Light,
                                fontSize = 15.sp,
                                modifier = Modifier.padding(vertical = 6.dp),
                                color = Color.Black
                            )

                            Map(listing.latitude, listing.longitude)

                            Text(
                                text = "Punti di interesse vicini",
                                style = LocalAppTypography.current.sectionTitle,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                            NearbyPlacesBlock(nearbyPlaces = listing.nearbyPlaces)
                        }//Quarto blocco, mappa

                        Divisore() //------//

                        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp)) {
                            Text(
                                text = "Inserzionista",
                                style = LocalAppTypography.current.sectionTitle
                            )
                            Spacer(modifier = Modifier.height(30.dp))
                            Column (modifier = Modifier.fillMaxWidth(),horizontalAlignment = Alignment.CenterHorizontally) {

                                Text( /* ToDo aggiornare text con il reale valore */
                                    text = "Stefano Rossi, 35",
                                    style = LocalAppTypography.current.featureTitle,
                                    fontSize = 20.sp,
                                    lineHeight = 12.sp,
                                )
                                Text( /* ToDo aggiornare text con il reale valore */
                                    text = "10 anni di esperienza",
                                    fontFamily = Roboto,
                                    fontWeight = FontWeight.ExtraLight,
                                    fontSize = 14.sp,
                                    lineHeight = 10.sp,
                                    color = Color.Black
                                )
                                Image( /* ToDo aggiornare text con il reale valore */
                                    painter = painterResource(R.drawable.person),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .height(70.dp)
                                        .width(70.dp)
                                        .padding(5.dp),
                                    contentScale = ContentScale.Crop
                                )
                                Text(  /* ToDo aggiornare text con il reale valore */
                                    text = "Tecnocasa S.R.L",
                                    style = LocalAppTypography.current.featureTitle,
                                    fontSize = 14.sp
                                )
                                Text( /* ToDo aggiornare text con il reale valore */
                                    text = "Sede Legale Via blblblbl, Milano",
                                    fontFamily = Roboto,
                                    fontWeight = FontWeight.ExtraLight,
                                    fontSize = 12.sp,
                                    lineHeight = 10.sp,
                                    color = Color.Black
                                )
                            }
                        }//Quinto blocco, Agente
                        Spacer(modifier = Modifier.height(20.dp))

                    }

                    if( userRole == "CLIENT")
                        BottomBar(
                            formatNumberWithDots(listing.price),
                            listingId = listing.id,
                            modifier = Modifier.align(Alignment.BottomCenter),
                            navController
                        )
                }
        }
    }
}

@Composable
fun NearbyPlacesBlock(nearbyPlaces: List<String>) {
    Column(modifier = Modifier.padding(end = 30.dp)) {
        nearbyPlaces.chunked(2).forEach { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth()
                    .padding(start = 10.dp, end= 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                rowItems.forEach { item ->
                    var (label, value) = item.split(":")
                    value += "m"
                    when (label) {
                        "parchi" -> {
                            FeatureItem(Icons.Outlined.Forest, label, value)
                        }

                        "scuole" -> {
                            FeatureItem(Icons.Outlined.School, label, value)
                        }

                        "trasporti" -> {
                            FeatureItem(Icons.Outlined.DirectionsBus, label, value)
                        }
                    }
                }
                if (rowItems.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
fun Divisore() {
    HorizontalDivider(
        modifier = Modifier.padding(8.dp),
        thickness = 1.5.dp,
        color = Color(0xFFBDBDBD)
    )
}

@Composable
private fun BottomBar(price: String, listingId: String, modifier: Modifier = Modifier, navController: NavController) {
    val navBarPadding = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    Surface(
        modifier
            .fillMaxWidth()
            .height(80.dp)
            .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
            .padding(bottom = navBarPadding)
            .border(1.dp, Color(0xFF9E9E9E), RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)),
        color = Color.White,
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Text(
                text = "€ ${price}",
                fontFamily = RobotoSlab,
                fontWeight = FontWeight.SemiBold,
                fontSize = 25.sp
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                CustomButton(icon = Icons.Filled.Call, style = "white", onClick = { /*TODO*/ })
                Spacer(modifier = Modifier.width(8.dp))
                CustomButton(
                    text = "Proposta",
                    icon = Icons.Outlined.AttachMoney,
                    style = "white",
                    onClick = { navController.navigate("listing/offer/${listingId}") },
                    )

            }
        }
    }
}

@Composable
fun FeatureItem(icon: ImageVector, label: String, value: String) {
    Row(modifier = Modifier.padding(vertical = 10.dp)) {
        Icon(
            icon,
            contentDescription = label,
            tint = Color(0xFF49454F),
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .size(32.dp)
        )
        Spacer(modifier = Modifier.width(7.dp))
        Column(horizontalAlignment = Alignment.Start) {
            Text(
                label, style = LocalAppTypography.current.featureTitle
            )
            Text(
                value, style = LocalAppTypography.current.featureValue
            )
        }
    }
}

//Composable per mostrare la descrizione per intero in una nuova pagina
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FullTextScreen(navController: NavController, text: String) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp),
                title = {
                    Text(
                        "Descrizione",
                        color = Color.White,
                        fontFamily = RobotoSerif,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 26.sp,
                        lineHeight = 21.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.Outlined.Close,
                            contentDescription = "Chiudi",
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF3F51B5),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(text)
        }
    }
}




