package com.attyran.flickrsearch.di

import android.app.Application

class PhotoSearchApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        initializeDagger()
    }

    private fun initializeDagger() {
        appComponent = DaggerAppComponent.builder()
                .appModule(AppModule()).build()
    }

    companion object {
        lateinit var appComponent: AppComponent
    }
}