package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.QuestionAnswer
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.SettingsBrightness
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Lock
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import com.example.R
import com.example.data.model.SavedAddress
import com.example.ui.ZaykaViewModel
import com.example.ui.theme.BorderLight
import com.example.ui.theme.TextMutedLight
import com.example.ui.theme.TextPrimaryLight
import com.example.ui.theme.TextSecondaryLight
import com.example.ui.theme.VegGreen
import com.example.ui.theme.ZaykaAmber
import com.example.ui.theme.ZaykaOrange
import com.example.ui.theme.ZaykaRed

@Composable
fun ProfileScreen(
    viewModel: ZaykaViewModel,
    onNavigateToAdmin: () -> Unit,
    onNavigateToLogin: () -> Unit = {},
    onNavigateToOrders: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val currentUser by viewModel.currentUser.collectAsState()
    val savedAddresses by viewModel.savedAddresses.collectAsState()
    val allFoodItems by viewModel.allFoodItems.collectAsState()
    val allOrders by viewModel.allOrders.collectAsState()
    val contactInfo by viewModel.restaurantContactInfo.collectAsState()
    var showAddAddressDialog by remember { mutableStateOf(false) }
    var showSignOutDialog by remember { mutableStateOf(false) }
    var expandedFaq1 by remember { mutableStateOf(false) }
    var expandedFaq2 by remember { mutableStateOf(false) }
    var expandedFaq3 by remember { mutableStateOf(false) }

    var newLabel by remember { mutableStateOf("Home") }
    var newAddress by remember { mutableStateOf("") }
    var newLandmark by remember { mutableStateOf("") }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA)),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // User Header Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(if (currentUser != null && !currentUser!!.isAnonymous) Color(0xFFE8F0FE) else Color(0xFFFFEBEE)),
                            contentAlignment = Alignment.Center
                        ) {
                            if (currentUser != null && !currentUser!!.isAnonymous) {
                                Image(
                                    painter = painterResource(id = R.drawable.ic_google_logo),
                                    contentDescription = "Google Account",
                                    modifier = Modifier.size(30.dp)
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = "User Avatar",
                                    tint = ZaykaRed,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = currentUser?.displayName ?: "Guest Foodie",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 18.sp,
                                color = TextPrimaryLight
                            )
                            Text(
                                text = currentUser?.email?.takeIf { it.isNotBlank() } ?: "Sign in to sync your profile",
                                fontSize = 12.sp,
                                color = TextSecondaryLight
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = if (currentUser != null && !currentUser!!.isAnonymous) Color(0xFFE8F5E9) else Color(0xFFFFF3E0)
                            ) {
                                Text(
                                    text = if (currentUser != null && !currentUser!!.isAnonymous) "✓ Google Authenticated" else "⭐ Guest Mode",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (currentUser != null && !currentUser!!.isAnonymous) VegGreen else ZaykaOrange,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider(color = Color(0xFFF1F3F5))
                    Spacer(modifier = Modifier.height(10.dp))

                    // Sign In or Sign Out Action
                    if (currentUser == null || currentUser!!.isAnonymous) {
                        OutlinedButton(
                            onClick = onNavigateToLogin,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp)
                                .testTag("profile_sign_in_button"),
                            shape = RoundedCornerShape(10.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, ZaykaRed),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = ZaykaRed)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_google_logo),
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Sign in with Google",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    } else {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (currentUser != null && !currentUser!!.isAnonymous) {
                                    val roleText = if (currentUser?.role == "RESTAURANT_ADMIN") " • Restaurant Admin" else " • Customer"
                                    "Signed in via ${currentUser?.authProvider}$roleText"
                                } else {
                                    "Signed in as Guest Foodie"
                                },
                                fontSize = 12.sp,
                                color = TextMutedLight
                            )
                            TextButton(
                                onClick = { showSignOutDialog = true },
                                modifier = Modifier.testTag("profile_sign_out_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ExitToApp,
                                    contentDescription = "Sign Out",
                                    tint = ZaykaRed,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Sign Out",
                                    color = ZaykaRed,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // Theme Options (Light Mode / Dark Mode / System)
        item {
            val isDarkThemeSetting by viewModel.isDarkTheme.collectAsState()
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("theme_settings_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(com.example.ui.theme.PrimaryLight),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isDarkThemeSetting == true) Icons.Default.DarkMode else Icons.Default.LightMode,
                                contentDescription = "Theme Icon",
                                tint = com.example.ui.theme.Primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "App Theme & Appearance",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = TextPrimaryLight
                            )
                            Text(
                                text = when (isDarkThemeSetting) {
                                    true -> "Dark Mode Active"
                                    false -> "Light Mode Active"
                                    null -> "System Default (Auto)"
                                },
                                fontSize = 12.sp,
                                color = TextSecondaryLight
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Light Mode Button
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { viewModel.setThemeMode(false) }
                                .testTag("theme_light_button"),
                            shape = RoundedCornerShape(12.dp),
                            color = if (isDarkThemeSetting == false) com.example.ui.theme.Primary else Color(0xFFF4F4F5),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isDarkThemeSetting == false) com.example.ui.theme.Primary else Color(0xFFE4E4E7)
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(vertical = 10.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LightMode,
                                    contentDescription = null,
                                    tint = if (isDarkThemeSetting == false) Color.White else TextPrimaryLight,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Light",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isDarkThemeSetting == false) Color.White else TextPrimaryLight
                                )
                            }
                        }

                        // Dark Mode Button
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { viewModel.setThemeMode(true) }
                                .testTag("theme_dark_button"),
                            shape = RoundedCornerShape(12.dp),
                            color = if (isDarkThemeSetting == true) com.example.ui.theme.Primary else Color(0xFFF4F4F5),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isDarkThemeSetting == true) com.example.ui.theme.Primary else Color(0xFFE4E4E7)
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(vertical = 10.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.DarkMode,
                                    contentDescription = null,
                                    tint = if (isDarkThemeSetting == true) Color.White else TextPrimaryLight,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Dark",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isDarkThemeSetting == true) Color.White else TextPrimaryLight
                                )
                            }
                        }

                        // Auto / System Button
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { viewModel.setThemeMode(null) }
                                .testTag("theme_auto_button"),
                            shape = RoundedCornerShape(12.dp),
                            color = if (isDarkThemeSetting == null) com.example.ui.theme.Primary else Color(0xFFF4F4F5),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isDarkThemeSetting == null) com.example.ui.theme.Primary else Color(0xFFE4E4E7)
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(vertical = 10.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.SettingsBrightness,
                                    contentDescription = null,
                                    tint = if (isDarkThemeSetting == null) Color.White else TextPrimaryLight,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "System",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isDarkThemeSetting == null) Color.White else TextPrimaryLight
                                )
                            }
                        }
                    }
                }
            }
        }

        // Quick Switch to Staff / Admin Mode (ONLY visible to Admin)
        if (currentUser?.isAdmin == true) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigateToAdmin() }
                        .testTag("staff_admin_card"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF212121))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF333333)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AdminPanelSettings,
                                    contentDescription = "Admin",
                                    tint = ZaykaAmber,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Restaurant Staff & Admin Mode",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = "Manage live kitchen orders & menu stock",
                                    color = Color(0xFFAAAAAA),
                                    fontSize = 11.sp
                                )
                            }
                        }

                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Open Admin",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }

        // Your Orders Shortcut
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigateToOrders() }
                    .testTag("profile_view_orders_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFFFEBEE)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.ReceiptLong,
                                contentDescription = "My Orders",
                                tint = ZaykaRed,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Your Orders & History",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = TextPrimaryLight
                            )
                            Text(
                                text = "${allOrders.size} total orders • Track live status & reorder",
                                color = TextSecondaryLight,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "View Orders",
                        tint = TextSecondaryLight,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        // Saved Addresses Section
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Saved Addresses",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = TextPrimaryLight
                        )
                        IconButton(
                            onClick = { showAddAddressDialog = true },
                            modifier = Modifier.testTag("add_address_btn")
                        ) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = "Add Address", tint = ZaykaRed)
                        }
                    }

                    savedAddresses.forEachIndexed { index, address ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.setDefaultAddress(address.id, address.fullAddress)
                                }
                                .padding(vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
                                verticalAlignment = Alignment.Top
                            ) {
                                Icon(
                                    imageVector = if (address.label == "Home") Icons.Default.Home else Icons.Default.LocationOn,
                                    contentDescription = null,
                                    tint = ZaykaRed,
                                    modifier = Modifier
                                        .size(20.dp)
                                        .padding(top = 2.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = address.label,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = TextPrimaryLight
                                        )
                                        if (address.isDefault) {
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Surface(
                                                shape = RoundedCornerShape(4.dp),
                                                color = Color(0xFFE8F5E9)
                                            ) {
                                                Text(
                                                    text = "DEFAULT",
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.ExtraBold,
                                                    color = VegGreen,
                                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                                )
                                            }
                                        }
                                    }
                                    Text(
                                        text = address.fullAddress,
                                        fontSize = 12.sp,
                                        color = TextSecondaryLight
                                    )
                                    if (address.landmark.isNotBlank()) {
                                        Text(
                                            text = "Near: ${address.landmark}",
                                            fontSize = 11.sp,
                                            color = TextMutedLight
                                        )
                                    }
                                }
                            }

                            if (address.isDefault) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Selected",
                                    tint = VegGreen,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        if (index < savedAddresses.lastIndex) {
                            HorizontalDivider(color = Color(0xFFF1F3F5))
                        }
                    }
                }
            }
        }

        // Database & Authentication Architecture Status
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("database_auth_status_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(com.example.ui.theme.PrimaryLight),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Storage,
                                    contentDescription = "Database",
                                    tint = com.example.ui.theme.Primary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Local Room DB & Auth Engine",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = TextPrimaryLight
                                )
                                Text(
                                    text = "SQLite Room 2.6 + Firebase / Credential Manager",
                                    fontSize = 11.sp,
                                    color = TextMutedLight
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = com.example.ui.theme.TertiaryLight
                        ) {
                            Text(
                                text = "ONLINE",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = com.example.ui.theme.Tertiary,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider(color = Color(0xFFF1F3F5))
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(text = "Menu Items Cached", fontSize = 11.sp, color = TextMutedLight)
                            Text(
                                text = "${allFoodItems.size} items",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimaryLight
                            )
                        }
                        Column {
                            Text(text = "Recorded Orders", fontSize = 11.sp, color = TextMutedLight)
                            Text(
                                text = "${allOrders.size} orders",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimaryLight
                            )
                        }
                        Column {
                            Text(text = "Auth State", fontSize = 11.sp, color = TextMutedLight)
                            Text(
                                text = if (currentUser != null && !currentUser!!.isAnonymous) "Google Auth" else "Demo/Guest",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (currentUser != null && !currentUser!!.isAnonymous) com.example.ui.theme.Tertiary else com.example.ui.theme.Primary
                            )
                        }
                    }
                }
            }
        }

        // Restaurant Info & Help (Admin-Managed)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("restaurant_help_contact_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = contactInfo.restaurantName.ifBlank { "Restaurant Help & Contact" },
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = TextPrimaryLight
                        )
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFFE8F5E9)
                        ) {
                            Text(
                                text = "LIVE HELPDESK",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = VegGreen,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    if (contactInfo.helpDeskDescription.isNotBlank()) {
                        Text(
                            text = contactInfo.helpDeskDescription,
                            fontSize = 11.sp,
                            color = TextSecondaryLight,
                            lineHeight = 15.sp,
                            modifier = Modifier.padding(top = 4.dp, bottom = 10.dp)
                        )
                    } else {
                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    // Direct Call Action
                    ContactRow(
                        icon = Icons.Default.Call,
                        title = "Call Restaurant Help Desk",
                        subtitle = "${contactInfo.supportPhone} (${contactInfo.operatingHours})",
                        iconTint = VegGreen,
                        iconBackground = Color(0xFFE8F5E9),
                        onClick = {
                            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${contactInfo.supportPhone}"))
                            runCatching { context.startActivity(intent) }
                        }
                    )

                    HorizontalDivider(color = Color(0xFFF1F3F5), modifier = Modifier.padding(vertical = 6.dp))

                    // WhatsApp Chat Action
                    ContactRow(
                        icon = Icons.Default.Chat,
                        title = "WhatsApp Support & Orders",
                        subtitle = "Chat with us at ${contactInfo.whatsappNumber}",
                        iconTint = Color(0xFF059669),
                        iconBackground = Color(0xFFECFDF5),
                        onClick = {
                            val cleanNum = contactInfo.whatsappNumber.replace("+", "").replace(" ", "").replace("-", "")
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/$cleanNum"))
                            runCatching { context.startActivity(intent) }
                        }
                    )

                    HorizontalDivider(color = Color(0xFFF1F3F5), modifier = Modifier.padding(vertical = 6.dp))

                    // Email Support Action
                    ContactRow(
                        icon = Icons.Default.Email,
                        title = "Email Customer Support",
                        subtitle = contactInfo.supportEmail,
                        iconTint = ZaykaOrange,
                        iconBackground = Color(0xFFFFF3E0),
                        onClick = {
                            val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:${contactInfo.supportEmail}"))
                            runCatching { context.startActivity(intent) }
                        }
                    )

                    HorizontalDivider(color = Color(0xFFF1F3F5), modifier = Modifier.padding(vertical = 6.dp))

                    // Operating Hours
                    ContactRow(
                        icon = Icons.Default.Schedule,
                        title = "Operating & Kitchen Timings",
                        subtitle = contactInfo.operatingHours,
                        iconTint = ZaykaAmber,
                        iconBackground = Color(0xFFFFFBEB)
                    )

                    HorizontalDivider(color = Color(0xFFF1F3F5), modifier = Modifier.padding(vertical = 6.dp))

                    // Location / Address
                    ContactRow(
                        icon = Icons.Default.LocationOn,
                        title = "Kitchen & Restaurant Address",
                        subtitle = contactInfo.address,
                        iconTint = ZaykaRed,
                        iconBackground = Color(0xFFFFEBEE)
                    )

                    HorizontalDivider(color = Color(0xFFF1F3F5), modifier = Modifier.padding(vertical = 6.dp))

                    // FSSAI Food Safety License
                    ContactRow(
                        icon = Icons.Default.VerifiedUser,
                        title = "FSSAI Food Safety License",
                        subtitle = "Lic. No. ${contactInfo.fssaiNumber}",
                        iconTint = Color(0xFF1E88E5),
                        iconBackground = Color(0xFFE3F2FD)
                    )

                    if (contactInfo.emergencyManagerContact.isNotBlank()) {
                        HorizontalDivider(color = Color(0xFFF1F3F5), modifier = Modifier.padding(vertical = 6.dp))
                        ContactRow(
                            icon = Icons.Default.SupportAgent,
                            title = "Kitchen / Manager Direct Line",
                            subtitle = contactInfo.emergencyManagerContact,
                            iconTint = Color(0xFF6A1B9A),
                            iconBackground = Color(0xFFF3E5F5),
                            onClick = {
                                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${contactInfo.emergencyManagerContact}"))
                                runCatching { context.startActivity(intent) }
                            }
                        )
                    }

                    // Help FAQs Accordion
                    if (contactInfo.faq1Question.isNotBlank() || contactInfo.faq2Question.isNotBlank() || contactInfo.faq3Question.isNotBlank()) {
                        Spacer(modifier = Modifier.height(14.dp))
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFF8FAFC),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(bottom = 6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.QuestionAnswer,
                                        contentDescription = null,
                                        tint = ZaykaAmber,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Frequently Asked Questions",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimaryLight
                                    )
                                }

                                if (contactInfo.faq1Question.isNotBlank()) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(8.dp))
                                            .clickable { expandedFaq1 = !expandedFaq1 }
                                            .padding(vertical = 6.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "Q: ${contactInfo.faq1Question}",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = TextPrimaryLight,
                                                modifier = Modifier.weight(1f)
                                            )
                                            Icon(
                                                imageVector = if (expandedFaq1) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                                contentDescription = null,
                                                tint = TextSecondaryLight,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                        if (expandedFaq1 && contactInfo.faq1Answer.isNotBlank()) {
                                            Text(
                                                text = contactInfo.faq1Answer,
                                                fontSize = 11.sp,
                                                color = TextSecondaryLight,
                                                lineHeight = 15.sp,
                                                modifier = Modifier.padding(top = 4.dp, start = 4.dp)
                                            )
                                        }
                                    }
                                }

                                if (contactInfo.faq2Question.isNotBlank()) {
                                    HorizontalDivider(color = Color(0xFFE2E8F0), modifier = Modifier.padding(vertical = 4.dp))
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(8.dp))
                                            .clickable { expandedFaq2 = !expandedFaq2 }
                                            .padding(vertical = 6.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "Q: ${contactInfo.faq2Question}",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = TextPrimaryLight,
                                                modifier = Modifier.weight(1f)
                                            )
                                            Icon(
                                                imageVector = if (expandedFaq2) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                                contentDescription = null,
                                                tint = TextSecondaryLight,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                        if (expandedFaq2 && contactInfo.faq2Answer.isNotBlank()) {
                                            Text(
                                                text = contactInfo.faq2Answer,
                                                fontSize = 11.sp,
                                                color = TextSecondaryLight,
                                                lineHeight = 15.sp,
                                                modifier = Modifier.padding(top = 4.dp, start = 4.dp)
                                            )
                                        }
                                    }
                                }

                                if (contactInfo.faq3Question.isNotBlank()) {
                                    HorizontalDivider(color = Color(0xFFE2E8F0), modifier = Modifier.padding(vertical = 4.dp))
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(8.dp))
                                            .clickable { expandedFaq3 = !expandedFaq3 }
                                            .padding(vertical = 6.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "Q: ${contactInfo.faq3Question}",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = TextPrimaryLight,
                                                modifier = Modifier.weight(1f)
                                            )
                                            Icon(
                                                imageVector = if (expandedFaq3) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                                contentDescription = null,
                                                tint = TextSecondaryLight,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                        if (expandedFaq3 && contactInfo.faq3Answer.isNotBlank()) {
                                            Text(
                                                text = contactInfo.faq3Answer,
                                                fontSize = 11.sp,
                                                color = TextSecondaryLight,
                                                lineHeight = 15.sp,
                                                modifier = Modifier.padding(top = 4.dp, start = 4.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // App Version
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 14.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Surface(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    color = Color.White,
                    shadowElevation = 2.dp
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_zayka_logo),
                        contentDescription = "Zayka Logo",
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(4.dp)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Zayka — Food Delivery App v1.0.0",
                    fontSize = 12.sp,
                    color = TextMutedLight,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "Crafted for ZaykaChicken Cafe & Restaurant",
                    fontSize = 11.sp,
                    color = TextMutedLight
                )
            }
        }
    }

    // Add Address Dialog
    if (showAddAddressDialog) {
        AlertDialog(
            onDismissRequest = { showAddAddressDialog = false },
            title = { Text("Add Delivery Address", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    OutlinedTextField(
                        value = newLabel,
                        onValueChange = { newLabel = it },
                        label = { Text("Label (Home, Work, Other)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = newAddress,
                        onValueChange = { newAddress = it },
                        label = { Text("Complete Address") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = newLandmark,
                        onValueChange = { newLandmark = it },
                        label = { Text("Landmark (Optional)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newAddress.isNotBlank()) {
                            viewModel.addAddress(newLabel, newAddress, newLandmark)
                            showAddAddressDialog = false
                            newAddress = ""
                            newLandmark = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ZaykaRed)
                ) {
                    Text("Save Address")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddAddressDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Sign Out Confirmation Dialog
    if (showSignOutDialog) {
        AlertDialog(
            onDismissRequest = { showSignOutDialog = false },
            title = { Text("Sign Out", fontWeight = FontWeight.Bold) },
            text = {
                Text("Are you sure you want to sign out of your Google account? You will continue as a guest.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        showSignOutDialog = false
                        viewModel.signOut(context, onSignedOut = onNavigateToLogin)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ZaykaRed)
                ) {
                    Text("Sign Out")
                }
            },
            dismissButton = {
                TextButton(onClick = { showSignOutDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun ContactRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    iconTint: Color = ZaykaRed,
    iconBackground: Color = Color(0xFFF1F3F5),
    onClick: (() -> Unit)? = null
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .then(
                if (onClick != null) {
                    Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .clickable { onClick() }
                        .padding(vertical = 4.dp, horizontal = 4.dp)
                } else {
                    Modifier.padding(vertical = 4.dp, horizontal = 4.dp)
                }
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(iconBackground),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(18.dp))
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextPrimaryLight)
            Text(text = subtitle, fontSize = 11.sp, color = TextSecondaryLight)
        }
        if (onClick != null) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = TextMutedLight,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}
