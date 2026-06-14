package com.immersiads.app.data.repository

import com.immersiads.app.data.local.VocabularyDao
import com.immersiads.app.data.local.entities.VocabularyEntity
import com.immersiads.app.data.model.VocabularyItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class VocabularyRepository(private val dao: VocabularyDao) {

    fun getAllVocabulary(): Flow<List<VocabularyItem>> =
        dao.getAllVocabulary().map { entities -> entities.map { it.toModel() } }

    fun getVocabularyByLanguage(languageCode: String): Flow<List<VocabularyItem>> =
        dao.getVocabularyByLanguage(languageCode).map { entities -> entities.map { it.toModel() } }

    suspend fun getVocabularyCount(): Int = dao.getVocabularyCount()

    suspend fun saveVocabulary(item: VocabularyItem): Long =
        dao.insertVocabulary(item.toEntity())

    suspend fun deleteVocabulary(item: VocabularyItem) =
        dao.deleteVocabulary(item.toEntity())

    private fun VocabularyEntity.toModel(): VocabularyItem = VocabularyItem(
        id = id,
        word = word,
        translation = translation,
        languageCode = languageCode,
        context = context,
        adId = adId,
        savedAtMs = savedAtMs,
        reviewCount = reviewCount
    )

    private fun VocabularyItem.toEntity(): VocabularyEntity = VocabularyEntity(
        id = id,
        word = word,
        translation = translation,
        languageCode = languageCode,
        context = context,
        adId = adId,
        savedAtMs = savedAtMs,
        reviewCount = reviewCount
    )
}
