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
import androidx.compose.ui.text.style.TextAlign
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

    // UI States
    var showResetDialog by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var tempDate by remember { mutableStateOf<LocalDate?>(null) }

    // --- DIALOG LOGIC (FIXED TO PREVENT RECOMPOSITION LOOPS) ---

    LaunchedEffect(showDatePicker) {
        if (showDatePicker) {
            val now = LocalDateTime.now()
            android.app.DatePickerDialog(
                context,
                { _, y, m, d ->
                    tempDate = LocalDate.of(y, m + 1, d)
                    showDatePicker = false
                    showTimePicker = true
                },
                now.year, now.monthValue - 1, now.dayOfMonth
            ).apply {
                datePicker.maxDate = System.currentTimeMillis()
                setOnDismissListener { showDatePicker = false }
                show()
            }
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

    // --- RESET CONFIRMATION DIALOG ---
    if (showResetDialog) {
        ResetConfirmationDialog(
            onConfirm = {
                viewModel.resetToNow()
                showResetDialog = false
            },
            onDismiss = { showResetDialog = false }
        )
    }

    // --- MAIN UI ---
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF104454))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(60.dp))

        Text(
            text = "I've been $habitName free for PEYSER EX ANAVI SIKIM noqte",
            color = Color.White.copy(alpha = 0.8f),
            fontSize = 18.sp
        )

        Spacer(modifier = Modifier.height(40.dp))

        // Slanted Progress Bars
        if (timeData.days > 0) {
            TimeProgressBlock(timeData.days.toString(), "days", listOf(Color(0xFF4FC3F7), Color(0xFF0288D1)), 1f)
        }
        TimeProgressBlock(timeData.hours.toString(), "hours", listOf(Color(0xFF29B6F6), Color(0xFF0277BD)), timeData.hours / 24f)
        TimeProgressBlock(timeData.minutes.toString(), "minutes", listOf(Color(0xFF4FC3F7), Color(0xFF01579B)), timeData.minutes / 60f)
        TimeProgressBlock(timeData.seconds.toString(), "seconds", listOf(Color(0xFF9575CD), Color(0xFF512DA8)), timeData.seconds / 60f)

        Spacer(modifier = Modifier.weight(1f))

        // Bottom Navigation Buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            // 1. Pleasant Share Logic
            BottomAction(Icons.Default.Share, "Share") {
                val dynamicShareText = buildString {
                    append("I've been $habitName free for ")
                    if (timeData.days > 0) {
                        append("${timeData.days} days and ${timeData.hours} hours")
                    } else {
                        if (timeData.hours > 0) append("${timeData.hours}h ")
                        if (timeData.minutes > 0) append("${timeData.minutes}m ")
                        append("${timeData.seconds}s")
                    }
                    append("! #DownBad")
                }
                val intent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, dynamicShareText)
                }
                context.startActivity(Intent.createChooser(intent, "Share progress"))
            }

            // 2. Set Date
            BottomAction(Icons.Default.DateRange, "Set Date") {
                showDatePicker = true
            }

            // 3. Profile Navigation
            BottomAction(Icons.Default.Person, "Profile") {
                onNavigateToSettings()
            }

            // 4. Reset with Confirmation
            BottomAction(Icons.Default.Refresh, "Reset") {
                showResetDialog = true
            }
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
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(progress.coerceIn(0.02f, 1f))
                .background(Brush.horizontalGradient(gradient), shape = SlantedShape(60f))
        )
        Row(modifier = Modifier.padding(start = 50.dp), verticalAlignment = Alignment.Bottom) {
            Text(value, color = Color.White, fontSize = 38.sp, fontWeight = FontWeight.Bold, fontStyle = FontStyle.Italic)
            Text(" $unit", color = Color.White.copy(alpha = 0.9f), fontSize = 22.sp, fontStyle = FontStyle.Italic, modifier = Modifier.padding(bottom = 6.dp))
        }
    }
}

@Composable
fun ResetConfirmationDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
    androidx.compose.ui.window.Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = androidx.compose.foundation.shape.RoundedCornerShape(28.dp),
            color = Color(0xFF1B5E6B),
            tonalElevation = 8.dp,
            modifier = Modifier.padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(Icons.Default.Refresh, null, tint = Color(0xFFFF8A80), modifier = Modifier.size(48.dp))
                Spacer(modifier = Modifier.height(16.dp))
                Text("Are you sure?", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    "This will reset your current progress to zero. It's okay to start over—every day is a new beginning.",
                    color = Color.White.copy(alpha = 0.8f),
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp,
                    fontSize = 15.sp
                )
                Spacer(modifier = Modifier.height(24.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.1f)),
                        modifier = Modifier.weight(1f),
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
                    ) {
                        Text("Keep Going", color = Color.White)
                    }
                    Button(
                        onClick = onConfirm,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5252)),
                        modifier = Modifier.weight(1f),
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
                    ) {
                        Text("Reset Now", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun BottomAction(icon: ImageVector, label: String, onClick: () -> Unit = {}) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable { onClick() }
            .padding(8.dp)
    ) {
        Icon(icon, null, tint = Color.LightGray.copy(alpha = 0.7f), modifier = Modifier.size(28.dp))
        Text(label, color = Color.LightGray.copy(alpha = 0.7f), fontSize = 12.sp)
    }
}