package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.engine.*
import com.example.ui.theme.*
import com.example.viewmodel.EditorToolbarTab
import com.example.viewmodel.EditorViewModel

@Composable
fun EditorBottomControlPanel(
    viewModel: EditorViewModel,
    activeTab: EditorToolbarTab,
    onCloseTab: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = TyoxSurface,
        tonalElevation = 8.dp
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Header for active tool sheet
            if (activeTab != EditorToolbarTab.NONE) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(TyoxSurfaceVariant)
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = when (activeTab) {
                            EditorToolbarTab.EDIT -> "Clip Actions"
                            EditorToolbarTab.AUDIO -> "Audio & Sound Effects"
                            EditorToolbarTab.TEXT -> "Text & Typography"
                            EditorToolbarTab.CAPTIONS -> "Auto Captions & Subtitles"
                            EditorToolbarTab.TTS -> "AI Text-to-Speech"
                            EditorToolbarTab.FILTERS -> "Filters & Presets"
                            EditorToolbarTab.ADJUST -> "Color Adjustments"
                            EditorToolbarTab.EFFECTS -> "Visual FX & Transitions"
                            EditorToolbarTab.STICKERS -> "Stickers & Badges"
                            EditorToolbarTab.AI_TOOLS -> "AI Magic Tools"
                            else -> ""
                        },
                        color = TyoxTextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )

                    IconButton(
                        onClick = onCloseTab,
                        modifier = Modifier.size(28.dp).testTag("btn_close_tool_sheet")
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TyoxTextSecondary)
                    }
                }
                Divider(color = TyoxBorder)
            }

            // Sheet Body
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(if (activeTab != EditorToolbarTab.NONE) 220.dp else 0.dp)
            ) {
                when (activeTab) {
                    EditorToolbarTab.EDIT -> ClipEditToolSheet(viewModel)
                    EditorToolbarTab.AUDIO -> AudioStudioToolSheet(viewModel)
                    EditorToolbarTab.TEXT -> TextStudioToolSheet(viewModel)
                    EditorToolbarTab.CAPTIONS -> CaptionsStudioToolSheet(viewModel)
                    EditorToolbarTab.TTS -> TtsStudioToolSheet(viewModel)
                    EditorToolbarTab.FILTERS -> FiltersToolSheet(viewModel)
                    EditorToolbarTab.ADJUST -> AdjustmentsToolSheet(viewModel)
                    EditorToolbarTab.EFFECTS -> EffectsAndTransitionsToolSheet(viewModel)
                    EditorToolbarTab.STICKERS -> StickersToolSheet(viewModel)
                    EditorToolbarTab.AI_TOOLS -> AiMagicToolsSheet(viewModel)
                    else -> {}
                }
            }

            // Primary Bottom Toolbar Icons
            EditorMainToolbar(
                activeTab = activeTab,
                onTabSelect = { tab -> viewModel.setActiveTab(tab) }
            )
        }
    }
}

@Composable
fun EditorMainToolbar(
    activeTab: EditorToolbarTab,
    onTabSelect: (EditorToolbarTab) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(TyoxBackground)
            .horizontalScroll(scrollState)
            .padding(horizontal = 8.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        ToolbarItem(
            icon = Icons.Default.Tune,
            label = "Edit",
            isSelected = activeTab == EditorToolbarTab.EDIT,
            tag = "tab_edit",
            onClick = { onTabSelect(EditorToolbarTab.EDIT) }
        )
        ToolbarItem(
            icon = Icons.Default.MusicNote,
            label = "Audio",
            isSelected = activeTab == EditorToolbarTab.AUDIO,
            tag = "tab_audio",
            onClick = { onTabSelect(EditorToolbarTab.AUDIO) }
        )
        ToolbarItem(
            icon = Icons.Default.Subtitles,
            label = "Captions",
            isSelected = activeTab == EditorToolbarTab.CAPTIONS,
            tag = "tab_captions",
            onClick = { onTabSelect(EditorToolbarTab.CAPTIONS) }
        )
        ToolbarItem(
            icon = Icons.Default.TextFields,
            label = "Text",
            isSelected = activeTab == EditorToolbarTab.TEXT,
            tag = "tab_text",
            onClick = { onTabSelect(EditorToolbarTab.TEXT) }
        )
        ToolbarItem(
            icon = Icons.Default.RecordVoiceOver,
            label = "TTS Voice",
            isSelected = activeTab == EditorToolbarTab.TTS,
            tag = "tab_tts",
            onClick = { onTabSelect(EditorToolbarTab.TTS) }
        )
        ToolbarItem(
            icon = Icons.Default.AutoAwesome,
            label = "AI Magic",
            isSelected = activeTab == EditorToolbarTab.AI_TOOLS,
            tag = "tab_ai",
            onClick = { onTabSelect(EditorToolbarTab.AI_TOOLS) }
        )
        ToolbarItem(
            icon = Icons.Default.PhotoFilter,
            label = "Filters",
            isSelected = activeTab == EditorToolbarTab.FILTERS,
            tag = "tab_filters",
            onClick = { onTabSelect(EditorToolbarTab.FILTERS) }
        )
        ToolbarItem(
            icon = Icons.Default.Contrast,
            label = "Adjust",
            isSelected = activeTab == EditorToolbarTab.ADJUST,
            tag = "tab_adjust",
            onClick = { onTabSelect(EditorToolbarTab.ADJUST) }
        )
        ToolbarItem(
            icon = Icons.Default.AutoFixHigh,
            label = "Effects",
            isSelected = activeTab == EditorToolbarTab.EFFECTS,
            tag = "tab_effects",
            onClick = { onTabSelect(EditorToolbarTab.EFFECTS) }
        )
        ToolbarItem(
            icon = Icons.Default.EmojiEmotions,
            label = "Stickers",
            isSelected = activeTab == EditorToolbarTab.STICKERS,
            tag = "tab_stickers",
            onClick = { onTabSelect(EditorToolbarTab.STICKERS) }
        )
    }
}

@Composable
fun ToolbarItem(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    tag: String,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) TyoxCyan.copy(alpha = 0.15f) else Color.Transparent)
            .clickable { onClick() }
            .padding(horizontal = 10.dp, vertical = 6.dp)
            .testTag(tag),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (isSelected) TyoxCyan else TyoxTextSecondary,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            color = if (isSelected) TyoxCyan else TyoxTextSecondary,
            fontSize = 10.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

// ==================== 1. CLIP EDIT SHEET ====================

@Composable
fun ClipEditToolSheet(viewModel: EditorViewModel) {
    val selectedClip = viewModel.getSelectedVideoClip()
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(12.dp)
    ) {
        // Quick Action Row: Split, Duplicate, Delete, Reverse, Freeze, Keyframe
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            ActionToolButton(
                icon = Icons.Default.ContentCut,
                label = "Split",
                tag = "btn_sheet_split",
                onClick = { viewModel.splitClipAtPlayhead() }
            )
            ActionToolButton(
                icon = Icons.Default.ContentCopy,
                label = "Duplicate",
                tag = "btn_sheet_duplicate",
                onClick = { viewModel.duplicateSelectedClip() }
            )
            ActionToolButton(
                icon = Icons.Default.Diamond,
                label = "Keyframe",
                tag = "btn_sheet_keyframe",
                onClick = { viewModel.addOrUpdateKeyframeOnSelectedClip() }
            )
            ActionToolButton(
                icon = Icons.Default.RotateRight,
                label = "Rotate",
                tag = "btn_sheet_rotate",
                onClick = { viewModel.rotateClip() }
            )
            ActionToolButton(
                icon = Icons.Default.AcUnit,
                label = "Freeze",
                tag = "btn_sheet_freeze",
                onClick = { viewModel.freezeFrame() }
            )
            ActionToolButton(
                icon = Icons.Default.DeleteOutline,
                label = "Delete",
                tag = "btn_sheet_delete",
                isDestructive = true,
                onClick = { viewModel.deleteSelectedClip() }
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Speed Adjustment Controls
        Text("Speed: ${selectedClip?.speed ?: 1.0f}x", color = TyoxTextSecondary, fontSize = 12.sp)
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            listOf(0.5f, 1.0f, 1.5f, 2.0f, 3.0f, 5.0f).forEach { sp ->
                FilterChip(
                    selected = selectedClip?.speed == sp,
                    onClick = { viewModel.setClipSpeed(sp) },
                    label = { Text("${sp}x", fontSize = 11.sp) },
                    modifier = Modifier.padding(end = 6.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Speed Curves (Flash in, Hero, Jump cut)
        Text("Speed Curves / Ramps", color = TyoxTextSecondary, fontSize = 12.sp)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            PresetLibrary.speedCurves.forEach { curve ->
                AssistChip(
                    onClick = { viewModel.setSpeedCurve(curve) },
                    label = { Text(curve.name, fontSize = 11.sp) },
                    leadingIcon = { Icon(Icons.Default.Speed, null, modifier = Modifier.size(14.dp)) }
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Flip & Transform
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                onClick = { viewModel.flipHorizontal() },
                modifier = Modifier.weight(1f)
            ) {
                Text("Flip H", fontSize = 12.sp)
            }
            OutlinedButton(
                onClick = { viewModel.flipVertical() },
                modifier = Modifier.weight(1f)
            ) {
                Text("Flip V", fontSize = 12.sp)
            }
            OutlinedButton(
                onClick = { viewModel.toggleReverse() },
                modifier = Modifier.weight(1f)
            ) {
                Text(if (selectedClip?.isReverse == true) "Normal" else "Reverse", fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun ActionToolButton(
    icon: ImageVector,
    label: String,
    tag: String,
    isDestructive: Boolean = false,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(6.dp)
            .testTag(tag),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(if (isDestructive) TyoxCoral.copy(alpha = 0.2f) else TyoxSurfaceVariant)
                .border(1.dp, if (isDestructive) TyoxCoral else TyoxBorder, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (isDestructive) TyoxCoral else TyoxCyan,
                modifier = Modifier.size(18.dp)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            color = if (isDestructive) TyoxCoral else TyoxTextPrimary,
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

// ==================== 2. AUDIO STUDIO SHEET ====================

@Composable
fun AudioStudioToolSheet(viewModel: EditorViewModel) {
    var audioSubTab by remember { mutableStateOf(0) } // 0: Music, 1: SFX, 2: Voiceover, 3: Extract
    var isRecording by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
    ) {
        // Sub-tabs: Music, SFX, Voiceover, Extract
        TabRow(
            selectedTabIndex = audioSubTab,
            containerColor = TyoxSurfaceVariant,
            contentColor = TyoxCyan,
            modifier = Modifier.clip(RoundedCornerShape(8.dp))
        ) {
            Tab(selected = audioSubTab == 0, onClick = { audioSubTab = 0 }) {
                Text("Music Tracks", modifier = Modifier.padding(vertical = 8.dp), fontSize = 12.sp)
            }
            Tab(selected = audioSubTab == 1, onClick = { audioSubTab = 1 }) {
                Text("Sound FX", modifier = Modifier.padding(vertical = 8.dp), fontSize = 12.sp)
            }
            Tab(selected = audioSubTab == 2, onClick = { audioSubTab = 2 }) {
                Text("Voiceover", modifier = Modifier.padding(vertical = 8.dp), fontSize = 12.sp)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        when (audioSubTab) {
            0 -> {
                // Music Library List
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(PresetLibrary.musicTracks) { track ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = TyoxSurfaceVariant),
                            modifier = Modifier
                                .width(170.dp)
                                .clickable { viewModel.addAudioTrack(track) }
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(track.title, color = TyoxTextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                                Text("${track.category} • ${track.bpm} BPM", color = TyoxTextSecondary, fontSize = 10.sp)
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("${track.durationMs / 1000}s", color = TyoxCyan, fontSize = 10.sp)
                                    Icon(Icons.Default.AddCircle, contentDescription = "Add", tint = TyoxCyan, modifier = Modifier.size(20.dp))
                                }
                            }
                        }
                    }
                }
            }
            1 -> {
                // SFX library
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(PresetLibrary.soundEffects) { sfx ->
                        OutlinedButton(
                            onClick = { viewModel.addSoundEffect(sfx) },
                            modifier = Modifier.padding(vertical = 4.dp)
                        ) {
                            Icon(Icons.Default.VolumeUp, null, modifier = Modifier.size(16.dp), tint = TyoxAmber)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(sfx.name, fontSize = 11.sp)
                        }
                    }
                }
            }
            2 -> {
                // Live voiceover recording simulation
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = if (isRecording) "Recording Voiceover... 🎙️" else "Tap red button to record voiceover",
                        color = if (isRecording) TyoxCoral else TyoxTextSecondary,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    IconButton(
                        onClick = {
                            if (!isRecording) {
                                isRecording = true
                            } else {
                                isRecording = false
                                viewModel.addRecordedVoiceover(durationMs = 4000L)
                            }
                        },
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(if (isRecording) TyoxCoral else TyoxSurfaceVariant)
                            .border(2.dp, TyoxCoral, CircleShape)
                    ) {
                        Icon(
                            imageVector = if (isRecording) Icons.Default.Stop else Icons.Default.Mic,
                            contentDescription = "Record",
                            tint = Color.White,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                }
            }
        }
    }
}

// ==================== 3. TEXT STUDIO SHEET ====================

@Composable
fun TextStudioToolSheet(viewModel: EditorViewModel) {
    var inputText by remember { mutableStateOf("Trending Clip") }
    var selectedFont by remember { mutableStateOf("Cinematic Sans") }
    var selectedColor by remember { mutableStateOf("#FFFFFF") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = inputText,
                onValueChange = { inputText = it },
                label = { Text("Text Layer Content") },
                modifier = Modifier.weight(1f),
                singleLine = true
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = { viewModel.addTextItem(text = inputText, font = selectedFont, colorHex = selectedColor) },
                colors = ButtonDefaults.buttonColors(containerColor = TyoxCyan, contentColor = Color.Black)
            ) {
                Text("Add Layer")
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Font Style Chips
        Text("Font Typography", color = TyoxTextSecondary, fontSize = 11.sp)
        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            items(PresetLibrary.fontStyles) { fontName ->
                FilterChip(
                    selected = selectedFont == fontName,
                    onClick = { selectedFont = fontName },
                    label = { Text(fontName, fontSize = 11.sp) }
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Color Presets
        Text("Color & Glow", color = TyoxTextSecondary, fontSize = 11.sp)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("#FFFFFF", "#00F0FF", "#FFB300", "#FF2A6D", "#05FFA1", "#7928CA").forEach { hex ->
                Box(
                    modifier = Modifier
                        .size(26.dp)
                        .clip(CircleShape)
                        .background(parseHexColor(hex, Color.White))
                        .border(
                            width = if (selectedColor == hex) 2.dp else 1.dp,
                            color = if (selectedColor == hex) Color.White else TyoxBorder,
                            shape = CircleShape
                        )
                        .clickable { selectedColor = hex }
                )
            }
        }
    }
}

// ==================== 4. CAPTIONS STUDIO SHEET ====================

@Composable
fun CaptionsStudioToolSheet(viewModel: EditorViewModel) {
    var selectedLanguage by remember { mutableStateOf("English") }
    var selectedTemplate by remember { mutableStateOf(CaptionTemplateStyle.VIRAL_BEAST) }
    var translateLang by remember { mutableStateOf("Spanish") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(12.dp)
    ) {
        // Auto caption generator trigger
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = { viewModel.generateAutoCaptions(language = selectedLanguage, template = selectedTemplate) },
                colors = ButtonDefaults.buttonColors(containerColor = TyoxCyan, contentColor = Color.Black),
                modifier = Modifier.weight(1.2f).testTag("btn_gen_captions")
            ) {
                Icon(Icons.Default.AutoAwesome, null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Generate Auto Captions", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.width(8.dp))

            OutlinedButton(
                onClick = { viewModel.translateCaptions(translateLang) },
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.Translate, null, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Translate: $translateLang", fontSize = 11.sp)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Caption Template Styles Carousel
        Text("Caption Animated Styles", color = TyoxTextSecondary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.height(4.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(CaptionTemplateStyle.values()) { template ->
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (selectedTemplate == template) TyoxCyanContainer else TyoxSurfaceVariant
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        width = if (selectedTemplate == template) 1.5.dp else 1.dp,
                        color = if (selectedTemplate == template) TyoxCyan else TyoxBorder
                    ),
                    modifier = Modifier
                        .width(140.dp)
                        .clickable {
                            selectedTemplate = template
                            viewModel.updateCaptionTemplate(template)
                        }
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text(template.displayName, color = TyoxTextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Text(template.description, color = TyoxTextSecondary, fontSize = 9.sp, maxLines = 2)
                    }
                }
            }
        }
    }
}

// ==================== 5. TTS STUDIO SHEET ====================

@Composable
fun TtsStudioToolSheet(viewModel: EditorViewModel) {
    var ttsText by remember { mutableStateOf("Level up your videos with AI voiceovers in Tyox Studio.") }
    var selectedVoice by remember { mutableStateOf(viewModel.ttsManager.availableVoices.first()) }
    var pitch by remember { mutableStateOf(1.0f) }
    var speed by remember { mutableStateOf(1.0f) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(12.dp)
    ) {
        OutlinedTextField(
            value = ttsText,
            onValueChange = { ttsText = it },
            label = { Text("Text to Read Aloud") },
            modifier = Modifier.fillMaxWidth(),
            maxLines = 2
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Voice Selector Carousel
        Text("Voice Artists", color = TyoxTextSecondary, fontSize = 11.sp)
        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            items(viewModel.ttsManager.availableVoices) { voice ->
                FilterChip(
                    selected = selectedVoice.id == voice.id,
                    onClick = { selectedVoice = voice },
                    label = { Text("${voice.name} (${voice.gender})", fontSize = 10.sp) }
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Preview Speech & Insert Button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                onClick = { viewModel.ttsManager.speak(ttsText, selectedVoice, pitch, speed) },
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.VolumeUp, null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Listen", fontSize = 12.sp)
            }

            Button(
                onClick = { viewModel.generateTtsVoiceClip(ttsText, selectedVoice, pitch, speed) },
                colors = ButtonDefaults.buttonColors(containerColor = TyoxCyan, contentColor = Color.Black),
                modifier = Modifier.weight(1.3f)
            ) {
                Icon(Icons.Default.Add, null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Add Voice Track", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

// ==================== 6. FILTERS SHEET ====================

@Composable
fun FiltersToolSheet(viewModel: EditorViewModel) {
    val selectedClip = viewModel.getSelectedVideoClip()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
    ) {
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(PresetLibrary.filters) { filter ->
                val isSelected = selectedClip?.filterName == filter.name
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) TyoxCyanContainer else TyoxSurfaceVariant
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        width = if (isSelected) 1.5.dp else 1.dp,
                        color = if (isSelected) TyoxCyan else TyoxBorder
                    ),
                    modifier = Modifier
                        .width(100.dp)
                        .clickable { viewModel.applyFilter(filter) }
                ) {
                    Column(
                        modifier = Modifier.padding(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(TyoxObsidianDark),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("🎬", fontSize = 20.sp)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(filter.name, color = TyoxTextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                        Text(filter.category.label, color = TyoxTextSecondary, fontSize = 9.sp)
                    }
                }
            }
        }
    }
}

// ==================== 7. ADJUSTMENTS SHEET ====================

@Composable
fun AdjustmentsToolSheet(viewModel: EditorViewModel) {
    val selectedClip = viewModel.getSelectedVideoClip()
    val adj = selectedClip?.adjustments ?: VideoAdjustments()
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Adjust Video Grade", color = TyoxTextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            TextButton(onClick = { viewModel.resetAdjustments() }) {
                Text("Reset All", color = TyoxCoral, fontSize = 12.sp)
            }
        }

        AdjustmentSliderItem("Brightness", adj.brightness, -1f, 1f) { v ->
            viewModel.updateAdjustments(adj.copy(brightness = v))
        }
        AdjustmentSliderItem("Contrast", adj.contrast, -1f, 1f) { v ->
            viewModel.updateAdjustments(adj.copy(contrast = v))
        }
        AdjustmentSliderItem("Saturation", adj.saturation, -1f, 1f) { v ->
            viewModel.updateAdjustments(adj.copy(saturation = v))
        }
        AdjustmentSliderItem("Temperature", adj.temperature, -1f, 1f) { v ->
            viewModel.updateAdjustments(adj.copy(temperature = v))
        }
        AdjustmentSliderItem("Vignette", adj.vignette, 0f, 1f) { v ->
            viewModel.updateAdjustments(adj.copy(vignette = v))
        }
    }
}

@Composable
fun AdjustmentSliderItem(
    title: String,
    value: Float,
    min: Float,
    max: Float,
    onValueChange: (Float) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, color = TyoxTextSecondary, fontSize = 11.sp, modifier = Modifier.width(90.dp))
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = min..max,
            modifier = Modifier.weight(1f),
            colors = SliderDefaults.colors(thumbColor = TyoxCyan, activeTrackColor = TyoxCyan)
        )
        Text(
            text = String.format("%.2f", value),
            color = TyoxTextPrimary,
            fontSize = 11.sp,
            modifier = Modifier.width(40.dp),
            textAlign = TextAlign.End
        )
    }
}

// ==================== 8. EFFECTS & TRANSITIONS SHEET ====================

@Composable
fun EffectsAndTransitionsToolSheet(viewModel: EditorViewModel) {
    var tabIndex by remember { mutableStateOf(0) } // 0: FX, 1: Transitions

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
    ) {
        TabRow(
            selectedTabIndex = tabIndex,
            containerColor = TyoxSurfaceVariant,
            contentColor = TyoxCyan,
            modifier = Modifier.clip(RoundedCornerShape(8.dp))
        ) {
            Tab(selected = tabIndex == 0, onClick = { tabIndex = 0 }) {
                Text("Video Effects", modifier = Modifier.padding(vertical = 8.dp), fontSize = 12.sp)
            }
            Tab(selected = tabIndex == 1, onClick = { tabIndex = 1 }) {
                Text("Transitions", modifier = Modifier.padding(vertical = 8.dp), fontSize = 12.sp)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        if (tabIndex == 0) {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(EffectType.values()) { eff ->
                    OutlinedButton(
                        onClick = { viewModel.addEffect(eff) }
                    ) {
                        Text("✨ ${eff.label}", fontSize = 11.sp)
                    }
                }
            }
        } else {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(TransitionType.values()) { tr ->
                    OutlinedButton(
                        onClick = { viewModel.setTransition(tr) }
                    ) {
                        Text(tr.label, fontSize = 11.sp)
                    }
                }
            }
        }
    }
}

// ==================== 9. STICKERS SHEET ====================

@Composable
fun StickersToolSheet(viewModel: EditorViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
    ) {
        Text("Add Sticker or Reaction", color = TyoxTextSecondary, fontSize = 12.sp)
        Spacer(modifier = Modifier.height(8.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            items(PresetLibrary.stickers) { (emoji, _) ->
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(TyoxSurfaceVariant)
                        .clickable { viewModel.addSticker(emoji) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(emoji, fontSize = 24.sp)
                }
            }
        }
    }
}

// ==================== 10. AI MAGIC TOOLS SHEET ====================

@Composable
fun AiMagicToolsSheet(viewModel: EditorViewModel) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = TyoxSurfaceVariant),
                modifier = Modifier
                    .weight(1f)
                    .clickable { viewModel.applySmartAutoTrimming() }
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.VolumeMute, null, tint = TyoxCyan, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Silence Trim", color = TyoxTextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    Text("Auto-cut silent pauses", color = TyoxTextSecondary, fontSize = 10.sp)
                }
            }

            Card(
                colors = CardDefaults.cardColors(containerColor = TyoxSurfaceVariant),
                modifier = Modifier
                    .weight(1f)
                    .clickable { viewModel.applyAutoReframe() }
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CropPortrait, null, tint = TyoxAmber, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Auto Reframe", color = TyoxTextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    Text("Smart 9:16 portrait crop", color = TyoxTextSecondary, fontSize = 10.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = TyoxSurfaceVariant),
                modifier = Modifier
                    .weight(1f)
                    .clickable { viewModel.applySceneDetection() }
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.MovieFilter, null, tint = TyoxViolet, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Scene Cuts", color = TyoxTextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    Text("Detect shot transitions", color = TyoxTextSecondary, fontSize = 10.sp)
                }
            }

            Card(
                colors = CardDefaults.cardColors(containerColor = TyoxSurfaceVariant),
                modifier = Modifier
                    .weight(1f)
                    .clickable { viewModel.generateAutoCaptions() }
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.ClosedCaption, null, tint = TyoxGreen, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Auto Captions", color = TyoxTextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    Text("Karaoke sync subtitles", color = TyoxTextSecondary, fontSize = 10.sp)
                }
            }
        }
    }
}
