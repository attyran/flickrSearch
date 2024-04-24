package com.attyran.flickrsearch

import androidx.navigation.NavHostController
import com.attyran.flickrsearch.FlickrDestinationsArgs.PHOTO_URL_ARG
import com.attyran.flickrsearch.FlickrScreens.DETAILS_SCREEN
import com.attyran.flickrsearch.FlickrScreens.SEARCH_SCREEN

private object FlickrScreens {
    const val SEARCH_SCREEN = "search"
    const val DETAILS_SCREEN = "details"
}

object FlickrDestinationsArgs {
    const val PHOTO_URL_ARG = "photoURL"
}

object FlickrDestinations {
    const val SEARCH_ROUTE = SEARCH_SCREEN
    const val DETAILS_ROUTE = "$DETAILS_SCREEN?$PHOTO_URL_ARG={$PHOTO_URL_ARG}"
}

class FlickrNavigationActions(private val navController: NavHostController) {
    fun navigateToDetails(photoURL: String) {
        val route = FlickrDestinations.DETAILS_ROUTE.replace("{$PHOTO_URL_ARG}", photoURL)
        navController.navigate(route)
    }
}