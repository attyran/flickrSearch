package com.attyran.flickrsearch

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.attyran.flickrsearch.FlickrDestinationsArgs.PHOTO_URL_ARG

@Composable
fun FlickrNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = FlickrDestinations.SEARCH_ROUTE,
    navActions: FlickrNavigationActions = remember(navController) {
        FlickrNavigationActions(navController)
    }
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(FlickrDestinations.SEARCH_ROUTE) {
            FlickrApp(onPhotoClicked = { photoURL ->
                navActions.navigateToDetails(photoURL)
            })
        }
        composable(
            FlickrDestinations.DETAILS_ROUTE,
            arguments = listOf(
                navArgument(PHOTO_URL_ARG) { type = NavType.StringType; nullable = false }
            )
        ) { entry ->
            DetailsScreen(
                entry.arguments?.getString(PHOTO_URL_ARG)!!
            )
        }
    }
}