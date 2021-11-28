package com.example.freeweather.data.db.entities

import androidx.room.*

private const val TABLE_NAME = "favourite_cities"
private const val COL_UID = "uid"
private const val COL_NAME = "name"
private const val COL_STATE = "state"
private const val COL_COUNTRY = "country"
private const val COL_LATITUDE = "lat"
private const val COL_LONGITUDE = "lon"


@Entity(tableName = TABLE_NAME)
data class FavouriteCity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = COL_UID)
    val uid: Long,
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
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(favouriteCity: FavouriteCity)

    @Delete
    suspend fun delete(favouriteCity: FavouriteCity)

    @Query("SELECT * FROM $TABLE_NAME")
    suspend fun getAll(): List<FavouriteCity>

    @Query("SELECT * FROM $TABLE_NAME WHERE $COL_NAME = :name AND $COL_STATE = :state AND $COL_COUNTRY = :country")
    suspend fun getBy(name: String, state: String?, country: String): List<FavouriteCity>
}