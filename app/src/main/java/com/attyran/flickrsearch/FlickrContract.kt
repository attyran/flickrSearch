package com.attyran.flickrsearch

import androidx.paging.PagingData
import com.attyran.flickrsearch.network.PhotoItem
import kotlinx.coroutines.flow.Flow

/**
 * Defines the contract for the Flickr search feature, grouping UI state and user intents.
 */
object FlickrContract {

    sealed interface UiState {
        data object Idle : UiState
        data class Success(val photos: Flow<PagingData<PhotoItem>>) : UiState
        data class Error(val message: String) : UiState
    }

    sealed interface Intent {
        data class Search(val query: String) : Intent
    }
}
