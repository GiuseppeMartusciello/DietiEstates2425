package com.example.dietiestates.ui.screens.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.dietiestates.ui.theme.RobotoSerif
import com.example.dietiestates.ui.theme.RobotoSlab
import com.google.accompanist.systemuicontroller.rememberSystemUiController

@Composable
fun TopBarOffer (navController: NavController, modifier: Modifier, text: String = "Le mie offerte") {
    val systemUiController = rememberSystemUiController()

    SideEffect {
        systemUiController.setStatusBarColor(
            Color(0xFF3F51B5),
            darkIcons = true
        ) // o false se immagine scura
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            //.height(30.dp)
            .statusBarsPadding()
//                    .padding(top = 8.dp)
            .background(Color(0xFF3F51B5)),
    ) {
        IconButton(
            onClick = { navController.popBackStack() }) {
            Icon(
                Icons.Default.ArrowBack,
                contentDescription = "Indietro",
                tint = Color.White
            )

        }
        Text(
            text = "${text}",
            fontFamily = RobotoSlab,
            fontWeight = FontWeight.SemiBold,
            fontSize = 26.sp,
            color = Color.White,
            modifier = Modifier.align(Alignment.Center)
        )


    }
}