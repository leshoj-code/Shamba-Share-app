package com.ojiambo.shambashare.ui.screens.equipment

import android.location.Geocoder
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Agriculture
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.ojiambo.shambashare.models.EquipmentData
import com.ojiambo.shambashare.navigation.ROUT_DASHBOARD
import com.ojiambo.shambashare.ui.theme.ShambaGreen
import com.ojiambo.shambashare.ui.theme.ShambaGreenLight
import com.ojiambo.shambashare.ui.theme.ShambaGreenPale


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEquipmentScreen(navController: NavController) {

    var equipmentName by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf("") }
    var pricePerHour by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var typeDropdownExpanded by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val equipmentTypes = listOf("Tractor", "Water Pump", "Harvester", "Plough", "Sprayer", "Other")

    val gradient = Brush.verticalGradient(
        colors = listOf(ShambaGreen, ShambaGreenLight)
    )

    Box(modifier = Modifier.fillMaxSize()) {

        Column(modifier = Modifier.fillMaxSize()) {

            // Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(brush = gradient)
                    .padding(horizontal = 8.dp, vertical = 16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "List Equipment",
                            color = Color.White,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Serif
                        )
                        Text(
                            text = "Add your machinery to the map",
                            color = ShambaGreenPale,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            // Form
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                Spacer(modifier = Modifier.height(4.dp))

                // Equipment type selector cards
                Text(
                    text = "Equipment Type",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0f2d1c)
                )

                // Type quick-select cards
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    listOf(
                        Triple("Tractor", "🚜", ShambaGreen),
                        Triple("Water Pump", "💧", Color(0xFF1565C0)),
                        Triple("Harvester", "🌾", Color(0xFF6A1B9A))
                    ).forEach { (type, emoji, color) ->
                        Card(
                            onClick = { selectedType = type },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (selectedType == type)
                                    color
                                else
                                    color.copy(alpha = 0.08f)
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 14.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(text = emoji, fontSize = 26.sp)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = type,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (selectedType == type)
                                        Color.White
                                    else
                                        color,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }

                // Dropdown for other types
                ExposedDropdownMenuBox(
                    expanded = typeDropdownExpanded,
                    onExpandedChange = { typeDropdownExpanded = it }
                ) {
                    OutlinedTextField(
                        value = if (selectedType.isNotEmpty()) selectedType else "",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Or select from all types") },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Agriculture,
                                contentDescription = null,
                                tint = ShambaGreen
                            )
                        },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = typeDropdownExpanded)
                        },
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ShambaGreen,
                            focusedLabelColor = ShambaGreen
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable, true)  // ← fix here
                    )
                    ExposedDropdownMenu(
                        expanded = typeDropdownExpanded,
                        onDismissRequest = { typeDropdownExpanded = false },
                        modifier = Modifier.background(Color.White)
                    ) {
                        equipmentTypes.forEach { type ->
                            DropdownMenuItem(
                                text = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = when (type) {
                                                "Tractor"    -> "🚜"
                                                "Water Pump" -> "💧"
                                                "Harvester"  -> "🌾"
                                                "Plough"     -> "⚙️"
                                                "Sprayer"    -> "💦"
                                                else         -> "🔧"
                                            },
                                            fontSize = 18.sp
                                        )
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Text(
                                            text = type,
                                            fontSize = 14.sp,
                                            color = if (selectedType == type) ShambaGreen else Color(0xFF333333),
                                            fontWeight = if (selectedType == type) FontWeight.Bold else FontWeight.Normal
                                        )
                                    }
                                },
                                onClick = {
                                    selectedType = type
                                    typeDropdownExpanded = false
                                },
                                modifier = Modifier.background(
                                    if (selectedType == type) ShambaGreen.copy(alpha = 0.05f) else Color.Transparent
                                )
                            )
                        }
                    }
                }

                // Equipment name
                OutlinedTextField(
                    value = equipmentName,
                    onValueChange = { equipmentName = it; errorMessage = "" },
                    label = { Text("Equipment Name") },
                    placeholder = { Text("e.g. Massey Ferguson 385") },
                    leadingIcon = {
                        Icon(Icons.Default.Tag, contentDescription = null, tint = ShambaGreen)
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ShambaGreen,
                        focusedLabelColor = ShambaGreen,
                        cursorColor = ShambaGreen
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                // Price per hour
                OutlinedTextField(
                    value = pricePerHour,
                    onValueChange = { pricePerHour = it; errorMessage = "" },
                    label = { Text("Price per Hour (KES)") },
                    placeholder = { Text("e.g. 3500") },
                    leadingIcon = {
                        Icon(
                            Icons.Default.AttachMoney,
                            contentDescription = null,
                            tint = ShambaGreen
                        )
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ShambaGreen,
                        focusedLabelColor = ShambaGreen,
                        cursorColor = ShambaGreen
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                // Location
                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it; errorMessage = "" },
                    label = { Text("Location") },
                    placeholder = { Text("e.g. Nairobi, Westlands") },
                    leadingIcon = {
                        Icon(
                            Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = ShambaGreen
                        )
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ShambaGreen,
                        focusedLabelColor = ShambaGreen,
                        cursorColor = ShambaGreen
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                // Location hint
                Text(
                    text = "📍 Enter your location — it will appear as a pin on the map",
                    fontSize = 12.sp,
                    color = Color.Gray,
                    lineHeight = 16.sp
                )

                // Error message
                if (errorMessage.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFFFFEBEE))
                            .padding(12.dp)
                    ) {
                        Text(
                            text = "❌ $errorMessage",
                            color = Color(0xFFD32F2F),
                            fontSize = 13.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Submit button
                Button(
                    onClick = {
                        when {
                            selectedType.isBlank() -> errorMessage = "Please select an equipment type."
                            equipmentName.isBlank() -> errorMessage = "Please enter the equipment name."
                            pricePerHour.isBlank() -> errorMessage = "Please enter a price per hour."
                            pricePerHour.toIntOrNull() == null -> errorMessage = "Price must be a valid number."
                            location.isBlank() -> errorMessage = "Please enter a location."
                            else -> {
                                isLoading = true
                                val uid = FirebaseAuth.getInstance().currentUser?.uid ?: ""

                                // Geocode the location to get lat/lng
                                try {
                                    val geocoder = Geocoder(context)
                                    val addresses = geocoder.getFromLocationName(location, 1)
                                    val lat = addresses?.firstOrNull()?.latitude ?: -1.286389
                                    val lng = addresses?.firstOrNull()?.longitude ?: 36.817223

                                    val equipmentId = FirebaseDatabase.getInstance()
                                        .getReference("Equipment")
                                        .push()
                                        .key ?: ""

                                    val equipment = EquipmentData(
                                        id = equipmentId,
                                        name = equipmentName.trim(),
                                        type = selectedType,
                                        pricePerHour = pricePerHour.toInt(),
                                        location = location.trim(),
                                        lat = lat,
                                        lng = lng,
                                        status = "Idle",
                                        ownerUid = uid
                                    )

                                    FirebaseDatabase.getInstance()
                                        .getReference("Equipment/$equipmentId")
                                        .setValue(equipment)
                                        .addOnSuccessListener {
                                            isLoading = false
                                            navController.navigate(ROUT_DASHBOARD) {
                                                popUpTo(ROUT_DASHBOARD) { inclusive = false }
                                            }
                                        }
                                        .addOnFailureListener { e ->
                                            isLoading = false
                                            errorMessage = e.message ?: "Failed to save equipment."
                                        }
                                } catch (e: Exception) {
                                    isLoading = false
                                    errorMessage = "Could not find location. Try a more specific address."
                                }
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ShambaGreen,
                        contentColor = Color.White
                    ),
                    enabled = !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            color = Color.White,
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(22.dp)
                        )
                    } else {
                        Text(
                            text = "📍 List on Map",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AddEquipmentScreenPreview() {
    AddEquipmentScreen(rememberNavController())
}