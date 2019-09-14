package com.attyran.flickrsearch

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.attyran.flickrsearch.network.*
import io.reactivex.Single
import org.junit.Before
import org.junit.ClassRule
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.mockito.runners.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class PhotoSearchViewModelTest {
    @Mock
    private lateinit var backendClient: BackendClient

    @Mock
    lateinit var tempObserver: Observer<PhotoSearchResponse>

    private lateinit var viewModel: PhotoSearchViewModel
    private lateinit var photoSearchClient: PhotoSearchClient

    // A JUnit Test Rule that swaps the background executor used by
    // the Architecture Components with a different one which executes each task synchronously.
    // You can use this rule for your host side tests that use Architecture Components.
    @Rule
    @JvmField
    var rule = InstantTaskExecutorRule()

    // Test rule for making the RxJava to run synchronously in unit test
    companion object {
        @ClassRule
        @JvmField
        val schedulers = RxImmediateSchedulerRule()

        const val tag = "blah"
    }

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        photoSearchClient = PhotoSearchClient(backendClient)
        viewModel = PhotoSearchViewModel(photoSearchClient)
    }

    @Test
    fun testSearchReturned_whenRequested() {
        // mock data
        val mockResponse = createMockPhotoSearchResponse(mockPhotos)

        Mockito.`when`(backendClient.search(tag)).
            thenReturn(Single.just(mockResponse))
        viewModel.photoSearchResponse.observeForever(tempObserver)
        viewModel.search(tag)

        for (i in viewModel.photoSearchResponse.value!!.photos.photo.indices) {
            assert(viewModel.photoSearchResponse.value!!.photos.photo[i] == mockResponse.photos.photo[i])
        }
    }

    @Test
    fun testSearchReturned_empty() {
        val mockResponse = createMockPhotoSearchResponse()
        Mockito.`when`(backendClient.search(tag)).
            thenReturn(Single.just(
                PhotoSearchResponse(
                    Result(
                        ArrayList()
                    )
                )
            ))
        viewModel.photoSearchResponse.observeForever(tempObserver)
        viewModel.search(tag)

        assert(viewModel.photoSearchResponse.value!!.photos.photo.size == mockResponse.photos.photo.size)
    }
}