package com.attyran.flickrsearch

import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class, NetworkModule::class])
interface AppComponent {

    fun inject(fragmentPhotoSearch: FragmentPhotoSearch)
}