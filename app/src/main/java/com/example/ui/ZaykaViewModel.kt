package com.example.ui

import android.app.Activity
import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.CartItem
import com.example.data.model.DealEntity
import com.example.data.model.FoodItem
import com.example.data.model.OrderEntity
import com.example.data.model.OrderStatus
import com.example.data.model.RestaurantContactEntity
import com.example.data.model.SavedAddress
import com.example.data.model.UserAccount
import com.example.data.repository.AuthManager
import com.example.data.repository.ZaykaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class Coupon(
    val code: String,
    val title: String,
    val description: String,
    val minOrder: Double,
    val discountPercent: Double = 0.0,
    val maxDiscount: Double = 0.0,
    val flatDiscount: Double = 0.0,
    val isFreeDelivery: Boolean = false,
    val isActive: Boolean = true,
    val badgeTag: String = "POPULAR"
)

fun DealEntity.toCoupon(): Coupon = Coupon(
    code = code,
    title = title,
    description = description,
    minOrder = minOrder,
    discountPercent = discountPercent,
    maxDiscount = maxDiscount,
    flatDiscount = flatDiscount,
    isFreeDelivery = isFreeDelivery,
    isActive = isActive,
    badgeTag = badgeTag
)

fun Coupon.toDealEntity(): DealEntity = DealEntity(
    code = code,
    title = title,
    description = description,
    minOrder = minOrder,
    discountPercent = discountPercent,
    maxDiscount = maxDiscount,
    flatDiscount = flatDiscount,
    isFreeDelivery = isFreeDelivery,
    isActive = isActive,
    badgeTag = badgeTag
)

class ZaykaViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = ZaykaRepository(application)
    private val authManager = AuthManager(application)

    val currentUser: StateFlow<UserAccount?> = authManager.currentUser

    private val _authLoading = MutableStateFlow(false)
    val authLoading: StateFlow<Boolean> = _authLoading.asStateFlow()

    private val _authError = MutableStateFlow<String?>(null)
    val authError: StateFlow<String?> = _authError.asStateFlow()

    val allFoodItems: StateFlow<List<FoodItem>> = repository.allFoodItems
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val cartItems: StateFlow<List<CartItem>> = repository.cartItems
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allOrders: StateFlow<List<OrderEntity>> = repository.allOrders
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeOrders: StateFlow<List<OrderEntity>> = repository.activeOrders
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // New Incoming Orders (Placed, awaiting kitchen confirmation)
    val unacceptedOrders: StateFlow<List<OrderEntity>> = repository.allOrders.map { orders ->
        orders.filter { it.status == OrderStatus.PLACED }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Admin Ringing Alert Preferences & State
    private val _isAdminSoundAlertEnabled = MutableStateFlow(true)
    val isAdminSoundAlertEnabled: StateFlow<Boolean> = _isAdminSoundAlertEnabled.asStateFlow()

    private val _isAlarmMutedForCurrentBatch = MutableStateFlow(false)
    val isAlarmMutedForCurrentBatch: StateFlow<Boolean> = _isAlarmMutedForCurrentBatch.asStateFlow()

    fun toggleAdminSoundAlert() {
        _isAdminSoundAlertEnabled.value = !_isAdminSoundAlertEnabled.value
    }

    fun muteCurrentOrderAlert() {
        _isAlarmMutedForCurrentBatch.value = true
    }

    fun unmuteCurrentOrderAlert() {
        _isAlarmMutedForCurrentBatch.value = false
    }

    fun acceptOrder(orderId: Long) {
        viewModelScope.launch {
            repository.updateOrderStatus(orderId, OrderStatus.CONFIRMED)
        }
    }

    /**
     * Simulates an incoming customer order for testing kitchen alert chime and notification banner.
     */
    fun simulateIncomingOrderForTesting() {
        viewModelScope.launch {
            val dummyItems = listOf(
                com.example.data.model.CartItem(
                    foodItemId = "biryani-1",
                    name = "Chicken Biryani Special",
                    category = "Biryani",
                    basePrice = 280.0,
                    portion = "Full Handi",
                    spiceLevel = "Medium Spicy",
                    finalUnitPrice = 280.0,
                    quantity = 2,
                    isVeg = false
                ),
                com.example.data.model.CartItem(
                    foodItemId = "curry-1",
                    name = "Chicken Butter Masala",
                    category = "Curry",
                    basePrice = 340.0,
                    portion = "Standard",
                    spiceLevel = "Medium Spicy",
                    finalUnitPrice = 340.0,
                    quantity = 1,
                    isVeg = false
                )
            )
            repository.placeOrder(
                items = dummyItems,
                subtotal = 900.0,
                deliveryFee = 40.0,
                tax = 45.0,
                packagingFee = 25.0,
                discount = 100.0,
                total = 910.0,
                appliedCoupon = "ZAYKA100",
                deliveryType = "DELIVERY",
                address = "Flat 502, Sky Tower, Sector 18, Noida",
                instruction = "Deliver hot with extra mint chutney",
                paymentMethod = "Cash on Delivery"
            )
            _isAlarmMutedForCurrentBatch.value = false
        }
    }

    val savedAddresses: StateFlow<List<SavedAddress>> = repository.allAddresses
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Filters & Search
    private val _selectedCategory = MutableStateFlow("All")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _isVegOnly = MutableStateFlow(false)
    val isVegOnly: StateFlow<Boolean> = _isVegOnly.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // Filtered menu list
    val filteredMenuItems: StateFlow<List<FoodItem>> = combine(
        allFoodItems,
        _selectedCategory,
        _isVegOnly,
        _searchQuery
    ) { items, cat, vegOnly, query ->
        items.filter { item ->
            val matchesCat = (cat == "All") || (item.category.contains(cat, ignoreCase = true))
            val matchesVeg = !vegOnly || item.isVeg
            val matchesQuery = query.isBlank() ||
                    item.name.contains(query, ignoreCase = true) ||
                    item.description.contains(query, ignoreCase = true) ||
                    item.category.contains(query, ignoreCase = true)
            matchesCat && matchesVeg && matchesQuery
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Checkout & Cart state
    private val _appliedCoupon = MutableStateFlow<Coupon?>(null)
    val appliedCoupon: StateFlow<Coupon?> = _appliedCoupon.asStateFlow()

    private val _deliveryType = MutableStateFlow("DELIVERY") // or "TAKEAWAY"
    val deliveryType: StateFlow<String> = _deliveryType.asStateFlow()

    private val _deliveryInstruction = MutableStateFlow("")
    val deliveryInstruction: StateFlow<String> = _deliveryInstruction.asStateFlow()

    private val _selectedPaymentMethod = MutableStateFlow("UPI")
    val selectedPaymentMethod: StateFlow<String> = _selectedPaymentMethod.asStateFlow()

    private val _selectedAddress = MutableStateFlow("Flat 402, Royal Palms Apartments, Sector 18, Noida")
    val selectedAddress: StateFlow<String> = _selectedAddress.asStateFlow()

    // Active Tracking Order ID
    private val _activeTrackingOrderId = MutableStateFlow<Long?>(null)
    val activeTrackingOrderId: StateFlow<Long?> = _activeTrackingOrderId.asStateFlow()

    // Store status (Open / Closed toggle for Admin)
    private val _isStoreOpen = MutableStateFlow(true)
    val isStoreOpen: StateFlow<Boolean> = _isStoreOpen.asStateFlow()

    // Theme Mode: null = system default, true = dark mode, false = light mode
    private val _isDarkTheme = MutableStateFlow<Boolean?>(null)
    val isDarkTheme: StateFlow<Boolean?> = _isDarkTheme.asStateFlow()

    fun setThemeMode(isDark: Boolean?) {
        _isDarkTheme.value = isDark
    }

    // Deals and Coupons flows
    val allDeals: StateFlow<List<Coupon>> = repository.allDeals
        .map { list -> list.map { it.toCoupon() } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeDeals: StateFlow<List<Coupon>> = repository.activeDeals
        .map { list -> list.map { it.toCoupon() } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Restaurant Help & Contact Information flow
    val restaurantContactInfo: StateFlow<RestaurantContactEntity> = repository.contactInfo
        .map { it ?: RestaurantContactEntity() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), RestaurantContactEntity())

    fun updateRestaurantContactInfo(info: RestaurantContactEntity) {
        viewModelScope.launch {
            repository.updateContactInfo(info)
        }
    }

    // Admin Deals & Discounts Management
    fun saveDeal(coupon: Coupon) {
        viewModelScope.launch {
            repository.saveDeal(coupon.toDealEntity())
        }
    }

    fun toggleDealStatus(code: String, currentStatus: Boolean) {
        viewModelScope.launch {
            repository.updateDealStatus(code, !currentStatus)
        }
    }

    fun deleteDeal(code: String) {
        viewModelScope.launch {
            repository.deleteDeal(code)
            if (_appliedCoupon.value?.code == code) {
                _appliedCoupon.value = null
            }
        }
    }

    fun selectCategory(category: String) {
        _selectedCategory.value = category
    }

    fun toggleVegOnly() {
        _isVegOnly.value = !_isVegOnly.value
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setDeliveryType(type: String) {
        _deliveryType.value = type
    }

    fun setDeliveryInstruction(instruction: String) {
        _deliveryInstruction.value = instruction
    }

    fun setPaymentMethod(method: String) {
        _selectedPaymentMethod.value = method
    }

    fun setSelectedAddress(address: String) {
        _selectedAddress.value = address
    }

    fun setActiveTrackingOrderId(orderId: Long?) {
        _activeTrackingOrderId.value = orderId
    }

    fun toggleStoreStatus() {
        _isStoreOpen.value = !_isStoreOpen.value
    }

    // Cart Operations
    fun addToCart(
        foodItem: FoodItem,
        portion: String = "Standard",
        spiceLevel: String = "Medium Spicy",
        addonsText: String = "",
        unitPrice: Double = foodItem.price
    ) {
        viewModelScope.launch {
            repository.addToCart(foodItem, portion, spiceLevel, addonsText, unitPrice)
        }
    }

    fun updateCartQuantity(cartId: Long, newQty: Int) {
        viewModelScope.launch {
            repository.updateCartItemQuantity(cartId, newQty)
        }
    }

    fun removeCartItem(cartId: Long) {
        viewModelScope.launch {
            repository.removeCartItem(cartId)
        }
    }

    fun clearCart() {
        viewModelScope.launch {
            repository.clearCart()
        }
    }

    // Coupon logic
    fun applyCoupon(coupon: Coupon): Boolean {
        val subtotal = cartItems.value.sumOf { it.totalCost }
        if (subtotal >= coupon.minOrder) {
            _appliedCoupon.value = coupon
            return true
        }
        return false
    }

    fun removeCoupon() {
        _appliedCoupon.value = null
    }

    // Price Calculations
    fun calculateSubtotal(items: List<CartItem>): Double = items.sumOf { it.totalCost }

    fun calculateDeliveryFee(items: List<CartItem>, delType: String): Double {
        if (delType == "TAKEAWAY") return 0.0
        val coupon = _appliedCoupon.value
        if (coupon != null && coupon.isFreeDelivery) return 0.0
        val subtotal = calculateSubtotal(items)
        if (subtotal >= 100.0) return 0.0
        return restaurantContactInfo.value.deliveryFeeAmount
    }

    fun calculatePackagingFee(delType: String): Double {
        return 0.0
    }

    fun calculateTax(subtotal: Double): Double = 0.0

    fun calculateDiscount(subtotal: Double): Double {
        val coupon = _appliedCoupon.value ?: return 0.0
        return when {
            coupon.flatDiscount > 0 -> coupon.flatDiscount
            coupon.discountPercent > 0 -> {
                val disc = subtotal * (coupon.discountPercent / 100.0)
                if (coupon.maxDiscount > 0) minOf(disc, coupon.maxDiscount) else disc
            }
            else -> 0.0
        }
    }

    fun calculateGrandTotal(items: List<CartItem>, delType: String): Double {
        val subtotal = calculateSubtotal(items)
        val deliveryFee = calculateDeliveryFee(items, delType)
        val packaging = calculatePackagingFee(delType)
        val tax = calculateTax(subtotal)
        val discount = calculateDiscount(subtotal)
        return (subtotal + deliveryFee + packaging + tax - discount).coerceAtLeast(0.0)
    }

    fun placeOrder(onSuccess: (Long) -> Unit, onError: ((String) -> Unit)? = null) {
        viewModelScope.launch {
            val user = currentUser.value
            val items = cartItems.value
            if (items.isEmpty()) return@launch

            val delType = _deliveryType.value
            val subtotal = calculateSubtotal(items)
            val deliveryFee = calculateDeliveryFee(items, delType)
            val packaging = calculatePackagingFee(delType)
            val tax = calculateTax(subtotal)
            val discount = calculateDiscount(subtotal)
            val total = calculateGrandTotal(items, delType)
            val couponCode = _appliedCoupon.value?.code ?: ""

            val newOrderId = repository.placeOrder(
                items = items,
                subtotal = subtotal,
                deliveryFee = deliveryFee,
                tax = tax,
                packagingFee = packaging,
                discount = discount,
                total = total,
                appliedCoupon = couponCode,
                deliveryType = delType,
                address = if (delType == "TAKEAWAY") "Zayka Chicken Cafe, Sector 18 (Self Pickup)" else _selectedAddress.value,
                instruction = _deliveryInstruction.value,
                paymentMethod = _selectedPaymentMethod.value
            )

            _appliedCoupon.value = null
            _activeTrackingOrderId.value = newOrderId
            _isAlarmMutedForCurrentBatch.value = false
            onSuccess(newOrderId)
        }
    }

    fun reorder(order: OrderEntity, onReordered: () -> Unit) {
        viewModelScope.launch {
            val allItems = allFoodItems.value
            val firstItem = allItems.firstOrNull() ?: return@launch
            repository.addToCart(
                foodItem = firstItem,
                portion = "Standard",
                spiceLevel = "Medium Spicy",
                addonsText = "Reordered from ${order.orderNumber}",
                unitPrice = firstItem.price
            )
            onReordered()
        }
    }

    // Admin Controls
    fun toggleItemAvailability(item: FoodItem) {
        viewModelScope.launch {
            repository.updateItemAvailability(item.id, !item.isAvailable)
        }
    }

    fun updateFoodPrice(id: String, newPrice: Double) {
        viewModelScope.launch {
            repository.updateItemPrice(id, newPrice)
        }
    }

    fun saveFoodItem(item: FoodItem) {
        viewModelScope.launch {
            repository.saveFoodItem(item)
        }
    }

    fun deleteFoodItem(id: String) {
        viewModelScope.launch {
            repository.deleteFoodItem(id)
        }
    }

    fun advanceOrderStatus(orderId: Long, currentStatus: String) {
        viewModelScope.launch {
            val nextStatus = when (currentStatus) {
                OrderStatus.PLACED -> OrderStatus.CONFIRMED
                OrderStatus.CONFIRMED -> OrderStatus.PREPARING
                OrderStatus.PREPARING -> OrderStatus.OUT_FOR_DELIVERY
                OrderStatus.OUT_FOR_DELIVERY -> OrderStatus.DELIVERED
                else -> OrderStatus.DELIVERED
            }
            repository.updateOrderStatus(orderId, nextStatus)
        }
    }

    fun cancelOrder(orderId: Long) {
        viewModelScope.launch {
            repository.updateOrderStatus(orderId, OrderStatus.CANCELLED)
        }
    }

    fun submitRating(orderId: Long, rating: Int, feedback: String) {
        viewModelScope.launch {
            repository.submitRating(orderId, rating, feedback)
        }
    }

    fun addAddress(label: String, address: String, landmark: String) {
        viewModelScope.launch {
            repository.addAddress(label, address, landmark)
        }
    }

    fun setDefaultAddress(id: Long, addressText: String) {
        viewModelScope.launch {
            repository.setDefaultAddress(id)
            _selectedAddress.value = addressText
        }
    }

    // Email & Password Authentication using local Room Database
    fun signInWithEmailPassword(
        email: String,
        pass: String,
        onSuccess: () -> Unit = {}
    ) {
        viewModelScope.launch {
            _authLoading.value = true
            _authError.value = null

            val trimmedEmail = email.trim()
            val trimmedPass = pass.trim()

            if (trimmedEmail.isBlank() || trimmedPass.isBlank()) {
                _authLoading.value = false
                _authError.value = "Please enter both email and password."
                return@launch
            }

            val isAdminChicken = trimmedEmail.equals("zaykachicken@gmail.com", ignoreCase = true) && trimmedPass == "zayka1236"
            val isDefaultAdmin = trimmedEmail.equals("restaurant@zayka.com", ignoreCase = true) && trimmedPass == "admin123"

            if (isAdminChicken || isDefaultAdmin) {
                val adminEmail = if (isAdminChicken) "zaykachicken@gmail.com" else "restaurant@zayka.com"
                val adminName = if (isAdminChicken) "Zayka Chicken Admin" else "Zayka Restaurant Admin"
                val restaurantUser = com.example.data.model.UserEntity(
                    email = adminEmail,
                    displayName = adminName,
                    passwordHash = trimmedPass,
                    phone = "+91 98765 12345",
                    role = "RESTAURANT_ADMIN"
                )
                repository.registerUser(restaurantUser)
                authManager.setUserAccount(
                    UserAccount(
                        uid = "restaurant_$adminEmail",
                        displayName = adminName,
                        email = adminEmail,
                        photoUrl = null,
                        isAnonymous = false,
                        authProvider = "RestaurantEmail",
                        role = "RESTAURANT_ADMIN"
                    )
                )
                _authError.value = null
                _authLoading.value = false
                onSuccess()
                return@launch
            }

            val user = repository.getUserByEmail(trimmedEmail)
            _authLoading.value = false

            if (user != null && user.passwordHash == trimmedPass) {
                authManager.setUserAccount(
                    UserAccount(
                        uid = "db_${user.email}",
                        displayName = user.displayName,
                        email = user.email,
                        photoUrl = null,
                        isAnonymous = false,
                        authProvider = "Email",
                        role = user.role
                    )
                )
                _authError.value = null
                onSuccess()
            } else if (user != null) {
                _authError.value = "Incorrect password. Please try again."
            } else {
                _authError.value = "No account found with this email. You can sign up below."
            }
        }
    }

    fun signUpWithEmailPassword(
        name: String,
        email: String,
        pass: String,
        phone: String = "",
        role: String = "CUSTOMER",
        onSuccess: () -> Unit = {}
    ) {
        viewModelScope.launch {
            _authLoading.value = true
            _authError.value = null

            val trimmedEmail = email.trim()
            val trimmedPass = pass.trim()
            val trimmedName = name.trim().ifBlank { trimmedEmail.substringBefore("@") }

            if (trimmedEmail.isBlank() || !trimmedEmail.contains("@")) {
                _authLoading.value = false
                _authError.value = "Please enter a valid email address."
                return@launch
            }
            if (trimmedPass.length < 4) {
                _authLoading.value = false
                _authError.value = "Password must be at least 4 characters long."
                return@launch
            }

            val existing = repository.getUserByEmail(trimmedEmail)
            if (existing != null) {
                _authLoading.value = false
                _authError.value = "An account with this email already exists. Please log in."
                return@launch
            }

            val newUser = com.example.data.model.UserEntity(
                email = trimmedEmail,
                displayName = trimmedName,
                passwordHash = trimmedPass,
                phone = phone.trim(),
                role = role
            )
            repository.registerUser(newUser)
            _authLoading.value = false

            authManager.setUserAccount(
                UserAccount(
                    uid = "db_${newUser.email}",
                    displayName = newUser.displayName,
                    email = newUser.email,
                    photoUrl = null,
                    isAnonymous = false,
                    authProvider = "Email",
                    role = newUser.role
                )
            )
            _authError.value = null
            onSuccess()
        }
    }

    fun signInWithPhoneNumber(
        phoneNumber: String,
        otpCode: String,
        onSuccess: () -> Unit = {}
    ) {
        viewModelScope.launch {
            _authLoading.value = true
            _authError.value = null
            val result = authManager.signInWithPhoneNumber(phoneNumber, otpCode)
            _authLoading.value = false
            result.onSuccess {
                _authError.value = null
                onSuccess()
            }.onFailure { ex ->
                _authError.value = ex.message ?: "Phone number verification failed."
            }
        }
    }

    // Direct Restaurant Email Authentication: Restaurant has dedicated access
    fun signInAsRestaurant(
        email: String,
        pass: String,
        onSuccess: () -> Unit = {}
    ) {
        viewModelScope.launch {
            _authLoading.value = true
            _authError.value = null

            val trimmedEmail = email.trim()
            val trimmedPass = pass.trim()

            if (trimmedEmail.isBlank() || trimmedPass.isBlank()) {
                _authLoading.value = false
                _authError.value = "Please enter restaurant staff email and password."
                return@launch
            }

            val user = repository.getUserByEmail(trimmedEmail)
            _authLoading.value = false

            if (user != null && user.passwordHash == trimmedPass && user.role == "RESTAURANT_ADMIN") {
                authManager.setUserAccount(
                    UserAccount(
                        uid = "restaurant_${user.email}",
                        displayName = user.displayName,
                        email = user.email,
                        photoUrl = null,
                        isAnonymous = false,
                        authProvider = "RestaurantEmail",
                        role = "RESTAURANT_ADMIN"
                    )
                )
                _authError.value = null
                onSuccess()
            } else if (user != null && user.role != "RESTAURANT_ADMIN") {
                _authError.value = "This email is registered as a customer, not restaurant staff."
            } else if (user != null) {
                _authError.value = "Invalid restaurant credentials. Please verify your password."
            } else {
                // If it's the official restaurant default credentials, allow creation or access
                val isAdminChicken = trimmedEmail.equals("zaykachicken@gmail.com", ignoreCase = true) && trimmedPass == "zayka1236"
                val isDefaultAdmin = trimmedEmail.equals("restaurant@zayka.com", ignoreCase = true) && trimmedPass == "admin123"

                if (isAdminChicken || isDefaultAdmin) {
                    val adminEmail = if (isAdminChicken) "zaykachicken@gmail.com" else "restaurant@zayka.com"
                    val adminName = if (isAdminChicken) "Zayka Chicken Admin" else "Zayka Restaurant Admin"
                    val restaurantUser = com.example.data.model.UserEntity(
                        email = adminEmail,
                        displayName = adminName,
                        passwordHash = trimmedPass,
                        phone = "+91 98765 12345",
                        role = "RESTAURANT_ADMIN"
                    )
                    repository.registerUser(restaurantUser)
                    authManager.setUserAccount(
                        UserAccount(
                            uid = "restaurant_$adminEmail",
                            displayName = adminName,
                            email = adminEmail,
                            photoUrl = null,
                            isAnonymous = false,
                            authProvider = "RestaurantEmail",
                            role = "RESTAURANT_ADMIN"
                        )
                    )
                    _authError.value = null
                    onSuccess()
                } else {
                    _authError.value = "No restaurant account found with this email."
                }
            }
        }
    }

    // Authentication functions
    fun signInWithGoogle(activity: Activity, onSuccess: () -> Unit = {}, onFailure: (String) -> Unit = {}) {
        viewModelScope.launch {
            _authLoading.value = true
            _authError.value = null
            val result = authManager.signInWithGoogle(activity)
            _authLoading.value = false
            result.onSuccess {
                _authError.value = null
                onSuccess()
            }.onFailure { ex ->
                val msg = ex.message ?: "Google Sign-In failed"
                _authError.value = msg
                onFailure(msg)
            }
        }
    }

    fun signUpWithEmail(email: String, password: String, name: String, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            _authLoading.value = true
            _authError.value = null
            val result = authManager.signUpWithEmail(email, password, name)
            _authLoading.value = false
            result.onSuccess {
                _authError.value = null
                onSuccess()
            }.onFailure { ex ->
                _authError.value = ex.message ?: "Sign up failed"
            }
        }
    }

    fun signInWithEmail(email: String, password: String, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            _authLoading.value = true
            _authError.value = null
            val result = authManager.signInWithEmail(email, password)
            _authLoading.value = false
            result.onSuccess {
                _authError.value = null
                onSuccess()
            }.onFailure { ex ->
                _authError.value = ex.message ?: "Sign in failed"
            }
        }
    }

    fun setAuthError(error: String) {
        _authError.value = error
    }

    fun updateCustomerProfile(name: String, contact: String) {
        authManager.updateProfile(name, contact)
    }

    fun continueAsGuest(onSuccess: () -> Unit = {}) {
        authManager.continueAsGuest()
        onSuccess()
    }

    fun signOut(activityContext: Context, onSignedOut: () -> Unit = {}) {
        viewModelScope.launch {
            authManager.signOut(activityContext)
            onSignedOut()
        }
    }

    fun clearAuthError() {
        _authError.value = null
    }
}
