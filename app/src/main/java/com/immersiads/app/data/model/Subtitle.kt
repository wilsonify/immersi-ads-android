package com.immersiads.app.data.model

data class Subtitle(
    val startTimeMs: Long,
    val endTimeMs: Long,
    val text: String,
    val translationText: String = ""
)
