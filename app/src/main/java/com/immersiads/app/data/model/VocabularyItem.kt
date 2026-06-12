package com.immersiads.app.data.model

data class VocabularyItem(
    val id: Long = 0,
    val word: String,
    val translation: String,
    val languageCode: String,
    val context: String = "",
    val adId: String = "",
    val savedAtMs: Long = System.currentTimeMillis(),
    val reviewCount: Int = 0
)
