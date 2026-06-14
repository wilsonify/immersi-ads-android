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
    val difficulty: DifficultyLevel = DifficultyLevel.BEGINNER,
    val license: String = "CC-BY 3.0",
    val attribution: String = "Blender Foundation",
    val attributionUrl: String = "https://peach.blender.org/"
)

enum class DifficultyLevel {
    BEGINNER, INTERMEDIATE, ADVANCED
}
