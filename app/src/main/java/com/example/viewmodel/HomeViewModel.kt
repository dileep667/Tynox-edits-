package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.*
import com.example.data.repository.ProjectRepository
import com.example.engine.PresetLibrary
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ProjectRepository = ProjectRepository(
        AppDatabase.getDatabase(application).projectDao()
    )

    val allProjects: StateFlow<List<ProjectEntity>> = repository.allProjects
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val draftProjects: StateFlow<List<ProjectEntity>> = repository.draftProjects
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedTab = MutableStateFlow(0) // 0: Home, 1: Projects, 2: Templates, 3: Settings
    val selectedTab: StateFlow<Int> = _selectedTab.asStateFlow()

    fun setTab(index: Int) {
        _selectedTab.value = index
    }

    init {
        // Seed default sample projects if database is fresh
        viewModelScope.launch {
            allProjects.first { projects ->
                if (projects.isEmpty()) {
                    createSeedProjects()
                }
                true
            }
        }
    }

    private suspend fun createSeedProjects() {
        // Demo project 1: Cyberpunk Reel
        val clip1 = VideoClip(
            title = "Cyberpunk Night Drive",
            drawableResName = "sample_cyberpunk",
            sourceDurationMs = 6000L,
            trimInMs = 0L,
            trimOutMs = 6000L,
            filterName = "Cyberpunk Neon",
            filterIntensity = 0.9f
        )
        val clip2 = VideoClip(
            title = "Mountain Sunrise View",
            drawableResName = "sample_reels_travel",
            sourceDurationMs = 5000L,
            trimInMs = 0L,
            trimOutMs = 5000L,
            filterName = "Teal & Orange",
            filterIntensity = 0.8f,
            transitionIn = TransitionType.ZOOM_IN
        )
        val audio1 = AudioClip(
            title = "Neon Cyber Pulse",
            category = "Cyberpunk",
            startTimeMs = 0L,
            durationMs = 11000L,
            trimInMs = 0L,
            trimOutMs = 11000L,
            beats = PresetLibrary.musicTracks[0].beats,
            waveformPoints = (0..40).map { (0.2f + 0.8f * kotlin.math.abs(kotlin.math.sin(it * 0.4))).toFloat() }
        )
        val text1 = TextItem(
            text = "TYOX PRO EDIT",
            startTimeMs = 500L,
            durationMs = 3500L,
            fontName = "Cyberpunk Display",
            fontSizeSp = 32f,
            textColorHex = "#00F0FF",
            glowColorHex = "#00F0FF",
            animation = TextAnimationType.GLITCH
        )

        val timeline1 = TimelineData(
            videoClips = listOf(clip1, clip2),
            audioClips = listOf(audio1),
            textItems = listOf(text1),
            totalDurationMs = 11000L
        )

        val project1 = ProjectEntity(
            id = UUID.randomUUID().toString(),
            name = "Cyberpunk Reel 4K",
            aspectRatio = AspectRatio.RATIO_9_16,
            resolution = ProjectResolution.RES_4K,
            fps = ProjectFps.FPS_60,
            durationMs = 11000L,
            thumbnailUri = "sample_cyberpunk",
            timelineJson = ProjectRepository.serializeTimeline(timeline1)
        )

        // Demo project 2: Creator Podcast
        val clipP1 = VideoClip(
            title = "Studio Podcast Interview",
            drawableResName = "sample_podcast_host",
            sourceDurationMs = 8000L,
            trimInMs = 0L,
            trimOutMs = 8000L,
            filterName = "Moody Blockbuster",
            filterIntensity = 0.7f
        )
        val cap1 = CaptionItem(
            fullText = "Creating viral content on mobile has never been faster with Tyox.",
            startTimeMs = 500L,
            durationMs = 3800L,
            words = listOf(
                CaptionWord("Creating", 0L, 350L),
                CaptionWord("viral", 350L, 750L, emoji = "🔥"),
                CaptionWord("content", 750L, 1200L),
                CaptionWord("on", 1200L, 1400L),
                CaptionWord("mobile", 1400L, 1900L, emoji = "📱"),
                CaptionWord("faster", 1900L, 2500L, emoji = "⚡"),
                CaptionWord("with", 2500L, 2800L),
                CaptionWord("Tyox.", 2800L, 3400L, emoji = "👑")
            ),
            template = CaptionTemplateStyle.VIRAL_BEAST
        )

        val timeline2 = TimelineData(
            videoClips = listOf(clipP1),
            captions = listOf(cap1),
            totalDurationMs = 8000L
        )

        val project2 = ProjectEntity(
            id = UUID.randomUUID().toString(),
            name = "Podcast Auto Caption Demo",
            aspectRatio = AspectRatio.RATIO_9_16,
            resolution = ProjectResolution.RES_1080P,
            fps = ProjectFps.FPS_30,
            durationMs = 8000L,
            thumbnailUri = "sample_podcast_host",
            timelineJson = ProjectRepository.serializeTimeline(timeline2)
        )

        repository.saveProject(project1)
        repository.saveProject(project2)
    }

    fun createNewProject(
        name: String,
        aspectRatio: AspectRatio,
        resolution: ProjectResolution,
        fps: ProjectFps,
        initialMediaUris: List<String> = emptyList(),
        onCreated: (String) -> Unit
    ) {
        viewModelScope.launch {
            val projectId = UUID.randomUUID().toString()
            val initialClips = if (initialMediaUris.isNotEmpty()) {
                initialMediaUris.mapIndexed { idx, uri ->
                    VideoClip(
                        title = "Media ${idx + 1}",
                        uri = uri,
                        sourceDurationMs = 5000L,
                        trimInMs = 0L,
                        trimOutMs = 5000L
                    )
                }
            } else {
                listOf(
                    VideoClip(
                        title = "Sample Intro",
                        drawableResName = "sample_cyberpunk",
                        sourceDurationMs = 5000L,
                        trimInMs = 0L,
                        trimOutMs = 5000L
                    )
                )
            }

            val totalDur = initialClips.sumOf { it.durationMs }
            val timeline = TimelineData(
                videoClips = initialClips,
                totalDurationMs = totalDur
            )

            val project = ProjectEntity(
                id = projectId,
                name = name.ifBlank { "Tyox Project #${(100..999).random()}" },
                aspectRatio = aspectRatio,
                resolution = resolution,
                fps = fps,
                durationMs = totalDur,
                thumbnailUri = initialClips.firstOrNull()?.drawableResName ?: "sample_cyberpunk",
                timelineJson = ProjectRepository.serializeTimeline(timeline)
            )

            repository.saveProject(project)
            onCreated(projectId)
        }
    }

    fun deleteProject(id: String) {
        viewModelScope.launch {
            repository.deleteProject(id)
        }
    }

    fun renameProject(id: String, newName: String) {
        viewModelScope.launch {
            repository.renameProject(id, newName)
        }
    }

    fun duplicateProject(id: String) {
        viewModelScope.launch {
            repository.duplicateProject(id)
        }
    }
}
