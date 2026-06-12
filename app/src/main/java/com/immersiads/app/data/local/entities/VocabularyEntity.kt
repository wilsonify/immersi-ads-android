package com.immersiads.app.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "vocabulary")
data class VocabularyEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val word: String,
    val translation: String,
    val languageCode: String,
    val context: String = "",
    val adId: String = "",
    val savedAtMs: Long = System.currentTimeMillis(),
    val reviewCount: Int = 0
)
