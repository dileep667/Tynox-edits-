package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.*
import com.example.data.repository.ProjectRepository
import com.example.engine.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.util.UUID

enum class EditorToolbarTab {
    NONE, EDIT, AUDIO, TEXT, CAPTIONS, STICKERS, EFFECTS, FILTERS, ADJUST, OVERLAY, TTS, AI_TOOLS
}

enum class SelectedTrackType {
    NONE, VIDEO, OVERLAY, AUDIO, TEXT, CAPTION, EFFECT, STICKER
}

class EditorViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ProjectRepository(AppDatabase.getDatabase(application).projectDao())
    val ttsManager = TextToSpeechManager(application)

    private val _project = MutableStateFlow<ProjectEntity?>(null)
    val project: StateFlow<ProjectEntity?> = _project.asStateFlow()

    private val _timeline = MutableStateFlow(TimelineData())
    val timeline: StateFlow<TimelineData> = _timeline.asStateFlow()

    private val _playheadMs = MutableStateFlow(0L)
    val playheadMs: StateFlow<Long> = _playheadMs.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _activeTab = MutableStateFlow(EditorToolbarTab.NONE)
    val activeTab: StateFlow<EditorToolbarTab> = _activeTab.asStateFlow()

    private val _selectedTrackType = MutableStateFlow(SelectedTrackType.NONE)
    val selectedTrackType: StateFlow<SelectedTrackType> = _selectedTrackType.asStateFlow()

    private val _selectedItemId = MutableStateFlow<String?>(null)
    val selectedItemId: StateFlow<String?> = _selectedItemId.asStateFlow()

    private val _timelineZoom = MutableStateFlow(1.0f) // 0.5f to 3.0f
    val timelineZoom: StateFlow<Float> = _timelineZoom.asStateFlow()

    private val _isFullscreenPreview = MutableStateFlow(false)
    val isFullscreenPreview: StateFlow<Boolean> = _isFullscreenPreview.asStateFlow()

    private val _statusMessage = MutableStateFlow<String?>(null)
    val statusMessage: StateFlow<String?> = _statusMessage.asStateFlow()

    // Undo / Redo stacks
    private val undoStack = mutableListOf<TimelineData>()
    private val redoStack = mutableListOf<TimelineData>()
    val canUndo = MutableStateFlow(false)
    val canRedo = MutableStateFlow(false)

    private var playbackJob: Job? = null

    fun loadProject(projectId: String) {
        viewModelScope.launch {
            val proj = repository.getProjectById(projectId)
            if (proj != null) {
                _project.value = proj
                val loadedTimeline = ProjectRepository.deserializeTimeline(proj.timelineJson)
                _timeline.value = loadedTimeline
                recalculateDuration()
            }
        }
    }

    private fun pushUndoSnapshot() {
        undoStack.add(_timeline.value.copy())
        if (undoStack.size > 30) undoStack.removeAt(0)
        redoStack.clear()
        updateUndoRedoStates()
        autoSave()
    }

    private fun updateUndoRedoStates() {
        canUndo.value = undoStack.isNotEmpty()
        canRedo.value = redoStack.isNotEmpty()
    }

    fun undo() {
        if (undoStack.isNotEmpty()) {
            val previous = undoStack.removeAt(undoStack.lastIndex)
            redoStack.add(_timeline.value.copy())
            _timeline.value = previous
            updateUndoRedoStates()
            recalculateDuration()
            showStatus("Undo")
            autoSave()
        }
    }

    fun redo() {
        if (redoStack.isNotEmpty()) {
            val next = redoStack.removeAt(redoStack.lastIndex)
            undoStack.add(_timeline.value.copy())
            _timeline.value = next
            updateUndoRedoStates()
            recalculateDuration()
            showStatus("Redo")
            autoSave()
        }
    }

    fun showStatus(msg: String) {
        _statusMessage.value = msg
        viewModelScope.launch {
            delay(2000)
            if (_statusMessage.value == msg) {
                _statusMessage.value = null
            }
        }
    }

    fun togglePlayPause() {
        if (_isPlaying.value) {
            pause()
        } else {
            play()
        }
    }

    fun play() {
        if (_timeline.value.totalDurationMs <= 0L) return
        _isPlaying.value = true
        playbackJob?.cancel()
        playbackJob = viewModelScope.launch {
            val startTime = System.currentTimeMillis()
            val startPlayhead = _playheadMs.value
            while (_isPlaying.value) {
                delay(33) // ~30 fps update
                val elapsed = System.currentTimeMillis() - startTime
                val newPos = startPlayhead + elapsed
                val total = _timeline.value.totalDurationMs
                if (newPos >= total) {
                    _playheadMs.value = 0L
                    _isPlaying.value = false
                    break
                } else {
                    _playheadMs.value = newPos
                }
            }
        }
    }

    fun pause() {
        _isPlaying.value = false
        playbackJob?.cancel()
    }

    fun seekTo(timeMs: Long) {
        val total = _timeline.value.totalDurationMs.coerceAtLeast(1000L)
        _playheadMs.value = timeMs.coerceIn(0L, total)
    }

    fun stepFrame(forward: Boolean) {
        pause()
        val stepMs = 33L // ~30fps frame
        val target = if (forward) _playheadMs.value + stepMs else _playheadMs.value - stepMs
        seekTo(target)
    }

    fun setZoom(zoom: Float) {
        _timelineZoom.value = zoom.coerceIn(0.5f, 3.5f)
    }

    fun toggleFullscreenPreview() {
        _isFullscreenPreview.value = !_isFullscreenPreview.value
    }

    fun setActiveTab(tab: EditorToolbarTab) {
        _activeTab.value = if (_activeTab.value == tab) EditorToolbarTab.NONE else tab
    }

    fun selectItem(trackType: SelectedTrackType, itemId: String?) {
        _selectedTrackType.value = trackType
        _selectedItemId.value = itemId
        if (itemId != null && trackType == SelectedTrackType.VIDEO) {
            _activeTab.value = EditorToolbarTab.EDIT
        }
    }

    private fun recalculateDuration() {
        val cur = _timeline.value
        val maxVideo = cur.videoClips.sumOf { it.durationMs }
        val maxAudio = cur.audioClips.maxOfOrNull { it.startTimeMs + it.durationMs } ?: 0L
        val maxText = cur.textItems.maxOfOrNull { it.startTimeMs + it.durationMs } ?: 0L
        val maxCap = cur.captions.maxOfOrNull { it.startTimeMs + it.durationMs } ?: 0L
        val maxEffect = cur.effects.maxOfOrNull { it.startTimeMs + it.durationMs } ?: 0L
        val maxSticker = cur.stickers.maxOfOrNull { it.startTimeMs + it.durationMs } ?: 0L

        val total = maxOf(maxVideo, maxAudio, maxText, maxCap, maxEffect, maxSticker, 1000L)
        _timeline.value = cur.copy(totalDurationMs = total)
    }

    private fun autoSave() {
        val proj = _project.value ?: return
        viewModelScope.launch {
            val updated = proj.copy(
                updatedAt = System.currentTimeMillis(),
                durationMs = _timeline.value.totalDurationMs,
                timelineJson = ProjectRepository.serializeTimeline(_timeline.value)
            )
            _project.value = updated
            repository.saveProject(updated)
        }
    }

    // ==================== VIDEO CLIP TOOLS ====================

    fun getSelectedVideoClip(): VideoClip? {
        val id = _selectedItemId.value ?: return _timeline.value.videoClips.firstOrNull()
        return _timeline.value.videoClips.find { it.id == id }
    }

    fun updateSelectedVideoClip(updater: (VideoClip) -> VideoClip) {
        val currentClip = getSelectedVideoClip() ?: return
        pushUndoSnapshot()
        val updatedList = _timeline.value.videoClips.map { clip ->
            if (clip.id == currentClip.id) updater(clip) else clip
        }
        _timeline.value = _timeline.value.copy(videoClips = updatedList)
        recalculateDuration()
        autoSave()
    }

    fun splitClipAtPlayhead() {
        val currentPlayhead = _playheadMs.value
        val clips = _timeline.value.videoClips
        var accumulatedTime = 0L

        val targetIndex = clips.indexOfFirst { clip ->
            val end = accumulatedTime + clip.durationMs
            val matches = currentPlayhead > accumulatedTime + 100L && currentPlayhead < end - 100L
            if (!matches) accumulatedTime += clip.durationMs
            matches
        }

        if (targetIndex != -1) {
            pushUndoSnapshot()
            val targetClip = clips[targetIndex]
            val splitOffsetInClip = ((currentPlayhead - accumulatedTime) * targetClip.speed).toLong()
            val originalTrimIn = targetClip.trimInMs
            val originalTrimOut = targetClip.trimOutMs

            val firstHalf = targetClip.copy(
                id = UUID.randomUUID().toString(),
                trimOutMs = originalTrimIn + splitOffsetInClip
            )
            val secondHalf = targetClip.copy(
                id = UUID.randomUUID().toString(),
                trimInMs = originalTrimIn + splitOffsetInClip,
                trimOutMs = originalTrimOut,
                transitionIn = TransitionType.NONE
            )

            val newClips = clips.toMutableList().apply {
                removeAt(targetIndex)
                add(targetIndex, secondHalf)
                add(targetIndex, firstHalf)
            }

            _timeline.value = _timeline.value.copy(videoClips = newClips)
            _selectedItemId.value = secondHalf.id
            recalculateDuration()
            showStatus("Split clip at playhead")
            autoSave()
        } else {
            showStatus("Position playhead over clip to split")
        }
    }

    fun deleteSelectedClip() {
        val currentClip = getSelectedVideoClip() ?: return
        if (_timeline.value.videoClips.size <= 1) {
            showStatus("Cannot delete the last remaining clip")
            return
        }
        pushUndoSnapshot()
        val newClips = _timeline.value.videoClips.filter { it.id != currentClip.id }
        _timeline.value = _timeline.value.copy(videoClips = newClips)
        _selectedItemId.value = newClips.firstOrNull()?.id
        recalculateDuration()
        showStatus("Deleted clip")
        autoSave()
    }

    fun duplicateSelectedClip() {
        val currentClip = getSelectedVideoClip() ?: return
        pushUndoSnapshot()
        val index = _timeline.value.videoClips.indexOfFirst { it.id == currentClip.id }
        val copy = currentClip.copy(id = UUID.randomUUID().toString(), title = "${currentClip.title} (Copy)")
        val newClips = _timeline.value.videoClips.toMutableList().apply {
            add(index + 1, copy)
        }
        _timeline.value = _timeline.value.copy(videoClips = newClips)
        _selectedItemId.value = copy.id
        recalculateDuration()
        showStatus("Duplicated clip")
        autoSave()
    }

    fun moveClip(forward: Boolean) {
        val currentClip = getSelectedVideoClip() ?: return
        val clips = _timeline.value.videoClips.toMutableList()
        val index = clips.indexOfFirst { it.id == currentClip.id }
        if (index == -1) return

        val targetIndex = if (forward) index + 1 else index - 1
        if (targetIndex in 0 until clips.size) {
            pushUndoSnapshot()
            val item = clips.removeAt(index)
            clips.add(targetIndex, item)
            _timeline.value = _timeline.value.copy(videoClips = clips)
            recalculateDuration()
            showStatus(if (forward) "Moved right" else "Moved left")
            autoSave()
        }
    }

    fun setClipSpeed(speed: Float) {
        updateSelectedVideoClip { it.copy(speed = speed) }
        showStatus("Speed set to ${speed}x")
    }

    fun setSpeedCurve(curve: SpeedCurve) {
        updateSelectedVideoClip { it.copy(speedCurve = curve) }
        showStatus("Applied ${curve.name} curve")
    }

    fun toggleReverse() {
        updateSelectedVideoClip { it.copy(isReverse = !it.isReverse) }
        showStatus("Reverse toggled")
    }

    fun freezeFrame() {
        val currentClip = getSelectedVideoClip() ?: return
        pushUndoSnapshot()
        val freezeClip = currentClip.copy(
            id = UUID.randomUUID().toString(),
            title = "Freeze Frame",
            trimInMs = 0L,
            trimOutMs = 2500L,
            sourceDurationMs = 2500L
        )
        val index = _timeline.value.videoClips.indexOfFirst { it.id == currentClip.id }
        val newClips = _timeline.value.videoClips.toMutableList().apply {
            add(index + 1, freezeClip)
        }
        _timeline.value = _timeline.value.copy(videoClips = newClips)
        recalculateDuration()
        showStatus("Inserted 2.5s Freeze Frame")
        autoSave()
    }

    fun rotateClip() {
        updateSelectedVideoClip { it.copy(rotation = (it.rotation + 90f) % 360f) }
    }

    fun flipHorizontal() {
        updateSelectedVideoClip { it.copy(flipHorizontal = !it.flipHorizontal) }
    }

    fun flipVertical() {
        updateSelectedVideoClip { it.copy(flipVertical = !it.flipVertical) }
    }

    fun applyFilter(preset: FilterPreset, intensity: Float = 1.0f) {
        updateSelectedVideoClip {
            it.copy(
                filterName = preset.name,
                filterIntensity = intensity,
                adjustments = if (preset.name == "Original") VideoAdjustments() else {
                    it.adjustments.copy(
                        brightness = preset.brightness,
                        contrast = preset.contrast,
                        saturation = preset.saturation,
                        temperature = preset.temperature,
                        tint = preset.tint,
                        vignette = preset.vignette,
                        grain = preset.grain
                    )
                }
            )
        }
        showStatus("Applied filter: ${preset.name}")
    }

    fun updateAdjustments(adjustments: VideoAdjustments) {
        updateSelectedVideoClip { it.copy(adjustments = adjustments) }
    }

    fun resetAdjustments() {
        updateSelectedVideoClip { it.copy(adjustments = VideoAdjustments(), filterName = "Normal", filterIntensity = 1.0f) }
        showStatus("Reset video adjustments")
    }

    fun setTransition(type: TransitionType, durationMs: Long = 500L) {
        updateSelectedVideoClip { it.copy(transitionIn = type, transitionDurationMs = durationMs) }
        showStatus("Applied ${type.label} transition")
    }

    fun setMask(type: MaskType, feather: Float = 0.1f, size: Float = 0.5f) {
        updateSelectedVideoClip { it.copy(mask = MaskConfig(type = type, feather = feather, size = size)) }
        showStatus("Mask: ${type.name}")
    }

    fun setChromaKey(enabled: Boolean, colorHex: String = "#00FF00", strength: Float = 0.4f) {
        updateSelectedVideoClip {
            it.copy(chromaKey = ChromaKeyConfig(enabled = enabled, targetColorHex = colorHex, strength = strength))
        }
        showStatus(if (enabled) "Chroma key enabled" else "Chroma key disabled")
    }

    fun addVideoClip(uri: String, resName: String? = null, title: String = "Media Clip") {
        pushUndoSnapshot()
        val newClip = VideoClip(
            title = title,
            uri = uri,
            drawableResName = resName,
            sourceDurationMs = 5000L,
            trimInMs = 0L,
            trimOutMs = 5000L
        )
        val updatedClips = _timeline.value.videoClips + newClip
        _timeline.value = _timeline.value.copy(videoClips = updatedClips)
        _selectedItemId.value = newClip.id
        recalculateDuration()
        showStatus("Added media clip")
        autoSave()
    }

    // ==================== KEYFRAME SYSTEM ====================

    fun addOrUpdateKeyframeOnSelectedClip() {
        val clip = getSelectedVideoClip() ?: return
        val currentPlayhead = _playheadMs.value
        val existing = clip.keyframes.toMutableList()
        val newKf = KeyframePoint(
            timeMs = currentPlayhead,
            positionX = clip.positionX,
            positionY = clip.positionY,
            scale = clip.scale,
            rotation = clip.rotation,
            opacity = clip.opacity,
            volume = clip.volume
        )
        val index = existing.indexOfFirst { kotlin.math.abs(it.timeMs - currentPlayhead) < 150L }
        if (index != -1) {
            existing[index] = newKf
        } else {
            existing.add(newKf)
            existing.sortBy { it.timeMs }
        }
        updateSelectedVideoClip { it.copy(keyframes = existing) }
        showStatus("Keyframe added at ${currentPlayhead}ms")
    }

    // ==================== AUDIO & SFX TOOLS ====================

    fun addAudioTrack(musicTrack: PresetLibrary.MusicTrack) {
        pushUndoSnapshot()
        val audio = AudioClip(
            title = musicTrack.title,
            category = musicTrack.category,
            startTimeMs = _playheadMs.value,
            durationMs = musicTrack.durationMs,
            trimInMs = 0L,
            trimOutMs = musicTrack.durationMs,
            beats = musicTrack.beats,
            waveformPoints = (0..50).map { (0.2f + 0.8f * kotlin.math.abs(kotlin.math.sin(it * 0.4))).toFloat() }
        )
        val list = _timeline.value.audioClips + audio
        _timeline.value = _timeline.value.copy(audioClips = list)
        _selectedItemId.value = audio.id
        _selectedTrackType.value = SelectedTrackType.AUDIO
        recalculateDuration()
        showStatus("Added audio: ${musicTrack.title}")
        autoSave()
    }

    fun addSoundEffect(sfx: PresetLibrary.SoundEffect) {
        pushUndoSnapshot()
        val audio = AudioClip(
            title = sfx.name,
            category = "SFX",
            isSfx = true,
            startTimeMs = _playheadMs.value,
            durationMs = sfx.durationMs,
            trimInMs = 0L,
            trimOutMs = sfx.durationMs,
            waveformPoints = (0..20).map { (0.3f + 0.7f * kotlin.math.abs(kotlin.math.sin(it * 0.8))).toFloat() }
        )
        val list = _timeline.value.audioClips + audio
        _timeline.value = _timeline.value.copy(audioClips = list)
        _selectedItemId.value = audio.id
        _selectedTrackType.value = SelectedTrackType.AUDIO
        recalculateDuration()
        showStatus("Added SFX: ${sfx.name}")
        autoSave()
    }

    fun addRecordedVoiceover(durationMs: Long, audioUri: String = "mic://voiceover") {
        pushUndoSnapshot()
        val audio = AudioClip(
            title = "Voiceover",
            uri = audioUri,
            category = "Voiceover",
            isVoiceover = true,
            startTimeMs = _playheadMs.value,
            durationMs = durationMs,
            trimInMs = 0L,
            trimOutMs = durationMs,
            waveformPoints = (0..35).map { (0.25f + 0.75f * kotlin.math.abs(kotlin.math.sin(it * 0.5))).toFloat() }
        )
        val list = _timeline.value.audioClips + audio
        _timeline.value = _timeline.value.copy(audioClips = list)
        _selectedItemId.value = audio.id
        _selectedTrackType.value = SelectedTrackType.AUDIO
        recalculateDuration()
        showStatus("Added recorded voiceover")
        autoSave()
    }

    fun deleteAudioClip(id: String) {
        pushUndoSnapshot()
        val list = _timeline.value.audioClips.filter { it.id != id }
        _timeline.value = _timeline.value.copy(audioClips = list)
        if (_selectedItemId.value == id) _selectedItemId.value = null
        recalculateDuration()
        showStatus("Deleted audio clip")
        autoSave()
    }

    fun extractAudioFromVideo() {
        val clip = getSelectedVideoClip() ?: return
        pushUndoSnapshot()
        val extracted = AudioClip(
            title = "Extracted: ${clip.title}",
            category = "Extracted",
            startTimeMs = _playheadMs.value,
            durationMs = clip.durationMs,
            trimInMs = 0L,
            trimOutMs = clip.durationMs,
            waveformPoints = (0..30).map { (0.2f + 0.8f * kotlin.math.abs(kotlin.math.cos(it * 0.4))).toFloat() }
        )
        val updatedAudios = _timeline.value.audioClips + extracted
        _timeline.value = _timeline.value.copy(audioClips = updatedAudios)
        recalculateDuration()
        showStatus("Extracted audio to timeline")
        autoSave()
    }

    // ==================== TEXT STUDIO ====================

    fun addTextItem(text: String = "Heading Text", font: String = "Cinematic Sans", colorHex: String = "#FFFFFF") {
        pushUndoSnapshot()
        val newItem = TextItem(
            text = text,
            fontName = font,
            textColorHex = colorHex,
            startTimeMs = _playheadMs.value,
            durationMs = 3000L
        )
        val list = _timeline.value.textItems + newItem
        _timeline.value = _timeline.value.copy(textItems = list)
        _selectedItemId.value = newItem.id
        _selectedTrackType.value = SelectedTrackType.TEXT
        _activeTab.value = EditorToolbarTab.TEXT
        recalculateDuration()
        showStatus("Added text layer")
        autoSave()
    }

    fun updateTextItem(item: TextItem) {
        pushUndoSnapshot()
        val list = _timeline.value.textItems.map { if (it.id == item.id) item else it }
        _timeline.value = _timeline.value.copy(textItems = list)
        autoSave()
    }

    fun deleteTextItem(id: String) {
        pushUndoSnapshot()
        val list = _timeline.value.textItems.filter { it.id != id }
        _timeline.value = _timeline.value.copy(textItems = list)
        if (_selectedItemId.value == id) _selectedItemId.value = null
        recalculateDuration()
        showStatus("Deleted text layer")
        autoSave()
    }

    // ==================== CAPTIONS SYSTEM ====================

    fun generateAutoCaptions(language: String = "English", template: CaptionTemplateStyle = CaptionTemplateStyle.VIRAL_BEAST) {
        pushUndoSnapshot()
        val totalDur = _timeline.value.totalDurationMs.coerceAtLeast(8000L)
        val generated = SpeechCaptionRecognizer.generateAutoCaptions(
            sourceLanguage = language,
            targetDurationMs = totalDur,
            template = template
        )
        _timeline.value = _timeline.value.copy(captions = generated)
        _activeTab.value = EditorToolbarTab.CAPTIONS
        recalculateDuration()
        showStatus("Generated auto-captions (${generated.size} segments)")
        autoSave()
    }

    fun translateCaptions(targetLanguage: String) {
        val currentCaps = _timeline.value.captions
        if (currentCaps.isEmpty()) {
            showStatus("Generate auto-captions first before translating")
            return
        }
        pushUndoSnapshot()
        val translated = SpeechCaptionRecognizer.translateCaptions(currentCaps, targetLanguage)
        _timeline.value = _timeline.value.copy(captions = translated)
        showStatus("Translated captions into $targetLanguage")
        autoSave()
    }

    fun updateCaptionTemplate(template: CaptionTemplateStyle) {
        pushUndoSnapshot()
        val updated = _timeline.value.captions.map { it.copy(template = template) }
        _timeline.value = _timeline.value.copy(captions = updated)
        showStatus("Applied caption style: ${template.displayName}")
        autoSave()
    }

    fun updateCaptionItem(caption: CaptionItem) {
        pushUndoSnapshot()
        val list = _timeline.value.captions.map { if (it.id == caption.id) caption else it }
        _timeline.value = _timeline.value.copy(captions = list)
        autoSave()
    }

    fun deleteCaptionItem(id: String) {
        pushUndoSnapshot()
        val list = _timeline.value.captions.filter { it.id != id }
        _timeline.value = _timeline.value.copy(captions = list)
        recalculateDuration()
        autoSave()
    }

    // ==================== TTS STUDIO ====================

    fun generateTtsVoiceClip(text: String, voiceOption: TtsVoiceOption, pitch: Float = 1.0f, speed: Float = 1.0f) {
        if (text.isBlank()) return
        pushUndoSnapshot()
        val clip = ttsManager.createTimelineAudioClip(
            text = text,
            voiceOption = voiceOption,
            startTimeMs = _playheadMs.value,
            speed = speed
        )
        val list = _timeline.value.audioClips + clip
        _timeline.value = _timeline.value.copy(audioClips = list)
        _selectedItemId.value = clip.id
        _selectedTrackType.value = SelectedTrackType.AUDIO
        recalculateDuration()
        showStatus("Generated TTS voice clip")
        autoSave()
    }

    // ==================== EFFECTS & STICKERS ====================

    fun addEffect(type: EffectType, intensity: Float = 0.75f) {
        pushUndoSnapshot()
        val item = EffectItem(
            type = type,
            startTimeMs = _playheadMs.value,
            durationMs = 3000L,
            intensity = intensity
        )
        val list = _timeline.value.effects + item
        _timeline.value = _timeline.value.copy(effects = list)
        _selectedItemId.value = item.id
        _selectedTrackType.value = SelectedTrackType.EFFECT
        recalculateDuration()
        showStatus("Added effect: ${type.label}")
        autoSave()
    }

    fun deleteEffect(id: String) {
        pushUndoSnapshot()
        val list = _timeline.value.effects.filter { it.id != id }
        _timeline.value = _timeline.value.copy(effects = list)
        recalculateDuration()
        autoSave()
    }

    fun addSticker(emojiOrSymbol: String) {
        pushUndoSnapshot()
        val item = StickerItem(
            content = emojiOrSymbol,
            startTimeMs = _playheadMs.value,
            durationMs = 3000L,
            positionX = 0f,
            positionY = 0f
        )
        val list = _timeline.value.stickers + item
        _timeline.value = _timeline.value.copy(stickers = list)
        _selectedItemId.value = item.id
        _selectedTrackType.value = SelectedTrackType.STICKER
        recalculateDuration()
        showStatus("Added sticker: $emojiOrSymbol")
        autoSave()
    }

    fun deleteSticker(id: String) {
        pushUndoSnapshot()
        val list = _timeline.value.stickers.filter { it.id != id }
        _timeline.value = _timeline.value.copy(stickers = list)
        recalculateDuration()
        autoSave()
    }

    // ==================== AI TOOLS SUITE ====================

    fun applySmartAutoTrimming() {
        pushUndoSnapshot()
        val trimmed = AiToolsEngine.applySmartTrimming(_timeline.value.videoClips)
        _timeline.value = _timeline.value.copy(videoClips = trimmed)
        recalculateDuration()
        showStatus("AI Smart Trim applied (silences removed)")
        autoSave()
    }

    fun applyAutoReframe() {
        pushUndoSnapshot()
        val reframed = _timeline.value.videoClips.map { AiToolsEngine.autoReframeToVertical(it) }
        _timeline.value = _timeline.value.copy(videoClips = reframed)
        showStatus("AI Auto Reframe applied for 9:16")
        autoSave()
    }

    fun applyBeatSync(musicTrack: PresetLibrary.MusicTrack) {
        addAudioTrack(musicTrack)
        showStatus("Beat markers generated on timeline")
    }

    fun applySceneDetection() {
        val clip = getSelectedVideoClip() ?: return
        val cuts = AiToolsEngine.detectSceneCuts(clip)
        if (cuts.isEmpty()) {
            showStatus("No additional scene cuts detected in this clip")
            return
        }
        pushUndoSnapshot()
        // Split at first detected cut
        _playheadMs.value = cuts.first()
        splitClipAtPlayhead()
        showStatus("AI Scene Cut split at ${cuts.first()}ms")
    }

    override fun onCleared() {
        super.onCleared()
        playbackJob?.cancel()
        ttsManager.release()
    }
}
