package com.taskflow

// ============================================================
// SPLASH SCREEN - COMMENTED OUT (no longer needed)
// ============================================================

//import android.content.Intent
//import android.os.Bundle
//import androidx.activity.ComponentActivity
//import androidx.activity.compose.setContent
//import androidx.compose.foundation.background
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.size
//import androidx.compose.material3.Icon
//import androidx.compose.runtime.LaunchedEffect
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.res.painterResource
//import androidx.compose.ui.unit.dp
//import kotlinx.coroutines.delay
//
//class SplashActivity : ComponentActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContent {
//            Box(
//                modifier = Modifier
//                    .fillMaxSize()
//                    .background(Color.Black),
//                contentAlignment = Alignment.Center
//            ) {
//                Icon(
//                    painter = painterResource(id = R.drawable.icon),
//                    contentDescription = "App Icon",
//                    tint = Color.Unspecified,
//                    modifier = Modifier.size(150.dp)
//                )
//
//                LaunchedEffect(Unit) {
//                    delay(2000)
//                    startActivity(Intent(this@SplashActivity, MainActivity::class.java))
//                    finish()
//                }
//            }
//        }
//    }
//}
