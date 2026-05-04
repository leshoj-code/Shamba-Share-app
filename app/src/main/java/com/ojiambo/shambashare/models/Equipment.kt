package com.ojiambo.shambashare.models

data class EquipmentData(
    val id: String = "",
    val name: String = "",
    val type: String = "",
    val pricePerHour: Int = 0,
    val location: String = "",
    val lat: Double = 0.0,
    val lng: Double = 0.0,
    val status: String = "Idle",
    val ownerUid: String = "",
    val ownerPhone: String = ""
)
