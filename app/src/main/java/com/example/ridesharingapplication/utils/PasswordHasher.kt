package com.example.ridesharingapplication.utils

import java.security.MessageDigest

object PasswordHasher {
    fun hash(password: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(password.toByteArray())
        return bytes.joinToString(separator = "") { "%02x".format(it) }
    }

    fun matches(rawPassword: String, storedHash: String): Boolean = hash(rawPassword) == storedHash
}
