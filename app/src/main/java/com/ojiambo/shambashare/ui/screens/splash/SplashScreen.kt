package com.ojiambo.shambashare.ui.screens.splash

import androidx.compose.animation.core.EaseOutBack
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ojiambo.shambashare.R
import com.ojiambo.shambashare.ui.theme.ShambaGreen
import com.ojiambo.shambashare.ui.theme.ShambaGreenLight
import com.ojiambo.shambashare.ui.theme.ShambaGreenPale
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController){

    // Animation states
    var startAnimation by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0.7f,
        animationSpec = tween(durationMillis = 1200, easing = EaseOutBack)
    )

    val alpha by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(durationMillis = 1200)
    )

    // Launch effect for animation + navigation
    LaunchedEffect(true) {
        startAnimation = true
        delay(2500)
    }

    // ShambaShare green gradient
    val gradient = Brush.verticalGradient(
        colors = listOf(
            ShambaGreen,        // #1a6b3c — dark green top
            ShambaGreenLight    // #2d9e5f — lighter green bottom
        )
    )

    Column(
        modifier = Modifier.run {
            background(brush = gradient)
                .fillMaxSize()
        },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {

        // Logo with animation
        Image(
            painter = painterResource(R.drawable.splash),
            contentDescription = "ShambaShare Logo",
            modifier = Modifier
                .size(220.dp)
                .scale(scale)
                .alpha(alpha)
        )

        Spacer(modifier = Modifier.height(30.dp))

        // Loading indicator (subtle)
        CircularProgressIndicator(
            color = androidx.compose.ui.graphics.Color.White,
            strokeWidth = 3.dp,
            modifier = Modifier.size(30.dp)
        )
    }

    // Tagline
    Text(
        text = "Rent farm equipment,\ngrow together.",
        color = ShambaGreenPale,
        fontSize = 14.sp,
        fontWeight = FontWeight.Medium,
        textAlign = TextAlign.Center,
        lineHeight = 20.sp,
        modifier = Modifier.alpha(alpha)
    )

    Spacer(modifier = Modifier.height(48.dp))


}



@Preview(showBackground = true)
@Composable
fun SplashScreenPreview(){
    SplashScreen(rememberNavController())
}