package com.ojiambo.shambashare.models

data class User(
    val username: String = "",
    val email: String = "",
    val password: String = "",
    val phone: String = "",
    val fullName: String = "",
    val uid: String = "",
    val role: String = "renter"   // default role
)