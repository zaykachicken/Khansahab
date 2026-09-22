package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey
    val email: String,
    val displayName: String,
    val passwordHash: String,
    val phone: String = "",
    val role: String = "CUSTOMER", // "CUSTOMER", "RESTAURANT_ADMIN"
    val createdAt: Long = System.currentTimeMillis()
)
