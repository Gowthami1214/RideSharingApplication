package com.example.ridesharingapplication.domain.usecase

import com.example.ridesharingapplication.domain.repository.AuthRepository
import javax.inject.Inject

data class AuthUseCases @Inject constructor(
    val repository: AuthRepository
)
