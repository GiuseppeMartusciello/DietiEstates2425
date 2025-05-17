package com.example.dietiestates

import android.app.Activity
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.dietiestates.ui.screens.FullTextScreen
import com.example.dietiestates.ui.screens.HomeScreen
import com.example.dietiestates.ui.screens.ListingScreen
import com.example.dietiestates.ui.screens.LoginScreen
import com.example.dietiestates.ui.screens.ModifyListingScreen
import com.example.dietiestates.ui.theme.CustomTypography
import com.example.dietiestates.ui.theme.DietiEstatesTheme
import com.example.dietiestates.ui.theme.LocalAppTypography
import com.example.dietiestates.ui.viewModel.AuthViewModel
import com.example.dietiestates.ui.viewModel.HomeViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppContainer.init(applicationContext)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            CompositionLocalProvider(LocalAppTypography provides CustomTypography) {
                DietiEstatesTheme {
                    // A surface container using the 'background' color from the theme
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        MyApp()
                    }
                }
            }
        }
    }
}

@Composable
fun MyApp() {
    //SetStatusBarColor(Color(0xFF3F51B5), darkIcons = false)

    val navController = rememberNavController()
    val authViewModel: AuthViewModel = viewModel()
    authViewModel.checkLogin()
    val startDestination = if (authViewModel.isLoggedIn.value) "home" else "loginscreen"

    NavHost(navController = navController, startDestination = startDestination) {
        composable(route = "loginscreen") {
            LoginScreen(navController)
        }
        composable(
            route = "listingscreen/{listingId}",
            arguments = listOf(navArgument("listingId") { type = NavType.StringType })
        ) { backStackEntry ->
            //val listingId = backStackEntry.arguments?.getString("listingId") ?: ""
            ListingScreen(navController)
        }
        composable(
            route = "modifylistingscreen/{listingId}",
            arguments = listOf(navArgument("listingId") { type = NavType.StringType })
        ) { backStackEntry ->
            //val listingId = backStackEntry.arguments?.getString("listingId") ?: ""
            ModifyListingScreen(navController)
        }
        composable(
            route = "listingviewdescriptionscreen/{text}",
            arguments = listOf(navArgument("text") { type = NavType.StringType })
        ) { backStackEntry ->
            val text = backStackEntry.arguments?.getString("text") ?: ""
            FullTextScreen(navController, text = text)
        }
        composable(route = "home") {
            HomeScreen(navController)
        }
        composable(route = "logout") {
            authViewModel.logout()
        }
    }
}

/*@Composable
fun SetStatusBarColor(color: Color, darkIcons: Boolean = false) {
    val window = (LocalView.current.context as Activity).window
    SideEffect {
        window.statusBarColor = color.toArgb()
        WindowCompat.getInsetsController(window, window.decorView)
            ?.isAppearanceLightStatusBars = darkIcons
    }
}*/



