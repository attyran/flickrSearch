package com.attyran.flickrsearch

import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay

@Composable
fun FlickrNavDisplay(
    backStack: SnapshotStateList<NavKey>,
    modifier: Modifier = Modifier,
    authViewModel: OAuthViewModel
) {
    NavDisplay(
        backStack = backStack,
        modifier = modifier,
        onBack = { backStack.removeLastOrNull() },
        entryProvider = entryProvider {
            entry<SearchKey> {
                FlickrApp(
                    onPhotoClicked = { photoURL -> backStack.add(DetailsKey(photoURL)) },
                    oAuthViewModel = authViewModel
                )
            }
            entry<DetailsKey> { key ->
                DetailsScreen(photoURL = key.photoURL)
            }
        }
    )
}