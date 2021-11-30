package com.example.freeweather.hilt.modules

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object JsonModule {

    @Provides
    @Singleton
    fun provideGson(): Gson = GsonBuilder().create()

}