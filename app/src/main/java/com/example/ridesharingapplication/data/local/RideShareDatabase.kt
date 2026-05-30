package com.example.ridesharingapplication.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.ridesharingapplication.data.local.dao.DriverDao
import com.example.ridesharingapplication.data.local.dao.RideDao
import com.example.ridesharingapplication.data.local.dao.SavedLocationDao
import com.example.ridesharingapplication.data.local.dao.UserDao
import com.example.ridesharingapplication.data.local.entity.DriverEntity
import com.example.ridesharingapplication.data.local.entity.RideEntity
import com.example.ridesharingapplication.data.local.entity.SavedLocationEntity
import com.example.ridesharingapplication.data.local.entity.UserEntity

@Database(
    entities = [UserEntity::class, RideEntity::class, SavedLocationEntity::class, DriverEntity::class],
    version = 1,
    exportSchema = false
)
abstract class RideShareDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun rideDao(): RideDao
    abstract fun savedLocationDao(): SavedLocationDao
    abstract fun driverDao(): DriverDao
}
