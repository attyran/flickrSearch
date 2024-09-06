package com.attyran.flickrsearch

import com.attyran.flickrsearch.network.PhotoItem

sealed class FlickrUiState {
    data object Idle : FlickrUiState()
    data class Success(val photos: List<PhotoItem>) : FlickrUiState()
    data class Error(val message: String) : FlickrUiState()
}