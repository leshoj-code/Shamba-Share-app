package com.ojiambo.shambashare.ui.screens.history

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Pending
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.ojiambo.shambashare.ui.theme.ShambaGreen
import com.ojiambo.shambashare.ui.theme.ShambaGreenLight
import com.ojiambo.shambashare.ui.theme.ShambaGreenPale

data class RentalRecord(
    val id: String = "",
    val equipmentName: String = "",
    val equipmentType: String = "",
    val renterName: String = "",
    val ownerName: String = "",
    val pricePerHour: Int = 0,
    val date: String = "",
    val status: String = "completed",  // completed, active, cancelled
    val mpesaRef: String = ""
)

// Sample data — replace with Firebase fetch
val sampleHistory = listOf(
    RentalRecord("1", "Massey Ferguson 385", "Tractor", "John Kamau", "Leshan Ojiambo", 3500, "Apr 28, 2026", "completed", "QK7X2Y9Z"),
    RentalRecord("2", "Water Pump 3inch", "Water Pump", "Mary Wanjiku", "Leshan Ojiambo", 800, "Apr 25, 2026", "completed", "PL3M5N8A"),
    RentalRecord("3", "Combine Harvester", "Harvester", "Peter Mwangi", "Leshan Ojiambo", 8000, "Apr 20, 2026", "cancelled", ""),
    RentalRecord("4", "John Deere 5075E", "Tractor", "Grace Akinyi", "Leshan Ojiambo", 4200, "Apr 15, 2026", "completed", "RT9B4C1D"),
    RentalRecord("5", "Massey Ferguson 385", "Tractor", "James Otieno", "Leshan Ojiambo", 3500, "Apr 10, 2026", "completed", "WN6E2F7G"),
)

@Composable
fun HistoryScreen(navController: NavController) {

    var selectedFilter by remember { mutableStateOf("All") }
    var historyList by remember { mutableStateOf(sampleHistory) }

    val filters = listOf("All", "Completed", "Active", "Cancelled")

    val filteredList = when (selectedFilter) {
        "Completed"  -> historyList.filter { it.status == "completed" }
        "Active"     -> historyList.filter { it.status == "active" }
        "Cancelled"  -> historyList.filter { it.status == "cancelled" }
        else         -> historyList
    }

    // Summary stats
    val totalCompleted = historyList.count { it.status == "completed" }
    val totalEarnings  = historyList
        .filter { it.status == "completed" }
        .sumOf { it.pricePerHour }

    val gradient = Brush.verticalGradient(
        colors = listOf(ShambaGreen, ShambaGreenLight)
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {

        // Header
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(brush = gradient)
                    .padding(20.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.White
                            )
                        }
                        Text(
                            text = "Rental History",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        // Placeholder for symmetry
                        Box(modifier = Modifier.size(48.dp))
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Your Rentals",
                        color = Color.White,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Serif
                    )
                    Text(
                        text = "Track all your equipment transactions",
                        color = ShambaGreenPale,
                        fontSize = 13.sp
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Summary stats
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Total rentals
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(14.dp))
                                .background(Color.White.copy(alpha = 0.15f))
                                .padding(16.dp)
                        ) {
                            Column {
                                Text(text = "📋", fontSize = 22.sp)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "$totalCompleted",
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "Completed",
                                    fontSize = 11.sp,
                                    color = Color.White.copy(alpha = 0.8f)
                                )
                            }
                        }

                        // Total earnings
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(14.dp))
                                .background(Color.White.copy(alpha = 0.15f))
                                .padding(16.dp)
                        ) {
                            Column {
                                Text(text = "💰", fontSize = 22.sp)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "KES $totalEarnings",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFFFD740)
                                )
                                Text(
                                    text = "Total Earned",
                                    fontSize = 11.sp,
                                    color = Color.White.copy(alpha = 0.8f)
                                )
                            }
                        }

                        // Total cancelled
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(14.dp))
                                .background(Color.White.copy(alpha = 0.15f))
                                .padding(16.dp)
                        ) {
                            Column {
                                Text(text = "❌", fontSize = 22.sp)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "${historyList.count { it.status == "cancelled" }}",
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "Cancelled",
                                    fontSize = 11.sp,
                                    color = Color.White.copy(alpha = 0.8f)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }

        // Filter chips
        item {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filters) { filter ->
                    FilterChip(
                        selected = selectedFilter == filter,
                        onClick = { selectedFilter = filter },
                        label = {
                            Text(
                                text = filter,
                                fontWeight = if (selectedFilter == filter)
                                    FontWeight.Bold
                                else
                                    FontWeight.Normal
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = ShambaGreen,
                            selectedLabelColor = Color.White,
                            containerColor = Color(0xFFF5F5F5),
                            labelColor = Color.Gray
                        )
                    )
                }
            }
        }

        // Empty state
        if (filteredList.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "📭", fontSize = 48.sp)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No rentals found",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0f2d1c)
                        )
                        Text(
                            text = "Your rental history will appear here",
                            fontSize = 13.sp,
                            color = Color.Gray
                        )
                    }
                }
            }
        }

        // Rental cards
        items(filteredList) { record ->
            RentalCard(
                record = record,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)
            )
        }
    }
}

@Composable
fun RentalCard(
    record: RentalRecord,
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
                // Equipment emoji
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            when (record.status) {
                                "completed" -> ShambaGreen.copy(alpha = 0.08f)
                                "active"    -> Color(0xFF1565C0).copy(alpha = 0.08f)
                                else        -> Color(0xFFD32F2F).copy(alpha = 0.08f)
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = when (record.equipmentType) {
                            "Tractor"    -> "🚜"
                            "Water Pump" -> "💧"
                            "Harvester"  -> "🌾"
                            else         -> "🔧"
                        },
                        fontSize = 26.sp
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = record.equipmentName,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0f2d1c)
                    )
                    Text(
                        text = record.date,
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }

                // Status badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50.dp))
                        .background(
                            when (record.status) {
                                "completed" -> ShambaGreen.copy(alpha = 0.1f)
                                "active"    -> Color(0xFF1565C0).copy(alpha = 0.1f)
                                else        -> Color(0xFFD32F2F).copy(alpha = 0.1f)
                            }
                        )
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Text(
                        text = when (record.status) {
                            "completed" -> "✅ Done"
                            "active"    -> "⚡ Active"
                            else        -> "❌ Cancelled"
                        },
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = when (record.status) {
                            "completed" -> ShambaGreen
                            "active"    -> Color(0xFF1565C0)
                            else        -> Color(0xFFD32F2F)
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Divider
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(Color(0xFFF5F5F5))
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Bottom row — price and M-Pesa ref
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "KES ${record.pricePerHour}/hr",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = ShambaGreen
                    )
                    Text(
                        text = "Renter: ${record.renterName}",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }

                if (record.mpesaRef.isNotEmpty()) {
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "M-Pesa Ref",
                            fontSize = 10.sp,
                            color = Color.Gray
                        )
                        Text(
                            text = record.mpesaRef,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0f2d1c)
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HistoryScreenPreview() {
    HistoryScreen(rememberNavController())
}