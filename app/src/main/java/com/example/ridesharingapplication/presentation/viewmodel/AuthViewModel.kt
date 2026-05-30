package com.example.ridesharingapplication.presentation.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ridesharingapplication.data.local.datastore.SessionState
import com.example.ridesharingapplication.data.local.entity.UserEntity
import com.example.ridesharingapplication.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class AuthUiState(
    val isLoading: Boolean = false,
    val message: String? = null,
    val isAuthenticated: Boolean = false
)

@HiltViewModel
@OptIn(ExperimentalCoroutinesApi::class)
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    val session: StateFlow<SessionState> = authRepository.session.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = SessionState()
    )

    val currentUser: StateFlow<UserEntity?> = session.flatMapLatest { state ->
        state.userId?.let(authRepository::observeUser) ?: flowOf(null)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun login(email: String, password: String, rememberMe: Boolean) {
        viewModelScope.launch {
            _uiState.value = AuthUiState(isLoading = true)
            val result = authRepository.login(email, password, rememberMe)
            _uiState.value = AuthUiState(message = result.message, isAuthenticated = result.success)
        }
    }

    fun signup(username: String, email: String, phone: String, password: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState(isLoading = true)
            val result = authRepository.signup(username, email, phone, password)
            _uiState.value = AuthUiState(message = result.message, isAuthenticated = result.success)
        }
    }

    fun signInWithGoogle(context: Context) {
        viewModelScope.launch {
            _uiState.value = AuthUiState(isLoading = true)
            val result = authRepository.signInWithGoogle(context)
            _uiState.value = AuthUiState(message = result.message, isAuthenticated = result.success)
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
            _uiState.value = AuthUiState(message = "Logged out.")
        }
    }

    fun clearMessage() {
        _uiState.value = _uiState.value.copy(message = null)
    }
}
