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
        get() {
            val normalized = email.trim().lowercase()
            return role == "RESTAURANT_ADMIN" ||
                    normalized == "yashrabalam9@gmail.com" ||
                    normalized.startsWith("admin@")
        }
}
