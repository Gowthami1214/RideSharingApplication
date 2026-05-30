package com.example.ridesharingapplication.domain.model

data class RideType(
    val name: String,
    val description: String,
    val multiplier: Double,
    val etaMinutes: Int
)
