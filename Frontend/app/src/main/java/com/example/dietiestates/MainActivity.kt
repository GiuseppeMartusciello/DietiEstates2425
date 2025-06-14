package com.example.dietiestates

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.WindowCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.dietiestates.ui.screens.AgencyProfileScreen
import com.example.dietiestates.ui.screens.ChangePassword
import com.example.dietiestates.ui.screens.ClientsOfferScreen
import com.example.dietiestates.ui.screens.CreateListingScreen
import com.example.dietiestates.ui.screens.FilterScreen
import com.example.dietiestates.ui.screens.FullTextScreen
import com.example.dietiestates.ui.screens.HomeScreen
import com.example.dietiestates.ui.screens.ListingScreen
import com.example.dietiestates.ui.screens.LoginScreen
import com.example.dietiestates.ui.screens.ModifyListingScreen
import com.example.dietiestates.ui.screens.MyOffersScreen
import com.example.dietiestates.ui.screens.OfferScreen
import com.example.dietiestates.ui.screens.MapSearchScreen
import com.example.dietiestates.ui.screens.RegisterScreen
import com.example.dietiestates.ui.screens.NotificationScreen
import com.example.dietiestates.ui.screens.ProfileScreen
import com.example.dietiestates.ui.screens.ResearchScreen
import com.example.dietiestates.ui.screens.SearchedListingScreen
import com.example.dietiestates.ui.theme.CustomTypography
import com.example.dietiestates.ui.theme.DietiEstatesTheme
import com.example.dietiestates.ui.theme.LocalAppTypography
import com.example.dietiestates.ui.viewModel.AuthViewModel
import com.example.dietiestates.ui.viewModel.ResearchViewModel
import com.example.dietiestates.utility.TokenManager


class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppContainer.init(applicationContext)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            CompositionLocalProvider(LocalAppTypography provides CustomTypography) {
                DietiEstatesTheme(darkTheme = false) {
                    // A surface container using the 'background' color from the theme
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = Color.White
                    ) {
                        MyApp()
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MyApp() {
    //SetStatusBarColor(Color(0xFF3F51B5), darkIcons = false)

    val navController = rememberNavController()
    val authViewModel: AuthViewModel = viewModel()
    val startDestination = if (authViewModel.checkLogin()) "home" else "loginscreen"

    NavHost(navController = navController, startDestination = startDestination) {
        composable(route = "loginscreen") {
            LoginScreen(navController)
        }
        composable(
            route = "registerscreen"
        ) {
            RegisterScreen(navController = navController)
        }
        composable(
            route = "changepasswordscreen",
        ) {
            ChangePassword(navController = navController)
        }

        composable(
            route = "listingscreen/{listingId}",
            arguments = listOf(navArgument("listingId") { type = NavType.StringType })
        ) { backStackEntry ->
            if (checkLogin(navController,authViewModel))
                ListingScreen(navController)
        }
        composable(
            route = "modifylistingscreen/{listingId}",
            arguments = listOf(navArgument("listingId") { type = NavType.StringType })
        ) { backStackEntry ->
            if (checkLogin(navController,authViewModel))
                ModifyListingScreen(navController)
        }
        composable(
            route = "listingviewdescriptionscreen/{text}",
            arguments = listOf(navArgument("text") { type = NavType.StringType })
        ) { backStackEntry ->
            val text = backStackEntry.arguments?.getString("text") ?: ""
            if (checkLogin(navController,authViewModel))
                FullTextScreen(navController, text = text)

        }
        composable(route = "createlistingscreen") {
            if (checkLogin(navController,authViewModel))
             CreateListingScreen(navController)

        }
        composable(route = "home") {
            if (checkLogin(navController,authViewModel))
                HomeScreen(navController)
        }
        composable(route = "offer") {
            if (checkLogin(navController,authViewModel))
                MyOffersScreen(navController = navController)
        }
        composable(route = "profile") {
            if (checkLogin(navController,authViewModel))
                ProfileScreen(navController)
        }
        composable(route = "agencyProfile") {
            if (checkLogin(navController,authViewModel))
                AgencyProfileScreen(navController)
        }
        composable(
            route = "listing/offer/{listingId}?clientId={clientId}",
            arguments = listOf(
                navArgument("listingId") { type = NavType.StringType },
                navArgument("clientId") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { backStackEntry ->
            if (checkLogin(navController,authViewModel))
                OfferScreen(navController = navController)
        }

        composable(
            route = "listing/offer/{listingId}/external",
            arguments = listOf(
                navArgument("listingId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            if (checkLogin(navController,authViewModel))
                OfferScreen(navController = navController)
        }

        //SottoRoot con viewModel Condiviso
        searchGraph(navController)

        composable(route = "notification") {
            if (checkLogin(navController,authViewModel))
                NotificationScreen(navController = navController)
        }


        composable(
            route = "agent/listing/offer/{listingId}",
            arguments = listOf(navArgument("listingId") { type = NavType.StringType })
        ) {
            if (checkLogin(navController,authViewModel))
                ClientsOfferScreen(navController = navController)
        }
    }
}


fun NavGraphBuilder.searchGraph(navController: NavController) {
    navigation(
        startDestination = "researchscreen",
        route = "search_root"
    ) {
        composable("researchscreen") { entry ->
            val parentEntry = remember(entry) {
                navController.getBackStackEntry("search_root")
            }
            val viewModel: ResearchViewModel = viewModel(parentEntry)
            ResearchScreen(viewModel,navController)
        }

        composable("mapscreen") { entry ->
            val parentEntry = remember(entry) {
                navController.getBackStackEntry("search_root")
            }
            val viewModel: ResearchViewModel = viewModel(parentEntry)
            MapSearchScreen(viewModel,navController)
        }

        composable("filterScreen") { entry ->
            val parentEntry = remember(entry) {
                navController.getBackStackEntry("search_root")
            }
            val viewModel: ResearchViewModel = viewModel(parentEntry)
            FilterScreen(navController, viewModel)
        }

        composable("searchedscreen") { entry ->
            val parentEntry = remember(entry) {
                navController.getBackStackEntry("search_root")
            }
            val viewModel: ResearchViewModel = viewModel(parentEntry)
            SearchedListingScreen(viewModel,navController)
        }
    }
}

fun checkLogin(navController: NavController,authViewModel: AuthViewModel): Boolean{
    if (!authViewModel.checkLogin()){
        TokenManager.clearSession()
        navController.navigate("loginscreen")
        return false;
    }

    return true;
}












