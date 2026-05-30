package com.example.ridesharingapplication.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "rides")
data class RideEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long,
    val pickupLocation: String,
    val destination: String,
    val fare: Double,
    val rideDate: Long = System.currentTimeMillis(),
    val status: String
)
