package com.example.ridesharingapplication.domain.repository

import com.example.ridesharingapplication.data.local.entity.DriverEntity
import com.example.ridesharingapplication.data.local.entity.RideEntity
import com.example.ridesharingapplication.data.local.entity.SavedLocationEntity
import kotlinx.coroutines.flow.Flow

interface RideRepository {
    fun observeRides(userId: Long): Flow<List<RideEntity>>
    fun searchRides(userId: Long, query: String): Flow<List<RideEntity>>
    suspend fun insertRide(ride: RideEntity)
    suspend fun deleteRide(ride: RideEntity)
    fun observeSavedLocations(userId: Long): Flow<List<SavedLocationEntity>>
    suspend fun saveLocation(location: SavedLocationEntity)
    suspend fun deleteLocation(location: SavedLocationEntity)
    fun observeDrivers(): Flow<List<DriverEntity>>
    suspend fun seedDrivers()
    suspend fun registerDriver(driver: DriverEntity)
}
