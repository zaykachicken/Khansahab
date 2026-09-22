package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "restaurant_contact_info")
data class RestaurantContactEntity(
    @PrimaryKey val id: Int = 1,
    val restaurantName: String = "Zayka Chicken Cafe",
    val supportPhone: String = "+91 98765 12345",
    val whatsappNumber: String = "+91 98765 12345",
    val supportEmail: String = "help@zaykacafe.com",
    val operatingHours: String = "10:00 AM - 11:30 PM (Mon-Sun)",
    val address: String = "Near Metro Gate 2, Sector 18, Noida, UP - 201301",
    val fssaiNumber: String = "12722055000492",
    val emergencyManagerContact: String = "+91 98111 22334",
    val preparationTimeMinutes: Int = 25,
    val deliveryFeeAmount: Double = 20.0,
    val helpDeskDescription: String = "We are committed to serving authentic chicken delicacies and ensuring prompt doorstep delivery. Reach us anytime for order inquiries, special bulk catering, or delivery status.",
    val faq1Question: String = "How can I track my live order?",
    val faq1Answer: String = "Go to the Orders tab and tap 'Track Live Order' to see real-time updates directly from our kitchen.",
    val faq2Question: String = "What is the average delivery time?",
    val faq2Answer: String = "Most orders are delivered within 30-40 minutes from our Sector 18 kitchen.",
    val faq3Question: String = "How do I cancel or modify my order?",
    val faq3Answer: String = "Call our direct restaurant helpline or message us on WhatsApp immediately before kitchen preparation begins."
)
