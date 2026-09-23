package com.example.ui.screens

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.ZaykaViewModel
import com.example.ui.theme.BorderLight
import com.example.ui.theme.TextMutedLight
import com.example.ui.theme.TextPrimaryLight
import com.example.ui.theme.TextSecondaryLight
import com.example.ui.theme.ZaykaAmber
import com.example.ui.theme.ZaykaOrange
import com.example.ui.theme.ZaykaRed
import com.example.ui.theme.ZaykaRedDark

fun Context.findActivity(): Activity? {
    var ctx = this
    while (ctx is ContextWrapper) {
        if (ctx is Activity) return ctx
        ctx = ctx.baseContext
    }
    return null
}

@Composable
fun LoginScreen(
    viewModel: ZaykaViewModel,
    onLoginSuccess: () -> Unit,
    onContinueAsGuest: () -> Unit,
    onBackClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val activity = context.findActivity()

    val authLoading by viewModel.authLoading.collectAsState()
    val authError by viewModel.authError.collectAsState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
    ) {
        // Decorative Top Gradient Arc
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            com.example.ui.theme.PrimaryDark,
                            com.example.ui.theme.Primary,
                            Color(0xFFFB7185)
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Bar with optional Back and Guest actions
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (onBackClick != null) {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.2f))
                            .testTag("login_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                } else {
                    Spacer(modifier = Modifier.width(44.dp))
                }

                TextButton(
                    onClick = {
                        viewModel.continueAsGuest(onSuccess = onContinueAsGuest)
                    },
                    modifier = Modifier.testTag("skip_login_button")
                ) {
                    Text(
                        text = "Skip for now",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Branding Emblem
            Surface(
                modifier = Modifier
                    .size(90.dp)
                    .clip(RoundedCornerShape(22.dp)),
                color = Color.White,
                shadowElevation = 6.dp
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_zayka_logo),
                    contentDescription = "Zayka Logo Emblem",
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(6.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "ZAYKA",
                fontSize = 32.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 2.sp,
                color = Color.White
            )

            Text(
                text = "Authentic Flavors • Delivered Fast",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White.copy(alpha = 0.9f)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Feature Highlights Row
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(horizontal = 20.dp)
            ) {
                FeatureBadge(icon = Icons.Filled.ElectricBolt, text = "30m Delivery")
                FeatureBadge(icon = Icons.Filled.Star, text = "4.8★ Rated")
                FeatureBadge(icon = Icons.Filled.Security, text = "100% Safe")
            }

            Spacer(modifier = Modifier.height(24.dp))

             // Authentication Container Card
            // Authentication Container Card (Google Authentication & Phone Number with OTP)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                var phoneInput by remember { mutableStateOf("") }
                var otpInput by remember { mutableStateOf("") }
                var isOtpSent by remember { mutableStateOf(false) }
                var generatedOtp by remember { mutableStateOf("") }

                val inputColors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                    focusedTextColor = TextPrimaryLight,
                    unfocusedTextColor = TextPrimaryLight,
                    focusedLabelColor = com.example.ui.theme.Primary,
                    unfocusedLabelColor = TextMutedLight,
                    focusedBorderColor = com.example.ui.theme.Primary,
                    unfocusedBorderColor = BorderLight,
                    cursorColor = com.example.ui.theme.Primary,
                    focusedContainerColor = Color(0xFFF8F9FA),
                    unfocusedContainerColor = Color(0xFFF8F9FA)
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Sign in to Zayka",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextPrimaryLight
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Unlock live tracking, saved addresses, member-exclusive offers & fast reordering.",
                        fontSize = 13.sp,
                        color = TextSecondaryLight,
                        textAlign = TextAlign.Center,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Error Alert Banner if sign in failed
                    AnimatedVisibility(
                        visible = authError != null,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp)
                                .testTag("login_error_banner"),
                            shape = RoundedCornerShape(12.dp),
                            color = com.example.ui.theme.SecondaryLight,
                            border = androidx.compose.foundation.BorderStroke(1.dp, com.example.ui.theme.Secondary)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Info,
                                    contentDescription = "Error Info",
                                    tint = com.example.ui.theme.Secondary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = authError ?: "",
                                    fontSize = 12.sp,
                                    color = com.example.ui.theme.SecondaryDark,
                                    modifier = Modifier.weight(1f)
                                )
                                IconButton(
                                    onClick = { viewModel.clearAuthError() },
                                    modifier = Modifier
                                        .size(24.dp)
                                        .testTag("dismiss_error_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Close,
                                        contentDescription = "Dismiss error",
                                        tint = com.example.ui.theme.SecondaryDark,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }

                    // --- SECTION 1: GOOGLE AUTHENTICATION ---
                    OutlinedButton(
                        onClick = {
                            val act = activity ?: (context as? Activity)
                            if (act != null) {
                                viewModel.signInWithGoogle(
                                    activity = act,
                                    onSuccess = onLoginSuccess,
                                    onFailure = { errorMsg ->
                                        viewModel.setAuthError(errorMsg)
                                    }
                                )
                            } else {
                                viewModel.setAuthError("Google Sign-In requires an active window context.")
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("google_auth_button"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White),
                        border = androidx.compose.foundation.BorderStroke(1.2.dp, Color(0xFFDADCE0)),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 1.dp)
                    ) {
                        GoogleLogoIcon()
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Continue with Google",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF3C4043)
                        )
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Divider: OR PHONE OTP
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        HorizontalDivider(modifier = Modifier.weight(1f), color = Color(0xFFE0E0E0))
                        Text(
                            text = "  OR WITH PHONE OTP  ",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextMutedLight
                        )
                        HorizontalDivider(modifier = Modifier.weight(1f), color = Color(0xFFE0E0E0))
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // --- SECTION 2: PHONE NUMBER WITH OTP ---
                    OutlinedTextField(
                        value = phoneInput,
                        onValueChange = { input ->
                            if (input.all { it.isDigit() || it == '+' || it == ' ' }) {
                                phoneInput = input
                            }
                        },
                        label = { Text("Mobile Number") },
                        placeholder = { Text("e.g. 9876543210") },
                        leadingIcon = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(start = 12.dp, end = 6.dp)
                            ) {
                                Icon(Icons.Default.Phone, contentDescription = null, tint = com.example.ui.theme.Primary, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("+91", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimaryLight)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("phone_number_input"),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        colors = inputColors
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    if (!isOtpSent) {
                        Button(
                            onClick = {
                                val cleaned = phoneInput.replace("+91", "").replace(" ", "").trim()
                                if (cleaned.length < 10) {
                                    viewModel.setAuthError("Please enter a valid 10-digit mobile number.")
                                    return@Button
                                }
                                val code = "123456"
                                generatedOtp = code
                                isOtpSent = true
                                otpInput = code
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .testTag("btn_send_otp"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = com.example.ui.theme.Primary
                            ),
                            enabled = phoneInput.isNotBlank() && !authLoading
                        ) {
                            Text("Send OTP via SMS", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    } else {
                        // OTP Sent notification banner
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFFE8F5E9),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF2E7D32), modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "OTP sent: $generatedOtp (Auto-filled)",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF2E7D32)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = otpInput,
                            onValueChange = { if (it.length <= 6 && it.all { ch -> ch.isDigit() }) otpInput = it },
                            label = { Text("6-Digit OTP Code") },
                            placeholder = { Text("123456") },
                            leadingIcon = {
                                Icon(Icons.Default.Lock, contentDescription = null, tint = com.example.ui.theme.Primary, modifier = Modifier.size(18.dp))
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("phone_otp_input"),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true,
                            colors = inputColors
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = {
                                if (otpInput.length != 6) {
                                    viewModel.setAuthError("Please enter the 6-digit OTP code.")
                                    return@Button
                                }
                                val formatted = if (phoneInput.startsWith("+91")) phoneInput else "+91 $phoneInput"
                                viewModel.signInWithPhoneNumber(formatted, otpInput, onSuccess = onLoginSuccess)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("phone_auth_submit_button"),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = com.example.ui.theme.Primary
                            ),
                            enabled = !authLoading
                        ) {
                            if (authLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(22.dp),
                                    color = Color.White,
                                    strokeWidth = 2.5.dp
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text("Verifying OTP...", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            } else {
                                Text("Verify & Sign In", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }

                        TextButton(
                            onClick = {
                                isOtpSent = false
                                otpInput = ""
                            },
                            modifier = Modifier.padding(top = 2.dp)
                        ) {
                            Text("Change Phone Number", fontSize = 12.sp, color = com.example.ui.theme.Primary)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Continue as Guest Button
                    OutlinedButton(
                        onClick = {
                            viewModel.continueAsGuest(onSuccess = onContinueAsGuest)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("continue_as_guest_button"),
                        shape = RoundedCornerShape(14.dp),
                        colors = androidx.compose.material3.ButtonDefaults.outlinedButtonColors(
                            contentColor = TextPrimaryLight
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.2.dp, BorderLight)
                    ) {
                        Text(
                            text = "Continue as Guest",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Footer / Legal notice
            Text(
                text = "By signing in, you agree to Zayka's\nTerms of Service and Privacy Policy",
                fontSize = 11.sp,
                color = TextSecondaryLight,
                textAlign = TextAlign.Center,
                lineHeight = 16.sp,
                modifier = Modifier.padding(horizontal = 32.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun GoogleLogoIcon(modifier: Modifier = Modifier) {
    androidx.compose.foundation.Canvas(modifier = modifier.size(20.dp)) {
        val w = size.width
        val h = size.height
        val stroke = 3.2.dp.toPx()
        val center = androidx.compose.ui.geometry.Offset(w / 2f, h / 2f)

        // Red top arc
        drawArc(
            color = Color(0xFFEA4335),
            startAngle = 180f,
            sweepAngle = 100f,
            useCenter = false,
            style = androidx.compose.ui.graphics.drawscope.Stroke(width = stroke)
        )
        // Yellow left arc
        drawArc(
            color = Color(0xFFFBBC05),
            startAngle = 120f,
            sweepAngle = 60f,
            useCenter = false,
            style = androidx.compose.ui.graphics.drawscope.Stroke(width = stroke)
        )
        // Green bottom arc
        drawArc(
            color = Color(0xFF34A853),
            startAngle = 20f,
            sweepAngle = 100f,
            useCenter = false,
            style = androidx.compose.ui.graphics.drawscope.Stroke(width = stroke)
        )
        // Blue right arc
        drawArc(
            color = Color(0xFF4285F4),
            startAngle = 280f,
            sweepAngle = 60f,
            useCenter = false,
            style = androidx.compose.ui.graphics.drawscope.Stroke(width = stroke)
        )
        // Blue crossbar
        drawLine(
            color = Color(0xFF4285F4),
            start = androidx.compose.ui.geometry.Offset(center.x - 1f, center.y),
            end = androidx.compose.ui.geometry.Offset(w - stroke / 2f, center.y),
            strokeWidth = stroke
        )
    }
}

@Composable
private fun FeatureBadge(
    icon: ImageVector,
    text: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(50),
        color = Color.White.copy(alpha = 0.2f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = text,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
        }
    }
}
