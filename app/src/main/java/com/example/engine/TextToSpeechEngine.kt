package com.example.engine

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import com.example.data.model.AudioClip
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File
import java.util.Locale
import java.util.UUID

data class TtsVoiceOption(
    val id: String,
    val name: String,
    val locale: Locale,
    val gender: String,
    val description: String
)

class TextToSpeechManager(private val context: Context) : TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = null
    private val _isReady = MutableStateFlow(false)
    val isReady: StateFlow<Boolean> = _isReady.asStateFlow()

    private val _isSpeaking = MutableStateFlow(false)
    val isSpeaking: StateFlow<Boolean> = _isSpeaking.asStateFlow()

    val availableVoices: List<TtsVoiceOption> = listOf(
        TtsVoiceOption("en_us_male_pro", "Marcus (Studio Pro Male)", Locale.US, "Male", "Deep cinematic narration voice"),
        TtsVoiceOption("en_us_female_pro", "Sophia (Crystal Female)", Locale.US, "Female", "Clear, energetic creator voice"),
        TtsVoiceOption("en_uk_male", "Arthur (British Narrator)", Locale.UK, "Male", "Sophisticated documentary voice"),
        TtsVoiceOption("en_uk_female", "Charlotte (British Host)", Locale.UK, "Female", "Polished podcast host voice"),
        TtsVoiceOption("es_male", "Mateo (Spanish Creator)", Locale("es", "ES"), "Male", "Dynamic warm Spanish voice"),
        TtsVoiceOption("es_female", "Valentina (Spanish Host)", Locale("es", "ES"), "Female", "Clear Latin American female voice"),
        TtsVoiceOption("fr_female", "Camille (French Chic)", Locale.FRANCE, "Female", "Smooth melodic Parisian voice"),
        TtsVoiceOption("de_male", "Lukas (German Pro)", Locale.GERMANY, "Male", "Structured punchy German male voice"),
        TtsVoiceOption("hi_female", "Aanya (Hindi Dynamic)", Locale("hi", "IN"), "Female", "Vibrant modern Hindi creator voice"),
        TtsVoiceOption("ja_female", "Sakura (Japanese Anime)", Locale.JAPAN, "Female", "Bright Japanese voice"),
        TtsVoiceOption("it_male", "Marco (Italian Warm)", Locale.ITALY, "Male", "Expressive Italian narrator")
    )

    init {
        tts = TextToSpeech(context.applicationContext, this)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts?.language = Locale.US
            tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {
                    _isSpeaking.value = true
                }

                override fun onDone(utteranceId: String?) {
                    _isSpeaking.value = false
                }

                @Deprecated("Deprecated in Java")
                override fun onError(utteranceId: String?) {
                    _isSpeaking.value = false
                }
            })
            _isReady.value = true
        } else {
            Log.e("TyoxTTS", "TextToSpeech initialization failed")
        }
    }

    fun speak(text: String, voiceOption: TtsVoiceOption, pitch: Float = 1.0f, speed: Float = 1.0f) {
        if (!_isReady.value || text.isBlank()) return
        tts?.let { engine ->
            engine.language = voiceOption.locale
            engine.setPitch(pitch)
            engine.setSpeechRate(speed)
            val utteranceId = UUID.randomUUID().toString()
            engine.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId)
        }
    }

    fun stop() {
        tts?.stop()
        _isSpeaking.value = false
    }

    fun createTimelineAudioClip(
        text: String,
        voiceOption: TtsVoiceOption,
        startTimeMs: Long = 0L,
        speed: Float = 1.0f
    ): AudioClip {
        // Calculate estimated audio duration based on word count & speech speed
        val wordCount = text.split("\\s+".toRegex()).size.coerceAtLeast(1)
        // Average speaking rate: ~150 words per minute = 2.5 words/sec -> 400ms per word
        val estimatedMs = ((wordCount * 400L) / speed).toLong().coerceIn(1000L, 60000L)

        return AudioClip(
            id = UUID.randomUUID().toString(),
            title = "TTS: ${text.take(18)}...",
            uri = "tts://${voiceOption.id}/${UUID.randomUUID()}",
            startTimeMs = startTimeMs,
            durationMs = estimatedMs,
            trimInMs = 0L,
            trimOutMs = estimatedMs,
            volume = 1.0f,
            speed = speed,
            isVoiceover = true,
            category = "TTS Voice",
            waveformPoints = generateSimulatedWaveform(wordCount * 8)
        )
    }

    private fun generateSimulatedWaveform(pointsCount: Int): List<Float> {
        val count = pointsCount.coerceIn(20, 100)
        return (0 until count).map { index ->
            val factor = kotlin.math.sin(index.toDouble() * 0.3).toFloat()
            (0.2f + 0.7f * kotlin.math.abs(factor)).coerceIn(0.1f, 1.0f)
        }
    }

    fun release() {
        tts?.stop()
        tts?.shutdown()
        tts = null
    }
}
