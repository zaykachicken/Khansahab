package com.example.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.outlined.AdminPanelSettings
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.FloatingCartBar
import com.example.ui.screens.AdminScreen
import com.example.ui.screens.CartScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LoginScreen
import com.example.ui.screens.OrderHistoryScreen
import com.example.ui.screens.OrderTrackingScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.theme.TextPrimaryLight
import com.example.ui.theme.TextSecondaryLight
import com.example.ui.theme.ZaykaRed

sealed class AppDestination(val route: String, val label: String) {
    object Home : AppDestination("home", "Delivery")
    object Orders : AppDestination("orders", "Orders")
    object Admin : AppDestination("admin", "Admin")
    object Profile : AppDestination("profile", "Profile")
    object Cart : AppDestination("cart", "Cart")
    object Tracking : AppDestination("tracking", "Tracking")
    object Login : AppDestination("login", "Login")
}

@Composable
fun ZaykaMainScreen(
    viewModel: ZaykaViewModel,
    modifier: Modifier = Modifier
) {
    val currentUser by viewModel.currentUser.collectAsState()
    var currentDestination by remember {
        mutableStateOf<AppDestination>(if (viewModel.currentUser.value != null) AppDestination.Home else AppDestination.Login)
    }
    var trackingOrderId by remember { mutableStateOf<Long?>(null) }

    val cartItems by viewModel.cartItems.collectAsState()
    val activeOrders by viewModel.activeOrders.collectAsState()
    val activeTrackingId by viewModel.activeTrackingOrderId.collectAsState()

    val totalCartItems = cartItems.sumOf { it.quantity }
    val totalCartPrice = cartItems.sumOf { it.totalCost }

    val showBottomBar = currentDestination != AppDestination.Cart &&
            currentDestination != AppDestination.Tracking &&
            currentDestination != AppDestination.Login

    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            if (showBottomBar) {
                Surface(
                    color = Color.White,
                    tonalElevation = 8.dp,
                    shadowElevation = 8.dp,
                    shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, com.example.ui.theme.BorderLight),
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .testTag("bottom_navigation_bar")
                ) {
                    NavigationBar(
                        containerColor = Color.Transparent,
                        tonalElevation = 0.dp,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Home (Delivery)
                        NavigationBarItem(
                            selected = currentDestination == AppDestination.Home,
                            onClick = { currentDestination = AppDestination.Home },
                            icon = {
                                Icon(
                                    imageVector = if (currentDestination == AppDestination.Home) Icons.Filled.Fastfood else Icons.Outlined.Fastfood,
                                    contentDescription = "Delivery"
                                )
                            },
                            label = { Text("Delivery", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Color.White,
                                selectedTextColor = com.example.ui.theme.Primary,
                                indicatorColor = com.example.ui.theme.Primary,
                                unselectedIconColor = com.example.ui.theme.TextSecondaryLight,
                                unselectedTextColor = com.example.ui.theme.TextSecondaryLight
                            ),
                            modifier = Modifier.testTag("nav_item_delivery")
                        )

                        // Orders
                        NavigationBarItem(
                            selected = currentDestination == AppDestination.Orders,
                            onClick = { currentDestination = AppDestination.Orders },
                            icon = {
                                BadgedBox(
                                    badge = {
                                        if (activeOrders.isNotEmpty()) {
                                            Badge(containerColor = com.example.ui.theme.Secondary) {
                                                Text("${activeOrders.size}", color = Color.White)
                                            }
                                        }
                                    }
                                ) {
                                    Icon(
                                        imageVector = if (currentDestination == AppDestination.Orders) Icons.Filled.ReceiptLong else Icons.Outlined.ReceiptLong,
                                        contentDescription = "Orders"
                                    )
                                }
                            },
                            label = { Text("Orders", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Color.White,
                                selectedTextColor = com.example.ui.theme.Primary,
                                indicatorColor = com.example.ui.theme.Primary,
                                unselectedIconColor = com.example.ui.theme.TextSecondaryLight,
                                unselectedTextColor = com.example.ui.theme.TextSecondaryLight
                            ),
                            modifier = Modifier.testTag("nav_item_orders")
                        )

                        // Restaurant Admin
                        NavigationBarItem(
                            selected = currentDestination == AppDestination.Admin,
                            onClick = { currentDestination = AppDestination.Admin },
                            icon = {
                                Icon(
                                    imageVector = if (currentDestination == AppDestination.Admin) Icons.Filled.AdminPanelSettings else Icons.Outlined.AdminPanelSettings,
                                    contentDescription = "Admin"
                                )
                            },
                            label = { Text("Admin", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Color.White,
                                selectedTextColor = com.example.ui.theme.Primary,
                                indicatorColor = com.example.ui.theme.Primary,
                                unselectedIconColor = com.example.ui.theme.TextSecondaryLight,
                                unselectedTextColor = com.example.ui.theme.TextSecondaryLight
                            ),
                            modifier = Modifier.testTag("nav_item_admin")
                        )

                        // Profile
                        NavigationBarItem(
                            selected = currentDestination == AppDestination.Profile,
                            onClick = { currentDestination = AppDestination.Profile },
                            icon = {
                                Icon(
                                    imageVector = if (currentDestination == AppDestination.Profile) Icons.Filled.Person else Icons.Outlined.Person,
                                    contentDescription = "Profile"
                                )
                            },
                            label = { Text("Profile", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Color.White,
                                selectedTextColor = com.example.ui.theme.Primary,
                                indicatorColor = com.example.ui.theme.Primary,
                                unselectedIconColor = com.example.ui.theme.TextSecondaryLight,
                                unselectedTextColor = com.example.ui.theme.TextSecondaryLight
                            ),
                            modifier = Modifier.testTag("nav_item_profile")
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
            // Main Screen Content based on destination
            when (currentDestination) {
                AppDestination.Home -> {
                    HomeScreen(
                        viewModel = viewModel,
                        onNavigateToCart = { currentDestination = AppDestination.Cart },
                        onNavigateToProfile = { currentDestination = AppDestination.Profile }
                    )
                }
                AppDestination.Orders -> {
                    OrderHistoryScreen(
                        viewModel = viewModel,
                        onTrackOrder = { orderId ->
                            trackingOrderId = orderId
                            currentDestination = AppDestination.Tracking
                        },
                        onReorderClicked = {
                            currentDestination = AppDestination.Cart
                        }
                    )
                }
                AppDestination.Admin -> {
                    AdminScreen(viewModel = viewModel)
                }
                AppDestination.Profile -> {
                    ProfileScreen(
                        viewModel = viewModel,
                        onNavigateToAdmin = { currentDestination = AppDestination.Admin },
                        onNavigateToLogin = { currentDestination = AppDestination.Login }
                    )
                }
                AppDestination.Login -> {
                    LoginScreen(
                        viewModel = viewModel,
                        onLoginSuccess = { currentDestination = AppDestination.Home },
                        onContinueAsGuest = { currentDestination = AppDestination.Home },
                        onBackClick = { currentDestination = AppDestination.Home }
                    )
                }
                AppDestination.Cart -> {
                    CartScreen(
                        viewModel = viewModel,
                        onBackClick = { currentDestination = AppDestination.Home },
                        onOrderPlaced = { orderId ->
                            trackingOrderId = orderId
                            currentDestination = AppDestination.Tracking
                        }
                    )
                }
                AppDestination.Tracking -> {
                    OrderTrackingScreen(
                        orderId = trackingOrderId ?: activeTrackingId ?: 1L,
                        viewModel = viewModel,
                        onBackClick = { currentDestination = AppDestination.Orders }
                    )
                }
            }

            // Floating Cart Bar (appears over Home screen when cart is not empty)
            if (currentDestination == AppDestination.Home && totalCartItems > 0) {
                FloatingCartBar(
                    totalItems = totalCartItems,
                    totalPrice = totalCartPrice,
                    onViewCartClick = { currentDestination = AppDestination.Cart },
                    modifier = Modifier.align(Alignment.BottomCenter)
                )
            }
        }
    }
}
