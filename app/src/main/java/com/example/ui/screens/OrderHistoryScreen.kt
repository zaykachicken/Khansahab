package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeliveryDining
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.OrderEntity
import com.example.data.model.OrderStatus
import com.example.ui.ZaykaViewModel
import com.example.ui.theme.TextMutedLight
import com.example.ui.theme.TextPrimaryLight
import com.example.ui.theme.TextSecondaryLight
import com.example.ui.theme.VegGreen
import com.example.ui.theme.ZaykaAmber
import com.example.ui.theme.ZaykaOrange
import com.example.ui.theme.ZaykaRed
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class OrderFilter(val title: String) {
    ALL("All Orders"),
    LIVE("Active / Live"),
    DELIVERED("Delivered"),
    CANCELLED("Cancelled")
}

@Composable
fun OrderHistoryScreen(
    viewModel: ZaykaViewModel,
    onTrackOrder: (Long) -> Unit,
    onReorderClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    val orders by viewModel.allOrders.collectAsState()
    var selectedFilter by remember { mutableStateOf(OrderFilter.ALL) }

    val filteredOrders = remember(orders, selectedFilter) {
        when (selectedFilter) {
            OrderFilter.ALL -> orders
            OrderFilter.LIVE -> orders.filter { it.status != OrderStatus.DELIVERED && it.status != OrderStatus.CANCELLED }
            OrderFilter.DELIVERED -> orders.filter { it.status == OrderStatus.DELIVERED }
            OrderFilter.CANCELLED -> orders.filter { it.status == OrderStatus.CANCELLED }
        }
    }

    val totalSpent = remember(orders) {
        orders.filter { it.status != OrderStatus.CANCELLED }.sumOf { it.total }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
    ) {
        // Top Header
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color.White,
            shadowElevation = 2.dp
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "My Past & Live Orders",
                            fontSize = 19.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = TextPrimaryLight
                        )
                        Text(
                            text = "View order status, item details & total cost",
                            fontSize = 12.sp,
                            color = TextSecondaryLight
                        )
                    }

                    if (orders.isNotEmpty()) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFFFFF3E0),
                            border = androidx.compose.foundation.BorderStroke(1.dp, ZaykaOrange.copy(alpha = 0.4f))
                        ) {
                            Text(
                                text = "${orders.size} Total",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = ZaykaOrange,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                if (orders.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(12.dp))

                    // Quick Summary Bar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFF4F6F8))
                            .padding(horizontal = 14.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Total Spending",
                            fontSize = 12.sp,
                            color = TextSecondaryLight,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "₹${totalSpent.toInt()}",
                            fontSize = 15.sp,
                            color = ZaykaRed,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Status Filter Tabs
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(OrderFilter.values()) { filter ->
                            val isSelected = selectedFilter == filter
                            val count = when (filter) {
                                OrderFilter.ALL -> orders.size
                                OrderFilter.LIVE -> orders.count { it.status != OrderStatus.DELIVERED && it.status != OrderStatus.CANCELLED }
                                OrderFilter.DELIVERED -> orders.count { it.status == OrderStatus.DELIVERED }
                                OrderFilter.CANCELLED -> orders.count { it.status == OrderStatus.CANCELLED }
                            }

                            Surface(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .clickable { selectedFilter = filter }
                                    .testTag("order_filter_${filter.name.lowercase()}"),
                                shape = RoundedCornerShape(20.dp),
                                color = if (isSelected) ZaykaRed else Color(0xFFF1F3F5),
                                border = if (isSelected) null else androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE4E4E7))
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = filter.title,
                                        fontSize = 12.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) Color.White else TextSecondaryLight
                                    )
                                    Spacer(modifier = Modifier.width(5.dp))
                                    Box(
                                        modifier = Modifier
                                            .size(16.dp)
                                            .clip(CircleShape)
                                            .background(if (isSelected) Color.White.copy(alpha = 0.25f) else Color.LightGray.copy(alpha = 0.4f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "$count",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) Color.White else TextPrimaryLight
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        if (orders.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFFFEBEE)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ReceiptLong,
                            contentDescription = null,
                            tint = ZaykaRed,
                            modifier = Modifier.size(40.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No Orders Placed Yet",
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = TextPrimaryLight
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Your placed orders will appear here with live tracking status, breakdown, and reorder options.",
                        fontSize = 13.sp,
                        color = TextSecondaryLight,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 20.dp)
                    )
                }
            }
        } else if (filteredOrders.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "No ${selectedFilter.title} Orders",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = TextPrimaryLight
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Try selecting 'All Orders' to view your complete history.",
                        fontSize = 12.sp,
                        color = TextSecondaryLight
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedButton(
                        onClick = { selectedFilter = OrderFilter.ALL },
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Show All Orders", fontSize = 12.sp, color = ZaykaRed)
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredOrders, key = { it.orderId }) { order ->
                    OrderHistoryCard(
                        order = order,
                        onTrack = { onTrackOrder(order.orderId) },
                        onReorder = {
                            viewModel.reorder(order, onReordered = onReorderClicked)
                        },
                        onRate = { rating ->
                            viewModel.submitRating(order.orderId, rating, "Great delicious food!")
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun OrderHistoryCard(
    order: OrderEntity,
    onTrack: () -> Unit,
    onReorder: () -> Unit,
    onRate: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val isLive = order.status != OrderStatus.DELIVERED && order.status != OrderStatus.CANCELLED
    val dateFormat = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
    val dateStr = dateFormat.format(Date(order.orderTimestamp))
    var isExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("order_card_${order.orderId}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header Row: Restaurant Name + Status Badge
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
                            .background(Color(0xFFFFEBEE)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Restaurant,
                            contentDescription = null,
                            tint = ZaykaRed,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Zayka Chicken Cafe",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = TextPrimaryLight
                        )
                        Text(
                            text = "Order #${order.orderNumber}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextMutedLight
                        )
                    }
                }

                // Status Badge
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = when (order.status) {
                        OrderStatus.DELIVERED -> Color(0xFFE8F5E9)
                        OrderStatus.CANCELLED -> Color(0xFFFFEBEE)
                        OrderStatus.OUT_FOR_DELIVERY -> Color(0xFFE3F2FD)
                        else -> Color(0xFFFFF3E0)
                    }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = when (order.status) {
                                OrderStatus.DELIVERED -> Icons.Default.CheckCircle
                                OrderStatus.CANCELLED -> Icons.Default.Cancel
                                OrderStatus.OUT_FOR_DELIVERY -> Icons.Default.DeliveryDining
                                else -> Icons.Default.HourglassTop
                            },
                            contentDescription = null,
                            tint = when (order.status) {
                                OrderStatus.DELIVERED -> VegGreen
                                OrderStatus.CANCELLED -> Color.Red
                                OrderStatus.OUT_FOR_DELIVERY -> Color(0xFF1976D2)
                                else -> ZaykaOrange
                            },
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = order.status.replace("_", " "),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = when (order.status) {
                                OrderStatus.DELIVERED -> VegGreen
                                OrderStatus.CANCELLED -> Color.Red
                                OrderStatus.OUT_FOR_DELIVERY -> Color(0xFF1976D2)
                                else -> ZaykaOrange
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Order Placed Date & Time
            Text(
                text = "Placed on $dateStr",
                fontSize = 11.sp,
                color = TextSecondaryLight
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Items Summary
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color(0xFFF9FAFB),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text(
                        text = "Ordered Items",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextMutedLight
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = order.itemsSummaryJson,
                        fontSize = 13.sp,
                        color = TextPrimaryLight,
                        lineHeight = 18.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Expandable Breakdown Details
            AnimatedVisibility(visible = isExpanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp)
                ) {
                    HorizontalDivider(color = Color(0xFFF1F3F5))
                    Spacer(modifier = Modifier.height(8.dp))

                    // Address
                    if (order.deliveryAddress.isNotBlank()) {
                        Row(
                            verticalAlignment = Alignment.Top,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = ZaykaRed,
                                modifier = Modifier
                                    .size(16.dp)
                                    .padding(top = 1.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Delivering to: ${order.deliveryAddress}",
                                fontSize = 11.sp,
                                color = TextSecondaryLight,
                                lineHeight = 15.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                    }

                    // Payment Method & Subtotal details
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Payment,
                                contentDescription = null,
                                tint = TextMutedLight,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Payment: ${order.paymentMethod}",
                                fontSize = 11.sp,
                                color = TextMutedLight
                            )
                        }

                        if (order.appliedCoupon.isNotBlank()) {
                            Text(
                                text = "Coupon '${order.appliedCoupon}' Applied",
                                fontSize = 11.sp,
                                color = VegGreen,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    if (order.discount > 0) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Discount Saved",
                                fontSize = 11.sp,
                                color = VegGreen
                            )
                            Text(
                                text = "-₹${order.discount.toInt()}",
                                fontSize = 11.sp,
                                color = VegGreen,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(color = Color(0xFFF1F3F5))
            Spacer(modifier = Modifier.height(8.dp))

            // Price & Details Toggle Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Total Cost",
                        fontSize = 11.sp,
                        color = TextMutedLight
                    )
                    Text(
                        text = "₹${order.total.toInt()}",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 16.sp,
                        color = ZaykaRed
                    )
                }

                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .clickable { isExpanded = !isExpanded }
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isExpanded) "Less details" else "View details",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextSecondaryLight
                    )
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        tint = TextSecondaryLight,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            // Rating or Action Buttons
            Spacer(modifier = Modifier.height(10.dp))

            if (isLive) {
                Button(
                    onClick = onTrack,
                    colors = ButtonDefaults.buttonColors(containerColor = ZaykaRed),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("track_order_btn_${order.orderId}")
                ) {
                    Icon(
                        imageVector = Icons.Default.DeliveryDining,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Track Live Order Status", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            } else if (order.status == OrderStatus.DELIVERED) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Star Rating
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "Rate: ", fontSize = 11.sp, color = TextSecondaryLight)
                        (1..5).forEach { star ->
                            Icon(
                                imageVector = if (star <= order.rating) Icons.Filled.Star else Icons.Outlined.Star,
                                contentDescription = "$star stars",
                                tint = if (star <= order.rating) ZaykaAmber else Color.LightGray,
                                modifier = Modifier
                                    .size(22.dp)
                                    .clickable { onRate(star) }
                            )
                        }
                    }

                    OutlinedButton(
                        onClick = onReorder,
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                        modifier = Modifier.testTag("reorder_btn_${order.orderId}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Replay,
                            contentDescription = null,
                            tint = ZaykaRed,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "Reorder", fontSize = 12.sp, color = ZaykaRed, fontWeight = FontWeight.Bold)
                    }
                }
            } else if (order.status == OrderStatus.CANCELLED) {
                OutlinedButton(
                    onClick = onReorder,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("reorder_cancelled_btn_${order.orderId}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Replay,
                        contentDescription = null,
                        tint = ZaykaRed,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Order Again", fontSize = 12.sp, color = ZaykaRed, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

