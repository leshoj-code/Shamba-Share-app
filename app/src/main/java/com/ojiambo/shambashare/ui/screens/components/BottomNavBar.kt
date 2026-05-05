package com.ojiambo.shambashare.ui.components

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Agriculture
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.ojiambo.shambashare.navigation.ROUT_DASHBOARD
import com.ojiambo.shambashare.navigation.ROUT_HISTORY
import com.ojiambo.shambashare.navigation.ROUT_MAP
import com.ojiambo.shambashare.navigation.ROUT_NOTIFICATIONS
import com.ojiambo.shambashare.navigation.ROUT_PROFILE
import com.ojiambo.shambashare.ui.theme.ShambaGreen

data class NavItem(
    val label: String,
    val icon: ImageVector,
    val route: String,
    val badgeCount: Int = 0
)

@Composable
fun BottomNavBar(
    navController: NavController,
    unreadCount: Int = 0
) {
    val navItems = listOf(
        NavItem("Home",     Icons.Default.Home,        ROUT_DASHBOARD),
        NavItem("Map",      Icons.Default.Map,         ROUT_MAP),
        NavItem("Alerts",   Icons.Default.Agriculture, ROUT_NOTIFICATIONS, unreadCount),
        NavItem("History",  Icons.Default.History,     ROUT_HISTORY),
        NavItem("Profile",  Icons.Default.Person,      ROUT_PROFILE),
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(
        containerColor = Color.White,
        contentColor = ShambaGreen,
        modifier = Modifier.height(64.dp)
    ) {
        navItems.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.route,
                onClick = {
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            popUpTo(ROUT_DASHBOARD) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                icon = {
                    BadgedBox(
                        badge = {
                            if (item.badgeCount > 0) {
                                Badge(containerColor = Color(0xFFFF5722)) {
                                    Text(
                                        text = "${item.badgeCount}",
                                        color = Color.White,
                                        fontSize = 10.sp
                                    )
                                }
                            }
                        }
                    ) {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.label,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                },
                label = {
                    Text(
                        text = item.label,
                        fontSize = 10.sp,
                        fontWeight = if (currentRoute == item.route)
                            FontWeight.Bold
                        else
                            FontWeight.Normal
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = ShambaGreen,
                    selectedTextColor = ShambaGreen,
                    unselectedIconColor = Color.Gray,
                    unselectedTextColor = Color.Gray,
                    indicatorColor = ShambaGreen.copy(alpha = 0.1f)
                )
            )
        }
    }
}