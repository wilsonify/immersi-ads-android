package com.immersiads.app.data.model

data class Advertisement(
    val id: String,
    val title: String,
    val brand: String,
    val languageCode: String,
    val videoUrl: String,
    val thumbnailUrl: String,
    val durationMs: Long,
    val subtitles: List<Subtitle> = emptyList(),
    val tags: List<String> = emptyList(),
    val difficulty: DifficultyLevel = DifficultyLevel.BEGINNER
)

enum class DifficultyLevel {
    BEGINNER, INTERMEDIATE, ADVANCED
}
