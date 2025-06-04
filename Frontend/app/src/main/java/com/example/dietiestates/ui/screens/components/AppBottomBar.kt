package com.example.dietiestates.ui.screens.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LocalOffer
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.example.tuaapp.ui.components.NavBar
import com.example.tuaapp.ui.components.NavItem

@Composable
fun AppBottomBar(navController: NavController)
{
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
}