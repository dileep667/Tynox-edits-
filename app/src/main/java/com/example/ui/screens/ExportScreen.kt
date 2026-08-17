package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ProjectFps
import com.example.data.model.ProjectResolution
import com.example.ui.theme.*
import com.example.viewmodel.EditorViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

enum class ExportQualityLevel(val label: String, val mbpsMultiplier: Float) {
    STANDARD("Standard (Good)", 1.0f),
    HIGH("High (Creator)", 1.5f),
    ULTRA("Ultra Master", 2.2f)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExportScreen(
    projectId: String,
    editorViewModel: EditorViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val project by editorViewModel.project.collectAsState()
    val timeline by editorViewModel.timeline.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    var selectedResolution by remember { mutableStateOf(project?.resolution ?: ProjectResolution.RES_1080P) }
    var selectedFps by remember { mutableStateOf(project?.fps ?: ProjectFps.FPS_60) }
    var selectedQuality by remember { mutableStateOf(ExportQualityLevel.HIGH) }
    var useHevc by remember { mutableStateOf(false) }

    // Export progress states
    var isExporting by remember { mutableStateOf(false) }
    var exportProgress by remember { mutableStateOf(0f) }
    var exportPhase by remember { mutableStateOf("Initializing Hardware Encoder...") }
    var isExportComplete by remember { mutableStateOf(false) }
    var currentRenderFrame by remember { mutableStateOf(0) }

    // Calculate estimated file size
    val durationSec = (timeline.totalDurationMs / 1000f).coerceAtLeast(1f)
    val baseBitrateMbps = when (selectedResolution) {
        ProjectResolution.RES_720P -> 6f
        ProjectResolution.RES_1080P -> 14f
        ProjectResolution.RES_2K -> 24f
        ProjectResolution.RES_4K -> 45f
    } * selectedQuality.mbpsMultiplier * (if (selectedFps == ProjectFps.FPS_60) 1.3f else 1.0f) * (if (useHevc) 0.65f else 1.0f)

    val estimatedSizeMb = (durationSec * baseBitrateMbps / 8f).coerceAtLeast(1.5f)
    val totalFrames = (durationSec * when (selectedFps) {
        ProjectFps.FPS_24 -> 24
        ProjectFps.FPS_25 -> 25
        ProjectFps.FPS_30 -> 30
        ProjectFps.FPS_50 -> 50
        ProjectFps.FPS_60 -> 60
    }).toInt()

    fun startExportSimulation() {
        isExporting = true
        isExportComplete = false
        exportProgress = 0f
        coroutineScope.launch {
            val phases = listOf(
                "Applying Multi-Pass Color Grading & LUTs..." to 0.25f,
                "Rendering Karaoke Animated Captions..." to 0.55f,
                "Synthesizing Audio & Beat FX Tracks..." to 0.80f,
                "Finalizing MP4 Video Container..." to 1.0f
            )

            var progress = 0f
            while (progress < 1.0f) {
                delay(60)
                progress += 0.02f
                exportProgress = progress.coerceAtMost(1f)
                currentRenderFrame = (totalFrames * exportProgress).toInt()

                exportPhase = when {
                    progress < 0.25f -> phases[0].first
                    progress < 0.55f -> phases[1].first
                    progress < 0.80f -> phases[2].first
                    else -> phases[3].first
                }
            }

            delay(300)
            isExporting = false
            isExportComplete = true
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = TyoxBackground,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isExportComplete) "Export Finished" else "Export Studio",
                        color = TyoxTextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        enabled = !isExporting,
                        modifier = Modifier.testTag("btn_export_back")
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
            if (!isExporting && !isExportComplete) {
                // Settings Selection Form
                Text("Project: ${project?.name ?: "Video"}", color = TyoxTextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Bold)

                // 1. Resolution
                Text("Export Resolution", color = TyoxTextSecondary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ProjectResolution.values().forEach { res ->
                        FilterChip(
                            selected = selectedResolution == res,
                            onClick = { selectedResolution = res },
                            label = { Text(res.label, fontSize = 11.sp) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // 2. Frame Rate (FPS)
                Text("Frame Rate", color = TyoxTextSecondary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(ProjectFps.FPS_24, ProjectFps.FPS_30, ProjectFps.FPS_60).forEach { fps ->
                        FilterChip(
                            selected = selectedFps == fps,
                            onClick = { selectedFps = fps },
                            label = { Text(fps.label, fontSize = 11.sp) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // 3. Quality Preset
                Text("Encoding Bitrate Quality", color = TyoxTextSecondary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ExportQualityLevel.values().forEach { q ->
                        FilterChip(
                            selected = selectedQuality == q,
                            onClick = { selectedQuality = q },
                            label = { Text(q.label.split(" ").first(), fontSize = 11.sp) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // 4. Codec Toggle
                Card(
                    colors = CardDefaults.cardColors(containerColor = TyoxSurfaceVariant),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("HEVC / H.265 Codec", color = TyoxTextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Text("Smaller file size with identical quality", color = TyoxTextSecondary, fontSize = 10.sp)
                        }
                        Switch(
                            checked = useHevc,
                            onCheckedChange = { useHevc = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = TyoxCyan, checkedTrackColor = TyoxCyanContainer)
                        )
                    }
                }

                // Estimated File Summary Card
                Card(
                    colors = CardDefaults.cardColors(containerColor = TyoxSurfaceVariant),
                    border = androidx.compose.foundation.BorderStroke(1.dp, TyoxBorder),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text("Estimated Export Summary", color = TyoxCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Video Duration", color = TyoxTextSecondary, fontSize = 12.sp)
                            Text("${String.format("%.1f", durationSec)} seconds", color = TyoxTextPrimary, fontSize = 12.sp)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Estimated File Size", color = TyoxTextSecondary, fontSize = 12.sp)
                            Text("~${String.format("%.1f", estimatedSizeMb)} MB", color = TyoxAmber, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Start Export Button
                Button(
                    onClick = { startExportSimulation() },
                    colors = ButtonDefaults.buttonColors(containerColor = TyoxCyan, contentColor = Color.Black),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("btn_start_export")
                ) {
                    Icon(Icons.Default.FileUpload, null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Start Fast Export (${selectedResolution.label})", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
            } else if (isExporting) {
                // Rendering Progress Mode
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .background(TyoxCyan.copy(alpha = 0.15f))
                            .border(2.dp, TyoxCyan, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${(exportProgress * 100).toInt()}%",
                            color = TyoxCyan,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "Rendering in 60 FPS...",
                        color = TyoxTextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = exportPhase,
                        color = TyoxCyan,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Frame $currentRenderFrame / $totalFrames",
                        color = TyoxTextSecondary,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    LinearProgressIndicator(
                        progress = exportProgress,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = TyoxCyan,
                        trackColor = TyoxSurfaceVariant
                    )
                }
            } else {
                // Export Complete Screen
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(TyoxGreen.copy(alpha = 0.2f))
                            .border(2.dp, TyoxGreen, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Check, contentDescription = "Done", tint = TyoxGreen, modifier = Modifier.size(36.dp))
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text("Export Ready!", color = TyoxTextPrimary, fontSize = 20.sp, fontWeight = FontWeight.Black)
                    Text("Saved to Gallery / DCIM / TyoxStudio", color = TyoxTextSecondary, fontSize = 12.sp)

                    Spacer(modifier = Modifier.height(24.dp))

                    // Social Share Buttons
                    Text("Share to Socials", color = TyoxTextSecondary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = {},
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("📱 Reels", fontSize = 11.sp)
                        }
                        OutlinedButton(
                            onClick = {},
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("🎬 Shorts", fontSize = 11.sp)
                        }
                        OutlinedButton(
                            onClick = {},
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("🎵 TikTok", fontSize = 11.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = onNavigateBack,
                        colors = ButtonDefaults.buttonColors(containerColor = TyoxCyan, contentColor = Color.Black),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth().testTag("btn_export_done_back")
                    ) {
                        Text("Back to Timeline Editor", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
