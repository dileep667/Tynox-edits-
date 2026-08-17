package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.*
import com.example.ui.theme.*
import kotlin.math.sin

@Composable
fun VideoPlayerPreview(
    aspectRatio: AspectRatio,
    timeline: TimelineData,
    playheadMs: Long,
    isPlaying: Boolean,
    isFullscreen: Boolean,
    onTogglePlay: () -> Unit,
    onSeek: (Long) -> Unit,
    onStepFrame: (Boolean) -> Unit,
    onToggleFullscreen: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showControlsOverlay by remember { mutableStateOf(true) }

    // Find active video clip at playhead
    var accumulatedTime = 0L
    var activeClip: VideoClip? = null
    var activeClipProgress = 0f
    for (clip in timeline.videoClips) {
        val clipDur = clip.durationMs
        if (playheadMs >= accumulatedTime && playheadMs < accumulatedTime + clipDur) {
            activeClip = clip
            activeClipProgress = (playheadMs - accumulatedTime).toFloat() / clipDur.toFloat()
            break
        }
        accumulatedTime += clipDur
    }
    if (activeClip == null && timeline.videoClips.isNotEmpty()) {
        activeClip = timeline.videoClips.firstOrNull()
    }

    // Active captions at playhead
    val activeCaptions = timeline.captions.filter {
        playheadMs >= it.startTimeMs && playheadMs <= it.startTimeMs + it.durationMs
    }

    // Active texts at playhead
    val activeTexts = timeline.textItems.filter {
        playheadMs >= it.startTimeMs && playheadMs <= it.startTimeMs + it.durationMs
    }

    // Active effects at playhead
    val activeEffects = timeline.effects.filter {
        playheadMs >= it.startTimeMs && playheadMs <= it.startTimeMs + it.durationMs
    }

    // Active stickers at playhead
    val activeStickers = timeline.stickers.filter {
        playheadMs >= it.startTimeMs && playheadMs <= it.startTimeMs + it.durationMs
    }

    // Calculate effect offsets
    val glitchEffect = activeEffects.find { it.type == EffectType.GLITCH }
    val shakeEffect = activeEffects.find { it.type == EffectType.SHAKE }
    val rgbSplitEffect = activeEffects.find { it.type == EffectType.RGB_SPLIT }
    val flashEffect = activeEffects.find { it.type == EffectType.FLASH }

    val shakeOffsetX = if (shakeEffect != null && isPlaying) {
        (sin(playheadMs / 40.0) * 12f * shakeEffect.intensity).toFloat()
    } else 0f
    val shakeOffsetY = if (shakeEffect != null && isPlaying) {
        (sin(playheadMs / 30.0) * 8f * shakeEffect.intensity).toFloat()
    } else 0f

    val rgbOffset = if (rgbSplitEffect != null) (8f * rgbSplitEffect.intensity) else 0f

    // Format timecodes
    val curSec = (playheadMs / 1000) % 60
    val curMin = (playheadMs / 60000) % 60
    val curFrame = ((playheadMs % 1000) / 33)
    val timecodeStr = String.format("%02d:%02d:%02d", curMin, curSec, curFrame)

    val totalMs = timeline.totalDurationMs
    val totSec = (totalMs / 1000) % 60
    val totMin = (totalMs / 60000) % 60
    val totFrame = ((totalMs % 1000) / 33)
    val totalTimecodeStr = String.format("%02d:%02d:%02d", totMin, totSec, totFrame)

    Box(
        modifier = modifier
            .background(TyoxObsidianDark)
            .pointerInput(Unit) {
                detectTapGestures(onTap = { showControlsOverlay = !showControlsOverlay })
            },
        contentAlignment = Alignment.Center
    ) {
        // Aspect ratio framed preview box
        Box(
            modifier = Modifier
                .padding(if (isFullscreen) 0.dp else 12.dp)
                .fillMaxHeight()
                .aspectRatio(aspectRatio.ratio, matchHeightConstraintsFirst = true)
                .clip(RoundedCornerShape(if (isFullscreen) 0.dp else 12.dp))
                .background(Color.Black)
                .border(
                    width = if (isFullscreen) 0.dp else 1.dp,
                    color = TyoxBorder,
                    shape = RoundedCornerShape(if (isFullscreen) 0.dp else 12.dp)
                )
                .graphicsLayer {
                    translationX = shakeOffsetX
                    translationY = shakeOffsetY
                    rotationZ = activeClip?.rotation ?: 0f
                    scaleX = (if (activeClip?.flipHorizontal == true) -1f else 1f) * (activeClip?.scale ?: 1f)
                    scaleY = (if (activeClip?.flipVertical == true) -1f else 1f) * (activeClip?.scale ?: 1f)
                },
            contentAlignment = Alignment.Center
        ) {
            // Render video layer
            val resId = remember(activeClip?.drawableResName) {
                when (activeClip?.drawableResName) {
                    "sample_cyberpunk" -> com.example.R.drawable.sample_cyberpunk
                    "sample_reels_travel" -> com.example.R.drawable.sample_reels_travel
                    "sample_podcast_host" -> com.example.R.drawable.sample_podcast_host
                    else -> com.example.R.drawable.sample_cyberpunk
                }
            }

            // Video / Image background layer
            Box(modifier = Modifier.fillMaxSize()) {
                androidx.compose.foundation.Image(
                    painter = painterResource(id = resId),
                    contentDescription = "Video Frame",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = androidx.compose.ui.layout.ContentScale.Crop
                )

                // Simulated Color Adjustment & Filter Overlay
                val adjustments = activeClip?.adjustments ?: VideoAdjustments()
                val filterIntensity = activeClip?.filterIntensity ?: 1.0f

                Canvas(modifier = Modifier.fillMaxSize()) {
                    // Brightness & exposure
                    if (adjustments.brightness != 0f || adjustments.exposure != 0f) {
                        val alpha = kotlin.math.abs(adjustments.brightness + adjustments.exposure).coerceIn(0f, 0.6f)
                        val color = if (adjustments.brightness + adjustments.exposure > 0) Color.White.copy(alpha = alpha) else Color.Black.copy(alpha = alpha)
                        drawRect(color = color)
                    }

                    // Temperature / Tint
                    if (adjustments.temperature > 0.1f) {
                        drawRect(color = Color(0xFFFF9800).copy(alpha = adjustments.temperature * 0.25f * filterIntensity))
                    } else if (adjustments.temperature < -0.1f) {
                        drawRect(color = Color(0xFF00B0FF).copy(alpha = -adjustments.temperature * 0.25f * filterIntensity))
                    }
                    if (adjustments.tint > 0.1f) {
                        drawRect(color = Color(0xFFE91E63).copy(alpha = adjustments.tint * 0.2f * filterIntensity))
                    }

                    // Contrast & Vignette
                    if (adjustments.vignette > 0.05f) {
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(Color.Transparent, Color.Black.copy(alpha = adjustments.vignette * 0.8f)),
                                center = center,
                                radius = size.minDimension * 0.8f
                            )
                        )
                    }

                    // Glitch Scanlines effect
                    if (glitchEffect != null && isPlaying) {
                        val numLines = 20
                        val lineSpacing = size.height / numLines
                        for (i in 0 until numLines) {
                            if (i % 2 == 0) {
                                drawLine(
                                    color = Color(0xFF00F0FF).copy(alpha = 0.2f * glitchEffect.intensity),
                                    start = Offset(0f, i * lineSpacing + (playheadMs % 50)),
                                    end = Offset(size.width, i * lineSpacing + (playheadMs % 50)),
                                    strokeWidth = 2f
                                )
                            }
                        }
                    }

                    // Flash strobe effect
                    if (flashEffect != null && (playheadMs % 600 < 100)) {
                        drawRect(color = Color.White.copy(alpha = 0.7f * flashEffect.intensity))
                    }

                    // Chroma key edge indicator if active
                    if (activeClip?.chromaKey?.enabled == true) {
                        drawRect(
                            color = Color(0xFF00FF00).copy(alpha = 0.15f),
                            style = Stroke(width = 4f)
                        )
                    }
                }
            }

            // Render Overlays
            timeline.overlayClips.filter { playheadMs in it.startTimeMs..(it.startTimeMs + it.durationMs) }.forEach { ov ->
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .offset(x = ov.positionX.dp, y = ov.positionY.dp)
                        .graphicsLayer {
                            scaleX = ov.scale
                            scaleY = ov.scale
                            rotationZ = ov.rotation
                            alpha = ov.opacity
                        }
                        .border(1.dp, TyoxCyan, RoundedCornerShape(8.dp))
                        .clip(RoundedCornerShape(8.dp))
                ) {
                    androidx.compose.foundation.Image(
                        painter = painterResource(id = com.example.R.drawable.sample_cyberpunk),
                        contentDescription = "PIP Overlay",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = androidx.compose.ui.layout.ContentScale.Crop
                    )
                }
            }

            // Render Active Text Layers
            activeTexts.forEach { textItem ->
                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .offset(x = textItem.positionX.dp, y = textItem.positionY.dp)
                        .graphicsLayer {
                            scaleX = textItem.scale
                            scaleY = textItem.scale
                            rotationZ = textItem.rotation
                            alpha = textItem.opacity
                        }
                        .then(
                            if (textItem.backgroundColorHex != null && textItem.backgroundColorHex.isNotBlank()) {
                                Modifier
                                    .background(
                                        color = parseHexColor(textItem.backgroundColorHex, Color.Black.copy(alpha = 0.6f)),
                                        shape = RoundedCornerShape(textItem.bgCornerRadiusDp.dp)
                                    )
                                    .padding(horizontal = textItem.bgPaddingDp.dp * 1.5f, vertical = textItem.bgPaddingDp.dp)
                            } else Modifier
                        )
                ) {
                    Text(
                        text = textItem.text,
                        color = parseHexColor(textItem.textColorHex, Color.White),
                        fontSize = textItem.fontSizeSp.sp,
                        fontWeight = if (textItem.isBold) FontWeight.Bold else FontWeight.Normal,
                        textAlign = when (textItem.alignment) {
                            "LEFT" -> TextAlign.Left
                            "RIGHT" -> TextAlign.Right
                            else -> TextAlign.Center
                        },
                        letterSpacing = textItem.letterSpacingSp.sp,
                        style = TextStyle(
                            shadow = if (textItem.glowColorHex != null) {
                                Shadow(
                                    color = parseHexColor(textItem.glowColorHex, TyoxCyan),
                                    offset = Offset(0f, 0f),
                                    blurRadius = 16f
                                )
                            } else if (textItem.shadowColorHex != null) {
                                Shadow(
                                    color = parseHexColor(textItem.shadowColorHex, Color.Black),
                                    offset = Offset(2f, 2f),
                                    blurRadius = 4f
                                )
                            } else null
                        )
                    )
                }
            }

            // Render Active Captions with Karaoke Animation
            activeCaptions.forEach { caption ->
                val captionProgressMs = playheadMs - caption.startTimeMs

                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = (100 * (1f - caption.positionY)).dp)
                        .padding(horizontal = 16.dp)
                ) {
                    when (caption.template) {
                        CaptionTemplateStyle.VIRAL_BEAST -> {
                            // Bold yellow bounce highlight on current word
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color.Black.copy(alpha = 0.5f))
                                    .padding(horizontal = 12.dp, vertical = 6.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                caption.words.forEach { word ->
                                    val isCurrentWord = captionProgressMs in word.startOffsetMs..word.endOffsetMs
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier.padding(horizontal = 3.dp)
                                    ) {
                                        if (word.emoji != null && isCurrentWord) {
                                            Text(word.emoji, fontSize = 20.sp)
                                        }
                                        Text(
                                            text = word.word.uppercase(),
                                            color = if (isCurrentWord) TyoxAmber else Color.White,
                                            fontSize = if (isCurrentWord) (caption.fontSizeSp + 3f).sp else caption.fontSizeSp.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            style = TextStyle(
                                                shadow = Shadow(Color.Black, Offset(2f, 2f), 4f)
                                            )
                                        )
                                    }
                                }
                            }
                        }

                        CaptionTemplateStyle.MODERN_PODCAST -> {
                            // Pill badge with cyan karaoke glow
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(24.dp))
                                    .background(TyoxSurfaceVariant.copy(alpha = 0.9f))
                                    .border(1.dp, TyoxBorder, RoundedCornerShape(24.dp))
                                    .padding(horizontal = 16.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                caption.words.forEach { word ->
                                    val isCurrent = captionProgressMs in word.startOffsetMs..word.endOffsetMs
                                    Text(
                                        text = "${word.word} ",
                                        color = if (isCurrent) TyoxCyan else TyoxTextSecondary,
                                        fontSize = caption.fontSizeSp.sp,
                                        fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Medium,
                                        style = TextStyle(
                                            shadow = if (isCurrent) Shadow(TyoxCyan, Offset.Zero, 12f) else null
                                        )
                                    )
                                }
                            }
                        }

                        CaptionTemplateStyle.GAMING_NEON -> {
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color(0xFF130026).copy(alpha = 0.85f))
                                    .border(1.5.dp, TyoxViolet, RoundedCornerShape(10.dp))
                                    .padding(horizontal = 14.dp, vertical = 6.dp)
                            ) {
                                caption.words.forEach { word ->
                                    val isCurrent = captionProgressMs in word.startOffsetMs..word.endOffsetMs
                                    Text(
                                        text = "${word.word} ",
                                        color = if (isCurrent) Color(0xFFFF007F) else Color(0xFF00F0FF),
                                        fontSize = if (isCurrent) (caption.fontSizeSp + 2f).sp else caption.fontSizeSp.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        style = TextStyle(
                                            shadow = Shadow(if (isCurrent) Color(0xFFFF007F) else Color(0xFF00F0FF), Offset.Zero, 10f)
                                        )
                                    )
                                }
                            }
                        }

                        else -> {
                            // Default clean subtitle
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color.Black.copy(alpha = 0.7f))
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = caption.fullText,
                                    color = Color.White,
                                    fontSize = caption.fontSizeSp.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }

            // Render Active Stickers
            activeStickers.forEach { sticker ->
                Box(
                    modifier = Modifier
                        .offset(x = sticker.positionX.dp, y = sticker.positionY.dp)
                        .graphicsLayer {
                            scaleX = sticker.scale
                            scaleY = sticker.scale
                            rotationZ = sticker.rotation
                            alpha = sticker.opacity
                        }
                ) {
                    Text(text = sticker.content, fontSize = 42.sp)
                }
            }
        }

        // Overlay Controls (HUD)
        if (showControlsOverlay) {
            // Top HUD bar: Timecode & Resolution
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .background(
                        Brush.verticalGradient(
                            listOf(Color.Black.copy(alpha = 0.75f), Color.Transparent)
                        )
                    )
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(TyoxCyan.copy(alpha = 0.15f))
                            .border(0.8.dp, TyoxCyan, RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = aspectRatio.label.split(" ").first(),
                            color = TyoxCyan,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "$timecodeStr / $totalTimecodeStr",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Medium
                    )
                }

                IconButton(
                    onClick = onToggleFullscreen,
                    modifier = Modifier.size(36.dp).testTag("btn_fullscreen_preview")
                ) {
                    Icon(
                        imageVector = if (isFullscreen) Icons.Default.FullscreenExit else Icons.Default.Fullscreen,
                        contentDescription = "Fullscreen",
                        tint = Color.White
                    )
                }
            }

            // Center Floating Play / Pause Action
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.5f))
                    .border(1.5.dp, TyoxCyan, CircleShape)
                    .clickable { onTogglePlay() }
                    .testTag("btn_play_pause"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = if (isPlaying) "Pause" else "Play",
                    tint = TyoxCyan,
                    modifier = Modifier.size(32.dp)
                )
            }

            // Bottom Frame Stepping Controls
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            listOf(Color.Transparent, Color.Black.copy(alpha = 0.75f))
                        )
                    )
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { onStepFrame(false) },
                        modifier = Modifier.size(36.dp).testTag("btn_prev_frame")
                    ) {
                        Icon(
                            imageVector = Icons.Default.SkipPrevious,
                            contentDescription = "Previous Frame",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    IconButton(
                        onClick = { onStepFrame(true) },
                        modifier = Modifier.size(36.dp).testTag("btn_next_frame")
                    ) {
                        Icon(
                            imageVector = Icons.Default.SkipNext,
                            contentDescription = "Next Frame",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                // Playback speed indicator
                if (activeClip?.speed != 1.0f && activeClip != null) {
                    Text(
                        text = "${activeClip.speed}x Speed",
                        color = TyoxAmber,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

fun parseHexColor(hex: String?, fallback: Color): Color {
    if (hex.isNullOrBlank()) return fallback
    return try {
        val clean = hex.removePrefix("#")
        val colorInt = clean.toLong(16)
        if (clean.length == 6) {
            Color(colorInt or 0x00000000FF000000)
        } else if (clean.length == 8) {
            Color(colorInt)
        } else fallback
    } catch (e: Exception) {
        fallback
    }
}
