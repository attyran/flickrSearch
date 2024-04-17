package com.attyran.flickrsearch

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun FlickrNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = FlickrDestinations.SEARCH_ROUTE,
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(FlickrDestinations.SEARCH_ROUTE) {
            FlickApp()
        }
        // TODO: update so we don't have to pass null
        composable(FlickrDestinations.DETAILS_ROUTE) {
            DetailsScreen(null)
        }
    }
}