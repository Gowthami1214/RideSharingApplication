package com.example.ridesharingapplication.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.ridesharingapplication.data.local.entity.DriverEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DriverDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertDrivers(drivers: List<DriverEntity>)

    @Query("SELECT * FROM drivers WHERE isAvailable = 1")
    fun observeAvailableDrivers(): Flow<List<DriverEntity>>

    @Query("DELETE FROM drivers")
    suspend fun clearDrivers()
}
