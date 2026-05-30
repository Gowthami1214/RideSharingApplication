package com.example.ridesharingapplication.data.local.datastore

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.sessionDataStore by preferencesDataStore(name = "session_preferences")

data class SessionState(val userId: Long? = null, val rememberMe: Boolean = false) {
    val isLoggedIn: Boolean = userId != null && userId > 0
}

@Singleton
class SessionManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val userIdKey = longPreferencesKey("user_id")
    private val rememberMeKey = booleanPreferencesKey("remember_me")

    val session: Flow<SessionState> = context.sessionDataStore.data.map { preferences ->
        SessionState(
            userId = preferences[userIdKey]?.takeIf { it > 0 },
            rememberMe = preferences[rememberMeKey] ?: false
        )
    }

    suspend fun saveSession(userId: Long, rememberMe: Boolean) {
        context.sessionDataStore.edit { preferences ->
            preferences[userIdKey] = userId
            preferences[rememberMeKey] = rememberMe
        }
    }

    suspend fun clearSession() {
        context.sessionDataStore.edit { preferences ->
            preferences.remove(userIdKey)
            preferences[rememberMeKey] = false
        }
    }
}
