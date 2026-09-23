package com.example.data.repository

import android.util.Log
import com.example.data.model.FoodItem
import com.example.data.model.OrderEntity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Manages real-time bi-directional cloud synchronization via Firebase Firestore
 * between the Customer/User App and the Restaurant Admin App.
 *
 * User App (Places Order) -> Firestore "orders" -> Admin App (Receives Alert & Accepts)
 * Admin App (Updates Status) -> Firestore "orders" -> User App (Updates Live Tracking)
 * Admin App (Updates Menu/Prices) -> Firestore "menu" -> User App (Updates Menu Items)
 */
class FirebaseSyncManager(private val repository: ZaykaRepository) {

    private val firestore: FirebaseFirestore? by lazy {
        try {
            FirebaseFirestore.getInstance()
        } catch (t: Throwable) {
            Log.w(TAG, "FirebaseFirestore initialization failed or unavailable: ${t.message}")
            null
        }
    }

    private val scope = CoroutineScope(Dispatchers.IO)
    private var ordersListener: ListenerRegistration? = null
    private var menuListener: ListenerRegistration? = null

    companion object {
        private const val TAG = "FirebaseSyncManager"
        const val COLLECTION_ORDERS = "orders"
        const val COLLECTION_MENU = "menu"
        const val COLLECTION_DEALS = "deals"
    }

    /**
     * Start listening for real-time order updates from Firestore.
     * When the Admin updates an order's status, or a user places an order,
     * it syncs automatically into the local repository.
     */
    fun startListeningForOrders() {
        val db = firestore ?: return
        if (ordersListener != null) return

        try {
            ordersListener = db.collection(COLLECTION_ORDERS)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        Log.w(TAG, "Firestore orders listener error: ${error.message}")
                        return@addSnapshotListener
                    }

                    if (snapshot != null && !snapshot.isEmpty) {
                        scope.launch {
                            for (doc in snapshot.documents) {
                                try {
                                    val orderId = doc.getLong("orderId") ?: doc.id.toLongOrNull() ?: continue
                                    val status = doc.getString("status") ?: continue
                                    val orderNum = doc.getString("orderNumber") ?: "ZYK-$orderId"
                                    val itemsSummary = doc.getString("itemsSummaryJson") ?: ""
                                    val total = doc.getDouble("total") ?: 0.0

                                    // Check if order exists locally
                                    val existing = repository.getOrderDirect(orderId)
                                    if (existing != null) {
                                        if (existing.status != status) {
                                            repository.updateOrderStatusLocally(orderId, status)
                                        }
                                    } else {
                                        // Insert newly received order from cloud
                                        val newOrder = OrderEntity(
                                            orderId = orderId,
                                            orderNumber = orderNum,
                                            itemsSummaryJson = itemsSummary,
                                            subtotal = doc.getDouble("subtotal") ?: total,
                                            deliveryFee = doc.getDouble("deliveryFee") ?: 0.0,
                                            tax = doc.getDouble("tax") ?: 0.0,
                                            packagingFee = doc.getDouble("packagingFee") ?: 0.0,
                                            discount = doc.getDouble("discount") ?: 0.0,
                                            total = total,
                                            appliedCoupon = doc.getString("appliedCoupon") ?: "",
                                            deliveryType = doc.getString("deliveryType") ?: "DELIVERY",
                                            deliveryAddress = doc.getString("deliveryAddress") ?: "",
                                            deliveryInstruction = doc.getString("deliveryInstruction") ?: "",
                                            paymentMethod = doc.getString("paymentMethod") ?: "Cash on Delivery",
                                            status = status,
                                            orderTimestamp = doc.getLong("orderTimestamp") ?: System.currentTimeMillis(),
                                            etaMinutes = doc.getLong("etaMinutes")?.toInt() ?: 30,
                                            riderName = doc.getString("riderName") ?: "Ramesh Kumar",
                                            riderPhone = doc.getString("riderPhone") ?: "+91 98765 43210",
                                            riderRating = (doc.getDouble("riderRating") ?: 4.9).toFloat()
                                        )
                                        repository.insertOrderDirect(newOrder)
                                    }
                                } catch (t: Throwable) {
                                    Log.e(TAG, "Error parsing Firestore order doc: ${t.message}")
                                }
                            }
                        }
                    }
                }
            Log.d(TAG, "Firestore orders real-time synchronization active.")
        } catch (t: Throwable) {
            Log.e(TAG, "Failed to start Firestore orders listener: ${t.message}")
        }
    }

    /**
     * Pushes a placed order to Firestore so the Admin App receives it instantly in the cloud.
     */
    fun syncOrderToCloud(orderId: Long, order: OrderEntity) {
        val db = firestore ?: return
        scope.launch {
            try {
                val data = hashMapOf(
                    "orderId" to orderId,
                    "orderNumber" to order.orderNumber,
                    "itemsSummaryJson" to order.itemsSummaryJson,
                    "subtotal" to order.subtotal,
                    "deliveryFee" to order.deliveryFee,
                    "tax" to order.tax,
                    "packagingFee" to order.packagingFee,
                    "discount" to order.discount,
                    "total" to order.total,
                    "appliedCoupon" to order.appliedCoupon,
                    "deliveryType" to order.deliveryType,
                    "deliveryAddress" to order.deliveryAddress,
                    "deliveryInstruction" to order.deliveryInstruction,
                    "paymentMethod" to order.paymentMethod,
                    "status" to order.status,
                    "orderTimestamp" to order.orderTimestamp,
                    "etaMinutes" to order.etaMinutes,
                    "riderName" to order.riderName,
                    "riderPhone" to order.riderPhone,
                    "riderRating" to order.riderRating
                )
                db.collection(COLLECTION_ORDERS)
                    .document(orderId.toString())
                    .set(data, SetOptions.merge())
                    .addOnSuccessListener {
                        Log.d(TAG, "Order #$orderId successfully synced to Firestore.")
                    }
                    .addOnFailureListener { e ->
                        Log.w(TAG, "Firestore order sync warning: ${e.message}")
                    }
            } catch (t: Throwable) {
                Log.e(TAG, "Error pushing order to Firestore: ${t.message}")
            }
        }
    }

    /**
     * Updates an order's status in Firestore (e.g. from Admin App: PLACED -> CONFIRMED -> PREPARING)
     */
    fun updateOrderStatusInCloud(orderId: Long, newStatus: String) {
        val db = firestore ?: return
        scope.launch {
            try {
                db.collection(COLLECTION_ORDERS)
                    .document(orderId.toString())
                    .update("status", newStatus)
                    .addOnSuccessListener {
                        Log.d(TAG, "Order #$orderId status updated to $newStatus in Firestore.")
                    }
                    .addOnFailureListener { e ->
                        Log.w(TAG, "Firestore status update warning: ${e.message}")
                    }
            } catch (t: Throwable) {
                Log.e(TAG, "Error updating order status in Firestore: ${t.message}")
            }
        }
    }

    /**
     * Syncs updated menu item from Admin to Firestore
     */
    fun syncFoodItemToCloud(item: FoodItem) {
        val db = firestore ?: return
        scope.launch {
            try {
                val data = hashMapOf(
                    "id" to item.id,
                    "name" to item.name,
                    "description" to item.description,
                    "price" to item.price,
                    "originalPrice" to item.originalPrice,
                    "category" to item.category,
                    "isVeg" to item.isVeg,
                    "isBestseller" to item.isBestseller,
                    "isAvailable" to item.isAvailable,
                    "imageDrawableName" to item.imageDrawableName,
                    "rating" to item.rating,
                    "ratingCount" to item.ratingCount
                )
                db.collection(COLLECTION_MENU)
                    .document(item.id)
                    .set(data, SetOptions.merge())
            } catch (t: Throwable) {
                Log.e(TAG, "Error syncing food item to Firestore: ${t.message}")
            }
        }
    }

    /**
     * Stop Firestore listeners when component/app is destroyed
     */
    fun stopListening() {
        ordersListener?.remove()
        ordersListener = null
        menuListener?.remove()
        menuListener = null
    }
}
