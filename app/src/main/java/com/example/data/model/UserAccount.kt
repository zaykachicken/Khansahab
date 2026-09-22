package com.example.data.model

data class UserAccount(
    val uid: String,
    val displayName: String,
    val email: String,
    val photoUrl: String? = null,
    val isAnonymous: Boolean = false,
    val authProvider: String = "Google",
    val role: String = "CUSTOMER" // "CUSTOMER" or "RESTAURANT_ADMIN"
) {
    val isAdmin: Boolean
        get() = role == "RESTAURANT_ADMIN"
}
