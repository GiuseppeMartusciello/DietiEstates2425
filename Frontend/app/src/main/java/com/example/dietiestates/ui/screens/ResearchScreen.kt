package com.example.dietiestates.ui.screens

import android.R
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LocalOffer
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.paddingFrom
import androidx.compose.material.TabRowDefaults.Divider
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.dietiestates.data.model.Research
import com.example.dietiestates.ui.theme.RobotoSerif
import com.example.dietiestates.ui.theme.RobotoSlab
import com.example.dietiestates.ui.viewModel.ResearchViewModel
import com.example.tuaapp.ui.components.NavBar
import com.example.tuaapp.ui.components.NavItem

@Composable
fun ResearchScreen(
    navController: NavController,
    viewModel: ResearchViewModel = viewModel()
) {
    LaunchedEffect(Unit) {
        // viewModel.fetchResearch10()
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
                    "cerca",
                    Icons.Outlined.Search, Icons.Filled.Search
                ), NavItem(
                    "notifiche",
                    Icons.Outlined.LocalOffer, Icons.Filled.LocalOffer,
                ), NavItem(
                    "logout",
                    Icons.Outlined.Person, Icons.Filled.Person,
                )
            )
        )
    }) { paddingValues ->

       Box() {

           Column(
               modifier = Modifier
                   .fillMaxWidth()
                   .fillMaxSize()
                   .padding(paddingValues)
                   .padding(vertical = 8.dp)
           ) {
               Research(
                   navController,
                   viewModel = viewModel
               )
           }

       }
    }
}



@Composable
fun Research(
    navController: NavController,
    viewModel: ResearchViewModel = viewModel()
) {

    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    var expanded by remember { mutableStateOf(false) }
    var query by remember { mutableStateOf("") }
    var textFieldWidth by remember { mutableStateOf(0) }

    val fakeResearches = listOf(
        Research(
            id = "1",
            searchType = "Comune",
            date = "2025-05-21",
            municipality = "Napoli",
            latitude = 40.8522,
            longitude = 14.2681
        ),
        Research(
            id = "2",
            searchType = "Mappa",
            date = "2025-05-20",
            municipality = "Fanculo",
            latitude = 41.9028,
            longitude = 12.4964
        ),
        Research(
            id = "3",
            searchType = "Coordinate",
            date = "2025-05-19",
            municipality = null,
            latitude = 45.4642,
            longitude = 9.19
        ),
        Research(
            id = "1",
            searchType = "Comune",
            date = "2025-05-21",
            municipality = "Salerno",
            latitude = 40.8522,
            longitude = 14.2681
        ),Research(
            id = "1",
            searchType = "Comune",
            date = "2025-05-21",
            municipality = "Positano",
            latitude = 40.8522,
            longitude = 14.2681
        ),Research(
            id = "1",
            searchType = "Comune",
            date = "2025-05-21",
            municipality = "Capri",
            latitude = 40.8522,
            longitude = 14.2681
        ),Research(
            id = "1",
            searchType = "Comune",
            date = "2025-05-21",
            municipality = "Ischia",
            latitude = 40.8522,
            longitude = 14.2681
        ),
    )

    Column(modifier = Modifier
        .fillMaxWidth()
        .pointerInput(Unit)
        {
            detectTapGestures(onTap = {
                expanded = false
                focusManager.clearFocus()
                keyboardController?.hide()
            })
        }
    ) {
        Box {
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                trailingIcon = {
                    Icon(imageVector = Icons.Default.Search, contentDescription = null)
                },
                modifier = Modifier
                    .padding(horizontal = 32.dp)
                    .fillMaxWidth()
                    .onGloballyPositioned {
                        textFieldWidth = it.size.width
                    }
                    .focusRequester(focusRequester)
                    .clickable {
                        expanded = false
                        focusRequester.requestFocus()
                        keyboardController?.show()
                    },
                singleLine = true
            )

        }
        History(
            fakeResearches,
            //researches = viewModel.researchState.value.researches ,
            onSelect = { selectedText -> query = selectedText },
            navController,
        )
    }
}



@Composable
fun History( researches: List<Research>,onSelect: (String) -> Unit,navController: NavController) {


    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .fillMaxWidth()
                .padding(top = 8.dp),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {

            if (researches.isEmpty()) {
                item {
                    Text(
                        textAlign = TextAlign.Center,
                        text = "Ancora nessuna ricerca effettuata",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
            if (!researches.isEmpty()) {


            item {
                
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = "Cronologia",
                    textAlign = TextAlign.Center,
                    fontFamily = RobotoSlab,
                    fontWeight = FontWeight.Normal,
                    fontSize = 16.sp,
                )
                Divider( // riga orizzontale sopra la lista
                    thickness = 1.dp,
                    color = Color.LightGray,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )
            }

                items(researches) { research ->
                    ResearchItem(research, onClick = onSelect)
                }

            item{
                Box(modifier = Modifier.fillMaxWidth()) {

                    MapButton(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .shadow(10.dp),
                        // spazio sopra la bottom bar
                        navController
                    )
                }
            }

            }
        }
    }
}


@Composable
fun ResearchItem(research: Research, onClick: (String) -> Unit) {

    val content = if (!research.municipality.isNullOrBlank()) {
        research.municipality
    } else {
        "latitudine:${research.latitude} longitudine:${research.longitude}"
    }

    Card(
        modifier = Modifier.fillMaxWidth()
            .clickable(  onClick =  { onClick(content) }),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF0FAFE),
        )
    )
    {

        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.History,
                contentDescription = "Icona cronologia",
                modifier = Modifier.padding(end = 8.dp)
            )

            val text = if (!research.municipality.isNullOrBlank()) research.municipality
             else "latitudine: ${research.latitude} longitudine: ${research.longitude}"


            Text(
                text = text,
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

@Composable
fun MapButton(
    modifier: Modifier = Modifier,
    navController: NavController,
)
{
    Button(
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF3F51B5),
            contentColor = Color.White
        ),
        shape = RectangleShape,
        onClick = {navController.navigate("map_search")},
        modifier = modifier ,
        contentPadding = PaddingValues(vertical = 12.dp, horizontal = 16.dp)
    )
    {
        Text(
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            text = "Ricerca Avanzata")
    }
}