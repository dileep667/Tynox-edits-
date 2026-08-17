package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var hardwareAcceleration by remember { mutableStateOf(true) }
    var autoSaveEnabled by remember { mutableStateOf(true) }
    var highPrecisionScrubber by remember { mutableStateOf(true) }
    var cacheSizeMb by remember { mutableStateOf(148.5f) }
    var showClearedToast by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = TyoxBackground,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Studio Settings",
                        color = TyoxTextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.testTag("btn_settings_back")
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TyoxTextPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = TyoxSurface)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Section 1: Playback & Rendering
            Text("Engine & Hardware", color = TyoxCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)

            Card(
                colors = CardDefaults.cardColors(containerColor = TyoxSurfaceVariant),
                shape = RoundedCornerShape(10.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("GPU Hardware Acceleration", color = TyoxTextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Text("Fast preview shaders & multi-threaded export", color = TyoxTextSecondary, fontSize = 10.sp)
                        }
                        Switch(
                            checked = hardwareAcceleration,
                            onCheckedChange = { hardwareAcceleration = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = TyoxCyan, checkedTrackColor = TyoxCyanContainer)
                        )
                    }

                    Divider(color = TyoxBorder, modifier = Modifier.padding(vertical = 10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("High Precision 60 FPS Scrubber", color = TyoxTextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Text("Frame-by-frame needle snapping", color = TyoxTextSecondary, fontSize = 10.sp)
                        }
                        Switch(
                            checked = highPrecisionScrubber,
                            onCheckedChange = { highPrecisionScrubber = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = TyoxCyan, checkedTrackColor = TyoxCyanContainer)
                        )
                    }
                }
            }

            // Section 2: Storage & Cache Management
            Text("Storage & Cache", color = TyoxCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)

            Card(
                colors = CardDefaults.cardColors(containerColor = TyoxSurfaceVariant),
                shape = RoundedCornerShape(10.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Temporary Video Cache", color = TyoxTextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Text("Generated audio waveforms, LUT previews", color = TyoxTextSecondary, fontSize = 10.sp)
                        }
                        Text("${String.format("%.1f", cacheSizeMb)} MB", color = TyoxAmber, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedButton(
                        onClick = {
                            cacheSizeMb = 0f
                            showClearedToast = true
                        },
                        modifier = Modifier.fillMaxWidth().testTag("btn_clear_cache")
                    ) {
                        Icon(Icons.Default.CleaningServices, null, modifier = Modifier.size(16.dp), tint = TyoxCyan)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(if (showClearedToast) "Cache Cleared!" else "Clear Cache", fontSize = 12.sp)
                    }
                }
            }

            // Section 3: About Tyox Studio
            Text("About Tyox Studio", color = TyoxCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)

            Card(
                colors = CardDefaults.cardColors(containerColor = TyoxSurfaceVariant),
                shape = RoundedCornerShape(10.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(TyoxCyan),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("T", color = Color.Black, fontWeight = FontWeight.Black, fontSize = 20.sp)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("TYOX STUDIO PRO", color = TyoxTextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text("Version 2.4.0 • Obsidian Engine", color = TyoxTextSecondary, fontSize = 11.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Comprehensive creator video suite with multi-track editing, AI karaoke auto-captions, native TTS narration voices, custom speed curves, and 4K 60FPS export.",
                        color = TyoxTextSecondary,
                        fontSize = 11.sp,
                        lineHeight = 16.sp
                    )
                }
            }
        }
    }
}
