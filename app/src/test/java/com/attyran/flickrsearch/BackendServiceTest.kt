package com.attyran.flickrsearch

import com.attyran.flickrsearch.network.BackendService
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import kotlin.test.assertEquals

class BackendServiceTest {

    private lateinit var service: BackendService
    private lateinit var mockWebServer: MockWebServer

    @Before
    fun setUp() {
        mockWebServer = MockWebServer()
        service = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(BackendService::class.java)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `search returns expected result`() = runBlocking {
        // Arrange
        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody("{\"photos\":{\"page\":1,\"pages\":18,\"perpage\":100,\"total\":1712,\"photo\":[{\"id\":\"53667363754\",\"owner\":\"197736085@N05\",\"secret\":\"49daf7f7b5\",\"server\":\"65535\",\"farm\":66,\"title\":\"The Legendary Star-Lord\",\"ispublic\":1,\"isfriend\":0,\"isfamily\":0}]},\"stat\":\"ok\"}")
        mockWebServer.enqueue(mockResponse)

        // Act
        val result = service.search("test")

        // Assert
        assertEquals("ok", result.stat)
        assertEquals(1, result.photos.page)
        assertEquals(18, result.photos.pages)
        assertEquals(100, result.photos.perPage)
        assertEquals(1712, result.photos.total)
        assertEquals(1, result.photos.photo.size)
        assertEquals("53667363754", result.photos.photo[0].id)
    }
}