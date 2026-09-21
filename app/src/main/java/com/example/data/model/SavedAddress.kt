package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_addresses")
data class SavedAddress(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val label: String, // "Home", "Work", "Other"
    val fullAddress: String,
    val landmark: String = "",
    val contactPhone: String = "+91 98765 12345",
    val isDefault: Boolean = false
)
