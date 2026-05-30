package com.example.ridesharingapplication.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_locations")
data class SavedLocationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long,
    val label: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val type: String
)
