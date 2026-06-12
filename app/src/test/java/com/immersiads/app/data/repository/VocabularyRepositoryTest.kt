package com.immersiads.app.data.repository

import com.immersiads.app.data.local.VocabularyDao
import com.immersiads.app.data.local.entities.VocabularyEntity
import com.immersiads.app.data.model.VocabularyItem
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class VocabularyRepositoryTest {

    private lateinit var dao: VocabularyDao
    private lateinit var repository: VocabularyRepository

    @Before
    fun setUp() {
        dao = mockk()
        repository = VocabularyRepository(dao)
    }

    @Test
    fun `getAllVocabulary maps entities to models correctly`() = runTest {
        val entity = VocabularyEntity(
            id = 1L,
            word = "hola",
            translation = "hello",
            languageCode = "es",
            context = "¡Hola mundo!",
            adId = "ad_001"
        )
        every { dao.getAllVocabulary() } returns flowOf(listOf(entity))

        val result = repository.getAllVocabulary().first()

        assertEquals(1, result.size)
        assertEquals("hola", result[0].word)
        assertEquals("hello", result[0].translation)
        assertEquals("es", result[0].languageCode)
    }

    @Test
    fun `getVocabularyByLanguage filters by language`() = runTest {
        val entity = VocabularyEntity(
            id = 1L,
            word = "bonjour",
            translation = "hello",
            languageCode = "fr",
            context = "Bonjour le monde!",
            adId = "ad_002"
        )
        every { dao.getVocabularyByLanguage("fr") } returns flowOf(listOf(entity))

        val result = repository.getVocabularyByLanguage("fr").first()

        assertEquals(1, result.size)
        assertEquals("fr", result[0].languageCode)
    }

    @Test
    fun `saveVocabulary inserts item correctly`() = runTest {
        val entitySlot = slot<VocabularyEntity>()
        coEvery { dao.insertVocabulary(capture(entitySlot)) } returns 1L

        val item = VocabularyItem(
            word = "gracias",
            translation = "thank you",
            languageCode = "es",
            context = "Muchas gracias",
            adId = "ad_001"
        )
        val id = repository.saveVocabulary(item)

        assertEquals(1L, id)
        assertEquals("gracias", entitySlot.captured.word)
        assertEquals("thank you", entitySlot.captured.translation)
    }

    @Test
    fun `deleteVocabulary calls dao delete`() = runTest {
        val entitySlot = slot<VocabularyEntity>()
        coEvery { dao.deleteVocabulary(capture(entitySlot)) } returns Unit

        val item = VocabularyItem(
            id = 5L,
            word = "adios",
            translation = "goodbye",
            languageCode = "es"
        )
        repository.deleteVocabulary(item)

        coVerify { dao.deleteVocabulary(any()) }
        assertEquals("adios", entitySlot.captured.word)
    }

    @Test
    fun `getVocabularyCount returns count from dao`() = runTest {
        coEvery { dao.getVocabularyCount() } returns 42

        val count = repository.getVocabularyCount()

        assertEquals(42, count)
    }
}
