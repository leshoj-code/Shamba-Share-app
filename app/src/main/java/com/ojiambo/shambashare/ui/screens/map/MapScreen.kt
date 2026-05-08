package com.ojiambo.shambashare.ui.screens.map

import android.R.attr.title
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.snapping.SnapPosition.Center.position
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Agriculture
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.google.firebase.database.FirebaseDatabase
import com.ojiambo.shambashare.ui.theme.ShambaGreen
import kotlinx.coroutines.launch
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.config.Configuration as OsmConfig
import com.google.android.gms.location.LocationServices

data class EquipmentMapItem(
    val id:           String = "",
    val name:         String = "",
    val type:         String = "",
    val pricePerHour: Int    = 0,
    val ownerPhone:   String = "",
    val lat:          Double = 0.0,
    val lng:          Double = 0.0,
    val status:       String = "Idle",
    val imageUrl:     String = ""
)



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(navController: NavController) {

    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    var searchQuery       by remember { mutableStateOf("") }
    var selectedEquipment by remember { mutableStateOf<EquipmentMapItem?>(null) }
    var equipmentList     by remember { mutableStateOf<List<EquipmentMapItem>>(emptyList()) }
    var mapView           by remember { mutableStateOf<MapView?>(null) }

    val scope         = rememberCoroutineScope()
    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberStandardBottomSheetState(
            initialValue    = SheetValue.Hidden,
            skipHiddenState = false
        )
    )

    fun moveToMyLocation() {

        try {

            fusedLocationClient.getCurrentLocation(
                com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY,
                null
            ).addOnSuccessListener { location ->

                if (location != null) {

                    val userPoint = GeoPoint(
                        location.latitude,
                        location.longitude
                    )

                    mapView?.controller?.apply {
                        setZoom(18.0)
                        animateTo(userPoint)
                    }

                } else {
                    android.widget.Toast
                        .makeText(
                            context,
                            "Unable to get current location",
                            android.widget.Toast.LENGTH_SHORT
                        )
                        .show()
                }
            }

        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }

    // Initialize OSM config
    LaunchedEffect(Unit) {
        OsmConfig.getInstance().userAgentValue = context.packageName
    }

    // Fetch equipment from Firebase
    LaunchedEffect(Unit) {
        FirebaseDatabase.getInstance()
            .getReference("Equipment")
            .get()
            .addOnSuccessListener { snapshot ->
                val items = mutableListOf<EquipmentMapItem>()
                snapshot.children.forEach { child ->
                    val item = EquipmentMapItem(
                        id           = child.child("id").value?.toString()           ?: "",
                        name         = child.child("name").value?.toString()         ?: "",
                        type         = child.child("type").value?.toString()         ?: "",
                        pricePerHour = child.child("pricePerHour").value
                            ?.toString()?.toIntOrNull()              ?: 0,
                        ownerPhone   = child.child("ownerPhone").value?.toString()   ?: "",
                        lat          = child.child("lat").value?.toString()
                            ?.toDoubleOrNull()                        ?: 0.0,
                        lng          = child.child("lng").value?.toString()
                            ?.toDoubleOrNull()                        ?: 0.0,
                        status       = child.child("status").value?.toString()       ?: "Idle",
                        imageUrl     = child.child("imageUrl").value?.toString()     ?: ""
                    )
                    if (item.lat != 0.0 && item.lng != 0.0) items.add(item)
                }
                equipmentList = items
            }
    }

    // Cleanup map on dispose
    DisposableEffect(Unit) {
        onDispose { mapView?.onDetach() }
    }

    BottomSheetScaffold(
        scaffoldState  = scaffoldState,
        sheetPeekHeight = 0.dp,
        sheetShape     = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        sheetContent   = {
            selectedEquipment?.let { equipment ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                ) {
                    // Handle bar
                    Box(
                        modifier = Modifier
                            .width(40.dp)
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(Color.LightGray)
                            .align(Alignment.CenterHorizontally)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Equipment image (if available)
                    if (equipment.imageUrl.isNotEmpty()) {
                        AsyncImage(
                            model              = equipment.imageUrl,
                            contentDescription = "Equipment photo",
                            modifier           = Modifier
                                .fillMaxWidth()
                                .height(180.dp)
                                .clip(RoundedCornerShape(14.dp)),
                            contentScale       = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    // Equipment header
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier          = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(ShambaGreen.copy(alpha = 0.1f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = when (equipment.type) {
                                    "Tractor"    -> "🚜"
                                    "Water Pump" -> "💧"
                                    "Harvester"  -> "🌾"
                                    else         -> "🚜"
                                },
                                fontSize = 28.sp
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text       = equipment.name,
                                fontSize   = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color      = Color(0xFF0f2d1c)
                            )
                            Text(
                                text     = equipment.type,
                                fontSize = 13.sp,
                                color    = Color.Gray
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50.dp))
                                .background(
                                    if (equipment.status == "Idle")
                                        ShambaGreen.copy(alpha = 0.1f)
                                    else
                                        Color(0xFFFFF3E0)
                                )
                                .padding(horizontal = 12.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text       = if (equipment.status == "Idle") "Available" else "In Use",
                                fontSize   = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color      = if (equipment.status == "Idle") ShambaGreen
                                else Color(0xFFE65100)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Price card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors   = CardDefaults.cardColors(
                            containerColor = ShambaGreen.copy(alpha = 0.05f)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment     = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text     = "Price per hour",
                                    fontSize = 12.sp,
                                    color    = Color.Gray
                                )
                                Text(
                                    text       = "KES ${equipment.pricePerHour}",
                                    fontSize   = 22.sp,
                                    fontWeight = FontWeight.Bold,
                                    color      = ShambaGreen
                                )
                            }
                            Icon(
                                imageVector    = Icons.Default.Agriculture,
                                contentDescription = null,
                                tint           = ShambaGreen.copy(alpha = 0.3f),
                                modifier       = Modifier.size(40.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Order Now button
                    Button(
                        onClick = {
                            // TODO: send order request to Firebase
                            scope.launch { scaffoldState.bottomSheetState.hide() }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape   = RoundedCornerShape(14.dp),
                        colors  = ButtonDefaults.buttonColors(
                            containerColor = ShambaGreen,
                            contentColor   = Color.White
                        ),
                        enabled = equipment.status == "Idle"
                    ) {
                        Text(
                            text       = if (equipment.status == "Idle")
                                "🚜 Order Now"
                            else
                                "Currently Unavailable",
                            fontSize   = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // WhatsApp button
                    Button(
                        onClick = {
                            if (equipment.ownerPhone.isNotEmpty()) {
                                val intent = Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse("https://wa.me/${equipment.ownerPhone}")
                                )
                                context.startActivity(intent)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape  = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF25D366),
                            contentColor   = Color.White
                        )
                    ) {
                        Text(
                            text       = "💬 WhatsApp Owner",
                            fontSize   = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    ) { paddingValues ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {

            // OpenStreetMap
            AndroidView(
                factory = { ctx ->
                    org.osmdroid.views.MapView(ctx).apply {
                        setTileSource(org.osmdroid.tileprovider.tilesource.TileSourceFactory.MAPNIK)
                        setMultiTouchControls(true)

                        // Explicitly define the provider first
                        val myLocationProvider = org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider(ctx)

                        // Pass the provider and the MapView (this) to the overlay
                        val locationOverlay = org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay(myLocationProvider, this)

                        locationOverlay.enableMyLocation()
                        // Optional: This makes the map follow the user as they move
                        // locationOverlay.enableFollowLocation()

                        this.overlays.add(locationOverlay)

                        controller.setZoom(13.0)
                        controller.setCenter(org.osmdroid.util.GeoPoint(-1.286389, 36.817223))
                        mapView = this
                    }
                },
                update = { map ->
                    map.overlays.removeAll {
                        it is org.osmdroid.views.overlay.Marker
                    }

                    // Filter by search query
                    val filtered = if (searchQuery.isBlank()) equipmentList
                    else equipmentList.filter {
                        it.name.contains(searchQuery, ignoreCase = true) ||
                                it.type.contains(searchQuery, ignoreCase = true)
                    }

                    filtered.forEach { equipment ->
                        val marker = org.osmdroid.views.overlay.Marker(map).apply {
                            position = GeoPoint(equipment.lat, equipment.lng)
                            title    = equipment.name
                            snippet  = "KES ${equipment.pricePerHour}/hr"
                            setOnMarkerClickListener { _, _ ->
                                selectedEquipment = equipment
                                scope.launch {
                                    scaffoldState.bottomSheetState.expand()
                                }
                                true
                            }
                        }
                        map.overlays.add(marker)
                    }
                    map.invalidate()
                },
                modifier = Modifier.fillMaxSize()
            )

            // Search bar
            Card(
                modifier  = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .align(Alignment.TopCenter),
                shape     = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                colors    = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                OutlinedTextField(
                    value         = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder   = { Text("Search equipment...") },
                    leadingIcon   = {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = null,
                            tint = ShambaGreen
                        )
                    },
                    singleLine = true,
                    colors     = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor   = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // My location FAB
            FloatingActionButton(
                onClick = {
                    moveToMyLocation()
                    android.util.Log.d("MAP_DEBUG", "FAB CLICKED")
                },
                modifier       = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp),
                containerColor = Color.White,
                contentColor   = ShambaGreen,
                shape          = CircleShape
            ) {
                Icon(
                    imageVector    = Icons.Default.MyLocation,
                    contentDescription = "My Location"
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MapScreenPreview() {
    MapScreen(rememberNavController())
}