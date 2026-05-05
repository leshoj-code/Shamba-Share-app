package com.ojiambo.shambashare.ui.screens.dashboard

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.ojiambo.shambashare.navigation.ROUT_ADD_EQUIPMENT
import com.ojiambo.shambashare.navigation.ROUT_HISTORY
import com.ojiambo.shambashare.navigation.ROUT_LOGIN
import com.ojiambo.shambashare.navigation.ROUT_MAP
import com.ojiambo.shambashare.navigation.ROUT_NOTIFICATIONS
import com.ojiambo.shambashare.navigation.ROUT_PROFILE
import com.ojiambo.shambashare.ui.theme.ShambaGreen
import com.ojiambo.shambashare.ui.theme.ShambaGreenLight
import com.ojiambo.shambashare.ui.theme.ShambaGreenPale

data class EquipmentItem(
    val id: String = "",
    val name: String = "",
    val type: String = "",
    val pricePerHour: Int = 0,
    val status: String = "Idle",
    val currentRenter: String? = null,
    val renterUid: String? = null,
    val renterPhone: String? = null
)

data class OrderRequest(
    val id: Int,
    val renterName: String,
    val equipmentName: String,
    val equipmentId: Int
)

val sampleEquipment = listOf(
    EquipmentItem("1", "Massey Ferguson 385", "Tractor", 3500, "Active", "John Kamau", "uid123", "0712345678"),
    EquipmentItem("2", "Water Pump 3inch", "Water Pump", 800, "Idle"),
    EquipmentItem("3", "Combine Harvester", "Harvester", 8000, "Idle"),
    EquipmentItem("4", "John Deere 5075E", "Tractor", 4200, "Idle"),
)

@Composable
fun DashboardScreen(navController: NavController) {

    var displayName by remember { mutableStateOf("there") }

    LaunchedEffect(Unit) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        if (uid != null) {
            FirebaseDatabase.getInstance()
                .getReference("Users/$uid")
                .get()
                .addOnSuccessListener { snapshot ->
                    val fullName = snapshot.child("fullName").value?.toString()
                    val username = snapshot.child("username").value?.toString()
                    displayName = fullName?.split(" ")?.firstOrNull()
                        ?: username
                                ?: "there"
                }
        }
    }

    var showOrderDialog by remember { mutableStateOf(false) }
    var pendingOrder by remember {
        mutableStateOf<OrderRequest?>(
            OrderRequest(1, "Paul Allan", "Massey Ferguson 385", 1)
        )
    }

    val activeCount = sampleEquipment.count { it.status == "Active" }
    val idleCount = sampleEquipment.count { it.status == "Idle" }
    val totalEarnings = sampleEquipment
        .filter { it.status == "Active" }
        .sumOf { it.pricePerHour }

    val gradient = Brush.verticalGradient(
        colors = listOf(ShambaGreen, ShambaGreenLight)
    )

    if (showOrderDialog && pendingOrder != null) {
        IncomingOrderDialog(
            order = pendingOrder!!,
            onAccept = {
                showOrderDialog = false
                pendingOrder = null
            },
            onDecline = {
                showOrderDialog = false
                pendingOrder = null
            }
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {

            // Header
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(brush = gradient)
                        .padding(horizontal = 20.dp, vertical = 24.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(CircleShape)
                                        .background(Color.White.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = getGreeting(),
                                        color = ShambaGreenPale,
                                        fontSize = 12.sp
                                    )
                                    Text(
                                        text = "$displayName 👋",
                                        color = Color.White,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Row {
                                BadgedBox(
                                    badge = {
                                        if (pendingOrder != null) {
                                            Badge(containerColor = Color(0xFFFF5722)) {
                                                Text("1", color = Color.White, fontSize = 10.sp)
                                            }
                                        }
                                    }
                                ) {
                                    IconButton(onClick = {
                                        navController.navigate(ROUT_NOTIFICATIONS)
                                    }) {
                                        Icon(
                                            imageVector = Icons.Default.Notifications,
                                            contentDescription = "Notifications",
                                            tint = Color.White
                                        )
                                    }
                                }

                                IconButton(onClick = {
                                    FirebaseAuth.getInstance().signOut()
                                    navController.navigate(ROUT_LOGIN) {
                                        popUpTo(0) { inclusive = true }
                                    }
                                }) {
                                    Icon(
                                        imageVector = Icons.Default.ExitToApp,
                                        contentDescription = "Logout",
                                        tint = Color.White
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        Text(
                            text = "ShambaShare",
                            color = Color.White,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Serif
                        )
                        Text(
                            text = "Tracking your fleet across Kenya 🇰🇪",
                            color = ShambaGreenPale,
                            fontSize = 13.sp
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            StatCard(
                                modifier = Modifier.weight(1f),
                                label = "Active Now",
                                value = "$activeCount",
                                emoji = "⚡",
                                valueColor = Color(0xFF69F0AE)
                            )
                            StatCard(
                                modifier = Modifier.weight(1f),
                                label = "Idle Units",
                                value = "$idleCount",
                                emoji = "💤",
                                valueColor = Color.White
                            )
                            StatCard(
                                modifier = Modifier.weight(1f),
                                label = "KES/hr",
                                value = "$totalEarnings",
                                emoji = "💰",
                                valueColor = Color(0xFFFFD740)
                            )
                        }
                    }
                }
            }

            // Quick actions
            item {
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = "Quick Actions",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0f2d1c),
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        QuickActionCard(
                            emoji = "➕",
                            label = "Add Equipment",
                            color = ShambaGreen,
                            onClick = { navController.navigate(ROUT_ADD_EQUIPMENT) }
                        )
                    }
                    item {
                        QuickActionCard(
                            emoji = "🗺️",
                            label = "View Map",
                            color = Color(0xFF1565C0),
                            onClick = { navController.navigate(ROUT_MAP) }
                        )
                    }
                    item {
                        QuickActionCard(
                            emoji = "📋",
                            label = "Rental History",
                            color = Color(0xFF6A1B9A),
                            onClick = { navController.navigate(ROUT_HISTORY) }
                        )
                    }
                    item {
                        QuickActionCard(
                            emoji = "👤",
                            label = "My Profile",
                            color = Color(0xFF00838F),
                            onClick = { navController.navigate(ROUT_PROFILE) }
                        )
                    }
                }
            }

            // Active rentals section
            item {
                Spacer(modifier = Modifier.height(24.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Currently in the Field",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0f2d1c)
                    )
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50.dp))
                            .background(ShambaGreen.copy(alpha = 0.1f))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "🟢 Live",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = ShambaGreen
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            val activeEquipment = sampleEquipment.filter { it.status == "Active" }
            if (activeEquipment.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFFF5F5F5))
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No equipment is currently rented out.",
                            color = Color.Gray,
                            fontSize = 14.sp
                        )
                    }
                }
            } else {
                items(activeEquipment) { equipment ->
                    EquipmentCard(
                        equipment = equipment,
                        navController = navController,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)
                    )
                }
            }

            // Idle fleet section
            item {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "Idle Fleet",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0f2d1c),
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            val idleEquipment = sampleEquipment.filter { it.status == "Idle" }
            items(idleEquipment) { equipment ->
                EquipmentCard(
                    equipment = equipment,
                    navController = navController,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)
                )
            }

            // Weather widget
            item {
                Spacer(modifier = Modifier.height(24.dp))
                WeatherWidget(modifier = Modifier.padding(horizontal = 20.dp))
                Spacer(modifier = Modifier.height(24.dp))
            }
        }

        FloatingActionButton(
            onClick = { navController.navigate(ROUT_ADD_EQUIPMENT) },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp),
            containerColor = ShambaGreen,
            contentColor = Color.White,
            shape = RoundedCornerShape(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add Equipment",
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

@Composable
fun StatCard(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    emoji: String,
    valueColor: Color
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(Color.White.copy(alpha = 0.15f))
            .padding(12.dp)
    ) {
        Column {
            Text(text = emoji, fontSize = 20.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = valueColor
            )
            Text(
                text = label,
                fontSize = 10.sp,
                color = Color.White.copy(alpha = 0.8f)
            )
        }
    }
}

@Composable
fun QuickActionCard(
    emoji: String,
    label: String,
    color: Color,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f)),
        modifier = Modifier.size(width = 100.dp, height = 90.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = emoji, fontSize = 28.sp)
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = color,
                textAlign = TextAlign.Center,
                lineHeight = 14.sp
            )
        }
    }
}

@Composable
fun EquipmentCard(
    equipment: EquipmentItem,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Equipment icon
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (equipment.status == "Active")
                                ShambaGreen.copy(alpha = 0.1f)
                            else
                                Color(0xFFF5F5F5)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = when (equipment.type) {
                            "Tractor"    -> "🚜"
                            "Water Pump" -> "💧"
                            "Harvester"  -> "🌾"
                            else         -> "🚜"
                        },
                        fontSize = 26.sp
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = equipment.name,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0f2d1c)
                    )
                    if (equipment.currentRenter != null) {
                        Text(
                            text = "Renter: ${equipment.currentRenter}",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    } else {
                        Text(
                            text = equipment.type,
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "KES ${equipment.pricePerHour}/hr",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = ShambaGreen
                    )
                }

                // Status badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50.dp))
                        .background(
                            if (equipment.status == "Active")
                                ShambaGreen.copy(alpha = 0.1f)
                            else
                                Color(0xFFF5F5F5)
                        )
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = if (equipment.status == "Active") "● Active" else "● Idle",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (equipment.status == "Active") ShambaGreen else Color.Gray
                    )
                }
            }

            // Request Payment button — only shown for Active equipment
            if (equipment.status == "Active") {
                Spacer(modifier = Modifier.height(12.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(Color(0xFFF5F5F5))
                )

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {
                        navController.navigate(
                            "payment/${equipment.id}/" +
                                    "${equipment.name}/" +
                                    "${equipment.renterUid ?: ""}/" +
                                    "${equipment.renterPhone ?: ""}/" +
                                    "${equipment.pricePerHour}"
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(42.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ShambaGreen,
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        text = "💰 Request Payment",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun WeatherWidget(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0f2d1c)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "🌦️", fontSize = 36.sp)
                Spacer(modifier = Modifier.width(14.dp))
                Column {
                    Text(
                        text = "Nairobi Weather",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Scattered showers expected",
                        color = Color.White.copy(alpha = 0.6f),
                        fontSize = 12.sp
                    )
                    Text(
                        text = "✅ Good for planting",
                        color = Color(0xFF69F0AE),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Text(
                text = "22°C",
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun IncomingOrderDialog(
    order: OrderRequest,
    onAccept: () -> Unit,
    onDecline: () -> Unit
) {
    Dialog(onDismissRequest = onDecline) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "🚜", fontSize = 56.sp)
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "New Order Request!",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0f2d1c)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "${order.renterName} wants to rent your",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = order.equipmentName,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = ShambaGreen,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = onAccept,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ShambaGreen,
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        text = "✅ Accept Request",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedButton(
                    onClick = onDecline,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text(
                        text = "Decline",
                        fontSize = 16.sp,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}

fun getGreeting(): String {
    val hour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)
    return when {
        hour < 12 -> "Good morning,"
        hour < 18 -> "Good afternoon,"
        else      -> "Good evening,"
    }
}

@Preview(showBackground = true)
@Composable
fun DashboardScreenPreview() {
    DashboardScreen(rememberNavController())
}