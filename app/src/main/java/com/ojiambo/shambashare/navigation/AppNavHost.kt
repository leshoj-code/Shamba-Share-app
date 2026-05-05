package com.ojiambo.shambashare.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.ojiambo.shambashare.ui.components.BottomNavBar
import com.ojiambo.shambashare.ui.screens.about.AboutScreen
import com.ojiambo.shambashare.ui.screens.auth.LoginScreen
import com.ojiambo.shambashare.ui.screens.auth.SignupScreen
import com.ojiambo.shambashare.ui.screens.dashboard.DashboardScreen
import com.ojiambo.shambashare.ui.screens.equipment.AddEquipmentScreen
import com.ojiambo.shambashare.ui.screens.history.HistoryScreen
import com.ojiambo.shambashare.ui.screens.map.MapScreen
import com.ojiambo.shambashare.ui.screens.notification.NotificationScreen
import com.ojiambo.shambashare.ui.screens.payment.PaymentScreen
import com.ojiambo.shambashare.ui.screens.profile.ProfileScreen
import com.ojiambo.shambashare.ui.screens.splash.SplashScreen

// Screens that should NOT show the bottom nav bar
val noNavBarRoutes = listOf(
    ROUT_SPLASH,
    ROUT_LOGIN,
    ROUT_SIGNUP,
    ROUT_ADD_EQUIPMENT
)

@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = ROUT_SPLASH
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val showBottomBar = currentRoute !in noNavBarRoutes

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                BottomNavBar(navController = navController)
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = modifier.padding(innerPadding)
        ) {
            composable(route = ROUT_ABOUT) {
                AboutScreen(navController)
            }
            composable(route = ROUT_LOGIN) {
                LoginScreen(navController)
            }
            composable(route = ROUT_SPLASH) {
                SplashScreen(navController)
            }
            composable(route = ROUT_SIGNUP) {
                SignupScreen(navController)
            }
            composable(route = ROUT_MAP) {
                MapScreen(navController)
            }
            composable(route = ROUT_DASHBOARD) {
                DashboardScreen(navController)
            }
            composable(route = ROUT_ADD_EQUIPMENT) {
                AddEquipmentScreen(navController)
            }
            composable(route = ROUT_HISTORY) {
                HistoryScreen(navController)
            }
            composable(route = ROUT_PROFILE) {
                ProfileScreen(navController)
            }
            composable(route = ROUT_NOTIFICATIONS) {
                NotificationScreen(navController)
            }

            composable(route = "$ROUT_PAYMENT/{equipmentId}/{equipmentName}/{renterUid}/{renterPhone}/{amount}") { backStackEntry ->
                val equipmentId   = backStackEntry.arguments?.getString("equipmentId")   ?: ""
                val equipmentName = backStackEntry.arguments?.getString("equipmentName") ?: ""
                val renterUid     = backStackEntry.arguments?.getString("renterUid")     ?: ""
                val renterPhone   = backStackEntry.arguments?.getString("renterPhone")   ?: ""
                val amount        = backStackEntry.arguments?.getString("amount")?.toIntOrNull() ?: 0
                PaymentScreen(
                    navController = navController,
                    equipmentId   = equipmentId,
                    equipmentName = equipmentName,
                    renterUid     = renterUid,
                    renterPhone   = renterPhone,
                    amount        = amount
                )
            }
        }
    }
}