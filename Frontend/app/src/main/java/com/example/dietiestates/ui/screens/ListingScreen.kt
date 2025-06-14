package com.example.dietiestates.ui.screens


import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.outlined.Air
import androidx.compose.material.icons.outlined.AttachMoney
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.DirectionsBus
import androidx.compose.material.icons.outlined.DirectionsCar
import androidx.compose.material.icons.outlined.Elevator
import androidx.compose.material.icons.outlined.Error
import androidx.compose.material.icons.outlined.Forest
import androidx.compose.material.icons.outlined.MeetingRoom
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.dietiestates.AppContainer
import com.example.dietiestates.R
import com.example.dietiestates.ui.screens.components.CustomButton
import com.example.dietiestates.ui.screens.components.ImageGalleryPager
import com.example.dietiestates.ui.screens.components.Map
import com.example.dietiestates.ui.theme.LocalAppTypography
import com.example.dietiestates.ui.theme.Roboto
import com.example.dietiestates.ui.theme.RobotoSerif
import com.example.dietiestates.ui.theme.RobotoSlab
import com.example.dietiestates.ui.viewModel.ListingViewModel
import com.example.dietiestates.utility.TokenManager
import com.example.dietiestates.utility.formatNumberWithDots
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import java.time.LocalDate
import java.time.Period

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ListingScreen(navController: NavController) {

    val viewModel: ListingViewModel = viewModel()
    val listing = viewModel.listingState.value.listing
    val agent = viewModel.listingState.value.agent
    val state = viewModel.listingState.value
    val scrollState = rememberScrollState()
    val systemUiController = rememberSystemUiController()

    val userRole = TokenManager.getUserRole()

    SideEffect {
        systemUiController.setStatusBarColor(
            Color.Transparent,
            darkIcons = true
        )
    }

    when {
        state.loadingListing || state.loadingAgent -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color.White)
            }
        }

        state.listing == null || state.agent == null-> {
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
                                Row(verticalAlignment = Alignment.Bottom) {
                                    Text(
                                        text = "€ ${formatNumberWithDots(listing.price)}",
                                        style = LocalAppTypography.current.listingPrice
                                    )

                                    if (listing.category == "RENT") {
                                        Spacer(modifier = Modifier.width(4.dp))

                                        Text(
                                            text = "al mese",
                                            style = LocalAppTypography.current.listingPrice.copy(
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Normal,
                                                color = Color.DarkGray
                                            )
                                        )
                                    }
                                }
                                if (userRole == "CLIENT")
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
                                        if (listing.category == "SALE") "Vendita" else "Affitto"
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

                        Column(
                            modifier = Modifier
                                .padding(horizontal = 16.dp, vertical = 14.dp)
                                .fillMaxWidth()
                        )
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
                            if (listing.description.length > 250) {
                                CustomButton(
                                    onClick = { navController.navigate("listingviewdescriptionscreen/${listing.description}") },
                                    modifier = Modifier.align(Alignment.CenterHorizontally),
                                    style = "blue",
                                    text = "Leggi di più"
                                )
                            }

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
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                val birthDateLocal = agent?.birthDate?.toInstant()?.atZone(java.time.ZoneId.systemDefault())
                                    ?.toLocalDate()
                                val startDateLocal = agent?.start_date?.toInstant()?.atZone(java.time.ZoneId.systemDefault())
                                    ?.toLocalDate()
                                val currentDate = LocalDate.now()
                                val yearsOld = Period.between(birthDateLocal, currentDate).years
                                val yearsExperience = Period.between(startDateLocal, currentDate).years
                                Text(
                                    text = "${agent?.name} ${agent?.surname}, $yearsOld",
                                    style = LocalAppTypography.current.featureTitle,
                                    fontSize = 20.sp,
                                    lineHeight = 12.sp,
                                )

                                if(yearsExperience > 2) {
                                    Text(
                                        text = "$yearsExperience anni di esperienza",
                                        fontFamily = Roboto,
                                        fontWeight = FontWeight.ExtraLight,
                                        fontSize = 14.sp,
                                        lineHeight = 10.sp,
                                        color = Color.Black
                                    )
                                }
                                Image(
                                    painter = painterResource(R.drawable.person),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .height(70.dp)
                                        .width(70.dp)
                                        .padding(5.dp),
                                    contentScale = ContentScale.Crop
                                )
                                Text(
                                    text = "${agent?.agencyName}",
                                    style = LocalAppTypography.current.featureTitle,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = "${agent?.agencyAddress}",
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

                    if (userRole == "CLIENT")
                        BottomBar(
                            listing.category,
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
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 10.dp, end = 20.dp),
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
private fun BottomBar(
    category: String,
    price: String,
    listingId: String,
    modifier: Modifier = Modifier,
    navController: NavController
) {
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
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = "€ ${price}",
                    style = LocalAppTypography.current.listingPrice
                )

                if (category == "RENT") {
                    Spacer(modifier = Modifier.width(4.dp))

                    Text(
                        text = "al mese",
                        style = LocalAppTypography.current.listingPrice.copy(
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Normal,
                            color = Color.DarkGray
                        )
                    )
                }
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                CallButton("3242424242") /* ToDo reale numero agente*/
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

@Composable
fun CallButton(phoneNumber: String) {
    val context = LocalContext.current

    CustomButton(
        icon = Icons.Filled.Call,
        style = "white",
        onClick = { openDialer(context, phoneNumber) })
}

fun openDialer(context: Context, phoneNumber: String) {
    val intent = Intent(Intent.ACTION_DIAL).apply {
        data = Uri.parse("tel:$phoneNumber")
    }
    context.startActivity(intent)
}
