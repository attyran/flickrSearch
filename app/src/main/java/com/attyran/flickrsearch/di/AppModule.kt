package com.attyran.flickrsearch.di

import com.attyran.flickrsearch.network.BackendClient
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule {

    @Provides
    @Singleton
    fun provideViewModelFactory(backendClient: BackendClient): PhotoSearchViewModelFactory {
        return PhotoSearchViewModelFactory(backendClient)
    }
}