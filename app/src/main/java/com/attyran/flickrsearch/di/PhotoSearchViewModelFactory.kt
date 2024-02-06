package com.attyran.flickrsearch.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.attyran.flickrsearch.PhotoSearchViewModel
import com.attyran.flickrsearch.network.BackendClient
import javax.inject.Inject

class PhotoSearchViewModelFactory
@Inject constructor(private val backendClient: BackendClient): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PhotoSearchViewModel::class.java)) {
            return PhotoSearchViewModel(backendClient) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}