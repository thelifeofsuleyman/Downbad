package com.thelifeofsuleyman.downbad.ui.screens

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.thelifeofsuleyman.downbad.ui.components.SlantedShape
import com.thelifeofsuleyman.downbad.ui.viewmodel.HomeViewModel
import com.thelifeofsuleyman.downbad.ui.viewmodel.TimeData
import java.time.LocalDate
import java.time.LocalDateTime

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = viewModel(),
    onNavigateToSettings: () -> Unit
) {
    val context = LocalContext.current
    val timeData by viewModel.timeTicker.collectAsState(initial = TimeData(0, 0, 0, 0))
    val habitName by viewModel.habitName.collectAsState()

    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var tempDate by remember { mutableStateOf<LocalDate?>(null) }

    // --- DIALOG LOGIC (FIXED) ---

    // This block ONLY runs once when showDatePicker becomes true.
    // It ignores the timer ticking.
    LaunchedEffect(showDatePicker) {
        if (showDatePicker) {
            val now = LocalDateTime.now()
            val picker = android.app.DatePickerDialog(
                context,
                { _, y, m, d ->
                    tempDate = LocalDate.of(y, m + 1, d)
                    showDatePicker = false
                    showTimePicker = true
                },
                now.year, now.monthValue - 1, now.dayOfMonth
            ).apply {
                // BLOCK FUTURE DATES:
                // This makes every date after 'today' greyed out and unclickable.
                datePicker.maxDate = System.currentTimeMillis()

                setOnDismissListener { showDatePicker = false }
            }
            picker.show()
        }
    }

    LaunchedEffect(showTimePicker) {
        if (showTimePicker) {
            val now = LocalDateTime.now()
            android.app.TimePickerDialog(
                context,
                { _, hr, min ->
                    tempDate?.let { date ->
                        var combined = LocalDateTime.of(date.year, date.month, date.dayOfMonth, hr, min)

                        // BLOCK FUTURE TIME:
                        // If the user picked "Today" but a time that hasn't happened yet,
                        // we force the start time to be "Now".
                        if (combined.isAfter(LocalDateTime.now())) {
                            combined = LocalDateTime.now()
                            android.widget.Toast.makeText(context, "Start time cannot be in the future. Set to 'Now'.", android.widget.Toast.LENGTH_SHORT).show()
                        }

                        viewModel.updateStartTime(combined)
                    }
                    showTimePicker = false
                },
                now.hour, now.minute, true
            ).apply {
                setOnDismissListener { showTimePicker = false }
                show()
            }
        }
    }

    // --- UI CODE ---
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF104454))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(60.dp))

        Text(
            text = "I've been $habitName free for",
            color = Color.White.copy(alpha = 0.8f),
            fontSize = 18.sp
        )

        Spacer(modifier = Modifier.height(40.dp))

        // Slanted Bars
        if (timeData.days > 0) {
            TimeProgressBlock(timeData.days.toString(), "days", listOf(Color(0xFF4FC3F7), Color(0xFF0288D1)), 1f)
        }
        TimeProgressBlock(timeData.hours.toString(), "hours", listOf(Color(0xFF29B6F6), Color(0xFF0277BD)), timeData.hours / 24f)
        TimeProgressBlock(timeData.minutes.toString(), "minutes", listOf(Color(0xFF4FC3F7), Color(0xFF01579B)), timeData.minutes / 60f)
        TimeProgressBlock(timeData.seconds.toString(), "seconds", listOf(Color(0xFF9575CD), Color(0xFF512DA8)), timeData.seconds / 60f)

        Spacer(modifier = Modifier.weight(1f))

        // Bottom Buttons
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            BottomAction(Icons.Default.Share, "Share") {
                // Shared text logic (Omitted for brevity, keep your pleasant share logic here)
            }
            BottomAction(Icons.Default.DateRange, "Set Date") {
                showDatePicker = true
            }
            BottomAction(Icons.Default.Person, "Profile") {
                onNavigateToSettings()
            }
            BottomAction(Icons.Default.Refresh, "Reset") {
                viewModel.resetToNow()
            }
        }
    }
}

@Composable
fun TimeProgressBlock(value: String, unit: String, gradient: List<Color>, progress: Float) {
    Box(
        modifier = Modifier.padding(vertical = 10.dp).fillMaxWidth(0.9f).height(80.dp)
            .background(Color.Black.copy(alpha = 0.15f), shape = SlantedShape(60f)),
        contentAlignment = Alignment.CenterStart
    ) {
        Box(
            modifier = Modifier.fillMaxHeight().fillMaxWidth(progress.coerceIn(0.02f, 1f))
                .background(Brush.horizontalGradient(gradient), shape = SlantedShape(60f))
        )
        Row(modifier = Modifier.padding(start = 50.dp), verticalAlignment = Alignment.Bottom) {
            Text(value, color = Color.White, fontSize = 38.sp, fontWeight = FontWeight.Bold, fontStyle = FontStyle.Italic)
            Text(" $unit", color = Color.White.copy(alpha = 0.9f), fontSize = 22.sp, fontStyle = FontStyle.Italic, modifier = Modifier.padding(bottom = 6.dp))
        }
    }
}

@Composable
fun BottomAction(icon: ImageVector, label: String, onClick: () -> Unit = {}) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable { onClick() }.padding(8.dp)) {
        Icon(icon, contentDescription = null, tint = Color.LightGray.copy(alpha = 0.7f), modifier = Modifier.size(28.dp))
        Text(label, color = Color.LightGray.copy(alpha = 0.7f), fontSize = 12.sp)
    }
}