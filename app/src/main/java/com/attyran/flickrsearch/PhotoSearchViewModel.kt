package com.attyran.flickrsearch

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.attyran.flickrsearch.network.PhotoSearchClient
import com.attyran.flickrsearch.network.PhotoSearchResponse
import javax.inject.Inject

class PhotoSearchViewModel
@Inject constructor(private val photoSearchClient: PhotoSearchClient) : ViewModel() {

    val photoSearchResponse: MutableLiveData<PhotoSearchResponse> = MutableLiveData()

    fun search(tag: String){
        photoSearchClient.search(tag, object : PhotoSearchClient.PhotoSearchClientCallback {
            override fun onSuccess(response: PhotoSearchResponse) {
                photoSearchResponse.value = response
            }

            override fun onError(errorMessage: Throwable) {
            }
        })
    }

    fun observePhotos() = photoSearchResponse
}