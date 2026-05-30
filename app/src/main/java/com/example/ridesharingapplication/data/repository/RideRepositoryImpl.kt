package com.example.ridesharingapplication.data.repository

import com.example.ridesharingapplication.data.local.dao.DriverDao
import com.example.ridesharingapplication.data.local.dao.RideDao
import com.example.ridesharingapplication.data.local.dao.SavedLocationDao
import com.example.ridesharingapplication.data.local.entity.DriverEntity
import com.example.ridesharingapplication.data.local.entity.RideEntity
import com.example.ridesharingapplication.data.local.entity.SavedLocationEntity
import com.example.ridesharingapplication.domain.repository.RideRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class RideRepositoryImpl @Inject constructor(
    private val rideDao: RideDao,
    private val savedLocationDao: SavedLocationDao,
    private val driverDao: DriverDao
) : RideRepository {
    override fun observeRides(userId: Long): Flow<List<RideEntity>> = rideDao.observeRides(userId)
    override fun searchRides(userId: Long, query: String): Flow<List<RideEntity>> = rideDao.searchRides(userId, query)
    override suspend fun insertRide(ride: RideEntity) { rideDao.insertRide(ride) }
    override suspend fun deleteRide(ride: RideEntity) = rideDao.deleteRide(ride)
    override fun observeSavedLocations(userId: Long): Flow<List<SavedLocationEntity>> = savedLocationDao.observeLocations(userId)
    override suspend fun saveLocation(location: SavedLocationEntity) { savedLocationDao.upsertLocation(location) }
    override suspend fun deleteLocation(location: SavedLocationEntity) = savedLocationDao.deleteLocation(location)
    override fun observeDrivers(): Flow<List<DriverEntity>> = driverDao.observeAvailableDrivers()

    override suspend fun seedDrivers() {
        driverDao.clearDrivers()
        driverDao.upsertDrivers(
            listOf(
                DriverEntity(name = "Arjun Rao", imageUrl = "https://images.unsplash.com/photo-1500648767791-00dcc994a43e", vehicle = "Hyundai Aura", plateNumber = "PB 08 EV 2198", rating = 4.9, latitude = 31.2552, longitude = 75.7049),
                DriverEntity(name = "Maya Singh", imageUrl = "https://images.unsplash.com/photo-1494790108377-be9c29b29330", vehicle = "Tata Nexon EV", plateNumber = "PB 36 MX 4412", rating = 4.8, latitude = 31.2518, longitude = 75.7017),
                DriverEntity(name = "Rohan Mehta", imageUrl = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d", vehicle = "Honda City", plateNumber = "PB 09 RH 7780", rating = 4.7, latitude = 31.2580, longitude = 75.7108)
            )
        )
    }

    override suspend fun registerDriver(driver: DriverEntity) {
        driverDao.upsertDrivers(listOf(driver))
    }
}
