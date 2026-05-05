package com.ojiambo.shambashare.ui.screens.notification

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.ojiambo.shambashare.ui.theme.ShambaGreen
import com.ojiambo.shambashare.ui.theme.ShambaGreenLight
import com.ojiambo.shambashare.ui.theme.ShambaGreenPale

data class AppNotification(
    val id: String = "",
    val title: String = "",
    val message: String = "",
    val time: String = "",
    val type: String = "order",   // order, payment, system
    val isRead: Boolean = false
)

val sampleNotifications = listOf(
    AppNotification(
        id = "1",
        title = "New Order Request! 🚜",
        message = "John Kamau wants to rent your Massey Ferguson 385",
        time = "2 mins ago",
        type = "order",
        isRead = false
    ),
    AppNotification(
        id = "2",
        title = "Payment Received 💰",
        message = "M-Pesa payment of KES 3,500 received from Mary Wanjiku. Ref: QK7X2Y9Z",
        time = "1 hr ago",
        type = "payment",
        isRead = false
    ),
    AppNotification(
        id = "3",
        title = "Order Accepted ✅",
        message = "Your request for Water Pump 3inch was accepted. Equipment is on its way!",
        time = "3 hrs ago",
        type = "order",
        isRead = true
    ),
    AppNotification(
        id = "4",
        title = "Payment Received 💰",
        message = "M-Pesa payment of KES 800 received from Peter Mwangi. Ref: PL3M5N8A",
        time = "Yesterday",
        type = "payment",
        isRead = true
    ),
    AppNotification(
        id = "5",
        title = "Welcome to ShambaShare 🌱",
        message = "Your account is set up and ready. Start listing your equipment or browse the map!",
        time = "Apr 28",
        type = "system",
        isRead = true
    ),
    AppNotification(
        id = "6",
        title = "Order Cancelled ❌",
        message = "Grace Akinyi cancelled their request for John Deere 5075E.",
        time = "Apr 25",
        type = "order",
        isRead = true
    ),
)

@Composable
fun NotificationScreen(navController: NavController) {

    var notifications by remember { mutableStateOf(sampleNotifications) }
    val unreadCount = notifications.count { !it.isRead }

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
                            text = "Notifications",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        // Mark all read button
                        IconButton(
                            onClick = {
                                notifications = notifications.map {
                                    it.copy(isRead = true)
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Mark all read",
                                tint = Color.White
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Activity Feed",
                                color = Color.White,
                                fontSize = 26.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Serif
                            )
                            Text(
                                text = if (unreadCount > 0)
                                    "$unreadCount unread notification${if (unreadCount > 1) "s" else ""}"
                                else
                                    "All caught up!",
                                color = ShambaGreenPale,
                                fontSize = 13.sp
                            )
                        }

                        // Unread badge
                        if (unreadCount > 0) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "$unreadCount",
                                    color = Color.White,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }

        // Mark all read / clear all row
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${notifications.size} notifications",
                    fontSize = 13.sp,
                    color = Color.Gray
                )
                TextButton(
                    onClick = { notifications = emptyList() }
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = null,
                        tint = Color(0xFFD32F2F),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Clear All",
                        color = Color(0xFFD32F2F),
                        fontSize = 13.sp
                    )
                }
            }
        }

        // Empty state
        if (notifications.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(60.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.NotificationsNone,
                            contentDescription = null,
                            tint = Color.LightGray,
                            modifier = Modifier.size(72.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No notifications yet",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0f2d1c)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Order requests and payment\nconfirmations will appear here",
                            fontSize = 13.sp,
                            color = Color.Gray,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            lineHeight = 20.sp
                        )
                    }
                }
            }
        }

        // Notification cards
        items(notifications) { notification ->
            NotificationCard(
                notification = notification,
                onRead = {
                    notifications = notifications.map {
                        if (it.id == notification.id) it.copy(isRead = true) else it
                    }
                },
                onDelete = {
                    notifications = notifications.filter { it.id != notification.id }
                },
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)
            )
        }
    }
}

@Composable
fun NotificationCard(
    notification: AppNotification,
    onRead: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (!notification.isRead)
                ShambaGreen.copy(alpha = 0.04f)
            else
                Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (!notification.isRead) 3.dp else 1.dp
        ),
        onClick = onRead
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Notification icon
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(
                        when (notification.type) {
                            "order"   -> ShambaGreen.copy(alpha = 0.12f)
                            "payment" -> Color(0xFFFFD740).copy(alpha = 0.2f)
                            else      -> Color(0xFF1565C0).copy(alpha = 0.1f)
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = when (notification.type) {
                        "order"   -> "🚜"
                        "payment" -> "💰"
                        else      -> "📢"
                    },
                    fontSize = 22.sp
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = notification.title,
                        fontSize = 14.sp,
                        fontWeight = if (!notification.isRead)
                            FontWeight.Bold
                        else
                            FontWeight.SemiBold,
                        color = Color(0xFF0f2d1c),
                        modifier = Modifier.weight(1f)
                    )

                    // Unread dot
                    if (!notification.isRead) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(ShambaGreen)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = notification.message,
                    fontSize = 13.sp,
                    color = Color.Gray,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = notification.time,
                        fontSize = 11.sp,
                        color = Color.LightGray
                    )
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(20.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = Color.LightGray,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NotificationScreenPreview() {
    NotificationScreen(rememberNavController())
}