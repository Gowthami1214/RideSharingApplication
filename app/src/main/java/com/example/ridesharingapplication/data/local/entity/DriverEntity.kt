package com.example.ridesharingapplication.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "drivers")
data class DriverEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val imageUrl: String,
    val vehicle: String,
    val plateNumber: String,
    val rating: Double,
    val latitude: Double,
    val longitude: Double,
    val isAvailable: Boolean = true
)
