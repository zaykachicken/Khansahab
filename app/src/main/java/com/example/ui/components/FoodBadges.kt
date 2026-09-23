package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.ui.theme.NonVegRed
import com.example.ui.theme.VegGreen
import com.example.ui.theme.ZaykaAmber

@Composable
fun VegNonVegIcon(
    isVeg: Boolean,
    modifier: Modifier = Modifier
) {
    val borderColor = if (isVeg) VegGreen else NonVegRed
    val innerColor = if (isVeg) VegGreen else NonVegRed

    Box(
        modifier = modifier
            .testTag(if (isVeg) "badge_veg" else "badge_non_veg")
            .size(16.dp)
            .border(1.5.dp, borderColor, RoundedCornerShape(3.dp))
            .padding(3.dp),
        contentAlignment = Alignment.Center
    ) {
        if (isVeg) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(innerColor)
            )
        } else {
            // Non-veg red triangle or square dot
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(innerColor)
            )
        }
    }
}

@Composable
fun RatingPill(
    rating: Float,
    reviewCount: Int? = null,
    modifier: Modifier = Modifier
) {
    val bg = when {
        rating >= 4.0f -> com.example.ui.theme.Tertiary // #16A34A (Green)
        rating >= 3.5f -> com.example.ui.theme.Primary // #E11D48 (Crimson Red)
        else -> com.example.ui.theme.Secondary // #BE123C (Wine Red)
    }

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(bg)
            .padding(horizontal = 6.dp, vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = String.format(java.util.Locale.US, "%.1f", rating),
            color = Color.White,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.width(2.dp))
        Icon(
            imageVector = Icons.Filled.Star,
            contentDescription = "Rating star",
            tint = Color.White,
            modifier = Modifier.size(10.dp)
        )
        if (reviewCount != null) {
            Spacer(modifier = Modifier.width(3.dp))
            Text(
                text = "($reviewCount)",
                color = Color.White.copy(alpha = 0.85f),
                fontSize = 10.sp
            )
        }
    }
}

@Composable
fun BestsellerTag(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .background(com.example.ui.theme.PrimaryLight)
            .border(0.8.dp, com.example.ui.theme.Primary, RoundedCornerShape(4.dp))
            .padding(horizontal = 6.dp, vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Filled.Star,
            contentDescription = null,
            tint = com.example.ui.theme.Primary,
            modifier = Modifier.size(10.dp)
        )
        Spacer(modifier = Modifier.width(3.dp))
        Text(
            text = "BESTSELLER",
            color = com.example.ui.theme.PrimaryDark,
            fontSize = 9.sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 0.5.sp
        )
    }
}

@Composable
fun FoodItemThumbnail(
    imageSource: String,
    contentDescription: String,
    modifier: Modifier = Modifier
) {
    if (imageSource.startsWith("http://") || imageSource.startsWith("https://") || imageSource.startsWith("content://") || imageSource.startsWith("file://")) {
        AsyncImage(
            model = imageSource,
            contentDescription = contentDescription,
            contentScale = ContentScale.Crop,
            modifier = modifier
        )
    } else {
        val resId = when (imageSource) {
            "img_tandoori_platter" -> R.drawable.img_tandoori_platter
            "img_butter_chicken" -> R.drawable.img_butter_chicken
            "img_chicken_tikka" -> R.drawable.img_chicken_tikka
            "img_paneer_tikka" -> R.drawable.img_paneer_tikka
            else -> R.drawable.img_hero_biryani
        }
        Image(
            painter = painterResource(id = resId),
            contentDescription = contentDescription,
            contentScale = ContentScale.Crop,
            modifier = modifier
        )
    }
}
