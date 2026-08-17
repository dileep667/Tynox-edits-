package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

enum class AspectRatio(val label: String, val ratio: Float, val widthRatio: Int, val heightRatio: Int) {
    RATIO_16_9("16:9 Landscape", 16f / 9f, 16, 9),
    RATIO_9_16("9:16 Reels/Shorts/TikTok", 9f / 16f, 9, 16),
    RATIO_1_1("1:1 Square", 1f, 1, 1),
    RATIO_4_5("4:5 Portrait Feed", 4f / 5f, 4, 5),
    RATIO_3_4("3:4 Classic Video", 3f / 4f, 3, 4)
}

enum class ProjectResolution(val label: String, val width: Int, val height: Int, val bitrateMbps: Float) {
    RES_720P("720p HD", 1280, 720, 8f),
    RES_1080P("1080p Full HD", 1920, 1080, 16f),
    RES_2K("2K QHD", 2560, 1440, 28f),
    RES_4K("4K Ultra HD", 3840, 2160, 50f)
}

enum class ProjectFps(val fps: Int, val label: String) {
    FPS_24(24, "24 fps (Cinematic)"),
    FPS_25(25, "25 fps (PAL)"),
    FPS_30(30, "30 fps (Standard)"),
    FPS_50(50, "50 fps (Smooth PAL)"),
    FPS_60(60, "60 fps (Ultra Smooth)")
}

enum class TransitionType(val label: String, val iconName: String) {
    NONE("None", "close"),
    FADE("Crossfade", "gradient"),
    DISSOLVE("Dissolve", "blur_on"),
    ZOOM_IN("Zoom In", "zoom_in"),
    ZOOM_OUT("Zoom Out", "zoom_out"),
    SPIN("Spin 360", "rotate_right"),
    SLIDE_LEFT("Slide Left", "arrow_back"),
    SLIDE_RIGHT("Slide Right", "arrow_forward"),
    PUSH_UP("Push Up", "arrow_upward"),
    BLUR_CROSS("Motion Blur", "blur_linear"),
    FLASH("White Flash", "flash_on"),
    SHAKE("Glitch Shake", "vibration"),
    LIGHT_LEAK("Light Leak", "wb_sunny"),
    CAMERA_SWISH("Whip Pan", "camera")
}

enum class FilterCategory(val label: String) {
    ALL("All"),
    CINEMATIC("Cinematic"),
    WARM("Warm"),
    COOL("Cool"),
    VINTAGE("Vintage"),
    FILM("Film"),
    NIGHT("Night"),
    PORTRAIT("Portrait"),
    URBAN("Urban"),
    TRAVEL("Travel"),
    DRAMATIC("Dramatic"),
    BW("Black & White")
}

data class FilterPreset(
    val id: String,
    val name: String,
    val category: FilterCategory,
    val brightness: Float = 0f,
    val contrast: Float = 1f,
    val saturation: Float = 1f,
    val temperature: Float = 0f,
    val tint: Float = 0f,
    val vignette: Float = 0f,
    val grain: Float = 0f,
    val fade: Float = 0f,
    val sharpen: Float = 0f,
    val colorFilterMatrix: List<Float>? = null
)

data class VideoAdjustments(
    val brightness: Float = 0f,      // -1.0 to 1.0
    val contrast: Float = 1.0f,      // 0.0 to 2.0
    val saturation: Float = 1.0f,    // 0.0 to 2.0
    val exposure: Float = 0f,        // -1.0 to 1.0
    val highlights: Float = 0f,      // -1.0 to 1.0
    val shadows: Float = 0f,         // -1.0 to 1.0
    val temperature: Float = 0f,     // -1.0 (Cool) to 1.0 (Warm)
    val tint: Float = 0f,            // -1.0 (Green) to 1.0 (Magenta)
    val sharpen: Float = 0f,         // 0.0 to 1.0
    val fade: Float = 0f,            // 0.0 to 1.0
    val vignette: Float = 0f,        // 0.0 to 1.0
    val grain: Float = 0f,           // 0.0 to 1.0
    val hue: Float = 0f              // -180 to 180 degrees
) {
    val isDefault: Boolean
        get() = brightness == 0f && contrast == 1.0f && saturation == 1.0f &&
                exposure == 0f && highlights == 0f && shadows == 0f &&
                temperature == 0f && tint == 0f && sharpen == 0f &&
                fade == 0f && vignette == 0f && grain == 0f && hue == 0f
}

data class SpeedCurvePoint(val x: Float, val speed: Float) // x: 0..1 timeline fraction, speed: 0.1..8.0

data class SpeedCurve(
    val name: String,
    val points: List<SpeedCurvePoint>
)

data class KeyframePoint(
    val timeMs: Long,
    val positionX: Float = 0f,
    val positionY: Float = 0f,
    val scale: Float = 1f,
    val rotation: Float = 0f,
    val opacity: Float = 1f,
    val volume: Float = 1f,
    val effectIntensity: Float = 1f
)

enum class MaskType {
    NONE, RECTANGLE, CIRCLE, LINEAR, SPLIT, STAR, HEART
}

data class MaskConfig(
    val type: MaskType = MaskType.NONE,
    val feather: Float = 0f,
    val size: Float = 0.5f,
    val rotation: Float = 0f,
    val inverted: Boolean = false
)

data class ChromaKeyConfig(
    val enabled: Boolean = false,
    val targetColorHex: String = "#00FF00",
    val strength: Float = 0.4f,
    val shadow: Float = 0.1f,
    val spillReduction: Float = 0.2f,
    val edgeFeather: Float = 0.05f
)

data class VideoClip(
    val id: String = UUID.randomUUID().toString(),
    val title: String = "Clip",
    val uri: String = "",
    val drawableResName: String? = null,
    val sourceDurationMs: Long = 5000L,
    val trimInMs: Long = 0L,
    val trimOutMs: Long = 5000L,
    val speed: Float = 1.0f,
    val isReverse: Boolean = false,
    val isMuted: Boolean = false,
    val volume: Float = 1.0f,
    val fadeInMs: Long = 0L,
    val fadeOutMs: Long = 0L,
    val rotation: Float = 0f,
    val flipHorizontal: Boolean = false,
    val flipVertical: Boolean = false,
    val scale: Float = 1.0f,
    val positionX: Float = 0f,
    val positionY: Float = 0f,
    val opacity: Float = 1.0f,
    val filterName: String = "Normal",
    val filterIntensity: Float = 1.0f,
    val adjustments: VideoAdjustments = VideoAdjustments(),
    val chromaKey: ChromaKeyConfig = ChromaKeyConfig(),
    val mask: MaskConfig = MaskConfig(),
    val speedCurve: SpeedCurve? = null,
    val keyframes: List<KeyframePoint> = emptyList(),
    val transitionIn: TransitionType = TransitionType.NONE,
    val transitionDurationMs: Long = 500L,
    val bgType: String = "NONE", // NONE, BLUR, COLOR, IMAGE
    val bgColorHex: String = "#000000"
) {
    val durationMs: Long
        get() = ((trimOutMs - trimInMs) / speed).toLong().coerceAtLeast(100L)
}

data class OverlayClip(
    val id: String = UUID.randomUUID().toString(),
    val title: String = "Overlay",
    val uri: String = "",
    val drawableResName: String? = null,
    val startTimeMs: Long = 0L,
    val durationMs: Long = 4000L,
    val positionX: Float = 0f,
    val positionY: Float = 0f,
    val scale: Float = 0.5f,
    val rotation: Float = 0f,
    val opacity: Float = 1.0f,
    val blendMode: String = "NORMAL", // NORMAL, SCREEN, MULTIPLY, OVERLAY, ADD
    val chromaKey: ChromaKeyConfig = ChromaKeyConfig(),
    val mask: MaskConfig = MaskConfig(),
    val keyframes: List<KeyframePoint> = emptyList()
)

data class AudioClip(
    val id: String = UUID.randomUUID().toString(),
    val title: String = "Audio",
    val uri: String = "",
    val presetResName: String? = null,
    val startTimeMs: Long = 0L,
    val durationMs: Long = 5000L,
    val trimInMs: Long = 0L,
    val trimOutMs: Long = 5000L,
    val volume: Float = 1.0f,
    val speed: Float = 1.0f,
    val fadeInMs: Long = 0L,
    val fadeOutMs: Long = 0L,
    val isVoiceover: Boolean = false,
    val isSfx: Boolean = false,
    val category: String = "Music",
    val beats: List<Long> = emptyList(),
    val waveformPoints: List<Float> = emptyList()
)

enum class TextAnimationType {
    NONE, FADE, SLIDE_UP, SLIDE_DOWN, TYPEWRITER, BOUNCE, ZOOM_IN, GLITCH, NEON_FLICKER, WIGGLE, KARAOKE_WIPE
}

data class TextItem(
    val id: String = UUID.randomUUID().toString(),
    val text: String = "Sample Text",
    val startTimeMs: Long = 0L,
    val durationMs: Long = 3000L,
    val fontName: String = "Cinematic Sans",
    val fontSizeSp: Float = 28f,
    val isBold: Boolean = true,
    val isItalic: Boolean = false,
    val alignment: String = "CENTER", // LEFT, CENTER, RIGHT
    val letterSpacingSp: Float = 1f,
    val lineSpacingSp: Float = 4f,
    val textColorHex: String = "#FFFFFF",
    val gradientColors: List<String>? = null,
    val strokeColorHex: String? = "#000000",
    val strokeWidthDp: Float = 3f,
    val shadowColorHex: String? = "#000000",
    val glowColorHex: String? = null,
    val backgroundColorHex: String? = null,
    val bgCornerRadiusDp: Float = 8f,
    val bgPaddingDp: Float = 8f,
    val opacity: Float = 1.0f,
    val rotation: Float = 0f,
    val scale: Float = 1.0f,
    val positionX: Float = 0f,
    val positionY: Float = 0f,
    val animation: TextAnimationType = TextAnimationType.NONE,
    val keyframes: List<KeyframePoint> = emptyList()
)

data class CaptionWord(
    val word: String,
    val startOffsetMs: Long,
    val endOffsetMs: Long,
    val emoji: String? = null
)

enum class CaptionTemplateStyle(val displayName: String, val category: String, val description: String) {
    VIRAL_BEAST("Viral Beast", "Viral", "Bold punchy yellow-white bounce with black outline"),
    MODERN_PODCAST("Modern Podcast", "Podcast", "Pill box background with glowing cyan karaoke word sync"),
    CINEMATIC_MINIMAL("Cinematic Minimal", "Cinematic", "Letterbox spacing, elegant white serif with subtle fade"),
    GAMING_NEON("Gaming Neon", "Gaming", "Electric purple/cyan glow with shake on punch words"),
    LUXURY_GOLD("Luxury Gold", "Luxury", "Golden metallic shimmer serif with smooth underline wipe"),
    BREAKING_NEWS("Breaking News", "News", "High-visibility red/white ticker caption bar"),
    SHORTS_POP("Shorts Pop", "Shorts", "Dynamic word-by-word pop with smart emojis"),
    MOTIVATION_BOLD("Motivation Bold", "Motivation", "All-caps high contrast orange energy punch"),
    RETRO_VHS("Retro VHS", "Meme", "Pixel monospace with chromatic aberration & scanlines"),
    TYPEWRITER_MINIMAL("Typewriter", "Minimal", "Character typewriter stream with blinking cursor"),
    GRADIENT_WAVE("Gradient Wave", "Reels", "Smooth rainbow pastel gradient flowing word by word"),
    EMOJI_PULSE("Emoji Pulse", "Viral", "Dynamic automatic animated emojis on top of active words")
}

data class CaptionItem(
    val id: String = UUID.randomUUID().toString(),
    val fullText: String = "",
    val startTimeMs: Long = 0L,
    val durationMs: Long = 2500L,
    val words: List<CaptionWord> = emptyList(),
    val template: CaptionTemplateStyle = CaptionTemplateStyle.VIRAL_BEAST,
    val translation: String? = null,
    val originalLanguage: String = "English",
    val targetLanguage: String? = null,
    val positionY: Float = 0.72f, // normalized 0..1 from top
    val fontSizeSp: Float = 26f
)

enum class EffectType(val label: String, val category: String) {
    GLITCH("Glitch Noise", "Glitch"),
    SHAKE("Camera Shake", "Shake"),
    BLUR("Gaussian Blur", "Blur"),
    RGB_SPLIT("RGB Split", "Distortion"),
    VHS("VHS Camcorder", "Film"),
    NEON_EDGE("Neon Outline", "Neon"),
    FLASH("White Strobe", "Flash"),
    MOTION_BLUR("Speed Motion Blur", "Motion"),
    RIPPLE("Ripple Waves", "Distortion"),
    LENS_FLARE("Anamorphic Flare", "Light"),
    PARTICLES("Floating Dust", "Particles"),
    COLOR_CYCLE("Cyber Wave", "Neon")
}

data class EffectItem(
    val id: String = UUID.randomUUID().toString(),
    val type: EffectType = EffectType.GLITCH,
    val startTimeMs: Long = 0L,
    val durationMs: Long = 3000L,
    val intensity: Float = 0.75f,
    val speed: Float = 1.0f
)

enum class StickerType {
    EMOJI, SHAPE, ARROW, SPEECH_BUBBLE, SOCIAL_BADGE, ANIMATED
}

data class StickerItem(
    val id: String = UUID.randomUUID().toString(),
    val content: String = "🔥", // emoji or svg name
    val type: StickerType = StickerType.EMOJI,
    val startTimeMs: Long = 0L,
    val durationMs: Long = 3000L,
    val positionX: Float = 0f,
    val positionY: Float = 0f,
    val scale: Float = 1.0f,
    val rotation: Float = 0f,
    val opacity: Float = 1.0f,
    val animation: String = "PULSE" // PULSE, BOUNCE, SPIN, FLOAT, NONE
)

data class TimelineData(
    val videoClips: List<VideoClip> = emptyList(),
    val overlayClips: List<OverlayClip> = emptyList(),
    val audioClips: List<AudioClip> = emptyList(),
    val textItems: List<TextItem> = emptyList(),
    val captions: List<CaptionItem> = emptyList(),
    val effects: List<EffectItem> = emptyList(),
    val stickers: List<StickerItem> = emptyList(),
    val totalDurationMs: Long = 0L
)

@Entity(tableName = "projects")
data class ProjectEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val name: String = "Untitled Project",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val thumbnailUri: String? = null,
    val aspectRatio: AspectRatio = AspectRatio.RATIO_9_16,
    val resolution: ProjectResolution = ProjectResolution.RES_1080P,
    val fps: ProjectFps = ProjectFps.FPS_30,
    val durationMs: Long = 0L,
    val isDraft: Boolean = true,
    val timelineJson: String = ""
)
