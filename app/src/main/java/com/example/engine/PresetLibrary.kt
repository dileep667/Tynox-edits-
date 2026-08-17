package com.example.engine

import com.example.data.model.*

object PresetLibrary {

    val filters: List<FilterPreset> = listOf(
        FilterPreset("norm", "Original", FilterCategory.ALL),
        // Cinematic
        FilterPreset("cin_teal_orange", "Teal & Orange", FilterCategory.CINEMATIC, contrast = 1.25f, saturation = 1.2f, temperature = 0.2f, tint = -0.1f, vignette = 0.2f),
        FilterPreset("cin_moody", "Moody Blockbuster", FilterCategory.CINEMATIC, brightness = -0.05f, contrast = 1.35f, saturation = 0.85f, vignette = 0.35f),
        FilterPreset("cin_anamorphic", "Anamorphic Blue", FilterCategory.CINEMATIC, contrast = 1.15f, saturation = 1.1f, temperature = -0.3f, tint = 0.1f),
        FilterPreset("cin_gold_hour", "Golden Cinema", FilterCategory.CINEMATIC, brightness = 0.05f, contrast = 1.1f, saturation = 1.3f, temperature = 0.5f),

        // Warm
        FilterPreset("warm_sunset", "Warm Sunset", FilterCategory.WARM, brightness = 0.05f, contrast = 1.1f, saturation = 1.25f, temperature = 0.45f),
        FilterPreset("warm_honey", "Honey Glow", FilterCategory.WARM, brightness = 0.08f, contrast = 1.05f, saturation = 1.15f, temperature = 0.35f),
        FilterPreset("warm_autumn", "Autumn Amber", FilterCategory.WARM, contrast = 1.2f, saturation = 1.3f, temperature = 0.4f, tint = 0.15f),

        // Cool
        FilterPreset("cool_arctic", "Arctic Frost", FilterCategory.COOL, brightness = 0.02f, contrast = 1.1f, saturation = 0.9f, temperature = -0.5f),
        FilterPreset("cool_nordic", "Nordic Winter", FilterCategory.COOL, brightness = -0.05f, contrast = 1.2f, saturation = 0.8f, temperature = -0.4f),
        FilterPreset("cool_slate", "Cool Slate", FilterCategory.COOL, contrast = 1.15f, saturation = 0.75f, temperature = -0.25f),

        // Vintage & Film
        FilterPreset("vint_80s", "VHS 1984", FilterCategory.VINTAGE, contrast = 0.9f, saturation = 1.2f, fade = 0.2f, grain = 0.4f),
        FilterPreset("vint_kodak", "Kodak Portra", FilterCategory.FILM, brightness = 0.04f, contrast = 1.15f, saturation = 1.05f, temperature = 0.15f, grain = 0.25f),
        FilterPreset("vint_fuji", "Fujifilm Velvia", FilterCategory.FILM, contrast = 1.3f, saturation = 1.4f, temperature = -0.1f),
        FilterPreset("vint_polaroid", "Polaroid 600", FilterCategory.VINTAGE, brightness = 0.1f, contrast = 0.95f, saturation = 0.9f, fade = 0.3f, vignette = 0.25f),

        // Night & Urban
        FilterPreset("night_neon", "Cyberpunk Neon", FilterCategory.NIGHT, contrast = 1.4f, saturation = 1.5f, temperature = -0.2f, tint = 0.3f, vignette = 0.4f),
        FilterPreset("night_tokyo", "Tokyo Drift", FilterCategory.NIGHT, brightness = -0.05f, contrast = 1.3f, saturation = 1.25f, temperature = -0.35f),
        FilterPreset("urb_bleach", "Bleach Bypass", FilterCategory.URBAN, brightness = 0.05f, contrast = 1.5f, saturation = 0.4f, sharpen = 0.3f),
        FilterPreset("urb_street", "Street Heat", FilterCategory.URBAN, contrast = 1.25f, saturation = 1.1f, temperature = 0.15f, vignette = 0.2f),

        // Portrait & Travel
        FilterPreset("port_soft", "Soft Glow", FilterCategory.PORTRAIT, brightness = 0.08f, contrast = 0.95f, saturation = 1.05f, fade = 0.15f),
        FilterPreset("port_radiant", "Radiant Skin", FilterCategory.PORTRAIT, brightness = 0.05f, contrast = 1.05f, saturation = 1.15f, temperature = 0.1f),
        FilterPreset("trav_tropic", "Tropical Island", FilterCategory.TRAVEL, brightness = 0.05f, contrast = 1.2f, saturation = 1.45f, temperature = 0.2f),
        FilterPreset("trav_desert", "Desert Dunes", FilterCategory.TRAVEL, brightness = 0.05f, contrast = 1.15f, saturation = 1.1f, temperature = 0.45f),

        // Dramatic & Black/White
        FilterPreset("dram_noir", "Film Noir", FilterCategory.BW, brightness = -0.05f, contrast = 1.6f, saturation = 0f, grain = 0.3f, vignette = 0.4f),
        FilterPreset("dram_monochrome", "Monochrome Classic", FilterCategory.BW, contrast = 1.2f, saturation = 0f),
        FilterPreset("dram_high_key", "High Key Silver", FilterCategory.BW, brightness = 0.15f, contrast = 1.1f, saturation = 0f),
        FilterPreset("dram_gothic", "Gothic Shadows", FilterCategory.DRAMATIC, brightness = -0.15f, contrast = 1.5f, saturation = 0.6f, vignette = 0.5f)
    )

    val speedCurves: List<SpeedCurve> = listOf(
        SpeedCurve("Custom", listOf(SpeedCurvePoint(0f, 1f), SpeedCurvePoint(0.5f, 1f), SpeedCurvePoint(1f, 1f))),
        SpeedCurve("Montage", listOf(SpeedCurvePoint(0f, 0.5f), SpeedCurvePoint(0.3f, 2.5f), SpeedCurvePoint(0.7f, 0.4f), SpeedCurvePoint(1f, 2.0f))),
        SpeedCurve("Hero Flow", listOf(SpeedCurvePoint(0f, 3.0f), SpeedCurvePoint(0.4f, 0.3f), SpeedCurvePoint(0.6f, 0.3f), SpeedCurvePoint(1f, 3.0f))),
        SpeedCurve("Bullet Time", listOf(SpeedCurvePoint(0f, 2.0f), SpeedCurvePoint(0.35f, 0.2f), SpeedCurvePoint(0.65f, 0.2f), SpeedCurvePoint(1f, 1.8f))),
        SpeedCurve("Jump Cut", listOf(SpeedCurvePoint(0f, 0.4f), SpeedCurvePoint(0.5f, 4.0f), SpeedCurvePoint(1f, 0.4f))),
        SpeedCurve("Flash In", listOf(SpeedCurvePoint(0f, 5.0f), SpeedCurvePoint(0.2f, 1.0f), SpeedCurvePoint(1f, 1.0f))),
        SpeedCurve("Flash Out", listOf(SpeedCurvePoint(0f, 1.0f), SpeedCurvePoint(0.8f, 1.0f), SpeedCurvePoint(1f, 5.0f)))
    )

    data class MusicTrack(
        val id: String,
        val title: String,
        val artist: String,
        val category: String,
        val durationMs: Long,
        val bpm: Int,
        val beats: List<Long>
    )

    val musicTracks: List<MusicTrack> = listOf(
        MusicTrack("m1", "Neon Cyber Pulse", "Tyox Audio", "Cyberpunk", 15000L, 128, listOf(0L, 468L, 937L, 1406L, 1875L, 2343L, 2812L, 3281L, 3750L, 4218L, 4687L, 5156L, 5625L, 6093L, 6562L, 7031L, 7500L, 7968L, 8437L, 8906L, 9375L, 9843L, 10312L, 10781L, 11250L, 11718L, 12187L, 12656L, 13125L, 13593L, 14062L, 14531L)),
        MusicTrack("m2", "Sunset Lo-Fi Vibes", "Tyox Audio", "Lofi", 18000L, 85, listOf(0L, 705L, 1411L, 2117L, 2823L, 3529L, 4235L, 4941L, 5647L, 6352L, 7058L, 7764L, 8470L, 9176L, 9882L, 10588L, 11294L, 12000L, 12705L, 13411L, 14117L, 14823L, 15529L, 16235L, 16941L, 17647L)),
        MusicTrack("m3", "Cinematic Epic Horizon", "Tyox Audio", "Cinematic", 20000L, 110, listOf(0L, 545L, 1090L, 1636L, 2181L, 2727L, 3272L, 3818L, 4363L, 4909L, 5454L, 6000L, 6545L, 7090L, 7636L, 8181L, 8727L, 9272L, 9818L, 10363L, 10909L, 11454L, 12000L, 12545L, 13090L, 13636L, 14181L, 14727L, 15272L, 15818L, 16363L, 16909L, 17454L, 18000L, 18545L, 19090L, 19636L)),
        MusicTrack("m4", "Vlog Energy Beats", "Tyox Audio", "Vlog", 14000L, 120, listOf(0L, 500L, 1000L, 1500L, 2000L, 2500L, 3000L, 3500L, 4000L, 4500L, 5000L, 5500L, 6000L, 6500L, 7000L, 7500L, 8000L, 8500L, 9000L, 9500L, 10000L, 10500L, 11000L, 11500L, 12000L, 12500L, 13000L, 13500L)),
        MusicTrack("m5", "Trap Drift Impact", "Tyox Audio", "Trending", 16000L, 140, listOf(0L, 428L, 857L, 1285L, 1714L, 2142L, 2571L, 3000L, 3428L, 3857L, 4285L, 4714L, 5142L, 5571L, 6000L, 6428L, 6857L, 7285L, 7714L, 8142L, 8571L, 9000L, 9428L, 9857L, 10285L, 10714L, 11142L, 11571L, 12000L, 12428L, 12857L, 13285L, 13714L, 14142L, 14571L, 15000L, 15428L, 15857L))
    )

    data class SoundEffect(
        val id: String,
        val name: String,
        val category: String,
        val durationMs: Long,
        val icon: String
    )

    val soundEffects: List<SoundEffect> = listOf(
        SoundEffect("sfx_whoosh_fast", "Fast Air Whoosh", "Whoosh", 450L, "air"),
        SoundEffect("sfx_whoosh_heavy", "Heavy Cinematic Swish", "Whoosh", 750L, "air"),
        SoundEffect("sfx_hit_punch", "Punch Impact Hit", "Hit", 600L, "flash_on"),
        SoundEffect("sfx_hit_bass", "Bass Drop Impact", "Hit", 1200L, "speaker"),
        SoundEffect("sfx_pop_bubble", "Clean Pop Bubble", "Pop", 300L, "bubble_chart"),
        SoundEffect("sfx_click_ui", "Mechanical Mouse Click", "Click", 180L, "touch_app"),
        SoundEffect("sfx_camera_shutter", "DSLR Camera Shutter", "Camera", 650L, "photo_camera"),
        SoundEffect("sfx_trans_swish", "Whip Transition Swish", "Transition", 550L, "transform"),
        SoundEffect("sfx_game_coin", "8-Bit Coin Up", "Gaming", 400L, "sports_esports"),
        SoundEffect("sfx_game_powerup", "Arcade Powerup", "Gaming", 850L, "sports_esports"),
        SoundEffect("sfx_horror_riser", "Horror Tension Sting", "Horror", 2200L, "warning"),
        SoundEffect("sfx_cin_boom", "Cinematic Sub Boom", "Cinematic", 1800L, "movie"),
        SoundEffect("sfx_cin_riser", "Epic Trailer Riser", "Cinematic", 3000L, "trending_up"),
        SoundEffect("sfx_glitch_swoop", "Glitch Tape Rewind", "Glitch", 700L, "tune"),
        SoundEffect("sfx_notif_chime", "Crystal Bell Alert", "Notification", 500L, "notifications"),
        SoundEffect("sfx_crowd_cheer", "Audience Applause & Cheer", "Crowd", 3500L, "groups"),
        SoundEffect("sfx_amb_rain", "Soft Window Rain", "Ambient", 6000L, "water_drop"),
        SoundEffect("sfx_amb_city", "Night City Traffic", "Ambient", 6000L, "location_city")
    )

    val fontStyles: List<String> = listOf(
        "Cinematic Sans",
        "Bold Impact",
        "Elegant Serif",
        "Modern Monospace",
        "Cyberpunk Display",
        "Handwritten Script",
        "Retro Arcade",
        "Editorial Header"
    )

    val textGradients: List<Pair<String, List<String>>> = listOf(
        "Electric Cyan" to listOf("#00F0FF", "#0072FF"),
        "Neon Purple" to listOf("#E040FB", "#7C4DFF"),
        "Sunset Gold" to listOf("#FF9100", "#FF1744"),
        "Cyber Pink" to listOf("#FF007F", "#7928CA"),
        "Emerald Glow" to listOf("#00E676", "#00B0FF"),
        "Pure White" to listOf("#FFFFFF", "#E0E0E0")
    )

    val stickers: List<Pair<String, String>> = listOf(
        "🔥" to "Fire", "⚡" to "Lightning", "🚀" to "Rocket", "💡" to "Idea",
        "💰" to "Money", "💯" to "100", "👑" to "Crown", "⭐" to "Star",
        "🎯" to "Target", "✨" to "Sparkles", "🎬" to "Clapper", "🎙️" to "Mic",
        "👀" to "Eyes", "🤯" to "Mind Blown", "💥" to "Boom", "🔊" to "Loud",
        "❤️" to "Heart", "👍" to "Thumbs Up", "🚨" to "Alert", "💎" to "Diamond"
    )
}
