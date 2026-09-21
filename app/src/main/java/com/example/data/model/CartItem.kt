package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cart_items")
data class CartItem(
    @PrimaryKey(autoGenerate = true) val cartId: Long = 0L,
    val foodItemId: String,
    val name: String,
    val category: String,
    val basePrice: Double,
    val portion: String = "Standard",
    val spiceLevel: String = "Medium Spicy",
    val addonsText: String = "",
    val finalUnitPrice: Double,
    val quantity: Int = 1,
    val isVeg: Boolean,
    val imageDrawableName: String = "img_hero_biryani"
) {
    val totalCost: Double
        get() = finalUnitPrice * quantity
}
