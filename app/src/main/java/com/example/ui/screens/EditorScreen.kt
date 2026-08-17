package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.AspectRatio
import com.example.ui.components.EditorBottomControlPanel
import com.example.ui.components.MultiTrackTimelineView
import com.example.ui.components.VideoPlayerPreview
import com.example.ui.theme.*
import com.example.viewmodel.EditorToolbarTab
import com.example.viewmodel.EditorViewModel
import com.example.viewmodel.SelectedTrackType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditorScreen(
    projectId: String,
    viewModel: EditorViewModel,
    onNavigateBack: () -> Unit,
    onOpenExport: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(projectId) {
        viewModel.loadProject(projectId)
    }

    val project by viewModel.project.collectAsState()
    val timeline by viewModel.timeline.collectAsState()
    val playheadMs by viewModel.playheadMs.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()
    val activeTab by viewModel.activeTab.collectAsState()
    val selectedTrackType by viewModel.selectedTrackType.collectAsState()
    val selectedItemId by viewModel.selectedItemId.collectAsState()
    val zoomFactor by viewModel.timelineZoom.collectAsState()
    val isFullscreen by viewModel.isFullscreenPreview.collectAsState()
    val statusMessage by viewModel.statusMessage.collectAsState()
    val canUndo by viewModel.canUndo.collectAsState()
    val canRedo by viewModel.canRedo.collectAsState()

    var showAddMediaDialog by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = TyoxBackground,
        topBar = {
            if (!isFullscreen) {
                TopAppBar(
                    title = {
                        Column {
                            Text(
                                text = project?.name ?: "Editor",
                                color = TyoxTextPrimary,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "${project?.resolution?.label ?: "1080P"} • ${project?.fps?.label ?: "60 FPS"}",
                                    color = TyoxTextSecondary,
                                    fontSize = 10.sp
                                )
                            }
                        }
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = onNavigateBack,
                            modifier = Modifier.testTag("btn_editor_back")
                        ) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TyoxTextPrimary)
                        }
                    },
                    actions = {
                        // Undo Button
                        IconButton(
                            onClick = { viewModel.undo() },
                            enabled = canUndo,
                            modifier = Modifier.testTag("btn_undo")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Undo,
                                contentDescription = "Undo",
                                tint = if (canUndo) TyoxTextPrimary else TyoxTextSecondary.copy(alpha = 0.3f)
                            )
                        }

                        // Redo Button
                        IconButton(
                            onClick = { viewModel.redo() },
                            enabled = canRedo,
                            modifier = Modifier.testTag("btn_redo")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Redo,
                                contentDescription = "Redo",
                                tint = if (canRedo) TyoxTextPrimary else TyoxTextSecondary.copy(alpha = 0.3f)
                            )
                        }

                        // Add Media Quick Action
                        IconButton(
                            onClick = { showAddMediaDialog = true },
                            modifier = Modifier.testTag("btn_add_media_header")
                        ) {
                            Icon(
                                imageVector = Icons.Default.AddPhotoAlternate,
                                contentDescription = "Add Media",
                                tint = TyoxCyan
                            )
                        }

                        Spacer(modifier = Modifier.width(4.dp))

                        // Export Button
                        Button(
                            onClick = { onOpenExport(projectId) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = TyoxCyan,
                                contentColor = Color.Black
                            ),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            modifier = Modifier.testTag("btn_header_export")
                        ) {
                            Icon(Icons.Default.FileUpload, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Export", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.width(8.dp))
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = TyoxSurface)
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(if (isFullscreen) PaddingValues(0.dp) else innerPadding)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Top Half: Video Player Preview (or Fullscreen)
                VideoPlayerPreview(
                    aspectRatio = project?.aspectRatio ?: AspectRatio.RATIO_9_16,
                    timeline = timeline,
                    playheadMs = playheadMs,
                    isPlaying = isPlaying,
                    isFullscreen = isFullscreen,
                    onTogglePlay = { viewModel.togglePlayPause() },
                    onSeek = { viewModel.seekTo(it) },
                    onStepFrame = { viewModel.stepFrame(it) },
                    onToggleFullscreen = { viewModel.toggleFullscreenPreview() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(if (isFullscreen) 1f else 0.85f)
                )

                if (!isFullscreen) {
                    // Middle: Multi-Track Interactive Timeline
                    MultiTrackTimelineView(
                        timeline = timeline,
                        playheadMs = playheadMs,
                        zoomFactor = zoomFactor,
                        selectedTrackType = selectedTrackType,
                        selectedItemId = selectedItemId,
                        onSeek = { viewModel.seekTo(it) },
                        onSelectItem = { trackType, id -> viewModel.selectItem(trackType, id) },
                        onZoomChange = { viewModel.setZoom(it) },
                        onSplitAtPlayhead = { viewModel.splitClipAtPlayhead() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    )

                    // Bottom: Toolbar & Expandable Tool Sheets
                    EditorBottomControlPanel(
                        viewModel = viewModel,
                        activeTab = activeTab,
                        onCloseTab = { viewModel.setActiveTab(EditorToolbarTab.NONE) }
                    )
                }
            }

            // Status Message Floating Toast
            AnimatedVisibility(
                visible = statusMessage != null,
                enter = fadeIn() + slideInVertically { -20 },
                exit = fadeOut() + slideOutVertically { -20 },
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 16.dp)
            ) {
                Surface(
                    color = TyoxSurfaceVariant.copy(alpha = 0.95f),
                    shape = RoundedCornerShape(20.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, TyoxCyan.copy(alpha = 0.5f)),
                    shadowElevation = 6.dp
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(TyoxCyan)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = statusMessage ?: "",
                            color = TyoxTextPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }

    // Add Media Dialog
    if (showAddMediaDialog) {
        AddMediaSampleDialog(
            onDismiss = { showAddMediaDialog = false },
            onSelectMedia = { resName, title ->
                showAddMediaDialog = false
                viewModel.addVideoClip(
                    uri = "res://$resName",
                    resName = resName,
                    title = title
                )
            }
        )
    }
}

@Composable
fun AddMediaSampleDialog(
    onDismiss: () -> Unit,
    onSelectMedia: (String, String) -> Unit
) {
    val sampleMedia = listOf(
        Triple("sample_cyberpunk", "Cyberpunk Neon Night", R.drawable.sample_cyberpunk),
        Triple("sample_reels_travel", "Sunrise Mountain View", R.drawable.sample_reels_travel),
        Triple("sample_podcast_host", "Studio Creator Interview", R.drawable.sample_podcast_host)
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Add Media Clip", color = TyoxTextPrimary, fontWeight = FontWeight.Bold)
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text("Select from creator library clips:", color = TyoxTextSecondary, fontSize = 12.sp)

                sampleMedia.forEach { (resName, title, drawableRes) ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = TyoxSurfaceVariant),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelectMedia(resName, title) }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(60.dp, 44.dp)
                                    .clip(RoundedCornerShape(6.dp))
                            ) {
                                androidx.compose.foundation.Image(
                                    painter = painterResource(id = drawableRes),
                                    contentDescription = title,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = androidx.compose.ui.layout.ContentScale.Crop
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(title, color = TyoxTextPrimary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                                Text("4K Ultra HD • 60 FPS", color = TyoxCyan, fontSize = 10.sp)
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TyoxTextSecondary)
            }
        },
        containerColor = TyoxSurface
    )
}
