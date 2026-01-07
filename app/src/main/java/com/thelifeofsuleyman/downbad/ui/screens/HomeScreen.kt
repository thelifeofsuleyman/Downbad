package com.thelifeofsuleyman.downbad.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.thelifeofsuleyman.downbad.ui.components.SlantedShape
import com.thelifeofsuleyman.downbad.ui.viewmodel.HomeViewModel
import com.thelifeofsuleyman.downbad.ui.viewmodel.TimeData

@Composable
fun HomeScreen(viewModel: HomeViewModel = viewModel()) {
    val timeData by viewModel.timeTicker.collectAsState(initial = TimeData(0, 0, 0, 0))

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF104454)) // Dark Teal Background
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(60.dp))

        Text(
            text = "I've been sober for",
            color = Color.White.copy(alpha = 0.8f),
            fontSize = 18.sp,
            fontWeight = FontWeight.Light
        )

        Spacer(modifier = Modifier.height(40.dp))

        // Days (Only show if > 0)
        if (timeData.days > 0) {
            TimeProgressBlock(
                value = timeData.days.toString(),
                unit = "days",
                gradient = listOf(Color(0xFF4FC3F7), Color(0xFF0288D1)),
                progress = 1f // Days bar stays full
            )
        }

        // Hours (Fills up to 24)
        TimeProgressBlock(
            value = timeData.hours.toString(),
            unit = "hours",
            gradient = listOf(Color(0xFF29B6F6), Color(0xFF0277BD)),
            progress = timeData.hours / 24f
        )

        // Minutes (Fills up to 60)
        TimeProgressBlock(
            value = timeData.minutes.toString(),
            unit = "minutes",
            gradient = listOf(Color(0xFF4FC3F7), Color(0xFF01579B)),
            progress = timeData.minutes / 60f
        )

        // Seconds (Fills up to 60) - You will see this move live!
        TimeProgressBlock(
            value = timeData.seconds.toString(),
            unit = "seconds",
            gradient = listOf(Color(0xFF9575CD), Color(0xFF512DA8)),
            progress = timeData.seconds / 60f
        )

        Spacer(modifier = Modifier.weight(1f))

        // Bottom Bar
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            BottomAction(Icons.Default.Share, "Share")
            BottomAction(Icons.Default.DateRange, "Log Urge")
            BottomAction(Icons.Default.Refresh, "Reset") { viewModel.resetTimer() }
        }
    }
}

@Composable
fun TimeProgressBlock(value: String, unit: String, gradient: List<Color>, progress: Float) {
    Box(
        modifier = Modifier
            .padding(vertical = 10.dp)
            .fillMaxWidth(0.9f)
            .height(80.dp)
            .background(Color.Black.copy(alpha = 0.15f), shape = SlantedShape(60f)),
        contentAlignment = Alignment.CenterStart
    ) {
        // The Progress Fill
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(progress.coerceAtLeast(0.02f)) // Ensure a tiny bit is always visible
                .background(Brush.horizontalGradient(gradient), shape = SlantedShape(60f))
        )

        // The Text Label
        Row(
            modifier = Modifier.padding(start = 50.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            Text(
                text = value,
                color = Color.White,
                fontSize = 38.sp,
                fontWeight = FontWeight.Bold,
                fontStyle = FontStyle.Italic
            )
            Text(
                text = " $unit",
                color = Color.White.copy(alpha = 0.9f),
                fontSize = 22.sp,
                fontStyle = FontStyle.Italic,
                modifier = Modifier.padding(bottom = 6.dp)
            )
        }
    }
}

@Composable
fun BottomAction(icon: ImageVector, label: String, onClick: () -> Unit = {}) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }.padding(8.dp)
    ) {
        Icon(icon, contentDescription = null, tint = Color.LightGray.copy(alpha = 0.7f), modifier = Modifier.size(28.dp))
        Text(label, color = Color.LightGray.copy(alpha = 0.7f), fontSize = 12.sp)
    }
}