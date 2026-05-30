package com.example.ridesharingapplication.domain.model

import com.google.android.gms.maps.model.LatLng

data class DriverProfile(
    val name: String,
    val imageUrl: String,
    val vehicle: String,
    val plate: String,
    val rating: Double,
    val etaMinutes: Int,
    val position: LatLng
)
