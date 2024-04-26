package com.attyran.flickrsearch

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import com.attyran.flickrsearch.network.BackendService
import com.attyran.flickrsearch.network.BackendClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideTapToSnapService(): BackendService {
        return BackendService.create()
    }
}