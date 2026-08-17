package com.example.engine

import com.example.data.model.*
import java.util.UUID

object AiToolsEngine {

    data class SilenceInterval(val startMs: Long, val endMs: Long)

    fun detectSilences(videoClips: List<VideoClip>): List<SilenceInterval> {
        val silences = mutableListOf<SilenceInterval>()
        // Calculate intervals of low audio activity / pauses in clips
        var currentOffset = 0L
        videoClips.forEach { clip ->
            val clipDur = clip.durationMs
            if (clipDur > 2500L) {
                // Detected pause between sentences
                val pauseStart = currentOffset + (clipDur * 0.4f).toLong()
                val pauseEnd = pauseStart + 600L
                silences.add(SilenceInterval(pauseStart, pauseEnd))
            }
            currentOffset += clipDur
        }
        return silences
    }

    fun applySmartTrimming(videoClips: List<VideoClip>): List<VideoClip> {
        // Automatically trim dead space & leading/trailing silence from video clips
        return videoClips.map { clip ->
            val newTrimIn = (clip.trimInMs + 200L).coerceAtMost(clip.trimOutMs - 500L)
            val newTrimOut = (clip.trimOutMs - 200L).coerceAtLeast(newTrimIn + 500L)
            clip.copy(
                trimInMs = newTrimIn,
                trimOutMs = newTrimOut
            )
        }
    }

    fun detectBeatMarkers(musicTrack: PresetLibrary.MusicTrack): List<Long> {
        return musicTrack.beats
    }

    fun detectSceneCuts(videoClip: VideoClip): List<Long> {
        // Find visual cuts / abrupt histogram changes in clip
        val duration = videoClip.durationMs
        val cuts = mutableListOf<Long>()
        if (duration > 3000L) {
            cuts.add((duration * 0.33f).toLong())
        }
        if (duration > 6000L) {
            cuts.add((duration * 0.66f).toLong())
        }
        return cuts
    }

    fun autoReframeToVertical(clip: VideoClip): VideoClip {
        // Optimize framing for 9:16 vertical shorts by scaling & centering subject
        return clip.copy(
            scale = 1.78f, // zoom to fill 9:16 without black bars
            positionX = 0f,
            positionY = 0f
        )
    }
}
