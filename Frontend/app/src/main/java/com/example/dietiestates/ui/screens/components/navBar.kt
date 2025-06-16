package com.example.tuaapp.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.RealEstateAgent
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LocalOffer
import androidx.compose.material.icons.outlined.Logout
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.RealEstateAgent
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.dietiestates.AppContainer
import com.example.dietiestates.utility.TokenManager

data class NavItem(
    val route: String,
    val iconOutlined: ImageVector,
    val iconFilled: ImageVector,
)

@Composable
fun NavBar(navController: NavController) {
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
    val role = TokenManager.getUserRole()

    val items = listOf(
        NavItem("home", Icons.Outlined.Home, Icons.Filled.Home),
        NavItem("offer", Icons.Outlined.LocalOffer, Icons.Filled.LocalOffer),
        NavItem("notification", Icons.Outlined.Notifications, Icons.Filled.Notifications)
    )

    Box{
        NavigationBar(
            containerColor = Color(0xFF303F9F),
            tonalElevation = 8.dp,
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .heightIn(max = 60.dp)
        ) {
            items.take(2).forEach { item ->
                val selected = currentRoute == item.route
                NavigationBarItem(
                    selected = selected,
                    onClick = {
                        if (currentRoute != item.route)
                            navController.navigate(item.route){
                                launchSingleTop = true }
                              },
                    icon = {
                        Icon(
                            imageVector = if (selected) item.iconFilled else item.iconOutlined,
                            contentDescription = null,
                            modifier = Modifier.size(30.dp)
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        selectedTextColor = Color.White,
                        indicatorColor = Color.Transparent,
                        unselectedIconColor = Color.White.copy(alpha = 1f),
                        unselectedTextColor = Color.White.copy(alpha = 1f)
                    )
                )
            }


            Spacer(modifier = Modifier.weight(0.7f))

            val thirdItem = items.getOrNull(2)
            thirdItem?.let { item ->
                val selected = currentRoute == item.route
                NavigationBarItem(
                    selected = selected,
                    onClick = {
                        if (currentRoute != item.route) {
                            navController.navigate(item.route) {
                                launchSingleTop = true
                            }
                        }
                    },
                    icon = {
                        Icon(
                            imageVector = if (selected) item.iconFilled else item.iconOutlined,
                            contentDescription = null,
                            modifier = Modifier.size(30.dp)
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        selectedTextColor = Color.White,
                        indicatorColor = Color.Transparent,
                        unselectedIconColor = Color.White.copy(alpha = 1f),
                        unselectedTextColor = Color.White.copy(alpha = 1f)
                    )
                )
            }

            //val userRole = TokenManager.getUserRole()
            val dynamicItem = when (role) {
                "CLIENT" -> NavItem("profile", Icons.Outlined.Person, Icons.Filled.Person)
                "MANAGER" -> NavItem("agencyProfile", Icons.Outlined.RealEstateAgent, Icons.Filled.RealEstateAgent)
                else -> NavItem("logout", Icons.Outlined.Logout, Icons.Filled.Logout)
            }

            val selectedDynamic = currentRoute == dynamicItem.route
            NavigationBarItem(
                selected = selectedDynamic,
                onClick = {
                    if (dynamicItem.route == "logout") {
                        TokenManager.clearSession()
                        navController.navigate("loginscreen") {
                            popUpTo(0) // Rimuove tutto lo stack di navigazione
                        }
                    } else if (currentRoute != dynamicItem.route) {
                        navController.navigate(dynamicItem.route) {
                            launchSingleTop = true
                        }
                    }
                },
                icon = {
                    Icon(
                        imageVector = if (selectedDynamic) dynamicItem.iconFilled else dynamicItem.iconOutlined,
                        contentDescription = null,
                        modifier = Modifier.size(30.dp)
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.White,
                    selectedTextColor = Color.White,
                    indicatorColor = Color.Transparent,
                    unselectedIconColor = Color.White.copy(alpha = 1f),
                    unselectedTextColor = Color.White.copy(alpha = 1f)
                )
            )
        }
        if(role == "AGENT" || role == "SUPPORT-ADMIN" || role == "MANAGER") {
            FloatingActionButton(
                onClick = { navController.navigate("createlistingscreen") },
                containerColor = Color(0xFF3F51B5),
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .offset(y = (-20).dp) // Stacca il bottone dalla NavigationBar
            ) {
                Icon(Icons.Outlined.Add, contentDescription = "Add")
            }
        }else{
            FloatingActionButton(
                onClick = { navController.navigate("researchScreen") },
                containerColor = Color(0xFF3F51B5),
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .offset(y = (-20).dp)
            ) {
                Icon(Icons.Outlined.Search, contentDescription = "Search")
            }
        }
    }
}
