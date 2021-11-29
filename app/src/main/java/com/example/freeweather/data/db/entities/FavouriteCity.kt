package com.example.freeweather.data.db.entities

import androidx.room.*
import kotlinx.coroutines.flow.Flow

private const val TABLE_NAME = "favourite_cities"
private const val COL_NAME = "name"
private const val COL_STATE = "state"
private const val COL_COUNTRY = "country"
private const val COL_LATITUDE = "lat"
private const val COL_LONGITUDE = "lon"


@Entity(tableName = TABLE_NAME, primaryKeys = [COL_LATITUDE, COL_LONGITUDE])
data class FavouriteCity(
    @ColumnInfo(name = COL_NAME)
    val name: String,
    @ColumnInfo(name = COL_STATE)
    val state: String?,
    @ColumnInfo(name = COL_COUNTRY)
    val country: String,
    @ColumnInfo(name = COL_LATITUDE)
    val latitude: Double,
    @ColumnInfo(name = COL_LONGITUDE)
    val longitude: Double
)

@Dao
interface FavouriteCityDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(favouriteCity: FavouriteCity)

    @Delete
    suspend fun delete(favouriteCity: FavouriteCity)

    @Query("SELECT * FROM $TABLE_NAME")
    fun getAll(): Flow<List<FavouriteCity>>

    @Query("SELECT * FROM $TABLE_NAME WHERE $COL_LATITUDE = :latitude AND $COL_LONGITUDE = :longitude")
    suspend fun getFavouriteCityByCoordinates(latitude: Double, longitude: Double): FavouriteCity?
}