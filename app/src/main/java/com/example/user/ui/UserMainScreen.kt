package com.example.user.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.outlined.Fastfood
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.ZaykaViewModel
import com.example.ui.components.FloatingCartBar
import com.example.ui.screens.CartScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LoginScreen
import com.example.ui.screens.OrderHistoryScreen
import com.example.ui.screens.OrderTrackingScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.theme.BorderLight
import com.example.ui.theme.Primary
import com.example.ui.theme.Secondary
import com.example.ui.theme.TextSecondaryLight

sealed class UserDestination(val route: String, val label: String) {
    object Home : UserDestination("home", "Delivery")
    object Orders : UserDestination("orders", "Orders")
    object Profile : UserDestination("profile", "Profile")
    object Cart : UserDestination("cart", "Cart")
    object Tracking : UserDestination("tracking", "Tracking")
    object Login : UserDestination("login", "Login")
}

/**
 * USER APP — Standalone Customer Application UI
 *
 * Dedicated strictly to customer-facing functionality:
 * - Menu browsing, search, categorization & veg/non-veg filtering
 * - Food item customization (portions, spice levels, add-ons)
 * - Cart management, coupon discounts & checkout
 * - Live real-time order tracking & delivery map visual
 * - Past order history & re-ordering
 * - Customer profile, delivery addresses & help/support
 *
 * Completely stripped of all admin-only dashboard views, staff menus,
 * kitchen alerts, price editing, and administrative controls.
 */
@Composable
fun UserMainScreen(
    viewModel: ZaykaViewModel,
    modifier: Modifier = Modifier
) {
    val currentUser by viewModel.currentUser.collectAsState()
    var currentDestination by remember {
        mutableStateOf<UserDestination>(UserDestination.Home)
    }
    var trackingOrderId by remember { mutableStateOf<Long?>(null) }

    val cartItems by viewModel.cartItems.collectAsState()
    val activeOrders by viewModel.activeOrders.collectAsState()
    val activeTrackingId by viewModel.activeTrackingOrderId.collectAsState()

    val totalCartItems = cartItems.sumOf { it.quantity }
    val totalCartPrice = cartItems.sumOf { it.totalCost }

    val showBottomBar = currentDestination != UserDestination.Cart &&
            currentDestination != UserDestination.Tracking &&
            currentDestination != UserDestination.Login

    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            if (showBottomBar) {
                Surface(
                    color = Color.White,
                    tonalElevation = 8.dp,
                    shadowElevation = 8.dp,
                    shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight),
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .testTag("user_bottom_navigation_bar")
                ) {
                    NavigationBar(
                        containerColor = Color.Transparent,
                        tonalElevation = 0.dp,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // 1. Delivery / Home Menu
                        NavigationBarItem(
                            selected = currentDestination == UserDestination.Home,
                            onClick = { currentDestination = UserDestination.Home },
                            icon = {
                                Icon(
                                    imageVector = if (currentDestination == UserDestination.Home) Icons.Filled.Fastfood else Icons.Outlined.Fastfood,
                                    contentDescription = "Delivery"
                                )
                            },
                            label = { Text("Delivery", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Color.White,
                                selectedTextColor = Primary,
                                indicatorColor = Primary,
                                unselectedIconColor = TextSecondaryLight,
                                unselectedTextColor = TextSecondaryLight
                            ),
                            modifier = Modifier.testTag("user_nav_item_delivery")
                        )

                        // 2. Customer Orders
                        NavigationBarItem(
                            selected = currentDestination == UserDestination.Orders,
                            onClick = { currentDestination = UserDestination.Orders },
                            icon = {
                                BadgedBox(
                                    badge = {
                                        if (activeOrders.isNotEmpty()) {
                                            Badge(containerColor = Secondary) {
                                                Text("${activeOrders.size}", color = Color.White)
                                            }
                                        }
                                    }
                                ) {
                                    Icon(
                                        imageVector = if (currentDestination == UserDestination.Orders) Icons.Filled.ReceiptLong else Icons.Outlined.ReceiptLong,
                                        contentDescription = "My Orders"
                                    )
                                }
                            },
                            label = { Text("My Orders", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Color.White,
                                selectedTextColor = Primary,
                                indicatorColor = Primary,
                                unselectedIconColor = TextSecondaryLight,
                                unselectedTextColor = TextSecondaryLight
                            ),
                            modifier = Modifier.testTag("user_nav_item_orders")
                        )

                        // 3. Customer Profile
                        NavigationBarItem(
                            selected = currentDestination == UserDestination.Profile,
                            onClick = { currentDestination = UserDestination.Profile },
                            icon = {
                                Icon(
                                    imageVector = if (currentDestination == UserDestination.Profile) Icons.Filled.Person else Icons.Outlined.Person,
                                    contentDescription = "Profile"
                                )
                            },
                            label = { Text("Profile", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Color.White,
                                selectedTextColor = Primary,
                                indicatorColor = Primary,
                                unselectedIconColor = TextSecondaryLight,
                                unselectedTextColor = TextSecondaryLight
                            ),
                            modifier = Modifier.testTag("user_nav_item_profile")
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentDestination) {
                UserDestination.Home -> {
                    HomeScreen(
                        viewModel = viewModel,
                        onNavigateToCart = { currentDestination = UserDestination.Cart },
                        onNavigateToProfile = { currentDestination = UserDestination.Profile }
                    )
                }
                UserDestination.Orders -> {
                    OrderHistoryScreen(
                        viewModel = viewModel,
                        onTrackOrder = { orderId ->
                            trackingOrderId = orderId
                            currentDestination = UserDestination.Tracking
                        },
                        onReorderClicked = {
                            currentDestination = UserDestination.Cart
                        }
                    )
                }
                UserDestination.Profile -> {
                    ProfileScreen(
                        viewModel = viewModel,
                        onNavigateToLogin = { currentDestination = UserDestination.Login },
                        onNavigateToOrders = { currentDestination = UserDestination.Orders }
                    )
                }
                UserDestination.Login -> {
                    LoginScreen(
                        viewModel = viewModel,
                        onLoginSuccess = { currentDestination = UserDestination.Home },
                        onContinueAsGuest = { currentDestination = UserDestination.Home },
                        onBackClick = { currentDestination = UserDestination.Home }
                    )
                }
                UserDestination.Cart -> {
                    CartScreen(
                        viewModel = viewModel,
                        onBackClick = { currentDestination = UserDestination.Home },
                        onOrderPlaced = { orderId ->
                            trackingOrderId = orderId
                            currentDestination = UserDestination.Tracking
                        }
                    )
                }
                UserDestination.Tracking -> {
                    OrderTrackingScreen(
                        orderId = trackingOrderId ?: activeTrackingId ?: 1L,
                        viewModel = viewModel,
                        onBackClick = { currentDestination = UserDestination.Orders }
                    )
                }
            }

            // Floating Cart Bar (appears over Home screen when cart is not empty)
            if (currentDestination == UserDestination.Home && totalCartItems > 0) {
                FloatingCartBar(
                    totalItems = totalCartItems,
                    totalPrice = totalCartPrice,
                    onViewCartClick = { currentDestination = UserDestination.Cart },
                    modifier = Modifier.align(Alignment.BottomCenter)
                )
            }
        }
    }
}
