package com.attyran.flickrsearch

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.attyran.flickrsearch.network.BackendClient
import com.attyran.flickrsearch.network.PhotoSearchResponse
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class PhotoSearchViewModel
@Inject constructor(private val backendClient: BackendClient) : ViewModel() {

    val photoSearchResponse: MutableLiveData<PhotoSearchResponse> = MutableLiveData()

    fun search(tag: String) {
        backendClient.search(tag).retry(MAX_RETRIES)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : SingleObserver<PhotoSearchResponse> {
                override fun onSubscribe(d: Disposable) {
                }

                override fun onSuccess(response: PhotoSearchResponse) {
                    photoSearchResponse.value = response
                }

                override fun onError(e: Throwable) {}
            })
    }

    fun observePhotos() = photoSearchResponse

    companion object {
        const val MAX_RETRIES: Long = 10
    }
}