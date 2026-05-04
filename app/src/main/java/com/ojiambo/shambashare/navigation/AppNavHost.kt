package com.ojiambo.shambashare.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ojiambo.shambashare.ui.screens.about.AboutScreen
import com.ojiambo.shambashare.ui.screens.auth.LoginScreen
import com.ojiambo.shambashare.ui.screens.auth.SignupScreen
import com.ojiambo.shambashare.ui.screens.dashboard.DashboardScreen
import com.ojiambo.shambashare.ui.screens.equipment.AddEquipmentScreen
import com.ojiambo.shambashare.ui.screens.history.HistoryScreen
import com.ojiambo.shambashare.ui.screens.map.MapScreen
import com.ojiambo.shambashare.ui.screens.profile.ProfileScreen
import com.ojiambo.shambashare.ui.screens.splash.SplashScreen

@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = ROUT_SPLASH
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {

        composable(ROUT_ABOUT) {
            AboutScreen(navController)
        }
        composable(ROUT_LOGIN) {
            LoginScreen(navController)
        }
        composable(ROUT_SPLASH) {
            SplashScreen(navController)
        }
        composable(ROUT_SIGNUP) {
            SignupScreen(navController)
        }
        composable(ROUT_MAP) {
            MapScreen(navController)
        }
        composable(ROUT_DASHBOARD) {
            DashboardScreen(navController)
        }
        composable(ROUT_ADD_EQUIPMENT) {
            AddEquipmentScreen(navController)
        }
        composable(ROUT_HISTORY) {
            HistoryScreen(navController)
        }
        composable(ROUT_PROFILE) {
            ProfileScreen(navController)
        }

    }

  }