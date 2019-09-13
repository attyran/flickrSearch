package com.attyran.flickrsearch.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.attyran.flickrsearch.network.PhotoSearchClient
import com.attyran.flickrsearch.PhotoSearchViewModel
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