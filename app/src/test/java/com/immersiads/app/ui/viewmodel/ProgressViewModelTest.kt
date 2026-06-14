package com.immersiads.app.ui.viewmodel

import com.immersiads.app.data.model.VocabularyItem
import com.immersiads.app.data.repository.VocabularyRepository
import com.immersiads.app.domain.UserPreferences
import com.immersiads.app.ui.progress.ProgressViewModel
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
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ProgressViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var userPreferences: UserPreferences
    private lateinit var vocabularyRepository: VocabularyRepository
    private lateinit var viewModel: ProgressViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        userPreferences = mockk(relaxed = true)
        vocabularyRepository = mockk(relaxed = true)
        every { userPreferences.streakCount } returns flowOf(5)
        every { userPreferences.totalAdsWatched } returns flowOf(12)
        every { userPreferences.targetLanguage } returns flowOf("fr")
        every { vocabularyRepository.getAllVocabulary() } returns flowOf(
            listOf(
                VocabularyItem(id = 1, word = "bonjour", translation = "hello", languageCode = "fr"),
                VocabularyItem(id = 2, word = "merci", translation = "thank you", languageCode = "fr")
            )
        )
        viewModel = ProgressViewModel(userPreferences, vocabularyRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loads streak count from preferences`() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()
        assertEquals(5, viewModel.uiState.value.streakCount)
    }

    @Test
    fun `loads total ads watched from preferences`() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()
        assertEquals(12, viewModel.uiState.value.totalAdsWatched)
    }

    @Test
    fun `loads vocabulary count from repository`() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()
        assertEquals(2, viewModel.uiState.value.vocabularyCount)
    }

    @Test
    fun `loads target language from preferences`() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()
        assertEquals("fr", viewModel.uiState.value.targetLanguage)
    }

    @Test
    fun `isLoading is false after initialization`() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()
        assertFalse(viewModel.uiState.value.isLoading)
    }
}
