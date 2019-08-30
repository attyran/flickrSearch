package com.attyran.flickrsearch

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