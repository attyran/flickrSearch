package com.attyran.flickrsearch

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import javax.inject.Inject

class PhotoSearchViewModelFactory
@Inject constructor(private val photoSearchClient: PhotoSearchClient): ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PhotoSearchViewModel::class.java)) {
            return PhotoSearchViewModel(photoSearchClient) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}