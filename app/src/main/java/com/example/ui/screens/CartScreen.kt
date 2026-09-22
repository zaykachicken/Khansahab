package com.example.ui.screens

import android.app.Activity
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Discount
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.SecondaryIndicator
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.CartItem
import com.example.ui.Coupon
import com.example.ui.ZaykaViewModel
import com.example.ui.components.VegNonVegIcon
import com.example.ui.theme.BorderLight
import com.example.ui.theme.TextMutedLight
import com.example.ui.theme.TextPrimaryLight
import com.example.ui.theme.TextSecondaryLight
import com.example.ui.theme.VegGreen
import com.example.ui.theme.ZaykaAmber
import com.example.ui.theme.ZaykaOrange
import com.example.ui.theme.ZaykaRed

@Composable
fun CartScreen(
    viewModel: ZaykaViewModel,
    onBackClick: () -> Unit,
    onOrderPlaced: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val activity = context.findActivity()

    val currentUser by viewModel.currentUser.collectAsState()
    val authLoading by viewModel.authLoading.collectAsState()
    val authError by viewModel.authError.collectAsState()

    val isGoogleSignedIn = currentUser != null && !currentUser!!.isAnonymous && currentUser!!.email.isNotBlank()

    val cartItems by viewModel.cartItems.collectAsState()
    val appliedCoupon by viewModel.appliedCoupon.collectAsState()
    val deliveryType by viewModel.deliveryType.collectAsState()
    val deliveryInstruction by viewModel.deliveryInstruction.collectAsState()
    val selectedPaymentMethod by viewModel.selectedPaymentMethod.collectAsState()
    val selectedAddress by viewModel.selectedAddress.collectAsState()
    val activeDeals by viewModel.activeDeals.collectAsState()

    var couponInput by remember { mutableStateOf("") }
    var couponError by remember { mutableStateOf<String?>(null) }

    val subtotal = viewModel.calculateSubtotal(cartItems)
    val deliveryFee = viewModel.calculateDeliveryFee(cartItems, deliveryType)
    val packagingFee = viewModel.calculatePackagingFee(deliveryType)
    val tax = viewModel.calculateTax(subtotal)
    val discount = viewModel.calculateDiscount(subtotal)
    val grandTotal = viewModel.calculateGrandTotal(cartItems, deliveryType)

    if (cartItems.isEmpty()) {
        EmptyCartView(onExploreClick = onBackClick)
        return
    }

    Box(modifier = modifier.fillMaxSize().background(Color(0xFFF8F9FA))) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header Top Bar
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
                    IconButton(onClick = onBackClick, modifier = Modifier.testTag("cart_back_btn")) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = TextPrimaryLight
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "Zayka Chicken Cafe",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = TextPrimaryLight
                        )
                        Text(
                            text = "Sector 18, Noida • Instant Delivery",
                            fontSize = 11.sp,
                            color = TextSecondaryLight
                        )
                    }
                }
            }

            // Scrollable Content
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(top = 14.dp, bottom = 100.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Google Account Verification / Sign-In Required Card
                item {
                    if (!isGoogleSignedIn) {
                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFFFCCBC)),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                            modifier = Modifier.testTag("cart_signin_required_card")
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Surface(
                                        shape = CircleShape,
                                        color = Color(0xFFFFEBEE),
                                        modifier = Modifier.size(36.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(
                                                imageVector = Icons.Default.Lock,
                                                contentDescription = "Lock",
                                                tint = ZaykaRed,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                    }
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "Google Sign-In Required",
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 15.sp,
                                            color = TextPrimaryLight
                                        )
                                        Text(
                                            text = "Pehle Google Sign-In karein, fir order place hoga",
                                            fontSize = 11.sp,
                                            color = ZaykaRed,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = "To ensure live real-time GPS tracking and instant delivery confirmation, please sign in with your Google account.",
                                    fontSize = 12.sp,
                                    color = TextSecondaryLight,
                                    lineHeight = 16.sp
                                )

                                if (authError != null) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = Color(0xFFFFEBEE),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            text = authError ?: "",
                                            fontSize = 11.sp,
                                            color = Color.Red,
                                            modifier = Modifier.padding(8.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                // Main Google Sign-In Button
                                Surface(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(48.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .border(1.dp, BorderLight, RoundedCornerShape(10.dp))
                                        .clickable(enabled = !authLoading) {
                                             if (activity != null) {
                                                 viewModel.signInWithGoogle(activity, onSuccess = {})
                                             }
                                         }
                                         .testTag("cart_google_signin_btn"),
                                    shape = RoundedCornerShape(10.dp),
                                    color = Color.White,
                                    shadowElevation = 1.dp
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(horizontal = 14.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        if (authLoading) {
                                            CircularProgressIndicator(
                                                modifier = Modifier.size(18.dp),
                                                color = ZaykaRed,
                                                strokeWidth = 2.dp
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = "Signing in with Google...",
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = TextPrimaryLight
                                            )
                                        } else {
                                            Image(
                                                painter = painterResource(id = R.drawable.ic_google_logo),
                                                contentDescription = "Google",
                                                modifier = Modifier.size(20.dp)
                                            )
                                            Spacer(modifier = Modifier.width(10.dp))
                                            Text(
                                                text = "Sign in with Google",
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = TextPrimaryLight
                                            )
                                        }
                                    }
                                                                 }
                             }
                         }
                     }
                 }

                 // Delivery Address Card
                item {
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFFFEBEE)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (deliveryType == "DELIVERY") Icons.Default.LocationOn else Icons.Default.Home,
                                    contentDescription = "Address",
                                    tint = ZaykaRed,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (deliveryType == "DELIVERY") "Delivering to:" else "Pickup from Restaurant:",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextSecondaryLight
                                )
                                Text(
                                    text = if (deliveryType == "DELIVERY") selectedAddress else "Zayka Chicken Cafe, Near Metro Gate 2, Sector 18, Noida",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextPrimaryLight,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }

                // Order Items Summary Card
                item {
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = "Your Order Items (${cartItems.size})",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimaryLight,
                                modifier = Modifier.padding(bottom = 10.dp)
                            )

                            cartItems.forEachIndexed { index, cartItem ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp)
                                        .testTag("cart_item_${cartItem.cartId}"),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        modifier = Modifier.weight(1f),
                                        verticalAlignment = Alignment.Top
                                    ) {
                                        VegNonVegIcon(isVeg = cartItem.isVeg, modifier = Modifier.padding(top = 4.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column {
                                            Text(
                                                text = cartItem.name,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp,
                                                color = TextPrimaryLight
                                            )
                                            Text(
                                                text = "${cartItem.portion} • ${cartItem.spiceLevel}",
                                                fontSize = 11.sp,
                                                color = TextSecondaryLight
                                            )
                                            if (cartItem.addonsText.isNotBlank()) {
                                                Text(
                                                    text = "+ ${cartItem.addonsText}",
                                                    fontSize = 11.sp,
                                                    color = ZaykaOrange
                                                )
                                            }
                                            Text(
                                                text = "₹${cartItem.totalCost.toInt()}",
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.ExtraBold,
                                                color = TextPrimaryLight,
                                                modifier = Modifier.padding(top = 2.dp)
                                            )
                                        }
                                    }

                                    // Stepper
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        border = BorderLight.let { androidx.compose.foundation.BorderStroke(1.dp, ZaykaRed) },
                                        color = Color.White
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                        ) {
                                            IconButton(
                                                onClick = { viewModel.updateCartQuantity(cartItem.cartId, cartItem.quantity - 1) },
                                                modifier = Modifier.size(26.dp)
                                            ) {
                                                Icon(
                                                    imageVector = if (cartItem.quantity == 1) Icons.Default.DeleteOutline else Icons.Default.Remove,
                                                    contentDescription = "Decrease",
                                                    tint = ZaykaRed,
                                                    modifier = Modifier.size(14.dp)
                                                )
                                            }

                                            Text(
                                                text = "${cartItem.quantity}",
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = ZaykaRed,
                                                modifier = Modifier.padding(horizontal = 6.dp)
                                            )

                                            IconButton(
                                                onClick = { viewModel.updateCartQuantity(cartItem.cartId, cartItem.quantity + 1) },
                                                modifier = Modifier.size(26.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Add,
                                                    contentDescription = "Increase",
                                                    tint = ZaykaRed,
                                                    modifier = Modifier.size(14.dp)
                                                )
                                            }
                                        }
                                    }
                                }

                                if (index < cartItems.lastIndex) {
                                    HorizontalDivider(color = Color(0xFFF1F3F5), modifier = Modifier.padding(vertical = 4.dp))
                                }
                            }
                        }
                    }
                }

                // Delivery Instructions Field
                item {
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = "Delivery / Cooking Instructions",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimaryLight
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            OutlinedTextField(
                                value = deliveryInstruction,
                                onValueChange = { viewModel.setDeliveryInstruction(it) },
                                placeholder = { Text("E.g. Avoid ringing bell, leave at security gate...") },
                                singleLine = true,
                                shape = RoundedCornerShape(10.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    unfocusedBorderColor = BorderLight,
                                    focusedBorderColor = ZaykaRed,
                                    unfocusedContainerColor = Color(0xFFFDFDFD),
                                    focusedContainerColor = Color.White
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("delivery_instruction_input")
                            )
                        }
                    }
                }

                // Coupons & Discounts Card
                item {
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Discount,
                                    contentDescription = null,
                                    tint = ZaykaOrange,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Offers & Coupons",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimaryLight
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            if (appliedCoupon != null) {
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = Color(0xFFE8F5E9),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, VegGreen),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 12.dp, vertical = 10.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = null,
                                                tint = VegGreen,
                                                modifier = Modifier.size(18.dp)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Column {
                                                Text(
                                                    text = "'${appliedCoupon!!.code}' Applied!",
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 13.sp,
                                                    color = VegGreen
                                                )
                                                Text(
                                                    text = "Saved ₹${discount.toInt()} on this order",
                                                    fontSize = 11.sp,
                                                    color = TextSecondaryLight
                                                )
                                            }
                                        }
                                        IconButton(
                                            onClick = { viewModel.removeCoupon() },
                                            modifier = Modifier.size(24.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Close,
                                                contentDescription = "Remove coupon",
                                                tint = Color.Gray,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                }
                            } else {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    OutlinedTextField(
                                        value = couponInput,
                                        onValueChange = {
                                            couponInput = it.uppercase()
                                            couponError = null
                                        },
                                        placeholder = { Text("Enter Promo Code") },
                                        singleLine = true,
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier
                                            .weight(1f)
                                            .testTag("coupon_input_field")
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Button(
                                        onClick = {
                                            val found = activeDeals.find { it.code.equals(couponInput.trim(), ignoreCase = true) }
                                            if (found != null) {
                                                val ok = viewModel.applyCoupon(found)
                                                if (ok) {
                                                    couponInput = ""
                                                    couponError = null
                                                } else {
                                                    couponError = "Min order value ₹${found.minOrder.toInt()} required"
                                                }
                                            } else {
                                                couponError = "Invalid or expired coupon code"
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = ZaykaRed),
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier.testTag("apply_coupon_btn")
                                    ) {
                                        Text("APPLY", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    }
                                }

                                if (couponError != null) {
                                    Text(
                                        text = couponError!!,
                                        color = Color.Red,
                                        fontSize = 11.sp,
                                        modifier = Modifier.padding(top = 4.dp)
                                    )
                                }

                                // Quick apply chips
                                if (activeDeals.isNotEmpty()) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(top = 8.dp),
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        activeDeals.forEach { c ->
                                            Surface(
                                                shape = RoundedCornerShape(6.dp),
                                                color = Color(0xFFF1F3F5),
                                                modifier = Modifier.clickable {
                                                    val ok = viewModel.applyCoupon(c)
                                                    if (!ok) {
                                                        couponError = "Min order value ₹${c.minOrder.toInt()} required for ${c.code}"
                                                    } else {
                                                        couponError = null
                                                    }
                                                }
                                            ) {
                                                Text(
                                                    text = c.code,
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = ZaykaOrange,
                                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Payment Method Selector
                item {
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = "Select Payment Mode",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimaryLight,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )

                            val paymentMethods = listOf(
                                "UPI" to "Instant Pay with GPay, PhonePe, Paytm",
                                "COD" to "Cash on Delivery / Card on Delivery",
                                "CARD" to "Credit / Debit Card / Net Banking"
                            )

                            paymentMethods.forEach { (mode, desc) ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { viewModel.setPaymentMethod(mode) }
                                        .padding(vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        RadioButton(
                                            selected = selectedPaymentMethod == mode,
                                            onClick = { viewModel.setPaymentMethod(mode) },
                                            colors = RadioButtonDefaults.colors(selectedColor = ZaykaRed)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Column {
                                            Text(
                                                text = when (mode) {
                                                    "UPI" -> "📱 UPI (Recommended)"
                                                    "COD" -> "💵 Cash on Delivery"
                                                    else -> "💳 Cards & Netbanking"
                                                },
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = TextPrimaryLight
                                            )
                                            Text(
                                                text = desc,
                                                fontSize = 11.sp,
                                                color = TextSecondaryLight
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Bill Details Card
                item {
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = "Bill Summary",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimaryLight,
                                modifier = Modifier.padding(bottom = 10.dp)
                            )

                            BillRow(label = "Item Total", amount = "₹${subtotal.toInt()}")
                            BillRow(
                                label = "Delivery Fee",
                                amount = if (deliveryFee == 0.0) "FREE" else "₹${deliveryFee.toInt()}",
                                isHighlight = deliveryFee == 0.0
                            )
                            BillRow(label = "Restaurant Packaging", amount = "₹${packagingFee.toInt()}")
                            BillRow(label = "Govt Taxes & GST (5%)", amount = "₹${tax.toInt()}")

                            if (discount > 0.0) {
                                BillRow(
                                    label = "Coupon Discount (${appliedCoupon?.code})",
                                    amount = "-₹${discount.toInt()}",
                                    isHighlight = true
                                )
                            }

                            HorizontalDivider(color = Color(0xFFEEEEEE), modifier = Modifier.padding(vertical = 10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "To Pay",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = TextPrimaryLight
                                )
                                Text(
                                    text = "₹${grandTotal.toInt()}",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = ZaykaRed
                                )
                            }
                        }
                    }
                }
            }
        }

        // Fixed Sticky Bottom Bar
        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth(),
            color = Color.White,
            shadowElevation = 12.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "₹${grandTotal.toInt()}",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 20.sp,
                        color = TextPrimaryLight
                    )
                    Text(
                        text = "TOTAL AMOUNT",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextSecondaryLight,
                        letterSpacing = 0.5.sp
                    )
                }

                if (isGoogleSignedIn) {
                    Button(
                        onClick = {
                            viewModel.placeOrder(onSuccess = { orderId ->
                                onOrderPlaced(orderId)
                            })
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ZaykaRed),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 14.dp),
                        modifier = Modifier.testTag("place_order_button")
                    ) {
                        Text(
                            text = "PLACE ORDER ➔",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                } else {
                    Button(
                        onClick = {
                            if (activity != null) {
                                viewModel.signInWithGoogle(activity, onSuccess = {})
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B)),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
                        modifier = Modifier.testTag("google_login_before_order_btn")
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_google_logo),
                            contentDescription = "Google",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Google Sign-In to Order",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun BillRow(
    label: String,
    amount: String,
    isHighlight: Boolean = false,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = 13.sp,
            color = if (isHighlight) VegGreen else TextSecondaryLight,
            fontWeight = if (isHighlight) FontWeight.Bold else FontWeight.Normal
        )
        Text(
            text = amount,
            fontSize = 13.sp,
            color = if (isHighlight) VegGreen else TextPrimaryLight,
            fontWeight = if (isHighlight) FontWeight.Bold else FontWeight.SemiBold
        )
    }
}

@Composable
fun EmptyCartView(
    onExploreClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
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
                    imageVector = Icons.Default.ShoppingBag,
                    contentDescription = "Empty Cart",
                    tint = ZaykaRed,
                    modifier = Modifier.size(40.dp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Your Cart is Empty",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 18.sp,
                color = TextPrimaryLight
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Good food is waiting for you! Add some delicious dishes from Zayka Chicken Cafe.",
                fontSize = 13.sp,
                color = TextSecondaryLight,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                lineHeight = 18.sp
            )
            Spacer(modifier = Modifier.height(20.dp))
            Button(
                onClick = onExploreClick,
                colors = ButtonDefaults.buttonColors(containerColor = ZaykaRed),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.testTag("explore_menu_btn")
            ) {
                Text(
                    text = "Explore Menu",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }
    }
}
