package com.example.ridesharingapplication.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.ridesharingapplication.data.local.entity.SavedLocationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SavedLocationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertLocation(location: SavedLocationEntity): Long

    @Delete
    suspend fun deleteLocation(location: SavedLocationEntity)

    @Query("SELECT * FROM saved_locations WHERE userId = :userId ORDER BY type, label")
    fun observeLocations(userId: Long): Flow<List<SavedLocationEntity>>
}
