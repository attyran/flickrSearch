package com.attyran.flickrsearch

import androidx.navigation.NavHostController
import com.attyran.flickrsearch.FlickrScreens.DETAILS_SCREEN
import com.attyran.flickrsearch.FlickrScreens.SEARCH_SCREEN

private object FlickrScreens {
    const val SEARCH_SCREEN = "search"
    const val DETAILS_SCREEN = "details"
}

object FlickrDestinations {
    const val SEARCH_ROUTE = SEARCH_SCREEN
    const val DETAILS_ROUTE = DETAILS_SCREEN
}

class FlickrNavigationActions(private val navController: NavHostController) {

}