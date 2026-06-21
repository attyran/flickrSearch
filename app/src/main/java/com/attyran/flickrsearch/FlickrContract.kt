package com.attyran.flickrsearch

import androidx.paging.LoadState

/**
 * Defines the contract for the Flickr search feature, grouping UI state and user intents.
 */
object FlickrContract {

    sealed interface UiState {
        data object Idle : UiState
        data object Loading : UiState
        data object Success : UiState
        data class Error(val message: String) : UiState
    }

    sealed interface Intent {
        data class Search(val query: String) : Intent
        data class UpdateLoadState(val state: LoadState) : Intent
    }
}
