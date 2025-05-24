package com.example.tuaapp.ui.components

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LocalOffer
import androidx.compose.material.icons.outlined.Logout
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.dietiestates.AppContainer

data class NavItem(
    val route: String,
    val iconOutlined: ImageVector,
    val iconFilled: ImageVector,
)

@Composable
fun NavBar(navController: NavController, items: List<NavItem>) {
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
    val role = AppContainer.tokenManager.getUserRole()

    Box(
    ) {
        NavigationBar(
            containerColor = Color(0xFF303F9F),
            tonalElevation = 8.dp,
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
        ) {
            items.take(2).forEach { item ->
                val selected = currentRoute == item.route
                NavigationBarItem(
                    selected = selected,
                    onClick = { navController.navigate(item.route) },
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

            // SPAZIO CENTRALE PER IL FAB
            if(role == "AGENT" || role == "SUPPORT-ADMIN" || role == "MANAGER")
                Spacer(modifier = Modifier.weight(0.7f))

            items.takeLast(2).forEach { item ->
                val selected = currentRoute == item.route
                NavigationBarItem(
                    selected = selected,
                    onClick = { navController.navigate(item.route) },
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
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun test() {
    val navController = rememberNavController()
    NavBar(
        navController = navController, items = listOf(
            NavItem(
                "Home",
                Icons.Outlined.Home, Icons.Filled.Home
            ), NavItem(
                "Cerca",
                Icons.Outlined.Search, Icons.Filled.Search
            ), NavItem(
                "Notifiche",
                Icons.Outlined.LocalOffer, Icons.Outlined.LocalOffer,
            ), NavItem(
                "Logout",
                Icons.Outlined.Person, Icons.Filled.Person,
            )
        )
    )
}
