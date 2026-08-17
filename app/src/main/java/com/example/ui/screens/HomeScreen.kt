package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.*
import com.example.ui.theme.*
import com.example.viewmodel.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onOpenEditor: (String) -> Unit,
    onOpenTemplates: () -> Unit,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val projects by viewModel.allProjects.collectAsState()
    var showNewProjectDialog by remember { mutableStateOf(false) }
    var projectToRename by remember { mutableStateOf<ProjectEntity?>(null) }
    var newProjectNameInput by remember { mutableStateOf("") }

    Scaffold(
        modifier = modifier.background(TyoxBackground),
        containerColor = TyoxBackground,
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    Brush.linearGradient(
                                        listOf(TyoxCyan, TyoxViolet)
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("T", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "TYOX STUDIO",
                                color = TyoxTextPrimary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = "Pro Mobile Video Editor",
                                color = TyoxCyan,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                },
                actions = {
                    IconButton(
                        onClick = onOpenSettings,
                        modifier = Modifier.testTag("btn_home_settings")
                    ) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings", tint = TyoxTextSecondary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = TyoxSurface)
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showNewProjectDialog = true },
                containerColor = TyoxCyan,
                contentColor = Color.Black,
                icon = { Icon(Icons.Default.Add, contentDescription = "New Project") },
                text = { Text("New Project", fontWeight = FontWeight.Bold) },
                modifier = Modifier.testTag("btn_fab_new_project")
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(4.dp))
                // Quick AI Tools Row Banner
                Text(
                    text = "AI Quick Tools",
                    color = TyoxTextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    item {
                        QuickToolCard(
                            title = "Auto Captions",
                            subtitle = "Viral word sync",
                            icon = Icons.Default.Subtitles,
                            accentColor = TyoxCyan,
                            tag = "card_quick_captions",
                            onClick = {
                                viewModel.createNewProject("Auto Caption Project", AspectRatio.RATIO_9_16, ProjectResolution.RES_1080P, ProjectFps.FPS_60) {
                                    onOpenEditor(it)
                                }
                            }
                        )
                    }
                    item {
                        QuickToolCard(
                            title = "AI Voiceover",
                            subtitle = "11 Studio voices",
                            icon = Icons.Default.RecordVoiceOver,
                            accentColor = TyoxAmber,
                            tag = "card_quick_tts",
                            onClick = {
                                viewModel.createNewProject("AI Voiceover Project", AspectRatio.RATIO_9_16, ProjectResolution.RES_1080P, ProjectFps.FPS_30) {
                                    onOpenEditor(it)
                                }
                            }
                        )
                    }
                    item {
                        QuickToolCard(
                            title = "Viral Templates",
                            subtitle = "Ready layouts",
                            icon = Icons.Default.AutoAwesomeMotion,
                            accentColor = TyoxViolet,
                            tag = "card_quick_templates",
                            onClick = onOpenTemplates
                        )
                    }
                }
            }

            item {
                // Section Title: Recent Projects
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "My Projects (${projects.size})",
                        color = TyoxTextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            if (projects.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(TyoxSurfaceVariant)
                            .border(1.dp, TyoxBorder, RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.VideoLibrary, null, tint = TyoxTextSecondary, modifier = Modifier.size(40.dp))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("No projects yet", color = TyoxTextPrimary, fontWeight = FontWeight.SemiBold)
                            Text("Tap 'New Project' below to start editing", color = TyoxTextSecondary, fontSize = 12.sp)
                        }
                    }
                }
            } else {
                items(projects, key = { it.id }) { proj ->
                    ProjectCardItem(
                        project = proj,
                        onClick = { onOpenEditor(proj.id) },
                        onRename = {
                            projectToRename = proj
                            newProjectNameInput = proj.name
                        },
                        onDuplicate = { viewModel.duplicateProject(proj.id) },
                        onDelete = { viewModel.deleteProject(proj.id) }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }

    // New Project Creator Dialog
    if (showNewProjectDialog) {
        NewProjectDialog(
            onDismiss = { showNewProjectDialog = false },
            onCreate = { name, aspect, res, fps ->
                showNewProjectDialog = false
                viewModel.createNewProject(name, aspect, res, fps) { newId ->
                    onOpenEditor(newId)
                }
            }
        )
    }

    // Rename Dialog
    if (projectToRename != null) {
        AlertDialog(
            onDismissRequest = { projectToRename = null },
            title = { Text("Rename Project", color = TyoxTextPrimary) },
            text = {
                OutlinedTextField(
                    value = newProjectNameInput,
                    onValueChange = { newProjectNameInput = it },
                    singleLine = true,
                    label = { Text("Project Name") }
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        projectToRename?.let { viewModel.renameProject(it.id, newProjectNameInput) }
                        projectToRename = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = TyoxCyan, contentColor = Color.Black)
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { projectToRename = null }) {
                    Text("Cancel", color = TyoxTextSecondary)
                }
            },
            containerColor = TyoxSurface
        )
    }
}

@Composable
fun QuickToolCard(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    accentColor: Color,
    tag: String,
    onClick: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = TyoxSurfaceVariant),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, TyoxBorder),
        modifier = Modifier
            .width(135.dp)
            .clickable { onClick() }
            .testTag(tag)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(accentColor.copy(alpha = 0.15f))
                    .border(1.dp, accentColor.copy(alpha = 0.5f), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = title, tint = accentColor, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(title, color = TyoxTextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            Text(subtitle, color = TyoxTextSecondary, fontSize = 10.sp)
        }
    }
}

@Composable
fun ProjectCardItem(
    project: ProjectEntity,
    onClick: () -> Unit,
    onRename: () -> Unit,
    onDuplicate: () -> Unit,
    onDelete: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    Card(
        colors = CardDefaults.cardColors(containerColor = TyoxSurfaceVariant),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, TyoxBorder),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("project_item_${project.id}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Thumbnail Preview
            val resId = when (project.thumbnailUri) {
                "sample_cyberpunk" -> R.drawable.sample_cyberpunk
                "sample_reels_travel" -> R.drawable.sample_reels_travel
                "sample_podcast_host" -> R.drawable.sample_podcast_host
                else -> R.drawable.sample_cyberpunk
            }

            Box(
                modifier = Modifier
                    .size(68.dp, 80.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.Black)
            ) {
                androidx.compose.foundation.Image(
                    painter = painterResource(id = resId),
                    contentDescription = project.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = androidx.compose.ui.layout.ContentScale.Crop
                )

                // Duration badge
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(4.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color.Black.copy(alpha = 0.75f))
                        .padding(horizontal = 4.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "${project.durationMs / 1000}s",
                        color = Color.White,
                        fontSize = 9.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Details
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = project.name,
                    color = TyoxTextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Aspect ratio badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(TyoxCyan.copy(alpha = 0.15f))
                            .padding(horizontal = 5.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = project.aspectRatio.label.split(" ").first(),
                            color = TyoxCyan,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    Text(
                        text = "${project.resolution.label} • ${project.fps.label}",
                        color = TyoxTextSecondary,
                        fontSize = 11.sp
                    )
                }
            }

            // Options menu
            Box {
                IconButton(onClick = { showMenu = true }) {
                    Icon(Icons.Default.MoreVert, contentDescription = "Options", tint = TyoxTextSecondary)
                }

                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false },
                    modifier = Modifier.background(TyoxSurface)
                ) {
                    DropdownMenuItem(
                        text = { Text("Rename", color = TyoxTextPrimary) },
                        leadingIcon = { Icon(Icons.Default.Edit, null, tint = TyoxCyan) },
                        onClick = {
                            showMenu = false
                            onRename()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Duplicate", color = TyoxTextPrimary) },
                        leadingIcon = { Icon(Icons.Default.ContentCopy, null, tint = TyoxAmber) },
                        onClick = {
                            showMenu = false
                            onDuplicate()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Delete", color = TyoxCoral) },
                        leadingIcon = { Icon(Icons.Default.Delete, null, tint = TyoxCoral) },
                        onClick = {
                            showMenu = false
                            onDelete()
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun NewProjectDialog(
    onDismiss: () -> Unit,
    onCreate: (String, AspectRatio, ProjectResolution, ProjectFps) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var selectedAspect by remember { mutableStateOf(AspectRatio.RATIO_9_16) }
    var selectedRes by remember { mutableStateOf(ProjectResolution.RES_1080P) }
    var selectedFps by remember { mutableStateOf(ProjectFps.FPS_60) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Create New Project", color = TyoxTextPrimary, fontWeight = FontWeight.Bold)
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Project Title (Optional)") },
                    placeholder = { Text("Viral Reel #1") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("input_project_name")
                )

                // Aspect Ratio Selector
                Text("Canvas Aspect Ratio", color = TyoxTextSecondary, fontSize = 12.sp)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    AspectRatio.values().forEach { aspect ->
                        FilterChip(
                            selected = selectedAspect == aspect,
                            onClick = { selectedAspect = aspect },
                            label = { Text(aspect.label.split(" ").first(), fontSize = 11.sp) }
                        )
                    }
                }

                // Resolution
                Text("Export Resolution", color = TyoxTextSecondary, fontSize = 12.sp)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    ProjectResolution.values().forEach { res ->
                        FilterChip(
                            selected = selectedRes == res,
                            onClick = { selectedRes = res },
                            label = { Text(res.label, fontSize = 11.sp) }
                        )
                    }
                }

                // Frame Rate (FPS)
                Text("Target Frame Rate", color = TyoxTextSecondary, fontSize = 12.sp)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    ProjectFps.values().forEach { fps ->
                        FilterChip(
                            selected = selectedFps == fps,
                            onClick = { selectedFps = fps },
                            label = { Text(fps.label, fontSize = 11.sp) }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onCreate(name, selectedAspect, selectedRes, selectedFps) },
                colors = ButtonDefaults.buttonColors(containerColor = TyoxCyan, contentColor = Color.Black),
                modifier = Modifier.testTag("btn_confirm_create_project")
            ) {
                Text("Start Editing", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TyoxTextSecondary)
            }
        },
        containerColor = TyoxSurface
    )
}
