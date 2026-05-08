package com.ojiambo.shambashare.ui.screens.payment

import android.content.Context
import android.widget.Toast
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.ojiambo.shambashare.navigation.ROUT_DASHBOARD
import com.ojiambo.shambashare.ui.theme.ShambaGreen
import com.ojiambo.shambashare.ui.theme.ShambaGreenLight
import com.ojiambo.shambashare.ui.theme.ShambaGreenPale
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun PaymentScreen(
    navController: NavController,
    equipmentId: String = "",
    equipmentName: String = "",
    renterUid: String = "",
    renterPhone: String = "",
    amount: Int = 0
) {
    var selectedMethod by remember { mutableStateOf("mpesa") }
    var phoneNumber    by remember { mutableStateOf(renterPhone) }
    var cashReference  by remember { mutableStateOf("") }
    var isLoading      by remember { mutableStateOf(false) }
    var errorMessage   by remember { mutableStateOf("") }

    val context = LocalContext.current

    val gradient = Brush.verticalGradient(
        colors = listOf(ShambaGreen, ShambaGreenLight)
    )

    Column(modifier = Modifier.fillMaxSize()) {

        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(brush = gradient)
                .padding(20.dp)
        ) {
            Column {
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
                            text = "Request Payment",
                            color = Color.White,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Serif
                        )
                        Text(
                            text = equipmentName,
                            color = ShambaGreenPale,
                            fontSize = 13.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Amount display
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White.copy(alpha = 0.15f))
                        .padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Amount Due",
                                color = ShambaGreenPale,
                                fontSize = 12.sp
                            )
                            Text(
                                text = "KES $amount",
                                color = Color.White,
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(text = "💰", fontSize = 40.sp)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        // Payment method + form
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Text(
                text = "Choose Payment Method",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0f2d1c)
            )

            // M-Pesa / Cash toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Surface(
                    onClick = { selectedMethod = "mpesa" },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    color = if (selectedMethod == "mpesa") ShambaGreen else Color(0xFFF5F5F5),
                    tonalElevation = 0.dp
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "📲", fontSize = 32.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "M-Pesa",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (selectedMethod == "mpesa") Color.White else Color(0xFF333333)
                        )
                        Text(
                            text = "STK Push to\nrenter's phone",
                            fontSize = 11.sp,
                            color = if (selectedMethod == "mpesa")
                                Color.White.copy(alpha = 0.8f)
                            else
                                Color.Gray,
                            textAlign = TextAlign.Center,
                            lineHeight = 15.sp
                        )
                    }
                }

                Surface(
                    onClick = { selectedMethod = "cash" },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    color = if (selectedMethod == "cash") Color(0xFF1565C0) else Color(0xFFF5F5F5),
                    tonalElevation = 0.dp
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "💵", fontSize = 32.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Cash",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (selectedMethod == "cash") Color.White else Color(0xFF333333)
                        )
                        Text(
                            text = "Record a manual\ncash payment",
                            fontSize = 11.sp,
                            color = if (selectedMethod == "cash")
                                Color.White.copy(alpha = 0.8f)
                            else
                                Color.Gray,
                            textAlign = TextAlign.Center,
                            lineHeight = 15.sp
                        )
                    }
                }
            }

            // M-Pesa form
            if (selectedMethod == "mpesa") {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Renter's M-Pesa Number",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0f2d1c)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = phoneNumber,
                            onValueChange = { phoneNumber = it },
                            label = { Text("Phone Number") },
                            placeholder = { Text("e.g. 0712345678") },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Phone,
                                    contentDescription = null,
                                    tint = ShambaGreen
                                )
                            },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = ShambaGreen,
                                focusedLabelColor  = ShambaGreen
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "📲 The renter will receive a PIN prompt on their phone",
                            fontSize = 12.sp,
                            color = Color.Gray,
                            lineHeight = 16.sp
                        )
                    }
                }
            }

            // Cash form
            if (selectedMethod == "cash") {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Cash Payment Reference",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0f2d1c)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = cashReference,
                            onValueChange = { cashReference = it },
                            label = { Text("Reference / Receipt No.") },
                            placeholder = { Text("e.g. CASH-001 or leave blank") },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF1565C0),
                                focusedLabelColor  = Color(0xFF1565C0)
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "💵 Confirm you have physically received KES $amount in cash",
                            fontSize = 12.sp,
                            color = Color.Gray,
                            lineHeight = 16.sp
                        )
                    }
                }
            }

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

            Spacer(modifier = Modifier.weight(1f))

            // Confirm button
            Button(
                onClick = {
                    when (selectedMethod) {
                        "mpesa" -> {
                            if (phoneNumber.isBlank()) {
                                errorMessage = "Please enter the renter's phone number."
                            } else {
                                isLoading = true
                                errorMessage = ""
                                triggerMpesaPayment(
                                    context     = context,
                                    phone       = phoneNumber,
                                    amount      = amount,
                                    equipmentId = equipmentId,
                                    renterUid   = renterUid,
                                    onSuccess   = {
                                        isLoading = false
                                        navController.navigate(ROUT_DASHBOARD) {
                                            popUpTo(ROUT_DASHBOARD) { inclusive = false }
                                        }
                                    },
                                    onError = { error ->
                                        isLoading = false
                                        errorMessage = error
                                    }
                                )
                            }
                        }
                        "cash" -> {
                            isLoading = true
                            errorMessage = ""
                            val ref = cashReference.ifBlank {
                                "CASH-${System.currentTimeMillis()}"
                            }
                            savePaymentToFirebase(
                                equipmentId = equipmentId,
                                renterUid   = renterUid,
                                amount      = amount,
                                method      = "cash",
                                reference   = ref
                            )
                            isLoading = false
                            Toast.makeText(
                                context,
                                "💵 Cash payment of KES $amount recorded. Ref: $ref",
                                Toast.LENGTH_LONG
                            ).show()
                            navController.navigate(ROUT_DASHBOARD) {
                                popUpTo(ROUT_DASHBOARD) { inclusive = false }
                            }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selectedMethod == "mpesa") ShambaGreen
                    else Color(0xFF1565C0),
                    contentColor   = Color.White
                ),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color       = Color.White,
                        strokeWidth = 2.dp,
                        modifier    = Modifier.size(22.dp)
                    )
                } else {
                    Text(
                        text = if (selectedMethod == "mpesa")
                            "📲 Send STK Push"
                        else
                            "💵 Confirm Cash Payment",
                        fontSize   = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

// ── Simulated STK Push ────────────────────────────────────────────────────────
fun triggerMpesaPayment(
    context:     Context,
    phone:       String,
    amount:      Int,
    equipmentId: String,
    renterUid:   String,
    onSuccess:   (String) -> Unit,
    onError:     (String) -> Unit
) {
    fun generateMpesaRef(): String {
        val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
        return (1..10).map { chars.random() }.joinToString("")
    }

    CoroutineScope(Dispatchers.Main).launch {
        try {
            Toast.makeText(
                context,
                "📲 STK Push sent to $phone. Waiting for PIN...",
                Toast.LENGTH_LONG
            ).show()

            // Simulate renter entering PIN (3 seconds)
            delay(3000)

            val mpesaRef  = generateMpesaRef()
            val ownerUid  = FirebaseAuth.getInstance().currentUser?.uid ?: ""
            val db        = FirebaseDatabase.getInstance()
            val paymentId = db.getReference("Payments").push().key ?: ""
            val timestamp = System.currentTimeMillis()

            // Save completed payment
            db.getReference("Payments/$paymentId").setValue(
                mapOf(
                    "id"          to paymentId,
                    "equipmentId" to equipmentId,
                    "renterUid"   to renterUid,
                    "ownerUid"    to ownerUid,
                    "amount"      to amount,
                    "phone"       to phone,
                    "method"      to "mpesa",
                    "reference"   to mpesaRef,
                    "status"      to "completed",
                    "timestamp"   to timestamp
                )
            )

            // Mark equipment Idle
            db.getReference("Equipment/$equipmentId").updateChildren(
                mapOf("status" to "Idle", "currentRenter" to null)
            )

            // Notify renter
            if (renterUid.isNotEmpty()) {
                db.getReference("Notifications/$renterUid").push().setValue(
                    mapOf(
                        "title"   to "Payment Confirmed 💰",
                        "message" to "Your payment of KES $amount was confirmed. Ref: $mpesaRef",
                        "type"    to "payment",
                        "ref"     to mpesaRef,
                        "isRead"  to false,
                        "time"    to timestamp
                    )
                )
            }

            // Notify owner
            if (ownerUid.isNotEmpty()) {
                db.getReference("Notifications/$ownerUid").push().setValue(
                    mapOf(
                        "title"   to "Payment Received 💰",
                        "message" to "KES $amount received via M-Pesa. Ref: $mpesaRef",
                        "type"    to "payment",
                        "ref"     to mpesaRef,
                        "isRead"  to false,
                        "time"    to timestamp
                    )
                )
            }

            Toast.makeText(
                context,
                "✅ Payment of KES $amount confirmed! Ref: $mpesaRef",
                Toast.LENGTH_LONG
            ).show()

            onSuccess(mpesaRef)

        } catch (e: Exception) {
            onError(e.message ?: "Payment simulation failed")
        }
    }
}

// ── Cash payment to Firebase ──────────────────────────────────────────────────
fun savePaymentToFirebase(
    equipmentId: String,
    renterUid:   String,
    amount:      Int,
    method:      String,
    reference:   String
) {
    val ownerUid  = FirebaseAuth.getInstance().currentUser?.uid ?: return
    val db        = FirebaseDatabase.getInstance()
    val paymentId = db.getReference("Payments").push().key ?: return
    val timestamp = System.currentTimeMillis()

    db.getReference("Payments/$paymentId").setValue(
        mapOf(
            "id"          to paymentId,
            "equipmentId" to equipmentId,
            "renterUid"   to renterUid,
            "ownerUid"    to ownerUid,
            "amount"      to amount,
            "method"      to method,
            "reference"   to reference,
            "timestamp"   to timestamp,
            "status"      to "completed"
        )
    )

    db.getReference("Equipment/$equipmentId").updateChildren(
        mapOf("status" to "Idle", "currentRenter" to null)
    )

    db.getReference("Notifications/$renterUid").push().setValue(
        mapOf(
            "title"   to "Payment Confirmed 💰",
            "message" to "Your payment of KES $amount was received. Ref: $reference",
            "type"    to method,
            "ref"     to reference,
            "isRead"  to false,
            "time"    to timestamp
        )
    )

    db.getReference("Notifications/$ownerUid").push().setValue(
        mapOf(
            "title"   to "Payment Received 💰",
            "message" to "KES $amount received via ${method.uppercase()}. Ref: $reference",
            "type"    to method,
            "ref"     to reference,
            "isRead"  to false,
            "time"    to timestamp
        )
    )
}

@Preview(showBackground = true)
@Composable
fun PaymentScreenPreview() {
    PaymentScreen(navController = rememberNavController())
}