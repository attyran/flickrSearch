package com.attyran.flickrsearch.di

import com.attyran.flickrsearch.network.PhotoSearchClient
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule {

    @Provides
    @Singleton
    fun provideViewModelFactory(photoSearchClient: PhotoSearchClient): PhotoSearchViewModelFactory {
        return PhotoSearchViewModelFactory(photoSearchClient)
    }
}