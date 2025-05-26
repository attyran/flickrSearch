package com.attyran.flickrsearch

import androidx.paging.PagingSource
import com.attyran.flickrsearch.network.BackendService
import com.attyran.flickrsearch.network.PhotoItem
import com.attyran.flickrsearch.network.PhotoResponse
import com.attyran.flickrsearch.network.Photos
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class FlickrPagingSourceTest {
    private lateinit var backendService: BackendService
    private lateinit var pagingSource: FlickrPagingSource

    @Before
    fun setup() {
        backendService = mockk()
        pagingSource = FlickrPagingSource(backendService, "test")
    }

    @Test
    fun `load returns success result when API call is successful`() = runTest {
        // Arrange
        val photos = listOf(
            PhotoItem(
                id = "1",
                owner = "owner1",
                secret = "secret1",
                server = "server1",
                farm = "1",
                title = "title1",
                isPublic = "1",
                isFriend = "0",
                isFamily = "0"
            )
        )
        val photoResponse = PhotoResponse(
            photos = Photos(
                page = 1,
                pages = 2,
                perPage = 10,
                total = 15,
                photo = photos
            ),
            stat = "ok"
        )

        coEvery {
            backendService.search(
                tag = "test",
                page = 1,
                perPage = 10
            )
        } returns photoResponse

        // Act
        val result = pagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = 1,
                loadSize = 10,
                placeholdersEnabled = false
            )
        )

        // Assert
        assertTrue(result is PagingSource.LoadResult.Page)
        assertEquals(photos, result.data)
        assertEquals(null, result.prevKey)
        assertEquals(2, result.nextKey)
    }

    @Test
    fun `load returns error result when API call fails`() = runTest {
        // Arrange
        val exception = RuntimeException("API error")
        coEvery {
            backendService.search(
                tag = "test",
                page = 1,
                perPage = 10
            )
        } throws exception

        // Act
        val result = pagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = 1,
                loadSize = 10,
                placeholdersEnabled = false
            )
        )

        // Assert
        assertTrue(result is PagingSource.LoadResult.Error)
        assertEquals(exception, result.throwable)
    }

    @Test
    fun `load returns error result when API response stat is not ok`() = runTest {
        // Arrange
        val photoResponse = PhotoResponse(
            photos = Photos(
                page = 1,
                pages = 0,
                perPage = 10,
                total = 0,
                photo = emptyList()
            ),
            stat = "fail"
        )

        coEvery {
            backendService.search(
                tag = "test",
                page = 1,
                perPage = 10
            )
        } returns photoResponse

        // Act
        val result = pagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = 1,
                loadSize = 10,
                placeholdersEnabled = false
            )
        )

        // Assert
        assertTrue(result is PagingSource.LoadResult.Error)
        assertTrue(result.throwable.message!!.contains("Failed to load photos"))
    }

    @Test
    fun `load handles last page correctly`() = runTest {
        // Arrange
        val photos = listOf(
            PhotoItem(
                id = "1",
                owner = "owner1",
                secret = "secret1",
                server = "server1",
                farm = "1",
                title = "title1",
                isPublic = "1",
                isFriend = "0",
                isFamily = "0"
            )
        )
        val photoResponse = PhotoResponse(
            photos = Photos(
                page = 2,
                pages = 2,
                perPage = 10,
                total = 15,
                photo = photos
            ),
            stat = "ok"
        )

        coEvery {
            backendService.search(
                tag = "test",
                page = 2,
                perPage = 10
            )
        } returns photoResponse

        // Act
        val result = pagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = 2,
                loadSize = 10,
                placeholdersEnabled = false
            )
        )

        // Assert
        assertTrue(result is PagingSource.LoadResult.Page)
        assertEquals(photos, result.data)
        assertEquals(1, result.prevKey)
        assertEquals(null, result.nextKey) // No next page since we're on the last page
    }
}