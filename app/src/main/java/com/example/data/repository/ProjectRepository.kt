package com.example.data.repository

import com.example.data.local.ProjectDao
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow
import org.json.JSONArray
import org.json.JSONObject

class ProjectRepository(private val projectDao: ProjectDao) {

    val allProjects: Flow<List<ProjectEntity>> = projectDao.getAllProjects()
    val draftProjects: Flow<List<ProjectEntity>> = projectDao.getDraftProjects()

    suspend fun getProjectById(id: String): ProjectEntity? = projectDao.getProjectById(id)

    fun getProjectByIdFlow(id: String): Flow<ProjectEntity?> = projectDao.getProjectByIdFlow(id)

    suspend fun saveProject(project: ProjectEntity) = projectDao.insertOrUpdateProject(project)

    suspend fun deleteProject(id: String) = projectDao.deleteProjectById(id)

    suspend fun renameProject(id: String, newName: String) = projectDao.renameProject(id, newName)

    suspend fun duplicateProject(id: String): ProjectEntity? {
        val original = projectDao.getProjectById(id) ?: return null
        val copy = original.copy(
            id = java.util.UUID.randomUUID().toString(),
            name = "${original.name} (Copy)",
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
        projectDao.insertOrUpdateProject(copy)
        return copy
    }

    companion object {
        fun serializeTimeline(timeline: TimelineData): String {
            val json = JSONObject()
            
            // Video clips
            val videosArr = JSONArray()
            timeline.videoClips.forEach { clip ->
                val vObj = JSONObject().apply {
                    put("id", clip.id)
                    put("title", clip.title)
                    put("uri", clip.uri)
                    put("drawableResName", clip.drawableResName ?: "")
                    put("sourceDurationMs", clip.sourceDurationMs)
                    put("trimInMs", clip.trimInMs)
                    put("trimOutMs", clip.trimOutMs)
                    put("speed", clip.speed.toDouble())
                    put("isReverse", clip.isReverse)
                    put("isMuted", clip.isMuted)
                    put("volume", clip.volume.toDouble())
                    put("fadeInMs", clip.fadeInMs)
                    put("fadeOutMs", clip.fadeOutMs)
                    put("rotation", clip.rotation.toDouble())
                    put("flipHorizontal", clip.flipHorizontal)
                    put("flipVertical", clip.flipVertical)
                    put("scale", clip.scale.toDouble())
                    put("positionX", clip.positionX.toDouble())
                    put("positionY", clip.positionY.toDouble())
                    put("opacity", clip.opacity.toDouble())
                    put("filterName", clip.filterName)
                    put("filterIntensity", clip.filterIntensity.toDouble())
                    put("transitionIn", clip.transitionIn.name)
                    put("transitionDurationMs", clip.transitionDurationMs)
                    put("bgType", clip.bgType)
                    put("bgColorHex", clip.bgColorHex)
                    
                    // Adjustments
                    val adjObj = JSONObject().apply {
                        put("brightness", clip.adjustments.brightness.toDouble())
                        put("contrast", clip.adjustments.contrast.toDouble())
                        put("saturation", clip.adjustments.saturation.toDouble())
                        put("exposure", clip.adjustments.exposure.toDouble())
                        put("highlights", clip.adjustments.highlights.toDouble())
                        put("shadows", clip.adjustments.shadows.toDouble())
                        put("temperature", clip.adjustments.temperature.toDouble())
                        put("tint", clip.adjustments.tint.toDouble())
                        put("sharpen", clip.adjustments.sharpen.toDouble())
                        put("fade", clip.adjustments.fade.toDouble())
                        put("vignette", clip.adjustments.vignette.toDouble())
                        put("grain", clip.adjustments.grain.toDouble())
                        put("hue", clip.adjustments.hue.toDouble())
                    }
                    put("adjustments", adjObj)
                    
                    // ChromaKey
                    val chromaObj = JSONObject().apply {
                        put("enabled", clip.chromaKey.enabled)
                        put("targetColorHex", clip.chromaKey.targetColorHex)
                        put("strength", clip.chromaKey.strength.toDouble())
                        put("shadow", clip.chromaKey.shadow.toDouble())
                        put("spillReduction", clip.chromaKey.spillReduction.toDouble())
                        put("edgeFeather", clip.chromaKey.edgeFeather.toDouble())
                    }
                    put("chromaKey", chromaObj)
                    
                    // Mask
                    val maskObj = JSONObject().apply {
                        put("type", clip.mask.type.name)
                        put("feather", clip.mask.feather.toDouble())
                        put("size", clip.mask.size.toDouble())
                        put("rotation", clip.mask.rotation.toDouble())
                        put("inverted", clip.mask.inverted)
                    }
                    put("mask", maskObj)
                }
                videosArr.put(vObj)
            }
            json.put("videoClips", videosArr)

            // Audio clips
            val audioArr = JSONArray()
            timeline.audioClips.forEach { audio ->
                val aObj = JSONObject().apply {
                    put("id", audio.id)
                    put("title", audio.title)
                    put("uri", audio.uri)
                    put("presetResName", audio.presetResName ?: "")
                    put("startTimeMs", audio.startTimeMs)
                    put("durationMs", audio.durationMs)
                    put("trimInMs", audio.trimInMs)
                    put("trimOutMs", audio.trimOutMs)
                    put("volume", audio.volume.toDouble())
                    put("speed", audio.speed.toDouble())
                    put("fadeInMs", audio.fadeInMs)
                    put("fadeOutMs", audio.fadeOutMs)
                    put("isVoiceover", audio.isVoiceover)
                    put("isSfx", audio.isSfx)
                    put("category", audio.category)
                }
                audioArr.put(aObj)
            }
            json.put("audioClips", audioArr)

            // Text items
            val textArr = JSONArray()
            timeline.textItems.forEach { t ->
                val tObj = JSONObject().apply {
                    put("id", t.id)
                    put("text", t.text)
                    put("startTimeMs", t.startTimeMs)
                    put("durationMs", t.durationMs)
                    put("fontName", t.fontName)
                    put("fontSizeSp", t.fontSizeSp.toDouble())
                    put("isBold", t.isBold)
                    put("isItalic", t.isItalic)
                    put("alignment", t.alignment)
                    put("textColorHex", t.textColorHex)
                    put("strokeColorHex", t.strokeColorHex ?: "")
                    put("strokeWidthDp", t.strokeWidthDp.toDouble())
                    put("shadowColorHex", t.shadowColorHex ?: "")
                    put("glowColorHex", t.glowColorHex ?: "")
                    put("backgroundColorHex", t.backgroundColorHex ?: "")
                    put("opacity", t.opacity.toDouble())
                    put("rotation", t.rotation.toDouble())
                    put("scale", t.scale.toDouble())
                    put("positionX", t.positionX.toDouble())
                    put("positionY", t.positionY.toDouble())
                    put("animation", t.animation.name)
                }
                textArr.put(tObj)
            }
            json.put("textItems", textArr)

            // Captions
            val capArr = JSONArray()
            timeline.captions.forEach { cap ->
                val cObj = JSONObject().apply {
                    put("id", cap.id)
                    put("fullText", cap.fullText)
                    put("startTimeMs", cap.startTimeMs)
                    put("durationMs", cap.durationMs)
                    put("template", cap.template.name)
                    put("translation", cap.translation ?: "")
                    put("originalLanguage", cap.originalLanguage)
                    put("targetLanguage", cap.targetLanguage ?: "")
                    put("positionY", cap.positionY.toDouble())
                    put("fontSizeSp", cap.fontSizeSp.toDouble())

                    val wordsArr = JSONArray()
                    cap.words.forEach { w ->
                        val wObj = JSONObject().apply {
                            put("word", w.word)
                            put("startOffsetMs", w.startOffsetMs)
                            put("endOffsetMs", w.endOffsetMs)
                            put("emoji", w.emoji ?: "")
                        }
                        wordsArr.put(wObj)
                    }
                    put("words", wordsArr)
                }
                capArr.put(cObj)
            }
            json.put("captions", capArr)

            // Effects
            val effArr = JSONArray()
            timeline.effects.forEach { eff ->
                val eObj = JSONObject().apply {
                    put("id", eff.id)
                    put("type", eff.type.name)
                    put("startTimeMs", eff.startTimeMs)
                    put("durationMs", eff.durationMs)
                    put("intensity", eff.intensity.toDouble())
                    put("speed", eff.speed.toDouble())
                }
                effArr.put(eObj)
            }
            json.put("effects", effArr)

            // Stickers
            val stArr = JSONArray()
            timeline.stickers.forEach { s ->
                val sObj = JSONObject().apply {
                    put("id", s.id)
                    put("content", s.content)
                    put("type", s.type.name)
                    put("startTimeMs", s.startTimeMs)
                    put("durationMs", s.durationMs)
                    put("positionX", s.positionX.toDouble())
                    put("positionY", s.positionY.toDouble())
                    put("scale", s.scale.toDouble())
                    put("rotation", s.rotation.toDouble())
                    put("opacity", s.opacity.toDouble())
                    put("animation", s.animation)
                }
                stArr.put(sObj)
            }
            json.put("stickers", stArr)
            json.put("totalDurationMs", timeline.totalDurationMs)

            return json.toString()
        }

        fun deserializeTimeline(jsonStr: String): TimelineData {
            if (jsonStr.isBlank()) return TimelineData()
            return try {
                val json = JSONObject(jsonStr)
                
                val videoList = mutableListOf<VideoClip>()
                val vArr = json.optJSONArray("videoClips")
                if (vArr != null) {
                    for (i in 0 until vArr.length()) {
                        val obj = vArr.getJSONObject(i)
                        val adjObj = obj.optJSONObject("adjustments")
                        val adj = if (adjObj != null) {
                            VideoAdjustments(
                                brightness = adjObj.optDouble("brightness", 0.0).toFloat(),
                                contrast = adjObj.optDouble("contrast", 1.0).toFloat(),
                                saturation = adjObj.optDouble("saturation", 1.0).toFloat(),
                                exposure = adjObj.optDouble("exposure", 0.0).toFloat(),
                                highlights = adjObj.optDouble("highlights", 0.0).toFloat(),
                                shadows = adjObj.optDouble("shadows", 0.0).toFloat(),
                                temperature = adjObj.optDouble("temperature", 0.0).toFloat(),
                                tint = adjObj.optDouble("tint", 0.0).toFloat(),
                                sharpen = adjObj.optDouble("sharpen", 0.0).toFloat(),
                                fade = adjObj.optDouble("fade", 0.0).toFloat(),
                                vignette = adjObj.optDouble("vignette", 0.0).toFloat(),
                                grain = adjObj.optDouble("grain", 0.0).toFloat(),
                                hue = adjObj.optDouble("hue", 0.0).toFloat()
                            )
                        } else VideoAdjustments()

                        val chromaObj = obj.optJSONObject("chromaKey")
                        val chroma = if (chromaObj != null) {
                            ChromaKeyConfig(
                                enabled = chromaObj.optBoolean("enabled", false),
                                targetColorHex = chromaObj.optString("targetColorHex", "#00FF00"),
                                strength = chromaObj.optDouble("strength", 0.4).toFloat(),
                                shadow = chromaObj.optDouble("shadow", 0.1).toFloat(),
                                spillReduction = chromaObj.optDouble("spillReduction", 0.2).toFloat(),
                                edgeFeather = chromaObj.optDouble("edgeFeather", 0.05).toFloat()
                            )
                        } else ChromaKeyConfig()

                        val maskObj = obj.optJSONObject("mask")
                        val mask = if (maskObj != null) {
                            MaskConfig(
                                type = try { MaskType.valueOf(maskObj.optString("type", "NONE")) } catch (e: Exception) { MaskType.NONE },
                                feather = maskObj.optDouble("feather", 0.0).toFloat(),
                                size = maskObj.optDouble("size", 0.5).toFloat(),
                                rotation = maskObj.optDouble("rotation", 0.0).toFloat(),
                                inverted = maskObj.optBoolean("inverted", false)
                            )
                        } else MaskConfig()

                        val clip = VideoClip(
                            id = obj.optString("id", java.util.UUID.randomUUID().toString()),
                            title = obj.optString("title", "Clip"),
                            uri = obj.optString("uri", ""),
                            drawableResName = obj.optString("drawableResName").takeIf { it.isNotBlank() },
                            sourceDurationMs = obj.optLong("sourceDurationMs", 5000L),
                            trimInMs = obj.optLong("trimInMs", 0L),
                            trimOutMs = obj.optLong("trimOutMs", 5000L),
                            speed = obj.optDouble("speed", 1.0).toFloat(),
                            isReverse = obj.optBoolean("isReverse", false),
                            isMuted = obj.optBoolean("isMuted", false),
                            volume = obj.optDouble("volume", 1.0).toFloat(),
                            fadeInMs = obj.optLong("fadeInMs", 0L),
                            fadeOutMs = obj.optLong("fadeOutMs", 0L),
                            rotation = obj.optDouble("rotation", 0.0).toFloat(),
                            flipHorizontal = obj.optBoolean("flipHorizontal", false),
                            flipVertical = obj.optBoolean("flipVertical", false),
                            scale = obj.optDouble("scale", 1.0).toFloat(),
                            positionX = obj.optDouble("positionX", 0.0).toFloat(),
                            positionY = obj.optDouble("positionY", 0.0).toFloat(),
                            opacity = obj.optDouble("opacity", 1.0).toFloat(),
                            filterName = obj.optString("filterName", "Normal"),
                            filterIntensity = obj.optDouble("filterIntensity", 1.0).toFloat(),
                            adjustments = adj,
                            chromaKey = chroma,
                            mask = mask,
                            transitionIn = try { TransitionType.valueOf(obj.optString("transitionIn", "NONE")) } catch (e: Exception) { TransitionType.NONE },
                            transitionDurationMs = obj.optLong("transitionDurationMs", 500L),
                            bgType = obj.optString("bgType", "NONE"),
                            bgColorHex = obj.optString("bgColorHex", "#000000")
                        )
                        videoList.add(clip)
                    }
                }

                val audioList = mutableListOf<AudioClip>()
                val aArr = json.optJSONArray("audioClips")
                if (aArr != null) {
                    for (i in 0 until aArr.length()) {
                        val obj = aArr.getJSONObject(i)
                        val audio = AudioClip(
                            id = obj.optString("id", java.util.UUID.randomUUID().toString()),
                            title = obj.optString("title", "Audio"),
                            uri = obj.optString("uri", ""),
                            presetResName = obj.optString("presetResName").takeIf { it.isNotBlank() },
                            startTimeMs = obj.optLong("startTimeMs", 0L),
                            durationMs = obj.optLong("durationMs", 5000L),
                            trimInMs = obj.optLong("trimInMs", 0L),
                            trimOutMs = obj.optLong("trimOutMs", 5000L),
                            volume = obj.optDouble("volume", 1.0).toFloat(),
                            speed = obj.optDouble("speed", 1.0).toFloat(),
                            fadeInMs = obj.optLong("fadeInMs", 0L),
                            fadeOutMs = obj.optLong("fadeOutMs", 0L),
                            isVoiceover = obj.optBoolean("isVoiceover", false),
                            isSfx = obj.optBoolean("isSfx", false),
                            category = obj.optString("category", "Music")
                        )
                        audioList.add(audio)
                    }
                }

                val textList = mutableListOf<TextItem>()
                val tArr = json.optJSONArray("textItems")
                if (tArr != null) {
                    for (i in 0 until tArr.length()) {
                        val obj = tArr.getJSONObject(i)
                        val item = TextItem(
                            id = obj.optString("id", java.util.UUID.randomUUID().toString()),
                            text = obj.optString("text", "Text"),
                            startTimeMs = obj.optLong("startTimeMs", 0L),
                            durationMs = obj.optLong("durationMs", 3000L),
                            fontName = obj.optString("fontName", "Cinematic Sans"),
                            fontSizeSp = obj.optDouble("fontSizeSp", 28.0).toFloat(),
                            isBold = obj.optBoolean("isBold", true),
                            isItalic = obj.optBoolean("isItalic", false),
                            alignment = obj.optString("alignment", "CENTER"),
                            textColorHex = obj.optString("textColorHex", "#FFFFFF"),
                            strokeColorHex = obj.optString("strokeColorHex").takeIf { it.isNotBlank() },
                            strokeWidthDp = obj.optDouble("strokeWidthDp", 3.0).toFloat(),
                            shadowColorHex = obj.optString("shadowColorHex").takeIf { it.isNotBlank() },
                            glowColorHex = obj.optString("glowColorHex").takeIf { it.isNotBlank() },
                            backgroundColorHex = obj.optString("backgroundColorHex").takeIf { it.isNotBlank() },
                            opacity = obj.optDouble("opacity", 1.0).toFloat(),
                            rotation = obj.optDouble("rotation", 0.0).toFloat(),
                            scale = obj.optDouble("scale", 1.0).toFloat(),
                            positionX = obj.optDouble("positionX", 0.0).toFloat(),
                            positionY = obj.optDouble("positionY", 0.0).toFloat(),
                            animation = try { TextAnimationType.valueOf(obj.optString("animation", "NONE")) } catch (e: Exception) { TextAnimationType.NONE }
                        )
                        textList.add(item)
                    }
                }

                val capList = mutableListOf<CaptionItem>()
                val cArr = json.optJSONArray("captions")
                if (cArr != null) {
                    for (i in 0 until cArr.length()) {
                        val obj = cArr.getJSONObject(i)
                        val words = mutableListOf<CaptionWord>()
                        val wArr = obj.optJSONArray("words")
                        if (wArr != null) {
                            for (wIdx in 0 until wArr.length()) {
                                val wObj = wArr.getJSONObject(wIdx)
                                words.add(
                                    CaptionWord(
                                        word = wObj.optString("word", ""),
                                        startOffsetMs = wObj.optLong("startOffsetMs", 0L),
                                        endOffsetMs = wObj.optLong("endOffsetMs", 300L),
                                        emoji = wObj.optString("emoji").takeIf { it.isNotBlank() }
                                    )
                                )
                            }
                        }

                        val item = CaptionItem(
                            id = obj.optString("id", java.util.UUID.randomUUID().toString()),
                            fullText = obj.optString("fullText", ""),
                            startTimeMs = obj.optLong("startTimeMs", 0L),
                            durationMs = obj.optLong("durationMs", 2500L),
                            words = words,
                            template = try { CaptionTemplateStyle.valueOf(obj.optString("template", "VIRAL_BEAST")) } catch (e: Exception) { CaptionTemplateStyle.VIRAL_BEAST },
                            translation = obj.optString("translation").takeIf { it.isNotBlank() },
                            originalLanguage = obj.optString("originalLanguage", "English"),
                            targetLanguage = obj.optString("targetLanguage").takeIf { it.isNotBlank() },
                            positionY = obj.optDouble("positionY", 0.72).toFloat(),
                            fontSizeSp = obj.optDouble("fontSizeSp", 26.0).toFloat()
                        )
                        capList.add(item)
                    }
                }

                val effList = mutableListOf<EffectItem>()
                val eArr = json.optJSONArray("effects")
                if (eArr != null) {
                    for (i in 0 until eArr.length()) {
                        val obj = eArr.getJSONObject(i)
                        val item = EffectItem(
                            id = obj.optString("id", java.util.UUID.randomUUID().toString()),
                            type = try { EffectType.valueOf(obj.optString("type", "GLITCH")) } catch (e: Exception) { EffectType.GLITCH },
                            startTimeMs = obj.optLong("startTimeMs", 0L),
                            durationMs = obj.optLong("durationMs", 3000L),
                            intensity = obj.optDouble("intensity", 0.75).toFloat(),
                            speed = obj.optDouble("speed", 1.0).toFloat()
                        )
                        effList.add(item)
                    }
                }

                val stList = mutableListOf<StickerItem>()
                val sArr = json.optJSONArray("stickers")
                if (sArr != null) {
                    for (i in 0 until sArr.length()) {
                        val obj = sArr.getJSONObject(i)
                        val item = StickerItem(
                            id = obj.optString("id", java.util.UUID.randomUUID().toString()),
                            content = obj.optString("content", "🔥"),
                            type = try { StickerType.valueOf(obj.optString("type", "EMOJI")) } catch (e: Exception) { StickerType.EMOJI },
                            startTimeMs = obj.optLong("startTimeMs", 0L),
                            durationMs = obj.optLong("durationMs", 3000L),
                            positionX = obj.optDouble("positionX", 0.0).toFloat(),
                            positionY = obj.optDouble("positionY", 0.0).toFloat(),
                            scale = obj.optDouble("scale", 1.0).toFloat(),
                            rotation = obj.optDouble("rotation", 0.0).toFloat(),
                            opacity = obj.optDouble("opacity", 1.0).toFloat(),
                            animation = obj.optString("animation", "PULSE")
                        )
                        stList.add(item)
                    }
                }

                TimelineData(
                    videoClips = videoList,
                    audioClips = audioList,
                    textItems = textList,
                    captions = capList,
                    effects = effList,
                    stickers = stList,
                    totalDurationMs = json.optLong("totalDurationMs", 0L)
                )
            } catch (e: Exception) {
                TimelineData()
            }
        }
    }
}
