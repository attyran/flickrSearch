package com.attyran.flickrsearch

import androidx.lifecycle.ViewModel
import com.attyran.flickrsearch.network.BackendClient
import com.attyran.flickrsearch.network.PhotoSearchResponse
import com.attyran.flickrsearch.network.Result
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class PhotoSearchViewModel
@Inject constructor(private val backendClient: BackendClient) : ViewModel() {

    private val _photoState: MutableStateFlow<PhotoSearchResponse> =
        MutableStateFlow(PhotoSearchResponse(Result(ArrayList())))
    val photoState: StateFlow<PhotoSearchResponse> = _photoState

    fun search(tag: String) {
        backendClient.search(tag).retry(MAX_RETRIES)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : SingleObserver<PhotoSearchResponse> {
                override fun onSubscribe(d: Disposable) {
                }

                override fun onSuccess(response: PhotoSearchResponse) {
                    _photoState.value = response
                }

                override fun onError(e: Throwable) {}
            })
    }

    companion object {
        const val MAX_RETRIES: Long = 10
    }
}