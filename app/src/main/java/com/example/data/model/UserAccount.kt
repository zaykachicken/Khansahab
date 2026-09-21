package com.example.data.model

data class UserAccount(
    val uid: String,
    val displayName: String,
    val email: String,
    val photoUrl: String? = null,
    val isAnonymous: Boolean = false,
    val authProvider: String = "Google"
)
