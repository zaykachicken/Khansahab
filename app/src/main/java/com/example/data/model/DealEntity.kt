package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "deals_and_discounts")
data class DealEntity(
    @PrimaryKey val code: String,
    val title: String,
    val description: String,
    val minOrder: Double,
    val discountPercent: Double = 0.0,
    val maxDiscount: Double = 0.0,
    val flatDiscount: Double = 0.0,
    val isFreeDelivery: Boolean = false,
    val isActive: Boolean = true,
    val badgeTag: String = "POPULAR" // e.g. "HOT DEAL", "LIMITED TIME", "POPULAR", "SUPER SAVER"
)
