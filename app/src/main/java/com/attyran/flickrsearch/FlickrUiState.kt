package com.attyran.flickrsearch

/**
 * Sealed interface representing the UI states of the photo search flow (Idle, Loading, Success, Error).
 */
sealed interface FlickrUiState {
    data object Idle : FlickrUiState
    data object Loading : FlickrUiState
    data object Success : FlickrUiState
    data class Error(val message: String) : FlickrUiState
}
