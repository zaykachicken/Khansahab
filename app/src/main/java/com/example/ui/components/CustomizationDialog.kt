package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateListOf
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
import com.example.data.model.FoodItem
import com.example.ui.theme.TextMutedLight
import com.example.ui.theme.TextPrimaryLight
import com.example.ui.theme.TextSecondaryLight
import com.example.ui.theme.ZaykaRed

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomizationBottomSheet(
    item: FoodItem,
    sheetState: SheetState,
    onDismiss: () -> Unit,
    onConfirm: (portion: String, spice: String, addons: String, finalPrice: Double) -> Unit
) {
    // Parse portions & spices
    val portionList = remember(item) {
        when {
            item.portionsJson.contains("Half") -> listOf("Half Portion", "Full Portion (+₹90)")
            item.portionsJson.contains("6 P") -> listOf("6 Pieces", "10 Pieces (Platter) (+₹120)")
            item.portionsJson.contains("Regular") -> listOf("Regular (Serves 2)", "Large (Serves 4) (+₹110)")
            else -> listOf("Standard Portion")
        }
    }

    val spiceList = remember(item) {
        listOf("Mild", "Medium Spicy (Chef Special)", "Extra Spicy 🔥")
    }

    val availableAddons = remember(item) {
        listOf(
            "Extra Boondi Raita" to 30.0,
            "Garlic Butter Naan (1 Pc)" to 45.0,
            "Mint Chutney & Spiced Onions" to 20.0,
            "Extra Salan Gravy" to 35.0
        )
    }

    var selectedPortion by remember { mutableStateOf(portionList.first()) }
    var selectedSpice by remember { mutableStateOf("Medium Spicy (Chef Special)") }
    val selectedAddons = remember { mutableStateListOf<Pair<String, Double>>() }

    val portionDelta = if (selectedPortion.contains("+₹")) {
        selectedPortion.substringAfter("+₹").substringBefore(")").toDoubleOrNull() ?: 0.0
    } else 0.0

    val addonsTotal = selectedAddons.sumOf { it.second }
    val finalPrice = item.price + portionDelta + addonsTotal

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        containerColor = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(bottom = 16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    VegNonVegIcon(isVeg = item.isVeg)
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = item.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            color = TextPrimaryLight
                        )
                        Text(
                            text = "Customize your meal to taste",
                            fontSize = 12.sp,
                            color = TextSecondaryLight
                        )
                    }
                }
                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                }
            }

            HorizontalDivider(color = Color(0xFFEEEEEE))

            LazyColumn(
                modifier = Modifier
                    .weight(1f, fill = false)
                    .padding(horizontal = 20.dp)
            ) {
                // Portions
                item {
                    Text(
                        text = "Choose Portion / Size",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = TextPrimaryLight,
                        modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                    )
                }
                items(portionList) { portion ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedPortion = portion }
                            .padding(vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = portion,
                            fontSize = 14.sp,
                            color = TextPrimaryLight
                        )
                        RadioButton(
                            selected = selectedPortion == portion,
                            onClick = { selectedPortion = portion },
                            colors = RadioButtonDefaults.colors(selectedColor = ZaykaRed)
                        )
                    }
                }

                // Spices
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Spice Level",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = TextPrimaryLight,
                        modifier = Modifier.padding(top = 12.dp, bottom = 8.dp)
                    )
                }
                items(spiceList) { spice ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedSpice = spice }
                            .padding(vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = spice,
                            fontSize = 14.sp,
                            color = TextPrimaryLight
                        )
                        RadioButton(
                            selected = selectedSpice == spice,
                            onClick = { selectedSpice = spice },
                            colors = RadioButtonDefaults.colors(selectedColor = ZaykaRed)
                        )
                    }
                }

                // Add-ons
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Add-ons & Extras",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = TextPrimaryLight,
                        modifier = Modifier.padding(top = 12.dp, bottom = 8.dp)
                    )
                }
                items(availableAddons) { addon ->
                    val isChecked = selectedAddons.contains(addon)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                if (isChecked) selectedAddons.remove(addon)
                                else selectedAddons.add(addon)
                            }
                            .padding(vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "${addon.first} (+₹${addon.second.toInt()})",
                            fontSize = 14.sp,
                            color = TextPrimaryLight
                        )
                        Checkbox(
                            checked = isChecked,
                            onCheckedChange = { checked ->
                                if (checked) selectedAddons.add(addon)
                                else selectedAddons.remove(addon)
                            },
                            colors = CheckboxDefaults.colors(checkedColor = ZaykaRed)
                        )
                    }
                }
            }

            HorizontalDivider(color = Color(0xFFEEEEEE), modifier = Modifier.padding(vertical = 8.dp))

            // Confirm Add
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(text = "Total Item Price", fontSize = 11.sp, color = TextSecondaryLight)
                    Text(
                        text = "₹${finalPrice.toInt()}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextPrimaryLight
                    )
                }

                Button(
                    onClick = {
                        val addonsStr = selectedAddons.joinToString(", ") { it.first }
                        onConfirm(selectedPortion, selectedSpice, addonsStr, finalPrice)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ZaykaRed),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.testTag("add_customized_item_btn")
                ) {
                    Text(
                        text = "Add Item to Cart",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}
