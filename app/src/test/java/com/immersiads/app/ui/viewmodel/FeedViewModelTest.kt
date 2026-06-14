package com.immersiads.app.ui.viewmodel

import com.immersiads.app.data.model.Advertisement
import com.immersiads.app.data.model.DifficultyLevel
import com.immersiads.app.data.model.Subtitle
import com.immersiads.app.data.repository.AdRepository
import com.immersiads.app.domain.UserPreferences
import com.immersiads.app.ui.feed.FeedViewModel
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
class FeedViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var adRepository: AdRepository
    private lateinit var userPreferences: UserPreferences
    private lateinit var viewModel: FeedViewModel

    private val spanishAd = Advertisement(
        id = "ad_es",
        title = "Spanish Ad",
        brand = "BrandEs",
        languageCode = "es",
        videoUrl = "https://example.com/video.mp4",
        thumbnailUrl = "",
        durationMs = 10000,
        subtitles = listOf(Subtitle(0, 1000, "Hola", "Hello")),
        difficulty = DifficultyLevel.BEGINNER
    )

    private val frenchAd = Advertisement(
        id = "ad_fr",
        title = "French Ad",
        brand = "BrandFr",
        languageCode = "fr",
        videoUrl = "https://example.com/video2.mp4",
        thumbnailUrl = "",
        durationMs = 10000,
        subtitles = listOf(Subtitle(0, 1000, "Bonjour", "Hello")),
        difficulty = DifficultyLevel.BEGINNER
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        adRepository = mockk()
        userPreferences = mockk(relaxed = true)
        every { adRepository.getAdvertisements() } returns flowOf(listOf(spanishAd, frenchAd))
        every { userPreferences.targetLanguage } returns flowOf("es")
        every { userPreferences.streakCount } returns flowOf(3)
        viewModel = FeedViewModel(adRepository, userPreferences)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state filters by target language`() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()
        val state = viewModel.uiState.value
        assertEquals(1, state.advertisements.size)
        assertEquals("es", state.advertisements[0].languageCode)
        assertTrue(state.filterByLanguage)
    }

    @Test
    fun `toggleLanguageFilter shows all ads when disabled`() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.toggleLanguageFilter()
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(2, state.advertisements.size)
        assertFalse(state.filterByLanguage)
    }

    @Test
    fun `toggleLanguageFilter re-enables filter`() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.toggleLanguageFilter()
        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.toggleLanguageFilter()
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(1, state.advertisements.size)
        assertTrue(state.filterByLanguage)
    }

    @Test
    fun `isLoading is false after initialization`() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `streak count is loaded from preferences`() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()
        assertEquals(3, viewModel.uiState.value.streakCount)
    }
}
