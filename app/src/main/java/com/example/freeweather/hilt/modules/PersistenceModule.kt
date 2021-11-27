package com.example.freeweather.hilt.modules

import android.content.Context
import androidx.room.Room
import com.example.freeweather.data.db.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

private const val DB_NAME = "database.db"

@InstallIn(SingletonComponent::class)
@Module
object PersistenceModule {

    @Provides
    @Singleton
    fun provideDatabase(applicationContext: Context) = Room.databaseBuilder(
        applicationContext,
        AppDatabase::class.java,
        DB_NAME
    )
}