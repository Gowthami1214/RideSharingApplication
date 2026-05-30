package com.example.ridesharingapplication.data.repository

import android.content.Context
import android.database.sqlite.SQLiteConstraintException
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import com.example.ridesharingapplication.data.local.dao.UserDao
import com.example.ridesharingapplication.data.local.datastore.SessionManager
import com.example.ridesharingapplication.data.local.datastore.SessionState
import com.example.ridesharingapplication.data.local.entity.UserEntity
import com.example.ridesharingapplication.domain.model.AuthResult
import com.example.ridesharingapplication.domain.repository.AuthRepository
import com.example.ridesharingapplication.utils.PasswordHasher
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await

// Web client ID (type 3) from google-services.json
private const val WEB_CLIENT_ID =
    "489252910084-6m8qtmfqic5u3uocjh1sp46hn1l0acn0.apps.googleusercontent.com"

class AuthRepositoryImpl @Inject constructor(
    private val userDao: UserDao,
    private val sessionManager: SessionManager
) : AuthRepository {
    override val session: Flow<SessionState> = sessionManager.session

    override suspend fun signup(username: String, email: String, phone: String, password: String): AuthResult {
        if (username.length < 3) return AuthResult(false, "Username must be at least 3 characters.")
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) return AuthResult(false, "Enter a valid email.")
        if (phone.length < 8) return AuthResult(false, "Enter a valid phone number.")
        if (password.length < 6) return AuthResult(false, "Password must be at least 6 characters.")

        return try {
            val id = userDao.insertUser(
                UserEntity(
                    username = username.trim(),
                    email = email.trim().lowercase(),
                    phone = phone.trim(),
                    passwordHash = PasswordHasher.hash(password)
                )
            )
            sessionManager.saveSession(id, rememberMe = true)
            AuthResult(true, "Welcome aboard.", id)
        } catch (_: SQLiteConstraintException) {
            AuthResult(false, "An account with this email or username already exists.")
        }
    }

    override suspend fun login(email: String, password: String, rememberMe: Boolean): AuthResult {
        if (email.isBlank() || password.isBlank()) return AuthResult(false, "Email and password are required.")
        val user = userDao.getUserByEmail(email.trim().lowercase())
            ?: return AuthResult(false, "No local account found for this email.")
        if (!PasswordHasher.matches(password, user.passwordHash)) return AuthResult(false, "Incorrect password.")
        sessionManager.saveSession(user.id, rememberMe)
        return AuthResult(true, "Login successful.", user.id)
    }

    override suspend fun signInWithGoogle(context: Context): AuthResult {
        return try {
            val credentialManager = CredentialManager.create(context)
            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(WEB_CLIENT_ID)
                .setAutoSelectEnabled(false)
                .build()
            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            val credentialResponse = credentialManager.getCredential(context, request)
            val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(
                credentialResponse.credential.data
            )
            val idToken = googleIdTokenCredential.idToken

            // Sign in to Firebase with the Google token
            val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
            val authResult = FirebaseAuth.getInstance().signInWithCredential(firebaseCredential).await()
            val firebaseUser = authResult.user
                ?: return AuthResult(false, "Google sign-in failed: no user returned.")

            // Upsert the user into local Room DB to keep session consistent
            val email = firebaseUser.email?.lowercase() ?: return AuthResult(false, "Google account has no email.")
            val displayName = firebaseUser.displayName ?: email.substringBefore("@")
            val existingUser = userDao.getUserByEmail(email)
            val userId = if (existingUser != null) {
                existingUser.id
            } else {
                userDao.insertUser(
                    UserEntity(
                        username = displayName,
                        email = email,
                        phone = "",
                        passwordHash = "" // Firebase-managed; no local password needed
                    )
                )
            }
            sessionManager.saveSession(userId, rememberMe = true)
            AuthResult(true, "Signed in as $displayName", userId)
        } catch (e: GetCredentialException) {
            AuthResult(false, "Google sign-in cancelled or unavailable.")
        } catch (e: Exception) {
            AuthResult(false, "Google sign-in error: ${e.localizedMessage}")
        }
    }

    override suspend fun logout() {
        FirebaseAuth.getInstance().signOut()
        sessionManager.clearSession()
    }

    override fun observeUser(userId: Long): Flow<UserEntity?> = userDao.observeUser(userId)
}
