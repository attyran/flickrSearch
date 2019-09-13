package com.attyran.flickrsearch.di

import com.attyran.flickrsearch.network.BackendClient
import com.attyran.flickrsearch.BuildConfig
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
class NetworkModule {

    @Provides
    @Singleton
    fun provideRetrofitBuilder(okHttpClient: OkHttpClient): Retrofit.Builder {
        return Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(okHttpClient)
    }

    @Provides
    @Singleton
    fun provideBackendClient(retrofitBuilder: Retrofit.Builder): BackendClient {
        return retrofitBuilder
                .baseUrl(BackendClient.BASE_URL)
                .build()
                .create(BackendClient::class.java)
    }

    @Provides
    @Singleton
    fun provideHttpClient(): OkHttpClient {
        val builder = OkHttpClient().newBuilder()
        if (BuildConfig.DEBUG) {
            val logging = HttpLoggingInterceptor()
            logging.level = HttpLoggingInterceptor.Level.BODY
            builder.addInterceptor(logging)
        }
        return builder.build()
    }
}