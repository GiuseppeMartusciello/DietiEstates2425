package com.example.dietiestates.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.TabRowDefaults.Divider
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.dietiestates.data.model.Research
import com.example.dietiestates.ui.screens.components.AppBottomBar
import com.example.dietiestates.ui.screens.components.AppTopBar
import com.example.dietiestates.ui.theme.RobotoSlab
import com.example.dietiestates.ui.viewModel.ResearchViewModel

@Composable
fun ResearchScreen(
    viewModel: ResearchViewModel,
    navController: NavController,
) {
    LaunchedEffect(Unit) {
        viewModel.fetchResearch10()
        viewModel.updateSelectedResearch(null)
        viewModel.isOldResearch = false
        viewModel.resetResearchForm()
    }
    SideEffect {

    }


    Scaffold(
        topBar = { AppTopBar(modifier = Modifier) },
        bottomBar = { AppBottomBar(navController)}
    ) { paddingValues ->

       Box(
           modifier = Modifier
               .padding(paddingValues)
       ) {
               Research(
                   navController,
                   viewModel = viewModel
               )
       }
    }
}



@Composable
fun Research(
    navController: NavController,
    viewModel: ResearchViewModel = viewModel()
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    var query by remember { mutableStateOf("") }



    Column(modifier = Modifier
        .fillMaxWidth()
        .pointerInput(Unit)
        {
            detectTapGestures(onTap = {
                focusManager.clearFocus()
                keyboardController?.hide()
            })
        }
    ) {
        CustomOutlineTextField(
            viewModel,
            query,
            onQueryChange = { query = it },
            navController,
            keyboardController
        )

        History(
            onSelect = { selectedQuery, research ->
                query = selectedQuery
                viewModel.updateSelectedResearch(research)
                viewModel.isOldResearch = true
            },
            viewModel
        )
    }
        Box(
            modifier = Modifier
                .fillMaxSize()
        ){
            MapButton(
                viewModel,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 30.dp),
                navController
            )
        }
}



@Composable
fun History(onSelect: (String, Research) -> Unit,viewModel: ResearchViewModel) {

    val state = viewModel.searchState.value

    Box(modifier = Modifier.fillMaxSize()) {
        Column {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = "Cronologia",
                textAlign = TextAlign.Center,
                fontFamily = RobotoSlab,
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp,
            )
            Divider(
                thickness = 1.dp,
                color = Color.LightGray,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )

            when {
                state.loading -> {
                    // puoi anche usare uno Spacer se vuoi spazio sopra il loader
                    Spacer(modifier = Modifier.height(32.dp))
                }

                state.error != null -> {
                    Text(
                        text = "Errore: ${state.error}",
                        color = Color.Red,
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        textAlign = TextAlign.Center
                    )
                }

                viewModel.researchState.value.researches.isEmpty() -> {
                    Text(
                        text = "Ancora nessuna ricerca effettuata",
                        fontFamily = RobotoSlab,
                        fontWeight = FontWeight.Normal,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 100.dp),
                        verticalArrangement = Arrangement.spacedBy(0.dp),
                        contentPadding = PaddingValues(bottom = 16.dp)
                    ) {
                        items(viewModel.researchState.value.researches) { research ->
                            ResearchItem(
                                research,
                                onSelect,
                                onDelete = {
                                    id -> viewModel.deleteResearch(id);
                                    viewModel.fetchResearch10()
                                }
                            )
                        }
                    }
                }
            }
        }

        if (state.loading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0x88000000)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color(0xFF3F51B5))
            }
        }
    }

}


@Composable
fun ResearchItem(research: Research, onSelect: (String, Research) -> Unit,onDelete: (String) -> Unit) {

    val content = if (!research.municipality.isNullOrBlank()) {
        research.municipality
    } else {
        "latitudine:${research.latitude} longitudine:${research.longitude}"
    }

    Card(
        modifier = Modifier.fillMaxWidth()
            .clickable(  onClick =  { onSelect(content,research) }),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF0FAFE),
        )
    )
    {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.History,
                contentDescription = "Icona cronologia",
                modifier = Modifier
                    .size(30.dp)
                    .padding(end = 8.dp)
            )

            val text = if (!research.municipality.isNullOrBlank()) research.municipality
             else "latitudine: ${research.latitude} longitudine: ${research.longitude}"

            Box(modifier = Modifier.weight(1f)) {
                Text(
                    text = text,
                    fontFamily = RobotoSlab,
                    fontWeight = FontWeight.Normal,
                    fontSize = 14.sp
                )
            }

            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Rimuovi ricerca",
                tint = Color.Red,
                modifier = Modifier
                    .size(20.dp)
                    .clickable { onDelete(research.id) }
            )
        }
    }
}


@Composable
fun MapButton(
    viewModel: ResearchViewModel,
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
        onClick = {
            viewModel.updateResearchFormState {
                copy(
                    municipality="",
                    searchType = "COORDINATES"
                )
            }
            navController.navigate("mapscreen")
                  },
        modifier = modifier
            .shadow(10.dp),
        contentPadding = PaddingValues(vertical = 12.dp, horizontal = 16.dp)
    )
    {
        Text(
            fontFamily = RobotoSlab,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            text = "Ricerca Avanzata")
    }
}

@Composable
fun CustomOutlineTextField(
    viewModel: ResearchViewModel,
    query: String,
    onQueryChange: (String) -> Unit,
    navController: NavController,
    keyboardController: SoftwareKeyboardController?
)
{
    val focusRequester = remember { FocusRequester() }
    var textFieldWidth by remember { mutableIntStateOf(0) }
    var showAlert by remember { mutableStateOf(false) }

    fun onSearch() {
        if(!query.isBlank()){
            viewModel.updateResearchFormState {copy(
                searchType = "MUNICIPALITY",
                municipality = query,
                latitude = "",
                longitude = "",
                radius=""
            )}
            navController.navigate("filterscreen")
            keyboardController?.hide()
        }
        else
        {
            showAlert = true
        }
    }

    Spacer(modifier = Modifier.height(8.dp))

    OutlinedTextField(
        value = query,
        onValueChange = {
            onQueryChange(it)
            viewModel.updateSelectedResearch(null)
            viewModel.isOldResearch = false
        },
        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(onDone = { onSearch() }),

        trailingIcon = {
            IconButton(
                onClick = {  onSearch() }){
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Cerca"
                )
            }
        },
        modifier = Modifier
            .padding(horizontal = 32.dp)
            .fillMaxWidth()
            .onGloballyPositioned {
                textFieldWidth = it.size.width
            }
            .focusRequester(focusRequester)
            .clickable {
                focusRequester.requestFocus()
                keyboardController?.show()
            },
        singleLine = true
    )

    if (showAlert) {
        AlertDialog(
            onDismissRequest = { showAlert = false },
            title = { Text("Attenzione") },
            text = { Text("Inserisci un comune per avviare la ricerca.") },
            confirmButton = {
                TextButton(onClick = { showAlert = false }) {
                    Text("Ok")
                }
            }
        )
    }
}