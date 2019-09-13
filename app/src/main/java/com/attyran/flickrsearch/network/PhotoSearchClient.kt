package com.attyran.flickrsearch.network

import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class PhotoSearchClient
@Inject constructor(private val backendClient: BackendClient) {

    fun search(tag: String, callback: PhotoSearchClientCallback) {
        backendClient.search(tag).retry(MAX_RETRIES)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : SingleObserver<PhotoSearchResponse> {
                    override fun onSubscribe(d: Disposable) {
                    }

                    override fun onSuccess(response: PhotoSearchResponse) {
                        callback.onSuccess(response)
                    }

                    override fun onError(e: Throwable) {
                        callback.onError(e)
                    }
                })
    }

    interface PhotoSearchClientCallback {
        fun onSuccess(response: PhotoSearchResponse)
        fun onError(errorMessage: Throwable)
    }

    companion object {
        const val MAX_RETRIES: Long = 10
    }
}