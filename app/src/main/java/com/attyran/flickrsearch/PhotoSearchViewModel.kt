package com.attyran.flickrsearch

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import javax.inject.Inject

class PhotoSearchViewModel
@Inject constructor(private val photoSearchClient: PhotoSearchClient) : ViewModel() {

    private val photos: MutableLiveData<List<Photo>> = MutableLiveData()

    fun search(tag: String){
        photoSearchClient.search(tag, object : PhotoSearchClient.PhotoSearchClientCallback {
            override fun onSuccess(response: PhotoSearchResponse) {
                photos.value = response.photos.photo
            }

            override fun onError(errorMessage: Throwable) {
            }
        })
    }

    fun observePhotos() = photos
}