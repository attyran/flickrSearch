package com.attyran.flickrsearch.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface OAuthService {
    @GET("oauth/access_token")
    suspend fun getAccessToken(
        @Query("oauth_nonce") oauthNonce: String,
        @Query("oauth_timestamp") oauthTimestamp: String,
        @Query("oauth_verifier") oauthVerifier: String,
        @Query("oauth_consumer_key") oauthConsumerKey: String,
        @Query("oauth_signature_method") oauthSignatureMethod: String = "HMAC-SHA1",
        @Query("oauth_version") oauthVersion: String = "1.0",
        @Query("oauth_token") oauthToken: String,
        @Query("oauth_signature") oauthSignature: String
    ): Response<String>

    @GET("oauth/request_token")
    suspend fun getRequestToken(
        @Query("oauth_nonce") oauthNonce: String,
        @Query("oauth_timestamp") oauthTimestamp: String,
        @Query("oauth_consumer_key") oauthConsumerKey: String,
        @Query("oauth_signature_method") oauthSignatureMethod: String = "HMAC-SHA1",
        @Query("oauth_version") oauthVersion: String = "1.0",
        @Query("oauth_callback") oauthCallback: String,
        @Query("oauth_signature") oauthSignature: String
    ): Response<String>

    companion object {
        private const val BASE_URL = "https://www.flickr.com/services/"

        fun create(): OAuthService {
            val logger = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BASIC }

            val client = OkHttpClient.Builder()
                .addInterceptor(logger)
                .build()

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build()
                .create(OAuthService::class.java)
        }
    }
}