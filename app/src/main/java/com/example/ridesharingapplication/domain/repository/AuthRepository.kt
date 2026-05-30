package com.example.ridesharingapplication.domain.repository

import android.content.Context
import com.example.ridesharingapplication.data.local.datastore.SessionState
import com.example.ridesharingapplication.data.local.entity.UserEntity
import com.example.ridesharingapplication.domain.model.AuthResult
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val session: Flow<SessionState>
    suspend fun signup(username: String, email: String, phone: String, password: String): AuthResult
    suspend fun login(email: String, password: String, rememberMe: Boolean): AuthResult
    suspend fun signInWithGoogle(context: Context): AuthResult
    suspend fun logout()
    fun observeUser(userId: Long): Flow<UserEntity?>
}
