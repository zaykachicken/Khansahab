package com.example.data.repository

import android.content.Context
import com.example.data.local.ZaykaDatabase
import com.example.data.model.CartItem
import com.example.data.model.FoodItem
import com.example.data.model.OrderEntity
import com.example.data.model.OrderStatus
import com.example.data.model.SavedAddress
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random

class ZaykaRepository(context: Context) {
    private val database = ZaykaDatabase.getDatabase(context)
    private val menuDao = database.menuDao()
    private val cartDao = database.cartDao()
    private val orderDao = database.orderDao()
    private val addressDao = database.addressDao()
    private val userDao = database.userDao()
    private val dealDao = database.dealDao()
    private val contactDao = database.contactDao()

    val syncManager by lazy { FirebaseSyncManager(this) }

    init {
        CoroutineScope(Dispatchers.IO).launch {
            seedInitialDataIfEmpty()
            try {
                syncManager.startListeningForOrders()
            } catch (e: Exception) {
                // Graceful fallback if Firebase is not yet configured or offline
            }
        }
    }

    // Restaurant Help & Contact info queries and updates
    val contactInfo: Flow<com.example.data.model.RestaurantContactEntity?> = contactDao.getContactInfo()

    suspend fun updateContactInfo(info: com.example.data.model.RestaurantContactEntity) {
        contactDao.insertOrUpdateContactInfo(info)
    }

    // Deals & Discounts queries and updates
    val allDeals: Flow<List<com.example.data.model.DealEntity>> = dealDao.getAllDeals()
    val activeDeals: Flow<List<com.example.data.model.DealEntity>> = dealDao.getActiveDeals()

    suspend fun saveDeal(deal: com.example.data.model.DealEntity) {
        dealDao.insertDeal(deal)
    }

    suspend fun updateDealStatus(code: String, isActive: Boolean) {
        dealDao.updateDealStatus(code, isActive)
    }

    suspend fun deleteDeal(code: String) {
        dealDao.deleteDeal(code)
    }

    // User authentication database operations
    suspend fun getUserByEmail(email: String) = userDao.getUserByEmail(email)

    suspend fun registerUser(user: com.example.data.model.UserEntity) {
        userDao.insertUser(user)
    }

    suspend fun updateUser(user: com.example.data.model.UserEntity) {
        userDao.updateUser(user)
    }

    // Menu queries
    val allFoodItems: Flow<List<FoodItem>> = menuDao.getAllFoodItems()

    suspend fun getFoodItemById(id: String): FoodItem? = menuDao.getFoodItemById(id)

    suspend fun saveFoodItem(item: FoodItem) {
        menuDao.insertItem(item)
        syncManager.syncFoodItemToCloud(item)
    }

    suspend fun updateItemAvailability(id: String, isAvailable: Boolean) {
        menuDao.updateAvailability(id, isAvailable)
        val item = getFoodItemById(id)
        if (item != null) {
            syncManager.syncFoodItemToCloud(item.copy(isAvailable = isAvailable))
        }
    }

    suspend fun updateItemPrice(id: String, newPrice: Double) {
        menuDao.updatePrice(id, newPrice)
        val item = getFoodItemById(id)
        if (item != null) {
            syncManager.syncFoodItemToCloud(item.copy(price = newPrice))
        }
    }

    suspend fun deleteFoodItem(id: String) {
        menuDao.deleteFoodItem(id)
    }

    // Cart operations
    val cartItems: Flow<List<CartItem>> = cartDao.getCartItems()

    suspend fun addToCart(
        foodItem: FoodItem,
        portion: String = "Standard",
        spiceLevel: String = "Medium Spicy",
        addonsText: String = "",
        unitPrice: Double = foodItem.price
    ) {
        val existing = cartDao.getCartItemsDirect().find {
            it.foodItemId == foodItem.id && it.portion == portion && it.spiceLevel == spiceLevel && it.addonsText == addonsText
        }
        if (existing != null) {
            cartDao.updateQuantity(existing.cartId, existing.quantity + 1)
        } else {
            cartDao.insertCartItem(
                CartItem(
                    foodItemId = foodItem.id,
                    name = foodItem.name,
                    category = foodItem.category,
                    basePrice = foodItem.price,
                    portion = portion,
                    spiceLevel = spiceLevel,
                    addonsText = addonsText,
                    finalUnitPrice = unitPrice,
                    quantity = 1,
                    isVeg = foodItem.isVeg,
                    imageDrawableName = foodItem.imageDrawableName
                )
            )
        }
    }

    suspend fun updateCartItemQuantity(cartId: Long, newQuantity: Int) {
        if (newQuantity <= 0) {
            cartDao.deleteCartItem(cartId)
        } else {
            cartDao.updateQuantity(cartId, newQuantity)
        }
    }

    suspend fun removeCartItem(cartId: Long) {
        cartDao.deleteCartItem(cartId)
    }

    suspend fun clearCart() {
        cartDao.clearCart()
    }

    // Orders
    val allOrders: Flow<List<OrderEntity>> = orderDao.getAllOrders()
    val activeOrders: Flow<List<OrderEntity>> = orderDao.getActiveOrders()

    fun getOrderById(orderId: Long): Flow<OrderEntity?> = orderDao.getOrderById(orderId)

    suspend fun placeOrder(
        items: List<CartItem>,
        subtotal: Double,
        deliveryFee: Double,
        tax: Double,
        packagingFee: Double,
        discount: Double,
        total: Double,
        appliedCoupon: String,
        deliveryType: String,
        address: String,
        instruction: String,
        paymentMethod: String
    ): Long {
        val orderNum = "ZYK-" + (1000 + Random.nextInt(9000))
        val summaryList = items.map { "${it.quantity}x ${it.name} (${it.portion})" }
        val itemsSummary = summaryList.joinToString(", ")

        val order = OrderEntity(
            orderNumber = orderNum,
            itemsSummaryJson = itemsSummary,
            subtotal = subtotal,
            deliveryFee = deliveryFee,
            tax = tax,
            packagingFee = packagingFee,
            discount = discount,
            total = total,
            appliedCoupon = appliedCoupon,
            deliveryType = deliveryType,
            deliveryAddress = address,
            deliveryInstruction = instruction,
            paymentMethod = paymentMethod,
            status = OrderStatus.PLACED,
            orderTimestamp = System.currentTimeMillis(),
            etaMinutes = if (deliveryType == "TAKEAWAY") 15 else 30,
            riderName = "Ramesh Kumar",
            riderPhone = "+91 98765 43210",
            riderRating = 4.9f
        )

        val newOrderId = orderDao.insertOrder(order)
        cartDao.clearCart()
        val placedOrderWithId = order.copy(orderId = newOrderId)
        syncManager.syncOrderToCloud(newOrderId, placedOrderWithId)
        return newOrderId
    }

    suspend fun updateOrderStatus(orderId: Long, status: String) {
        orderDao.updateOrderStatus(orderId, status)
        syncManager.updateOrderStatusInCloud(orderId, status)
    }

    suspend fun getOrderDirect(orderId: Long): OrderEntity? = orderDao.getOrderByIdDirect(orderId)

    suspend fun insertOrderDirect(order: OrderEntity): Long = orderDao.insertOrder(order)

    suspend fun updateOrderStatusLocally(orderId: Long, status: String) {
        orderDao.updateOrderStatus(orderId, status)
    }

    suspend fun submitRating(orderId: Long, rating: Int, feedback: String) {
        orderDao.updateRating(orderId, rating, feedback)
    }

    // Addresses
    val allAddresses: Flow<List<SavedAddress>> = addressDao.getAllAddresses()

    suspend fun addAddress(label: String, fullAddress: String, landmark: String) {
        addressDao.insertAddress(
            SavedAddress(
                label = label,
                fullAddress = fullAddress,
                landmark = landmark,
                isDefault = false
            )
        )
    }

    suspend fun setDefaultAddress(id: Long) {
        addressDao.clearDefault()
        addressDao.setDefault(id)
    }

    suspend fun deleteAddress(id: Long) {
        addressDao.deleteAddress(id)
    }

    private suspend fun seedInitialDataIfEmpty() {
        if (menuDao.getItemCount() == 0) {
            val initialMenu = listOf(
                FoodItem(
                    id = "zayka_biryani_01",
                    name = "Special Chicken Dum Biryani",
                    category = "Biryani",
                    description = "Layered fragrant basmati rice slow-cooked with tender spiced chicken pieces, royal saffron, caramelized onions, and fresh mint. Served with cooling raita & salan.",
                    price = 280.0,
                    originalPrice = 340.0,
                    isVeg = false,
                    rating = 4.8f,
                    ratingCount = 1840,
                    isBestseller = true,
                    imageDrawableName = "img_hero_biryani",
                    portionsJson = "[\"Half (1-2 Pcs)\", \"Full (3-4 Pcs)\", \"Family Pack\"]",
                    spicesJson = "[\"Mild\", \"Medium Spicy\", \"Extra Spicy (Andhra Style)\"]",
                    addonsJson = "Extra Raita (+₹30), Extra Boiled Egg (+₹20), Mirchi Ka Salan (+₹40)"
                ),
                FoodItem(
                    id = "zayka_tikka_02",
                    name = "Tandoori Chicken Tikka Platter",
                    category = "Starters & Kebabs",
                    description = "Succulent boneless chicken chunks marinated in hung curd, Kashmiri deghi mirch, mustard oil and authentic clay oven spices, grilled to smoky perfection.",
                    price = 320.0,
                    originalPrice = 380.0,
                    isVeg = false,
                    rating = 4.9f,
                    ratingCount = 1420,
                    isBestseller = true,
                    imageDrawableName = "img_tandoori_platter",
                    portionsJson = "[\"6 Pieces\", \"10 Pieces (Platter)\"]",
                    spicesJson = "[\"Medium Spicy\", \"Fiery Hot\"]",
                    addonsJson = "Mint Coriander Dip (+₹25), Rumali Roti (+₹20), Lachha Onion (+₹15)"
                ),
                FoodItem(
                    id = "zayka_butter_03",
                    name = "Butter Chicken Handi (Boneless)",
                    category = "Curries & Gravy",
                    description = "Tandoor roasted chicken tikka simmered in a velvety smooth tomato makhani gravy enriched with fresh churned butter, cashew cream, and fragrant kasuri methi.",
                    price = 340.0,
                    originalPrice = 399.0,
                    isVeg = false,
                    rating = 4.9f,
                    ratingCount = 2100,
                    isBestseller = true,
                    imageDrawableName = "img_butter_chicken",
                    portionsJson = "[\"Regular (Serves 2)\", \"Large (Serves 3-4)\"]",
                    spicesJson = "[\"Mild Creamy\", \"Medium Spicy\"]",
                    addonsJson = "Garlic Naan (1 Pc) (+₹45), Extra Butter Cube (+₹20)"
                ),
                FoodItem(
                    id = "zayka_kebab_04",
                    name = "Murgh Malai Kebab",
                    category = "Starters & Kebabs",
                    description = "Melt-in-mouth chicken cubes steeped in cardamom, cheese, cashew paste and fresh cream, charbroiled over charcoal.",
                    price = 310.0,
                    originalPrice = 360.0,
                    isVeg = false,
                    rating = 4.7f,
                    ratingCount = 890,
                    isBestseller = false,
                    imageDrawableName = "img_tandoori_platter",
                    portionsJson = "[\"6 Pieces\", \"8 Pieces\"]",
                    spicesJson = "[\"Mild & Creamy\"]",
                    addonsJson = "Green Chutney (+₹20), Rumali Roti (+₹20)"
                ),
                FoodItem(
                    id = "zayka_kadahi_05",
                    name = "Chicken Kadhai Lahori",
                    category = "Curries & Gravy",
                    description = "Wok tossed bone-in chicken with freshly pounded coriander seeds, dry red chillies, crunchy bell peppers, and diced onions in thick onion-tomato gravy.",
                    price = 310.0,
                    originalPrice = 350.0,
                    isVeg = false,
                    rating = 4.6f,
                    ratingCount = 650,
                    isBestseller = false,
                    imageDrawableName = "img_butter_chicken",
                    portionsJson = "[\"Half\", \"Full\"]",
                    spicesJson = "[\"Medium Spicy\", \"Spicy Desi\"]",
                    addonsJson = "Tandoori Roti (2 Pcs) (+₹30)"
                ),
                FoodItem(
                    id = "zayka_paneer_06",
                    name = "Paneer Tikka Butter Masala",
                    category = "Curries & Gravy",
                    description = "Charcoal grilled fresh cottage cheese cubes cooked in a rich, buttery tomato and almond gravy. 100% Pure Vegetarian delicacy.",
                    price = 270.0,
                    originalPrice = 320.0,
                    isVeg = true,
                    rating = 4.7f,
                    ratingCount = 980,
                    isBestseller = true,
                    imageDrawableName = "img_butter_chicken",
                    portionsJson = "[\"Regular (Serves 2)\", \"Large\"]",
                    spicesJson = "[\"Mild\", \"Medium Spicy\"]",
                    addonsJson = "Butter Kulcha (+₹45), Jeera Rice (+₹90)"
                ),
                FoodItem(
                    id = "zayka_dal_07",
                    name = "Dal Makhani Amritsari",
                    category = "Curries & Gravy",
                    description = "Whole black lentils and kidney beans slow-simmered overnight over charcoal embers, finished with white butter and pure dairy cream.",
                    price = 230.0,
                    originalPrice = 280.0,
                    isVeg = true,
                    rating = 4.8f,
                    ratingCount = 1350,
                    isBestseller = false,
                    imageDrawableName = "img_butter_chicken",
                    portionsJson = "[\"Regular (Serves 2)\"]",
                    spicesJson = "[\"Standard Mild\"]",
                    addonsJson = "Garlic Naan (+₹45), Sirka Onion (+₹15)"
                ),
                FoodItem(
                    id = "zayka_lollipop_08",
                    name = "Crispy Chicken Lollipop (6 Pcs)",
                    category = "Starters & Kebabs",
                    description = "Crispy battered chicken winglets tossed in fiery Schezwan-garlic reduction with spring onions and toasted sesame.",
                    price = 260.0,
                    originalPrice = 300.0,
                    isVeg = false,
                    rating = 4.7f,
                    ratingCount = 740,
                    isBestseller = false,
                    imageDrawableName = "img_tandoori_platter",
                    portionsJson = "[\"6 Pcs\", \"10 Pcs\"]",
                    spicesJson = "[\"Spicy\", \"Extra Spicy\"]",
                    addonsJson = "Extra Hot Schezwan Dip (+₹30)"
                ),
                FoodItem(
                    id = "zayka_combo_09",
                    name = "Zayka Royal Feast Thali Combo",
                    category = "Combos & Thalis",
                    description = "Ultimate feast box: 1 Chicken Dum Biryani + 3 Pcs Chicken Tikka + Butter Chicken Curry (150ml) + 1 Garlic Naan + Cooling Boondi Raita + Gulab Jamun.",
                    price = 449.0,
                    originalPrice = 599.0,
                    isVeg = false,
                    rating = 4.9f,
                    ratingCount = 3200,
                    isBestseller = true,
                    imageDrawableName = "img_hero_biryani",
                    portionsJson = "[\"Solo Feast\", \"Duo Mega Feast\"]",
                    spicesJson = "[\"Medium Spicy\"]",
                    addonsJson = "Cold Drink Can (+₹40)"
                ),
                FoodItem(
                    id = "zayka_naan_10",
                    name = "Garlic Butter Naan (2 Pcs)",
                    category = "Breads & Rice",
                    description = "Freshly baked clay oven leavened bread brushed with melted farm butter, minced roasted garlic, and chopped coriander leaves.",
                    price = 90.0,
                    originalPrice = 110.0,
                    isVeg = true,
                    rating = 4.8f,
                    ratingCount = 2200,
                    isBestseller = true,
                    imageDrawableName = "img_tandoori_platter",
                    portionsJson = "[\"2 Pieces\"]",
                    spicesJson = "[\"Standard\"]",
                    addonsJson = "Extra Butter Spread (+₹15)"
                ),
                FoodItem(
                    id = "zayka_rumali_11",
                    name = "Rumali Roti (3 Pcs)",
                    category = "Breads & Rice",
                    description = "Hand-tossed handkerchief-thin flatbread griddled on inverted kadhai, light and soft.",
                    price = 70.0,
                    originalPrice = 90.0,
                    isVeg = true,
                    rating = 4.5f,
                    ratingCount = 890,
                    isBestseller = false,
                    imageDrawableName = "img_tandoori_platter",
                    portionsJson = "[\"3 Pieces\"]",
                    spicesJson = "[\"Standard\"]",
                    addonsJson = ""
                ),
                FoodItem(
                    id = "zayka_shahi_12",
                    name = "Royal Shahi Tukda with Rabdi",
                    category = "Beverages & Desserts",
                    description = "Crispy golden fried bread infused with saffron sugar syrup, blanketed in thick pistachio-almond rabdi and edible silver vark.",
                    price = 140.0,
                    originalPrice = 180.0,
                    isVeg = true,
                    rating = 4.9f,
                    ratingCount = 1150,
                    isBestseller = true,
                    imageDrawableName = "img_butter_chicken",
                    portionsJson = "[\"2 Pieces\", \"4 Pieces (Sharing)\"]",
                    spicesJson = "[\"Sweet Dessert\"]",
                    addonsJson = "Extra Roasted Dry Fruits (+₹30)"
                ),
                FoodItem(
                    id = "zayka_lassi_13",
                    name = "Kesari Mango Lassi (Chilled)",
                    category = "Beverages & Desserts",
                    description = "Creamy thick yogurt shake blended with sweet Alphonso mango pulp, saffron strands, and crushed cardamom.",
                    price = 90.0,
                    originalPrice = 120.0,
                    isVeg = true,
                    rating = 4.7f,
                    ratingCount = 1450,
                    isBestseller = false,
                    imageDrawableName = "img_hero_biryani",
                    portionsJson = "[\"350ml Glass\", \"500ml Bottle\"]",
                    spicesJson = "[\"Chilled\"]",
                    addonsJson = ""
                )
            )
            menuDao.insertAll(initialMenu)
        }

        if (addressDao.getAddressCount() == 0) {
            addressDao.insertAddress(
                SavedAddress(
                    label = "Home",
                    fullAddress = "Flat 402, Royal Palms Apartments, Sector 18, Noida",
                    landmark = "Opposite Metro Gate 2",
                    contactPhone = "+91 98765 43210",
                    isDefault = true
                )
            )
            addressDao.insertAddress(
                SavedAddress(
                    label = "Office",
                    fullAddress = "Tower B, 6th Floor, Cyber City, DLF Phase 2, Gurugram",
                    landmark = "Near Rapid Metro Station",
                    contactPhone = "+91 98765 43210",
                    isDefault = false
                )
            )
        }

        if (userDao.getUserCount() == 0) {
            // Seed default restaurant admin in Room DB
            userDao.insertUser(
                com.example.data.model.UserEntity(
                    email = "restaurant@zayka.com",
                    displayName = "Zayka Restaurant Admin",
                    passwordHash = "admin123",
                    phone = "+91 98765 12345",
                    role = "RESTAURANT_ADMIN"
                )
            )
        }

        // Seed default Deals and Discounts in Room DB
        dealDao.insertAllDeals(
            listOf(
                com.example.data.model.DealEntity(
                    code = "ZAYKA50",
                    title = "50% OFF up to ₹120",
                    description = "Valid on authentic Biryani & Starters above ₹249",
                    minOrder = 249.0,
                    discountPercent = 50.0,
                    maxDiscount = 120.0,
                    flatDiscount = 0.0,
                    isFreeDelivery = false,
                    isActive = true,
                    badgeTag = "POPULAR"
                ),
                com.example.data.model.DealEntity(
                    code = "FEAST100",
                    title = "FLAT ₹100 OFF",
                    description = "Valid on family feast orders above ₹499",
                    minOrder = 499.0,
                    discountPercent = 0.0,
                    maxDiscount = 0.0,
                    flatDiscount = 100.0,
                    isFreeDelivery = false,
                    isActive = true,
                    badgeTag = "HOT DEAL"
                ),
                com.example.data.model.DealEntity(
                    code = "FREEDEL",
                    title = "FREE DELIVERY",
                    description = "Zero delivery charges on all meals above ₹199",
                    minOrder = 199.0,
                    discountPercent = 0.0,
                    maxDiscount = 0.0,
                    flatDiscount = 0.0,
                    isFreeDelivery = true,
                    isActive = true,
                    badgeTag = "SUPER SAVER"
                ),
                com.example.data.model.DealEntity(
                    code = "MUGHLAI75",
                    title = "₹75 OFF on Mughlai",
                    description = "Special dinner discount on Kebabs & Curries above ₹399",
                    minOrder = 399.0,
                    discountPercent = 0.0,
                    maxDiscount = 0.0,
                    flatDiscount = 75.0,
                    isFreeDelivery = false,
                    isActive = true,
                    badgeTag = "LIMITED TIME"
                )
            )
        )

        // Seed default Restaurant Contact & Help Desk Info
        if (contactDao.getContactInfoDirect() == null) {
            contactDao.insertOrUpdateContactInfo(
                com.example.data.model.RestaurantContactEntity(
                    id = 1,
                    restaurantName = "Zayka Chicken Cafe",
                    supportPhone = "+91 98765 12345",
                    whatsappNumber = "+91 98765 12345",
                    supportEmail = "help@zaykacafe.com",
                    operatingHours = "10:00 AM - 11:30 PM (Mon-Sun)",
                    address = "Near Metro Gate 2, Sector 18, Noida, UP - 201301",
                    fssaiNumber = "12722055000492",
                    emergencyManagerContact = "+91 98111 22334",
                    helpDeskDescription = "We are committed to serving authentic chicken delicacies and ensuring prompt doorstep delivery. Reach us anytime for order inquiries, special bulk catering, or delivery status.",
                    faq1Question = "How can I track my live order?",
                    faq1Answer = "Go to the Orders tab and tap 'Track Live Order' to see real-time updates directly from our kitchen.",
                    faq2Question = "What is the average delivery time?",
                    faq2Answer = "Most orders are delivered within 30-40 minutes from our Sector 18 kitchen.",
                    faq3Question = "How do I cancel or modify my order?",
                    faq3Answer = "Call our direct restaurant helpline or message us on WhatsApp immediately before kitchen preparation begins."
                )
            )
        }
    }
}
