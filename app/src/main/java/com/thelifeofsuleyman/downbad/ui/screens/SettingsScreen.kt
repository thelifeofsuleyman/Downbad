package com.thelifeofsuleyman.downbad.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.SystemUpdate
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.thelifeofsuleyman.downbad.ui.viewmodel.HomeViewModel
import com.thelifeofsuleyman.downbad.data.UpdateManager
import com.thelifeofsuleyman.downbad.data.UpdateStatus
import com.thelifeofsuleyman.downbad.R
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(viewModel: HomeViewModel, onBack: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Data States
    val habitName by viewModel.habitName.collectAsState()
    var textState by remember { mutableStateOf(habitName) }

    // Update States
    val updateManager = remember { UpdateManager(context) }
    var updateStatus by remember { mutableStateOf<UpdateStatus?>(null) }
    var isChecking by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profile & Settings", fontWeight = FontWeight.Bold) },
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
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()), // Allows scrolling on smaller phones
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            // --- 1. BRANDING LOGO ---
            Image(
                painter = painterResource(id = R.drawable.app_logo),
                contentDescription = "App Logo",
                modifier = Modifier
                    .size(150.dp)
                    .clip(CircleShape)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // --- 2. HABIT CUSTOMIZATION ---
            Text(
                text = "What are you quitting?",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = textState,
                onValueChange = { textState = it },
                label = { Text("Example: Alcohol, Sugar, Junk Food") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    viewModel.updateHabitName(textState)
                    onBack()
                },
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(16.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Save Changes", fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(40.dp))
            HorizontalDivider(color = Color.Gray.copy(alpha = 0.2f))
            Spacer(modifier = Modifier.height(24.dp))

            // --- 3. GITHUB UPDATES SECTION ---
            Text(
                text = "App Updates",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedButton(
                onClick = {
                    scope.launch {
                        isChecking = true
                        updateStatus = updateManager.checkForUpdates()
                        isChecking = false
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                enabled = !isChecking
            ) {
                if (isChecking) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                } else {
                    Icon(Icons.Default.SystemUpdate, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Check for GitHub Updates")
                }
            }

            // Status Messages
            updateStatus?.let { status ->
                Spacer(modifier = Modifier.height(12.dp))
                when (status) {
                    is UpdateStatus.UpToDate -> {
                        Text("You are on the latest version!", color = Color(0xFF4CAF50), fontSize = 14.sp)
                    }
                    is UpdateStatus.Error -> {
                        Text("Check failed: ${status.message}", color = Color.Red, fontSize = 14.sp)
                    }
                    is UpdateStatus.Available -> {
                        // Dialog is handled below
                    }
                }
            }
        }
    }

    // Update Found Dialog
    if (updateStatus is UpdateStatus.Available) {
        val availableStatus = updateStatus as UpdateStatus.Available
        AlertDialog(
            onDismissRequest = { updateStatus = null },
            title = { Text("New Version Found") },
            text = { Text("Version ${availableStatus.version} is available. Would you like to download the APK?") },
            confirmButton = {
                Button(onClick = {
                    updateManager.downloadAndInstall(availableStatus.url)
                    updateStatus = null
                }) {
                    Text("Download")
                }
            },
            dismissButton = {
                TextButton(onClick = { updateStatus = null }) {
                    Text("Later")
                }
            }
        )
    }
}