package com.ambercatalbas.vaktinde.core.data.di

import com.ambercatalbas.vaktinde.core.data.remote.aladhan.AladhanApiService
import com.ambercatalbas.vaktinde.core.data.remote.diyanet.DiyanetApiService
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideGson(): Gson = GsonBuilder().create()

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(
                HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BASIC
                }
            )
            .build()
    }

    @Provides
    @Singleton
    @Named("aladhan")
    fun provideAladhanRetrofit(client: OkHttpClient, gson: Gson): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.aladhan.com/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    @Provides
    @Singleton
    @Named("diyanet")
    fun provideDiyanetRetrofit(client: OkHttpClient, gson: Gson): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://ezanvakti.imsakiyem.com/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    @Provides
    @Singleton
    fun provideAladhanApiService(@Named("aladhan") retrofit: Retrofit): AladhanApiService {
        return retrofit.create(AladhanApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideDiyanetApiService(@Named("diyanet") retrofit: Retrofit): DiyanetApiService {
        return retrofit.create(DiyanetApiService::class.java)
    }
}
