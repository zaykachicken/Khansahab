package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "food_items")
data class FoodItem(
    @PrimaryKey val id: String,
    val name: String,
    val category: String,
    val description: String,
    val price: Double,
    val originalPrice: Double,
    val isVeg: Boolean,
    val rating: Float = 4.5f,
    val ratingCount: Int = 120,
    val isBestseller: Boolean = false,
    val imageDrawableName: String = "img_hero_biryani",
    val isAvailable: Boolean = true,
    val portionsJson: String = "[\"Standard\"]",
    val spicesJson: String = "[\"Medium Spicy\"]",
    val addonsJson: String = "[]"
)

data class PortionOption(
    val name: String,
    val priceDelta: Double = 0.0
)

data class AddonOption(
    val id: String,
    val name: String,
    val price: Double
)
