package com.example.engine

import com.example.data.model.*
import java.util.UUID

object SpeechCaptionRecognizer {

    val supportedLanguages = listOf(
        "English" to "en",
        "Spanish" to "es",
        "Hindi" to "hi",
        "Urdu" to "ur",
        "Arabic" to "ar",
        "French" to "fr",
        "German" to "de",
        "Portuguese" to "pt",
        "Italian" to "it",
        "Russian" to "ru",
        "Turkish" to "tr",
        "Chinese" to "zh",
        "Japanese" to "ja",
        "Korean" to "ko"
    )

    private val sampleSentences = listOf(
        "Welcome to Tyox Studio! Today we are creating a viral video with dynamic captions and smooth transitions.",
        "Step one is picking the right pacing, cutting out all silences, and adding high energy beat drops.",
        "Watch how the captions highlight automatically with karaoke sync and custom animations in real time.",
        "Export your video in crisp 4K 60FPS ready for Instagram Reels, YouTube Shorts, and TikTok!"
    )

    fun generateAutoCaptions(
        sourceLanguage: String = "English",
        targetDurationMs: Long = 12000L,
        template: CaptionTemplateStyle = CaptionTemplateStyle.VIRAL_BEAST
    ): List<CaptionItem> {
        val result = mutableListOf<CaptionItem>()
        var currentOffsetMs = 400L

        sampleSentences.forEach { sentence ->
            if (currentOffsetMs >= targetDurationMs) return@forEach
            val wordsRaw = sentence.split("\\s+".toRegex()).filter { it.isNotBlank() }
            val words = mutableListOf<CaptionWord>()
            var wordStart = 0L

            wordsRaw.forEach { rawWord ->
                val cleanWord = rawWord.trim()
                val wordDuration = (280L + (cleanWord.length * 35L)).coerceIn(240L, 650L)
                val emoji = detectEmojiForWord(cleanWord)

                words.add(
                    CaptionWord(
                        word = cleanWord,
                        startOffsetMs = wordStart,
                        endOffsetMs = wordStart + wordDuration,
                        emoji = emoji
                    )
                )
                wordStart += wordDuration + 40L
            }

            val captionDuration = wordStart + 200L
            result.add(
                CaptionItem(
                    id = UUID.randomUUID().toString(),
                    fullText = sentence,
                    startTimeMs = currentOffsetMs,
                    durationMs = captionDuration,
                    words = words,
                    template = template,
                    originalLanguage = sourceLanguage
                )
            )

            currentOffsetMs += captionDuration + 300L
        }

        return result
    }

    private fun detectEmojiForWord(word: String): String? {
        val lower = word.lowercase().replace(Regex("[^a-z]"), "")
        return when (lower) {
            "fire", "hot", "viral" -> "🔥"
            "money", "rich", "profit", "cash" -> "💰"
            "rocket", "fast", "growth", "launch" -> "🚀"
            "mind", "crazy", "insane", "wild" -> "🤯"
            "idea", "tip", "secret", "smart" -> "💡"
            "king", "boss", "best", "winner" -> "👑"
            "star", "shine", "glow", "magic" -> "✨"
            "video", "film", "cinema", "edit" -> "🎬"
            "mic", "podcast", "talk", "speak" -> "🎙️"
            "warning", "alert", "danger", "stop" -> "🚨"
            "love", "heart", "great", "awesome" -> "❤️"
            "energy", "power", "shock", "lightning" -> "⚡"
            else -> null
        }
    }

    fun translateCaptions(
        captions: List<CaptionItem>,
        targetLanguage: String
    ): List<CaptionItem> {
        val translationDictionary = mapOf(
            "Spanish" to mapOf(
                "Welcome" to "Bienvenidos", "to" to "a", "today" to "hoy", "creating" to "creando",
                "video" to "video", "dynamic" to "dinámico", "captions" to "subtítulos",
                "smooth" to "suaves", "transitions" to "transiciones", "step" to "paso",
                "one" to "uno", "right" to "correcto", "pacing" to "ritmo", "silences" to "silencios",
                "energy" to "energía", "beat" to "ritmo", "watch" to "mira", "highlight" to "resaltar",
                "export" to "exportar", "crisp" to "nítido"
            ),
            "Hindi" to mapOf(
                "Welcome" to "स्वागत है", "to" to "में", "today" to "आज", "video" to "वीडियो",
                "dynamic" to "डायनामिक", "captions" to "कैप्शन", "smooth" to "स्मूथ",
                "transitions" to "ट्रांज़िशन", "step" to "कदम", "one" to "एक",
                "energy" to "ऊर्जा", "watch" to "देखें", "export" to "एक्सपोर्ट करें"
            ),
            "French" to mapOf(
                "Welcome" to "Bienvenue", "to" to "sur", "today" to "aujourd'hui", "video" to "vidéo",
                "dynamic" to "dynamiques", "captions" to "sous-titres", "smooth" to "fluides",
                "transitions" to "transitions", "step" to "étape", "energy" to "énergie"
            ),
            "German" to mapOf(
                "Welcome" to "Willkommen", "to" to "bei", "today" to "heute", "video" to "Video",
                "dynamic" to "dynamischen", "captions" to "Untertiteln", "smooth" to "flüssigen",
                "transitions" to "Übergängen", "energy" to "Energie"
            ),
            "Arabic" to mapOf(
                "Welcome" to "مرحباً بكم", "to" to "في", "today" to "اليوم", "video" to "فيديو",
                "captions" to "تسميات توضيحية", "smooth" to "سلسة", "energy" to "طاقة"
            ),
            "Portuguese" to mapOf(
                "Welcome" to "Bem-vindo", "to" to "ao", "today" to "hoje", "video" to "vídeo",
                "dynamic" to "dinâmicas", "captions" to "legendas", "smooth" to "suaves"
            )
        )

        val dict = translationDictionary[targetLanguage] ?: emptyMap()

        return captions.map { item ->
            val translatedWords = item.words.map { w ->
                val clean = w.word.replace(Regex("[^a-zA-Z]"), "")
                val translated = dict[clean] ?: dict[clean.replaceFirstChar { it.uppercase() }] ?: w.word
                w.copy(word = translated)
            }
            val newFullText = translatedWords.joinToString(" ") { it.word }
            item.copy(
                fullText = newFullText,
                words = translatedWords,
                targetLanguage = targetLanguage,
                translation = newFullText
            )
        }
    }
}
