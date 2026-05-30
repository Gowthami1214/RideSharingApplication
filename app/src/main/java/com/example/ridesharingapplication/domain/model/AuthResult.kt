package com.example.ridesharingapplication.domain.model

data class AuthResult(
    val success: Boolean,
    val message: String,
    val userId: Long? = null
)
