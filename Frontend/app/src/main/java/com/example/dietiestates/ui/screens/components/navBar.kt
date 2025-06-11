package com.example.tuaapp.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
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
                    onClick = { navController.navigate(item.route){
                        launchSingleTop = true } },
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

            items.takeLast(2).forEach { item ->
                val selected = currentRoute == item.route
                NavigationBarItem(
                    selected = selected,
                    onClick = { navController.navigate(item.route){
                        launchSingleTop = true
                    } },
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
