package com.example.dietiestates

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.dietiestates.ui.screens.ChangePassword
import com.example.dietiestates.ui.screens.CreateListingScreen
import com.example.dietiestates.ui.screens.FullTextScreen
import com.example.dietiestates.ui.screens.HomeScreen
import com.example.dietiestates.ui.screens.ListingScreen
import com.example.dietiestates.ui.screens.LoginScreen
import com.example.dietiestates.ui.screens.MapSearchScreen
import com.example.dietiestates.ui.screens.RegisterScreen
import com.example.dietiestates.ui.screens.ModifyListingScreen
import com.example.dietiestates.ui.screens.ResearchScreen
import com.example.dietiestates.ui.theme.CustomTypography
import com.example.dietiestates.ui.theme.DietiEstatesTheme
import com.example.dietiestates.ui.theme.LocalAppTypography
import com.example.dietiestates.ui.viewModel.AuthViewModel


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
    //val startDestination = "researchscreen"

    val startDestination = if (authViewModel.isLoggedIn.value) "home" else "loginscreen"


    NavHost(navController = navController, startDestination = startDestination) {

        composable(route = "loginscreen") {
            LoginScreen(navController)
        }
        composable(
            route ="registerscreen"
            ) {
            RegisterScreen(navController = navController)
        }
        composable(
            route = "changepasswordscreen",
        ){
            ChangePassword(navController= navController)
        }

        composable(
            route = "listingscreen/{listingId}",
            arguments = listOf(navArgument("listingId") { type = NavType.StringType })
        ) { backStackEntry ->
            ListingScreen(navController)
        }
        composable(
            route = "modifylistingscreen/{listingId}",
            arguments = listOf(navArgument("listingId") { type = NavType.StringType })
        ) { backStackEntry ->

            ModifyListingScreen(navController)

        }
        composable(
            route = "listingviewdescriptionscreen/{text}",
            arguments = listOf(navArgument("text") { type = NavType.StringType })
        ) { backStackEntry ->
            val text = backStackEntry.arguments?.getString("text") ?: ""
            FullTextScreen(navController, text = text)
        }
        composable(route = "createlistingscreen") {
            CreateListingScreen(navController)
        }
        composable(route = "home") {
            HomeScreen(navController)
        }
        composable(route = "logout") {
            authViewModel.logout()
        }
        composable("map_search") {
            MapSearchScreen(navController = navController)
        }
        composable(route = "researchscreen") {
            ResearchScreen(navController = navController)
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



