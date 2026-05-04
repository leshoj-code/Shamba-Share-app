package com.ojiambo.shambashare.ui.screens.profile

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.ojiambo.shambashare.navigation.ROUT_HISTORY
import com.ojiambo.shambashare.navigation.ROUT_LOGIN
import com.ojiambo.shambashare.ui.theme.ShambaGreen
import com.ojiambo.shambashare.ui.theme.ShambaGreenLight
import com.ojiambo.shambashare.ui.theme.ShambaGreenPale

@Composable
fun ProfileScreen(navController: NavController) {

    var fullName by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var role by remember { mutableStateOf("") }

    // Fetch user data from Firebase
    LaunchedEffect(Unit) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        if (uid != null) {
            FirebaseDatabase.getInstance()
                .getReference("Users/$uid")
                .get()
                .addOnSuccessListener { snapshot ->
                    fullName = snapshot.child("fullName").value?.toString() ?: ""
                    username = snapshot.child("username").value?.toString() ?: ""
                    email = snapshot.child("email").value?.toString() ?: ""
                    phone = snapshot.child("phone").value?.toString() ?: ""
                    role = snapshot.child("role").value?.toString() ?: "renter"
                }
        }
    }

    val gradient = Brush.verticalGradient(
        colors = listOf(ShambaGreen, ShambaGreenLight)
    )

    // Avatar initials
    val initials = fullName
        .split(" ")
        .take(2)
        .mapNotNull { it.firstOrNull()?.uppercaseChar() }
        .joinToString("")
        .ifEmpty { "?" }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {

        // Header with avatar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(brush = gradient)
                .padding(20.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {

                // Back button row
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
                        text = "My Profile",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    // Edit button
                    IconButton(onClick = { /* TODO: edit profile */ }) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Avatar circle with initials
                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = initials,
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontFamily = FontFamily.Serif
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = fullName.ifEmpty { "Loading..." },
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Serif
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Role badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50.dp))
                        .background(Color.White.copy(alpha = 0.2f))
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = if (role == "owner") "🚜 Equipment Owner" else "👨‍🌾 Farmer / Renter",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Stats row — only show for owners
                if (role == "owner") {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        ProfileStat(label = "Equipment", value = "4")
                        ProfileStatDivider()
                        ProfileStat(label = "Active", value = "1")
                        ProfileStatDivider()
                        ProfileStat(label = "Earnings", value = "KES 12k")
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }

        // Info cards
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Text(
                text = "Account Information",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Gray
            )

            // Info card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(4.dp)) {
                    ProfileInfoRow(
                        icon = Icons.Default.Person,
                        label = "Full Name",
                        value = fullName.ifEmpty { "—" }
                    )
                    Divider(color = Color(0xFFF5F5F5), thickness = 1.dp)
                    ProfileInfoRow(
                        icon = Icons.Default.Person,
                        label = "Username",
                        value = "@${username.ifEmpty { "—" }}"
                    )
                    Divider(color = Color(0xFFF5F5F5), thickness = 1.dp)
                    ProfileInfoRow(
                        icon = Icons.Default.Email,
                        label = "Email",
                        value = email.ifEmpty { "—" }
                    )
                    Divider(color = Color(0xFFF5F5F5), thickness = 1.dp)
                    ProfileInfoRow(
                        icon = Icons.Default.Phone,
                        label = "Phone (M-Pesa)",
                        value = phone.ifEmpty { "—" }
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Settings",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Gray
            )

            // Settings card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(4.dp)) {
                    ProfileMenuRow(
                        icon = Icons.Default.History,
                        label = "Rental History",
                        iconColor = Color(0xFF6A1B9A),
                        onClick = { navController.navigate(ROUT_HISTORY) }
                    )
                    Divider(color = Color(0xFFF5F5F5), thickness = 1.dp)
                    ProfileMenuRow(
                        icon = Icons.Default.Notifications,
                        label = "Notifications",
                        iconColor = Color(0xFF1565C0),
                        onClick = { /* TODO */ }
                    )
                    Divider(color = Color(0xFFF5F5F5), thickness = 1.dp)
                    ProfileMenuRow(
                        icon = Icons.Default.Lock,
                        label = "Change Password",
                        iconColor = ShambaGreen,
                        onClick = { /* TODO */ }
                    )
                    Divider(color = Color(0xFFF5F5F5), thickness = 1.dp)
                    ProfileMenuRow(
                        icon = Icons.Default.Shield,
                        label = "Privacy Policy",
                        iconColor = Color.Gray,
                        onClick = { /* TODO */ }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Logout button
            Button(
                onClick = {
                    FirebaseAuth.getInstance().signOut()
                    navController.navigate(ROUT_LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFFEBEE),
                    contentColor = Color(0xFFD32F2F)
                )
            ) {
                Icon(
                    imageVector = Icons.Default.ExitToApp,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Logout",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun ProfileStat(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            color = ShambaGreenPale,
            fontSize = 11.sp
        )
    }
}

@Composable
fun ProfileStatDivider() {
    Box(
        modifier = Modifier
            .width(1.dp)
            .height(32.dp)
            .background(Color.White.copy(alpha = 0.3f))
    )
}

@Composable
fun ProfileInfoRow(
    icon: ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(ShambaGreen.copy(alpha = 0.08f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = ShambaGreen,
                modifier = Modifier.size(18.dp)
            )
        }
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                fontSize = 11.sp,
                color = Color.Gray
            )
            Text(
                text = value,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF0f2d1c)
            )
        }
    }
}

@Composable
fun ProfileMenuRow(
    icon: ImageVector,
    label: String,
    iconColor: Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(iconColor.copy(alpha = 0.08f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(18.dp)
            )
        }
        Spacer(modifier = Modifier.width(14.dp))
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF0f2d1c),
            modifier = Modifier.weight(1f)
        )
        Icon(
            imageVector = Icons.Default.ArrowForward,
            contentDescription = null,
            tint = Color.LightGray,
            modifier = Modifier.size(16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    ProfileScreen(rememberNavController())
}