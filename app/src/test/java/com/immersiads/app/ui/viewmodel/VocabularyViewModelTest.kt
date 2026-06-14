package com.immersiads.app.ui.viewmodel

import com.immersiads.app.data.model.VocabularyItem
import com.immersiads.app.data.repository.VocabularyRepository
import com.immersiads.app.domain.UserPreferences
import com.immersiads.app.ui.vocabulary.VocabularyViewModel
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class VocabularyViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var vocabularyRepository: VocabularyRepository
    private lateinit var userPreferences: UserPreferences
    private lateinit var viewModel: VocabularyViewModel

    private val item1 = VocabularyItem(
        id = 1, word = "hola", translation = "hello",
        languageCode = "es", context = "Hola mundo", adId = "ad1"
    )
    private val item2 = VocabularyItem(
        id = 2, word = "adiós", translation = "goodbye",
        languageCode = "es", context = "Adiós amigo", adId = "ad1"
    )
    private val item3 = VocabularyItem(
        id = 3, word = "bonjour", translation = "hello",
        languageCode = "fr", context = "Bonjour le monde", adId = "ad2"
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        vocabularyRepository = mockk(relaxed = true)
        userPreferences = mockk(relaxed = true)
        every { vocabularyRepository.getAllVocabulary() } returns flowOf(listOf(item1, item2, item3))
        every { userPreferences.targetLanguage } returns flowOf("es")
        viewModel = VocabularyViewModel(vocabularyRepository, userPreferences)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loads all vocabulary items`() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()
        assertEquals(3, viewModel.uiState.value.vocabularyItems.size)
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `search query filters by word`() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.updateSearchQuery("hola")
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(1, viewModel.uiState.value.vocabularyItems.size)
        assertEquals("hola", viewModel.uiState.value.vocabularyItems[0].word)
    }

    @Test
    fun `search query filters by translation`() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.updateSearchQuery("goodbye")
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(1, viewModel.uiState.value.vocabularyItems.size)
        assertEquals("adiós", viewModel.uiState.value.vocabularyItems[0].word)
    }

    @Test
    fun `empty search shows all items`() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.updateSearchQuery("hola")
        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.updateSearchQuery("")
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(3, viewModel.uiState.value.vocabularyItems.size)
    }

    @Test
    fun `search is case insensitive`() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.updateSearchQuery("HOLA")
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(1, viewModel.uiState.value.vocabularyItems.size)
    }

    @Test
    fun `deleteItem calls repository delete`() = runTest {
        viewModel.deleteItem(item1)
        testDispatcher.scheduler.advanceUntilIdle()
        coVerify { vocabularyRepository.deleteVocabulary(item1) }
    }
}
