package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.ui.theme.*
import com.example.viewmodel.SelectedTrackType

@Composable
fun MultiTrackTimelineView(
    timeline: TimelineData,
    playheadMs: Long,
    zoomFactor: Float,
    selectedTrackType: SelectedTrackType,
    selectedItemId: String?,
    onSeek: (Long) -> Unit,
    onSelectItem: (SelectedTrackType, String?) -> Unit,
    onZoomChange: (Float) -> Unit,
    onSplitAtPlayhead: () -> Unit,
    modifier: Modifier = Modifier
) {
    // 1 second (1000ms) = 60dp * zoomFactor
    val pxPerSecond = 60.dp * zoomFactor
    val msPerDp = 1000f / pxPerSecond.value
    val totalTimelineWidth = ((timeline.totalDurationMs / 1000f) * pxPerSecond.value).coerceAtLeast(400f).dp

    val scrollState = rememberScrollState()

    // Auto-scroll timeline to follow playhead smoothly during playback
    LaunchedEffect(playheadMs) {
        val playheadDp = (playheadMs / 1000f) * pxPerSecond.value
        val viewWidthPx = 300 // viewport threshold
        val targetScroll = (playheadDp - 150).coerceAtLeast(0f).toInt()
        if (kotlin.math.abs(scrollState.value - targetScroll) > 200) {
            scrollState.animateScrollTo(targetScroll)
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(TyoxBackground)
    ) {
        // Timeline Header Controls: Zoom In/Out, Current Track Info, Quick Split
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(TyoxSurface)
                .padding(horizontal = 12.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Quick Split Button
                Surface(
                    onClick = onSplitAtPlayhead,
                    shape = RoundedCornerShape(6.dp),
                    color = TyoxSurfaceVariant,
                    border = androidx.compose.foundation.BorderStroke(1.dp, TyoxBorder),
                    modifier = Modifier.testTag("btn_quick_split")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCut,
                            contentDescription = "Split",
                            tint = TyoxCyan,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Split", color = TyoxTextPrimary, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Track Count Badges
                Text(
                    text = "${timeline.videoClips.size} Clips • ${timeline.audioClips.size} Audio • ${timeline.captions.size} Caps",
                    color = TyoxTextSecondary,
                    fontSize = 11.sp
                )
            }

            // Timeline Zoom Controls
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = { onZoomChange(zoomFactor - 0.25f) },
                    modifier = Modifier.size(28.dp).testTag("btn_zoom_out")
                ) {
                    Icon(Icons.Default.Remove, "Zoom Out", tint = TyoxTextSecondary, modifier = Modifier.size(16.dp))
                }
                Text(
                    text = "${(zoomFactor * 100).toInt()}%",
                    color = TyoxTextSecondary,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
                IconButton(
                    onClick = { onZoomChange(zoomFactor + 0.25f) },
                    modifier = Modifier.size(28.dp).testTag("btn_zoom_in")
                ) {
                    Icon(Icons.Default.Add, "Zoom In", tint = TyoxTextSecondary, modifier = Modifier.size(16.dp))
                }
            }
        }

        Divider(color = TyoxBorder, thickness = 1.dp)

        // Scrollable Multi-Track Timeline Canvas
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .horizontalScroll(scrollState)
        ) {
            Column(
                modifier = Modifier
                    .width(totalTimelineWidth + 200.dp)
                    .fillMaxHeight()
                    .padding(horizontal = 16.dp)
            ) {
                // 1. Time Ruler (Seconds, Frames, Beats)
                TimelineRuler(
                    totalDurationMs = timeline.totalDurationMs,
                    pxPerSecond = pxPerSecond,
                    onSeek = onSeek,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(26.dp)
                )

                Spacer(modifier = Modifier.height(4.dp))

                // 2. Video Track (Main Track)
                VideoClipsTrack(
                    clips = timeline.videoClips,
                    pxPerSecond = pxPerSecond,
                    selectedItemId = selectedItemId,
                    selectedTrackType = selectedTrackType,
                    onSelectClip = { id -> onSelectItem(SelectedTrackType.VIDEO, id) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                )

                Spacer(modifier = Modifier.height(4.dp))

                // 3. Caption Track (If captions exist)
                if (timeline.captions.isNotEmpty()) {
                    CaptionsTrack(
                        captions = timeline.captions,
                        pxPerSecond = pxPerSecond,
                        selectedItemId = selectedItemId,
                        onSelectCaption = { id -> onSelectItem(SelectedTrackType.CAPTION, id) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(28.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }

                // 4. Text Track (If text layers exist)
                if (timeline.textItems.isNotEmpty()) {
                    TextLayersTrack(
                        textItems = timeline.textItems,
                        pxPerSecond = pxPerSecond,
                        selectedItemId = selectedItemId,
                        onSelectText = { id -> onSelectItem(SelectedTrackType.TEXT, id) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(28.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }

                // 5. Audio / Music / SFX / Voiceover Track
                if (timeline.audioClips.isNotEmpty()) {
                    AudioClipsTrack(
                        audioClips = timeline.audioClips,
                        pxPerSecond = pxPerSecond,
                        selectedItemId = selectedItemId,
                        onSelectAudio = { id -> onSelectItem(SelectedTrackType.AUDIO, id) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(38.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }

                // 6. Effects Track
                if (timeline.effects.isNotEmpty()) {
                    EffectsTrack(
                        effects = timeline.effects,
                        pxPerSecond = pxPerSecond,
                        selectedItemId = selectedItemId,
                        onSelectEffect = { id -> onSelectItem(SelectedTrackType.EFFECT, id) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(26.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }

                // 7. Stickers Track
                if (timeline.stickers.isNotEmpty()) {
                    StickersTrack(
                        stickers = timeline.stickers,
                        pxPerSecond = pxPerSecond,
                        selectedItemId = selectedItemId,
                        onSelectSticker = { id -> onSelectItem(SelectedTrackType.STICKER, id) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(26.dp)
                    )
                }
            }

            // Glowing Playhead Needle Overlay
            val playheadXOffset = ((playheadMs / 1000f) * pxPerSecond.value).dp + 16.dp

            Box(
                modifier = Modifier
                    .offset(x = playheadXOffset - 7.dp)
                    .fillMaxHeight()
                    .width(14.dp)
                    .pointerInput(Unit) {
                        detectDragGestures { change, dragAmount ->
                            change.consume()
                            val deltaMs = (dragAmount.x * msPerDp).toLong()
                            onSeek(playheadMs + deltaMs)
                        }
                    },
                contentAlignment = Alignment.TopCenter
            ) {
                // Needle Head (Triangle)
                Canvas(modifier = Modifier.size(14.dp, 12.dp)) {
                    val path = Path().apply {
                        moveTo(0f, 0f)
                        lineTo(size.width, 0f)
                        lineTo(size.width / 2f, size.height)
                        close()
                    }
                    drawPath(path, color = TyoxPlayhead)
                }

                // Vertical Playhead Line
                Box(
                    modifier = Modifier
                        .padding(top = 10.dp)
                        .fillMaxHeight()
                        .width(2.dp)
                        .background(
                            Brush.verticalGradient(
                                listOf(TyoxPlayhead, TyoxCyan, TyoxCyanDim)
                            )
                        )
                )
            }
        }
    }
}

@Composable
fun TimelineRuler(
    totalDurationMs: Long,
    pxPerSecond: androidx.compose.ui.unit.Dp,
    onSeek: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    Canvas(
        modifier = modifier
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    val clickedSec = offset.x / pxPerSecond.toPx()
                    onSeek((clickedSec * 1000L).toLong())
                }
            }
    ) {
        val totalSec = (totalDurationMs / 1000f) + 5f
        val stepSec = if (pxPerSecond.toPx() < 40f) 5 else 1

        for (sec in 0..totalSec.toInt() step stepSec) {
            val x = sec * pxPerSecond.toPx()
            val isMajor = sec % 5 == 0

            // Tick lines
            drawLine(
                color = if (isMajor) TyoxTextSecondary else TyoxBorderLight,
                start = Offset(x, if (isMajor) 0f else size.height * 0.5f),
                end = Offset(x, size.height),
                strokeWidth = if (isMajor) 1.5f else 1f
            )
        }
    }
}

@Composable
fun VideoClipsTrack(
    clips: List<VideoClip>,
    pxPerSecond: androidx.compose.ui.unit.Dp,
    selectedItemId: String?,
    selectedTrackType: SelectedTrackType,
    onSelectClip: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        clips.forEach { clip ->
            val clipWidth = ((clip.durationMs / 1000f) * pxPerSecond.value).dp.coerceAtLeast(36.dp)
            val isSelected = selectedItemId == clip.id && selectedTrackType == SelectedTrackType.VIDEO

            Box(
                modifier = Modifier
                    .width(clipWidth)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(6.dp))
                    .background(
                        if (isSelected) TyoxCyanContainer else TyoxSurfaceVariant
                    )
                    .border(
                        width = if (isSelected) 2.dp else 1.dp,
                        color = if (isSelected) TyoxCyan else TyoxBorder,
                        shape = RoundedCornerShape(6.dp)
                    )
                    .clickable { onSelectClip(clip.id) }
                    .padding(horizontal = 6.dp, vertical = 4.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = clip.title,
                            color = if (isSelected) TyoxCyan else TyoxTextPrimary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "${String.format("%.1f", clip.durationMs / 1000f)}s • ${clip.speed}x",
                            color = TyoxTextSecondary,
                            fontSize = 9.sp
                        )
                    }

                    // Clip property badges
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (clip.filterName != "Normal" && clip.filterName != "Original") {
                            Box(
                                modifier = Modifier
                                    .padding(end = 4.dp)
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(TyoxViolet)
                            )
                        }
                        if (clip.keyframes.isNotEmpty()) {
                            Icon(
                                imageVector = Icons.Default.Diamond,
                                contentDescription = "Keyframes",
                                tint = TyoxAmber,
                                modifier = Modifier.size(10.dp)
                            )
                        }
                        if (clip.transitionIn != TransitionType.NONE) {
                            Icon(
                                imageVector = Icons.Default.Transform,
                                contentDescription = "Transition",
                                tint = TyoxCyan,
                                modifier = Modifier.size(12.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CaptionsTrack(
    captions: List<CaptionItem>,
    pxPerSecond: androidx.compose.ui.unit.Dp,
    selectedItemId: String?,
    onSelectCaption: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        captions.forEach { cap ->
            val startDp = ((cap.startTimeMs / 1000f) * pxPerSecond.value).dp
            val widthDp = ((cap.durationMs / 1000f) * pxPerSecond.value).dp.coerceAtLeast(30.dp)
            val isSelected = selectedItemId == cap.id

            Box(
                modifier = Modifier
                    .offset(x = startDp)
                    .width(widthDp)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(4.dp))
                    .background(if (isSelected) TyoxAmber.copy(alpha = 0.3f) else TyoxTrackCaption.copy(alpha = 0.2f))
                    .border(
                        width = if (isSelected) 1.5.dp else 1.dp,
                        color = if (isSelected) TyoxAmber else TyoxTrackCaption,
                        shape = RoundedCornerShape(4.dp)
                    )
                    .clickable { onSelectCaption(cap.id) }
                    .padding(horizontal = 4.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    text = "📝 ${cap.fullText}",
                    color = Color.White,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun TextLayersTrack(
    textItems: List<TextItem>,
    pxPerSecond: androidx.compose.ui.unit.Dp,
    selectedItemId: String?,
    onSelectText: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        textItems.forEach { t ->
            val startDp = ((t.startTimeMs / 1000f) * pxPerSecond.value).dp
            val widthDp = ((t.durationMs / 1000f) * pxPerSecond.value).dp.coerceAtLeast(30.dp)
            val isSelected = selectedItemId == t.id

            Box(
                modifier = Modifier
                    .offset(x = startDp)
                    .width(widthDp)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(4.dp))
                    .background(if (isSelected) TyoxGreen.copy(alpha = 0.3f) else TyoxTrackText.copy(alpha = 0.2f))
                    .border(
                        width = if (isSelected) 1.5.dp else 1.dp,
                        color = if (isSelected) TyoxGreen else TyoxTrackText,
                        shape = RoundedCornerShape(4.dp)
                    )
                    .clickable { onSelectText(t.id) }
                    .padding(horizontal = 4.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    text = "T: ${t.text}",
                    color = Color.White,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun AudioClipsTrack(
    audioClips: List<AudioClip>,
    pxPerSecond: androidx.compose.ui.unit.Dp,
    selectedItemId: String?,
    onSelectAudio: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        audioClips.forEach { audio ->
            val startDp = ((audio.startTimeMs / 1000f) * pxPerSecond.value).dp
            val widthDp = ((audio.durationMs / 1000f) * pxPerSecond.value).dp.coerceAtLeast(30.dp)
            val isSelected = selectedItemId == audio.id

            val trackColor = when {
                audio.isVoiceover -> TyoxTrackVoice
                audio.isSfx -> TyoxViolet
                else -> TyoxTrackAudio
            }

            Box(
                modifier = Modifier
                    .offset(x = startDp)
                    .width(widthDp)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(6.dp))
                    .background(if (isSelected) trackColor.copy(alpha = 0.35f) else trackColor.copy(alpha = 0.2f))
                    .border(
                        width = if (isSelected) 1.5.dp else 1.dp,
                        color = if (isSelected) Color.White else trackColor,
                        shape = RoundedCornerShape(6.dp)
                    )
                    .clickable { onSelectAudio(audio.id) }
                    .padding(horizontal = 6.dp, vertical = 2.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween) {
                    Text(
                        text = "🎵 ${audio.title}",
                        color = Color.White,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    // Audio Waveform Visualizer on Track
                    Canvas(modifier = Modifier.fillMaxWidth().height(14.dp)) {
                        val points = if (audio.waveformPoints.isNotEmpty()) audio.waveformPoints else listOf(0.5f)
                        val barWidth = (size.width / points.size.toFloat()).coerceAtLeast(2f)
                        points.forEachIndexed { i, p ->
                            val barHeight = size.height * p
                            drawRect(
                                color = if (isSelected) Color.White else trackColor,
                                topLeft = Offset(i * barWidth, size.height - barHeight),
                                size = Size(barWidth * 0.7f, barHeight)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EffectsTrack(
    effects: List<EffectItem>,
    pxPerSecond: androidx.compose.ui.unit.Dp,
    selectedItemId: String?,
    onSelectEffect: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        effects.forEach { eff ->
            val startDp = ((eff.startTimeMs / 1000f) * pxPerSecond.value).dp
            val widthDp = ((eff.durationMs / 1000f) * pxPerSecond.value).dp.coerceAtLeast(30.dp)
            val isSelected = selectedItemId == eff.id

            Box(
                modifier = Modifier
                    .offset(x = startDp)
                    .width(widthDp)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(4.dp))
                    .background(if (isSelected) TyoxTrackEffect.copy(alpha = 0.4f) else TyoxTrackEffect.copy(alpha = 0.2f))
                    .border(
                        width = if (isSelected) 1.5.dp else 1.dp,
                        color = if (isSelected) Color.White else TyoxTrackEffect,
                        shape = RoundedCornerShape(4.dp)
                    )
                    .clickable { onSelectEffect(eff.id) }
                    .padding(horizontal = 4.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    text = "✨ ${eff.type.label}",
                    color = Color.White,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun StickersTrack(
    stickers: List<StickerItem>,
    pxPerSecond: androidx.compose.ui.unit.Dp,
    selectedItemId: String?,
    onSelectSticker: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        stickers.forEach { s ->
            val startDp = ((s.startTimeMs / 1000f) * pxPerSecond.value).dp
            val widthDp = ((s.durationMs / 1000f) * pxPerSecond.value).dp.coerceAtLeast(30.dp)
            val isSelected = selectedItemId == s.id

            Box(
                modifier = Modifier
                    .offset(x = startDp)
                    .width(widthDp)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(4.dp))
                    .background(if (isSelected) TyoxTrackSticker.copy(alpha = 0.4f) else TyoxTrackSticker.copy(alpha = 0.2f))
                    .border(
                        width = if (isSelected) 1.5.dp else 1.dp,
                        color = if (isSelected) Color.White else TyoxTrackSticker,
                        shape = RoundedCornerShape(4.dp)
                    )
                    .clickable { onSelectSticker(s.id) }
                    .padding(horizontal = 4.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    text = "${s.content} Sticker",
                    color = Color.White,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
