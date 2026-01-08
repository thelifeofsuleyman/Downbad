package com.thelifeofsuleyman.downbad.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment // FIXED: For Alignment.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.thelifeofsuleyman.downbad.ui.viewmodel.HomeViewModel
// IMPORTANT: Replace this with YOUR actual package name if different
import com.thelifeofsuleyman.downbad.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(viewModel: HomeViewModel, onBack: () -> Unit) {
    val habitName by viewModel.habitName.collectAsState()
    var textState by remember { mutableStateOf(habitName) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profile & Settings") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally // Centers the logo
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            // --- YOUR LOGO ---
            // Ensure you have put your logo in res/drawable and named it app_logo.png
            Image(
                painter = painterResource(id = R.drawable.app_logo),
                contentDescription = "App Logo",
                modifier = Modifier
                    .size(160.dp)
                    .clip(CircleShape)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // --- INPUT FIELDS ---
            Text(
                text = "What are you quitting?",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.align(Alignment.Start) // Aligns text to left
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = textState,
                onValueChange = { textState = it },
                label = { Text("Habit name (e.g. Alcohol, Sugar)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    viewModel.updateHabitName(textState)
                    onBack()
                },
                modifier = Modifier.fillMaxWidth(),
                shape = CircleShape
            ) {
                Text("Save Changes")
            }
        }
    }
}