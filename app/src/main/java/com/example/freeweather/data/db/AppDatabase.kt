package com.example.freeweather.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.freeweather.data.db.entities.FavouriteCity
import com.example.freeweather.data.db.entities.FavouriteCityDao

private const val DB_VERSION = 1

@Database(entities = [ FavouriteCity::class ], version = DB_VERSION)
abstract class AppDatabase : RoomDatabase() {
    abstract fun favouriteCityDao(): FavouriteCityDao
}