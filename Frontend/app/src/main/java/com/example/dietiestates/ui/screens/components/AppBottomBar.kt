package com.example.dietiestates.ui.screens.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LocalOffer
import androidx.compose.material.icons.outlined.Logout
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.example.dietiestates.AppContainer
import com.example.tuaapp.ui.components.NavBar
import com.example.tuaapp.ui.components.NavItem

@Composable
fun AppBottomBar(navController: NavController) {
    val userRole = AppContainer.tokenManager.getUserRole()

    NavBar(
        navController = navController, items = listOf(
            NavItem(
                "home",
                Icons.Outlined.Home, Icons.Filled.Home
            ),
            NavItem(
                "offer",
                Icons.Outlined.LocalOffer, Icons.Filled.LocalOffer,
            ),
            NavItem(
                "notification",
                Icons.Outlined.Notifications, Icons.Filled.Notifications
            ),
            if (userRole == "CLIENT") {

                NavItem(
                    "profile",
                    Icons.Outlined.Person, Icons.Filled.Person,
                )
            } else if(userRole == "MANAGER"){
                NavItem(
                    "agencyProfile",
                    Icons.Outlined.Person, Icons.Filled.Person,
                )
            } else{
                NavItem(
                    "logout",
                    Icons.Outlined.Logout, Icons.Filled.Logout,
                )
            }
        )
    )
}