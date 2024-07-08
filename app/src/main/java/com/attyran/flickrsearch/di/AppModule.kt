package com.attyran.flickrsearch.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import com.attyran.flickrsearch.network.BackendService
import com.attyran.flickrsearch.network.OAuthService
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideBackendService(): BackendService {
        return BackendService.create()
    }

    @Singleton
    @Provides
    fun provideOAuthService(): OAuthService {
        return OAuthService.create()
    }
}