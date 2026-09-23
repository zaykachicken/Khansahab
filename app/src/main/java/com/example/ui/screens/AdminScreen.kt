package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Discount
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material.icons.filled.Percent
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.material.icons.filled.RingVolume
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.R
import com.example.data.model.FoodItem
import com.example.data.model.OrderEntity
import com.example.data.model.OrderStatus
import com.example.ui.Coupon
import com.example.ui.ZaykaViewModel
import com.example.ui.components.FoodItemThumbnail
import com.example.ui.components.VegNonVegIcon
import com.example.ui.theme.TextPrimaryLight
import com.example.ui.theme.TextSecondaryLight
import com.example.ui.theme.VegGreen
import com.example.ui.theme.ZaykaOrange
import com.example.ui.theme.ZaykaRed
import com.example.ui.util.OrderAlertSoundManager

@Composable
fun AdminScreen(
    viewModel: ZaykaViewModel,
    onLogout: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val allOrders by viewModel.allOrders.collectAsState()
    val activeOrders by viewModel.activeOrders.collectAsState()
    val unacceptedOrders by viewModel.unacceptedOrders.collectAsState()
    val isAdminSoundAlertEnabled by viewModel.isAdminSoundAlertEnabled.collectAsState()
    val isAlarmMutedForCurrentBatch by viewModel.isAlarmMutedForCurrentBatch.collectAsState()
    val foodItems by viewModel.allFoodItems.collectAsState()
    val allDeals by viewModel.allDeals.collectAsState()
    val isStoreOpen by viewModel.isStoreOpen.collectAsState()

    // Sound Notification Engine Loop: Rings when there are unaccepted placed orders
    LaunchedEffect(unacceptedOrders, isAdminSoundAlertEnabled, isAlarmMutedForCurrentBatch) {
        if (unacceptedOrders.isNotEmpty() && isAdminSoundAlertEnabled && !isAlarmMutedForCurrentBatch) {
            OrderAlertSoundManager.startRinging(context, this)
        } else {
            OrderAlertSoundManager.stopRinging()
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            OrderAlertSoundManager.stopRinging()
        }
    }

    var selectedTab by remember { mutableIntStateOf(0) }
    var searchQuery by remember { mutableStateOf("") }
    var dealSearchQuery by remember { mutableStateOf("") }

    // Dialog States
    var editingItem by remember { mutableStateOf<FoodItem?>(null) }
    var showAddDialog by remember { mutableStateOf(false) }
    var quickPricingItem by remember { mutableStateOf<FoodItem?>(null) }
    var itemToDelete by remember { mutableStateOf<FoodItem?>(null) }

    // Deal Dialog States
    var editingDeal by remember { mutableStateOf<Coupon?>(null) }
    var showAddDealDialog by remember { mutableStateOf(false) }
    var dealToDelete by remember { mutableStateOf<Coupon?>(null) }

    val totalRevenue = allOrders.filter { it.status == OrderStatus.DELIVERED }.sumOf { it.total }

    val filteredFoodItems = remember(foodItems, searchQuery) {
        if (searchQuery.isBlank()) foodItems
        else foodItems.filter {
            it.name.contains(searchQuery, ignoreCase = true) ||
                    it.category.contains(searchQuery, ignoreCase = true)
        }
    }

    val filteredDeals = remember(allDeals, dealSearchQuery) {
        if (dealSearchQuery.isBlank()) allDeals
        else allDeals.filter {
            it.code.contains(dealSearchQuery, ignoreCase = true) ||
                    it.title.contains(dealSearchQuery, ignoreCase = true) ||
                    it.description.contains(dealSearchQuery, ignoreCase = true)
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Admin Top Bar
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color(0xFF1E1E1E),
                shadowElevation = 4.dp
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(8.dp)),
                                color = Color.White
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.ic_zayka_logo),
                                    contentDescription = "Zayka Logo",
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Restaurant Admin Panel",
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White
                                )
                                Text(
                                    text = "ZaykaChicken Cafe & Restaurant",
                                    fontSize = 11.sp,
                                    color = Color(0xFFAAAAAA)
                                )
                            }
                        }

                        // Store open toggle & Exit button
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = if (isStoreOpen) "STORE OPEN" else "STORE CLOSED",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isStoreOpen) Color(0xFF00E676) else Color(0xFFFF5252)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Switch(
                                checked = isStoreOpen,
                                onCheckedChange = { viewModel.toggleStoreStatus() },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = VegGreen,
                                    uncheckedThumbColor = Color.LightGray,
                                    uncheckedTrackColor = Color.DarkGray
                                ),
                                modifier = Modifier.testTag("admin_store_toggle")
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            IconButton(
                                onClick = onLogout,
                                modifier = Modifier.testTag("admin_logout_button")
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                                    contentDescription = "Exit Admin",
                                    tint = Color(0xFFFF8A80),
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Stats Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        MetricCard(
                            title = "Today's Orders",
                            value = "${allOrders.size}",
                            icon = Icons.Default.ListAlt,
                            tint = ZaykaOrange,
                            modifier = Modifier.weight(1f)
                        )
                        MetricCard(
                            title = "Active Kitchen",
                            value = "${activeOrders.size}",
                            icon = Icons.Default.Fastfood,
                            tint = ZaykaRed,
                            modifier = Modifier.weight(1f)
                        )
                        MetricCard(
                            title = "Revenue",
                            value = "₹${totalRevenue.toInt()}",
                            icon = Icons.Default.AttachMoney,
                            tint = VegGreen,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Order Receive Alert Sound Bar
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("admin_sound_alert_bar"),
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF242424))
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = if (unacceptedOrders.isNotEmpty() && !isAlarmMutedForCurrentBatch && isAdminSoundAlertEnabled) Icons.Default.RingVolume else if (isAdminSoundAlertEnabled) Icons.Default.VolumeUp else Icons.Default.VolumeOff,
                                        contentDescription = "Sound Status",
                                        tint = if (unacceptedOrders.isNotEmpty() && !isAlarmMutedForCurrentBatch && isAdminSoundAlertEnabled) Color(0xFFFF1744) else if (isAdminSoundAlertEnabled) Color(0xFF00E676) else Color.Gray,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = "Order Receive Alert ⚠️ Sound",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White
                                            )
                                            if (unacceptedOrders.isNotEmpty() && !isAlarmMutedForCurrentBatch && isAdminSoundAlertEnabled) {
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Surface(
                                                    color = Color(0xFFFF1744),
                                                    shape = RoundedCornerShape(4.dp)
                                                ) {
                                                    Text(
                                                        text = "RINGING 🔔",
                                                        fontSize = 9.sp,
                                                        fontWeight = FontWeight.ExtraBold,
                                                        color = Color.White,
                                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                                    )
                                                }
                                            }
                                        }
                                        Text(
                                            text = if (isAdminSoundAlertEnabled) "Continuous chime on new orders" else "Sound alerts disabled",
                                            fontSize = 10.sp,
                                            color = if (isAdminSoundAlertEnabled) Color(0xFFB0BEC5) else Color.Gray
                                        )
                                    }
                                }

                                Switch(
                                    checked = isAdminSoundAlertEnabled,
                                    onCheckedChange = { viewModel.toggleAdminSoundAlert() },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = Color.White,
                                        checkedTrackColor = VegGreen,
                                        uncheckedThumbColor = Color.LightGray,
                                        uncheckedTrackColor = Color.DarkGray
                                    ),
                                    modifier = Modifier
                                        .size(width = 36.dp, height = 22.dp)
                                        .testTag("admin_sound_alert_toggle")
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            HorizontalDivider(color = Color(0xFF333333))
                            Spacer(modifier = Modifier.height(6.dp))

                            // Action button: Test Alert Sound
                            OutlinedButton(
                                onClick = { OrderAlertSoundManager.playSingleBeep(context) },
                                shape = RoundedCornerShape(6.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = ZaykaOrange),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("btn_test_order_sound")
                            ) {
                                Icon(Icons.Default.VolumeUp, contentDescription = null, tint = ZaykaOrange, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("🔊 Test Audio Chime", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }
            }

            // High-Visibility Pulsing Incoming Order Ringing Banner (Shown when new order placed)
            if (unacceptedOrders.isNotEmpty()) {
                IncomingOrderRingingBanner(
                    unacceptedOrders = unacceptedOrders,
                    isMuted = isAlarmMutedForCurrentBatch,
                    onAccept = { order ->
                        viewModel.acceptOrder(order.orderId)
                    },
                    onMuteToggle = {
                        if (isAlarmMutedForCurrentBatch) {
                            viewModel.unmuteCurrentOrderAlert()
                        } else {
                            viewModel.muteCurrentOrderAlert()
                        }
                    },
                    onViewOrders = {
                        selectedTab = 0
                    }
                )
            }

            // Tabs: Live Orders vs Menu Inventory vs Deals & Discounts
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.White,
                contentColor = ZaykaRed
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Live Orders (${activeOrders.size})", fontWeight = FontWeight.Bold, fontSize = 12.sp) },
                    modifier = Modifier.testTag("admin_tab_orders")
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Menu (${foodItems.size})", fontWeight = FontWeight.Bold, fontSize = 12.sp) },
                    modifier = Modifier.testTag("admin_tab_menu")
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = { Text("Deals & Discounts (${allDeals.size})", fontWeight = FontWeight.Bold, fontSize = 12.sp) },
                    modifier = Modifier.testTag("admin_tab_deals")
                )
                Tab(
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 },
                    text = { Text("Help & Contact", fontWeight = FontWeight.Bold, fontSize = 12.sp) },
                    modifier = Modifier.testTag("admin_tab_contact")
                )
            }

            when (selectedTab) {
                0 -> {
                    // Live Orders Queue
                    if (activeOrders.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = VegGreen,
                                    modifier = Modifier.size(48.dp)
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = "Kitchen queue is clear!",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = TextPrimaryLight
                                )
                                Text(
                                    text = "New customer orders will appear here in real-time.",
                                    fontSize = 12.sp,
                                    color = TextSecondaryLight
                                )
                            }
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(activeOrders, key = { it.orderId }) { order ->
                                AdminOrderCard(
                                    order = order,
                                    onAdvanceStatus = {
                                        viewModel.advanceOrderStatus(order.orderId, order.status)
                                    },
                                    onCancelOrder = {
                                        viewModel.cancelOrder(order.orderId)
                                    }
                                )
                            }
                        }
                    }
                }
                1 -> {
                    // Menu Inventory & Pricing Management Tab
                    Column(modifier = Modifier.fillMaxSize()) {
                        // Search & Action Header
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = searchQuery,
                                onValueChange = { searchQuery = it },
                                placeholder = { Text("Search food items...", fontSize = 13.sp) },
                                leadingIcon = {
                                    Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(20.dp))
                                },
                                trailingIcon = {
                                    if (searchQuery.isNotEmpty()) {
                                        IconButton(onClick = { searchQuery = "" }) {
                                            Icon(Icons.Default.Close, contentDescription = "Clear", modifier = Modifier.size(16.dp))
                                        }
                                    }
                                },
                                singleLine = true,
                                shape = RoundedCornerShape(10.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = Color.White,
                                    unfocusedContainerColor = Color.White,
                                    focusedBorderColor = ZaykaRed,
                                    unfocusedBorderColor = Color(0xFFE0E0E0)
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(50.dp)
                                    .testTag("admin_menu_search")
                            )

                            Button(
                                onClick = { showAddDialog = true },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = ZaykaRed),
                                modifier = Modifier
                                    .height(50.dp)
                                    .testTag("admin_add_item_btn")
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Add Item", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                        }

                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 6.dp, bottom = 80.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(filteredFoodItems, key = { it.id }) { item ->
                                AdminMenuItemCard(
                                    item = item,
                                    onToggleAvailability = {
                                        viewModel.toggleItemAvailability(item)
                                    },
                                    onEditClick = {
                                        editingItem = item
                                    },
                                    onQuickPriceClick = {
                                        quickPricingItem = item
                                    },
                                    onDeleteClick = {
                                        itemToDelete = item
                                    }
                                )
                            }
                        }
                    }
                }
                2 -> {
                    // Deals & Discounts Management Tab
                    Column(modifier = Modifier.fillMaxSize()) {
                        // Search & Add Deal Header
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = dealSearchQuery,
                                onValueChange = { dealSearchQuery = it },
                                placeholder = { Text("Search coupons...", fontSize = 13.sp) },
                                leadingIcon = {
                                    Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(20.dp))
                                },
                                trailingIcon = {
                                    if (dealSearchQuery.isNotEmpty()) {
                                        IconButton(onClick = { dealSearchQuery = "" }) {
                                            Icon(Icons.Default.Close, contentDescription = "Clear", modifier = Modifier.size(16.dp))
                                        }
                                    }
                                },
                                singleLine = true,
                                shape = RoundedCornerShape(10.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = Color.White,
                                    unfocusedContainerColor = Color.White,
                                    focusedBorderColor = ZaykaRed,
                                    unfocusedBorderColor = Color(0xFFE0E0E0)
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(50.dp)
                                    .testTag("admin_deal_search")
                            )

                            Button(
                                onClick = { showAddDealDialog = true },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = ZaykaRed),
                                modifier = Modifier
                                    .height(50.dp)
                                    .testTag("admin_add_deal_btn")
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("New Deal", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                        }

                        if (filteredDeals.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(24.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        imageVector = Icons.Default.Discount,
                                        contentDescription = null,
                                        tint = Color.Gray,
                                        modifier = Modifier.size(48.dp)
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text(
                                        text = "No deals found",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                        color = TextPrimaryLight
                                    )
                                    Text(
                                        text = "Create promotional coupons to attract more foodies!",
                                        fontSize = 12.sp,
                                        color = TextSecondaryLight
                                    )
                                }
                            }
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 6.dp, bottom = 80.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                items(filteredDeals, key = { it.code }) { deal ->
                                    AdminDealCard(
                                        deal = deal,
                                        onToggleStatus = {
                                            viewModel.toggleDealStatus(deal.code, deal.isActive)
                                        },
                                        onEditClick = {
                                            editingDeal = deal
                                        },
                                        onDeleteClick = {
                                            dealToDelete = deal
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
                3 -> {
                    HelpAndContactAdminTab(
                        viewModel = viewModel,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }

    // Edit Item Dialog
    editingItem?.let { item ->
        FoodItemEditDialog(
            item = item,
            onDismiss = { editingItem = null },
            onSave = { updatedItem ->
                viewModel.saveFoodItem(updatedItem)
                editingItem = null
            }
        )
    }

    // Add New Item Dialog
    if (showAddDialog) {
        FoodItemEditDialog(
            item = null,
            onDismiss = { showAddDialog = false },
            onSave = { newItem ->
                viewModel.saveFoodItem(newItem)
                showAddDialog = false
            }
        )
    }

    // Quick Pricing Dialog
    quickPricingItem?.let { item ->
        QuickPricingDialog(
            item = item,
            onDismiss = { quickPricingItem = null },
            onPriceUpdated = { newPrice, newOriginalPrice ->
                viewModel.saveFoodItem(
                    item.copy(price = newPrice, originalPrice = newOriginalPrice)
                )
                quickPricingItem = null
            }
        )
    }

    // Confirm Delete Dialog
    itemToDelete?.let { item ->
        AlertDialog(
            onDismissRequest = { itemToDelete = null },
            title = { Text("Delete ${item.name}?", fontWeight = FontWeight.Bold) },
            text = { Text("Are you sure you want to remove this dish from the menu? This action cannot be undone.") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteFoodItem(item.id)
                        itemToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { itemToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Edit Deal Dialog
    editingDeal?.let { deal ->
        DealEditDialog(
            deal = deal,
            onDismiss = { editingDeal = null },
            onSave = { updatedDeal ->
                viewModel.saveDeal(updatedDeal)
                editingDeal = null
            }
        )
    }

    // Add New Deal Dialog
    if (showAddDealDialog) {
        DealEditDialog(
            deal = null,
            onDismiss = { showAddDealDialog = false },
            onSave = { newDeal ->
                viewModel.saveDeal(newDeal)
                showAddDealDialog = false
            }
        )
    }

    // Confirm Delete Deal Dialog
    dealToDelete?.let { deal ->
        AlertDialog(
            onDismissRequest = { dealToDelete = null },
            title = { Text("Delete Coupon ${deal.code}?", fontWeight = FontWeight.Bold) },
            text = { Text("Are you sure you want to permanently delete this discount offer? Customers won't be able to apply it.") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteDeal(deal.code)
                        dealToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("Delete Deal")
                }
            },
            dismissButton = {
                TextButton(onClick = { dealToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun IncomingOrderRingingBanner(
    unacceptedOrders: List<OrderEntity>,
    isMuted: Boolean,
    onAccept: (OrderEntity) -> Unit,
    onMuteToggle: () -> Unit,
    onViewOrders: () -> Unit,
    modifier: Modifier = Modifier
) {
    val firstOrder = unacceptedOrders.firstOrNull() ?: return
    val infiniteTransition = rememberInfiniteTransition(label = "order_ring_transition")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_alpha"
    )
    val scalePulse by infiniteTransition.animateFloat(
        initialValue = 0.98f,
        targetValue = 1.02f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale_pulse"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .border(
                width = 2.5.dp,
                color = Color(0xFFFF1744).copy(alpha = pulseAlpha),
                shape = RoundedCornerShape(14.dp)
            )
            .testTag("incoming_order_ringing_banner"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF0F2)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFFF1744)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isMuted) Icons.Default.VolumeOff else Icons.Default.RingVolume,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "⚠️ NEW ORDER RECEIVED!",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFFD50000)
                            )
                            if (unacceptedOrders.size > 1) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    color = Color(0xFFD50000),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text(
                                        text = "+${unacceptedOrders.size - 1} more",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                        Text(
                            text = if (isMuted) "⚠️ Sound alert muted • Tap Accept to confirm" else "🔔 Loud order alert sound ringing continuously",
                            fontSize = 11.sp,
                            color = Color(0xFF880E4F),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                IconButton(
                    onClick = onMuteToggle,
                    modifier = Modifier.size(34.dp)
                ) {
                    Icon(
                        imageVector = if (isMuted) Icons.Default.VolumeOff else Icons.Default.NotificationsActive,
                        contentDescription = "Toggle Mute",
                        tint = if (isMuted) Color.Gray else Color(0xFFD50000),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = Color(0xFFFFCDD2))
            Spacer(modifier = Modifier.height(10.dp))

            // Order Quick Details
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Order #${firstOrder.orderNumber} • ₹${firstOrder.total.toInt()} (${firstOrder.paymentMethod})",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = TextPrimaryLight
                    )
                    Text(
                        text = firstOrder.itemsSummaryJson,
                        fontSize = 12.sp,
                        color = Color(0xFF424242),
                        maxLines = 2
                    )
                    if (firstOrder.deliveryAddress.isNotBlank()) {
                        Text(
                            text = "📍 ${firstOrder.deliveryAddress}",
                            fontSize = 11.sp,
                            color = TextSecondaryLight,
                            maxLines = 1
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onMuteToggle,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF424242))
                ) {
                    Icon(
                        imageVector = if (isMuted) Icons.Default.VolumeUp else Icons.Default.VolumeOff,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(if (isMuted) "Unmute" else "Mute Sound", fontSize = 11.sp)
                }

                Button(
                    onClick = { onAccept(firstOrder) },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = VegGreen),
                    modifier = Modifier
                        .weight(1.5f)
                        .testTag("admin_banner_accept_order_btn")
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Accept Order", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun MetricCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    tint: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF2C2C2C))
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Icon(imageVector = icon, contentDescription = null, tint = tint, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = value, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = Color.White)
            Text(text = title, fontSize = 10.sp, color = Color(0xFFAAAAAA))
        }
    }
}

@Composable
fun AdminOrderCard(
    order: OrderEntity,
    onAdvanceStatus: () -> Unit,
    onCancelOrder: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isPendingPlaced = order.status == OrderStatus.PLACED
    val infiniteTransition = rememberInfiniteTransition(label = "order_card_ring")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 700, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "order_pulse"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .then(
                if (isPendingPlaced) {
                    Modifier.border(
                        width = 2.dp,
                        color = Color(0xFFFF1744).copy(alpha = pulseAlpha),
                        shape = RoundedCornerShape(14.dp)
                    )
                } else {
                    Modifier
                }
            )
            .testTag("admin_order_${order.orderId}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isPendingPlaced) Color(0xFFFFF8F8) else Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isPendingPlaced) 3.dp else 1.5.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Order #${order.orderNumber}",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 15.sp,
                            color = TextPrimaryLight
                        )
                        if (isPendingPlaced) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = Color(0xFFFF1744)
                            ) {
                                Text(
                                    text = "🔔 RINGING",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                    Text(
                        text = "${order.deliveryType} • ₹${order.total.toInt()} (${order.paymentMethod})",
                        fontSize = 12.sp,
                        color = TextSecondaryLight
                    )
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = if (isPendingPlaced) Color(0xFFFFEBEE) else Color(0xFFFEFCE8)
                ) {
                    Text(
                        text = order.status,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = if (isPendingPlaced) Color(0xFFD50000) else Color(0xFF854D0E),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = order.itemsSummaryJson,
                fontSize = 13.sp,
                color = TextPrimaryLight,
                lineHeight = 17.sp
            )

            if (order.deliveryInstruction.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Note: ${order.deliveryInstruction}",
                    fontSize = 11.sp,
                    color = ZaykaRed,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = Color(0xFFF1F3F5))
            Spacer(modifier = Modifier.height(10.dp))

            // Action Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onCancelOrder,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Reject", fontSize = 12.sp)
                }

                val nextActionTitle = when (order.status) {
                    OrderStatus.PLACED -> "Accept Order"
                    OrderStatus.CONFIRMED -> "Start Cooking"
                    OrderStatus.PREPARING -> "Send with Rider"
                    OrderStatus.OUT_FOR_DELIVERY -> "Mark Delivered"
                    else -> "Done"
                }

                Button(
                    onClick = onAdvanceStatus,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isPendingPlaced) VegGreen else ZaykaRed
                    ),
                    modifier = Modifier
                        .weight(2f)
                        .testTag("admin_advance_btn_${order.orderId}")
                ) {
                    if (isPendingPlaced) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                    }
                    Text(nextActionTitle, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun AdminMenuItemCard(
    item: FoodItem,
    onToggleAvailability: () -> Unit,
    onEditClick: () -> Unit,
    onQuickPriceClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Main Top Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Food Image
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(10.dp))
                ) {
                    FoodItemThumbnail(
                        imageSource = item.imageDrawableName,
                        contentDescription = item.name,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Name, Category & Price
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        VegNonVegIcon(isVeg = item.isVeg)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = item.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = TextPrimaryLight,
                            maxLines = 1
                        )
                    }

                    Spacer(modifier = Modifier.height(3.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "₹${item.price.toInt()}",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 15.sp,
                            color = ZaykaRed
                        )
                        if (item.originalPrice > item.price) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "₹${item.originalPrice.toInt()}",
                                fontSize = 12.sp,
                                color = Color.Gray,
                                style = androidx.compose.ui.text.TextStyle(textDecoration = androidx.compose.ui.text.style.TextDecoration.LineThrough)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "• ${item.category}",
                            fontSize = 11.sp,
                            color = TextSecondaryLight
                        )
                    }
                }

                // In Stock Switch
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = if (item.isAvailable) "IN STOCK" else "OUT OF STOCK",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (item.isAvailable) VegGreen else Color.Red
                    )
                    Switch(
                        checked = item.isAvailable,
                        onCheckedChange = { onToggleAvailability() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = VegGreen,
                            uncheckedThumbColor = Color.LightGray,
                            uncheckedTrackColor = Color(0xFFEEEEEE)
                        ),
                        modifier = Modifier.testTag("admin_stock_switch_${item.id}")
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = Color(0xFFF1F3F5))
            Spacer(modifier = Modifier.height(8.dp))

            // Action Buttons Row: Edit Item, Pricing, Delete
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Delete button
                IconButton(
                    onClick = onDeleteClick,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete item",
                        tint = Color(0xFFFF5252),
                        modifier = Modifier.size(18.dp)
                    )
                }

                Spacer(modifier = Modifier.width(6.dp))

                // Quick Pricing Button
                OutlinedButton(
                    onClick = onQuickPriceClick,
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    modifier = Modifier.height(34.dp)
                ) {
                    Icon(Icons.Default.AttachMoney, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(2.dp))
                    Text("Change Price", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Full Edit Button
                Button(
                    onClick = onEditClick,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2C2C2C)),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    modifier = Modifier
                        .height(34.dp)
                        .testTag("admin_edit_btn_${item.id}")
                ) {
                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Edit Item", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun FoodItemEditDialog(
    item: FoodItem?,
    onDismiss: () -> Unit,
    onSave: (FoodItem) -> Unit
) {
    val isNew = item == null

    var name by remember { mutableStateOf(item?.name ?: "") }
    var category by remember { mutableStateOf(item?.category ?: "Biryani & Rice") }
    var description by remember { mutableStateOf(item?.description ?: "") }
    var priceText by remember { mutableStateOf(item?.price?.toInt()?.toString() ?: "") }
    var originalPriceText by remember { mutableStateOf(item?.originalPrice?.toInt()?.toString() ?: "") }
    var isVeg by remember { mutableStateOf(item?.isVeg ?: false) }
    var isBestseller by remember { mutableStateOf(item?.isBestseller ?: false) }
    var selectedImageSource by remember { mutableStateOf(item?.imageDrawableName ?: "img_hero_biryani") }
    var customImageUrl by remember { mutableStateOf(if (item?.imageDrawableName?.startsWith("http") == true) item.imageDrawableName else "") }
    var isUsingCustomUrl by remember { mutableStateOf(item?.imageDrawableName?.startsWith("http") == true || item?.imageDrawableName?.startsWith("content") == true) }

    // Photo picker launcher
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedImageSource = uri.toString()
            isUsingCustomUrl = true
        }
    }

    val presetImages = listOf(
        "img_hero_biryani" to "Dum Biryani",
        "img_butter_chicken" to "Butter Chicken",
        "img_tandoori_platter" to "Tandoori Platter",
        "img_chicken_tikka" to "Chicken Tikka",
        "img_paneer_tikka" to "Paneer Tikka"
    )

    val categories = listOf("Biryani & Rice", "Curries & Gravies", "Tandoor & Starters", "Breads & Combos", "Beverages & Desserts")

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = Color.White,
            shadowElevation = 8.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Title
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = if (isNew) "Add New Food Item" else "Edit Food Item",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextPrimaryLight
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Food Image Selection Section
                Text(
                    text = "Food Item Image",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimaryLight
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Current Image Preview
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .border(1.5.dp, ZaykaRed, RoundedCornerShape(12.dp))
                    ) {
                        FoodItemThumbnail(
                            imageSource = selectedImageSource,
                            contentDescription = "Item Preview",
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column {
                        // Pick from device gallery
                        OutlinedButton(
                            onClick = {
                                photoPickerLauncher.launch(
                                    androidx.activity.result.PickVisualMediaRequest(
                                        ActivityResultContracts.PickVisualMedia.ImageOnly
                                    )
                                )
                            },
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Pick from Gallery", fontSize = 12.sp)
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Or choose from restaurant presets below",
                            fontSize = 10.sp,
                            color = Color.Gray
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Preset Images Selector
                Text(text = "Preset Photos:", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = TextSecondaryLight)
                Spacer(modifier = Modifier.height(6.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(presetImages) { (drawableName, label) ->
                        val isSelected = selectedImageSource == drawableName && !isUsingCustomUrl
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable {
                                    selectedImageSource = drawableName
                                    isUsingCustomUrl = false
                                }
                                .padding(2.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .border(
                                        width = if (isSelected) 2.5.dp else 1.dp,
                                        color = if (isSelected) ZaykaRed else Color.LightGray,
                                        shape = RoundedCornerShape(8.dp)
                                    )
                            ) {
                                FoodItemThumbnail(
                                    imageSource = drawableName,
                                    contentDescription = label,
                                    modifier = Modifier.fillMaxSize()
                                )
                                if (isSelected) {
                                    Surface(
                                        color = ZaykaRed,
                                        shape = CircleShape,
                                        modifier = Modifier
                                            .size(16.dp)
                                            .align(Alignment.TopEnd)
                                            .padding(2.dp)
                                    ) {
                                        Icon(Icons.Default.Check, contentDescription = null, tint = Color.White)
                                    }
                                }
                            }
                            Text(text = label, fontSize = 9.sp, maxLines = 1, color = if (isSelected) ZaykaRed else Color.DarkGray)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Image URL input
                OutlinedTextField(
                    value = customImageUrl,
                    onValueChange = {
                        customImageUrl = it
                        if (it.isNotBlank()) {
                            selectedImageSource = it
                            isUsingCustomUrl = true
                        }
                    },
                    label = { Text("Or paste Food Image Web URL", fontSize = 11.sp) },
                    placeholder = { Text("https://example.com/dish.jpg", fontSize = 11.sp) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )

                Spacer(modifier = Modifier.height(14.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(14.dp))

                // Dish Name
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Dish Name *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Category chips
                Text(text = "Category:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimaryLight)
                Spacer(modifier = Modifier.height(6.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(categories) { cat ->
                        val isSelected = category == cat
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = if (isSelected) ZaykaRed else Color(0xFFF1F3F5),
                            modifier = Modifier.clickable { category = cat }
                        ) {
                            Text(
                                text = cat,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) Color.White else TextPrimaryLight,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Pricing Row: Selling Price & MRP / Original Price
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = priceText,
                        onValueChange = { priceText = it },
                        label = { Text("Selling Price (₹) *") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    )

                    OutlinedTextField(
                        value = originalPriceText,
                        onValueChange = { originalPriceText = it },
                        label = { Text("Original MRP (₹)") },
                        placeholder = { Text("Optional") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Description
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description & Ingredients") },
                    maxLines = 3,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Veg / Non-Veg & Bestseller Toggles
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        VegNonVegIcon(isVeg = isVeg)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = if (isVeg) "Vegetarian" else "Non-Vegetarian", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    }

                    Switch(
                        checked = isVeg,
                        onCheckedChange = { isVeg = it },
                        colors = SwitchDefaults.colors(checkedTrackColor = VegGreen)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Mark as Bestseller", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    Switch(
                        checked = isBestseller,
                        onCheckedChange = { isBestseller = it },
                        colors = SwitchDefaults.colors(checkedTrackColor = ZaykaOrange)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Save Action
                val canSave = name.isNotBlank() && (priceText.toDoubleOrNull() ?: 0.0) > 0

                Button(
                    onClick = {
                        val parsedPrice = priceText.toDoubleOrNull() ?: 0.0
                        val parsedOriginalPrice = originalPriceText.toDoubleOrNull() ?: parsedPrice
                        val finalItem = FoodItem(
                            id = item?.id ?: "food_${System.currentTimeMillis()}",
                            name = name.trim(),
                            category = category,
                            description = description.trim(),
                            price = parsedPrice,
                            originalPrice = if (parsedOriginalPrice >= parsedPrice) parsedOriginalPrice else parsedPrice,
                            isVeg = isVeg,
                            rating = item?.rating ?: 4.5f,
                            ratingCount = item?.ratingCount ?: 50,
                            isBestseller = isBestseller,
                            imageDrawableName = selectedImageSource,
                            isAvailable = item?.isAvailable ?: true,
                            portionsJson = item?.portionsJson ?: "[\"Standard\"]",
                            spicesJson = item?.spicesJson ?: "[\"Medium Spicy\"]",
                            addonsJson = item?.addonsJson ?: "[]"
                        )
                        onSave(finalItem)
                    },
                    enabled = canSave,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ZaykaRed),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("admin_save_food_item_btn")
                ) {
                    Text(
                        text = if (isNew) "Add Item to Menu" else "Save Changes",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

@Composable
fun QuickPricingDialog(
    item: FoodItem,
    onDismiss: () -> Unit,
    onPriceUpdated: (newPrice: Double, newOriginalPrice: Double) -> Unit
) {
    var priceText by remember { mutableStateOf(item.price.toInt().toString()) }
    var originalPriceText by remember { mutableStateOf(item.originalPrice.toInt().toString()) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color.White,
            shadowElevation = 8.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "Update Pricing",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = TextPrimaryLight
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = item.name,
                    fontSize = 13.sp,
                    color = TextSecondaryLight
                )

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = priceText,
                    onValueChange = { priceText = it },
                    label = { Text("Selling Price (₹)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = originalPriceText,
                    onValueChange = { originalPriceText = it },
                    label = { Text("Original MRP (₹)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            val p = priceText.toDoubleOrNull() ?: item.price
                            val op = originalPriceText.toDoubleOrNull() ?: p
                            onPriceUpdated(p, op)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ZaykaRed),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Update Price")
                    }
                }
            }
        }
    }
}

@Composable
fun AdminDealCard(
    deal: Coupon,
    onToggleStatus: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("admin_deal_card_${deal.code}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = if (deal.isActive) ZaykaOrange else Color.Gray
                    ) {
                        Text(
                            text = deal.code,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 13.sp,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                    if (deal.badgeTag.isNotBlank()) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = ZaykaRed.copy(alpha = 0.12f)
                        ) {
                            Text(
                                text = deal.badgeTag,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = ZaykaRed,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (deal.isActive) "Active" else "Disabled",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (deal.isActive) VegGreen else Color.Gray
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Switch(
                        checked = deal.isActive,
                        onCheckedChange = { onToggleStatus() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = VegGreen
                        ),
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("deal_toggle_${deal.code}")
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = deal.title,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = TextPrimaryLight
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = deal.description,
                fontSize = 12.sp,
                color = TextSecondaryLight
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Deal Specs Badges
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = Color(0xFFF1F3F5)
                ) {
                    Text(
                        text = "Min Order: ₹${deal.minOrder.toInt()}",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.DarkGray,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                    )
                }

                if (deal.discountPercent > 0) {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = Color(0xFFE8F5E9)
                    ) {
                        Text(
                            text = "${deal.discountPercent.toInt()}% OFF (Max ₹${deal.maxDiscount.toInt()})",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF2E7D32),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                        )
                    }
                } else if (deal.flatDiscount > 0) {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = Color(0xFFFEFCE8)
                    ) {
                        Text(
                            text = "Flat ₹${deal.flatDiscount.toInt()} OFF",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF854D0E),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                        )
                    }
                } else if (deal.isFreeDelivery) {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = Color(0xFFE3F2FD)
                    ) {
                        Text(
                            text = "FREE DELIVERY",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF1565C0),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                        )
                    }
                }
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 10.dp),
                color = Color(0xFFF1F3F5)
            )

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = onDeleteClick,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red),
                    modifier = Modifier.testTag("admin_delete_deal_${deal.code}")
                ) {
                    Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Delete", fontSize = 12.sp)
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = onEditClick,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ZaykaRed),
                    modifier = Modifier.testTag("admin_edit_deal_${deal.code}")
                ) {
                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Edit Deal", fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
fun DealEditDialog(
    deal: Coupon?,
    onDismiss: () -> Unit,
    onSave: (Coupon) -> Unit
) {
    val isEdit = deal != null

    var code by remember { mutableStateOf(deal?.code ?: "") }
    var title by remember { mutableStateOf(deal?.title ?: "") }
    var description by remember { mutableStateOf(deal?.description ?: "") }
    var minOrderText by remember { mutableStateOf(deal?.minOrder?.toInt()?.toString() ?: "199") }
    var badgeTag by remember { mutableStateOf(deal?.badgeTag ?: "POPULAR") }

    // Discount type: 0 = Percentage (e.g. 50% max ₹120), 1 = Flat (e.g. ₹100), 2 = Free Delivery
    var discountType by remember {
        mutableIntStateOf(
            when {
                deal?.isFreeDelivery == true -> 2
                (deal?.flatDiscount ?: 0.0) > 0 -> 1
                else -> 0
            }
        )
    }

    var discountPercentText by remember { mutableStateOf(deal?.discountPercent?.toInt()?.toString() ?: "50") }
    var maxDiscountText by remember { mutableStateOf(deal?.maxDiscount?.toInt()?.toString() ?: "120") }
    var flatDiscountText by remember { mutableStateOf(deal?.flatDiscount?.toInt()?.toString() ?: "100") }
    var isActive by remember { mutableStateOf(deal?.isActive ?: true) }

    var validationError by remember { mutableStateOf<String?>(null) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color.White,
            shadowElevation = 8.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = if (isEdit) "Edit Deal / Discount" else "Create New Deal",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = TextPrimaryLight
                )
                Text(
                    text = "Manage coupon parameters and customer discount logic",
                    fontSize = 12.sp,
                    color = TextSecondaryLight
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Promo Code
                OutlinedTextField(
                    value = code,
                    onValueChange = {
                        code = it.uppercase().replace(" ", "")
                        validationError = null
                    },
                    label = { Text("Coupon Code (e.g. ZAYKA50)") },
                    singleLine = true,
                    enabled = !isEdit, // Code is primary key
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("deal_code_input")
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Title
                OutlinedTextField(
                    value = title,
                    onValueChange = {
                        title = it
                        validationError = null
                    },
                    label = { Text("Offer Title (e.g. 50% OFF up to ₹120)") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("deal_title_input")
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Description
                OutlinedTextField(
                    value = description,
                    onValueChange = {
                        description = it
                        validationError = null
                    },
                    label = { Text("Short Description") },
                    maxLines = 2,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("deal_desc_input")
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Discount Type Selector
                Text(
                    text = "Discount Type",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimaryLight
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf("Percentage %", "Flat Amount ₹", "Free Delivery").forEachIndexed { index, label ->
                        val isSelected = discountType == index
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) ZaykaRed else Color(0xFFF1F3F5),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { discountType = index }
                        ) {
                            Text(
                                text = label,
                                color = if (isSelected) Color.White else TextPrimaryLight,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Dynamic fields according to discount type
                when (discountType) {
                    0 -> {
                        // Percentage
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = discountPercentText,
                                onValueChange = { discountPercentText = it },
                                label = { Text("Discount %") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = maxDiscountText,
                                onValueChange = { maxDiscountText = it },
                                label = { Text("Max Cap (₹)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                    1 -> {
                        // Flat
                        OutlinedTextField(
                            value = flatDiscountText,
                            onValueChange = { flatDiscountText = it },
                            label = { Text("Flat Discount Amount (₹)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    2 -> {
                        // Free delivery info
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFFE3F2FD),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Customers using this code will receive ₹0 delivery fee on eligible orders.",
                                fontSize = 11.sp,
                                color = Color(0xFF1565C0),
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Min Order & Badge
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = minOrderText,
                        onValueChange = { minOrderText = it },
                        label = { Text("Min Order (₹)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = badgeTag,
                        onValueChange = { badgeTag = it.uppercase() },
                        label = { Text("Badge (Optional)") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Is Active Toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Enable Deal Immediately", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    Switch(
                        checked = isActive,
                        onCheckedChange = { isActive = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = VegGreen
                        )
                    )
                }

                if (validationError != null) {
                    Text(
                        text = validationError!!,
                        color = Color.Red,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(top = 6.dp)
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            val codeClean = code.trim().uppercase()
                            val titleClean = title.trim()
                            val descClean = description.trim()
                            val minOrderVal = minOrderText.toDoubleOrNull() ?: 0.0

                            if (codeClean.isBlank()) {
                                validationError = "Coupon code cannot be empty"
                                return@Button
                            }
                            if (titleClean.isBlank()) {
                                validationError = "Please enter an offer title"
                                return@Button
                            }

                            val newCoupon = Coupon(
                                code = codeClean,
                                title = titleClean,
                                description = if (descClean.isBlank()) "Special discount for you" else descClean,
                                minOrder = minOrderVal,
                                discountPercent = if (discountType == 0) (discountPercentText.toDoubleOrNull() ?: 0.0) else 0.0,
                                maxDiscount = if (discountType == 0) (maxDiscountText.toDoubleOrNull() ?: 0.0) else 0.0,
                                flatDiscount = if (discountType == 1) (flatDiscountText.toDoubleOrNull() ?: 0.0) else 0.0,
                                isFreeDelivery = discountType == 2,
                                isActive = isActive,
                                badgeTag = badgeTag.trim().uppercase()
                            )
                            onSave(newCoupon)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ZaykaRed),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("save_deal_button")
                    ) {
                        Text(if (isEdit) "Save Changes" else "Create Deal")
                    }
                }
            }
        }
    }
}


