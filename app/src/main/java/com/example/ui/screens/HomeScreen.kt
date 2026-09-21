package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Discount
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.CartItem
import com.example.data.model.FoodItem
import com.example.ui.Coupon
import com.example.ui.ZaykaViewModel
import com.example.ui.components.BestsellerTag
import com.example.ui.components.CustomizationBottomSheet
import com.example.ui.components.FoodItemThumbnail
import com.example.ui.components.RatingPill
import com.example.ui.components.VegNonVegIcon
import com.example.ui.theme.BorderLight
import com.example.ui.theme.TextMutedLight
import com.example.ui.theme.TextPrimaryLight
import com.example.ui.theme.TextSecondaryLight
import com.example.ui.theme.VegGreen
import com.example.ui.theme.ZaykaAmber
import com.example.ui.theme.ZaykaOrange
import com.example.ui.theme.ZaykaRed

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: ZaykaViewModel,
    onNavigateToCart: () -> Unit,
    onNavigateToProfile: () -> Unit,
    modifier: Modifier = Modifier
) {
    val menuItems by viewModel.filteredMenuItems.collectAsState()
    val cartItems by viewModel.cartItems.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val isVegOnly by viewModel.isVegOnly.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedAddress by viewModel.selectedAddress.collectAsState()
    val isStoreOpen by viewModel.isStoreOpen.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()

    var customisingItem by remember { mutableStateOf<FoodItem?>(null) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val categories = listOf(
        "All",
        "Biryani",
        "Starters & Kebabs",
        "Curries & Gravy",
        "Combos & Thalis",
        "Breads & Rice",
        "Beverages & Desserts"
    )

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 90.dp)
        ) {
            // Location & Profile Top Bar
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("location_picker_row")
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = "Location",
                            tint = ZaykaRed,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "DELIVERING TO",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = ZaykaRed,
                                    letterSpacing = 0.5.sp
                                )
                                Icon(
                                    imageVector = Icons.Default.KeyboardArrowDown,
                                    contentDescription = "Select location",
                                    tint = ZaykaRed,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Text(
                                text = selectedAddress,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimaryLight,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    Surface(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .clickable { onNavigateToProfile() }
                            .testTag("profile_button"),
                        color = if (currentUser != null && !currentUser!!.isAnonymous) com.example.ui.theme.TertiaryLight else com.example.ui.theme.PrimaryLight,
                        shape = CircleShape
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            if (currentUser != null && !currentUser!!.isAnonymous) {
                                Image(
                                    painter = painterResource(id = R.drawable.ic_google_logo),
                                    contentDescription = "Profile",
                                    modifier = Modifier.size(20.dp)
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = "Profile",
                                    tint = ZaykaRed,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Search Bar & Veg Switch
            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { viewModel.setSearchQuery(it) },
                        placeholder = { Text("Search for Biryani, Butter Chicken, Tikka...") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search",
                                tint = TextSecondaryLight
                            )
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { viewModel.setSearchQuery("") }) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Clear",
                                        tint = TextSecondaryLight
                                    )
                                }
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = BorderLight,
                            focusedBorderColor = ZaykaRed,
                            unfocusedContainerColor = Color.White,
                            focusedContainerColor = Color.White
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("search_food_input")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            VegNonVegIcon(isVeg = true)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Pure Veg",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.sp,
                                color = TextPrimaryLight
                            )
                        }
                        Switch(
                            checked = isVegOnly,
                            onCheckedChange = { viewModel.toggleVegOnly() },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = VegGreen,
                                uncheckedThumbColor = Color.LightGray,
                                uncheckedTrackColor = Color(0xFFEEEEEE)
                            ),
                            modifier = Modifier.testTag("veg_only_switch")
                        )
                    }
                }
            }

            // Restaurant Hero Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column {
                        // Hero Food Banner
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.img_hero_biryani),
                                contentDescription = "Zayka Chicken Banner",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                            // Subtle gradient overlay for contrast
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        Brush.verticalGradient(
                                            colors = listOf(
                                                Color.Transparent,
                                                Color.Black.copy(alpha = 0.7f)
                                            )
                                        )
                                    )
                            )
                            // Store status badge
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isStoreOpen) com.example.ui.theme.Tertiary else com.example.ui.theme.Secondary,
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(12.dp)
                            ) {
                                Text(
                                    text = if (isStoreOpen) "● OPEN NOW" else "● CLOSED",
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }

                            // Bottom banner text
                            Row(
                                modifier = Modifier
                                    .align(Alignment.BottomStart)
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(RoundedCornerShape(10.dp)),
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
                                        text = "Zayka Chicken Cafe & Restaurant",
                                        color = Color.White,
                                        fontSize = 17.sp,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                    Text(
                                        text = "North Indian • Hyderabadi Dum Biryani • Mughlai",
                                        color = Color.White.copy(alpha = 0.9f),
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }

                        // Meta details row
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                RatingPill(rating = 4.8f, reviewCount = 2100)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Timer,
                                    contentDescription = "Delivery time",
                                    tint = ZaykaOrange,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "25-30 mins • 2.4 km",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextSecondaryLight
                                )
                            }
                            Text(
                                text = "Free delivery > ₹499",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = ZaykaRed
                            )
                        }
                    }
                }
            }

            // Offers Carousel
            item {
                Column(modifier = Modifier.padding(vertical = 6.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Discount,
                            contentDescription = null,
                            tint = ZaykaOrange,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "DEALS & DISCOUNTS",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = TextPrimaryLight,
                            letterSpacing = 0.5.sp
                        )
                    }

                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(viewModel.availableCoupons) { coupon ->
                            OfferCard(coupon = coupon, onApply = {
                                viewModel.applyCoupon(coupon)
                            })
                        }
                    }
                }
            }

            // Categories Filter Chips
            item {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(categories) { category ->
                        val isSelected = selectedCategory == category
                        FilterChip(
                            selected = isSelected,
                            onClick = { viewModel.selectCategory(category) },
                            label = {
                                Text(
                                    text = category,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    fontSize = 13.sp
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = ZaykaRed,
                                selectedLabelColor = Color.White,
                                containerColor = Color.White,
                                labelColor = TextPrimaryLight
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = isSelected,
                                borderColor = if (isSelected) ZaykaRed else BorderLight
                            ),
                            shape = RoundedCornerShape(20.dp),
                            modifier = Modifier.testTag("category_chip_$category")
                        )
                    }
                }
            }

            // Menu Section Header
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = if (selectedCategory == "All") "Recommended Dishes (${menuItems.size})"
                        else "$selectedCategory (${menuItems.size})",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 17.sp,
                        color = TextPrimaryLight
                    )
                }
            }

            // Menu Items List
            items(menuItems, key = { it.id }) { foodItem ->
                val existingCartItem = cartItems.find { it.foodItemId == foodItem.id }
                FoodItemCard(
                    item = foodItem,
                    cartItem = existingCartItem,
                    onAddClick = { customisingItem = foodItem },
                    onIncrement = {
                        if (existingCartItem != null) {
                            viewModel.updateCartQuantity(existingCartItem.cartId, existingCartItem.quantity + 1)
                        } else {
                            viewModel.addToCart(foodItem)
                        }
                    },
                    onDecrement = {
                        if (existingCartItem != null) {
                            viewModel.updateCartQuantity(existingCartItem.cartId, existingCartItem.quantity - 1)
                        }
                    }
                )
                HorizontalDivider(
                    color = Color(0xFFF1F3F5),
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }

        // Customization Bottom Sheet
        if (customisingItem != null) {
            CustomizationBottomSheet(
                item = customisingItem!!,
                sheetState = sheetState,
                onDismiss = { customisingItem = null },
                onConfirm = { portion, spice, addons, finalPrice ->
                    viewModel.addToCart(
                        foodItem = customisingItem!!,
                        portion = portion,
                        spiceLevel = spice,
                        addonsText = addons,
                        unitPrice = finalPrice
                    )
                    customisingItem = null
                }
            )
        }
    }
}

@Composable
fun OfferCard(
    coupon: Coupon,
    onApply: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .width(220.dp)
            .clickable { onApply() }
            .testTag("coupon_card_${coupon.code}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = com.example.ui.theme.PrimaryContainer),
        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(com.example.ui.theme.PrimaryLight, com.example.ui.theme.Primary)))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = ZaykaOrange
                ) {
                    Text(
                        text = coupon.code,
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = coupon.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = TextPrimaryLight
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = coupon.description,
                fontSize = 11.sp,
                color = TextSecondaryLight,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun FoodItemCard(
    item: FoodItem,
    cartItem: CartItem?,
    onAddClick: () -> Unit,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
            .testTag("food_item_${item.id}"),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Left Info Column
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(end = 12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                VegNonVegIcon(isVeg = item.isVeg)
                if (item.isBestseller) {
                    Spacer(modifier = Modifier.width(8.dp))
                    BestsellerTag()
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = item.name,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = TextPrimaryLight
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                Text(
                    text = "₹${item.price.toInt()}",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 15.sp,
                    color = TextPrimaryLight
                )
                if (item.originalPrice > item.price) {
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "₹${item.originalPrice.toInt()}",
                        fontSize = 12.sp,
                        color = TextMutedLight,
                        textDecoration = TextDecoration.LineThrough
                    )
                }
            }

            RatingPill(rating = item.rating, reviewCount = item.ratingCount)

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = item.description,
                fontSize = 12.sp,
                color = TextSecondaryLight,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 16.sp
            )
        }

        // Right Image & Add Button
        Box(
            modifier = Modifier
                .width(118.dp)
                .height(118.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            FoodItemThumbnail(
                imageSource = item.imageDrawableName,
                contentDescription = item.name,
                modifier = Modifier
                    .size(110.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .align(Alignment.TopCenter)
            )

            // Add or Stepper Button
            if (cartItem == null) {
                Surface(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .border(1.dp, ZaykaRed, RoundedCornerShape(8.dp))
                        .clickable(enabled = item.isAvailable) { onAddClick() }
                        .testTag("add_btn_${item.id}"),
                    color = Color.White,
                    shadowElevation = 3.dp
                ) {
                    Text(
                        text = if (item.isAvailable) "ADD +" else "SOLD OUT",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 13.sp,
                        color = if (item.isAvailable) ZaykaRed else Color.Gray,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)
                    )
                }
            } else {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = ZaykaRed,
                    shadowElevation = 3.dp,
                    modifier = Modifier.testTag("stepper_${item.id}")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
                    ) {
                        IconButton(
                            onClick = onDecrement,
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Remove,
                                contentDescription = "Decrease",
                                tint = Color.White,
                                modifier = Modifier.size(14.dp)
                            )
                        }

                        Text(
                            text = "${cartItem.quantity}",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )

                        IconButton(
                            onClick = onIncrement,
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Increase",
                                tint = Color.White,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
