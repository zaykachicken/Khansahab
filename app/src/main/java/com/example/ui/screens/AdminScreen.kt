package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Store
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
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
import com.example.ui.ZaykaViewModel
import com.example.ui.components.FoodItemThumbnail
import com.example.ui.components.VegNonVegIcon
import com.example.ui.theme.TextPrimaryLight
import com.example.ui.theme.TextSecondaryLight
import com.example.ui.theme.VegGreen
import com.example.ui.theme.ZaykaOrange
import com.example.ui.theme.ZaykaRed

@Composable
fun AdminScreen(
    viewModel: ZaykaViewModel,
    modifier: Modifier = Modifier
) {
    val allOrders by viewModel.allOrders.collectAsState()
    val activeOrders by viewModel.activeOrders.collectAsState()
    val foodItems by viewModel.allFoodItems.collectAsState()
    val isStoreOpen by viewModel.isStoreOpen.collectAsState()

    var selectedTab by remember { mutableIntStateOf(0) }
    var searchQuery by remember { mutableStateOf("") }

    // Dialog States
    var editingItem by remember { mutableStateOf<FoodItem?>(null) }
    var showAddDialog by remember { mutableStateOf(false) }
    var quickPricingItem by remember { mutableStateOf<FoodItem?>(null) }
    var itemToDelete by remember { mutableStateOf<FoodItem?>(null) }

    val totalRevenue = allOrders.filter { it.status == OrderStatus.DELIVERED }.sumOf { it.total }

    val filteredFoodItems = remember(foodItems, searchQuery) {
        if (searchQuery.isBlank()) foodItems
        else foodItems.filter {
            it.name.contains(searchQuery, ignoreCase = true) ||
                    it.category.contains(searchQuery, ignoreCase = true)
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

                        // Store open toggle
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
                }
            }

            // Tabs: Live Orders vs Menu Inventory
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.White,
                contentColor = ZaykaRed
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Live Orders (${activeOrders.size})", fontWeight = FontWeight.Bold) },
                    modifier = Modifier.testTag("admin_tab_orders")
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Menu & Pricing (${foodItems.size})", fontWeight = FontWeight.Bold) },
                    modifier = Modifier.testTag("admin_tab_menu")
                )
            }

            if (selectedTab == 0) {
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
            } else {
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
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("admin_order_${order.orderId}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Order #${order.orderNumber}",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 15.sp,
                        color = TextPrimaryLight
                    )
                    Text(
                        text = "${order.deliveryType} • ₹${order.total.toInt()} (${order.paymentMethod})",
                        fontSize = 12.sp,
                        color = TextSecondaryLight
                    )
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color(0xFFFFF3E0)
                ) {
                    Text(
                        text = order.status,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = ZaykaOrange,
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
                    colors = ButtonDefaults.buttonColors(containerColor = ZaykaRed),
                    modifier = Modifier
                        .weight(2f)
                        .testTag("admin_advance_btn_${order.orderId}")
                ) {
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

