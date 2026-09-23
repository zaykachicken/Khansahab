package com.example.admin.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.ui.ZaykaViewModel
import com.example.ui.screens.AdminScreen
import com.example.ui.screens.RestaurantLoginScreen

/**
 * ADMIN APP — Standalone Restaurant Partner & Admin Application UI
 *
 * Dedicated strictly to restaurant management and kitchen operations:
 * - Admin credentials authentication & role-based gatekeeping (RESTAURANT_ADMIN)
 * - Live incoming order ringing audio alarm chime (OrderAlertSoundManager)
 * - Order lifecycle management (Accept/Reject, Placed -> Confirmed -> Preparing -> Out for Delivery -> Delivered)
 * - Real-time cloud synchronization via Firebase Firestore with Customer User App
 * - Menu management (Add food item, edit details, update prices, toggle availability/stock)
 * - Deals & coupons configuration
 * - Restaurant contact, support & business settings
 * - Daily sales & order analytics
 *
 * Strictly prevents customer shopping, cart, or customer checkout features.
 */
@Composable
fun AdminMainScreen(
    viewModel: ZaykaViewModel,
    onSwitchToUser: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val currentUser by viewModel.currentUser.collectAsState()
    val isUserAdmin = currentUser?.isAdmin == true

    Box(modifier = modifier.fillMaxSize()) {
        AnimatedContent(
            targetState = isUserAdmin,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            label = "admin_auth_content"
        ) { authenticatedAsAdmin ->
            if (authenticatedAsAdmin) {
                AdminScreen(
                    viewModel = viewModel,
                    onLogout = {
                        viewModel.signOut(context)
                        onSwitchToUser?.invoke()
                    }
                )
            } else {
                RestaurantLoginScreen(
                    viewModel = viewModel,
                    onLoginSuccess = {
                        // UserAccount state is updated to RESTAURANT_ADMIN,
                        // triggering transition to AdminScreen
                    },
                    onBackClick = {
                        onSwitchToUser?.invoke()
                    }
                )
            }
        }
    }
}
