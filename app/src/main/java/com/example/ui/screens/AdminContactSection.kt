package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.HelpCenter
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.QuestionAnswer
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.RestaurantContactEntity
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
fun HelpAndContactAdminTab(
    viewModel: ZaykaViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val liveContactInfo by viewModel.restaurantContactInfo.collectAsState()

    var restaurantName by remember { mutableStateOf(liveContactInfo.restaurantName) }
    var supportPhone by remember { mutableStateOf(liveContactInfo.supportPhone) }
    var whatsappNumber by remember { mutableStateOf(liveContactInfo.whatsappNumber) }
    var supportEmail by remember { mutableStateOf(liveContactInfo.supportEmail) }
    var operatingHours by remember { mutableStateOf(liveContactInfo.operatingHours) }
    var address by remember { mutableStateOf(liveContactInfo.address) }
    var fssaiNumber by remember { mutableStateOf(liveContactInfo.fssaiNumber) }
    var emergencyManagerContact by remember { mutableStateOf(liveContactInfo.emergencyManagerContact) }
    var preparationTimeMinutes by remember { mutableStateOf(liveContactInfo.preparationTimeMinutes.toString()) }
    var deliveryFeeAmount by remember { mutableStateOf(liveContactInfo.deliveryFeeAmount.toString()) }
    var helpDeskDescription by remember { mutableStateOf(liveContactInfo.helpDeskDescription) }

    var faq1Question by remember { mutableStateOf(liveContactInfo.faq1Question) }
    var faq1Answer by remember { mutableStateOf(liveContactInfo.faq1Answer) }
    var faq2Question by remember { mutableStateOf(liveContactInfo.faq2Question) }
    var faq2Answer by remember { mutableStateOf(liveContactInfo.faq2Answer) }
    var faq3Question by remember { mutableStateOf(liveContactInfo.faq3Question) }
    var faq3Answer by remember { mutableStateOf(liveContactInfo.faq3Answer) }

    var isSavedSuccessfully by remember { mutableStateOf(false) }
    var showPreviewFaqs by remember { mutableStateOf(true) }

    // Sync state when DB updates
    LaunchedEffect(liveContactInfo) {
        restaurantName = liveContactInfo.restaurantName
        supportPhone = liveContactInfo.supportPhone
        whatsappNumber = liveContactInfo.whatsappNumber
        supportEmail = liveContactInfo.supportEmail
        operatingHours = liveContactInfo.operatingHours
        address = liveContactInfo.address
        fssaiNumber = liveContactInfo.fssaiNumber
        emergencyManagerContact = liveContactInfo.emergencyManagerContact
        preparationTimeMinutes = liveContactInfo.preparationTimeMinutes.toString()
        deliveryFeeAmount = liveContactInfo.deliveryFeeAmount.toString()
        helpDeskDescription = liveContactInfo.helpDeskDescription

        faq1Question = liveContactInfo.faq1Question
        faq1Answer = liveContactInfo.faq1Answer
        faq2Question = liveContactInfo.faq2Question
        faq2Answer = liveContactInfo.faq2Answer
        faq3Question = liveContactInfo.faq3Question
        faq3Answer = liveContactInfo.faq3Answer
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA)),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Top Banner / Intro
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF263238)),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = CircleShape,
                        color = Color(0xFF37474F),
                        modifier = Modifier.size(44.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.SupportAgent,
                                contentDescription = "Support",
                                tint = ZaykaAmber,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Restaurant Help & Contact Settings",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                        Text(
                            text = "Manage customer helpline, WhatsApp, email, timings, and FAQs shown across the app.",
                            fontSize = 11.sp,
                            color = Color(0xFFB0BEC5),
                            lineHeight = 15.sp
                        )
                    }
                }
            }
        }

        // Save Status Feedback
        if (isSavedSuccessfully) {
            item {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFFE8F5E9),
                    border = BorderStroke(1.dp, Color(0xFFA5D6A7)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Saved",
                            tint = VegGreen,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Help & Contact information updated and published to all users!",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2E7D32)
                        )
                    }
                }
            }
        }

        // Section 1: Helpline & Direct Contacts
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Call,
                            contentDescription = null,
                            tint = ZaykaRed,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Direct Helplines & Communication",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = TextPrimaryLight
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = restaurantName,
                        onValueChange = {
                            restaurantName = it
                            isSavedSuccessfully = false
                        },
                        label = { Text("Restaurant / Brand Name") },
                        leadingIcon = { Icon(Icons.Default.Restaurant, contentDescription = null, tint = ZaykaRed) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("admin_contact_name_field"),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ZaykaRed,
                            unfocusedBorderColor = BorderLight
                        ),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = supportPhone,
                        onValueChange = {
                            supportPhone = it
                            isSavedSuccessfully = false
                        },
                        label = { Text("Customer Helpline / Phone (Direct Call)") },
                        leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = VegGreen) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("admin_contact_phone_field"),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ZaykaRed,
                            unfocusedBorderColor = BorderLight
                        ),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = whatsappNumber,
                        onValueChange = {
                            whatsappNumber = it
                            isSavedSuccessfully = false
                        },
                        label = { Text("WhatsApp Support Number") },
                        leadingIcon = { Icon(Icons.Default.SupportAgent, contentDescription = null, tint = VegGreen) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("admin_contact_whatsapp_field"),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ZaykaRed,
                            unfocusedBorderColor = BorderLight
                        ),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = supportEmail,
                        onValueChange = {
                            supportEmail = it
                            isSavedSuccessfully = false
                        },
                        label = { Text("Customer Support Email") },
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = ZaykaOrange) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("admin_contact_email_field"),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ZaykaRed,
                            unfocusedBorderColor = BorderLight
                        ),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = emergencyManagerContact,
                        onValueChange = {
                            emergencyManagerContact = it
                            isSavedSuccessfully = false
                        },
                        label = { Text("Kitchen / Manager Direct Line") },
                        leadingIcon = { Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = ZaykaAmber) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("admin_contact_manager_field"),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ZaykaRed,
                            unfocusedBorderColor = BorderLight
                        ),
                        singleLine = true
                    )
                }
            }
        }

        // Section 2: Help Desk Timings, Address & Food Safety License
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = null,
                            tint = ZaykaOrange,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Operating Hours, Location & License",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = TextPrimaryLight
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = operatingHours,
                        onValueChange = {
                            operatingHours = it
                            isSavedSuccessfully = false
                        },
                        label = { Text("Help Desk & Kitchen Operating Hours") },
                        leadingIcon = { Icon(Icons.Default.Schedule, contentDescription = null, tint = ZaykaOrange) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("admin_contact_hours_field"),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ZaykaRed,
                            unfocusedBorderColor = BorderLight
                        ),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = address,
                        onValueChange = {
                            address = it
                            isSavedSuccessfully = false
                        },
                        label = { Text("Restaurant Physical Address") },
                        leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null, tint = ZaykaRed) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("admin_contact_address_field"),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ZaykaRed,
                            unfocusedBorderColor = BorderLight
                        ),
                        maxLines = 3
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = fssaiNumber,
                        onValueChange = {
                            fssaiNumber = it
                            isSavedSuccessfully = false
                        },
                        label = { Text("FSSAI Food Safety License Number") },
                        leadingIcon = { Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = VegGreen) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("admin_contact_fssai_field"),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ZaykaRed,
                            unfocusedBorderColor = BorderLight
                        ),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = helpDeskDescription,
                        onValueChange = {
                            helpDeskDescription = it
                            isSavedSuccessfully = false
                        },
                        label = { Text("Customer Support Welcome Message & Guarantee") },
                        leadingIcon = { Icon(Icons.Default.Info, contentDescription = null, tint = TextSecondaryLight) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("admin_contact_description_field"),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ZaykaRed,
                            unfocusedBorderColor = BorderLight
                        ),
                        maxLines = 4
                    )
                }
            }
        }

        // Section: Order Prep Time & Delivery Fees
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = null,
                            tint = ZaykaRed,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Order Preparation & Delivery Fee Setup",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = TextPrimaryLight
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = preparationTimeMinutes,
                        onValueChange = {
                            preparationTimeMinutes = it
                            isSavedSuccessfully = false
                        },
                        label = { Text("Order Preparing Time (Minutes)") },
                        leadingIcon = { Icon(Icons.Default.Schedule, contentDescription = null, tint = ZaykaRed) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("admin_prep_time_field"),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ZaykaRed,
                            unfocusedBorderColor = BorderLight
                        ),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = deliveryFeeAmount,
                        onValueChange = {
                            deliveryFeeAmount = it
                            isSavedSuccessfully = false
                        },
                        label = { Text("Standard Delivery Fee (₹) [Orders ₹100+ Get Free Delivery]") },
                        leadingIcon = { Icon(Icons.Default.Schedule, contentDescription = null, tint = VegGreen) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("admin_delivery_fee_field"),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ZaykaRed,
                            unfocusedBorderColor = BorderLight
                        ),
                        singleLine = true
                    )
                }
            }
        }

        // Section 3: Customer Help FAQs Management
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.QuestionAnswer,
                                contentDescription = null,
                                tint = ZaykaAmber,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Help Desk FAQs Management",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = TextPrimaryLight
                            )
                        }

                        IconButton(onClick = { showPreviewFaqs = !showPreviewFaqs }) {
                            Icon(
                                imageVector = if (showPreviewFaqs) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                contentDescription = "Toggle FAQs"
                            )
                        }
                    }

                    AnimatedVisibility(visible = showPreviewFaqs) {
                        Column {
                            Spacer(modifier = Modifier.height(12.dp))

                            // FAQ 1
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Color(0xFFF1F5F9),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(
                                        text = "FAQ #1 (Order Tracking & Delivery)",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextSecondaryLight
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    OutlinedTextField(
                                        value = faq1Question,
                                        onValueChange = { faq1Question = it; isSavedSuccessfully = false },
                                        label = { Text("Question 1") },
                                        modifier = Modifier.fillMaxWidth(),
                                        singleLine = true
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    OutlinedTextField(
                                        value = faq1Answer,
                                        onValueChange = { faq1Answer = it; isSavedSuccessfully = false },
                                        label = { Text("Answer 1") },
                                        modifier = Modifier.fillMaxWidth(),
                                        maxLines = 3
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // FAQ 2
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Color(0xFFF1F5F9),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(
                                        text = "FAQ #2 (Delivery Time & Coverage)",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextSecondaryLight
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    OutlinedTextField(
                                        value = faq2Question,
                                        onValueChange = { faq2Question = it; isSavedSuccessfully = false },
                                        label = { Text("Question 2") },
                                        modifier = Modifier.fillMaxWidth(),
                                        singleLine = true
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    OutlinedTextField(
                                        value = faq2Answer,
                                        onValueChange = { faq2Answer = it; isSavedSuccessfully = false },
                                        label = { Text("Answer 2") },
                                        modifier = Modifier.fillMaxWidth(),
                                        maxLines = 3
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // FAQ 3
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Color(0xFFF1F5F9),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(
                                        text = "FAQ #3 (Cancellations & Modifications)",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextSecondaryLight
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    OutlinedTextField(
                                        value = faq3Question,
                                        onValueChange = { faq3Question = it; isSavedSuccessfully = false },
                                        label = { Text("Question 3") },
                                        modifier = Modifier.fillMaxWidth(),
                                        singleLine = true
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    OutlinedTextField(
                                        value = faq3Answer,
                                        onValueChange = { faq3Answer = it; isSavedSuccessfully = false },
                                        label = { Text("Answer 3") },
                                        modifier = Modifier.fillMaxWidth(),
                                        maxLines = 3
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Section 4: Live Customer Card Preview
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "👀 Live Customer Preview",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 13.sp,
                            color = TextPrimaryLight
                        )
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = Color(0xFFE0F2FE)
                        ) {
                            Text(
                                text = "REAL-TIME PREVIEW",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0369A1),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = restaurantName.ifBlank { "Zayka Chicken Cafe" },
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimaryLight
                    )
                    Text(
                        text = helpDeskDescription.ifBlank { "Direct customer assistance and fast resolution." },
                        fontSize = 11.sp,
                        color = TextSecondaryLight,
                        lineHeight = 15.sp,
                        modifier = Modifier.padding(top = 2.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Quick Call Test button
                        OutlinedButton(
                            onClick = {
                                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$supportPhone"))
                                runCatching { context.startActivity(intent) }
                            },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = VegGreen)
                        ) {
                            Icon(Icons.Default.Call, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Test Call", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        // Quick WhatsApp Test button
                        OutlinedButton(
                            onClick = {
                                val cleanNum = whatsappNumber.replace("+", "").replace(" ", "").replace("-", "")
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/$cleanNum"))
                                runCatching { context.startActivity(intent) }
                            },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF059669))
                        ) {
                            Icon(Icons.Default.SupportAgent, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Test WhatsApp", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Action Buttons: Save and Reset
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        restaurantName = "Zayka Chicken Cafe"
                        supportPhone = "+91 98765 12345"
                        whatsappNumber = "+91 98765 12345"
                        supportEmail = "help@zaykacafe.com"
                        operatingHours = "10:00 AM - 11:30 PM (Mon-Sun)"
                        address = "Near Metro Gate 2, Sector 18, Noida, UP - 201301"
                        fssaiNumber = "12722055000492"
                        emergencyManagerContact = "+91 98111 22334"
                        preparationTimeMinutes = "25"
                        deliveryFeeAmount = "20.0"
                        helpDeskDescription = "We are committed to serving authentic chicken delicacies and ensuring prompt doorstep delivery. Reach us anytime for order inquiries, special bulk catering, or delivery status."
                        faq1Question = "How can I track my live order?"
                        faq1Answer = "Go to the Orders tab and tap 'Track Live Order' to see real-time updates directly from our kitchen."
                        faq2Question = "What is the average delivery time?"
                        faq2Answer = "Most orders are delivered within 30-40 minutes from our Sector 18 kitchen."
                        faq3Question = "How do I cancel or modify my order?"
                        faq3Answer = "Call our direct restaurant helpline or message us on WhatsApp immediately before kitchen preparation begins."
                        isSavedSuccessfully = false
                    },
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("admin_reset_contact_btn")
                ) {
                    Text("Reset Defaults", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }

                Button(
                    onClick = {
                        val updatedInfo = RestaurantContactEntity(
                            id = 1,
                            restaurantName = restaurantName.trim().ifBlank { "Zayka Chicken Cafe" },
                            supportPhone = supportPhone.trim().ifBlank { "+91 98765 12345" },
                            whatsappNumber = whatsappNumber.trim().ifBlank { "+91 98765 12345" },
                            supportEmail = supportEmail.trim().ifBlank { "help@zaykacafe.com" },
                            operatingHours = operatingHours.trim().ifBlank { "10:00 AM - 11:30 PM (Mon-Sun)" },
                            address = address.trim().ifBlank { "Near Metro Gate 2, Sector 18, Noida, UP - 201301" },
                            fssaiNumber = fssaiNumber.trim().ifBlank { "12722055000492" },
                            emergencyManagerContact = emergencyManagerContact.trim().ifBlank { "+91 98111 22334" },
                            preparationTimeMinutes = preparationTimeMinutes.trim().toIntOrNull() ?: 25,
                            deliveryFeeAmount = deliveryFeeAmount.trim().toDoubleOrNull() ?: 20.0,
                            helpDeskDescription = helpDeskDescription.trim().ifBlank { "We are committed to serving authentic chicken delicacies and ensuring prompt doorstep delivery." },
                            faq1Question = faq1Question.trim(),
                            faq1Answer = faq1Answer.trim(),
                            faq2Question = faq2Question.trim(),
                            faq2Answer = faq2Answer.trim(),
                            faq3Question = faq3Question.trim(),
                            faq3Answer = faq3Answer.trim()
                        )
                        viewModel.updateRestaurantContactInfo(updatedInfo)
                        isSavedSuccessfully = true
                        Toast.makeText(context, "Restaurant Help & Contact Info updated!", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ZaykaRed),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .weight(1.5f)
                        .height(48.dp)
                        .testTag("admin_save_contact_btn")
                ) {
                    Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Save & Publish Info", fontSize = 13.sp, fontWeight = FontWeight.ExtraBold)
                }
            }
        }
    }
}
