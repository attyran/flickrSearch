package com.attyran.flickrsearch.di

import com.attyran.flickrsearch.PhotoSearchFragment
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class, NetworkModule::class])
interface AppComponent {

    fun inject(photoSearchFragment: PhotoSearchFragment)
}