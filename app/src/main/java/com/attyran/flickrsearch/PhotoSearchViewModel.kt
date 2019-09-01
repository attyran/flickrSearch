package com.attyran.flickrsearch

import androidx.lifecycle.ViewModel
import javax.inject.Inject

class PhotoSearchViewModel
@Inject constructor(private val photoSearchClient: PhotoSearchClient) : ViewModel() {

    fun search(tag: String, callback: PhotoSearchClient.PhotoSearchClientCallback) {
        photoSearchClient.search(tag, callback)
    }
}