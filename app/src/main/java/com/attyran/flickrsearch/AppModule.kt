package com.attyran.flickrsearch

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import com.attyran.flickrsearch.network.BackendService
import com.attyran.flickrsearch.network.BackendClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    fun provideBackendClient(): BackendClient {
        return Retrofit.Builder()
            .baseUrl(BackendClient.BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(BackendClient::class.java)
    }

    @Provides
    fun provideBackendService(backendClient: BackendClient): BackendService {
        return BackendService(backendClient)
    }
}