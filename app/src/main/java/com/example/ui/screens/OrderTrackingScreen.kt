package com.example.ui.screens

import android.content.Intent
import android.net.Uri
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material.icons.filled.TwoWheeler
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.OrderEntity
import com.example.data.model.OrderStatus
import com.example.ui.ZaykaViewModel
import com.example.ui.components.DeliveryMapVisual
import com.example.ui.theme.BorderLight
import com.example.ui.theme.TextMutedLight
import com.example.ui.theme.TextPrimaryLight
import com.example.ui.theme.TextSecondaryLight
import com.example.ui.theme.VegGreen
import com.example.ui.theme.ZaykaAmber
import com.example.ui.theme.ZaykaOrange
import com.example.ui.theme.ZaykaRed

@Composable
fun OrderTrackingScreen(
    orderId: Long,
    viewModel: ZaykaViewModel,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val allOrders by viewModel.allOrders.collectAsState()
    val contactInfo by viewModel.restaurantContactInfo.collectAsState()
    val order = allOrders.find { it.orderId == orderId } ?: allOrders.firstOrNull()

    if (order == null) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No active order found")
        }
        return
    }

    val status = order.status

    val statusIndex = when (status) {
        OrderStatus.PLACED -> 1
        OrderStatus.CONFIRMED -> 2
        OrderStatus.PREPARING -> 3
        OrderStatus.OUT_FOR_DELIVERY -> 4
        OrderStatus.DELIVERED -> 5
        else -> 0
    }

    val headlineText = when (status) {
        OrderStatus.PLACED -> "Order Received by Restaurant"
        OrderStatus.CONFIRMED -> "Zayka Cafe Confirmed Your Order"
        OrderStatus.PREPARING -> "Chef is Preparing Your Feast 👨‍🍳"
        OrderStatus.OUT_FOR_DELIVERY -> "Rider is On The Way! 🛵"
        OrderStatus.DELIVERED -> "Delivered! Enjoy Your Meal 🎉"
        OrderStatus.CANCELLED -> "Order Cancelled"
        else -> "Tracking Order"
    }

    val subheadText = when (status) {
        OrderStatus.PLACED -> "Restaurant is reviewing your order details"
        OrderStatus.CONFIRMED -> "Your authentic dishes are being assigned to kitchen"
        OrderStatus.PREPARING -> "Fresh ingredients cooking in slow handi dum style"
        OrderStatus.OUT_FOR_DELIVERY -> "Delivery partner Ramesh Kumar is riding to your door"
        OrderStatus.DELIVERED -> "Delivered at ${order.deliveryAddress}"
        OrderStatus.CANCELLED -> "This order has been cancelled"
        else -> ""
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA)),
        contentPadding = PaddingValues(bottom = 40.dp)
    ) {
        // Top App Bar
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.White,
                shadowElevation = 2.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBackClick, modifier = Modifier.testTag("tracking_back_btn")) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = TextPrimaryLight
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "Live Order Tracking",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 17.sp,
                            color = TextPrimaryLight
                        )
                        Text(
                            text = "Order #${order.orderNumber}",
                            fontSize = 12.sp,
                            color = TextSecondaryLight
                        )
                    }
                }
            }
        }

        // Live Map Visual
        item {
            Box(modifier = Modifier.padding(16.dp)) {
                DeliveryMapVisual(
                    status = status,
                    etaMinutes = if (status == OrderStatus.DELIVERED) 0 else order.etaMinutes
                )
            }
        }

        // Status Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = headlineText,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 16.sp,
                                color = if (status == OrderStatus.DELIVERED) VegGreen else TextPrimaryLight
                            )
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = subheadText,
                                fontSize = 12.sp,
                                color = TextSecondaryLight
                            )
                        }

                        if (status != OrderStatus.DELIVERED && status != OrderStatus.CANCELLED) {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Color(0xFFFFEBEE)
                            ) {
                                Column(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "${order.etaMinutes}",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = ZaykaRed
                                    )
                                    Text(
                                        text = "MINS",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = ZaykaRed
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // 5-step timeline
                    TimelineStep(
                        title = "Order Placed",
                        subtitle = "Sent to restaurant",
                        isCompleted = statusIndex >= 1,
                        isCurrent = statusIndex == 1
                    )
                    TimelineStep(
                        title = "Order Confirmed",
                        subtitle = "Zayka Cafe accepted your meal",
                        isCompleted = statusIndex >= 2,
                        isCurrent = statusIndex == 2
                    )
                    TimelineStep(
                        title = "Food Preparing",
                        subtitle = "Handi cooking on slow flame",
                        isCompleted = statusIndex >= 3,
                        isCurrent = statusIndex == 3
                    )
                    TimelineStep(
                        title = "Out for Delivery",
                        subtitle = "Partner ${order.riderName} is riding to you",
                        isCompleted = statusIndex >= 4,
                        isCurrent = statusIndex == 4
                    )
                    TimelineStep(
                        title = "Delivered",
                        subtitle = "Order reached destination",
                        isCompleted = statusIndex >= 5,
                        isCurrent = statusIndex == 5,
                        isLast = true
                    )
                }
            }
        }

        // Rider Details Card (Visible when Out for Delivery or Preparing)
        item {
            if (status != OrderStatus.CANCELLED) {
                Spacer(modifier = Modifier.height(12.dp))
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFFEF9C3)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.TwoWheeler,
                                    contentDescription = "Rider",
                                    tint = ZaykaOrange,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = order.riderName,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = TextPrimaryLight
                                )
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = null,
                                        tint = ZaykaAmber,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(2.dp))
                                    Text(
                                        text = "${order.riderRating} • Sanitized & Masked",
                                        fontSize = 11.sp,
                                        color = TextSecondaryLight
                                    )
                                }
                            }
                        }

                        // Call Action
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFFE8F5E9),
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .clickable {
                                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${order.riderPhone}"))
                                    runCatching { context.startActivity(intent) }
                                }
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Call,
                                    contentDescription = "Call rider",
                                    tint = VegGreen,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Restaurant Direct Help Desk Card
        item {
            Spacer(modifier = Modifier.height(12.dp))
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.SupportAgent,
                                contentDescription = null,
                                tint = ZaykaRed,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Restaurant Help & Support",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = TextPrimaryLight
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = Color(0xFFFEFCE8)
                        ) {
                            Text(
                                text = contactInfo.operatingHours,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF854D0E),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${contactInfo.supportPhone}"))
                                runCatching { context.startActivity(intent) }
                            },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f),
                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 6.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = VegGreen)
                        ) {
                            Icon(Icons.Default.Call, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Call Restaurant", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = {
                                val cleanNum = contactInfo.whatsappNumber.replace("+", "").replace(" ", "").replace("-", "")
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/$cleanNum"))
                                runCatching { context.startActivity(intent) }
                            },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f),
                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 6.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF059669))
                        ) {
                            Icon(Icons.Default.Chat, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("WhatsApp Help", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Order Summary Details Card
        item {
            Spacer(modifier = Modifier.height(12.dp))
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Order Details",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = TextPrimaryLight
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = order.itemsSummaryJson,
                        fontSize = 13.sp,
                        color = TextSecondaryLight,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    HorizontalDivider(color = Color(0xFFF1F3F5))
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Amount Paid", fontSize = 13.sp, color = TextSecondaryLight)
                        Text(
                            text = "₹${order.total.toInt()} (${order.paymentMethod})",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimaryLight
                        )
                    }

                    if (order.appliedCoupon.isNotBlank()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Promo Applied", fontSize = 12.sp, color = VegGreen)
                            Text(
                                text = order.appliedCoupon,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = VegGreen
                            )
                        }
                    }

                    // Cancel Order Option (if still in Placed / Confirmed)
                    if (status == OrderStatus.PLACED || status == OrderStatus.CONFIRMED) {
                        Spacer(modifier = Modifier.height(14.dp))
                        OutlinedButton(
                            onClick = { viewModel.cancelOrder(order.orderId) },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("cancel_order_btn")
                        ) {
                            Text("Cancel Order", fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TimelineStep(
    title: String,
    subtitle: String,
    isCompleted: Boolean,
    isCurrent: Boolean,
    isLast: Boolean = false,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(22.dp)
                    .clip(CircleShape)
                    .background(
                        when {
                            isCompleted -> VegGreen
                            isCurrent -> ZaykaOrange
                            else -> Color(0xFFD3D8DD)
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isCompleted) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                } else if (isCurrent) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                    )
                }
            }

            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(28.dp)
                        .background(if (isCompleted) VegGreen else Color(0xFFE0E0E0))
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.padding(bottom = if (isLast) 0.dp else 12.dp)) {
            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = if (isCurrent || isCompleted) FontWeight.Bold else FontWeight.Medium,
                color = if (isCurrent || isCompleted) TextPrimaryLight else TextMutedLight
            )
            Text(
                text = subtitle,
                fontSize = 11.sp,
                color = TextSecondaryLight
            )
        }
    }
}
