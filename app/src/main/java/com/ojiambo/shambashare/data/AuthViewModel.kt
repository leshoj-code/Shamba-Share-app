package com.ojiambo.shambashare.data

import android.content.Context
import android.widget.Toast
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.ojiambo.shambashare.models.User
import com.ojiambo.shambashare.navigation.ROUT_DASHBOARD
import com.ojiambo.shambashare.navigation.ROUT_LOGIN
import com.ojiambo.shambashare.navigation.ROUT_MAP
import com.ojiambo.shambashare.navigation.ROUT_SIGNUP

class AuthViewModel(val navController: NavController, val context: Context) {

    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()

    fun signup(
        fullName: String,
        username: String,
        email: String,
        phone: String,
        password: String,
        confirmPassword: String,
        role: String
    ) {
        if (fullName.isBlank() || username.isBlank() || email.isBlank() ||
            phone.isBlank() || password.isBlank() || confirmPassword.isBlank()
        ) {
            Toast.makeText(context, "Please fill in all fields.", Toast.LENGTH_LONG).show()
            return
        }

        if (password != confirmPassword) {
            Toast.makeText(context, "Passwords do not match.", Toast.LENGTH_LONG).show()
            return
        }

        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val uid = mAuth.currentUser!!.uid
                    val userData = User(
                        fullName = fullName,
                        username = username,
                        email = email,
                        phone = phone,
                        password = password,
                        uid = uid,
                        role = role
                    )
                    FirebaseDatabase.getInstance()
                        .getReference("Users/$uid")
                        .setValue(userData)
                        .addOnCompleteListener { result ->
                            if (result.isSuccessful) {
                                Toast.makeText(context, "Account created!", Toast.LENGTH_LONG).show()
                                // Route based on role
                                if (role == "owner") {
                                    navController.navigate(ROUT_DASHBOARD) {
                                        popUpTo(ROUT_SIGNUP) { inclusive = true }
                                    }
                                } else {
                                    navController.navigate(ROUT_MAP) {
                                        popUpTo(ROUT_SIGNUP) { inclusive = true }
                                    }
                                }
                            } else {
                                Toast.makeText(
                                    context,
                                    result.exception?.message ?: "Registration failed.",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                } else {
                    Toast.makeText(
                        context,
                        task.exception?.message ?: "Signup failed.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
    }

    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            Toast.makeText(context, "Email and password cannot be blank.", Toast.LENGTH_LONG).show()
            return
        }

        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val uid = mAuth.currentUser!!.uid
                    FirebaseDatabase.getInstance()
                        .getReference("Users/$uid")
                        .get()
                        .addOnSuccessListener { snapshot ->
                            val role = snapshot.child("role").value?.toString() ?: "renter"
                            Toast.makeText(context, "Welcome back!", Toast.LENGTH_SHORT).show()
                            if (role == "owner") {
                                navController.navigate("dashboard") {
                                    popUpTo(ROUT_LOGIN) { inclusive = true }
                                }
                            } else {
                                navController.navigate("map") {
                                    popUpTo(ROUT_LOGIN) { inclusive = true }
                                }
                            }
                        }
                        .addOnFailureListener {
                            Toast.makeText(context, "Failed to fetch user role.", Toast.LENGTH_SHORT).show()
                            navController.navigate("map") {
                                popUpTo(ROUT_LOGIN) { inclusive = true }
                            }
                        }
                } else {
                    Toast.makeText(
                        context,
                        "Wrong email or password.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    fun logout() {
        mAuth.signOut()
        navController.navigate(ROUT_LOGIN) {
            popUpTo(0) { inclusive = true }
        }
    }

    fun isLoggedIn(): Boolean = mAuth.currentUser != null
}