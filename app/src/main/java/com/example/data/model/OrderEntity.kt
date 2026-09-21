package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey(autoGenerate = true) val orderId: Long = 0L,
    val orderNumber: String,
    val itemsSummaryJson: String,
    val subtotal: Double,
    val deliveryFee: Double,
    val tax: Double,
    val packagingFee: Double,
    val discount: Double,
    val total: Double,
    val appliedCoupon: String = "",
    val deliveryType: String = "DELIVERY",
    val deliveryAddress: String,
    val deliveryInstruction: String = "",
    val paymentMethod: String = "UPI",
    val status: String = "PLACED",
    val orderTimestamp: Long = System.currentTimeMillis(),
    val etaMinutes: Int = 30,
    val riderName: String = "Ramesh Kumar",
    val riderPhone: String = "+91 98765 43210",
    val riderRating: Float = 4.9f,
    val rating: Int = 0,
    val reviewFeedback: String = ""
)

object OrderStatus {
    const val PLACED = "PLACED"
    const val CONFIRMED = "CONFIRMED"
    const val PREPARING = "PREPARING"
    const val OUT_FOR_DELIVERY = "OUT_FOR_DELIVERY"
    const val DELIVERED = "DELIVERED"
    const val CANCELLED = "CANCELLED"
}
