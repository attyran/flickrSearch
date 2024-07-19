package com.attyran.flickrsearch

import com.attyran.flickrsearch.network.OAuthService
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import kotlin.test.assertEquals

class OAuthServiceTest {

    private lateinit var service: OAuthService
    private lateinit var mockWebServer: MockWebServer

    @Before
    fun setUp() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        val client = OkHttpClient.Builder().build()
        service = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .client(client)
            .addConverterFactory(ScalarsConverterFactory.create())
            .build()
            .create(OAuthService::class.java)
    }

    @Test
    fun `getAccessToken returns expected response`() = runBlocking {
        val expectedResponse = "access_token=token"
        val mockResponse = MockResponse()
            .setBody(expectedResponse)
        mockWebServer.enqueue(mockResponse)

        val result = service.getAccessToken(
            "nonce",
            "timestamp",
            "verifier",
            "consumerKey",
            "signatureMethod",
            "1.0",
            "token",
            "signature")

        assertEquals(true, result.isSuccessful)
        assertEquals(expectedResponse, result.body())
    }

    @Test
    fun `getRequestToken returns expected response`() = runBlocking {
        val expectedResponse = "oauth_token=token&oauth_token_secret=secret"
        mockWebServer.enqueue(MockResponse().setBody(expectedResponse))

        val result = service.getRequestToken(
            "nonce",
            "timestamp",
            "consumerKey",
            "callback",
            "1.0",
            "callback",
            "signature",
            )
        assertEquals(true, result.isSuccessful)
        assertEquals(expectedResponse, result.body())
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }
}