package com.attyran.flickrsearch

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Custom Application class initialized with HiltAndroidApp to bootstrap Dagger-Hilt
 * dependency injection for the application.
 */
@HiltAndroidApp
class FlickrApplication: Application() {}