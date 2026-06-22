package com.attyran.flickrsearch

import androidx.paging.PagingData
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class FlickrViewModelTest {
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: FlickrRepository
    private lateinit var viewModel: FlickrViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk()
        every { repository.searchTag(any()) } returns flowOf(PagingData.empty())
        viewModel = FlickrViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial uiState is Idle`() {
        assertEquals(FlickrContract.UiState.Idle, viewModel.uiState.value)
    }

    @Test
    fun `searchTag with blank query sets Error`() = runTest {
        viewModel.processIntent(FlickrContract.Intent.Search("   "))

        assertEquals(
            FlickrContract.UiState.Error("Search query is blank"),
            viewModel.uiState.value
        )
        verify(exactly = 0) { repository.searchTag(any()) }
    }

    @Test
    fun `searchTag with new query sets Success`() = runTest {
        viewModel.processIntent(FlickrContract.Intent.Search("keanu"))
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value is FlickrContract.UiState.Success)
        verify(exactly = 1) { repository.searchTag("keanu") }
    }

    @Test
    fun `searchTag with same query does not re-fetch`() = runTest {
        viewModel.processIntent(FlickrContract.Intent.Search("keanu"))
        advanceUntilIdle()

        viewModel.processIntent(FlickrContract.Intent.Search("keanu"))
        advanceUntilIdle()

        verify(exactly = 1) { repository.searchTag("keanu") }
    }

    @Test
    fun `new search after success triggers new query fetch`() = runTest {
        viewModel.processIntent(FlickrContract.Intent.Search("keanu"))
        advanceUntilIdle()

        viewModel.processIntent(FlickrContract.Intent.Search("matrix"))
        advanceUntilIdle()

        verify(exactly = 1) { repository.searchTag("keanu") }
        verify(exactly = 1) { repository.searchTag("matrix") }
    }
}
