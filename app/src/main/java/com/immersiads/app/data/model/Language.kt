package com.immersiads.app.data.model

data class Language(
    val code: String,
    val name: String,
    val nativeName: String,
    val flagEmoji: String
)

val SUPPORTED_LANGUAGES = listOf(
    Language("en", "English", "English", "🇺🇸"),
    Language("es", "Spanish", "Español", "🇪🇸"),
    Language("fr", "French", "Français", "🇫🇷"),
    Language("de", "German", "Deutsch", "🇩🇪"),
    Language("it", "Italian", "Italiano", "🇮🇹"),
    Language("pt", "Portuguese", "Português", "🇵🇹"),
    Language("ja", "Japanese", "日本語", "🇯🇵"),
    Language("ko", "Korean", "한국어", "🇰🇷"),
    Language("zh", "Chinese", "中文", "🇨🇳"),
    Language("ar", "Arabic", "العربية", "🇸🇦"),
    Language("ru", "Russian", "Русский", "🇷🇺"),
    Language("hi", "Hindi", "हिन्दी", "🇮🇳")
)
