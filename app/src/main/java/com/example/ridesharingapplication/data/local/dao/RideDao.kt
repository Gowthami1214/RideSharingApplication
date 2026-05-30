package com.example.ridesharingapplication.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.ridesharingapplication.data.local.entity.RideEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RideDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRide(ride: RideEntity): Long

    @Delete
    suspend fun deleteRide(ride: RideEntity)

    @Query("SELECT * FROM rides WHERE userId = :userId ORDER BY rideDate DESC")
    fun observeRides(userId: Long): Flow<List<RideEntity>>

    @Query(
        "SELECT * FROM rides WHERE userId = :userId AND " +
            "(pickupLocation LIKE '%' || :query || '%' OR destination LIKE '%' || :query || '%' OR status LIKE '%' || :query || '%') " +
            "ORDER BY rideDate DESC"
    )
    fun searchRides(userId: Long, query: String): Flow<List<RideEntity>>
}
